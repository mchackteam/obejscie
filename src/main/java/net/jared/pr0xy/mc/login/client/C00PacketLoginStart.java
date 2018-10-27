package net.jared.pr0xy.mc.login.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.login.NetHandlerLoginClient;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C00PacketLoginStart extends Packet
{
    public String name;
    
    public C00PacketLoginStart() {
    }
    
    public C00PacketLoginStart(String name) {
        this.name = name;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerLoginClient)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.name = pis.readString(16);
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(0);
        pos.writeString(this.name);
    }
}
