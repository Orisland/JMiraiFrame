package org.orisland;

import Tool.JsonTool;
import Tool.MiraiTool;
import cn.hutool.core.io.FastByteArrayOutputStream;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileReader;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.Mirai;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.dataPack.StringToMeaningful;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.SingleShipDataSimple;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static org.orisland.wows.ApiConfig.dataDir;
import static org.orisland.wows.DataInit.initDataRefresh;
import static org.orisland.wows.dataPack.DiffData.accountRecordAt;
import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.PrData.*;
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
            saveAccountShipInfo("566316445", ApiConfig.Server.EU, false);
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
            updateAccountLocalDataAuto(false);
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
            JsonNode jsonNode = AllShipInfo();
            System.out.println(jsonNode.size());
            System.out.println(jsonNode);
        }else if (event.getMessage().contentToString().equals("20")){
            saveShipInfo();
        }else if (event.getMessage().contentToString().equals("21")){
            MessageChainBuilder builder = new MessageChainBuilder();
            builder.append("信息\r信息");
            event.getSender().getGroup().sendMessage(builder.build());
            MessageChainBuilder builder1 = new MessageChainBuilder();
            builder1.append("信息\n信息");
            event.getSender().getGroup().sendMessage(builder1.build());
            MessageChainBuilder builder2 = new MessageChainBuilder();
            builder2.append("信息\t信息");
            event.getSender().getGroup().sendMessage(builder2.build());
        }else if (event.getMessage().contentToString().equals("22")){
            ForwardMessage.Node node1 = new ForwardMessage.Node(event.getBot().getId(), 1653807529, "bot", new MessageChainBuilder().append("test1").build());
            ForwardMessage.Node node2 = new ForwardMessage.Node(event.getBot().getId(), 1653807530, "bot", new MessageChainBuilder().append("test2").build());
            ForwardMessage.Node node3 = new ForwardMessage.Node(event.getBot().getId(), 1653807531, "bot", new MessageChainBuilder().append("test3").build());

            Bot bot = event.getBot();
            ForwardMessageBuilder iNodes = new ForwardMessageBuilder(event.getSender());

        }else if (event.getMessage().contentToString().equals("23")){
//            InputStream resourceAsStream = WowsPlugin.class.getClassLoader().getResourceAsStream("图片1.png");
//            Image image1 = net.mamoe.mirai.contact.Contact.uploadImage(event.getSender(), resourceAsStream);
//            InputStream resourceAsStream1 = WowsPlugin.class.getClassLoader().getResourceAsStream("图片2.png");
//            Image image2 = net.mamoe.mirai.contact.Contact.uploadImage(event.getSender(), resourceAsStream1);
//            InputStream resourceAsStream2 = WowsPlugin.class.getClassLoader().getResourceAsStream("图片3.png");
//            Image image3 = net.mamoe.mirai.contact.Contact.uploadImage(event.getSender(), resourceAsStream2);
//            InputStream resourceAsStream3 = WowsPlugin.class.getClassLoader().getResourceAsStream("1.png");
//            Image image4 = net.mamoe.mirai.contact.Contact.uploadImage(event.getSender(), resourceAsStream3);

            Image image = StringToMeaningful.getImage((CommandSenderOnMessage) event.getSender().getGroup(), "1.png");

            MessageChainBuilder append = new MessageChainBuilder()
                    .append("这是一条测试")
                    .append("\n")
                            .append(image);

            event.getGroup().sendMessage(append.build());
        }else if (event.getMessage().contentToString().equals("24")){
            System.out.println("org.orisland.plugin\\\\prImg\\\\");
        }else if (event.getMessage().contentToString().equals("25")){

        }else if (event.getMessage().contentToString().equals("26")){
        }else {
        }

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus bothEven(MessageEvent event) throws IOException {

        return ListeningStatus.LISTENING;
    }


}
