package org.orisland.wows.dataPack;

import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;

import static org.orisland.wows.ApiConfig.*;
import static org.orisland.wows.ApiConfig.dataDirNa;

@Slf4j
public class StringToMeaningful {
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
     * 将字符串转化为不同的数据类型
     * @param str   字符串
     * @return      所需的type
     */
    public static Type StringToType(String str){
        str = str.toUpperCase();
        switch (str){
            case "RAN":
            case "随机":
            case "RANDOM":
                return Type.random;
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
