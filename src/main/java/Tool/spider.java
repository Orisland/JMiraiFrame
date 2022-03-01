package Tool;

import Tool.HttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import kotlinx.serialization.json.Json;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.orisland.wows.WowsApiConfig;
import org.orisland.wows.WowsApiConfig.*;
import org.orisland.wows.bean.playerShipStats;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static org.orisland.wows.WowsApiConfig.*;

/**
 * @Author: zhaolong
 * @Time: 23:54
 * @Date: 2022年02月25日 23:54
 **/
@Log4j2
public class spider {
//    网页的select地址
    public final static String Battles = "";
    public final static String winRate = "";

    /**
     * 直接访问该用户的全部信息
     * 这个方法一般直接是访问wowsnumber的用户主页的
     * @param server
     * @param uid
     * @param userName
     * @return
     * @throws IOException
     */
    public static Document getInfo(Server server, Long uid, String userName) throws IOException {
        return Jsoup.parse(Objects.requireNonNull(HttpClient.getUrlByString(String.format(WOWS_numbers, server) + uid + "," + userName)));
    }

    /**
     * 用于获取某个用户的近期战绩，使用的是wowsnumber渠道
     * @param server
     * @param uid
     * @param dataType
     * @param date
     * @param type
     * @return
     * @throws IOException
     */
    public static Document StatsByType(Server server, String uid, dataType dataType, String date, String type) throws IOException {
        String content = HttpClient.getUrlByString(String.format(WowsApiConfig.WOWS_numbers_ships_types, server, dataType, uid, date, type));
        if (content.equals("")){
            return null;
        }else {
            return Jsoup.parse(content);
        }
    }

    /**
     * 用于获取uid的所有船只的数据信息，用来进行统计和加减
     * @param server    区服
     * @param uid       玩家id
     * @return          返回数据
     * @throws IOException
     */
    public static playerShipStats getPlayerShipStats(Server server, Long uid) throws IOException {
        playerShipStats pss = JsonTool.mapper.readValue(HttpClient.getUrlByString(String.format(GET_SHIP_INFO, server, APPID, uid)), playerShipStats.class);
        if (pss.getData().size() == 0){
            log.info("uid不存在，请检查uid:"+uid);
            return null;
        }else {
            log.info(uid + "数据获取完成");
            return pss;
        }
    }
}
