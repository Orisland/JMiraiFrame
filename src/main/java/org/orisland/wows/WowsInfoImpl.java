package org.orisland.wows;

import Tool.HttpClient;
import Tool.JsonTool;
import com.fasterxml.jackson.databind.JsonNode;
import org.orisland.wows.bean.id_infoSearchResult;
import org.orisland.wows.bean.name_idSearchResult;
import org.orisland.wows.bean.player;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @Author: zhaolong
 * @Time: 00:19
 * @Date: 2022年02月22日 00:19
 **/
public class WowsInfoImpl implements WowsInfos{
    /**
     * 根据船只id获取船的信息
     * @param shipid
     * @param flag
     * @return
     */
    public String getShipInfo(Long shipid, int flag) throws IOException {
        // get ship uid from WG
        String getUrl;
        if (flag == 0){
            getUrl = WowsApiConfig.GET_BOAT_BASIC_INFO
                    + "?application_id=" + WowsApiConfig.APPID
                    + "&ship_id=" + shipid;
        }
        else {
            getUrl = WowsApiConfig.GET_BOAT_BASIC_INFO_ASIA
                    + "?application_id=" + WowsApiConfig.APPID
                    + "&ship_id=" + shipid;
        }
        System.out.println(getUrl);
        JsonNode resp = HttpClient.getUrlByJson(getUrl);
        System.out.println(resp);
        String shipname = "某活动船";
        if (!(resp.get("data").toString().equals("{}"))){
            shipname = resp.get("data").get(String.valueOf(shipid)).get("name").asText();
        }


        String status = resp.get("status").asText();
        if(!"ok".equals(status)){
            System.out.println(status);
            System.out.println("wait what???");
            return "EOF";
        }

        return shipname;
    }

    /**
     * GET
     * 通过用户名获取用户id
     * wows所有用户相关的api都是通过uid调用的，所以这一步是基础的一步
     * template url : https://api.worldofwarships.eu/wows/account/list/?application_id=21a433ec77510c77b44a3047937b4bb1&search=lsahi
     * {
     *      "status": "ok",
     *      "meta": {
     *          "count": 1
     *      },
     *      "data": [
     *      {
     *             "nickname": "Orisland_Ex",
     *             "account_id": 566316444
     *      }
     *      ]
     * }
     */
    @Override
    public String getUserId(String username, int flag) throws IOException {
        System.out.println("开始调用username api");
        System.out.println(username);
        String getUrl;
        // get user uid from WG
        if (flag == 0){
            getUrl = WowsApiConfig.GET_PLAYER_ID
                    + "?application_id=" + WowsApiConfig.APPID
                    + "&search=" + username;
        }
        else {
            getUrl = WowsApiConfig.GRT_PLAYER_ID_ASIA
                    + "?application_id=" + WowsApiConfig.APPID
                    + "&search=" + username;

        }
        name_idSearchResult resp = JsonTool.mapper.readValue(HttpClient.getUrlByJson(getUrl).toString(), name_idSearchResult.class);

        if(!"ok".equals(resp.getStatus())){
            return "EOF";
        }

        JsonNode curUser = JsonTool.mapper.valueToTree(resp.getData().get(0));

        String id = curUser.get("account_id").asText();
        if (id.equals("") || id == null){
            return "EOF";
        }


        return id;
    }

