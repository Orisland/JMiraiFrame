package org.orisland.wows.dataPack;

import Tool.FileTool;
import Tool.HttpClient;
import Tool.JsonTool;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.orisland.wows.doMain.SingleShip;
import org.orisland.wows.doMain.Tag;
import org.orisland.wows.doMain.singleShipData.SingleShipData;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
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
            JsonNode urlByJson = HttpClient.getUrlByJson(String.format(SHIPID_SHIPIINFO, Server.EU, APPID, shipId, API_LANGUAGE, 1));
            if (!urlByJson.get("status").asText().equals("ok")){
                log.info("api错误！");
                return null;
            }
            JsonNode data = urlByJson.get("data").get(shipId);
            singleShip = new SingleShip();
            singleShip.setShipId(shipId);
            singleShipPack(singleShip, data);
        }
        return singleShip;
    }

    /**
     * 获取wg数据库内的所有船只数据
     * @return  打包数据
     */
    public static JsonNode AllShipInfo(){
        JsonNode urlByJson = HttpClient.getUrlByJson(String.format(SHIPID_SHIPIINFO, Server.EU, APPID, "", API_LANGUAGE, 1));
        int pages = urlByJson.get("meta").get("page_total").asInt();
        int total = urlByJson.get("meta").get("total").asInt();

        ObjectNode shipList = JsonTool.mapper.createObjectNode();
        ObjectNode temp;

        for (int i = 1; i <= pages; i++){
            urlByJson = HttpClient.getUrlByJson(String.format(SHIPID_SHIPIINFO, Server.EU, APPID, "", API_LANGUAGE, i));
            if (urlByJson.get("status").asText().equals("ok")){
                JsonNode content = urlByJson.get("data");
                for (JsonNode jsonNode : content) {
                    temp = JsonTool.mapper.createObjectNode();
                    temp.put("ship_id_str", jsonNode.get("ship_id_str").asText());
                    temp.put("ship_id", jsonNode.get("ship_id").asText());
                    temp.put("nation", jsonNode.get("nation").asText());
                    temp.put("tier", jsonNode.get("tier").asText());
                    temp.put("type", jsonNode.get("type").asText());
                    shipList.set(temp.get("ship_id").asText(), temp);
                }
                log.info("完成{}/{}", shipList.size(), total);
            }else {
                log.warn("page:{} api发生错误！", i);
            }
        }
        return shipList;
    }

    /**
     * 获取浩舰数据库的船只语言信息
     * 不知道这种获取方式能活多久呢
     * 浩舰数据库：
     * <a href="https://iwarship.net/wowsdb/index">...</a>
     * @return  json数据
     */
    public static JsonNode IWarShipShipName(){
        JsonNode shipName = null;

        int count = 0;
        while (count <= reTry){
            try {
                Document document = Jsoup.connect("https://iwarship.net/wowsdb/index").get();
                Elements script = document.getElementsByTag("script");
                for (Element element : script) {
                    for (String var : element.data().split("var")) {
                        if (var.length() < 8)
                            continue;
                        if (var.startsWith(" shipName")){
                            String s = var.split("JSON.parse\\(")[1].split("\\);")[0];
                            String regex = "\\\\\"";
                            String s1 = s.replaceAll(regex, "\"");
                            String json = s1.substring(1, s1.length() - 1);
                            shipName = JsonTool.mapper.readTree(json);
                            return shipName;
                        }
                    }
                }
            }catch (Exception e){
                log.error("第{}次发生错误！", ++count);
                e.printStackTrace();
            }
        }

        log.warn("出现了意料之外的浩舰船只名称解析问题！");
        return null;
    }

    /**
     * wg api数据与浩舰数据混合
     */
    public static JsonNode ShipDataMix(){
        JsonNode wgApi = AllShipInfo();
        JsonNode iWarShip = IWarShipShipName();

        String date = DateUtil.format(new Date(), "YYYYMMdd");

        ObjectNode shipList = JsonTool.mapper.createObjectNode();
        shipList.put("date", date);

        ObjectNode data = JsonTool.mapper.createObjectNode();

        ObjectNode temp = null;

        for (JsonNode wgShip : wgApi) {
            JsonNode iShip = iWarShip.get(wgShip.get("ship_id_str").asText());
            if (iShip == null){
                log.info("发现了浩舰未储存的船只！{}", wgShip.get("ship_id_str").asText());
                continue;
            }

            temp = JsonTool.mapper.createObjectNode();

            String shipIdStr = wgShip.get("ship_id_str").asText();
            String shipId = wgShip.get("ship_id").asText();

//            浩舰数据
            String zh = iShip.get("zh").asText();
            String en = iShip.get("en").asText();
            String py = iShip.get("py").asText();
            Tag tag = TagDecoding(iShip.get("tag").asText());

//            wg数据
            String wgType = wgShip.get("type").asText();
            String wgTier = wgShip.get("tier").asText();

            String wgNation = WgToNation(wgShip.get("nation").asText());

            temp.put("shipId", shipId);
            temp.put("shipIdStr", shipIdStr);

            temp.put("zh", zh.trim().replaceAll("\\s", ""));
            temp.put("en", en);
            temp.put("py", py.trim().replaceAll("\\s", ""));

//            优先浩舰数据
            if (tag == null){
                temp.put("type", wgType);
                temp.put("tier", wgTier);
                temp.put("nation", wgNation);
            }else {
                temp.put("type", tag.getType());
                temp.put("tier", tag.getTier());
                temp.put("nation", tag.getNation());
            }
            data.set(shipId, temp);
        }

        shipList.set("data", data);

        return shipList;
    }

    /**
     * 将船只数据保存到本地
     * @return          flag
     */
    public static boolean saveShipInfo(){
        log.info("获取船只语言数据开始!");
        JsonNode shipDataMix = ShipDataMix();
        FileUtil.writeUtf8String(shipDataMix.toString(), dataDir + "ships_cn.json");
        log.info("船只数据获取完成!");
        return true;
    }

    public static Tag TagDecoding(String tagString){
        if (tagString == null){
            log.warn("tag为空！");
            return null;
        }

        String nation;
        String tier;
        String type;

        char[] chars = tagString.toCharArray();
        Tag tag = new Tag();
        if (tagString.length() == 3){
            char nationChar = chars[0];
            char tierChar = chars[1];
            char typeChar = chars[2];

            nation = iWarShipToNation(nationChar);
            tier = String.valueOf(tierChar);
            type = charToType(typeChar);

        }else if (tagString.length() == 4){
            char nationChar = chars[0];
            char tierChar1 = chars[1];
            char tierChar2 = chars[2];
            char typeChar = chars[3];

            nation = iWarShipToNation(nationChar);
            tier = String.valueOf(tierChar1) + tierChar2;
            type = charToType(typeChar);

        }else {
            log.warn("{}出现了未知的错误!", tag);
            return null;
        }

        tag.setNation(nation);
        tag.setTier(tier);
        tag.setType(type);
        return tag;
    }

    /**
     * 将char转化为国家，若不匹配则返回unknown
     * @param charStr   字符串
     * @return          国家
     */
    public static String iWarShipToNation(char charStr){
        switch (charStr){
            case 'E':
                return "英联邦";
            case 'W':
                return "欧洲";
            case 'F':
                return "法国";
            case 'D':
                return "德国";
            case 'I':
                return "意大利";
            case 'R':
                return "日本";
            case 'H':
                return "荷兰";
            case 'V':
                return "泛美";
            case 'C':
                return "泛亚";
            case 'S':
//                    这里西班牙也是S，需要在后续的处理单独区分
                return "苏联";
            case 'M':
                return "美国";
            case 'Y':
                return "英国";
            default:
                log.warn("{}出现了未知char!", charStr);
                return "Unknown";
        }
    }

    /**
     * 将wg的国籍数据转化为
     * @return
     */
    public static String WgToNation(String string){
        switch (string){
            case "commonwealth":
                return "英联邦";
            case "europe":
                return "欧洲";
            case "pan_america":
                return "泛美";
            case "france":
                return "法国";
            case "usa":
                return "美国";
            case "germany":
                return "德国";
            case "italy":
                return "意大利";
            case "uk":
                return "应该";
            case "japan":
                return "日本";
            case "netherlands":
                return "荷兰";
            case "pan_asia":
                return "泛亚";
            case "ussr":
                return "苏联";
            case "spain":
                return "西班牙";
            default:
                log.warn("出现了意料之外的国籍");
                return "unknown";

        }
    }

    /**
     * 将char转化为不同的舰种
     * @param charStr   字符
     * @return
     */
    public static String charToType(char charStr){
        switch (charStr){
            case 'A':
                return "航母";
            case 'B':
                return "战列";
            case 'D':
                return "驱逐";
            case 'C':
                return "巡洋";
            case 'S':
                return "潜艇";
            default:
                log.warn("{}出现了未知的char!", charStr);
                return "Unknown";
        }
    }

    /**
     * singleShip数据打包
     * @param singleShip
     * @param data
     */
    private static void singleShipPack(SingleShip singleShip, JsonNode data) {
        singleShip.setName(data.get("name").asText());
        singleShip.setNation(data.get("nation").asText());
        singleShip.setPremium(data.get("is_premium").asBoolean());
        singleShip.setTier(data.get("tier").asInt());
        singleShip.setType(data.get("type").asText());
        singleShip.setSpecial(data.get("is_special").asBoolean());
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

        JsonNode data = JsonTool.mapper.valueToTree(urlByJson.get("data").get(accountId));
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
        JsonNode data = ShipExpected.get("data").get(shipId);
        if (data.size() == 0){
            return null;
        }else
            return data;
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
                singleShipPack(singleShip, datum);
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
        log.info("汉语精确匹配失败!");

//        模糊匹配
        for (JsonNode jsonNode : LocalShipInfo) {
            if (jsonNode.get("name").asText().contains(shipName)){
                return jsonNode;
            }
        }
        log.info("汉语模糊匹配失败!");

//        英语精确匹配
        for (JsonNode jsonNode : LocalShipInfo) {
            if (jsonNode.get("enname").asText().equalsIgnoreCase(shipName)){
                return jsonNode;
            }
        }
        log.info("英语精确匹配失败!");

//        英语模糊匹配
        for (JsonNode jsonNode : LocalShipInfo) {
            if (jsonNode.get("enname").asText().toUpperCase().contains(shipName.toUpperCase())){
                return jsonNode;
            }
        }
        log.info("英语模糊匹配失败!");
        log.warn("{}不存在！",shipName);
        return null;
    }
}
