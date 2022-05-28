package org.orisland.wows.dataPack;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.orisland.wows.ApiConfig.*;
import static org.orisland.wows.ApiConfig.dataDirNa;

@Slf4j
public class StringToMeaningful {

    /**
     * 通过船只名称查找船只
     * @param shipName 船只名字
     * @return          查询结果
     */
    public static JsonNode ShipNameToShipId(String shipName){
//        查找是否为纯英语
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(shipName);

        String ship = PinyinUtil.getPinyin(shipName, "");

//        包含汉语
        if (m.find()){
            //        精确匹配
            for (JsonNode jsonNode : LocalShipInfo) {
                if (jsonNode.get("zh").asText().equals(shipName)){
                    return jsonNode;
                }
            }
            log.info("汉语精确匹配失败!");
            //        精确匹配

            for (JsonNode jsonNode : LocalShipInfo) {
                if (jsonNode.get("zh").asText().contains(shipName)){
                    return jsonNode;
                }
            }
            log.info("汉语精确匹配失败!");

//        不包含汉语
        }else {
//        英语精确匹配
            for (JsonNode jsonNode : LocalShipInfo) {
                if (jsonNode.get("en").asText().equalsIgnoreCase(shipName)){
                    return jsonNode;
                }
            }
            log.info("英语精确匹配失败!");

//        英语模糊匹配
            for (JsonNode jsonNode : LocalShipInfo) {
                if (jsonNode.get("en").asText().toUpperCase().contains(shipName.toUpperCase())){
                    return jsonNode;
                }
            }
            log.info("英语模糊匹配失败!");
        }

        for (JsonNode jsonNode : LocalShipInfo) {
            if (jsonNode.get("py").asText().equals(ship)){
                return jsonNode;
            }
        }
        log.info("拼音精确匹配失败!");

        for (JsonNode jsonNode : LocalShipInfo) {
            if (jsonNode.get("py").asText().contains(ship)){
                return jsonNode;
            }
        }
        log.info("拼音模糊匹配失败!");

        log.warn("{}不存在！",shipName);
        return null;
    }

    /**
     * 根据区服选择保存地址
     * @param server    区服
     * @return          保存地址
     */
    public static String ServerToDir(ApiConfig.Server server){
        switch (server){
            case EU:
                return dataDirEu;
            case ASIA:
                return dataDirAsia;
            case RU:
                return dataDirRu;
            case NA:
            case com:
                return dataDirNa;
            default:
                log.error("出现了意料之外的数据！");
                return null;
        }
    }

    /**
     * 将字符串识别为区服信息
     * @param str   待转化信息
     * @return      枚举变量区服
     */
    public static Server StringToServer(String str){
        str = str.toUpperCase();
        switch (str){
            case "欧服":
            case "欧":
            case "EU":
                return Server.EU;
            case "亚服":
            case "亚":
            case "ASIA":
                return Server.ASIA;
            case "COM":
            case "NA":
            case "美服":
            case "美":
                return Server.com;
            case "俄服":
            case "俄":
            case "RU":
                return Server.RU;
            default:
                log.error("出现了预料之外的问题！");
                return null;
        }
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
     * 将字符串转化为不同的数据类型
     * @param str   字符串
     * @return      所需的type
     */
    public static Type StringToType(String str){
        str = str.toUpperCase();
        switch (str){
            case "SJ":
            case "随机":
            case "RANDOM":
                return Type.random;
            case "PW":
            case "排位":
            case "RANK":
                return Type.rank;
            case "ALL":
            case "NORMAL":
            case "全部":
            default:
                return Type.normal;
        }
    }
}