    /**
     * GET
     * 通过uid获取用户基本信息(水表总表)
     * template url : https://api.worldofwarships.eu/wows/account/info/?application_id=21a433ec77510c77b44a3047937b4bb1&account_id=567190085
     *
     * 返回太麻烦了，去WG的api文档看吧
     * https://developers.wargaming.net/reference/eu/wows/account/info/
     */
    @Override
    public player getUserBasicInfo(String uid, int flag) throws IOException {
        System.out.println("开始调用userid api");
        String getUrl;
        System.out.println(uid);
        // get user info from WG
        if (flag==0){
            getUrl = WowsApiConfig.GET_PLAYER_BASIC_INFO
                    + "?application_id=" + WowsApiConfig.APPID
                    + "&account_id=" + uid;
        }else {
            getUrl = WowsApiConfig.GET_PLAYER_BASIC_INFO_ASIA
                    + "?application_id=" + WowsApiConfig.APPID
                    + "&account_id=" + uid;
        }

        id_infoSearchResult res = JsonTool.mapper.readValue(HttpClient.getUrlByJson(getUrl).toString(), id_infoSearchResult.class);
        JsonNode userinfo = res.getData().get(uid);
        System.out.println(userinfo);

        player player = new player();

        if (userinfo == null){
            return player;
        }

        String nickname = userinfo.get("nickname").asText();
        System.out.println(nickname);
        JsonNode pvpStatus = userinfo.get("statistics").get("pvp");

        Long battles = pvpStatus.get("battles").asLong();
        Long survivedBattles = pvpStatus.get("survived_battles").asLong();
        Long max_xp = pvpStatus.get("max_xp").asLong();
        double hit_rate1 = (pvpStatus.get("main_battery").get("hits").asDouble()) / pvpStatus.get("main_battery").get("shots").asDouble();
        BigDecimal hit_rate = new BigDecimal(Double.toString(hit_rate1));
        double torpedoes_rate1 = (pvpStatus.get("torpedoes").get("hits").asDouble()) / pvpStatus.get("torpedoes").get("shots").asDouble();
        BigDecimal torpedoes_rate = new BigDecimal(Double.toString(torpedoes_rate1));
        double sen_hit_rate1 = (pvpStatus.get("second_battery").get("hits").asDouble()) / pvpStatus.get("second_battery").get("shots").asDouble();
        BigDecimal sen_hit_rate = new BigDecimal(Double.toString(sen_hit_rate1));
        long ave_xp = pvpStatus.get("xp").asLong() / battles;
        double survived_rate1 = survivedBattles * 1.0 / battles;
        BigDecimal survived_rate = new BigDecimal(Double.toString(survived_rate1));
        Long wins = pvpStatus.get("wins").asLong();
        Long losses = pvpStatus.get("losses").asLong();
        Long survived_wins = pvpStatus.get("survived_wins").asLong();
        double win_rate1 = (wins *1.0) / battles;
        BigDecimal win_rate = new BigDecimal(Double.toString(win_rate1));
        Long damage = pvpStatus.get("damage_dealt").asLong();
        Long ave_damage = damage / battles;
        Long frags = pvpStatus.get("frags").asLong();
        Long die = battles - survivedBattles;

        Long torpedoes_max_frags_battle = pvpStatus.get("torpedoes").get("max_frags_battle").asLong();    //单场鱼雷击杀
        Long torpedoes_max_frags_ship_id = pvpStatus.get("torpedoes").get("max_frags_ship_id").asLong();    //单场鱼雷击杀id
        System.out.println(torpedoes_max_frags_ship_id);
        String name_torpedoes_max_frags_ship_id = getShipInfo(torpedoes_max_frags_ship_id,flag);                             //船名字

//        Long cv_max_frags_battle = pvpStatus.getJSONObject("aircraft").getLongValue("max_frags_battle");    //单场击杀最高
//        Long cv_max_frags_ship_id = pvpStatus.getJSONObject("aircraft").getLongValue("max_frags_ship_id");  //当场最高击杀船只
//        String name_cv_max_frags_ship_id = "你莫得cv~";
//        if (cv_max_frags_ship_id != 0){
//            name_cv_max_frags_ship_id = getShipInfo(cv_max_frags_ship_id,flag);                                   //船名字
//        }
        Long max_single_kill = pvpStatus.get("max_frags_battle").asLong();                                  //最大单杀
        Long max_frags_boat_id = pvpStatus.get("main_battery").get("max_frags_ship_id").asLong(); //最大击杀id
        String name_max_frags_boat_id = getShipInfo(max_frags_boat_id,flag);                                         //最大击杀船名
        //System.out.println(name_max_frags_boat_id);

        Long max_damage_dealt = pvpStatus.get("max_damage_dealt").asLong();                                 //最大伤害
        Long max_damage_boat_id = pvpStatus.get("max_damage_dealt_ship_id").asLong();                       //最大伤害id
        String name_max_damage_boat_id = getShipInfo(max_damage_boat_id,flag);                                       //最大伤害船名字
        //System.out.println(name_max_damage_boat_id);

        Long max_xp_ship_id = pvpStatus.get("max_xp_ship_id").asLong();
        String name_max_xp_ship_id = getShipInfo(max_xp_ship_id,flag);

        double KD1 = frags* 1.0 / die;
        BigDecimal KD = new BigDecimal(Double.toString(KD1));
        double win_servived_rate1 = survived_wins * 1.0 / wins;
        BigDecimal win_servived_rate = new BigDecimal(Double.toString(win_servived_rate1));


        //我踏马再用double我就是nt……
        BigDecimal ten = new BigDecimal(100.0);
        hit_rate = hit_rate.setScale(3, RoundingMode.HALF_UP).multiply(ten);
        hit_rate = hit_rate.setScale(1, RoundingMode.HALF_UP);
        sen_hit_rate = sen_hit_rate.setScale(3,RoundingMode.HALF_UP).multiply(ten);
        sen_hit_rate = sen_hit_rate.setScale(1,RoundingMode.HALF_UP);
        torpedoes_rate = torpedoes_rate.setScale(3,RoundingMode.HALF_UP).multiply(ten);
        torpedoes_rate = torpedoes_rate.setScale(1,RoundingMode.HALF_UP);
        BigDecimal survived_rate3 = survived_rate.setScale(3,RoundingMode.HALF_UP).multiply(ten);
        String survived_rate2 = survived_rate3.setScale(1,RoundingMode.HALF_UP).toString();

        KD = KD.setScale(1,RoundingMode.HALF_UP);
        win_rate = win_rate.setScale(3,RoundingMode.HALF_UP).multiply(ten);
        win_rate = win_rate.setScale(1,RoundingMode.HALF_UP);
        win_servived_rate = win_servived_rate.setScale(3,RoundingMode.HALF_UP).multiply(ten);
        win_servived_rate = win_servived_rate.setScale(1,RoundingMode.HALF_UP);

        player.max_xp_ship_id = max_xp_ship_id;
        player.name_max_xp_ship_id = name_max_xp_ship_id;
        player.user_id = Long.parseLong(uid);
        player.nickname =nickname;
        player.ave_damage =ave_damage;
        player.ave_xp =ave_xp;
        player.battles =battles;
//        player.cv_max_frags_battle = cv_max_frags_battle;
//        player.cv_max_frags_ship_id = cv_max_frags_ship_id;
        player.damage = damage;
        player.die = die;
        player.frags = frags;
        player.hit_rate = hit_rate.toString();
        player.KD = KD.toString();
        player.losses = losses;
        player.max_damage_boat_id = max_damage_boat_id;
        player.max_damage_dealt = max_damage_dealt;
        player.max_frags_boat_id = max_frags_boat_id;
        player.max_single_kill = max_single_kill;
        player.max_xp =max_xp;
//        player.name_cv_max_frags_ship_id = name_cv_max_frags_ship_id;
        player.name_max_damage_boat_id = name_max_damage_boat_id;
        player.name_max_frags_boat_id = name_max_frags_boat_id;
        player.name_torpedoes_max_frags_ship_id = name_torpedoes_max_frags_ship_id;
        player.sen_hit_rate = sen_hit_rate.toString();
        player.survived_rate = survived_rate2;
        player.survived_wins = survived_wins;
        player.survivedBattles = survivedBattles;
        player.torpedoes_max_frags_battle = torpedoes_max_frags_battle;
        player.torpedoes_max_frags_ship_id = torpedoes_max_frags_ship_id;
        player.torpedoes_rate = torpedoes_rate.toString();
        player.win_rate = win_rate.toString();
        player.win_servived_rate = win_servived_rate.toString();
        player.wins = wins;


//        String res1 =
//                "查询玩家信息" + nickname + "\n"
//                        + "总场数" + battles +"\n"
//                        + "存活场数" + survivedBattles + "\n"
//                        + "单场最大经验数"+max_xp + "\n"
//                        + "命中率"+hit_rate+"%" +"\n"
//                        + "单场最多击杀"+max_single_kill+"\n"
//                        + "副炮命中率"+sen_hit_rate + "%" + "\n"
//                        + "场均经验" + ave_xp +"\n"
//                        + "存活率" + survived_rate + "%" + "\n"
//                        + "KD" + KD +"\n"
//                        + "平均伤害" + ave_damage + "\n"
//                        + "平均经验" + ave_xp + "\n"
//                        + "胜率" + win_rate + "%" + "\n"
//                        + "胜利存活"+ survived_wins + "\n"
//                        + "胜利存活率" + win_servived_rate + "%"
//                        + "鱼雷精度" + torpedoes_rate + "%";

        return player;
    }
}
