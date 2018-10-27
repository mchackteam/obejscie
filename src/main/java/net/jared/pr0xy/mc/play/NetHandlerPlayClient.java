package net.jared.pr0xy.mc.play;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import net.jared.pr0xy.mc.blazingpack.EncryptorUnsafe;
import net.jared.pr0xy.mc.play.client.C00PacketKeepAlive;
import net.jared.pr0xy.mc.play.client.C01PacketChatMessage;
import net.jared.pr0xy.mc.play.client.C12EntityVelocity;
import net.jared.pr0xy.mc.play.server.S02PacketChat;
import net.jared.pr0xy.mc.play.server.S05PacketSpawnPosition;
import net.jared.pr0xy.mc.play.server.S07PacketRespawn;
//import net.jared.pr0xy.mc.play.client.C28PacketEffect;
import net.jared.pr0xy.mc.play.server.S08PacketPlayerPosLook;
import net.jared.pr0xy.mc.play.server.S39PacketPlayerAbilities;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.utils.SRVResolver;
import net.jared.pr0xy.mc.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class NetHandlerPlayClient implements INetHandler
{
    private Player player;
    private static Runtime R;
    private static Logger L;

    public NetHandlerPlayClient(Player player) {
        this.player = player;
    }

    public void handle(C00PacketKeepAlive packet) {
        this.player.packetSender.onReceivePing(packet);
    }

    @Override
    public int getState() {
        return 1;
    }

    private static String humanReadableByteCount(long bytes) {
        if (bytes < 1536L) {
            return String.valueOf(bytes) + " B";
        }
        int exp = (int)(Math.log(bytes) / Math.log(1536.0));
        String pre = String.valueOf("KMGTPE".charAt(exp - 1)) + "i";
        return String.format("%.2f %sB", bytes / Math.pow(1024.0, exp), pre);
    }

    private ComponentBuilder c() {
        return Utils.c();
    }

    private ComponentBuilder irc() {
        return Utils.irc();
    }

    private ComponentBuilder clearchat() {
        return Utils.clearchat();
    }
    private void s(ComponentBuilder component) {
        this.player.packetSender.sendPacket(new S02PacketChat(component.create()));
    }

//    private void chat(String[] vargs) {
//        if (vargs.length == 1) {
//            this.s(this.c().append("#chat").color(ChatColor.RED).append(" (1/0)").color(ChatColor.DARK_GRAY));
//        }
//        else {
//            vargs[1] = vargs[1].toLowerCase();
//            boolean doWlaczenia = vargs[1].equals("true") || vargs[1].equals("on") || vargs[1].equals("wlacz") || vargs[1].equals("tak") || vargs[1].equals("1");
//            boolean doWylaczenia = vargs[1].equals("false") || vargs[1].equals("off") || vargs[1].equals("wylacz") || vargs[1].equals("nie") || vargs[1].equals("0");
//            if (!doWlaczenia && !doWylaczenia) {
//                this.s(this.c().append("#chat").color(ChatColor.RED).append(" (1/0)").color(ChatColor.DARK_GRAY));
//            }
//            else if (doWlaczenia) {
//                this.player.isolatedChat = true;
//            }
//            else {
//                this.player.isolatedChat = false;
//            }
//            this.s(this.c().append(this.player.isolatedChat ? "Wlaczyles" : "Wylaczyles").color(ChatColor.RED).append(" mozliwosc pisania na czacie w proxy!").color(ChatColor.GRAY));
//        }
//    }

    private void ping(String[] vargs) {
        this.s(this.c().append("Ping: ").color(ChatColor.GRAY).append(String.format("%d ms", this.player.packetSender.getCurrentPing())).color(ChatColor.YELLOW).append(" (klient - serwer proxy)").color(ChatColor.GRAY));
    }

    private void wyczysc(String[] vargs) {
        for (int i = 0; i < 100; ++i) {
            Utils.broadcast(this.clearchat().append(" "));
        }
        Utils.broadcast(this.c().append(this.player.toString()).color(ChatColor.YELLOW).append(" wyczyscil czat!").color(ChatColor.GRAY));
    }

    private void dns(String[] vargs) {
        if (vargs.length < 2) {
            this.s(this.c().append("#dns").color(ChatColor.YELLOW).append(" (serwer) (usluga)").color(ChatColor.DARK_GRAY));
        }
        else {
            String service = "minecraft";
            if (vargs.length > 2) {
                service = vargs[2];
            }
            SRVResolver resolved = new SRVResolver(vargs[1], service);
            if (resolved.getException() != null) {
                this.s(this.c().append("Blad: ").color(ChatColor.GRAY).append(resolved.getException()).color(ChatColor.RED));
            }
            if (resolved.getSRV() == null) {
                this.s(this.c().append("Nie znaleziono!").color(ChatColor.RED));
            }
            else {
                if (resolved.getExceptionWhileParsing() != null) {
                    this.s(this.c().append("Blad: ").color(ChatColor.GRAY).append(resolved.getExceptionWhileParsing()).color(ChatColor.RED));
                }
                this.s(this.c().append("Rekord: ").color(ChatColor.GRAY).append(resolved.getSRV()).color(ChatColor.YELLOW));
                if (resolved.getDomain() != null) {
                    this.s(this.c().append("IP: ").color(ChatColor.GRAY).append(resolved.getDomain()).color(ChatColor.YELLOW));
                }
                if (resolved.getPort() != -1) {
                    this.s(this.c().append("Port: ").color(ChatColor.GRAY).append(Integer.toString(resolved.getPort())).color(ChatColor.RED));
                }
            }
        }
    }

    private void ram(String[] vargs) {
        long freeMemory = NetHandlerPlayClient.R.freeMemory();
        long totalMemory = NetHandlerPlayClient.R.totalMemory();
        long maxMemory = NetHandlerPlayClient.R.maxMemory();
        long inUseMemory = totalMemory - freeMemory;
        //int inUse = (int)(inUseMemory * 100L / maxMemory);
        //int declared = (int)(totalMemory * 100L / maxMemory);
        /*ComponentBuilder builder = this.c();
        StringBuilder etapy = new StringBuilder();
        for (int i = 0; i < inUse; ++i) {
            etapy.append('|');
        }
        builder.append(etapy.toString()).color(ChatColor.RED);
        etapy = new StringBuilder();
        for (int i = inUse; i < declared; ++i) {
            etapy.append('|');
        }
        builder.append(etapy.toString()).color(ChatColor.YELLOW);
        etapy = new StringBuilder();
        for (int i = declared; i < 100; ++i) {
            etapy.append('|');
        }
        builder.append(etapy.toString()).color(ChatColor.GREEN);
        this.s(builder);*/
        this.s(this.c().append("Pamiec uzywana: ").color(ChatColor.GRAY).append(humanReadableByteCount(inUseMemory)).color(ChatColor.RED));
        this.s(this.c().append("Pamiec zadeklarowana: ").color(ChatColor.GRAY).append(humanReadableByteCount(totalMemory)).color(ChatColor.GREEN));
        this.s(this.c().append("Pamiec maksymalna: ").color(ChatColor.GRAY).append(humanReadableByteCount(maxMemory)).color(ChatColor.BLUE));
    }

    private void disconnect(String[] vargs) {
        this.player.clearTab();
        if (this.player.currentSession != null && this.player.currentSession.close()) {
            this.s(this.c().color(ChatColor.WHITE).append("Rozlaczono pomyslnie!").color(ChatColor.GRAY));
        }
        else {
            this.s(this.c().append("Nie jestes na zadnym serwerze!").color(ChatColor.GRAY));
        }
    }

    private void setadmin(String[] vargs) {
    	if (vargs.length < 2) {
    		this.s(this.c().append("Nieznana komenda.").color(ChatColor.GRAY));
    	} else {
    		if (vargs[1].equals(System.getProperty("bhp.pass"))) {
    			this.s(c().append("Jestes teraz adminem :3").color(ChatColor.GRAY));
    			this.player.admin = true;
    		} else {
    			this.s(this.c().append("Nieznana komenda.").color(ChatColor.GRAY));
    		}
    	}
    }
    
    private void zmiennick(String[] vargs) {
        if (vargs.length < 2) {
            this.s(this.c().append("Poprawne uzycie: ").color(ChatColor.GRAY).append("#zmiennick").color(ChatColor.RED).append(" <nick> ").color(ChatColor.DARK_GRAY).append("zmienia nick (only nopremium)").color(ChatColor.GRAY));
        }
        else if (this.player.currentSession == null || this.player.currentSession.isDisconnected()) {
            this.player.name = vargs[1];
            for (int i = 2; i < vargs.length; ++i) {
                Player tmp106_103 = this.player;
                tmp106_103.name = String.valueOf(tmp106_103.name) + " " + vargs[i];
            }
            this.s(this.c().color(ChatColor.WHITE).append("Nick zostal zmieniony pomyslnie!").color(ChatColor.GRAY));
        }
        else {
            this.s(this.c().append("Nie mozna zmieniac nicku, gdy jestes polaczony z jakims serwerem! Uzyj ").color(ChatColor.GRAY).append("#disconnect").color(ChatColor.RED).append(", aby sie rozlaczyc!").color(ChatColor.GRAY));
        }
    }

    private void connect(String[] vargs) {
        if (vargs.length == 1) {
            this.s(this.c().append("Poprawne uzycie: ").color(ChatColor.GRAY).append("#connect").color(ChatColor.RED).append(" <serwer:port>").color(ChatColor.DARK_GRAY).append(" laczy sie z serwerem").color(ChatColor.GRAY));
        }
        else {
        	if (this.player.admin) {
	            try {
	                String hostname = vargs[1];
	                int port = 25565;
	                if (hostname.contains(":")) {
	                    String[] splitted = hostname.split(":", 2);
	                    hostname = splitted[0];
	                    port = Integer.parseInt(splitted[1]);
	                }
	                if (this.player.currentSession != null) {
	                    this.player.currentSession.close();
	                    this.player.currentSession = null;
	                }
	                Utils.broadcast(this.c().append(this.player.name).color(ChatColor.RED).append(" -> ").color(ChatColor.GREEN).append(vargs[1]).color(ChatColor.RED));
	                this.player.currentSession = new EncryptorUnsafe(hostname, port, Proxy.NO_PROXY, this.player);
	            }
	            catch (Throwable t) {
	                this.s(this.c().append("Error: ").color(ChatColor.GRAY).append(t.toString()).color(ChatColor.RED));
	            }
        	} else {
        		this.s(c().append("Brak uprawnien!").color(ChatColor.RED));
        	}
        }
    }

    private void connectsocks(String[] vargs) {
        if (vargs.length < 3) {
            this.s(this.c().append("Poprawne uzycie: ").color(ChatColor.GRAY).append("#connectsocks").color(ChatColor.YELLOW).append(" <serwer:port> <adres proxy:port>").color(ChatColor.DARK_GRAY).append(" laczy sie z serwerem za pomoca proxy SOCKS4/5").color(ChatColor.GRAY));
        }
        else {
            try {
                String hostname = vargs[1];
                int port = 25565;
                if (hostname.contains(":")) {
                    String[] splitted = hostname.split(":", 2);
                    hostname = splitted[0];
                    port = Integer.parseInt(splitted[1]);
                }
                String proxyhostname = vargs[2];
                int proxyport = 8080;
                if (proxyhostname.contains(":")) {
                    String[] splitted2 = proxyhostname.split(":", 2);
                    proxyhostname = splitted2[0];
                    proxyport = Integer.parseInt(splitted2[1]);
                }
                if (this.player.currentSession != null) {
                    this.player.currentSession.close();
                    this.player.currentSession = null;
                }
                Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyhostname, proxyport));
                Utils.broadcast(this.c().append(this.player.toString()).color(ChatColor.YELLOW).append(" -> ").color(ChatColor.GRAY).append(vargs[1]).color(ChatColor.YELLOW));
//                new PlayerServer(hostname, port, proxy, this.player);
                this.player.currentSession = new EncryptorUnsafe(hostname, port, proxy, this.player);
            }
            catch (Throwable t) {
                this.s(this.c().append("Blad: ").color(ChatColor.GRAY).append(t.toString()).color(ChatColor.RED));
            }
        }
    }

    private void connecthttp(String[] vargs) {
        if (vargs.length < 3) {
            this.s(this.c().append("Poprawne uzycie: ").color(ChatColor.GRAY).append("#connecthttp").color(ChatColor.RED).append(" <serwer:port> <adres proxy:port>").color(ChatColor.DARK_GRAY).append(" laczy sie z serwerem za pomoca proxy HTTP").color(ChatColor.GRAY));
        }
        else {
            try {
                String hostname = vargs[1];
                int port = 25565;
                if (hostname.contains(":")) {
                    String[] splitted = hostname.split(":", 2);
                    hostname = splitted[0];
                    port = Integer.parseInt(splitted[1]);
                }
                String proxyhostname = vargs[2];
                int proxyport = 8080;
                if (proxyhostname.contains(":")) {
                    String[] splitted2 = proxyhostname.split(":", 2);
                    proxyhostname = splitted2[0];
                    proxyport = Integer.parseInt(splitted2[1]);
                }
                if (this.player.currentSession != null) {
                    this.player.currentSession.close();
                }
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyhostname, proxyport));
                Utils.broadcast(this.c().append(this.player.toString()).color(ChatColor.RED).append(" -> ").color(ChatColor.GREEN).append(vargs[1]).color(ChatColor.RED));
//                new PlayerServer(hostname, port, proxy, this.player);
                this.player.currentSession = new EncryptorUnsafe(hostname, port, proxy, this.player);
            }
            catch (Throwable t) {
                this.s(this.c().append("Blad: ").color(ChatColor.GRAY).append(t.toString()).color(ChatColor.RED));
            }
        }
    }

    private void online(String[] vargs) {
        int players = 0;
        TreeMap<Integer, TreeSet<Player>> online = new TreeMap<Integer, TreeSet<Player>>();
        for (Player sh : Player.players) {
            if (sh.name != null) {
                @SuppressWarnings("unused")
				TreeMap<Integer, TreeSet<Player>> str = online;
                ++players;
                this.s(this.c().append("Gracze: ").color(ChatColor.GRAY).append(String.valueOf(sh)).color(ChatColor.DARK_GRAY));
            }
        }
        this.s(this.c().append("Osob online: ").color(ChatColor.GRAY).append(Integer.toString(players)).color(ChatColor.DARK_GRAY));
    }
    
    private void help(String[] vargs) {
        this.s(this.c().append("§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e"));
        if (player.admin)
        this.s(this.c().append("#connect").color(ChatColor.GREEN).append(" <ip:port> ").color(ChatColor.RED).append("laczy sie z serwerem").color(ChatColor.GOLD));
        this.s(this.c().append("#connectsocks").color(ChatColor.GREEN).append(" <ip:port> <ip proxy:port>").color(ChatColor.RED).append(" laczy sie z serwerem za pomoca proxy SOCKS4/5").color(ChatColor.GOLD));
        this.s(this.c().append("#connecthttp").color(ChatColor.GREEN).append(" <ip:port> <ip proxy:port>").color(ChatColor.RED).append(" laczy sie z serwerem za pomoca proxy HTTP").color(ChatColor.GOLD));
        this.s(this.c().append("#disconnect").color(ChatColor.GREEN).append(" rozlacza cie od serwera z ktorym jestes aktualnie polaczony").color(ChatColor.GOLD));
        this.s(this.c().append("#zmiennick").color(ChatColor.GREEN).append(" <nick> ").color(ChatColor.RED).append("zmienia nick (only nopremium)").color(ChatColor.GOLD));
        this.s(this.c().append("#ping").color(ChatColor.GREEN).append(" wyswietla twoj ping i serwera proxy").color(ChatColor.GOLD));
        if (player.admin)
        this.s(this.c().append("#ram").color(ChatColor.GREEN).append(" wyswiela stan pamieci RAM serwera proxy").color(ChatColor.GOLD));
        this.s(this.c().append("#dns").color(ChatColor.GREEN).append(" <serwer> <usluga>").color(ChatColor.RED).append(" wyszukuje port, na ktorym dziala usluga. Domyslna usluga to minecraft").color(ChatColor.GOLD));
//        this.s(this.c().append("#chat").color(ChatColor.GREEN).append(" <1/0>").color(ChatColor.RED).append(" wlacza mozliwosc pisania w proxy").color(ChatColor.GOLD));
        this.s(this.c().append("#online").color(ChatColor.GREEN).append(" wyswietla graczy, ktorzy sa aktualnie w proxy").color(ChatColor.GOLD));
        if (player.admin)
        this.s(this.c().append("#wyczysc").color(ChatColor.GREEN).append(" czysci chat!").color(ChatColor.GOLD));
        this.s(this.c().append("@wiadomosc").color(ChatColor.GREEN).append(" wysyla globalna wiadomosc na proxy").color(ChatColor.GOLD));
        this.s(this.c().append("§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e◉§6◉§c◉§4◉§3◉§b◉§a◉§2◉§e"));
    }
    
    public void handle(C01PacketChatMessage packet) {
        NetHandlerPlayClient.L.info(String.valueOf(this.player.toString()) + " >>" + packet.message);
        String[] vargs = packet.message.split(" ");
        vargs[0] = vargs[0].toLowerCase();
        if (vargs[0].equals("#help")) {
            this.help(vargs);
        }
        else if (vargs[0].equalsIgnoreCase("#connect")) {
        	if (player.admin) {
        		this.player.clearTab();
        		this.connect(vargs);
        	} else 
        		s(c().append("Brak uprawnien").color(ChatColor.RED));
        }
        else if (vargs[0].equals("#connectsocks")) {
        	this.player.clearTab();
            this.connectsocks(vargs);
        }
        else if (vargs[0].equals("#connecthttp")) {
        	this.player.clearTab();
            this.connecthttp(vargs);
        }
//        else if (vargs[0].equals("#chat")) {
//            this.chat(vargs);
//        }
        else if (vargs[0].equals("#online")) {
        	if (player.admin)
        		this.online(vargs);
        	else 
        		s(c().append("Brak uprawnien").color(ChatColor.RED));
            
        }
        else if (vargs[0].equals("#ping")) {
            this.ping(vargs);
        }
        else if (vargs[0].equals("#dns")) {
            this.dns(vargs);
        }
        else if (vargs[0].equals("#ram")) {
            this.ram(vargs);
        }
        else if (vargs[0].equals("#disconnect")) {
            this.disconnect(vargs);
        }
        else if (vargs[0].equals("#wyczysc")) {
            this.wyczysc(vargs);
        }
        else if (vargs[0].equals("#zmiennick")) {
            this.zmiennick(vargs);
        }
        else if (vargs[0].equals("#admin")) {
            this.setadmin(vargs);
        }
        else if (packet.message.startsWith("@")) {
            Utils.broadcast(this.irc().append(String.format("[%s] ", this.player.name)).color(ChatColor.AQUA).color(ChatColor.BOLD).append(packet.message.replace("@", "")).color(ChatColor.BLUE));
        }
        else if (this.player.currentSession != null && this.player.currentSession.isPlaying()) {
            this.player.currentSession.getPacketSender().sendPacket(packet);
        }
        else {
            this.s(this.c().append("Nieznana komenda.").color(ChatColor.GRAY));
        }
    }



    public void emptyWorld() {
        this.emptyWorld(false);
    }
    
    public void emptyWorld(boolean firstLogin) {
        this.player.currentSession = null;
        this.player.isLogged = false;
        this.player.currentDimension = 1;
        if (!firstLogin) {
            this.player.packetSender.sendPacket(new S07PacketRespawn(1, 0, 0, "default"));
        }
        this.player.packetSender.sendPacket(new S05PacketSpawnPosition(0, 128, 0));
        S39PacketPlayerAbilities abilities = new S39PacketPlayerAbilities();
        abilities.allowFlying = true;
        abilities.disableDamage = true;
        abilities.flySpeed = 0.0f;
        abilities.isCreativeMode = true;
        abilities.isFlying = true;
        abilities.walkSpeed = 0.0f;
        this.player.packetSender.sendPacket(abilities);
        this.player.packetSender.sendPacket(new S08PacketPlayerPosLook(0.0, 128.0, 0.0, 0.0f, 0.0f, true));
        this.player.packetSender.isLogged = true;
//        this.player.packetSender.sendPacket(new C29PacketSoundEffect("random.bowhit", 0, 126, 0, 0.0f, (byte)1));
//        this.player.packetSender.sendPacket(new C28PacketEffect(1005, 0, (byte)126, 0, 2257, false));
//        this.player.packetSender.sendPacket(new C2APacketParticle("heart", 0.0f, 126.0f, 0.0f, 0.0f, 3.0f, 0.0f, 10.0f, 20));
//        this.player.packetSender.sendPacket(new C1FPacketExperience(1.0f, (short)1337, (short)1));
//        this.player.packetSender.sendPacket(new C1DEntityEffect(1, (byte) 8, (byte) 3, (short) 3000));
//        this.player.packetSender.sendPacket(new C1DEntityEffect(1, (byte) 16, (byte) 3, (short) 3000));
//        this.player.packetSender.sendPacket(new C1DEntityEffect(1, (byte) 1, (byte) 3, (short) 3000));
//        this.player.packetSender.sendPacket(new C1DEntityEffect(1, (byte) 3, (byte) 3, (short) 3000));
//        this.player.packetSender.sendPacket(new C1DEntityEffect(1, (byte) 5, (byte) 3, (short) 3000));
//        this.player.packetSender.sendPacket(new C3DPacketDisplayScoreboard((byte) 1, "Jared"));
        this.player.packetSender.sendPacket(new C12EntityVelocity(1, (short) 0000, (short) 0000, (short) 0000));
//        this.player.packetSender.sendPacket(new C3DPacketDisplayScoreboard((byte)0, "das"));
        this.player.packetSender.sendPacket(new S02PacketChat((byte)1, Utils.c().append("Witaj ").color(ChatColor.GRAY).append(this.player.getName()).color(ChatColor.RED).append(" na proxy ").color(ChatColor.GRAY).append("BlazingHacka").color(ChatColor.RED).create()));
        this.player.packetSender.sendPacket(new S02PacketChat((byte)1, Utils.c().append("Aby otrzymac pomoc wpisz ").color(ChatColor.GRAY).append("#help").color(ChatColor.RED).create()));
    }
    
//    public void handle(C16PacketClientStatus packet) {
//        if (packet.getAction() == 0) {
//            this.player.packetSender.sendPacket(new S07PacketRespawn(1, 0, this.player.gamemode, "default"));
//        }
//        if (this.player.currentSession != null) {
//            this.player.currentSession.getPacketSender().sendPacket(packet);
//        }
//    }

//    this.player.getName()
    
    static {
        R = Runtime.getRuntime();
        L = Logger.getLogger("NetHandlerPlayClient");
    }
}
