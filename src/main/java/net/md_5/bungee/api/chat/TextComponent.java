package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class TextComponent extends BaseComponent
{
    private static Pattern url;
    private String text;
    
    static {
        url = Pattern.compile("^(?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*)?$");
    }
    
    TextComponent() {
    }
    
    public TextComponent(String text) {
        this.text = text;
    }
    
    @SuppressWarnings("incomplete-switch")
	public static BaseComponent[] fromLegacyText(String message) {
        ArrayList<BaseComponent> components = new ArrayList<BaseComponent>();
        StringBuilder builder = new StringBuilder();
        TextComponent component = new TextComponent();
        Matcher matcher = TextComponent.url.matcher(message);
        for (int i = 0; i < message.length(); ++i) {
            char c = message.charAt(i);
            if (c == '§') {
                ++i;
                c = message.charAt(i);
                if (c >= 'A' && c <= 'Z') {
                    c += ' ';
                }
                ChatColor format = ChatColor.getByChar(c);
                if (format != null) {
                    if (builder.length() > 0) {
                        TextComponent old = component;
                        component = new TextComponent(old);
                        old.setText(builder.toString());
                        builder = new StringBuilder();
                        components.add(old);
                    }
                    switch (format) {
                        case BOLD: {
                            component.setBold(true);
                            continue;
                        }
                        case ITALIC: {
                            component.setItalic(true);
                            continue;
                        }
                        case UNDERLINE: {
                            component.setUnderlined(true);
                            continue;
                        }
                        case STRIKETHROUGH: {
                            component.setStrikethrough(true);
                            continue;
                        }
                        case MAGIC: {
                            component.setObfuscated(true);
                            continue;
                        }
                        case RESET: {
                            format = ChatColor.WHITE;
                            break;
                        }
                    }
                    component = new TextComponent();
                    component.setColor(format);
                }
            }
            else {
                int pos = message.indexOf(32, i);
                if (pos == -1) {
                    pos = message.length();
                }
                if (matcher.region(i, pos).find()) {
                    if (builder.length() > 0) {
                        TextComponent old = component;
                        component = new TextComponent(old);
                        old.setText(builder.toString());
                        builder = new StringBuilder();
                        components.add(old);
                    }
                    TextComponent old = component;
                    component = new TextComponent(old);
                    String urlString = message.substring(i, pos);
                    component.setText(urlString);
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, urlString.startsWith("http") ? urlString : ("http://" + urlString)));
                    components.add(component);
                    i += pos - i - 1;
                    component = old;
                }
                else {
                    builder.append(c);
                }
            }
        }
        if (builder.length() > 0) {
            component.setText(builder.toString());
            components.add(component);
        }
        if (components.isEmpty()) {
            components.add(new TextComponent(""));
        }
        return components.toArray(new BaseComponent[components.size()]);
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public TextComponent(TextComponent textComponent) {
        super(textComponent);
        this.setText(textComponent.getText());
    }
    
    public TextComponent(BaseComponent... extras) {
        this.setText("");
        this.setExtra(new ArrayList<BaseComponent>(Arrays.asList(extras)));
    }
    
    @Override
    public BaseComponent duplicate() {
        return new TextComponent(this);
    }
    
    protected void toPlainText(StringBuilder builder) {
        builder.append(this.text);
        super.toPlainText(builder);
    }
    
    protected void toLegacyText(StringBuilder builder) {
        builder.append(this.getColor());
        if (this.isBold()) {
            builder.append(ChatColor.BOLD);
        }
        if (this.isItalic()) {
            builder.append(ChatColor.ITALIC);
        }
        if (this.isUnderlined()) {
            builder.append(ChatColor.UNDERLINE);
        }
        if (this.isStrikethrough()) {
            builder.append(ChatColor.STRIKETHROUGH);
        }
        if (this.isObfuscated()) {
            builder.append(ChatColor.MAGIC);
        }
        builder.append(this.text);
        super.toLegacyText(builder);
    }
    
    @Override
    public String toString() {
        return String.format("TextComponent{text=%s, %s}", this.text, super.toString());
    }
}
