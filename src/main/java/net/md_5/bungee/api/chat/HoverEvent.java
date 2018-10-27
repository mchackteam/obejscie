package net.md_5.bungee.api.chat;

public final class HoverEvent
{
    private Action action;
    private BaseComponent[] value;
    
    public Action getAction() {
        return this.action;
    }
    
    public BaseComponent[] getValue() {
        return this.value;
    }
    
    public HoverEvent(Action action, BaseComponent... value) {
        this.action = action;
        this.value = value;
    }
    
    public enum Action
    {
        SHOW_TEXT("SHOW_TEXT", 0), 
        SHOW_ACHIEVEMENT("SHOW_ACHIEVEMENT", 1), 
        SHOW_ITEM("SHOW_ITEM", 2), 
        SHOW_ENTITY("SHOW_ENTITY", 3);
        
        private Action(String s, int n) {
        }
    }
}
