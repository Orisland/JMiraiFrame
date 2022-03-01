package org.orisland.wows;

import Tool.JsonTool;
import Tool.fileTool;
import Tool.spider;
import Tool.timeTool;
import com.fasterxml.jackson.databind.JsonNode;
import kotlinx.serialization.json.Json;
import org.orisland.Plugin;
import org.orisland.wows.bean.playerShipStats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class dataFactory {
    public static playerDaily catchData(Long uid, WowsApiConfig.Server server) throws IOException {
        File[] files = fileTool.readDir(String.valueOf(Plugin.INSTANCE.getDataFolderPath()));
//        String json = null;
//        for (File file : files) {
//            if (file.toString().contains(String.valueOf(uid))){
//                json = Files.readString(Path.of(WowsApiConfig.dataDir));
//                break;
//            }
//        }
//        if (json == null)null
//        JsonNode playerData = JsonTool.mapper.readValue(json, )
        Long today = timeTool.getToday();
        playerShipStats playerShipStats = spider.getPlayerShipStats(server, uid);
        JsonNode playerData = playerShipStats.getData();
        for (JsonNode playerDatum : playerData) {
            if (playerDatum.get("last_battle_time").asLong() - today > 0){
                System.out.println(playerDatum.get("ship_id"));
            }
        }

        return null;
    }
}
