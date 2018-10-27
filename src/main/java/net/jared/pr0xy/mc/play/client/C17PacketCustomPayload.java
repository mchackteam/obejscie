package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C17PacketCustomPayload extends Packet
{
    public String channel;
    public byte[] data;
    
    public C17PacketCustomPayload() {
    }
    
    public C17PacketCustomPayload(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
        if (data.length >= 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }
    
    @Override
    public void process(INetHandler handler) {
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.channel = pis.readString(20);
        this.data = pis.readBytes(pis.limit);
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(23);
        pos.writeString(this.channel);
        if (pos.handler.protocolVersion < 47) {
            pos.writeUnsignedShort(this.data.length);
        }
        pos.write(this.data);
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
}
