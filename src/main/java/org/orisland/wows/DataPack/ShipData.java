package org.orisland.wows.DataPack;

import Tool.FileTool;
import Tool.HttpClient;
import Tool.JsonTool;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.doMain.SingleShip;
import org.orisland.wows.doMain.SingleShipData.SingleShipData;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.orisland.wows.ApiConfig.*;

@Slf4j
public class ShipData {

    /**
     * 将ShipId信息获取为Ship实例
     * ship实例为精简数据
     * @param shipId 船只id
     * @return  查询结果
     */
    public static SingleShip SearchShipIdToShipInfo(String shipId){
        SingleShip singleShip = null;
        if (LOCAL_SHIP_INFO != null){
            JsonNode jsonNode = LOCAL_SHIP_INFO.get(shipId);
            singleShip = new SingleShip();
            singleShip.setType(jsonNode.get("type").asText());
            singleShip.setPremium(jsonNode.get("premium").asBoolean());
            singleShip.setName(jsonNode.get("name").asText());
            singleShip.setShipId(shipId);
            singleShip.setNation(jsonNode.get("nation").asText());
        }else {
            JsonNode urlByJson = HttpClient.getUrlByJson(String.format(SHIPID_SHIPIINFO, Server.EU, APPID, shipId, API_LANGUAGE));
            if (!urlByJson.get("status").asText().equals("ok")){
                log.info("api错误！");
                return null;
            }
            JsonNode data = urlByJson.get("data").get(shipId);
            singleShip = new SingleShip();
            singleShip.setShipId(shipId);
            singleShip.setImages(data.get("images"));
            singleShip.setName(data.get("name").asText());
            singleShip.setNation(data.get("nation").asText());
            singleShip.setPremium(data.get("is_premium").asBoolean());
            singleShip.setTier(data.get("tier").asInt());
            singleShip.setType(data.get("type").asText());
            singleShip.setSpecial(data.get("is_special").asBoolean());
        }

        return singleShip;
    }

    /**
     * 获取该用户指定船只信息
     * @param accountId 用户id
     * @param shipId 船只id
     * @return 查询结果列表
     */
    public static List<SingleShipData> SearchAccountIdToShipInfo(String accountId, String shipId) {
        JsonNode urlByJson = HttpClient.getUrlByJson(String.format(ACCOUNT_SHIP, Server.EU, APPID, accountId, shipId, API_LANGUAGE));
        SingleShipData singleShipData = null;
        if (!urlByJson.get("status").asText().equals("ok")){
            log.error("api错误！");
            return null;
        }

        ArrayNode data = JsonTool.mapper.valueToTree(urlByJson.get("data").get(accountId));
        List<SingleShipData> dataList = new ArrayList<>();
        try {
            for (JsonNode datum : data) {
                singleShipData = JsonTool.mapper.readValue(datum.toString(), SingleShipData.class);
                dataList.add(singleShipData);
            }
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return dataList;
    }

    /**
     * 获取该用户开过的所有船只信息
     * @param accountId 用户id
     */
    public static void SearchAccountIdToShipInfo(String accountId){
        SearchAccountIdToShipInfo(accountId, "");
    }




    /**
     *打包全部船只信息
     * 维护信息适用，插件不涉及
     * @param path
     */
    public static void readShips(String path) throws IOException {
        File[] files = FileTool.readDir(path);
        int count =0;
        ObjectNode objectNode = JsonTool.mapper.createObjectNode();
        for (File file : files) {
            String s = FileUtil.readString(file, StandardCharsets.UTF_8);
            JsonNode jsonNode = JsonTool.mapper.readTree(s);
            if (!jsonNode.get("status").asText().equals("ok")){
                log.error("{}错误!",file.getAbsolutePath());
                return;
            }
            JsonNode data = jsonNode.get("data");
            for (JsonNode datum : data) {
                SingleShip singleShip = new SingleShip();
                singleShip.setShipId(datum.get("ship_id").asText());
//                singleShip.setImages(datum.get("images"));
                singleShip.setName(datum.get("name").asText());
                singleShip.setNation(datum.get("nation").asText());
                singleShip.setPremium(datum.get("is_premium").asBoolean());
                singleShip.setTier(datum.get("tier").asInt());
                singleShip.setType(datum.get("type").asText());
                singleShip.setSpecial(datum.get("is_special").asBoolean());
                objectNode.set(singleShip.getShipId(), JsonTool.mapper.readTree(JsonTool.mapper.writeValueAsString(singleShip)));
                System.out.println(JsonTool.mapper.writeValueAsString(singleShip));
                count++;
            }
        }
        FileUtil.writeUtf8String(objectNode.toString(), "D:\\IdeaLib\\JMiraiFrame\\test.json");
        System.out.println("记录数:"+count);
    }
}
