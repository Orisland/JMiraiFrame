package org.orisland.wows;

import org.orisland.Plugin;

import java.io.File;

public class ApiConfig {
    //app id
//    https://developers.wargaming.net/applications/
    public static final String APPID = "21a433ec77510c77b44a3047937b4bb1";

    public static final String GET_PLAYER_ID = "https://api.worldofwarships.%s/wows/account/list/?application_id=%s&search=%s";

    public static final String GET_PLAYER_BASIC_INFO = "https://api.worldofwarships.%s/wows/account/info/?application_id=%s&account_id=%s";

    //给定船的id，查船名字   区服-appid-shipid-语言
    public static final String WOWS_getShips = "https://api.worldofwarships.%s/wows/encyclopedia/ships/?application_id=%s&ship_id=%s&language=%s";

    //获取服务器的数据
    public static final String WowsPr = "https://api.wows-numbers.com/personal/rating/expected/json/";

    //core
    //关键api，获取指定用户的所有船只战绩信息-> 区服-appid-uid
    public static final String GET_SHIP_INFO = "https://api.worldofwarships.%s/wows/ships/stats/?application_id=%s&account_id=%s";

    //窝窝屎的各种区服-> 区服
    public static final String WOWS_numbers= "https://%s.wows-numbers.com/player/";

    //窝窝屎最近战绩
    //查最近开的船战绩,wowsnumber渠道->区服-船种/单船-uid-date-类型
    public static final String WOWS_numbers_ships_types = "https://%s.wows-numbers.com/user/snapshot/%s.ajax?accountId=%s&date=%s&type=%s";

    //玩家数据地址
    public static final String dataDir = Plugin.INSTANCE.getDataFolder() + File.separator + "playerData" + File.separator;

    //    区服
    public static enum Server{
        EU, ASIA, NA, RU
    }

}
