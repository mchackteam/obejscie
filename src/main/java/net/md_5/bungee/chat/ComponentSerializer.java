package net.md_5.bungee.chat;

import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import net.md_5.bungee.api.chat.TranslatableComponent;
import net.md_5.bungee.api.chat.TextComponent;
import java.lang.reflect.Type;
import com.google.gson.GsonBuilder;
import java.util.HashSet;
import com.google.gson.Gson;
import net.md_5.bungee.api.chat.BaseComponent;
import com.google.gson.JsonDeserializer;

public class ComponentSerializer implements JsonDeserializer<BaseComponent>
{
    private static Gson gson;
    public static ThreadLocal<HashSet<BaseComponent>> serializedComponents;
    
    static {
        gson = new GsonBuilder().registerTypeAdapter(BaseComponent.class, new ComponentSerializer()).registerTypeAdapter(TextComponent.class, new TextComponentSerializer()).registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer()).create();
        serializedComponents = new ThreadLocal<HashSet<BaseComponent>>();
    }
    
    public static BaseComponent[] parse(String json) {
        if (json.startsWith("[")) {
            return ComponentSerializer.gson.fromJson(json, BaseComponent[].class);
        }
        return new BaseComponent[] { ComponentSerializer.gson.fromJson(json, BaseComponent.class) };
    }
    
    public static String toString(BaseComponent component) {
        return "{\"text\":\"\", \"extra\": [" + ComponentSerializer.gson.toJson(component) + "]}";
    }
    
    public static String toString(BaseComponent... components) {
        return "{\"text\":\"\", \"extra\": [" + ComponentSerializer.gson.toJson(new TextComponent(components)) + "]}";
    }
    
    @Override
    public BaseComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonPrimitive()) {
            return new TextComponent(json.getAsString());
        }
        JsonObject object = json.getAsJsonObject();
        if (object.has("translate")) {
            return context.deserialize(json, TranslatableComponent.class);
        }
        return context.deserialize(json, TextComponent.class);
    }
}
