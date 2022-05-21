package org.orisland.wows.DataPack;

import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.PlayerObj;
import org.orisland.wows.doMain.SinglePlayer.SinglePlayer;

import static org.orisland.wows.DataPack.PlayerData.NickNameToAccountInfo;

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
