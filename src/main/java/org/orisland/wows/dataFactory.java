package org.orisland.wows;

import Tool.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import kotlinx.serialization.json.Json;
import org.jsoup.internal.StringUtil;
import org.orisland.Plugin;
import org.orisland.wows.bean.playerShipStats;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.orisland.wows.WowsApiConfig.WowsPr;

public class dataFactory {
    /**
     * 获取当天玩家玩过的船，并吧数据封装进JSON进行下一步数据处理
     * 这里顺便进行储存查询的数据，以待后期调用
     * @param uid
     * @param server
     * @return
     * @throws IOException
     */
    public static ObjectNode shipToday(String uid, WowsApiConfig.Server server) throws IOException {
        Long today = timeTool.getToday();
        playerShipStats playerShipStats = spider.getPlayerShipStats(server, uid);
        JsonNode playerData = playerShipStats.getData();

        //将查到的数据存入本地的json
        String[] YMD = timeTool.getYMD();
        fileTool.saveFile(playerData, WowsApiConfig.dataDir, uid + "," +YMD[3] + ".json");


        ObjectNode shipToday = JsonTool.mapper.createObjectNode();
        int count = 0;
        for (JsonNode playerDatum : playerData) {
            if (playerDatum.get("last_battle_time").asLong() - today > 0){
                count++;
                shipToday.set(String.valueOf(playerDatum.get("ship_id")), playerDatum);
                String url = String.format(WowsApiConfig.WOWS_getShips, WowsApiConfig.Server.EU, WowsApiConfig.APPID, playerDatum.get("ship_id"), "zh-cn");
                System.out.println(HttpClient.getUrlByJson(url).get("data").get(String.valueOf(playerDatum.get("ship_id"))).get("name"));
            }
        }
        shipToday.put("count", count);
        shipToday.put("uid", uid);
        System.out.println("数据获取完毕");
        if (count == 0)
            System.out.println("今日战绩为空");

        System.out.println(shipToday);
        return shipToday;
    }

    /**
     * 比较两个json之间的不同点，算出pr以及其他数据
     *
     * Pr计算公式，来源于wowsnumber
     * @ https://wows-numbers.com/personal/rating
     * rDmg = actualDmg/expectedDmg
     * rWins = actualWins/expectedWins
     * rFrags = actualFrags/expectedFrags
     *
     * nDmg = max(0, (rDmg - 0.4) / (1 - 0.4))
     * nFrags = max(0, (rFrags - 0.1) / (1 - 0.1))
     * nWins = max(0, (rWins - 0.7) / (1 - 0.7))
     *
     * PR =  700*nDMG + 300*nFrags + 150*nWins
     *
     * @param shipData  传入玩家当日的开船类别和信息
     * @throws IOException
     */
    public static void findShipCompare(JsonNode shipData) throws IOException {
        if (shipData.get("count").asLong() == 0){
            System.out.println("玩家今天没有开船啊，退出查询");
            return;
        }

        System.out.println(shipData);

        String[] YMD = timeTool.getYMD();
        String uid = shipData.get("uid").asText();
        File[] files = fileTool.readDir(WowsApiConfig.dataDir);
        String json = null;
        JsonNode jsonNode = null;
        for (File file : files) {
            if (file.toString().contains(uid)){
                json = Files.readString(file.toPath());
                break;
            }
        }

        JsonNode shipsPr = getWowsShipPr();

        if (json != null){
            jsonNode = JsonTool.mapper.readTree(json);
        }
    }

    /**
     * 获取wowsnumber的pr数据
     * @return
     * @throws IOException
     */
    public static JsonNode getWowsShipPr() throws IOException {
        return HttpClient.getUrlByJson(WowsPr);
    }

