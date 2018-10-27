package net.jared.pr0xy.mc.play;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Logger;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.jared.pr0xy.mc.play.client.C17PacketCustomPayload;
import net.jared.pr0xy.mc.play.server.S01PacketJoinGame;
import net.jared.pr0xy.mc.play.server.S02PacketChat;
import net.jared.pr0xy.mc.play.server.S07PacketRespawn;
import net.jared.pr0xy.mc.play.server.S38PacketPlayerListItem;
import net.jared.pr0xy.mc.play.server.S3FPacketCustomPayload;
import net.jared.pr0xy.mc.play.server.S40PacketDisconnect;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.server.Server;
import net.jared.pr0xy.mc.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.chat.ComponentSerializer;

public class NetHandlerPlayServer implements INetHandler {
    public Player player;
    private Server remoteServer;
    private static Logger L;

    public NetHandlerPlayServer(Player player, Server remoteServer) {
        this.player = player;
        this.remoteServer = remoteServer;
    }

    @Override
    public int getState() {
        return 1;
    }

    public void handle(S01PacketJoinGame packet)
    {
        if (this.player == null) {
            return;
        }
        this.player.isLogged = true;
        this.player.packetSender.sendPacket(new S07PacketRespawn(
                packet.dimension == 0 ? -1 : 0, 0, 0, "default"));
        this.player.packetSender.sendPacket(new S07PacketRespawn(
                packet.dimension, 0, 0, "default"));
        this.player.packetSender.sendPacket(packet);
        this.player.packetSender.sendPacket(new S07PacketRespawn(
                packet.dimension == 0 ? -1 : 0, 0, 0, "default"));
        this.player.packetSender.sendPacket(new S07PacketRespawn(
                packet.dimension, 0, 0, "default"));
        this.player.currentDimension = packet.dimension;
    }

    public void handle(S07PacketRespawn packet)
    {
        if (this.player == null) {
            return;
        }
        this.player.isLogged = true;
        this.player.currentDimension = packet.dimension;
        this.player.packetSender.sendPacket(packet);
    }
    
    private ComponentBuilder c() {
        return Utils.c();
    }
    
    private void sendMsg(ComponentBuilder component) {
        if (this.player == null) {
            return;
        }
        this.player.packetSender.sendPacket(new S02PacketChat(component.create()));
    }
    
    public void handle(S40PacketDisconnect packet) {
        if (this.player == null) {
            return;
        }
        NetHandlerPlayServer.L.info("Rozlaczono z serwerem: " + packet.reason);
        this.sendMsg(this.c().append("Serwer rozlaczyl sie podczas gry:").color(ChatColor.RED));
        this.player.packetSender.sendPacket(new S02PacketChat(packet.reason));
        this.remoteServer.close();
        ((NetHandlerPlayClient)this.player.packetReceiver.netHandler).emptyWorld();
    }
    
    public void handle(S38PacketPlayerListItem packet) {
        if (this.player == null) {
            return;
        }
        if (packet.online) {
            if (!this.player.tab.contains(packet.playerName)) {
                this.player.tab.add(packet.playerName);
            }
        }
        else {
            this.player.tab.remove(packet.playerName);
        }
        this.player.packetSender.sendPacket(packet);
    }

    public void automsg(S02PacketChat packet) {
        while (true) {
            try {
                Thread.sleep(30000L);
                if (this.player == null) {
                    return;
                }
            } catch (InterruptedException ex) {
                this.sendMsg(this.c().append("Powered by BlazingHack. Lajknij naszego Fanpage :P https://facebook.com/BlazingHack").color(ChatColor.RED));
            }
        }
    }

    @SuppressWarnings("deprecation")
	public void handle(S3FPacketCustomPayload packet) {
        this.player.packetSender.sendPacket(packet);
        if (packet.channel.equals("MC|Brand")) {
            this.sendMsg(this.c().append("Silnik: ").color(ChatColor.GRAY).append(new String(packet.data)).color(ChatColor.RED));
            this.sendMsg(this.c().append("Podziekuj jaredowi :D").color(ChatColor.GREEN));
        }
        else if (packet.channel.equals("MC|TPack")) {
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            byte[] randomBytes = packet.data;
            URLClassLoader loader = null;
            Class<?> testClass = null;
            try {
                loader = new URLClassLoader(new URL[] { new File("blazingpack_1.7.10App.jar").toURL() });
                testClass = loader.loadClass("net.minecraft.client.f");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            buf.writeShort(randomBytes.length);
            buf.write(randomBytes);
            try {
                Constructor<?> testClassConstructor = testClass.getConstructor(Integer.TYPE);
                Object test = testClassConstructor.newInstance(64);
                Method getArrayMethod = testClass.getMethod("c", (Class<?>[])new Class[0]);
                Method getNewTestMethod = testClass.getMethod("b", byte[].class, Integer.TYPE, Integer.TYPE);
                byte[] arr = (byte[])getArrayMethod.invoke(test, new Object[0]);
                buf.writeShort(arr.length);
                buf.write(arr);
                byte[] joinString = { 99, 104, 117, 106, 32, 99, 105, 32, 119, 32, 100, 117, 112, 101, 0 };
                test = getNewTestMethod.invoke(test, joinString, 0, joinString.length);
                test = getNewTestMethod.invoke(test, randomBytes, 0, randomBytes.length);
                test = getNewTestMethod.invoke(test, arr, 0, arr.length);
                arr = (byte[])getArrayMethod.invoke(test, new Object[0]);
                buf.writeShort(arr.length);
                buf.write(arr);
                this.remoteServer.getPacketSender().sendPacket(new C17PacketCustomPayload("MC|TPack", buf.toByteArray()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (packet.channel.equals("MC|EPack")) {
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeLong(System.nanoTime());
            this.remoteServer.getPacketSender().sendPacket(new C17PacketCustomPayload("MC|EPack", buf.toByteArray()));
        }
    }
    
    public void handle(S02PacketChat packet) {
        try {
            NetHandlerPlayServer.L.info(String.valueOf("gracz " + this.remoteServer.getNickname()) + " z serwera " + this.remoteServer.getDefinedHostname() + ":" + this.remoteServer.getPort() + " = " + BaseComponent.toPlainText(ComponentSerializer.parse(packet.text)));
        }
        catch (Exception ex) {}
        if (this.player == null) {
            return;
        }
        this.player.packetSender.sendPacket(packet);
    }
    
    static {
        L = Logger.getLogger("NetHandlerPlayServer");
    }
}
