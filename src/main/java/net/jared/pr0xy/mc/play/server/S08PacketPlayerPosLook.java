package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S08PacketPlayerPosLook extends Packet
{
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    
    public S08PacketPlayerPosLook() {
    }
    
    public S08PacketPlayerPosLook(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }
    
    @Override
    public void process(INetHandler handler) {
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.x = pis.readDouble();
        this.y = pis.readDouble();
        this.z = pis.readDouble();
        this.yaw = pis.readFloat();
        this.pitch = pis.readFloat();
        if (pis.handler.protocolVersion >= 47) {
            pis.read();
        }
        else {
            this.onGround = (pis.read() == 1);
        }
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt((pos.handler.protocolVersion >= 107) ? 46 : 8);
        pos.writeDouble(this.x);
        pos.writeDouble(this.y);
        pos.writeDouble(this.z);
        pos.writeFloat(this.yaw);
        pos.writeFloat(this.pitch);
        if (pos.handler.protocolVersion >= 47) {
            pos.write(0);
        }
        else {
            pos.write(this.onGround ? 1 : 0);
        }
        if (pos.handler.protocolVersion >= 107) {
            pos.writeVarInt(0);
        }
    }
}
