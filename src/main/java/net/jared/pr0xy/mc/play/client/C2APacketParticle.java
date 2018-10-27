package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C2APacketParticle extends Packet
{
    private String particlename;
    private float x;
    private float y;
    private float z;
    private float offsetx;
    private float offsety;
    private float offsetz;
    private float particledata;
    private int number;
    
    public C2APacketParticle(String particlename, float x, float y, float z, float offsetx, float offsety, float offsetz, float particledata, int number) {
        this.particlename = particlename;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.offsetz = offsetz;
        this.particledata = particledata;
        this.number = number;
    }
    
    @Override
    public void process(INetHandler p0) {
    }
    
    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.particlename = inputStream.readString(16);
        this.x = inputStream.readFloat();
        this.y = inputStream.readFloat();
        this.z = inputStream.readFloat();
        this.offsetx = inputStream.readFloat();
        this.offsety = inputStream.readFloat();
        this.offsetz = inputStream.readFloat();
        this.particledata = inputStream.readFloat();
        this.number = inputStream.readInt();
    }
    
    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(42);
        outputStream.writeString(this.particlename);
        outputStream.writeFloat(this.x);
        outputStream.writeFloat(this.y);
        outputStream.writeFloat(this.z);
        outputStream.writeFloat(this.offsetx);
        outputStream.writeFloat(this.offsety);
        outputStream.writeFloat(this.offsetz);
        outputStream.writeFloat(this.particledata);
        outputStream.writeInt(this.number);
    }
    
    public String getParticlename() {
        return this.particlename;
    }
    
    public float getX() {
        return this.x;
    }
    
    public float getY() {
        return this.y;
    }
    
    public float getZ() {
        return this.z;
    }
    
    public float getOffsetx() {
        return this.offsetx;
    }
    
    public float getOffsety() {
        return this.offsety;
    }
    
    public float getOffsetz() {
        return this.offsetz;
    }
    
    public float getParticledata() {
        return this.particledata;
    }
    
    public int getNumber() {
        return this.number;
    }
}
