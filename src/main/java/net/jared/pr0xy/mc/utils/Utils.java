package net.jared.pr0xy.mc.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.play.server.S02PacketChat;
import net.jared.pr0xy.mc.threads.COutputStreamThread;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Utils
{
    public static ComponentBuilder c() {
        return new ComponentBuilder("§4(§c§lBlazingProxy§4) §f§l");
    }

    public static ComponentBuilder irc() {
        return new ComponentBuilder("§6(§e§lIRC§6) §e§l").color(ChatColor.BOLD);
    }

    public static ComponentBuilder clearchat() {
        return new ComponentBuilder("");
    }
    
    public static int getSizeVarInt(int n) {
        int bytes = 1;
        while ((n & 0xFFFFFF80) != 0x0) {
            ++bytes;
            n >>>= 7;
        }
        return bytes;
    }
    
    public static void sendMsg(COutputStreamThread stream, ComponentBuilder component) {
        stream.sendPacket(new S02PacketChat(component.create()));
    }
    
    public static void broadcast(ComponentBuilder cb) {
        BaseComponent[] b = cb.create();
        for (Player sh : Player.players) {
            try {
                sh.packetSender.sendPacket(new S02PacketChat(b));
            }
            catch (Throwable t) {}
        }
    }
}
