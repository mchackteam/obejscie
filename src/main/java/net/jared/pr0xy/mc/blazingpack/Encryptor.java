package net.jared.pr0xy.mc.blazingpack;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.management.ManagementFactory;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.UUID;
import java.util.zip.ZipFile;

import net.jared.pr0xy.mc.server.PlayerServer;
import net.jared.pr0xy.mc.server.Server;
import net.md_5.bungee.api.ChatColor;

public class Encryptor
{
    public static synchronized String encrypt(Server server, String key) throws IOException {
        try {
            checkUpdate(server);
        }
        catch (NoSuchAlgorithmException e) {
            //server.sendMsg(PlayerServer.c().append(e.toString()).color(ChatColor.GRAY));
        	server.sendMsg(PlayerServer.c().append("Wystapil blad zwiazany z systemem na maszynie. Prosze zglosic ten problem do Teamu BlazingHack."));
        }
        //server.sendMsg(PlayerServer.c().append("Kodowanie handshake...").color(ChatColor.GRAY));
        String file = null;
        switch (OSValidator.OSType) {
            case WINDOWS: {
                file = extract("acpackWindows");
                break;
            }
            case LINUX:
            case SOLARIS: {
                file = extract("acpackLinux");
                break;
            }
            case MACOS: {
                file = extract("acpackMac");
                break;
            }
            default: {
                throw new RuntimeException("Unknow OS!");
            }
        }
        server.sendMsg(PlayerServer.c().append("Prosze czekac...").color(ChatColor.GRAY));
        ProcessBuilder pb = new ProcessBuilder(new String[] { file });
        pb.redirectErrorStream(true);
        Process p = pb.start();
        InputStream is = p.getInputStream();
        OutputStream os = p.getOutputStream();
        BufferedReader isb = new BufferedReader(new InputStreamReader(is));
        BufferedWriter osb = new BufferedWriter(new OutputStreamWriter(os));
        osb.write(String.valueOf(key) + System.lineSeparator());
        osb.flush();
        String k = isb.readLine();
        //server.sendMsg(PlayerServer.c().append("Wynik:").color(ChatColor.GRAY).append((k.length() > 32) ? (String.valueOf(k.substring(0, 32)) + "...") : k).color(ChatColor.RED));
        p.destroy();
        try {
            p.waitFor();
        }
        catch (Throwable t) {}
        try {
            //server.sendMsg(PlayerServer.c().append("Kod wyjscia: " + p.exitValue()).color(ChatColor.GRAY));
        }
        catch (Throwable t2) {}
        new File(file).delete();
        return k;
    }
    
    private static String extract(String name) throws IOException {
        File f = new File(OSValidator.BLAZINGPACK_1710, String.valueOf(new BigInteger(130, new SecureRandom()).toString(16)) + ((OSValidator.OSType == OSValidator.OS.WINDOWS) ? ".exe" : ""));
        f.deleteOnExit();
        File zip = new File(OSValidator.BLAZINGPACK_1710, "blazingpack_1.7.10App.jar");
        ZipFile zf = new ZipFile(zip);
        byte[] b = new byte[8192];
        InputStream is = zf.getInputStream(zf.getEntry(name));
        FileOutputStream fos = new FileOutputStream(f);
        int n;
        while ((n = is.read(b)) != -1) {
            fos.write(b, 0, n);
        }
        fos.close();
        zf.close();
        f.setExecutable(true);
        f.setWritable(true);
        f.setReadable(true);
        return f.getAbsolutePath();
    }
    
