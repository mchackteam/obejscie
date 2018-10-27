package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S05PacketSpawnPosition extends Packet
{
    public int x;
    public int y;
    public int z;
    
    public S05PacketSpawnPosition() {
    }
    
    public S05PacketSpawnPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public void process(INetHandler handler) {
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        if (pis.handler.protocolVersion >= 47) {
            long val = pis.readLong();
            this.x = (int)(val >> 38);
            this.y = (int)(val >> 26 & 0xFFFL);
            this.z = (int)(val << 38 >> 38);
        }
        else {
            this.x = pis.readInt();
            this.y = pis.readInt();
            this.z = pis.readInt();
        }
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt((pos.handler.protocolVersion >= 107) ? 67 : 5);
        if (pos.handler.protocolVersion >= 47) {
            pos.writeLong((this.x & 0x3FFFFFF) << 38 | (this.y & 0xFFF) << 26 | (this.z & 0x3FFFFFF));
        }
        else {
            pos.writeInt(this.x);
            pos.writeInt(this.y);
            pos.writeInt(this.z);
        }
    }
}
