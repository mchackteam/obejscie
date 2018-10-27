package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayClient;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C01PacketChatMessage extends Packet
{
    public String message;
    
    public C01PacketChatMessage() {
    }
    
    public C01PacketChatMessage(String message) {
        this.message = message;
    }
    
    public static int getPacketID(int protocolVersion) {
        if (protocolVersion >= 107) {
            return 2;
        }
        return 1;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayClient)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.message = pis.readString(100);
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(getPacketID(pos.handler.protocolVersion));
        pos.writeString(this.message);
    }
}
