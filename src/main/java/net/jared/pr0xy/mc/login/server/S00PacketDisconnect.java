package net.jared.pr0xy.mc.login.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.md_5.bungee.chat.ComponentSerializer;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.login.NetHandlerLoginServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.jared.pr0xy.mc.packets.Packet;

public class S00PacketDisconnect extends Packet
{
    public BaseComponent[] reason;
    
    public S00PacketDisconnect() {
    }
    
    public S00PacketDisconnect(BaseComponent[] reason) {
        this.reason = reason;
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerLoginServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.reason = ComponentSerializer.parse(pis.readString(32767));
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(0);
        pos.writeString(ComponentSerializer.toString(this.reason));
    }
}
