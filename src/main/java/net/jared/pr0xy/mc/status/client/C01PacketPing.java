package net.jared.pr0xy.mc.status.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.status.NetHandlerStatusClient;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C01PacketPing extends Packet
{
    public long time;
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerStatusClient)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.time = pis.readLong();
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(1);
        pos.writeLong(this.time);
    }
}
