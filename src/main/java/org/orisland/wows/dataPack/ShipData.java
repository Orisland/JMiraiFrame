package org.orisland.wows.dataPack;

import Tool.FileTool;
import Tool.HttpClient;
import Tool.JsonTool;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.SingleShip;
import org.orisland.wows.doMain.singleShipData.SingleShipData;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        if (LocalShipInfo != null){
            JsonNode jsonNode = LocalShipInfo.get(shipId);
            singleShip = new SingleShip();
            singleShip.setType(jsonNode.get("type").asText());
            singleShip.setPremium(jsonNode.get("premium").asBoolean());
            singleShip.setName(jsonNode.get("name").asText());
            singleShip.setShipId(shipId);
            singleShip.setNation(jsonNode.get("nation").asText());
            singleShip.setTier(jsonNode.get("tier").asInt());
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
    public static List<SingleShipData> SearchAccountIdToShipInfo(String accountId, String shipId, Server server) {
        JsonNode urlByJson = HttpClient.getUrlByJson(String.format(ACCOUNT_SHIP, server, APPID, accountId, shipId, API_LANGUAGE));
        SingleShipData singleShipData = null;
        if (!urlByJson.get("status").asText().equals("ok")){
            log.error("api错误！");
            return null;
        }
        if (urlByJson.get("data").get(accountId) == null){
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
    public static List<SingleShipData> SearchAccountIdToShipInfo(String accountId, Server server){
        return SearchAccountIdToShipInfo(accountId, "", server);
    }

    /**
     * 根据船只id预期数据
     * @param shipId 船只id
     * @return 找到的船只数据
     */
    public static JsonNode ShipToExpected(String shipId){
        return ShipExpected.get("data").get(shipId);
    }


    /**
     * 单船的pr查询
     * @param ship 船只战斗数据
     * @return  处理后的pr结果
     */
    public static JsonNode ShipPr(JsonNode ship){
        String shipId = ship.get("shipId").asText();
        JsonNode shipExpected = ShipToExpected(shipId);
        long battle = ship.get("battle").asLong();
        double actualDmg = ship.get("Dmg").asDouble();
        double expectedDmg = shipExpected.get("average_damage_dealt").asDouble() * battle;
        double actualWins = ship.get("Wins").asDouble();
        double expectedWins = shipExpected.get("win_rate").asDouble() / 100 * battle;
        double actualFrags = ship.get("Frags").asDouble();
        double expectedFrags =  shipExpected.get("average_frags").asDouble() * battle;

        ShipPr shipPr = new ShipPr();
        shipPr.setActualFrags(actualFrags);
        shipPr.setExpectedFrags(expectedFrags);
        shipPr.setActualDmg(actualDmg);
        shipPr.setExpectedDmg(expectedDmg);
        shipPr.setActualWins(actualWins);
        shipPr.setExpectedWins(expectedWins);

        return PrStandard(shipPr.PrCalculate());
    }

    public static ShipDataObj shipInfoPack(JsonNode ship){

        return null;
    }

    /**
     * 由pr数据得知具体级别和颜色
     * @param pr pr
     * @return  打包数据
     */
    public static JsonNode PrInfo(BigDecimal pr){
        int score = Integer.parseInt(String.valueOf(pr));
        String evaluate;
        int distance;
        String color;
        if (score < 750){
            evaluate = "还需努力";
            distance = 750 - score;
            color = "番茄";
        }else if (score < 1100){
            evaluate = "低于平均";
            distance = 1100 - score;
            color = "玉米";
        }else if (score < 1350){
            evaluate = "平均水平";
            distance = 1350 - score;
            color = "蛋黄";
        }else if (score < 1550){
            evaluate = "好";
            distance = 1550 - score;
            color = "小葱";
        }else if (score < 1750){
            evaluate = "很好";
            distance = 1750 - score;
            color = "白菜";
        }else if (score < 2100){
            evaluate = "非常好";
            distance = 2100 - score;
            color = "青菜";
        }else if (score < 2450){
            evaluate = "大佬平均";
            distance = 2450 - score;
            color = "茄子";
        }else if (score < 5000){
            evaluate = "神佬平均";
            distance = score - 2450;
            color = "大茄子";
        } else if (score < 9999){
            evaluate = "……？您";
            distance = score - 5000;
            color = "茄子PlusMaxPro限量版";
        }else {
            log.warn("score异常！");
            evaluate = "未知";
            distance = 0;
            color = "灰";
        }

        ObjectNode objectNode = JsonTool.mapper.createObjectNode();
        objectNode.put("evaluate", evaluate);
        objectNode.put("distance",  "+" + String.valueOf(distance));
        objectNode.put("color", color);

        return objectNode;
    }

    /**
     * pr标准化并添加info数据
     * @param PR 需要标准化的pr
     * @return 标准化pr
     */
    public static JsonNode PrStandard(double PR){
        BigDecimal PrStandard = new BigDecimal(PR).setScale(0, RoundingMode.HALF_UP);
        JsonNode jsonNode = PrInfo(PrStandard);
        ObjectNode objectNode = JsonTool.mapper.createObjectNode();
        objectNode.put("pr", String.valueOf(PrStandard));
        objectNode.setAll((ObjectNode) jsonNode);
        return objectNode;
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
                count++;
            }
        }
        FileUtil.writeUtf8String(objectNode.toString(), "D:\\IdeaLib\\JMiraiFrame\\test.json");
        log.info("记录数:{}", count);
    }

    /**
     * 通过船只名称查找船只
     * @param shipName 船只名字
     * @return          查询结果
     */
    public static JsonNode ShipNameToShipId(String shipName){
//        精确匹配
        for (JsonNode jsonNode : LocalShipInfo) {
            if (jsonNode.get("name").asText().equals(shipName)){
                return jsonNode;
            }
        }
//        模糊匹配
        for (JsonNode jsonNode : LocalShipInfo) {
            if (jsonNode.get("name").asText().contains(shipName)){
                return jsonNode;
            }
        }
        log.warn("{}不存在！",shipName);
        return null;
    }
}