    @SuppressWarnings("resource")
	public static void checkUpdate(Server server) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        File f = new File(OSValidator.BLAZINGPACK_1710, "blazingpack_1.7.10App.jar");
        if (f.exists()) {
            InputStream is = new FileInputStream(f);
            byte[] buff = new byte[8192];
            int n;
            while ((n = is.read(buff)) != -1) {
                md.update(buff, 0, n);
            }
            byte[] myDigest = md.digest();
            is.close();
            server.sendMsg(PlayerServer.c().append("Weryfikacja pliku w sandboxie...").color(ChatColor.GRAY));
            URL u = new URL("http://blazingpack.pl/api/files/client/1.7.10/md5");
            is = u.openStream();
            byte[] remoteDigest = new byte[32];
            StringBuilder sb = new StringBuilder();
            byte[] array;
            for (int length = (array = myDigest).length, i = 0; i < length; ++i) {
                byte b = array[i];
                sb.append(String.format("%02x", b));
            }
            new DataInputStream(is).readFully(remoteDigest);
            is.close();
            if (sb.toString().equals(new String(remoteDigest).toLowerCase())) {
                //server.sendMsg(PlayerServer.c().append("Sprawdzono!").color(ChatColor.GRAY));
                return;
            }
        }
        //server.sendMsg(PlayerServer.c().append("Pobieranie update..").color(ChatColor.GRAY));
        if (System.getProperty("bhp.admin", "false").equalsIgnoreCase("true")) {
	        URL u2 = new URL("http://blazingpack.pl/api/files/client/1.7.10/size");
	        InputStream is2 = u2.openStream();
	        long size = 0L;
	        byte[] buff2 = new byte[8192];
	        int n2;
	        while ((n2 = is2.read()) != -1) {
	            if (n2 >= 48 && n2 <= 57) {
	                size *= 10L;
	                size += n2 - 48;
	            }
	        }
	        is2.close();
	        u2 = new URL("http://blazingpack.pl/api/files/client/1.7.10/download");
	        is2 = u2.openStream();
	        FileOutputStream fos = new FileOutputStream(new File(OSValidator.BLAZINGPACK_1710, "blazingpack_1.7.10App.jar"));
	        long downloaded = 0L;
	        long lastMsg = System.currentTimeMillis();
	        while ((n2 = is2.read(buff2)) != -1) {
	            downloaded += n2;
	            fos.write(buff2, 0, n2);
	            if (System.currentTimeMillis() - lastMsg > 1000L) {
	                server.sendMsg(PlayerServer.c().append(String.format("Pobieranie... %3.2f%%", downloaded * 100.0 / size)).color(ChatColor.GRAY));
	                lastMsg = System.currentTimeMillis();
	            }
	        }
	        fos.close();
	        is2.close();
	        server.sendMsg(PlayerServer.c().append("Pobrano!").color(ChatColor.GRAY));
        } else {
        	server.sendMsg(PlayerServer.c().append("Plik niezgodny... Wymagana aktualizacja, ale proxy uruchomione jest w trybie retail.").color(ChatColor.RED));
        	server.sendMsg(PlayerServer.c().append("Prosze zglosic ten problem do Teamu BlazingHack."));
        	
        }
    }
    
    public static String a(String string) {
        String machineName = ManagementFactory.getRuntimeMXBean().getName();
        int n = machineName.indexOf(64);
        try {
            if (n < 1) {
                return string;
            }
        }
        catch (NumberFormatException ex) {}
        try {
            return Long.toString(Long.parseLong(machineName.substring(0, n)));
        }
        catch (NumberFormatException e) {
            return string;
        }
    }
    
    public static String encrypt(Server server, String username, String serverName, int port) throws IOException {
        String kod = String.valueOf(serverName) + "|" + username + "|" + UUID.randomUUID().toString().replace("-", "") + "|null|" + System.currentTimeMillis() + "|" + new File(OSValidator.BLAZINGPACK_1710, "blazingpack_1.7.10App.jar").getAbsolutePath() + "|<PID>|unsupported|unsupported|unsupported|http://blazingpack.pl/|" + UUID.randomUUID();
        return encrypt(server, kod);
    }
    
    public static void main(String[] args) {
//        System.out.println(UUID.randomUUID().toString().length());
    }
}
