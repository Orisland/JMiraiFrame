package org.orisland.wows.dataPack;

import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.PlayerObj;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;

import static org.orisland.wows.dataPack.PlayerData.NickNameToAccountInfo;

/**
 * 数据集中处理打包
 */
public class DataHandler {
    public static PlayerObj searchByUserName(String nickName, ApiConfig.Server server){
        SinglePlayer singlePlayer = NickNameToAccountInfo(nickName, server);
        PlayerObj playerObj = new PlayerObj();

        return null;
    }


}
