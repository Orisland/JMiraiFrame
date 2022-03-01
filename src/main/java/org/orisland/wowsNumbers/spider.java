package org.orisland.wowsNumbers;

import Tool.HttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.orisland.wows.WowsApiConfig;
import org.orisland.wows.WowsApiConfig.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

import static org.orisland.wows.WowsApiConfig.WOWS_numbers;

/**
 * @Author: zhaolong
 * @Time: 23:54
 * @Date: 2022年02月25日 23:54
 **/
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




}
