package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public abstract class BaseComponent
{
    BaseComponent parent;
    private ChatColor color;
    private Boolean bold;
    private Boolean italic;
    private Boolean underlined;
    private Boolean strikethrough;
    private Boolean obfuscated;
    private String insertion;
    private List<BaseComponent> extra;
    private ClickEvent clickEvent;
    private HoverEvent hoverEvent;
    
    public Boolean getBold() {
        return this.bold;
    }
    
    public void setBold(Boolean bold) {
        this.bold = bold;
    }
    
    public Boolean getItalic() {
        return this.italic;
    }
    
    public void setItalic(Boolean italic) {
        this.italic = italic;
    }
    
    public Boolean getUnderlined() {
        return this.underlined;
    }
    
    public void setUnderlined(Boolean underlined) {
        this.underlined = underlined;
    }
    
    public Boolean getStrikethrough() {
        return this.strikethrough;
    }
    
    public void setStrikethrough(Boolean strikethrough) {
        this.strikethrough = strikethrough;
    }
    
    public Boolean getObfuscated() {
        return this.obfuscated;
    }
    
    public void setObfuscated(Boolean obfuscated) {
        this.obfuscated = obfuscated;
    }
    
    public String getInsertion() {
        return this.insertion;
    }
    
    public void setInsertion(String insertion) {
        this.insertion = insertion;
    }
    
    public ClickEvent getClickEvent() {
        return this.clickEvent;
    }
    
    public void setClickEvent(ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
    }
    
    public HoverEvent getHoverEvent() {
        return this.hoverEvent;
    }
    
    public void setHoverEvent(HoverEvent hoverEvent) {
        this.hoverEvent = hoverEvent;
    }
    
    public List<BaseComponent> getExtra() {
        return this.extra;
    }
    
    public void setColor(ChatColor color) {
        this.color = color;
    }
    
    BaseComponent() {
    }
    
    BaseComponent(BaseComponent old) {
        this.setColor(old.getColorRaw());
        this.setBold(old.isBoldRaw());
        this.setItalic(old.isItalicRaw());
        this.setUnderlined(old.isUnderlinedRaw());
        this.setStrikethrough(old.isStrikethroughRaw());
        this.setObfuscated(old.isObfuscatedRaw());
        this.setInsertion(old.getInsertion());
        this.setClickEvent(old.getClickEvent());
        this.setHoverEvent(old.getHoverEvent());
        if (old.getExtra() != null) {
            for (BaseComponent component : old.getExtra()) {
                this.addExtra(component.duplicate());
            }
        }
    }
    
    public abstract BaseComponent duplicate();
    
    public static String toLegacyText(BaseComponent... components) {
        StringBuilder builder = new StringBuilder();
        for (BaseComponent msg : components) {
            builder.append(msg.toLegacyText());
        }
        return builder.toString();
    }
    
    public static String toPlainText(BaseComponent... components) {
        StringBuilder builder = new StringBuilder();
        for (BaseComponent msg : components) {
            builder.append(msg.toPlainText());
        }
        return builder.toString();
    }
    
    public ChatColor getColor() {
        if (this.color != null) {
            return this.color;
        }
        if (this.parent == null) {
            return ChatColor.WHITE;
        }
        return this.parent.getColor();
    }
    
    public ChatColor getColorRaw() {
        return this.color;
    }
    
    public boolean isBold() {
        if (this.bold == null) {
            return this.parent != null && this.parent.isBold();
        }
        return this.bold;
    }
    
    public Boolean isBoldRaw() {
        return this.bold;
    }
    
    public boolean isItalic() {
        if (this.italic == null) {
            return this.parent != null && this.parent.isItalic();
        }
        return this.italic;
    }
    
    public Boolean isItalicRaw() {
        return this.italic;
    }
    
    public boolean isUnderlined() {
        if (this.underlined == null) {
            return this.parent != null && this.parent.isUnderlined();
        }
        return this.underlined;
    }
    
    public Boolean isUnderlinedRaw() {
        return this.underlined;
    }
    
    public boolean isStrikethrough() {
        if (this.strikethrough == null) {
            return this.parent != null && this.parent.isStrikethrough();
        }
        return this.strikethrough;
    }
    
    public Boolean isStrikethroughRaw() {
        return this.strikethrough;
    }
    
    public boolean isObfuscated() {
        if (this.obfuscated == null) {
            return this.parent != null && this.parent.isObfuscated();
        }
        return this.obfuscated;
    }
    
    public Boolean isObfuscatedRaw() {
        return this.obfuscated;
    }
    
    public void setExtra(List<BaseComponent> components) {
        for (BaseComponent component : components) {
            component.parent = this;
        }
        this.extra = components;
    }
    
    public void addExtra(String text) {
        this.addExtra(new TextComponent(text));
    }
    
    public void addExtra(BaseComponent component) {
        if (this.extra == null) {
            this.extra = new ArrayList<BaseComponent>();
        }
        component.parent = this;
        this.extra.add(component);
    }
    
    public boolean hasFormatting() {
        return this.color != null || this.bold != null || this.italic != null || this.underlined != null || this.strikethrough != null || this.obfuscated != null || this.hoverEvent != null || this.clickEvent != null;
    }
    
    public String toPlainText() {
        StringBuilder builder = new StringBuilder();
        this.toPlainText(builder);
        return builder.toString();
    }
    
    void toPlainText(StringBuilder builder) {
        if (this.extra != null) {
            for (BaseComponent e : this.extra) {
                e.toPlainText(builder);
            }
        }
    }
    
    public String toLegacyText() {
        StringBuilder builder = new StringBuilder();
        this.toLegacyText(builder);
        return builder.toString();
    }
    
    void toLegacyText(StringBuilder builder) {
        if (this.extra != null) {
            for (BaseComponent e : this.extra) {
                e.toLegacyText(builder);
            }
        }
    }
}
