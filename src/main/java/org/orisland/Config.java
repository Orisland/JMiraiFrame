package org.orisland;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

@Slf4j
public class Config {

    public static String DataDir = Plugin.INSTANCE.getDataFolder() + File.separator;
    public static String ConfigDir = Plugin.INSTANCE.getConfigFolder() + File.separator;

    public static String JeffContent = DataDir + "jeff.txt";
    public static String JeffConfig = ConfigDir + "config.yml";

    public static String SplitChar = "%s";

    public static List<String> JeffJoke = null;

    public static String BlackList = null;

    public static String FollewList = null;
}
