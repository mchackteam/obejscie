package net.jared.pr0xy.mc.play.server;

import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.NetHandlerPlayServer;
import net.jared.pr0xy.mc.play.INetHandler;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.jared.pr0xy.mc.packets.Packet;

public class S02PacketChat extends Packet
{
    public String text;
    public byte position;
    
    public S02PacketChat(String text) {
        this.text = text;
    }
    
    public S02PacketChat() {
    }
    
    public S02PacketChat(BaseComponent... text) {
        this.text = ComponentSerializer.toString(text);
    }
    
    public S02PacketChat(byte position, BaseComponent... text) {
        this.text = ComponentSerializer.toString(text);
        this.position = position;
    }
    
    @Override
    public void process(INetHandler handler) {
        ((NetHandlerPlayServer)handler).handle(this);
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.text = pis.readString(32767);
        if (pis.handler.protocolVersion >= 47) {
            this.position = pis.readByte();
        }
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt((pos.handler.protocolVersion >= 107) ? 15 : 2);
        pos.writeString(this.text.replace("\"extra\":[]", ""));
        if (pos.handler.protocolVersion >= 47) {
            pos.write(this.position);
        }
    }
}
