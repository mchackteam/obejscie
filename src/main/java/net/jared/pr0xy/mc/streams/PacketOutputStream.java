package net.jared.pr0xy.mc.streams;

import java.io.IOException;
import java.io.OutputStream;

import net.jared.pr0xy.mc.server.Player;
import pl.blazinghack.proxy.Proxy;

import java.io.DataOutputStream;

public class PacketOutputStream extends DataOutputStream
{
    public Player handler;
    @SuppressWarnings("unused")
	private byte[] writeBuffer;
    
    public PacketOutputStream(OutputStream outputStream) {
        super(outputStream);
        this.writeBuffer = new byte[8];
    }
    
    public void writeString(String n) throws IOException {
        byte[] bytes = n.getBytes(Proxy.UTF_8);
        if (bytes.length > 32767) {
            throw new RuntimeException("String too big (was " + bytes.length + " bytes encoded, max 32767)");
        }
        this.writeVarInt(bytes.length);
        this.write(bytes);
    }
    
    public void writeUnsignedShort(int v) throws IOException {
        this.write(v >>> 8 & 0xFF);
        this.write(v >>> 0 & 0xFF);
    }
    
    public void writeShort(short v) throws IOException {
        this.write(v >>> 8 & 0xFF);
        this.write(v >>> 0 & 0xFF);
    }
    
    public void writeVarInt(int n) throws IOException {
        while ((n & 0xFFFFFF80) != 0x0) {
            this.write((n & 0x7F) | 0x80);
            n >>>= 7;
        }
        this.write(n);
    }
}
