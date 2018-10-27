package net.jared.pr0xy.mc.threads;

import java.io.IOException;
import net.jared.pr0xy.mc.play.NetHandlerPlayClient;
import net.jared.pr0xy.mc.streams.PacketOutputStream;
import net.jared.pr0xy.mc.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import net.jared.pr0xy.mc.packets.CustomPacket;
import net.jared.pr0xy.mc.server.Server;
import net.jared.pr0xy.mc.packets.Packet;
import java.util.List;
import java.util.zip.Deflater;
import net.jared.pr0xy.mc.server.Player;

public class SOutputStreamThread extends Thread
{
    private Player handler;
    private Deflater deflater;
    private PacketOutputStream stream;
    private List<Packet> packets;
    private List<Packet> priority;
    private Server remoteServer;
    private List<CustomPacket> customs;
    private Object lock;
    public int putNBT;
    
    public SOutputStreamThread(Player handler, Server remoteServer) {
        super(handler.group, "SOutputStreamThread");
        this.deflater = new Deflater();
        this.packets = new ArrayList<Packet>();
        this.priority = new ArrayList<Packet>();
        this.customs = new ArrayList<CustomPacket>();
        this.lock = new Object();
        this.putNBT = 0;
        this.handler = handler;
        this.remoteServer = remoteServer;
        this.deflater.setLevel(9);
        this.start();
    }
    
    @SuppressWarnings("resource")
	@Override
    public void run() {
        try {
            this.stream = new PacketOutputStream(this.remoteServer.getOutputStream());
            this.stream.handler = this.handler;
            while (true) {
                Packet toSend = null;
                CustomPacket custom = null;
                synchronized (this.lock) {
                    if (this.priority.size() == 0 && this.packets.size() == 0 && this.customs.size() == 0 && this.putNBT == 0) {
                        try {
                            this.lock.wait(1000L);
                        }
                        catch (InterruptedException ex) {}
                    }
                    if (this.priority.size() > 0) {
                        toSend = this.priority.remove(0);
                    }
                    else if (this.packets.size() > 0) {
                        toSend = this.packets.remove(0);
                    }
                    else if (this.customs.size() > 0) {
                        custom = this.customs.remove(0);
                    }
                    else if (this.putNBT == 0) {
                        continue;
                    }
                }
                int threshold = this.remoteServer.getPacketReceiver().treshold;
                if (threshold == -1) {
                    if (toSend != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PacketOutputStream pos = new PacketOutputStream(baos);
                        pos.handler = this.handler;
                        toSend.write(pos);
                        this.stream.writeVarInt(baos.size());
                        baos.writeTo(this.stream);
                    }
                    else if (custom != null) {
                        this.stream.writeVarInt(custom.len);
                        this.stream.writeVarInt(custom.id);
                        this.stream.write(custom.data);
                    }
                }
                else if (toSend != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PacketOutputStream pos = new PacketOutputStream(baos);
                    pos.handler = this.handler;
                    toSend.write(pos);
                    if (baos.size() < threshold) {
                        this.stream.writeVarInt(Utils.getSizeVarInt(0) + baos.size());
                        this.stream.writeVarInt(0);
                        baos.writeTo(this.stream);
                    }
                    else {
                        byte[] b = baos.toByteArray();
                        this.deflater.setInput(b, 0, b.length);
                        this.deflater.finish();
                        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
                        PacketOutputStream pos_compressed = new PacketOutputStream(compressed);
                        pos_compressed.writeVarInt(b.length);
                        byte[] buffer = new byte[8192];
                        while (!this.deflater.finished()) {
                            int n = this.deflater.deflate(buffer);
                            pos_compressed.write(buffer, 0, n);
                        }
                        this.deflater.reset();
                        pos_compressed.flush();
                        this.stream.writeVarInt(compressed.size());
                        compressed.writeTo(this.stream);
                    }
                }
                else if (custom != null) {
                    if (custom.len < threshold) {
                        this.stream.writeVarInt(Utils.getSizeVarInt(0) + custom.len);
                        this.stream.writeVarInt(0);
                        this.stream.writeVarInt(custom.id);
                        this.stream.write(custom.data);
                    }
                    else {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream(custom.len);
                        PacketOutputStream pos = new PacketOutputStream(baos);
                        pos.writeVarInt(custom.id);
                        pos.write(custom.data);
                        byte[] b = baos.toByteArray();
                        this.deflater.setInput(b, 0, b.length);
                        this.deflater.finish();
                        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
                        PacketOutputStream pos_compressed = new PacketOutputStream(compressed);
                        pos_compressed.writeVarInt(b.length);
                        byte[] buffer = new byte[8192];
                        while (!this.deflater.finished()) {
                            int n = this.deflater.deflate(buffer);
                            pos_compressed.write(buffer, 0, n);
                        }
                        this.deflater.reset();
                        pos_compressed.flush();
                        this.stream.writeVarInt(compressed.size());
                        compressed.writeTo(this.stream);
                    }
                }
                this.stream.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            if (this.remoteServer == this.handler.currentSession) {
                ((NetHandlerPlayClient)this.handler.packetReceiver.netHandler).emptyWorld();
            }
            this.remoteServer.close();
        }
    }
    
    public void sendPacket(Packet packet) {
        synchronized (this.lock) {
            if (packet.hasPriority()) {
                this.priority.add(packet);
            }
            else {
                this.packets.add(packet);
            }
            this.lock.notify();
        }
    }
    
    public void sendPacket(int len, int id, byte[] data) {
        synchronized (this.lock) {
            this.customs.add(new CustomPacket(len, id, data));
            this.lock.notify();
        }
    }
}
