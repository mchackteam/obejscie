package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class ComponentBuilder
{
    private TextComponent current;
    private List<BaseComponent> parts;
    
    public ComponentBuilder(ComponentBuilder original) {
        this.parts = new ArrayList<BaseComponent>();
        this.current = new TextComponent(original.current);
        for (BaseComponent baseComponent : original.parts) {
            this.parts.add(baseComponent.duplicate());
        }
    }
    
    public ComponentBuilder(String text) {
        this.parts = new ArrayList<BaseComponent>();
        this.current = new TextComponent(text);
    }
    
    public ComponentBuilder append(String text) {
        return this.append(text, FormatRetention.ALL);
    }
    
    public ComponentBuilder append(String text, FormatRetention retention) {
        this.parts.add(this.current);
        (this.current = new TextComponent(this.current)).setText(text);
        this.retain(retention);
        return this;
    }
    
    public ComponentBuilder color(ChatColor color) {
        this.current.setColor(color);
        return this;
    }
    
    public ComponentBuilder bold(boolean bold) {
        this.current.setBold(bold);
        return this;
    }
    
    public ComponentBuilder italic(boolean italic) {
        this.current.setItalic(italic);
        return this;
    }
    
    public ComponentBuilder underlined(boolean underlined) {
        this.current.setUnderlined(underlined);
        return this;
    }
    
    public ComponentBuilder strikethrough(boolean strikethrough) {
        this.current.setStrikethrough(strikethrough);
        return this;
    }
    
    public ComponentBuilder obfuscated(boolean obfuscated) {
        this.current.setObfuscated(obfuscated);
        return this;
    }
    
    public ComponentBuilder insertion(String insertion) {
        this.current.setInsertion(insertion);
        return this;
    }
    
    public ComponentBuilder event(ClickEvent clickEvent) {
        this.current.setClickEvent(clickEvent);
        return this;
    }
    
    public ComponentBuilder event(HoverEvent hoverEvent) {
        this.current.setHoverEvent(hoverEvent);
        return this;
    }
    
    public ComponentBuilder reset() {
        return this.retain(FormatRetention.NONE);
    }
    
    public ComponentBuilder retain(FormatRetention retention) {
        BaseComponent previous = this.current;
        switch (retention) {
            case NONE: {
                this.current = new TextComponent(this.current.getText());
            }
            case EVENTS: {
                (this.current = new TextComponent(this.current.getText())).setInsertion(previous.getInsertion());
                this.current.setClickEvent(previous.getClickEvent());
                this.current.setHoverEvent(previous.getHoverEvent());
                break;
            }
            case FORMATTING: {
                this.current.setClickEvent(null);
                this.current.setHoverEvent(null);
                break;
            }
		default:
			break;
        }
        return this;
    }
    
    public BaseComponent[] create() {
        this.parts.add(this.current);
        return this.parts.toArray(new BaseComponent[this.parts.size()]);
    }
    
    public enum FormatRetention
    {
        NONE("NONE", 0), 
        FORMATTING("FORMATTING", 1), 
        EVENTS("EVENTS", 2), 
        ALL("ALL", 3);
        
        private FormatRetention(String s, int n) {
        }
    }
}
