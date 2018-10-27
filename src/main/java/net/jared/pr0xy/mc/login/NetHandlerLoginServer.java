package net.jared.pr0xy.mc.login;

import net.jared.pr0xy.mc.play.NetHandlerPlayServer;
import net.jared.pr0xy.mc.login.server.S02PacketLoginSuccess;
import net.jared.pr0xy.mc.play.server.S02PacketChat;
import net.md_5.bungee.api.ChatColor;
import net.jared.pr0xy.mc.utils.Utils;
import net.jared.pr0xy.mc.login.server.S00PacketDisconnect;
import net.jared.pr0xy.mc.login.server.S03PacketEnableCompression;
import net.jared.pr0xy.mc.server.Server;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.play.INetHandler;

public class NetHandlerLoginServer implements INetHandler
{
    private Player player;
    private Server remoteServer;
    
    public NetHandlerLoginServer(Player handler, Server remoteServer) {
        this.player = handler;
        this.remoteServer = remoteServer;
    }
    
    @Override
    public int getState() {
        return 3;
    }
    
    public void handle(S03PacketEnableCompression packet) {
        this.remoteServer.getPacketReceiver().treshold = packet.threshold;
    }
    
    public void handle(S00PacketDisconnect packet) {
        this.player.packetSender.sendPacket(new S02PacketChat(Utils.c().append("Serwer rozlaczyl sie podczas logowania:").color(ChatColor.RED).create()));
        this.player.packetSender.sendPacket(new S02PacketChat(packet.reason));
    }
    
    public void handle(S02PacketLoginSuccess packet) {
        this.remoteServer.getPacketReceiver().netHandler = new NetHandlerPlayServer(this.player, this.remoteServer);
        this.player.packetSender.sendPacket(new S02PacketChat(Utils.c().append("Pobieranie terenu...").color(ChatColor.GRAY).create()));
    }
}
