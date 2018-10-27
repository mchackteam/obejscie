package net.jared.pr0xy.mc.play.client;

import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.streams.PacketOutputStream;

import java.io.IOException;

/**
 * Created by jaredmeister on 2016-09-20.
 */
public class C1DEntityEffect extends Packet{
    private int entityid;
    private byte effectid;
    private byte aplifier;
    private short duration;

    public C1DEntityEffect(int entityid, byte effectid, byte aplifier, short duration){
        this.entityid = entityid;
        this.effectid = effectid;
        this.aplifier = aplifier;
        this.duration = duration;
    }


    @Override
    public void process(INetHandler p0) {

    }

    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.entityid = inputStream.readInt();
        this.effectid = inputStream.readByte();
        this.aplifier = inputStream.readByte();
        this.duration = inputStream.readShort();
    }

    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(0x1D);
        outputStream.writeInt(this.entityid);
        outputStream.writeByte(this.effectid);
        outputStream.writeByte(this.aplifier);
        outputStream.writeShort(this.duration);
    }
}
