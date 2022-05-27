package org.orisland.wows.command;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.*;
import org.orisland.WowsPlugin;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.Bind;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.orisland.wows.ApiConfig.ShipExpected;
import static org.orisland.wows.ApiConfig.reTry;
import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.ServerData.StringToServer;
import static org.orisland.wows.dataPack.ShipData.*;
import static org.orisland.wows.dataPack.PrData.*;
import static org.orisland.wows.dataPack.DiffData.diffDataBetween;
import static org.orisland.wows.dataPack.DiffData.diffShip;


@Slf4j
public class Account extends JCompositeCommand {
    public static final Account INSTANCE = new Account();

    private Account() {
        super(WowsPlugin.INSTANCE, "wws", new String[]{"w"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"今日", "me", "today"})
    @Description("查询自己的当日pr")
    public void PrToday(CommandSenderOnMessage sender) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();


        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry){
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());
                    messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message);
                }

                sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
                ForwardMessage build = message.build();

                ForwardMessage record = new ForwardMessage(
                        build.getPreview(),
                        String.format("[%s]%s", bind.getServer() == ApiConfig.Server.NA
                                || bind.getServer() == ApiConfig.Server.com
                                ? "NA"
                                : bind.getServer(), bind.getAccountName()),
                        "今日战绩",
                        build.getSource(),
                        build.getSummary(),
                        build.getNodeList());

                sender.sendMessage(record);

                return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10);
            }
        }

        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    @SubCommand({"今日单船", "todayship", "ts"})
    @Description("查询自己的当日指定ship的pr")
    public void PrShipToday(CommandSenderOnMessage sender, String shipName) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry){
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                } else {
                    JsonNode jsonNode = ShipNameToShipId(shipName);
                    List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());

                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
                    MessageChainBuilder builder = new MessageChainBuilder();

                    if (shipDataObjs.size() != 0) {
                        ShipDataObj shipDataObj1 = new ShipDataObj();
                        ShipPr shipPr = new ShipPr();
                        for (ShipDataObj shipDataObj : shipDataObjs) {
                            ShipDataObjPack(shipDataObj1, shipPr, shipDataObj);
                            if (!Objects.equals(shipDataObj.getShip().getShipId(), jsonNode.get("shipId").asText())) {
                                continue;
                            }
                            messagePack(messageChainBuilder, shipDataObj, shipDataObj.getPR());
                        }
                        shipDataObj1.update();
                        JsonNode prStandard = PrStandard(shipPr.PrCalculate());
                        builder.append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                                .append("\r")
                                .append(String.format("今日综合评级:%s %s %s%s", prStandard.get("color").asText(),
                                        prStandard.get("pr").asText(), prStandard.get("evaluate").asText(), prStandard.get("distance").asText()))
                                .append("\r")
                                .append(String.format("场数:%s", shipDataObj1.getBattle()))
                                .append("\r")
                                .append(String.format("胜率:%s", shipDataObj1.getWinRate()))
                                .append("\r")
                                .append("========")
                                .append("\r")
                                .append(messageChainBuilder.build());
                    } else {
                        messageChainBuilder
                                .append("空")
                                .append("\r");
                    }
                    chain = messagePack(builder, quoteReply);
                }
                sender.sendMessage(chain);
                return;
            } catch (Exception e) {
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10 * 1000);
            }
        }

        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    @SubCommand({"spp"})
    @Description("查询指定用户pr")
    public void userPr(CommandSenderOnMessage sender, String accountName, String StringServer) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        ApiConfig.Server server = StringToServer(StringServer);
        SinglePlayer singlePlayer = AccountIdToAccountInfo(searchNickNameToAccountId(accountName, server), server);

        int count = 0;
        StringBuilder exception = new StringBuilder();
        while (count <= ApiConfig.reTry){
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                } else {
                    if (singlePlayer == null){
                        chain = new MessageChainBuilder()
                                .append(String.format("[%s]%s玩家不存在或账户异常!", server == ApiConfig.Server.com ? "NA" : server, accountName))
                                .append(quoteReply)
                                .build();
                        sender.sendMessage(chain);
                        return;
                    }
                    ShipDataObj shipDataObj = AccountIdShipToPr(String.valueOf(singlePlayer.getAccount_id()), server);
                    if (shipDataObj == null){
                        chain = new MessageChainBuilder()
                                .append(String.format("[%s]%s战绩已隐藏!", server, accountName))
                                .append(quoteReply)
                                .build();
                        sender.sendMessage(chain);
                        return;
                    }
                    ShipPr pr = shipDataObj.getPR();

                    shipDataObj.update(singlePlayer);

                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                            .append(String.format("[%s]%s:", server, singlePlayer.getNickname()))
                            .append("\r");

                    messagePack(messageChainBuilder, shipDataObj, pr);
                    chain = messagePack(messageChainBuilder, quoteReply);
                }
                sender.sendMessage(chain);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10 * 1000);
            }
        }
        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    @SubCommand({"searchShipName", "sps"})
    @Description("查询自己的指定船的pr")
    public void PrShip(CommandSenderOnMessage sender, String shipName) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry){
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                } else {
                    String shipId;
                    if (ShipNameToShipId(shipName) == null){
                        chain = new MessageChainBuilder()
                                .append("请检查船名是否正确！")
                                .append("\r")
                                .append("不允许使用英文，不允许存在错别字，允许部分匹配！")
                                .append("\r")
                                .append("请注意标点符号例如马克斯·尹麦曼中的·不可省略，否则请只输入马克斯!")
                                .append("\r")
                                .append("若您确定您的船名输入无误请私聊bot！")
                                .append(quoteReply)
                                .build();
                        sender.sendMessage(chain);
                        return;
                    }else {
                         shipId = ShipNameToShipId(shipName).get("shipId").asText();
                    }
                    ShipDataObj shipDataObj = AccountIdToPr(bind.getAccountId(), bind.getServer(), shipId);
                    if (shipDataObj == null){
                        chain = new MessageChainBuilder()
                                .append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                                .append("\r")
                                .append("请勿查询您还没有开过的船只！")
                                .append(quoteReply)
                                .build();
                        sender.sendMessage(chain);
                        return;
                    }
                    ShipPr pr = shipDataObj.getPR();
                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                            .append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                            .append("\r");

                    messagePack(messageChainBuilder, shipDataObj, pr);
                    chain = messagePack(messageChainBuilder, quoteReply);
                }
                sender.sendMessage(chain);
                return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10 * 1000);
            }
        }


        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    @SubCommand({"战绩", "pr", "水表"})
    @Description("查询自己综合pr")
    public void PrMe(CommandSenderOnMessage sender) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        int count = 0;
        StringBuilder exception = new StringBuilder();
        while (count <= ApiConfig.reTry){
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                } else {
                    ShipDataObj shipDataObj = AccountIdShipToPr(bind.getAccountId(), bind.getServer());
                    ShipPr pr = shipDataObj.getPR();
                    if (shipDataObj == null){
                        chain = new MessageChainBuilder()
                                .append(String.format("[%s]%s战绩已隐藏!", bind.getServer(), bind.getAccountName()))
                                .append(quoteReply)
                                .build();
                        sender.sendMessage(chain);
                        return;
                    }

                    shipDataObj.update(AccountIdToAccountInfo(bind.getAccountId(), bind.getServer()));

                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                            .append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                            .append("\r");

                    messagePack(messageChainBuilder, shipDataObj, pr);
                    chain = messagePack(messageChainBuilder, quoteReply);
                }
                sender.sendMessage(chain);
                return;
            } catch (Exception e) {
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10 * 1000);
            }
        }
        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    @SubCommand({"昨日战绩", "ypr"})
    @Description("查询自己昨天pr")
    public void yesterdayPr(CommandSenderOnMessage sender) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        int count = 0;
        StringBuilder exception = new StringBuilder();
        while (count <= ApiConfig.reTry){
            try {
            Bind bind = findAccountId(qq);
            if (bind == null) {
                chain = bindErrorPack(quoteReply);
                sender.sendMessage(chain);
                return;
            } else {
                List<ShipDataObj> shipDataObjs = accountRecordAt(bind.getAccountId(), bind.getServer(), 1);
                messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message);
            }

            sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
            ForwardMessage build = message.build();

            ForwardMessage record = new ForwardMessage(
                    build.getPreview(),
                    String.format("[%s]%s", bind.getServer() == ApiConfig.Server.NA
                            || bind.getServer() == ApiConfig.Server.com
                            ? "NA"
                            : bind.getServer(), bind.getAccountName()),
                    "昨日战绩",
                    build.getSource(),
                    build.getSummary(),
                    build.getNodeList());

            sender.sendMessage(record);

            return;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10 * 1000);
            }
        }
        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    @SubCommand({"时间段", "dpr"})
    @Description("查询时间段内的pr")
    public void datePr(CommandSenderOnMessage sender, String from, String to) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry){
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                } else {
                    List<ShipDataObj> shipDataObjs = diffDataBetween(bind.getAccountId(), bind.getServer(), Integer.parseInt(from), Integer.parseInt(to));
                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                            .append(new PlainText(String.format("[%s]%s:", bind.getServer(), bind.getAccountName())))
                            .append("\r");
                    if (shipDataObjs.size() != 0) {
                        for (ShipDataObj shipDataObj : shipDataObjs) {
                            messagePack(messageChainBuilder, shipDataObj, shipDataObj.getPR());
                        }
                    } else {
                        messageChainBuilder
                                .append("空")
                                .append("\r");
                    }
                    chain = messagePack(messageChainBuilder, quoteReply);
                }
                sender.sendMessage(chain);
                return;
            } catch (Exception e) {
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10 * 1000);
            }
        }
        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    @SubCommand({"预期", "expship", "es"})
    @Description("查询服务器船只数据")
    public void expectData(CommandSenderOnMessage sender, String shipName) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        int count = 0;
        StringBuilder exception = new StringBuilder();
        while (count <= ApiConfig.reTry){
            try {
                JsonNode jsonNode = ShipNameToShipId(shipName);
                if (jsonNode == null){
                    chain = new MessageChainBuilder()
                            .append("请检查船名是否正确！")
                            .append("\r")
                            .append("不允许使用英文，不允许存在错别字")
                            .append("\r")
                            .append("请注意标点符号例如马克斯·尹麦曼中的·不可省略，否则请只输入马克斯!")
                            .append("\r")
                            .append("若您确定您的船名输入无误请私聊bot！")
                            .build();
                    sender.sendMessage(chain);
                    return;
                }

                String name = jsonNode.get("name").asText();
                String shipId = jsonNode.get("shipId").asText();
                JsonNode jsonNode1 = ShipToExpected(shipId);
                String date = DateUtil.format(DateUtil.date(ShipExpected.get("time").asLong() * 1000), "YYYYMMdd");

                String[] average_damage_dealts = jsonNode1.get("average_damage_dealt").asText().split("\\.");
                String[] average_frags = jsonNode1.get("average_frags").asText().split("\\.");
                String[] win_rates = jsonNode1.get("win_rate").asText().split("\\.");

                chain = messagePack(new MessageChainBuilder()
                        .append("服务器船只数据:")
                        .append("\r")
                        .append(String.format("数据更新时间:%s", date))
                        .append("\r")
                        .append(name)
                        .append("\r")
                        .append(String.format("均伤:%s.%s", average_damage_dealts[0], average_damage_dealts[1].substring(0,2)))
                        .append("\r")
                        .append(String.format("均击杀:%s.%s", average_frags[0], average_frags[1].substring(0, 2)))
                        .append("\r")
                        .append(String.format("胜率:%s.%s%%", win_rates[0], win_rates[1].substring(0,2)))
                        .append("\r")
                        .append("=========")
                        .append("\r"), quoteReply);

                sender.sendMessage(chain);
                return;
            } catch (Exception e) {
                log.error("错误计数:{}", ++count);
                exception.append(e.getMessage())
                        .append("\r");
                Thread.sleep(reTry / 10 * 1000);
            }
        }
        chain = errorFinally(quoteReply, exception.toString());
        sender.sendMessage(chain);
    }

    /**
     * 中间打包
     * @param messageChainBuilder
     * @param shipDataObj
     * @param pr
     * @return
     */
    public static MessageChainBuilder messagePack(MessageChainBuilder messageChainBuilder, ShipDataObj shipDataObj, ShipPr pr) {
        if (shipDataObj.getShip() == null){
            messageChainBuilder
                    .append(String.format("综合评级:%s %s %s%s", pr.getColor(), pr.getPR(), pr.getEvaluate(), pr.getDistance()))
                    .append("\r")
                    .append(String.format("场数:%s", shipDataObj.getBattle()))
                    .append("\r")
                    .append(String.format("胜率:%s", shipDataObj.getWinRate()))
                    .append("\r")
                    .append(String.format("均伤:%s", shipDataObj.getAveDmg()))
                    .append("\r")
                    .append(String.format("经验:%s", shipDataObj.getAveXp()))
                    .append("\r")
                    .append(String.format("KD：%s", shipDataObj.getKD()))
                    .append("\r")
                    .append(String.format("命中率:%s", shipDataObj.getHitRate()))
                    .append("\r")
                    .append(String.format("存活胜利率:%s", shipDataObj.getSurviveWinRate()))
                    .append("\r");
        }else {
            messageChainBuilder
                    .append(shipDataObj.getShip().getName())
                    .append("\r")
                    .append(String.format("综合评级:%s %s %s%s", pr.getColor(), pr.getPR(), pr.getEvaluate(), pr.getDistance()))
                    .append("\r")
                    .append(String.format("场数:%s", shipDataObj.getBattle()))
                    .append("\r")
                    .append(String.format("胜率:%s", shipDataObj.getWinRate()))
                    .append("\r")
                    .append(String.format("均伤:%s", shipDataObj.getAveDmg()))
                    .append("\r")
                    .append(String.format("经验:%s", shipDataObj.getAveXp()))
                    .append("\r")
                    .append(String.format("KD：%s", shipDataObj.getKD()))
                    .append("\r")
                    .append(String.format("命中率:%s", shipDataObj.getHitRate()))
                    .append("\r")
                    .append(String.format("存活胜利率:%s", shipDataObj.getSurviveWinRate()))
                    .append("\r");
        }

        return messageChainBuilder;
    }

    /**
     * 结尾pack
     * @param messageChainBuilder
     * @param quoteReply
     * @return
     */
    public static MessageChain messagePack(MessageChainBuilder messageChainBuilder, QuoteReply quoteReply){
        messageChainBuilder.append(String.format("查询时间：%s", DateUtil.format(new Date(), "YYYY-MM-dd HH:mm")))
                .append(quoteReply)
                .build();
        return messageChainBuilder.build();
    }

    public static MessageChain bindErrorPack(QuoteReply quoteReply){
        MessageChain build = new MessageChainBuilder()
                .append(new PlainText("未找到账户，请先绑定账号！"))
                .append(new PlainText("输入(/)wh help查看插件帮助！"))
                .append(quoteReply)
                .build();
        return build;
    }

    public static MessageChain errorFinally(QuoteReply quoteReply, String exception){
        MessageChain build = new MessageChainBuilder()
                .append("出错啦！请重试！")
                .append("\r")
                .append(exception)
                .append(quoteReply)
                .build();
        return  build;
    }


    /**
     * 数据打包
     * @param shipDataObj1
     * @param shipPr
     * @param shipDataObj
     */
    private void ShipDataObjPack(ShipDataObj shipDataObj1, ShipPr shipPr, ShipDataObj shipDataObj) {
        shipDataObj1.setBattle(shipDataObj1.getBattle() + shipDataObj.getBattle());
        shipDataObj1.setKill(shipDataObj1.getKill() + shipDataObj.getKill());
        shipDataObj1.setWins(shipDataObj1.getWins() + shipDataObj.getWins());
        shipDataObj1.setDmg(shipDataObj1.getDmg() + shipDataObj.getDmg());
        shipPr.setExpectedDmg(shipPr.getExpectedDmg() + shipDataObj.getPR().getExpectedDmg());
        shipPr.setExpectedWins(shipPr.getExpectedWins() + shipDataObj.getPR().getExpectedWins());
        shipPr.setExpectedFrags(shipPr.getExpectedFrags() + shipDataObj.getPR().getExpectedFrags());
        shipPr.setActualDmg(shipPr.getActualDmg() + shipDataObj.getPR().getActualDmg());
        shipPr.setActualWins(shipPr.getActualWins() + shipDataObj.getPR().getActualWins());
        shipPr.setActualFrags(shipPr.getActualFrags() + shipDataObj.getPR().getActualFrags());
    }

    /**
     * pr主要数据打包处理
     * @param shipDataObjs
     * @param messageItem
     * @param messageList
     * @param sender
     * @param bind
     * @param message
     */
    public void messagePackPr(List<ShipDataObj> shipDataObjs,
                              MessageChainBuilder messageItem,
                              ForwardMessageBuilder messageList,
                              CommandSenderOnMessage sender,
                              Bind bind,
                              ForwardMessageBuilder message){
        if (shipDataObjs.size() != 0) {
            ShipDataObj shipDataObj1 = new ShipDataObj();
            ShipPr shipPr = new ShipPr();
            for (ShipDataObj shipDataObj : shipDataObjs) {
                messageItem = new MessageChainBuilder();
                ShipDataObjPack(shipDataObj1, shipPr, shipDataObj);
                messagePack(messageItem, shipDataObj, shipDataObj.getPR());
                messageList.add(sender.getBot(), messageItem.build());
            }

            shipDataObj1.update();

            JsonNode jsonNode = PrStandard(shipPr.PrCalculate());

            messageItem = new MessageChainBuilder();
            messageItem.append(String.format("[%s]%s:", bind.getServer() == ApiConfig.Server.com ? "NA"
                            : bind.getServer(), bind.getAccountName()))
                    .append("\r")
                    .append(String.format("昨日综合评级:%s %s %s%s", jsonNode.get("color").asText(),
                            jsonNode.get("pr").asText(), jsonNode.get("evaluate").asText(), jsonNode.get("distance").asText()))
                    .append("\r")
                    .append(String.format("场数:%s", shipDataObj1.getBattle()))
                    .append("\r")
                    .append(String.format("胜率:%s", shipDataObj1.getWinRate()))
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
    }

}
