package org.orisland.wows.dataPack;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.Bind;
import org.orisland.wows.doMain.PlayerObj;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.orisland.wows.dataPack.PlayerData.NickNameToAccountInfo;
import static org.orisland.wows.dataPack.PrData.PrStandard;
import static org.orisland.wows.dataPack.StringToMeaningful.getImage;

/**
 * 数据集中处理打包
 */
@Slf4j
public class DataHandler {

    /**
     * 为信息链增加长度
     * @param messageList
     * @param messageItem
     * @param bot
     * @param msg
     */
    public static void addForwardLine (ForwardMessageBuilder messageList, MessageChainBuilder messageItem, Bot bot, String msg){
        messageItem.clear();
        messageItem.append(msg);
        messageList.add(bot, messageItem.build());
    }

    /**
     *
     * @param msg       信息打包
     * @param title     标题
     * @param sender    发送者
     * @param brief     简讯
     * @param preview   预览3条
     * @param bot       机器人
     * @return          信息块
     */
    public static ForwardMessage addForwardPack(String[] msg, String title, String brief, String[] preview, CommandSenderOnMessage sender, Bot bot){
        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();
        ForwardMessageBuilder previewList = new ForwardMessageBuilder(sender.getFromEvent().getSender());

        for (String s : preview) {
            messageItem.clear();
            previewList.add(bot, messageItem.append(s).build());
        }

        ForwardMessage previews = previewList.build();

        for (String s : msg) {
            messageItem.clear();
            messageList.add(bot, messageItem.append(s).build());
        }

        ForwardMessage build = messageList.build();

        return new ForwardMessage(previews.getPreview(), title, brief, build.getSource(), build.getSummary(), build.getNodeList());

    }

    /**
     * 中间打包
     * @param messageChainBuilder
     * @param shipDataObj
     * @param pr
     * @return
     */
    public static void singleShipInfoPack(CommandSenderOnMessage sender, MessageChainBuilder messageChainBuilder, ShipDataObj shipDataObj, ShipPr pr, ApiConfig.Type type) throws IOException {
        if (shipDataObj.getShip() != null) {
            messageChainBuilder
                    .append(shipDataObj.getShip().getName())
                    .append("\r");
        }
        messageChainBuilder
                .append(getImage(sender, pr.getPic()))
                .append("\n")
                .append(String.format("综合评级：%s %s %s%s", pr.getColor(), pr.getPR(), pr.getEvaluate(), pr.getDistance()))
                .append("\r")
                .append(String.format("场数：%s", shipDataObj.getBattle()))
                .append("\r")
                .append(String.format("胜率：%s", shipDataObj.getWinRate()))
                .append("\r")
                .append(String.format("均伤：%s", shipDataObj.getAveDmg()))
                .append("\r")
                .append(String.format("经验：%s", shipDataObj.getAveXp()))
                .append("\r")
                .append(String.format("KD：%s", shipDataObj.getKD()))
                .append("\r")
                .append(String.format("命中率：%s", shipDataObj.getHitRate()))
                .append("\r")
                .append(String.format("存活胜利率：%s", shipDataObj.getSurviveWinRate()))
                .append("\r")
                .append(String.format("战斗类型：%s", shipDataObj.isRank() ? "排位" : "随机"))
                .append("\r")
                .append("========")
                .append("\r");
    }

    /**
     * 结尾pack
     * @param messageChainBuilder
     * @param quoteReply
     * @return
     */
    public static MessageChain messageLinePackEnd(MessageChainBuilder messageChainBuilder, QuoteReply quoteReply) {
        messageChainBuilder.append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")))
                .append(quoteReply == null ? new PlainText("") : quoteReply)
                .build();
        return messageChainBuilder.build();
    }

    /**
     * 绑定异常信息打包
     * @param quoteReply
     * @return
     */
    public static MessageChain bindErrorPack(QuoteReply quoteReply) {
        MessageChain build = new MessageChainBuilder()
                .append(new PlainText("未找到账户，请先绑定账号！"))
                .append(new PlainText("输入(/)wh help查看插件帮助！"))
                .append(quoteReply)
                .build();
        return build;
    }

    /**
     * 方法出现异常时信息打包
     * @param quoteReply
     * @param exception
     * @return
     */
    public static MessageChain errorFinally(QuoteReply quoteReply, String exception) {
        MessageChain build = new MessageChainBuilder()
                .append("出错啦！请重试！")
                .append("\r")
                .append(exception)
                .append(quoteReply)
                .build();
        return build;
    }


    /**
     * 数据打包
     *
     * @param shipDataObjOld
     * @param shipPr
     * @param shipDataObjNew
     */
    public static void ShipDataObjPack(ShipDataObj shipDataObjOld, ShipPr shipPr, ShipDataObj shipDataObjNew) {
        shipDataObjOld.setBattle(shipDataObjOld.getBattle() + shipDataObjNew.getBattle());
        shipDataObjOld.setKill(shipDataObjOld.getKill() + shipDataObjNew.getKill());
        shipDataObjOld.setWins(shipDataObjOld.getWins() + shipDataObjNew.getWins());
        shipDataObjOld.setDmg(shipDataObjOld.getDmg() + shipDataObjNew.getDmg());
        shipPr.setExpectedDmg(shipPr.getExpectedDmg() + shipDataObjNew.getPR().getExpectedDmg());
        shipPr.setExpectedWins(shipPr.getExpectedWins() + shipDataObjNew.getPR().getExpectedWins());
        shipPr.setExpectedFrags(shipPr.getExpectedFrags() + shipDataObjNew.getPR().getExpectedFrags());
        shipPr.setActualDmg(shipPr.getActualDmg() + shipDataObjNew.getPR().getActualDmg());
        shipPr.setActualWins(shipPr.getActualWins() + shipDataObjNew.getPR().getActualWins());
        shipPr.setActualFrags(shipPr.getActualFrags() + shipDataObjNew.getPR().getActualFrags());
    }


