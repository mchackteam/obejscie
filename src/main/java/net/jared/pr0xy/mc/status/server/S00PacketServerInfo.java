package net.jared.pr0xy.mc.status.server;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.md_5.bungee.chat.TranslatableComponentSerializer;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.chat.TextComponentSerializer;
import net.md_5.bungee.api.chat.TextComponent;
import java.lang.reflect.Type;
import net.md_5.bungee.chat.ComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import com.google.gson.GsonBuilder;
import net.jared.pr0xy.mc.streams.PacketOutputStream;
import java.io.IOException;
import net.jared.pr0xy.mc.streams.PacketInputStream;
import net.jared.pr0xy.mc.play.INetHandler;
import com.google.gson.Gson;
import net.jared.pr0xy.mc.packets.Packet;

public class S00PacketServerInfo extends Packet
{
    private static Gson GSON;
    public ServerStatusResponse response;
    
    public S00PacketServerInfo() {
    }
    
    public S00PacketServerInfo(ServerStatusResponse response) {
        this.response = response;
    }
    
    @Override
    public void process(INetHandler handler) {
    }
    
    @Override
    public void read(PacketInputStream pis) throws IOException {
        this.response = S00PacketServerInfo.GSON.fromJson(pis.readString(32767), ServerStatusResponse.class);
    }
    
    @Override
    public void write(PacketOutputStream pos) throws IOException {
        pos.writeVarInt(0);
        pos.writeString(S00PacketServerInfo.GSON.toJson(this.response));
    }
    
    static {
        GSON = new GsonBuilder().registerTypeAdapter(BaseComponent.class, new ComponentSerializer()).registerTypeAdapter(TextComponent.class, new TextComponentSerializer()).registerTypeAdapter(ServerStatusResponse.class, new ServerStatusResponse.Serializer()).registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer()).create();
    }
    
    public static class GameProfile
    {
        public String id;
        public String name;
        
        public GameProfile() {
        }
        
        public GameProfile(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
    
    public static class MinecraftProtocolVersionIdentifier
    {
        public String name;
        public int protocol;
        
        public MinecraftProtocolVersionIdentifier() {
        }
        
        public MinecraftProtocolVersionIdentifier(String name, int protocol) {
            this.name = name;
            this.protocol = protocol;
        }
    }
    
    public static class PlayerCountData
    {
        public int max;
        public int online;
        public GameProfile[] sample;
    }
    
    public static class ServerStatusResponse
    {
        public BaseComponent description;
        public PlayerCountData players;
        public MinecraftProtocolVersionIdentifier version;
        public String favicon;
        
        public static class Serializer implements JsonSerializer<ServerStatusResponse>
        {
            @Override
            public JsonElement serialize(ServerStatusResponse src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject obj = new JsonObject();
                if (src.description != null) {
                    obj.add("description", context.serialize(src.description));
                }
                if (src.players != null) {
                    obj.add("players", context.serialize(src.players));
                }
                if (src.version != null) {
                    obj.add("version", context.serialize(src.version));
                }
                if (src.favicon != null) {
                    obj.add("favicon", context.serialize(src.favicon));
                }
                return obj;
            }
        }
    }
}
