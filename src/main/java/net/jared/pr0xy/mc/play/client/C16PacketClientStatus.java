package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C16PacketClientStatus extends Packet
{
    private int action;
    
    @Override
    public void process(INetHandler listener) {
//        ((NetHandlerPlayClient)listener).handle(this);
    }
    
    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.action = inputStream.readByte();
    }
    
    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(22);
        outputStream.writeByte(this.action);
    }
    
    public int getAction() {
        return this.action;
    }
}
