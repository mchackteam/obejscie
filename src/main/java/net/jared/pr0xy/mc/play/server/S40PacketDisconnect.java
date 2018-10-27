package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.jared.pr0xy.mc.packets.Packet;

public class S40PacketDisconnect extends Packet
{
    public String reason;
    
    public S40PacketDisconnect(String text) {
        this.reason = text;
    }
    
    public S40PacketDisconnect() {
    }
    
    public S40PacketDisconnect(BaseComponent... text) {
        this.reason = ComponentSerializer.toString(text);
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.reason = pis.readString(32767);
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(64);
        pos.writeString(this.reason);
    }
}
