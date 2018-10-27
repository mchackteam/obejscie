package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayClient;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C00PacketKeepAlive extends Packet
{
    public int keepAliveID;
    
    public C00PacketKeepAlive() {
    }
    
    public C00PacketKeepAlive(int keepAliveID) {
        this.keepAliveID = keepAliveID;
    }
    
    public static int getPacketID(int protocolVersion) {
        if (protocolVersion >= 107) {
            return 11;
        }
        return 0;
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayClient)handler).handle(this);
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
