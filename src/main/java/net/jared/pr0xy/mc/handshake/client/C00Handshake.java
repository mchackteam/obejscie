package net.jared.pr0xy.mc.handshake.client;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.handshake.NetHandlerHandshakeClient;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class C00Handshake extends Packet
{
    public int protocolVersion;
    public String serverAddress;
    public int serverPort;
    public int nextState;
    
    public C00Handshake() {
    }
    
    public C00Handshake(int protocolVersion, String serverAddress, int serverPort, int nextState) {
        this.protocolVersion = protocolVersion;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.nextState = nextState;
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerHandshakeClient)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.protocolVersion = pis.readVarInt();
        this.serverAddress = pis.readString(255);
        this.serverPort = pis.readUnsignedShort();
        this.nextState = pis.readVarInt();
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(0);
        pos.writeVarInt(this.protocolVersion);
        pos.writeString(this.serverAddress);
        pos.writeUnsignedShort(this.serverPort);
        pos.writeVarInt(this.nextState);
    }
}
