package Tool;

import net.mamoe.yamlkt.YamlMap;

import static org.orisland.wows.ApiConfig.configDir;

public class YmlTool {
    /**
     * 读取指定key的yaml转为String
     * @param path 路径
     * @param key 键值
     * @return res
     */
    public static String ReadYamlToString(String path, String key){
        YamlMap yamlMap = FileTool.ReadStringToYaml(FileTool.ReadDirToString(path));
        return yamlMap.getString(key);
    }

    /**
     * 读取指定key的yaml转为Boolean
     * @param path 路径
     * @param key 键值
     * @return res
     */
    public static boolean ReadYamlToBoolean(String path, String key){
        YamlMap yamlMap = FileTool.ReadStringToYaml(FileTool.ReadDirToString(path));
        return Boolean.parseBoolean(yamlMap.getString(key));
    }
}
