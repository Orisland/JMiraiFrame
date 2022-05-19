package org.orisland;

import Tool.JsonTool;
import Tool.fileTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kotlinx.serialization.json.Json;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import org.orisland.wows.WowsApiConfig;
import org.orisland.wows.dataFactory;

import java.io.IOException;
import java.nio.file.Files;

import static org.orisland.wows.dataFactory.shipToday;
import static org.orisland.wows.dataFactory.findShipCompare;

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
    public void function(String uid) throws IOException {
//        findShipCompare(shipToday(uid, WowsApiConfig.Server.EU));

        dataFactory.compare(shipToday(uid, WowsApiConfig.Server.EU), JsonTool.mapper.readTree(Files.readString(dataFactory.getUidFile(uid).toPath())));


//        System.out.println(dataFactory.getUidFile(uid));

//        fileTool.saveFile(spider.getPlayerShipStats(WowsApiConfig.Server.EU, Long.parseLong("566316444")).getData(), String.valueOf(Plugin.INSTANCE.getDataFolderPath() + File.separator + "playerData"), "566316444.json");
//        log.info(Plugin.INSTANCE.getDataFolderPath() + File.separator + "playerData");
    }

    @SubCommand
    public void function1(){
        System.out.println(WowsApiConfig.dataDir);
    }
}
