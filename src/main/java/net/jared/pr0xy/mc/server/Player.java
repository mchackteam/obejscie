package net.jared.pr0xy.mc.server;

import java.net.SocketException;

import net.jared.pr0xy.mc.play.client.C1DEntityEffect;
import net.jared.pr0xy.mc.threads.CInputStreamThread;
import net.jared.pr0xy.mc.threads.COutputStreamThread;
import net.md_5.bungee.api.ChatColor;
import net.jared.pr0xy.mc.utils.Utils;
import net.jared.pr0xy.mc.play.server.S38PacketPlayerListItem;
import java.util.Collections;
import java.util.ArrayList;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.List;

public class Player
{
    public List<String> tab;
    public List<String> effects;
    public static List<Player> players;
    private static Logger L;
    public Socket socket;
    public int currentDimension;
    public COutputStreamThread packetSender;
    public CInputStreamThread packetReceiver;
    public ThreadGroup group;
    public boolean isLogged;
    public int protocolVersion;
    public boolean isolatedChat;
    public String name;
    public Server currentSession;
    public int gamemode;
    public boolean admin;
    
    Player(ThreadGroup group, Socket socket) {
        this.tab = Collections.synchronizedList(new ArrayList<String>());
        this.effects = Collections.synchronizedList(new ArrayList<String>());
        this.currentDimension = 1;
        this.isolatedChat = false;
        this.socket = socket;
        this.group = group;
        this.admin = false;
        if (socket != null) {
            this.run();
        }
    }
    
    public void clearTab() {
        for (String s : this.tab) {
            this.packetSender.sendPacket(new S38PacketPlayerListItem(s, false, 0));
        }
        this.tab.clear();
    }

    public void clearEffects() {
//        for (String s : this.effects) {
        if (this.currentSession !=null){
            this.packetSender.sendPacket(new C1DEntityEffect(1, (byte) 0, (byte) 0, (short) 0));
        }
        this.effects.clear();
    }

    //this.player.packetSender.sendPacket(new C1DEntityEffect(1, (byte) 5, (byte) 3, (short) 3000));
    
    public void close() {
        try {
            this.socket.close();
        }
        catch (Throwable t) {}
        Player.players.remove(this);
        Player.L.info(String.format("Rozlaczono: %s -> %s", this.socket.getRemoteSocketAddress(), this.socket.getLocalSocketAddress()));
        if (this.currentSession != null) {
            this.currentSession.close();
        }
        if (this.name != null) {
            Utils.broadcast(Utils.c().append(String.format(this.name, new Object[0])).color(ChatColor.RED).append(" opuscil ").color(ChatColor.GRAY).append("Proxy").color(ChatColor.RED).append("!").color(ChatColor.GRAY));
        }
    }
    
    public void run() {
        try {
            this.socket.setSoTimeout(20000);
        }
        catch (SocketException ex) {}
        Player.L.info(String.format("Nowe polaczenie: %s -> %s", this.socket.getRemoteSocketAddress(), this.socket.getLocalSocketAddress()));
        this.packetReceiver = new CInputStreamThread(this);
        this.packetSender = new COutputStreamThread(this);
    }
    
    @Override
    public String toString() {
        return String.format("%s", this.getName());
    }
    
    public String getName() {
        return (this.currentSession != null) ? this.currentSession.getNickname() : this.name;
    }
    
    static {
        Player.players = Collections.synchronizedList(new ArrayList<Player>());
        L = Logger.getLogger("Player");
    }
}
