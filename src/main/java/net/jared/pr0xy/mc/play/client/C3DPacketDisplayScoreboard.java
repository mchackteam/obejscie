package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C3DPacketDisplayScoreboard extends Packet
{
    private Byte position;
    private String scorename;
    
    public C3DPacketDisplayScoreboard(Byte position, String scorename) {
        this.position = position;
        this.scorename = scorename;
    }
    
    @Override
    public void process(INetHandler p0) {
    }
    
    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.position = inputStream.readByte();
        this.scorename = inputStream.readString(16);
    }
    
    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(61);
        outputStream.writeByte(this.position);
        outputStream.writeString(this.scorename);
    }
    
    public Byte getPosition() {
        return this.position;
    }
    
    public String getScorename() {
        return this.scorename;
    }
}
