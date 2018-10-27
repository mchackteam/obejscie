package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S3FPacketCustomPayload extends Packet
{
    public String channel;
    public byte[] data;
    private short length;
    
    public S3FPacketCustomPayload() {
    }
    
    public S3FPacketCustomPayload(String channel, byte[] data) {
        if (data.length >= 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.channel = pis.readString(20);
        this.length = pis.readShort();
        pis.read(this.data = new byte[this.length], 0, this.length);
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(63);
        pos.writeString(this.channel);
        pos.writeShort(this.length);
        pos.write(this.data);
    }
}
