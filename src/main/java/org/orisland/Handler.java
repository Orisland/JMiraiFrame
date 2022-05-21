package org.orisland;

import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import net.mamoe.mirai.event.events.MessageEvent;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.DataPack.ShipData;
import org.orisland.wows.doMain.SingleShipDataSimple;


import java.io.IOException;
import java.util.List;

import static org.orisland.wows.DataPack.PlayerData.NickNameToAccountInfo;
import static org.orisland.wows.DataPack.PlayerData.searchNickNameToAccountId;
import static org.orisland.wows.DataPack.PlayerData.SearchAccountIdToAccountInfoByDate;
import static org.orisland.wows.DataPack.ShipData.SearchAccountIdToShipInfo;

/**
 * @Author: zhaolong
 * @Time: 3:07 下午
 * @Date: 2021年07月03日 15:07
 **/
public class Handler extends SimpleListenerHost {
    @EventHandler
    public ListeningStatus friendListener(FriendMessageEvent event) throws IOException {

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus groupListener(GroupMessageEvent event) throws IOException {
        if (event.getMessage().contentToString().contains("1")){
            NickNameToAccountInfo("Orisland_Ex", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().contains("2")){
            searchNickNameToAccountId("Orisland_Ex", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().contains("3")){
//            ShipData.SearchShipIdToShipInfo("4277090288");
            SearchAccountIdToShipInfo("566316444", "4277090288");
        }else if (event.getMessage().contentToString().contains("4")){
            SearchAccountIdToShipInfo("566316444");
        }else if (event.getMessage().contentToString().contains("5")){
            List<SingleShipDataSimple> singleShipDataSimples = SearchAccountIdToAccountInfoByDate("566316444", ApiConfig.Server.EU, "20220518,20220517");
            for (SingleShipDataSimple singleShipDataSimple : singleShipDataSimples) {
                System.out.println(singleShipDataSimple);
            }
        }

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus bothEven(MessageEvent event) throws IOException {

        return ListeningStatus.LISTENING;
    }


}
