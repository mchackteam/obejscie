package net.jared.pr0xy.mc.status;

import java.util.ArrayList;
import java.util.UUID;

import net.jared.pr0xy.mc.play.INetHandler;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.status.client.C00PacketServerQuery;
import net.jared.pr0xy.mc.status.client.C01PacketPing;
import net.jared.pr0xy.mc.status.server.S00PacketServerInfo;
import net.jared.pr0xy.mc.status.server.S01PacketPong;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class NetHandlerStatusClient implements INetHandler
{
    private Player handler;
    
    public NetHandlerStatusClient(Player handler) {
        this.handler = handler;
    }
    
    @Override
    public int getState() {
        return 2;
    }
    
    @SuppressWarnings("unused")
	private void sendPing() {
        S00PacketServerInfo.ServerStatusResponse res = new S00PacketServerInfo.ServerStatusResponse();
        res.favicon = "data:image/png;base64,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";
        TextComponent component = new TextComponent("§e§l§m>>§6§l§m---§f§m§l»§e§l Proxy by Team BlazingHack / NODE 1");
        component.setBold(true);
        component.setColor(ChatColor.RED);
//        component.wait((long) 1.0, 1);

//        component.setBold(true);
//        component.setColor(ChatColor.RED);
        res.description = component;
        res.version = new S00PacketServerInfo.MinecraftProtocolVersionIdentifier();
        res.version.name = "§8[§a§lBETA§8]";
        //res.version.protocol = this.handler.protocolVersion;
        
        // Pozwoli na wyswietlanie Version Name
        res.version.protocol = 99;
        
        /*if (res.version.protocol != 4 && res.version.protocol != 5 && res.version.protocol != 47 && res.version.protocol != 107) {
            res.version.protocol = 5;
        }*/
        res.players = new S00PacketServerInfo.PlayerCountData();
        res.players.max = 0;
        res.players.online = 0;
        ArrayList<S00PacketServerInfo.GameProfile> profiles = new ArrayList<S00PacketServerInfo.GameProfile>();
        for (Player sh : Player.players) {
            if (sh.name != null) {
                S00PacketServerInfo.PlayerCountData players2;
                S00PacketServerInfo.PlayerCountData players = players2 = res.players;
                ++players2.online;
                profiles.add(new S00PacketServerInfo.GameProfile(new UUID(16384L, Long.MIN_VALUE).toString(), sh.name));
            }
        }
        res.players.sample = profiles.toArray(new S00PacketServerInfo.GameProfile[profiles.size()]);
        S00PacketServerInfo resPacket = new S00PacketServerInfo(res);
        this.handler.packetSender.sendPacket(resPacket);
    }
    
    public void handle(C00PacketServerQuery packet) {
        while (true) {
            try {
                Thread.sleep(10L);
                if (this.handler.socket.isClosed()) {
                    return;
                }
            }
            catch (InterruptedException ex) {}
            if (this.handler.packetSender.priority.size() == 0 && this.handler.packetSender.packets.size() == 0 && this.handler.packetSender.customs.size() == 0) {
                this.sendPing();
            }
        }
    }
    
    public void handle(C01PacketPing packet) {
        this.handler.packetSender.sendPacket(new S01PacketPong(packet.time));
    }
}
