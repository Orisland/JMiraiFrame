package org.orisland.wows;

import org.orisland.Plugin;

import java.io.File;

public class ApiConfig {
    /**
     * app id
     * url:<a href="https://developers.wargaming.net/applications/">申请AppId</a>
     */
    public static final String APPID = "21a433ec77510c77b44a3047937b4bb1";

    /**
     * 用昵称查用户
     * 区服-appid-昵称
     */
    public static final String GET_PLAYER_ID = "https://api.worldofwarships.%s/wows/account/list/?application_id=%s&search=%s";

    /**
     * 用id查用户(唯一)
     * 区服-appid-uid
     */
    public static final String GET_PLAYER_BASIC_INFO = "https://api.worldofwarships.%s/wows/account/info/?application_id=%s&account_id=%s";

    /**
     * 查询指定船的信息(唯一)
     * 区服-appid-shipid-语言
     */
    public static final String WOWS_getShips = "https://api.worldofwarships.%s/wows/encyclopedia/ships/?application_id=%s&ship_id=%s&language=%s";

    /**
     * wowsnumber的名片
     * uid
     */
    public static final String WOWS_CARD = "https://static.wows-numbers.com/wows/%s.png";

    /**
     * 船只期望数据
     * 获取用户pr和指定船的pr的数据源
     */
    public static final String WowsPr = "https://api.wows-numbers.com/personal/rating/expected/json/";

    /**
     * 按日期查询玩家数据
     * 获取指定日期的玩家数据,不指定日期为当前日期,没有就向上翻一个月
     * dates允许输入以逗号分割的日期YYYYMMDD,YYYYMMDD
     * 区服-appid-uid-dates
     */
    public static final String PLAYER_DATE = "https://api.worldofwarships.%s/wows/account/statsbydate/?application_id=%s&account_id=%s&dates=%s";

    /**
     *获取指定用户的所有船只战绩信息或指定船只信息
     * 区服-appid-uid-shipid
     */
    public static final String GET_SHIP_INFO = "https://api.worldofwarships.%s/wows/ships/stats/?application_id=%s&account_id=%s&ship_id=%s";

    /**
     * 本地玩家数据地址
     */
    public static final String dataDir = Plugin.INSTANCE.getDataFolder() + File.separator + "playerData" + File.separator;

    /**
     * 区服
     * 欧服，亚服，美服，俄服
     */
    public static enum Server{
        EU, ASIA, NA, RU
    }

    //=============================以下api暂未使用
    //窝窝屎的各种区服-> 区服
    public static final String WOWS_numbers= "https://%s.wows-numbers.com/player/";

    //窝窝屎最近战绩
    //查最近开的船战绩,wowsnumber渠道->区服-船种/单船-uid-date-类型
    public static final String WOWS_numbers_ships_types = "https://%s.wows-numbers.com/user/snapshot/%s.ajax?accountId=%s&date=%s&type=%s";

}
