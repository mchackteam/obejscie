package net.jared.pr0xy.mc.status.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S01PacketPong extends Packet
{
    public long time;
    
    public S01PacketPong() {
    }
    
    public S01PacketPong(long time) {
        this.time = time;
    }
    
    @Override
    public void process(INetHandler handler) {
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
