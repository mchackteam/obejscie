package net.jared.pr0xy.mc.utils;

import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;

public class SRVResolver
{
    private static DirContext srvContext;
    private String newDomain;
    private String exception;
    private String exceptionWhileParsing;
    private int newPort;
    private String srv;
    
    public SRVResolver(String domain, String type) {
        String[] parts = new String[0];
        try {
            Attributes a = SRVResolver.srvContext.getAttributes("_" + type + "._tcp." + domain, new String[] { "SRV" });
            Attribute attr;
            Object obj;
            if (a != null && (attr = a.get("srv")) != null && (obj = attr.get(0)) != null) {
                this.srv = obj.toString();
                parts = this.srv.split(" ");
            }
        }
        catch (NamingException e) {
            this.exception = e.toString();
        }
        try {
            this.newPort = Integer.parseInt(parts[2]);
            this.newDomain = parts[3];
        }
        catch (Exception e2) {
            this.newPort = -1;
            this.newDomain = null;
            this.exceptionWhileParsing = e2.toString();
        }
    }
    
    public String getSRV() {
        return this.srv;
    }
    
    public String getException() {
        return this.exception;
    }
    
    public String getExceptionWhileParsing() {
        return this.exceptionWhileParsing;
    }
    
    public String getDomain() {
        return this.newDomain;
    }
    
    public int getPort() {
        return this.newPort;
    }
    
    static {
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        try {
            srvContext = new InitialDirContext(env);
        }
        catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
}