    /**
     * pr主要数据打包处理
     *
     * @param shipDataObjs
     * @param messageItem
     * @param messageList
     * @param sender
     * @param bind
     * @param message
     */
    public static boolean messagePackPr(List<ShipDataObj> shipDataObjs,
                                 MessageChainBuilder messageItem,
                                 ForwardMessageBuilder messageList,
                                 CommandSenderOnMessage sender,
                                 Bind bind,
                                 ForwardMessageBuilder message,
                                 ApiConfig.Type type
    ) throws IOException {
        try {
            if (shipDataObjs.get(0) == null){
                messageItem
                        .append("空")
                        .append("\r");
                message.add(sender.getBot(), messageItem.build());
                messageItem = new MessageChainBuilder()
                        .append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")));
                message.add(sender.getBot(), messageItem.build());
                return false;
            }
        }catch (Exception e){
//            ignore
        }

        if (shipDataObjs == null || shipDataObjs.size() == 0){
            messageItem
                    .append("空")
                    .append("\r");
            message.add(sender.getBot(), messageItem.build());
            messageItem = new MessageChainBuilder()
                    .append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")));
            message.add(sender.getBot(), messageItem.build());
            return true;
        }


//防止出现没打xx但是查询的问题
        int rank = 0;
        int random = 0;
        boolean flag = true;
        for (ShipDataObj shipDataObj : shipDataObjs) {
            if (shipDataObj.isRank())
                rank ++;
            else
                random ++;
        }
        if (rank == 0 && type == ApiConfig.Type.rank){
            flag = false;
        }
        if (random == 0 && type == ApiConfig.Type.random){
            flag = false;
        }

        if (shipDataObjs.size() != 0 && flag) {
            ShipDataObj shipDataObj = new ShipDataObj();
            ShipPr shipPr = new ShipPr();
            for (ShipDataObj shipDataObjItem : shipDataObjs) {
                if (type == ApiConfig.Type.normal){
                    messageItem = new MessageChainBuilder();
                    ShipDataObjPack(shipDataObj, shipPr, shipDataObjItem);
                    singleShipInfoPack(sender, messageItem, shipDataObjItem, shipDataObjItem.getPR(), shipDataObjItem.isRank() ? ApiConfig.Type.rank : ApiConfig.Type.random);
                    messageList.add(sender.getBot(), messageItem.build());
                }else if (type == ApiConfig.Type.random){
                    if (!shipDataObjItem.isRank()) {
                        messageItem = new MessageChainBuilder();
                        ShipDataObjPack(shipDataObj, shipPr, shipDataObjItem);
                        singleShipInfoPack(sender, messageItem, shipDataObjItem, shipDataObjItem.getPR(), ApiConfig.Type.random);
                        messageList.add(sender.getBot(), messageItem.build());
                    }
                }else if (type == ApiConfig.Type.rank){
                    if (shipDataObjItem.isRank()) {
                        messageItem = new MessageChainBuilder();
                        ShipDataObjPack(shipDataObj, shipPr, shipDataObjItem);
                        singleShipInfoPack(sender, messageItem, shipDataObjItem, shipDataObjItem.getPR(), ApiConfig.Type.rank);
                        messageList.add(sender.getBot(), messageItem.build());
                    }
                }
            }

            shipDataObj.update();

            JsonNode jsonNode = PrStandard(shipPr.PrCalculate());

            messageItem = new MessageChainBuilder();
            messageItem.append(String.format("[%s]%s：", bind.getServer() == ApiConfig.Server.com ? "NA"
                            : bind.getServer(), bind.getAccountName()))
                    .append("\r")
                    .append(getImage(sender, jsonNode.get("pic").asText()))
                    .append("\n")
                    .append(String.format("%s综合评级：%s %s %s%s",
                            type == ApiConfig.Type.rank
                                    ? "排位"
                                    : type == ApiConfig.Type.random
                                    ? "随机" : "",
                            jsonNode.get("color").asText(),
                            jsonNode.get("pr").asText(),
                            jsonNode.get("evaluate").asText(),
                            jsonNode.get("distance").asText()))
                    .append("\r")
                    .append(String.format("场数：%s", shipDataObj.getBattle()))
                    .append("\r")
                    .append(String.format("胜率：%s", shipDataObj.getWinRate()))
                    .append("\r");

            message.add(sender.getBot(), messageItem.build());
            message.add(sender.getBot(), messageList.build());
        } else {
            messageItem
                    .append("空")
                    .append("\r");
            message.add(sender.getBot(), messageItem.build());
        }

        messageItem = new MessageChainBuilder()
                .append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")));
        message.add(sender.getBot(), messageItem.build());
        return true;
    }


}
