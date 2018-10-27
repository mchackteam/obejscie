package net.jared.pr0xy.mc.threads;

import java.io.EOFException;
import net.jared.pr0xy.mc.play.NetHandlerPlayClient;
import net.jared.pr0xy.mc.login.NetHandlerLoginServer;
import net.jared.pr0xy.mc.play.server.S40PacketDisconnect;
import net.jared.pr0xy.mc.play.server.S3FPacketCustomPayload;
import net.jared.pr0xy.mc.play.server.S38PacketPlayerListItem;
import net.jared.pr0xy.mc.play.server.S07PacketRespawn;
import net.jared.pr0xy.mc.play.server.S02PacketChat;
import net.jared.pr0xy.mc.play.server.S01PacketJoinGame;
import net.jared.pr0xy.mc.play.server.S00PacketKeepAlive;
import net.jared.pr0xy.mc.login.server.S03PacketEnableCompression;
import net.jared.pr0xy.mc.login.server.S02PacketLoginSuccess;
import net.jared.pr0xy.mc.login.server.S00PacketDisconnect;
import net.jared.pr0xy.mc.packets.Packet;
import java.util.zip.DataFormatException;
import java.io.ByteArrayInputStream;

import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.utils.Utils;
import java.io.IOException;
import java.util.zip.Inflater;

import net.jared.pr0xy.mc.server.Server;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.server.Player;

public class SInputStreamThread extends Thread
{
    private Player handler;
    public INetHandler netHandler;
    public Server currentSession;
    public int treshold;
    private PacketInputStream stream;
    private Inflater inflater;
    
    public SInputStreamThread(Player handler, Server currentSession) {
        super(handler.group, "SInputStreamThread");
        this.treshold = -1;
        this.inflater = new Inflater();
        this.handler = handler;
        this.currentSession = currentSession;
        this.start();
    }
    
    private PacketInputStream decompress() throws IOException, DataFormatException {
        if (this.treshold == -1) {
            this.stream.limit = -1;
            this.stream.limit = this.stream.readVarInt();
            if (this.stream.limit > 2097152) {
                throw new IOException("Size of " + this.stream.limit + " is larger than protocol maximum of 2097152");
            }
            return this.stream;
        }
        else {
            this.stream.limit = -1;
            int packetLength = this.stream.readVarInt();
            this.stream.limit = packetLength;
            int dataLen = this.stream.readVarInt();
            if (dataLen == 0) {
                return this.stream;
            }
            if (dataLen < this.treshold) {
                throw new IOException("Badly compressed packet - size of " + packetLength + " is below server threshold of " + this.treshold);
            }
            if (dataLen > 2097152) {
                throw new IOException("Badly compressed packet - size of " + packetLength + " is larger than protocol maximum of 2097152");
            }
            this.inflater.setInput(this.stream.readBytes(packetLength - Utils.getSizeVarInt(dataLen)));
            byte[] decompressed = new byte[dataLen];
            this.inflater.inflate(decompressed);
            this.inflater.reset();
            PacketInputStream pis = new PacketInputStream(new ByteArrayInputStream(decompressed));
            pis.handler = this.handler;
            pis.limit = dataLen;
            return pis;
        }
    }
    
    private Packet nextPacket() throws IOException, DataFormatException {
        PacketInputStream stream_ = this.decompress();
        int id = stream_.readVarInt();
        Packet packet = null;
        Label_0257: {
            switch (this.netHandler.getState()) {
                case 3: {
                    switch (id) {
                        case 0: {
                            packet = new S00PacketDisconnect();
                            break;
                        }
                        case 2: {
                            packet = new S02PacketLoginSuccess();
                            break;
                        }
                        case 3: {
                            packet = new S03PacketEnableCompression();
                            break;
                        }
                    }
                    break;
                }
                case 1: {
                    switch (id) {
                        case 0: {
                            packet = new S00PacketKeepAlive();
                            break Label_0257;
                        }
                        case 1: {
                            packet = new S01PacketJoinGame();
                            break Label_0257;
                        }
                        case 2: {
                            packet = new S02PacketChat();
                            break Label_0257;
                        }
                        case 7: {
                            packet = new S07PacketRespawn();
                            break Label_0257;
                        }
                        case 56: {
                            packet = new S38PacketPlayerListItem();
                            break Label_0257;
                        }
                        case 63: {
                            packet = new S3FPacketCustomPayload();
                            break Label_0257;
                        }
                        case 64: {
                            packet = new S40PacketDisconnect();
                            break Label_0257;
                        }
                    }
                    break;
                }
            }
        }
        if (packet != null) {
            packet.read(stream_);
        }
        else {
            byte[] data = stream_.readBytes(stream_.limit);
            if (/*bez tych id!=int crashuje na mc4u i przez to nie ma taba. Jak sie je usunie to tab jest ale crashuje */id != 42 && id != 59 && id != 60 && id != 61 && id != 62 && this.handler.currentSession == this.currentSession) {
                this.handler.packetSender.sendPacket(data.length + Utils.getSizeVarInt(id), id, data);
            }
        }
        return packet;
    }
    
    @Override
    public void run() {
        this.netHandler = new NetHandlerLoginServer(this.handler, this.currentSession);
        try {
            this.stream = new PacketInputStream(this.handler.currentSession.getInputStream());
            this.stream.handler = this.handler;
            while (true) {
                Packet p = this.nextPacket();
                if (p != null) {
                    p.process(this.netHandler);
                }
            }
        }
        catch (EOFException e3) {
            if (this.currentSession == this.handler.currentSession && this.handler.isLogged) {
                ((NetHandlerPlayClient)this.handler.packetReceiver.netHandler).emptyWorld();
            }
            this.currentSession.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            if (this.currentSession == this.handler.currentSession && this.handler.isLogged) {
                ((NetHandlerPlayClient)this.handler.packetReceiver.netHandler).emptyWorld();
            }
            this.currentSession.close();
        }
        catch (DataFormatException e2) {
            e2.printStackTrace();
            if (this.currentSession == this.handler.currentSession && this.handler.isLogged) {
                ((NetHandlerPlayClient)this.handler.packetReceiver.netHandler).emptyWorld();
            }
            this.currentSession.close();
        }
    }
}
