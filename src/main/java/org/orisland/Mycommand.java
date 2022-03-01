package org.orisland;

import Tool.fileTool;
import Tool.spider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlinx.serialization.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.console.data.PluginData;
import net.mamoe.mirai.console.data.PluginDataHolder;
import net.mamoe.mirai.console.data.PluginDataStorage;
import net.mamoe.mirai.console.data.java.JAutoSavePluginData;
import org.jetbrains.annotations.NotNull;
import org.orisland.wows.WowsApiConfig;

import java.io.File;
import java.io.IOException;

import static org.orisland.wows.dataFactory.catchData;

/**
 * @Author: zhaolong
 * @Time: 00:05
 * @Date: 2021年07月30日 00:05
 **/
@Slf4j
public class Mycommand extends JCompositeCommand {
    public static final Mycommand INSTANCE = new Mycommand();
    public static final ObjectMapper mapper =  new ObjectMapper();

    private Mycommand(){
        super(Plugin.INSTANCE, "pic", new String[]{"p"}, Plugin.INSTANCE.getParentPermission());
    }

    @SubCommand("t")
    @Description("test")
    public void function(Long uid) throws IOException {
        catchData(Long.parseLong("566316446"), WowsApiConfig.Server.EU);


//        fileTool.saveFile(spider.getPlayerShipStats(WowsApiConfig.Server.EU, Long.parseLong("566316444")).getData(), String.valueOf(Plugin.INSTANCE.getDataFolderPath() + File.separator + "playerData"), "566316444.json");
//        fileTool.readDir(Plugin.INSTANCE.getDataFolderPath() + File.separator + "playerData" + File.separator);
//        log.info(Plugin.INSTANCE.getDataFolderPath() + File.separator + "playerData");
    }
}
