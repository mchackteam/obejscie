package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S38PacketPlayerListItem extends Packet
{
    public String playerName;
    public boolean online;
    public int ping;
    
    public S38PacketPlayerListItem() {
    }
    
    public S38PacketPlayerListItem(String playerName, boolean online, int ping) {
        this.playerName = playerName;
        this.online = online;
        this.ping = ping;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.playerName = pis.readString(16);
        this.online = (pis.read() == 1);
        this.ping = pis.readShort();
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(56);
        pos.writeString(this.playerName);
        pos.write(this.online ? 1 : 0);
        pos.writeShort((short)this.ping);
    }
}
