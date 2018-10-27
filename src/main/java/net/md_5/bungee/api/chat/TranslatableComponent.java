package net.md_5.bungee.api.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class TranslatableComponent extends BaseComponent
{
    private ResourceBundle locales;
    private Pattern format;
    private String translate;
    private List<BaseComponent> with;
    
    public TranslatableComponent() {
        this.locales = ResourceBundle.getBundle("mojang-translations/en_US");
        this.format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    }
    
    public String getTranslate() {
        return this.translate;
    }
    
    public void setTranslate(String translate) {
        this.translate = translate;
    }
    
    public List<BaseComponent> getWith() {
        return this.with;
    }
    
    public TranslatableComponent(TranslatableComponent original) {
        super(original);
        this.locales = ResourceBundle.getBundle("mojang-translations/en_US");
        this.format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
        this.setTranslate(original.getTranslate());
        if (original.getWith() != null) {
            List<BaseComponent> temp = new ArrayList<BaseComponent>();
            for (BaseComponent baseComponent : original.getWith()) {
                temp.add(baseComponent.duplicate());
            }
            this.setWith(temp);
        }
    }
    
    public TranslatableComponent(String translate, Object... with) {
        this.locales = ResourceBundle.getBundle("mojang-translations/en_US");
        this.format = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
        this.setTranslate(translate);
        List<BaseComponent> temp = new ArrayList<BaseComponent>();
        for (Object w : with) {
            if (w instanceof String) {
                temp.add(new TextComponent((String)w));
            }
            else {
                temp.add((BaseComponent)w);
            }
        }
        this.setWith(temp);
    }
    
    @Override
    public BaseComponent duplicate() {
        return new TranslatableComponent(this);
    }
    
    public void setWith(List<BaseComponent> components) {
        for (BaseComponent component : components) {
            component.parent = this;
        }
        this.with = components;
    }
    
    public void addWith(String text) {
        this.addWith(new TextComponent(text));
    }
    
    public void addWith(BaseComponent component) {
        if (this.with == null) {
            this.with = new ArrayList<BaseComponent>();
        }
        component.parent = this;
        this.with.add(component);
    }
    
    protected void toPlainText(StringBuilder builder) {
        String trans;
        try {
            trans = this.locales.getString(this.translate);
        }
        catch (MissingResourceException e) {
            trans = this.translate;
        }
        Matcher matcher = this.format.matcher(trans);
        int position = 0;
        int i = 0;
        while (matcher.find(position)) {
            int pos = matcher.start();
            if (pos != position) {
                builder.append(trans.substring(position, pos));
            }
            position = matcher.end();
            String formatCode = matcher.group(2);
            switch (formatCode.charAt(0)) {
                default: {
                    continue;
                }
                case 'd':
                case 's': {
                    String withIndex = matcher.group(1);
                    this.with.get((withIndex != null) ? (Integer.parseInt(withIndex) - 1) : i++).toPlainText(builder);
                    continue;
                }
                case '%': {
                    builder.append('%');
                    continue;
                }
            }
        }
        if (trans.length() != position) {
            builder.append(trans.substring(position, trans.length()));
        }
        super.toPlainText(builder);
    }
    
    protected void toLegacyText(StringBuilder builder) {
        String trans;
        try {
            trans = this.locales.getString(this.translate);
        }
        catch (MissingResourceException e) {
            trans = this.translate;
        }
        Matcher matcher = this.format.matcher(trans);
        int position = 0;
        int i = 0;
        while (matcher.find(position)) {
            int pos = matcher.start();
            if (pos != position) {
                this.addFormat(builder);
                builder.append(trans.substring(position, pos));
            }
            position = matcher.end();
            String formatCode = matcher.group(2);
            switch (formatCode.charAt(0)) {
                default: {
                    continue;
                }
                case 'd':
                case 's': {
                    String withIndex = matcher.group(1);
                    this.with.get((withIndex != null) ? (Integer.parseInt(withIndex) - 1) : i++).toLegacyText(builder);
                    continue;
                }
                case '%': {
                    this.addFormat(builder);
                    builder.append('%');
                    continue;
                }
            }
        }
        if (trans.length() != position) {
            this.addFormat(builder);
            builder.append(trans.substring(position, trans.length()));
        }
        super.toLegacyText(builder);
    }
    
    private void addFormat(StringBuilder builder) {
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
    }
}
