package net.jared.pr0xy.mc.handshake;

import net.jared.pr0xy.mc.status.NetHandlerStatusClient;
import net.jared.pr0xy.mc.login.NetHandlerLoginClient;
import net.jared.pr0xy.mc.handshake.client.C00Handshake;
import net.jared.pr0xy.mc.server.Player;
import net.jared.pr0xy.mc.play.INetHandler;

public class NetHandlerHandshakeClient implements INetHandler
{
    private Player handler;
    
    public NetHandlerHandshakeClient(Player handler) {
        this.handler = handler;
    }
    
    public void handle(C00Handshake packet) {
        this.handler.protocolVersion = packet.protocolVersion;
        if (packet.nextState == 2) {
            this.handler.packetReceiver.netHandler = new NetHandlerLoginClient(this.handler);
        }
        else if (packet.nextState == 1) {
            this.handler.packetReceiver.netHandler = new NetHandlerStatusClient(this.handler);
        }
        else {
            this.handler.close();
        }
    }
    
    @Override
    public int getState() {
        return 0;
    }
}
