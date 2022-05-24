package org.orisland.wows.command;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.orisland.WowsPlugin;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.Bind;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;
import org.orisland.wows.doMain.SingleShip;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.ShipData.*;

/**
 * @Author: zhaolong
 * @Time: 00:05
 * @Date: 2021年07月30日 00:05
 **/
public class Account extends JCompositeCommand {
    public static final Account INSTANCE = new Account();

    private Account(){
        super(WowsPlugin.INSTANCE, "wws", new String[]{"w"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"今日", "me", "today"})
    @Description("查询自己的当日pr")
    public void PrToday(CommandSenderOnMessage sender){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        try {
            Bind bind = findAccountId(qq);
            if (bind == null){
                chain = new MessageChainBuilder()
                        .append(new PlainText("未找到账户，请先绑定账号！"))
                        .append(new PlainText("输入/w help查看插件帮助！"))
                        .append(quoteReply)
                        .build();
            }else {
                List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                        .append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                        .append("\r");
                if (shipDataObjs.size() != 0){
                    for (ShipDataObj shipDataObj : shipDataObjs) {
                        messageChainBuilder = messagePack(messageChainBuilder, shipDataObj, shipDataObj.getPR());
                    }
                }else {
                    messageChainBuilder
                            .append("空");
                }
                chain = messageChainBuilder.append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")))
                        .append(quoteReply)
                        .build();
            }
            sender.sendMessage(chain);
        }catch (Exception e){
            chain = new MessageChainBuilder()
                    .append("出错啦！请重试！")
                    .append(e.getMessage())
                    .append(quoteReply)
                    .build();
            sender.sendMessage(chain);
        }
    }

    @SubCommand({"今日单船", "todayship"})
    @Description("查询自己的当日指定ship的pr")
    public void PrShipToday(CommandSenderOnMessage sender, String shipName){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;

        try {
            Bind bind = findAccountId(qq);
            if (bind == null){
                chain = new MessageChainBuilder()
                        .append(new PlainText("未找到账户，请先绑定账号！"))
                        .append(new PlainText("输入/w help查看插件帮助！"))
                        .append(quoteReply)
                        .build();
            }else {
                JsonNode jsonNode = ShipNameToShipId(shipName);
                List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                        .append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                        .append("\r");
                if (shipDataObjs.size() != 0){
                    for (ShipDataObj shipDataObj : shipDataObjs) {
                        if (!Objects.equals(shipDataObj.getShip().getShipId(), jsonNode.get("shipId").asText())){
                            continue;
                        }
                        messageChainBuilder = messagePack(messageChainBuilder, shipDataObj, shipDataObj.getPR());
                    }
                }else {
                    messageChainBuilder
                            .append("空");
                }
                chain = messageChainBuilder.append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")))
                        .append(quoteReply)
                        .build();
            }
            sender.sendMessage(chain);
        }catch (Exception e){
            chain = new MessageChainBuilder()
                    .append("出错啦！请重试！")
                    .append(e.getMessage())
                    .append(quoteReply)
                    .build();
            sender.sendMessage(chain);
        }
    }

    @SubCommand({"找人", "sp", "searchplayer"})
    @Description("查询指定用户当日pr")
    public void userPrToday(CommandSenderOnMessage sender, String accountName, String StringServer){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        MessageChain chain = null;

        try {
            ApiConfig.Server server = StringToServer(StringServer);
            SinglePlayer singlePlayer = NickNameToAccountInfo(accountName, server);
            List<ShipDataObj> shipDataObjs = diffShip(String.valueOf(singlePlayer.getAccount_id()), server);
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                    .append(new PlainText(String.format("[%s]%s:", server, singlePlayer.getNickname())))
                    .append("\r");
            if (shipDataObjs.size() != 0){
                for (ShipDataObj shipDataObj : shipDataObjs) {
                    messageChainBuilder = messagePack(messageChainBuilder, shipDataObj, shipDataObj.getPR());
                }
            }else {
                messageChainBuilder
                        .append("空");
            }
            chain = messageChainBuilder.append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")))
                    .append(quoteReply)
                    .build();
            sender.sendMessage(chain);
        }catch (Exception e){
            e.printStackTrace();
            chain = new MessageChainBuilder()
                    .append("出错啦！请重试！")
                    .append("\r")
                    .append(e.getMessage())
                    .append(quoteReply)
                    .build();
            sender.sendMessage(chain);
        }
    }

    @SubCommand({"searchShipName", "sps"})
    @Description("查询自己的指定船的pr")
    public void PrShip(CommandSenderOnMessage sender, String shipName){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        try {
            Bind bind = findAccountId(qq);
            if (bind == null){
                chain = new MessageChainBuilder()
                        .append(new PlainText("未找到账户，请先绑定账号！"))
                        .append(new PlainText("输入/w help查看插件帮助！"))
                        .append(quoteReply)
                        .build();
            }else {
                String shipId = ShipNameToShipId(shipName).get("shipId").asText();
                ShipDataObj shipDataObj = AccountIdToPr(bind.getAccountId(), bind.getServer(), shipId);
                ShipPr pr = shipDataObj.getPR();
                MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                        .append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                        .append("\r");

                messageChainBuilder = messagePack(messageChainBuilder, shipDataObj, pr);
                chain = messageChainBuilder.append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")))
                        .append(quoteReply)
                        .build();
            }
            sender.sendMessage(chain);
        }catch (Exception e){
            e.printStackTrace();
            chain = new MessageChainBuilder()
                    .append("出错啦！请重试！")
                    .append(e.getMessage())
                    .append(quoteReply)
                    .build();
            sender.sendMessage(chain);
        }
    }

    /**
     * 数据打包
     * @param messageChainBuilder
     * @param shipDataObj
     * @param pr
     * @return
     */
    public static MessageChainBuilder messagePack(MessageChainBuilder messageChainBuilder, ShipDataObj shipDataObj, ShipPr pr){
        messageChainBuilder
                .append(shipDataObj.getShip().getName())
                .append("\r")
                .append(String.format("综合评级:%s %s %s%s", pr.getColor(), pr.getPR(), pr.getEvaluate(),pr.getDistance()))
                .append("\r")
                .append(String.format("场数:%s", shipDataObj.getBattle()))
                .append("\r")
                .append(String.format("胜率:%s", shipDataObj.getWinRate()))
                .append("\r")
                .append(String.format("均伤:%s", shipDataObj.getAveDmg()))
                .append("\r")
                .append(String.format("经验:%s", shipDataObj.getAveXp()))
                .append("\r")
                .append(String.format("KD：%s",shipDataObj.getKD()))
                .append("\r")
                .append(String.format("命中率:%s", shipDataObj.getHitRate()))
                .append("\r")
                .append(String.format("存活胜利率:%s", shipDataObj.getSurviveWinRate()))
                .append("\r")
                .append("========")
                .append("\r");
        return messageChainBuilder;
    }

}
