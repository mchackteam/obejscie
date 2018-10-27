package net.jared.pr0xy.mc.threads;

import java.io.EOFException;

import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.handshake.NetHandlerHandshakeClient;
import net.jared.pr0xy.mc.login.client.C00PacketLoginStart;
import net.jared.pr0xy.mc.status.client.C01PacketPing;
import net.jared.pr0xy.mc.status.client.C00PacketServerQuery;
import net.jared.pr0xy.mc.play.client.C16PacketClientStatus;
import net.jared.pr0xy.mc.play.client.C01PacketChatMessage;
import net.jared.pr0xy.mc.play.client.C00PacketKeepAlive;
import net.jared.pr0xy.mc.handshake.client.C00Handshake;
import net.jared.pr0xy.mc.packets.Packet;
import net.jared.pr0xy.mc.streams.PacketInputStream;

import java.io.IOException;

public class CInputStreamThread extends Thread
{
    private Player player;
    public INetHandler netHandler;
    private PacketInputStream stream;
    
    private static int getSizeVarInt(int n) throws IOException {
        int bytes = 1;
        while ((n & 0xFFFFFF80) != 0x0) {
            ++bytes;
            n >>>= 7;
        }
        return bytes;
    }
    
    public CInputStreamThread(Player player) {
        super(player.group, "CInputStreamThread");
        this.player = player;
        this.start();
    }
    
    private Packet nextPacket() throws IOException {
        this.stream.setLimit(-1);
        int len = this.stream.readVarInt();
        if (len > 2097152) {
            this.player.close();
            return null;
        }
        this.stream.setLimit(len);
        int id = this.stream.readVarInt();
        Packet packet = null;
        Label_0267: {
            switch (this.netHandler.getState()) {
                case 0: {
                    switch (id) {
                        case 0: {
                            packet = new C00Handshake();
                            break;
                        }
                    }
                    break;
                }
                case 1: {
                    if (C00PacketKeepAlive.getPacketID(this.player.protocolVersion) == id) {
                        packet = new C00PacketKeepAlive();
                    }
                    switch (id) {
                        case 1: {
                            packet = new C01PacketChatMessage();
                            break;
                        }
                        case 22: {
                            packet = new C16PacketClientStatus();
                            break;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (id) {
                        case 0: {
                            packet = new C00PacketServerQuery();
                            break;
                        }
                        case 1: {
                            packet = new C01PacketPing();
                            break;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (id) {
                        case 0: {
                            packet = new C00PacketLoginStart();
                            break Label_0267;
                        }
                    }
                    break;
                }
            }
        }
        if (packet != null) {
            packet.read(this.stream);
        }
        else {
            byte[] data = this.stream.readBytes(len - getSizeVarInt(id));
            try {
                if (this.player.currentSession != null && this.player.currentSession.isPlaying()) {
                    this.player.currentSession.getPacketSender().sendPacket(len, id, data);
                }
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return packet;
    }
    
    @Override
    public void run() {
        this.netHandler = new NetHandlerHandshakeClient(this.player);
        try {
            this.stream = new PacketInputStream(this.player.socket.getInputStream());
            this.stream.handler = this.player;
            while (true) {
                Packet p = this.nextPacket();
                if (p != null) {
                    p.process(this.netHandler);
                }
            }
        }
        catch (EOFException e2) {
            this.player.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
