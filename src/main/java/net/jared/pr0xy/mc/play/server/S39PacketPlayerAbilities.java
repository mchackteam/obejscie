package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S39PacketPlayerAbilities extends Packet
{
    public boolean disableDamage;
    public boolean isFlying;
    public boolean allowFlying;
    public boolean isCreativeMode;
    public float flySpeed;
    public float walkSpeed;
    
    @Override
    public void process(INetHandler handler) {
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        byte flags = pis.readByte();
        this.disableDamage = ((flags & 0x1) > 0);
        this.isFlying = ((flags & 0x2) > 0);
        this.allowFlying = ((flags & 0x4) > 0);
        this.isCreativeMode = ((flags & 0x8) > 0);
        this.flySpeed = pis.readFloat();
        this.walkSpeed = pis.readFloat();
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt((pos.handler.protocolVersion >= 107) ? 43 : 57);
        byte flags = 0;
        if (this.disableDamage) {
            flags |= 0x1;
        }
        if (this.isFlying) {
            flags |= 0x2;
        }
        if (this.allowFlying) {
            flags |= 0x4;
        }
        if (this.isCreativeMode) {
            flags |= 0x8;
        }
        pos.write(flags);
        pos.writeFloat(this.flySpeed);
        if (pos.handler.protocolVersion >= 107) {
            pos.writeFloat(1.0f);
        }
        else {
            pos.writeFloat(this.walkSpeed);
        }
    }
}
