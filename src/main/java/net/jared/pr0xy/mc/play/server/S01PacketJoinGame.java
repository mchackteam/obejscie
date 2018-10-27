package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.packets.Packet;

public class S01PacketJoinGame extends Packet
{
    public int entityID;
    public int gamemode;
    public boolean hardcoreFlag;
    public int dimension;
    public int difficulty;
    public int maxPlayers;
    public String levelType;
    public boolean reducedDebugInfo;
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.entityID = pis.readInt();
        int var = pis.read();
        this.hardcoreFlag = ((var & 0x8) == 0x8);
        this.gamemode = (var & 0xFFFFFFF7);
        this.dimension = pis.readByte();
        this.difficulty = pis.read();
        this.maxPlayers = pis.read();
        this.levelType = pis.readString(16);
        if (pis.handler.protocolVersion >= 47) {
            this.reducedDebugInfo = (pis.read() == 1);
        }
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt((pos.handler.protocolVersion >= 107) ? 35 : 1);
        pos.writeInt(this.entityID);
        int var = this.gamemode;
        if (this.hardcoreFlag) {
            var |= 0x8;
        }
        pos.write(var);
        pos.write(this.dimension);
        pos.write(this.difficulty);
        pos.write(this.maxPlayers);
        pos.writeString(this.levelType);
        if (pos.handler.protocolVersion >= 47) {
            pos.write(this.reducedDebugInfo ? 1 : 0);
        }
    }
}
