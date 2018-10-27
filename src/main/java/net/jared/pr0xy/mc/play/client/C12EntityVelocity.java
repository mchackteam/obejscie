package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.streams.PacketOutputStream;

import java.io.IOException;

/**
 * Created by jaredmeister on 2016-09-20.
 */
public class C12EntityVelocity extends Packet{
    private int entityid;
    private short x;
    private short y;
    private short z;

    public C12EntityVelocity(int entityid, short x, short y, short z){
        this.entityid = entityid;
        this.x = x;
        this.y = y;
        this.z = z;

    }

    @Override
    public void process(INetHandler p0) {

    }

    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.entityid = inputStream.readInt();
        this.x = inputStream.readShort();
        this.y = inputStream.readShort();
        this.z = inputStream.readShort();

    }

    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(0x12);
        outputStream.writeInt(this.entityid);
        outputStream.writeShort(this.x);
        outputStream.writeShort(this.y);
        outputStream.writeShort(this.z);

    }
}