package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S07PacketRespawn extends Packet
{
    public int dimension;
    public int difficulty;
    public int gamemode;
    public String levelType;
    
    public S07PacketRespawn() {
    }
    
    public S07PacketRespawn(int dimension, int difficulty, int gamemode, String levelType) {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.gamemode = gamemode;
        this.levelType = levelType;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.dimension = pis.readInt();
        this.difficulty = pis.read();
        this.gamemode = pis.read();
        this.levelType = pis.readString(16);
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(7);
        pos.writeInt(this.dimension);
        pos.write(this.difficulty);
        pos.write(this.gamemode);
        pos.writeString(this.levelType);
    }
}
