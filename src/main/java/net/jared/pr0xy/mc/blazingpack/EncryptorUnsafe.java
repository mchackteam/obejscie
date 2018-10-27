package net.jared.pr0xy.mc.blazingpack;

import java.io.IOException;

import net.jared.pr0xy.mc.server.Player;
import java.net.Proxy;
import net.jared.pr0xy.mc.server.PlayerServer;

public class EncryptorUnsafe extends PlayerServer
{
    public EncryptorUnsafe(String hostname, int port, Proxy proxy, Player handler) {
        super(hostname, port, proxy, handler);
    }
    
    @Override
    public String getHostnameToHandshake() throws IOException {
        return Encryptor.encrypt(this, this.name, this.domain, this.port);
    }
}
