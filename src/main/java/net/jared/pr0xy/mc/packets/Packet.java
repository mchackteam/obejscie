package net.jared.pr0xy.mc.packets;

import java.io.IOException;

import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.streams.PacketOutputStream;

public abstract class Packet
{
    public boolean hasPriority() {
        return false;
    }
    
    public abstract void process(INetHandler p0);
    
    public abstract void read(PacketInputStream inputStream) throws IOException;
    
    public abstract void write(PacketOutputStream outputStream) throws IOException;
}
