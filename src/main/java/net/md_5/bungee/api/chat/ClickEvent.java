package net.md_5.bungee.api.chat;

public final class ClickEvent
{
    private Action action;
    private String value;
    
    public Action getAction() {
        return this.action;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }
    
    public enum Action
    {
        OPEN_URL("OPEN_URL", 0), 
        OPEN_FILE("OPEN_FILE", 1), 
        RUN_COMMAND("RUN_COMMAND", 2), 
        SUGGEST_COMMAND("SUGGEST_COMMAND", 3), 
        CHANGE_PAGE("CHANGE_PAGE", 4);
        
        private Action(String s, int n) {
        }
    }
}
