package net.jared.pr0xy.mc.login.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.login.NetHandlerLoginServer;
import net.jared.pr0xy.mc.play.INetHandler;
import java.util.UUID;
import net.jared.pr0xy.mc.packets.Packet;

public class S02PacketLoginSuccess extends Packet
{
    public UUID uuid;
    public String username;
    
    public S02PacketLoginSuccess() {
    }
    
    public S02PacketLoginSuccess(String username) {
        this.username = username;
    }
    
    public S02PacketLoginSuccess(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }
    
    @Override
    public boolean hasPriority() {
        return true;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerLoginServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        String uuid_ = pis.readString(36);
        if (!uuid_.isEmpty()) {
            if (uuid_.length() == 32) {
                uuid_ = String.valueOf(uuid_.substring(0, 8)) + "-" + uuid_.substring(8, 12) + "-" + uuid_.substring(12, 16) + "-" + uuid_.substring(16, 20) + "-" + uuid_.substring(20);
            }
            this.uuid = UUID.fromString(uuid_);
        }
        this.username = pis.readString(16);
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(2);
        pos.writeString((this.uuid == null) ? "" : this.uuid.toString());
        pos.writeString(this.username);
    }
}
