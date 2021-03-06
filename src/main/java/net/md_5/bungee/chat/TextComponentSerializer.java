package net.md_5.bungee.chat;

import java.util.List;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonParseException;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import com.google.gson.JsonElement;
import com.google.gson.JsonDeserializer;
import net.md_5.bungee.api.chat.TextComponent;
import com.google.gson.JsonSerializer;

public class TextComponentSerializer extends BaseComponentSerializer implements JsonSerializer<TextComponent>, JsonDeserializer<TextComponent>
{
    @Override
    public TextComponent deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        TextComponent component = new TextComponent(new BaseComponent[0]);
        JsonObject object = json.getAsJsonObject();
        this.deserialize(object, component, context);
        component.setText(object.get("text").getAsString());
        return component;
    }
    
    @Override
    public JsonElement serialize(TextComponent src, Type typeOfSrc, JsonSerializationContext context) {
        List<BaseComponent> extra = src.getExtra();
        if (!src.hasFormatting() && (extra == null || extra.isEmpty())) {
            return new JsonPrimitive(src.getText());
        }
        JsonObject object = new JsonObject();
        this.serialize(object, src, context);
        object.addProperty("text", src.getText());
        return object;
    }
}
