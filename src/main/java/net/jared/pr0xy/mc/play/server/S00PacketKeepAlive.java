package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S00PacketKeepAlive extends Packet
{
    public int keepAliveID;
    
    public S00PacketKeepAlive() {
    }
    
    public S00PacketKeepAlive(int keepAliveID) {
        this.keepAliveID = keepAliveID;
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
    
    public static int getPacketID(int protocolVersion) {
        return (protocolVersion >= 107) ? 31 : 0;
    }
    
    @Override
    public void process(INetHandler handler) {
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        if (pis.handler.protocolVersion >= 47) {
            this.keepAliveID = pis.readVarInt();
        }
        else {
            this.keepAliveID = pis.readInt();
        }
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(getPacketID(pos.handler.protocolVersion));
        if (pos.handler.protocolVersion >= 47) {
            pos.writeVarInt(this.keepAliveID);
        }
        else {
            pos.writeInt(this.keepAliveID);
        }
    }
}
