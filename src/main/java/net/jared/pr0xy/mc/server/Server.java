package net.jared.pr0xy.mc.server;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.jared.pr0xy.mc.play.client.C01PacketChatMessage;
import java.io.InputStream;
import net.jared.pr0xy.mc.threads.SOutputStreamThread;
import net.jared.pr0xy.mc.threads.SInputStreamThread;
import java.io.OutputStream;

public abstract class Server extends Thread
{
    public abstract boolean close();
    
    public abstract String getNickname();
    
    public abstract OutputStream getOutputStream();
    
    public abstract SInputStreamThread getPacketReceiver();
    
    public abstract SOutputStreamThread getPacketSender();
    
    public abstract int getPort();
    
    public abstract String getDefinedHostname();
    
    public abstract boolean isDisconnected();
    
    public abstract void setKeepSession();
    
    public abstract InputStream getInputStream();
    
    public void sendMessage(String msg) {
        this.getPacketSender().sendPacket(new C01PacketChatMessage(msg));
    }
    
    public boolean isPlaying() {
        return !this.isDisconnected() && this.getPacketSender() != null && this.getPacketReceiver() != null && this.getPacketReceiver().netHandler != null && this.getPacketReceiver().netHandler.getState() == 1;
    }
    
    public abstract void sendMsg(ComponentBuilder p0);
}
