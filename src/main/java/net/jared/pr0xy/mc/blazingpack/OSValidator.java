package net.jared.pr0xy.mc.blazingpack;

import java.io.File;

public class OSValidator
{
    public static OS OSType;
    public static File WORKING_DIR;
    public static File VERSIONS;
    public static File BLAZINGPACK_1710;
    
    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            OSType = OS.WINDOWS;
        }
        else if (osName.contains("mac")) {
            OSType = OS.MACOS;
        }
        else if (osName.contains("solaris")) {
            OSType = OS.SOLARIS;
        }
        else if (osName.contains("sunos")) {
            OSType = OS.SOLARIS;
        }
        else if (osName.contains("linux")) {
            OSType = OS.LINUX;
        }
        else if (osName.contains("unix")) {
            OSType = OS.LINUX;
        }
        else {
            OSType = OS.UNKNOW;
        }
        String userHome = System.getProperty("user.home", ".");
        switch (OSValidator.OSType) {
            case LINUX:
            case SOLARIS: {
                WORKING_DIR = new File(userHome, ".minecraft/");
                break;
            }
            case WINDOWS: {
                String applicationData = System.getenv("APPDATA");
                if (applicationData != null) {
                    WORKING_DIR = new File(applicationData, ".minecraft/");
                    break;
                }
                WORKING_DIR = new File(userHome, ".minecraft/");
                break;
            }
            case MACOS: {
                WORKING_DIR = new File(userHome, "Library/Application Support/minecraft");
                break;
            }
            default: {
                WORKING_DIR = new File(userHome, "minecraft/");
                break;
            }
        }
        if (!OSValidator.WORKING_DIR.exists() && !OSValidator.WORKING_DIR.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + OSValidator.WORKING_DIR);
        }
        OSValidator.WORKING_DIR.mkdirs();
        (VERSIONS = new File(OSValidator.WORKING_DIR, "versions")).mkdirs();
        (BLAZINGPACK_1710 = new File(OSValidator.VERSIONS, "blazingpack_1.7.10")).mkdirs();
    }
    
    public enum OS
    {
        LINUX("LINUX", 0), 
        SOLARIS("SOLARIS", 1), 
        WINDOWS("WINDOWS", 2), 
        MACOS("MACOS", 3), 
        UNKNOW("UNKNOW", 4);
        
        private OS(String s, int n) {
        }
    }
}
