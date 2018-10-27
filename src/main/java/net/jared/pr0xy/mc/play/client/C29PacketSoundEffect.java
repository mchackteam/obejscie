package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C29PacketSoundEffect extends Packet
{
    private String soundname;
    private int x;
    private int y;
    private int z;
    private float volume;
    private int pitch;
    
    public C29PacketSoundEffect(String soundname, int x, int y, int z, float volume, byte pitch) {
        this.soundname = soundname;
        this.x = x;
        this.y = y;
        this.z = z;
        this.volume = volume;
        this.pitch = pitch;
    }
    
    @Override
    public void process(INetHandler p0) {
    }
    
    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.soundname = inputStream.readString(16);
        this.x = inputStream.read();
        this.y = inputStream.read();
        this.z = inputStream.read();
        this.volume = inputStream.readFloat();
        this.pitch = inputStream.read();
    }
    
    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(41);
        outputStream.writeString(this.soundname);
        outputStream.writeInt(this.x);
        outputStream.writeInt(this.y);
        outputStream.writeInt(this.z);
        outputStream.writeFloat(this.volume);
        outputStream.writeByte(this.pitch);
    }
    
    public String getSoundname() {
        return this.soundname;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public float getVolume() {
        return this.volume;
    }
    
    public int getPitch() {
        return this.pitch;
    }
}
