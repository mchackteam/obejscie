package net.jared.pr0xy.mc.login;

import java.util.UUID;

import net.jared.pr0xy.mc.login.client.C00PacketLoginStart;
import net.jared.pr0xy.mc.login.server.S02PacketLoginSuccess;
import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.play.NetHandlerPlayClient;
import net.jared.pr0xy.mc.play.server.S01PacketJoinGame;
import net.jared.pr0xy.mc.play.server.S38PacketPlayerListItem;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.utils.Utils;
import net.md_5.bungee.api.ChatColor;

public class NetHandlerLoginClient implements INetHandler
{
    private Player player;
    
    public NetHandlerLoginClient(Player player) {
        this.player = player;
    }
    
    private void nowaSesja(String name) {
        this.player.name = name;
        this.player.packetReceiver.netHandler = new NetHandlerPlayClient(this.player);
        Player.players.add(this.player);
        UUID uuid = new UUID(16384L, Long.MIN_VALUE);
        this.player.packetSender.sendPacket(new S02PacketLoginSuccess(uuid, name));
        S01PacketJoinGame joinPacket = new S01PacketJoinGame();
        joinPacket.entityID = 1;
        joinPacket.levelType = "default";
        joinPacket.difficulty = 0;
        joinPacket.dimension = 1;
        joinPacket.gamemode = 1;
        joinPacket.hardcoreFlag = false;
        joinPacket.maxPlayers = 60;
        this.player.packetSender.sendPacket(joinPacket);
        ((NetHandlerPlayClient)this.player.packetReceiver.netHandler).emptyWorld(true);
//        this.player.packetSender.sendPacket(new C29PacketSoundEffect("random.bowhit", 0, 128, 0, 0.5f, (byte)63));
        this.player.packetSender.sendPacket(new S38PacketPlayerListItem("§8Proxy by", true, 5));
        this.player.packetSender.sendPacket(new S38PacketPlayerListItem("§c§lBlazingHack", true, 5));
        this.player.packetSender.sendPacket(new S38PacketPlayerListItem(" ", true, 5));
    }
    
    public void handle(C00PacketLoginStart packet) {
        this.nowaSesja(packet.name);
        Utils.broadcast(Utils.c().append(this.player.name).color(ChatColor.RED).append(" dolaczyl do ").color(ChatColor.GRAY).append("Proxy").color(ChatColor.RED).append("!").color(ChatColor.GRAY));
    }
    
    @Override
    public int getState() {
        return 3;
    }
}
