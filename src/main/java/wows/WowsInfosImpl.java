package wows;

import org.orisland.wows.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import org.orisland.wows.WowsApiConfig;
import org.orisland.wows.WowsInfos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class WowsInfosImpl implements WowsInfos {
    @Override
    public String getShipInfo(Long shipid, int flag) {

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
        JSONObject resp = JSONObject.parseObject(HttpUtil.Get(getUrl,""));
        String shipname = "某活动船";
        if (!(resp.getJSONObject("data").toJSONString().equals("{}"))){
            shipname = resp.getJSONObject("data").getJSONObject(String.valueOf(shipid)).getString("name");
        }


        String status = resp.getString("status");
        if(!"ok".equals(status)){
            System.out.println(status);
            System.out.println("wait what???");
            return "EOF";
        }

        return shipname;
    }

    /**
     *
     * @param args
     * 测试方法
     * 个人是传参数打断点看看回参就ok了
     *
     */
    public static void main(String[] args){

        WowsInfosImpl tst = new WowsInfosImpl();

        String msg = "查水表 Orisland_EX";
        String[] msgSplit = msg.split( " ");

        //String uid = tst.getUserId(msgSplit[1]);
        //String res = tst.getUserBasicInfo(uid);

        //System.out.println(res);
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
     *             "nickname": "lsahi",
     *             "account_id": 567190085
     *      }
     *      ]
     * }
     */
    @Override
    public String getUserId(String username, int flag) {
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
        JSONObject resp = JSONObject.parseObject(HttpUtil.Get(getUrl,""));

        List<Object> data = resp.getJSONArray("data");
        String status = resp.getString("status");


        if(!"ok".equals(status)){
            return "EOF";
        }

        JSONObject curUser = JSONObject.parseObject(data.get(0).toString());
        System.out.println(curUser);

        String name = curUser.getString("nickname");
        String id = curUser.getString("account_id");
        System.out.println(name);
        System.out.println(id);
        System.out.println(username);
        if (id.equals("") || id == null){
            return "EOF";
        }

//        if(!name.equals(username)){
//            return "EOF";
//        }

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
    public player getUserBasicInfo(String uid, int flag) {
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
        JSONObject resp = JSONObject.parseObject(HttpUtil.Get(getUrl,""));
        System.out.println(resp);

        JSONObject userinfo = resp.getJSONObject("data").getJSONObject(uid);
        if (userinfo == null){
            return new player();
        }
        System.out.println("test");

        String nickname = userinfo.getString("nickname");
        System.out.println(nickname);
        JSONObject pvpStatus = userinfo.getJSONObject("statistics").getJSONObject("pvp");

        Long battles = pvpStatus.getLongValue("battles");
        Long survivedBattles = pvpStatus.getLongValue("survived_battles");
        Long max_xp = pvpStatus.getLongValue("max_xp");
        double hit_rate1 = (1.0 * pvpStatus.getJSONObject("main_battery").getLongValue("hits")) / pvpStatus.getJSONObject("main_battery").getLongValue("shots");
        BigDecimal hit_rate = new BigDecimal(Double.toString(hit_rate1));
        double torpedoes_rate1 = (1.0 * pvpStatus.getJSONObject("torpedoes").getLongValue("hits")) / pvpStatus.getJSONObject("torpedoes").getLongValue("shots");
        BigDecimal torpedoes_rate = new BigDecimal(Double.toString(torpedoes_rate1));
        double sen_hit_rate1 = (1.0 * pvpStatus.getJSONObject("second_battery").getLongValue("hits")) / pvpStatus.getJSONObject("second_battery").getLongValue("shots");
        BigDecimal sen_hit_rate = new BigDecimal(Double.toString(sen_hit_rate1));
        long ave_xp = pvpStatus.getLongValue("xp") / battles;
        double survived_rate1 = survivedBattles * 1.0 / battles;
        BigDecimal survived_rate = new BigDecimal(Double.toString(survived_rate1));
        Long wins = pvpStatus.getLongValue("wins");
        Long losses = pvpStatus.getLongValue("losses");
        Long survived_wins = pvpStatus.getLongValue("survived_wins");
        double win_rate1 = (wins *1.0) / battles;
        BigDecimal win_rate = new BigDecimal(Double.toString(win_rate1));
        Long damage = pvpStatus.getLongValue("damage_dealt");
        Long ave_damage = damage / battles;
        Long frags = pvpStatus.getLongValue("frags");
        Long die = battles - survivedBattles;

        Long torpedoes_max_frags_battle = pvpStatus.getJSONObject("torpedoes").getLongValue("max_frags_battle");    //单场鱼雷击杀
        Long torpedoes_max_frags_ship_id = pvpStatus.getJSONObject("torpedoes").getLongValue("max_frags_ship_id");    //单场鱼雷击杀id
        String name_torpedoes_max_frags_ship_id = getShipInfo(torpedoes_max_frags_ship_id,flag);                             //船名字

//        Long cv_max_frags_battle = pvpStatus.getJSONObject("aircraft").getLongValue("max_frags_battle");    //单场击杀最高
//        Long cv_max_frags_ship_id = pvpStatus.getJSONObject("aircraft").getLongValue("max_frags_ship_id");  //当场最高击杀船只
//        String name_cv_max_frags_ship_id = "你莫得cv~";
//        if (cv_max_frags_ship_id != 0){
//            name_cv_max_frags_ship_id = getShipInfo(cv_max_frags_ship_id,flag);                                   //船名字
//        }
        Long max_single_kill = pvpStatus.getLongValue("max_frags_battle");                                  //最大单杀
        Long max_frags_boat_id = pvpStatus.getJSONObject("main_battery").getLongValue("max_frags_ship_id"); //最大击杀id
        String name_max_frags_boat_id = getShipInfo(max_frags_boat_id,flag);                                         //最大击杀船名
        //System.out.println(name_max_frags_boat_id);

        Long max_damage_dealt = pvpStatus.getLongValue("max_damage_dealt");                                 //最大伤害
        Long max_damage_boat_id = pvpStatus.getLongValue("max_damage_dealt_ship_id");                       //最大伤害id
        String name_max_damage_boat_id = getShipInfo(max_damage_boat_id,flag);                                       //最大伤害船名字
        //System.out.println(name_max_damage_boat_id);

        Long max_xp_ship_id = pvpStatus.getLongValue("max_xp_ship_id");
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
        player player = new player();
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


        String res =
                "查询玩家信息" + nickname + "\n"
                        + "总场数" + battles +"\n"
                        + "存活场数" + survivedBattles + "\n"
                        + "单场最大经验数"+max_xp + "\n"
                        + "命中率"+hit_rate+"%" +"\n"
                        + "单场最多击杀"+max_single_kill+"\n"
                        + "副炮命中率"+sen_hit_rate + "%" + "\n"
                        + "场均经验" + ave_xp +"\n"
                        + "存活率" + survived_rate + "%" + "\n"
                        + "KD" + KD +"\n"
                        + "平均伤害" + ave_damage + "\n"
                        + "平均经验" + ave_xp + "\n"
                        + "胜率" + win_rate + "%" + "\n"
                        + "胜利存活"+ survived_wins + "\n"
                        + "胜利存活率" + win_servived_rate + "%"
                        + "鱼雷精度" + torpedoes_rate + "%";

        return player;
    }

}
