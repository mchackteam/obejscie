package net.jared.pr0xy.mc.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

import pl.blazinghack.proxy.Proxy;

public class MCServer extends Thread
{
    private static Logger L;
    private static boolean working;
    private static ServerSocket server;
    
    public static String oppasswd;
    
    public static void load() {
    }
    
    public static void stopServer() {
        if (MCServer.working) {
            MCServer.working = false;
            if (MCServer.server != null && !MCServer.server.isClosed()) {
                try {
                    MCServer.server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private MCServer() {
        super("MCServer");
        this.start();
    }
    
    @SuppressWarnings("unused")
	private boolean isAllowed() {
        try {
            URL url = new URL("http://pastebin.com/raw/x7PEVMEX");
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.toLowerCase().startsWith("true")) {
                    System.out.println("Proxy jest odblokowane");
                    scanner.close();
                    return true;
                }
            }
            scanner.close();
            System.out.println("Proxy jest zablokowane");
            return false;
        } catch (IOException ex) {
            Logger.getLogger(MCServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    @Override
    public void run() {
        /*if(!isAllowed()){
            return;
        }*/
		System.out.println("     /|");
		System.out.println("  |\\/ |/|");
		System.out.println(" /       \\   BHProxy Minecraft Server Proxy");
		System.out.println(" | _     |     written by TEAM BLAZINGHACK");
		System.out.println("  / ) (_/      jared - quake1337 - gabixdev");
		System.out.println("  `.    _/         Init.D - opl - TiREX");
		System.out.println("   L___|");
		System.out.println("");
		System.out.println("");
		System.out.println("");
    	
		MCServer.L.info("Uruchamianie BHProxy v" + Proxy.Version);
		
        MCServer.server = null;
        
        MCServer.L.info("Ladowanie konfigruacji...");
        Yaml yaml = new Yaml();
        
        Map<?, ?> conf;
        
        try {
			conf = (Map<?, ?>) yaml.load(new FileInputStream(new File("config.yml")));
		} catch (FileNotFoundException e1) {
			L.warning("Nie znaleziono konfiguracji!");
			e1.printStackTrace();
			return;
		}
        
        try {
        	System.setProperty("bhp.pass", conf.get("oppass").toString());
        } catch (Exception e) {
        	System.setProperty("bhp.pass", "blazinggowno666");
        	L.warning("Nie mozna zaladowac hasla!");
        } finally {
        	L.info("Haslo: " + System.getProperty("bhp.pass"));
        }
        
        
        try {
        	int port = Integer.parseInt(conf.get("port").toString());
            MCServer.server = new ServerSocket(port);
        }
        catch (IOException e2) {
        	MCServer.L.warning("Nie mozna podpiac sie pod port z pliku konfiguracyjnego...");
            try {
                MCServer.server = new ServerSocket(25565);
            }
            catch (IOException e3) {
                MCServer.L.warning("Nie mozna podpiac sie pod domyslny port, przydzielam losowy port...");
                try {
                	MCServer.server = new ServerSocket(0);
                } catch (IOException e4) {
                	L.severe("Nie mozna podpiac sie pod losowy port, wylaczanie proxy...");
                	return;	
                }
            }
        }
        MCServer.L.info(String.format("IP serwera: %s...", MCServer.server.getLocalSocketAddress()));
        while (MCServer.working) {
            try {
                MCServer.L.fine("Akceptowanie nowego polaczenia...");
                Socket s = MCServer.server.accept();
                if (!MCServer.working) {
                    s.close();
                }
                else {
                    ThreadGroup group = new ThreadGroup(s.getRemoteSocketAddress().toString());
                    new Player(group, s);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        MCServer.L.fine("Proxy zostalo zamkniete");
        if (MCServer.server != null && !MCServer.server.isClosed()) {
            try {
                System.exit(0);
                MCServer.server.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    static {
        L = Logger.getLogger("MCServer");
        MCServer.working = true;
        new MCServer();
    }
}
