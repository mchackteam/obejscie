package net.jared.pr0xy.mc.packets;

public class CustomPacket
{
    public int len;
    public int id;
    public byte[] data;
    
    public CustomPacket(int len, int id, byte[] data) {
        this.len = len;
        this.id = id;
        this.data = data;
    }
}
