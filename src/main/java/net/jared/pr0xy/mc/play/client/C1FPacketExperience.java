package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C1FPacketExperience extends Packet
{
    private float bar;
    private short level;
    private short totexperience;
    
    public C1FPacketExperience(float bar, short level, short totexperience) {
        this.bar = bar;
        this.level = level;
        this.totexperience = totexperience;
    }
    
    @Override
    public void process(INetHandler p0) {
    }
    
    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.bar = inputStream.readFloat();
        this.level = inputStream.readShort();
        this.totexperience = inputStream.readShort();
    }
    
    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(31);
        outputStream.writeFloat(this.bar);
        outputStream.writeShort(this.level);
        outputStream.writeShort(this.totexperience);
    }
}
