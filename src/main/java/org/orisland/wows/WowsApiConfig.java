package org.orisland.wows;

import org.orisland.Plugin;
import org.orisland.wows.bean.rankSingle;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class WowsApiConfig {

    //app id
    public static final String APPID = "21a433ec77510c77b44a3047937b4bb1";


    public static final String GET_PLAYER_ID = "https://api.worldofwarships.eu/wows/account/list/";

    public static final String GET_PLAYER_BASIC_INFO = "https://api.worldofwarships.eu/wows/account/info/";

    public static final String GET_BOAT_BASIC_INFO = "https://api.worldofwarships.eu/wows/encyclopedia/ships/";

    public static final String GRT_PLAYER_ID_ASIA = "https://api.worldofwarships.asia/wows/account/list/";

    public static final String GET_PLAYER_BASIC_INFO_ASIA = "https://api.worldofwarships.asia/wows/account/info/";

    public static final String GET_BOAT_BASIC_INFO_ASIA = "https://api.worldofwarships.asia/wows/encyclopedia/ships/";

    //关键api，获取指定用户的所有船只战绩信息-> 区服-appid-uid
    public static final String GET_SHIP_INFO = "https://api.worldofwarships.%s/wows/ships/stats/?application_id=%s&account_id=%s";

    //窝窝屎的各种区服-> 区服
    public static final String WOWS_numbers= "https://%s.wows-numbers.com/player/";

    //窝窝屎最近战绩
    //查最近开的船战绩,wowsnumber渠道->区服-船种/单船-uid-date-类型
    public static final String WOWS_numbers_ships_types = "https://%s.wows-numbers.com/user/snapshot/%s.ajax?accountId=%s&date=%s&type=%s";

    //玩家数据地址
    public static final String dataDir = Plugin.INSTANCE.getDataFolder() + File.separator + "playData" + File.separator;

    //仍需努力
    public static final rankSingle ImprovementNeed = new rankSingle("red", "仍需努力");

    //低于平均
    public static final rankSingle BelowAverage = new rankSingle("#FE7903", "低于平均");

    //平均水平
    public static final rankSingle Average = new rankSingle("#FFC71F", "平均水平");

    //好
    public static final rankSingle Good = new rankSingle( "#44B300", "好");

    //很好
    public static final rankSingle VeryGOOD = new rankSingle("#318000", "很好");

    //非常好
    public static final rankSingle Great = new rankSingle("#02C9B3", "非常好");

    //大佬平均
    public static final rankSingle Unicum = new rankSingle("#02C9B3", "大佬平均");

    //神佬平均
    public static final rankSingle SuperUnicum = new rankSingle("#A00DC5", "神佬平均");

    //筛选
    public static final rankSingle[] RankList = {BelowAverage, ImprovementNeed, Average, Good, VeryGOOD, Great, Unicum, SuperUnicum};

    //战绩层次
    public static enum Color{
        ImprovementNeed, BelowAverage, Average, Good, VeryGOOD, Great, Unicum, SuperUnicum
    }

//    区服
    public static enum Server{
        EU, ASIA, NA, RU
    }

    public static enum dataType{
        types, ships
    }


}
