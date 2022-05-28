package org.orisland.wows.dataPack;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.message.data.*;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.Bind;
import org.orisland.wows.doMain.PlayerObj;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;

import java.util.Date;
import java.util.List;

import static org.orisland.wows.dataPack.PlayerData.NickNameToAccountInfo;
import static org.orisland.wows.dataPack.PrData.PrStandard;

/**
 * 数据集中处理打包
 */
@Slf4j
public class DataHandler {

    /**
     * 中间打包
     * @param messageChainBuilder
     * @param shipDataObj
     * @param pr
     * @return
     */
    public static void singleShipInfoPack(MessageChainBuilder messageChainBuilder, ShipDataObj shipDataObj, ShipPr pr, ApiConfig.Type type) {
        if (shipDataObj.getShip() != null) {
            messageChainBuilder
                    .append(shipDataObj.getShip().getName())
                    .append("\r");
        }
        messageChainBuilder
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
                .append(quoteReply)
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
    ) {
        if (shipDataObjs == null)
            return false;

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
                    singleShipInfoPack(messageItem, shipDataObjItem, shipDataObjItem.getPR(), shipDataObjItem.isRank() ? ApiConfig.Type.rank : ApiConfig.Type.random);
                    messageList.add(sender.getBot(), messageItem.build());
                }else if (type == ApiConfig.Type.random){
                    if (!shipDataObjItem.isRank()) {
                        messageItem = new MessageChainBuilder();
                        ShipDataObjPack(shipDataObj, shipPr, shipDataObjItem);
                        singleShipInfoPack(messageItem, shipDataObjItem, shipDataObjItem.getPR(), ApiConfig.Type.random);
                        messageList.add(sender.getBot(), messageItem.build());
                    }
                }else if (type == ApiConfig.Type.rank){
                    if (shipDataObjItem.isRank()) {
                        messageItem = new MessageChainBuilder();
                        ShipDataObjPack(shipDataObj, shipPr, shipDataObjItem);
                        singleShipInfoPack(messageItem, shipDataObjItem, shipDataObjItem.getPR(), ApiConfig.Type.rank);
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
