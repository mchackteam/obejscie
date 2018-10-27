package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S2BPacketChangeStateGame extends Packet
{
    private int reason;
    private float value;
    
    public S2BPacketChangeStateGame(int reason, float value) {
        this.reason = reason;
        this.value = value;
    }
    
    @Override
    public void process(INetHandler p0) {
    }
    
    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.reason = inputStream.readByte();
        this.value = inputStream.readFloat();
    }
    
    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(43);
        outputStream.writeByte(this.reason);
        outputStream.writeFloat(this.value);
    }
    
    public int getReason() {
        return this.reason;
    }
    
    public float getValue() {
        return this.value;
    }
}
