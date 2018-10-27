package net.jared.pr0xy.mc.threads;

import java.io.IOException;
import java.io.ByteArrayOutputStream;

import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.play.server.S00PacketKeepAlive;
import net.jared.pr0xy.mc.play.client.C00PacketKeepAlive;
import java.util.ArrayList;
import net.jared.pr0xy.mc.packets.CustomPacket;
import net.jared.pr0xy.mc.packets.Packet;
import net.jared.pr0xy.mc.streams.PacketOutputStream;

import java.util.List;
import java.util.logging.Logger;

public class COutputStreamThread extends Thread
{
    private static Logger L;
    private Player player;
    private PacketOutputStream stream;
    public List<Packet> packets;
    public List<Packet> priority;
    public List<CustomPacket> customs;
    private Object lock;
    private int lastPingID;
    private long ping;
    private long timeInMillisWhenSent;
    private long lastPingSent;
    public boolean isLogged;
    
    public COutputStreamThread(Player player) {
        super(player.group, "COutputStreamThread");
        this.packets = new ArrayList<Packet>();
        this.priority = new ArrayList<Packet>();
        this.customs = new ArrayList<CustomPacket>();
        this.lock = new Object();
        this.lastPingID = 0;
        this.ping = -1L;
        this.timeInMillisWhenSent = -1L;
        this.lastPingSent = 0L;
        this.player = player;
        this.start();
    }
    
    public void onReceivePing(C00PacketKeepAlive packet) {
        if (packet.keepAliveID == this.lastPingID) {
            this.ping = System.currentTimeMillis() - this.timeInMillisWhenSent;
            this.lastPingSent = System.currentTimeMillis();
            this.timeInMillisWhenSent = -1L;
        }
    }
    
    public long getCurrentPing() {
        if (this.timeInMillisWhenSent != -1L) {
            return Math.max(this.ping, System.currentTimeMillis() - this.timeInMillisWhenSent);
        }
        return this.ping;
    }
    
    private void sendPingIfNeeded() {
        if (!this.isLogged) {
            return;
        }
        if (this.timeInMillisWhenSent != -1L) {
            return;
        }
        if (System.currentTimeMillis() - this.lastPingSent < 1000L) {
            return;
        }
        ++this.lastPingID;
        this.timeInMillisWhenSent = System.currentTimeMillis();
        this.priority.add(new S00PacketKeepAlive(this.lastPingID));
    }
    
    @Override
    public void run() {
        try {
            this.stream = new PacketOutputStream(this.player.socket.getOutputStream());
            this.stream.handler = this.player;
            while (!this.player.socket.isClosed()) {
                Packet toSend = null;
                CustomPacket custom = null;
                synchronized (this.lock) {
                    if (this.priority.size() == 0 && this.packets.size() == 0 && this.customs.size() == 0) {
                        try {
                            this.lock.wait(1000L);
                        }
                        catch (InterruptedException ex) {}
                    }
                    if (this.getCurrentPing() > 20000L) {
                        COutputStreamThread.L.warning("Timed out!");
                        this.player.close();
                    }
                    else if (this.priority.size() == 0 && this.packets.size() == 0 && this.customs.size() == 0) {
                        this.sendPingIfNeeded();
                        continue;
                    }
                    if (this.priority.size() > 0) {
                        toSend = this.priority.remove(0);
                    }
                    else if (this.packets.size() > 0) {
                        toSend = this.packets.remove(0);
                    }
                    else {
                        custom = this.customs.remove(0);
                    }
                }
                if (toSend != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    PacketOutputStream pos = new PacketOutputStream(baos);
                    pos.handler = this.player;
                    toSend.write(pos);
                    this.stream.writeVarInt(baos.size());
                    baos.writeTo(this.stream);
                }
                else {
                    this.stream.writeVarInt(custom.len);
                    this.stream.writeVarInt(custom.id);
                    this.stream.write(custom.data);
                }
                this.stream.flush();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            this.player.close();
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
    
    static {
        L = Logger.getLogger("COutputStreamThread");
    }
}
