package pl.blazinghack.proxy;

import java.io.IOException;
import net.jared.pr0xy.mc.server.MCServer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class Proxy extends Thread
{
    private static Logger L;
    public static Charset UTF_8;
	public static String Version = "1.0";
    
    public static void main(String[] args) throws IOException {
        Runtime.getRuntime().addShutdownHook(new Proxy());
        MCServer.load();
    }
    
    private Proxy() {
        super("StopThread");
    }
    
    @Override
    public void run() {
        Proxy.L.fine("Wylaczanie proxy...");
        MCServer.stopServer();
        Proxy.L.info("Zatrzymano!");
    }
    
    //test gitlaba
    
    static {
        L = Logger.getLogger("Start");
        UTF_8 = Charset.forName("UTF-8");
    }
}
