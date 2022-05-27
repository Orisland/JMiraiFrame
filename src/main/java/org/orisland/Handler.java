package org.orisland;

import Tool.JsonTool;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.ForwardMessageBuilder;
import net.mamoe.mirai.message.data.PlainText;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.SingleShipDataSimple;


import java.io.IOException;
import java.util.List;

import static org.orisland.wows.DataInit.initDataRefresh;
import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.PrData.NickNameToPr;
import static org.orisland.wows.dataPack.PrData.ShipPr;
import static org.orisland.wows.dataPack.ShipData.*;
import static org.orisland.wows.dataPack.DiffData.diffShip;

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
        if (event.getMessage().contentToString().equals("1")){
            NickNameToAccountInfo("Orisland_Ex", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("2")){
            searchNickNameToAccountId("Orisland_Ex", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("3")){
//            ShipData.SearchShipIdToShipInfo("4277090288");
            SearchAccountIdToShipInfo("566316444", "4277090288", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("4")){
            SearchAccountIdToShipInfo("567073590", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("5")){
            List<SingleShipDataSimple> singleShipDataSimples = SearchAccountIdToAccountInfoByDate("566316444", ApiConfig.Server.EU, "20220518,20220517");
            for (SingleShipDataSimple singleShipDataSimple : singleShipDataSimples) {
                System.out.println(singleShipDataSimple);
            }
        }else if (event.getMessage().contentToString().equals("6")){
            ObjectNode ship = JsonTool.mapper.createObjectNode();
            ship.put("Dmg", "47281979");
            ship.put("Wins", "276");
            ship.put("Frags", "484");
            ship.put("battle", "552");
            ship.put("shipId", "4277090288");
            ShipPr(ship);
        }else if (event.getMessage().contentToString().equals("7")){
            NickNameToPr("Orisland_Ex", ApiConfig.Server.EU);
//            System.out.println(orisland_ex);
        }else if (event.getMessage().contentToString().equals("8")){
            readAccountToday("566316444", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("9")){
            saveAccountShipInfo("566316445", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("10")){
            List<ShipDataObj> shipDataObjs = diffShip("566316444", ApiConfig.Server.EU);
            for (ShipDataObj shipDataObj : shipDataObjs) {
                System.out.println(shipDataObj.getShip().getName());
                System.out.println(shipDataObj.getBattle());
                System.out.println(shipDataObj.getAveDmg());
                System.out.println(shipDataObj.getPR().getPR());
            }
        }else if (event.getMessage().contentToString().equals("11")){
            originPlayerData("566316444", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("12")){
            selectData("566316444", ApiConfig.Server.EU, true);
        }else if (event.getMessage().contentToString().equals("13")){
            updateAccountLocalData("566316444", ApiConfig.Server.EU);
        }else if (event.getMessage().contentToString().equals("14")){
            initDataRefresh();
        }else if (event.getMessage().contentToString().equals("15")){
            updateAccountLocalDataAuto();
        }else if (event.getMessage().contentToString().equals("16")){
            accountRecordAt("566316444", ApiConfig.Server.EU, 5);
        }else if (event.getMessage().contentToString().equals("17")){
            System.out.println(searchAccountIdToAccountInfo("566316444", ApiConfig.Server.EU));
        }else if (event.getMessage().contentToString().equals("18")){
            Bot bot = event.getBot();
            ForwardMessageBuilder iNodes = new ForwardMessageBuilder(event.getSender());
            iNodes.add(bot, new PlainText("123"));
            iNodes.add(bot, new PlainText("test"));
            event.getSender().getGroup().sendMessage(iNodes.build());
        }else if (event.getMessage().contentToString().equals("19")){
            Bot bot = event.getBot();
            ForwardMessageBuilder iNodes = new ForwardMessageBuilder(event.getSender());
            iNodes.add(bot, new PlainText("123"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            iNodes.add(bot, new PlainText("test"));
            ForwardMessage build = iNodes.build();
            ForwardMessage forwardMessage = new ForwardMessage(build.getPreview(), "标题", "test", "source", "总结", build.getNodeList());
            event.getSender().getGroup().sendMessage(forwardMessage);

        }

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus bothEven(MessageEvent event) throws IOException {

        return ListeningStatus.LISTENING;
    }


}
