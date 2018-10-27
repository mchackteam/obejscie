package net.jared.pr0xy.mc.server;

import java.io.InputStream;
import net.jared.pr0xy.mc.login.client.C00PacketLoginStart;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.handshake.client.C00Handshake;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import net.jared.pr0xy.mc.streams.PacketOutputStream;

import java.net.InetSocketAddress;

import net.jared.pr0xy.mc.play.server.S02PacketChat;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.jared.pr0xy.mc.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.jared.pr0xy.mc.utils.SRVResolver;
import java.io.IOException;
import java.net.Proxy;
import java.net.Socket;

import net.jared.pr0xy.mc.threads.SInputStreamThread;
import net.jared.pr0xy.mc.threads.SOutputStreamThread;

public class PlayerServer extends Server
{
    public SOutputStreamThread packetSender;
    public SInputStreamThread packetReceiver;
    public String domain;
    public String hostname;
    public int port;
    public String name;
    public Player handler;
    public Socket stream;
    public Proxy proxy;
    
    public String getHostnameToHandshake() throws IOException {
        return this.hostname;
    }
    
    public PlayerServer(String hostname, int port, Proxy proxy, Player handler) {
        this.proxy = Proxy.NO_PROXY;
        this.domain = hostname;
        SRVResolver srv = new SRVResolver(hostname, "minecraft");
        if (srv.getException() == null && srv.getExceptionWhileParsing() == null) {
            this.hostname = srv.getDomain();
            this.port = srv.getPort();
        }
        else {
            this.port = port;
            this.hostname = hostname;
        }
        if (this.hostname.endsWith(".")) {
            this.hostname = this.hostname.substring(0, this.hostname.length() - 1);
        }
        this.handler = handler;
        this.proxy = proxy;
        if (this.proxy == null) {
            this.proxy = Proxy.NO_PROXY;
        }
        this.name = handler.name;
        this.start();
    }
    
    @Override
    public synchronized boolean close() {
        if (this.stream != null) {
            try {
                this.stream.close();
            }
            catch (IOException ex) {}
            Utils.broadcast(c().append(this.name).color(ChatColor.RED).append(" \u2716 ").color(ChatColor.DARK_RED).append(String.valueOf(this.hostname) + ((this.port == 25565) ? "" : (":" + this.port))).color(ChatColor.RED));
        }
        boolean disconnected = this.stream != null;
        this.stream = null;
        return disconnected;
    }
    
    @Override
    public boolean isDisconnected() {
        return this.stream == null;
    }
    
    public static ComponentBuilder c() {
        return Utils.c();
    }
    
    @Override
    public void sendMsg(ComponentBuilder component) {
        this.handler.packetSender.sendPacket(new S02PacketChat(component.create()));
    }
    
    @SuppressWarnings("resource")
	@Override
    public void run() {
        try {
            this.handler.clearTab();
            this.sendMsg(c().append("Proba polaczenia...").color(ChatColor.GRAY));
            String myHostname = this.getHostnameToHandshake();
            this.sendMsg(c().append("Pingowanie: " + myHostname).color(ChatColor.GRAY));
            Socket privateStream = new Socket(this.proxy);
            privateStream.connect(new InetSocketAddress(this.hostname, this.port), 20000);
            PacketOutputStream pos_ = new PacketOutputStream(privateStream.getOutputStream());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PacketOutputStream pos = new PacketOutputStream(baos);
            new C00Handshake(this.handler.protocolVersion, myHostname, this.port, 1).write(pos);
            pos_.writeVarInt(baos.size());
            baos.writeTo(pos_);
            pos_.flush();
            baos = new ByteArrayOutputStream();
            new PacketOutputStream(baos).writeVarInt(0);
            pos_.writeVarInt(baos.size());
            baos.writeTo(pos_);
            pos_.flush();
            PacketInputStream pis = new PacketInputStream(privateStream.getInputStream());
            pis.readVarInt();
            pis.readVarInt();
            pis.readString(32767);
            long a = System.currentTimeMillis();
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            PacketOutputStream pos2 = new PacketOutputStream(baos2);
            pos2.writeVarInt(1);
            pos2.writeLong(0L);
            pos_.writeVarInt(baos2.size());
            baos2.writeTo(pos_);
            pos_.flush();
            PacketInputStream pis2 = new PacketInputStream(privateStream.getInputStream());
            pis2.readVarInt();
            pis2.readVarInt();
            pis2.readLong();
            a = System.currentTimeMillis() - a;
            this.sendMsg(c().append("Odebrano ping ").color(ChatColor.GRAY).append(String.valueOf(a) + " ms").color(ChatColor.RED));
            privateStream.close();
            
            (this.stream = new Socket(this.proxy)).connect(new InetSocketAddress(this.hostname, this.port), 20000);
            this.sendMsg(c().append("Trwa generowanie outputu acpack...").color(ChatColor.GRAY));
            this.handler.currentSession = this;
            this.packetSender = new SOutputStreamThread(this.handler, this);
            this.packetReceiver = new SInputStreamThread(this.handler, this);
            this.packetSender.sendPacket(new C00Handshake(this.handler.protocolVersion, myHostname, this.port, 2));
            this.packetSender.sendPacket(new C00PacketLoginStart(this.name));
        }
        catch (IOException e) {
            this.close();
            this.sendMsg(c().append(String.format("Blad laczenia! Zglos to na fanpage: https://facebook.com/BlazingHack || Szczegoly: %s", e.toString())).color(ChatColor.RED));
            e.printStackTrace();
        }
    }
    
    @Override
    public String getNickname() {
        return this.name;
    }
    
    @Override
    public OutputStream getOutputStream() {
        try {
            return this.stream.getOutputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public SInputStreamThread getPacketReceiver() {
        return this.packetReceiver;
    }
    
    @Override
    public SOutputStreamThread getPacketSender() {
        return this.packetSender;
    }
    
    @Override
    public int getPort() {
        return this.port;
    }
    
    @Override
    public String getDefinedHostname() {
        return this.hostname;
    }
    
    @Override
    public void setKeepSession() {
        this.handler = null;
    }
    
    @Override
    public InputStream getInputStream() {
        try {
            return this.stream.getInputStream();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
