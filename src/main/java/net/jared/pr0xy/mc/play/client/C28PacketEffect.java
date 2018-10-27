package net.jared.pr0xy.mc.play.client;

import java.io.DataInput;
import java.io.IOException;

import net.jared.pr0xy.mc.packets.Packet;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.streams.PacketOutputStream;

public class C28PacketEffect extends Packet
{
    private int effectid;
    private int x;
    private byte y;
    private int z;
    private int data;
    private boolean disablevol;

    public C28PacketEffect(int effectid, int x, byte y, int z, int data, boolean disablevol) {
        this.effectid = effectid;
        this.x = x;
        this.y = y;
        this.z = z;
        this.data = data;
        this.disablevol = disablevol;
    }

    @Override
    public void process(INetHandler p0) {
    }

    @Override
    public void read(PacketInputStream inputStream) throws IOException {
        this.effectid = inputStream.readInt();
        this.x = inputStream.readInt();
        this.y = inputStream.readByte();
        this.z = inputStream.readInt();
        this.data = inputStream.readInt();
        this.disablevol = ((DataInput) inputStream).readBoolean();
    }

    @Override
    public void write(PacketOutputStream outputStream) throws IOException {
        outputStream.writeVarInt(40);
        outputStream.writeInt(this.effectid);
        outputStream.writeInt(this.x);
        outputStream.writeByte(this.y);
        outputStream.writeInt(this.z);
        outputStream.writeInt(this.data);
        outputStream.writeBoolean(this.disablevol);
    }

    public int getEffectid() {
        return this.effectid;
    }

    public int getX() {
        return this.x;
    }

    public byte getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public int getData() {
        return this.data;
    }

    public boolean isDisablevol() {
        return this.disablevol;
    }
}
