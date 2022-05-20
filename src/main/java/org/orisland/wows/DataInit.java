package org.orisland.wows;

import Tool.FileTool;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.yamlkt.YamlMap;

import static org.orisland.wows.ApiConfig.configDir;

@Slf4j
public class DataInit {
    public static void init(){
        initAppId();
    }

    /**
     * 获取配置文件中的appid
     */
    public static void initAppId(){
        YamlMap yamlMap = FileTool.ReadStringToYaml(FileTool.ReadDirToString(configDir + "config.yml"));
        String appid = yamlMap.getString("appid");
        if (!appid.equals("") && !appid.equals("null")){
            ApiConfig.APPID = appid;
            log.info("appid:{}初始化完成！",appid);
        }else {
            log.error("错误！appid为空,Wows plugin初始化失败！");
        }

    }
}
