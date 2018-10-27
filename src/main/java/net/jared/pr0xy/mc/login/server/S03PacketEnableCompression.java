package net.jared.pr0xy.mc.login.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.login.NetHandlerLoginServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S03PacketEnableCompression extends Packet
{
    public int threshold;
    
    public S03PacketEnableCompression() {
    }
    
    public S03PacketEnableCompression(int threshold) {
        this.threshold = threshold;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerLoginServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.threshold = pis.readVarInt();
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(3);
        pos.writeVarInt(this.threshold);
    }
}