    /**
     * 获取包含某个uid的文件，后续进行处理
     * 若查出的结果==3或>=3，则删除最旧的版本，留相对较新的版本
     * 类似于版本控制，暂时不考虑多天查找，先完成 今日战绩 的排序删除操作.
     * TODO:在后续，可能会考虑提高数据版本备份，以支持多天的战绩查询
     * 考虑一种例外情况，玩家在第一天查询了数据后，第二天打了船，但是第二天没有查询，那么这里保存的仍然是上一次的战绩，那么当玩家再次查询的时候
     * 就会出现，当天的数据减去很久之前数据的情况，出现大量船的现象。如果出文字的话，有可能会直接boom，qq不允许一次性发送过多的图片，img的必要性。
     * @param uid
     * @return  返回次新的
     */
    public static File getUidFile(String uid){
        List<File> uidFiles = new ArrayList<>();
        File[] files = fileTool.readDir(WowsApiConfig.dataDir);
        for (File file : files) {
            if (file.toString().contains(uid)){
                uidFiles.add(file);
            }
        }
        if (uidFiles.size() >= 3){
            System.out.println("执行删除过早版本操作");
            fileTool.delteFile(sort(files));
        }
        System.out.println(uidFiles.get(1));
        System.out.println(uidFiles.get(0));
        System.out.println(uidFiles.size() == 2 ? uidFiles.get(1) : uidFiles.get(0));
//        return uidFiles.size() == 2 ? uidFiles.get(1) : uidFiles.get(0);
        return uidFiles.get(0);
    }

    /**
     * getUidFile()专用方法，用于删除最旧的版本数据
     * @param files
     * @return
     */
    public static String sort(File[] files){
        for(int i=0; i< files.length-1; i++){
            for (int j=0; j< files.length-i-1; j++){
                if (Long.parseLong(files[i].toString().split(",")[1].split(".json")[0])
                        < Long.parseLong(files[i+ 1].toString().split(",")[1].split(".json")[0])){
                    File temp = files[i];
                    files[i] = files[i + 1];
                    files[i + 1] = temp;
                }
            }
        }
        return files[files.length-1].toString();
    }

    /**
     * 核心方法，比较当天的结果和之前的结果的
     * @param newone
     * @param oldone
     * @return
     */
    public static JsonNode compare(JsonNode newone, JsonNode oldone){
        Iterator<Map.Entry<String, JsonNode>> fields = newone.fields();
        List<String> keyList = new ArrayList<>();
        while (fields.hasNext()){
            String one = fields.next().getKey();
            if (StringUtil.isNumeric(one))
                keyList.add(one);
        }

        ObjectNode result = JsonTool.mapper.createObjectNode();
        ObjectNode single = null;

        key:for (String s : keyList) {
            for (JsonNode jsonNode : oldone) {
                if (jsonNode.get("ship_id").asText().equals(s)){
                    single = JsonTool.mapper.createObjectNode();
                    single.put("battle",newone.get(s).get("battles").asLong() - jsonNode.get("battles").asLong());
                    single.put("xp", newone.get(s).get("pvp").get("xp").asLong() - jsonNode.get("pvp").get("xp").asLong());
                    single.put("wins", newone.get(s).get("pvp").get("wins").asLong() - jsonNode.get("pvp").get("wins").asLong());
                    single.put("losses", newone.get(s).get("pvp").get("losses").asLong() - jsonNode.get("pvp").get("losses").asLong());
                    single.put("damage_dealt", newone.get(s).get("pvp").get("damage_dealt").asLong() - jsonNode.get("pvp").get("damage_dealt").asLong());
                    single.put("shots", newone.get(s).get("pvp").get("main_battery").get("shots").asLong() - jsonNode.get("pvp").get("main_battery").get("shots").asLong());
                    single.put("hits", newone.get(s).get("pvp").get("main_battery").get("hits").asLong() - jsonNode.get("pvp").get("main_battery").get("hits").asLong());
                    if (single.get("battle").asLong() == 0) //若出现数据异常的情况要跳出避免异常
                        continue key;
                    result.set(s,single);
                    continue key;
                }
            }
//            //若出现了刚买的船则直接添加不减
            single = JsonTool.mapper.createObjectNode();
            single.put("battle",newone.get(s).get("battles").asLong());
            single.put("xp", newone.get(s).get("pvp").get("xp").asLong());
            single.put("wins", newone.get(s).get("pvp").get("wins").asLong());
            single.put("losses", newone.get(s).get("pvp").get("losses").asLong());
            single.put("damage_dealt", newone.get(s).get("pvp").get("damage_dealt").asLong());
            single.put("shots", newone.get(s).get("pvp").get("main_battery").get("shots").asLong());
            single.put("hits", newone.get(s).get("pvp").get("main_battery").get("hits").asLong());
            if (single.get("battle").asLong() <= 0)
                continue;
            result.set(s,single);
        }

        System.out.println(result);

        return null;
    }
}
