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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.orisland.wows.ApiConfig.*;
import static org.orisland.wows.dataPack.DataHandler.bindErrorPack;
import static org.orisland.wows.dataPack.DataHandler.errorFinally;
import static org.orisland.wows.dataPack.DiffData.*;
import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.StringToMeaningful.*;
import static org.orisland.wows.dataPack.ShipData.*;
import static org.orisland.wows.dataPack.PrData.*;
import static org.orisland.wows.dataPack.DataHandler.*;


@Slf4j
public class Account extends JCompositeCommand {
    public static final Account INSTANCE = new Account();

    private Account() {
        super(WowsPlugin.INSTANCE, "wws", new String[]{"w"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"今日", "me", "today", "recent"})
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

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());
                    boolean b = messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, Type.random);
                    if (!b){
                        chain = errorFinally(quoteReply, "访问的战绩可能不存在！");
                        sender.sendMessage(chain);
                        return;
                    }
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

    @SubCommand({"recent", "最近", "me"})
    @Description("查询自己的当日pr")
    public void PrToday(CommandSenderOnMessage sender, long day) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    if (day < 0)
                        day = -day;
                    long today = Long.parseLong(DateUtil.format(new Date(), "YYYYMMdd"));
                    List<ShipDataObj> shipDataObjs = diffDataBetween(bind.getAccountId(), bind.getServer(), (int) today, (int) (today - day));
                    boolean b = messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, Type.random);
                    if (!b){
                        chain = errorFinally(quoteReply, "访问的战绩可能不存在！");
                        sender.sendMessage(chain);
                        return;
                    }
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

    @SubCommand({"全部", "all", "al"})
    @Description("查询自己的当日pr")
    public void PrRandomToday(CommandSenderOnMessage sender, long day) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    if (day < 0)
                        day = -day;
                    long today = Long.parseLong(DateUtil.format(new Date(), "YYYYMMdd"));
                    List<ShipDataObj> shipDataObjs = diffDataBetween(bind.getAccountId(), bind.getServer(), (int) today, (int) (today - day));
                    boolean b = messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, Type.normal);
                    if (!b){
                        chain = errorFinally(quoteReply, "访问的战绩可能不存在！");
                        sender.sendMessage(chain);
                        return;
                    }
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

    @SubCommand({"全部", "all", "al"})
    @Description("查询自己的当日pr")
    public void PrRandomToday(CommandSenderOnMessage sender) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());
                    boolean b = messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, Type.normal);
                    if (!b){
                        chain = errorFinally(quoteReply, "访问的战绩可能不存在！");
                        sender.sendMessage(chain);
                        return;
                    }
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

    @SubCommand({"rank", "软壳", "排位", "pw"})
    @Description("查询今日软壳战绩的pr")
    public void rankPrToday(CommandSenderOnMessage sender) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();


        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());

                    boolean b = messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, Type.rank);
                    if (!b){
                        chain = errorFinally(quoteReply, "访问的战绩可能不存在！");
                        sender.sendMessage(chain);
                        return;
                    }
                }

                sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
                ForwardMessage build = message.build();

                ForwardMessage record = new ForwardMessage(
                        build.getPreview(),
                        String.format("[%s]%s", bind.getServer() == ApiConfig.Server.NA
                                || bind.getServer() == ApiConfig.Server.com
                                ? "NA"
                                : bind.getServer(), bind.getAccountName()),
                        "今日rank战绩",
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

    @SubCommand({"rank", "软壳", "排位", "pw"})
    @Description("查询今日软壳战绩的pr")
    public void rankPrToday(CommandSenderOnMessage sender, long day) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();


        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    if (day < 0)
                        day = -day;
                    long today = Long.parseLong(DateUtil.format(new Date(), "YYYYMMdd"));
                    List<ShipDataObj> shipDataObjs = diffDataBetween(bind.getAccountId(), bind.getServer(), (int) today, (int) (today - day));

                    boolean b = messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, Type.rank);
                    if (!b){
                        chain = errorFinally(quoteReply, "访问的战绩可能不存在！");
                        sender.sendMessage(chain);
                        return;
                    }
                }

                sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
                ForwardMessage build = message.build();

                ForwardMessage record = new ForwardMessage(
                        build.getPreview(),
                        String.format("[%s]%s", bind.getServer() == ApiConfig.Server.NA
                                || bind.getServer() == ApiConfig.Server.com
                                ? "NA"
                                : bind.getServer(), bind.getAccountName()),
                        "今日rank战绩",
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

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                } else {
                    JsonNode jsonNode = ShipNameToShipId(shipName);
                    List<ShipDataObj> shipDataObjs = diffShip(bind.getAccountId(), bind.getServer());

                    ShipDataObj shipDataObj1 = new ShipDataObj();
                    ShipPr shipPr = new ShipPr();
                    List<ShipDataObj> shipDataObjs1 = new ArrayList<>();
                    for (ShipDataObj shipDataObj : shipDataObjs) {
                        ShipDataObjPack(shipDataObj1, shipPr, shipDataObj);
                        if (!Objects.equals(shipDataObj.getShip().getShipId(), jsonNode.get("shipId").asText())) {
                            continue;
                        }
                        shipDataObjs1.add(shipDataObj);
                    }
                    boolean b = messagePackPr(shipDataObjs1, messageItem, messageList, sender, bind, message, Type.normal);
                    if (!b){
                        chain = errorFinally(quoteReply, "访问的战绩可能不存在！");
                        sender.sendMessage(chain);
                        return;
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
        ForwardMessageBuilder messList = null;

        ForwardMessageBuilder preInfo = new ForwardMessageBuilder(sender.getFromEvent().getSender())
                .add(sender.getBot(), new MessageChainBuilder().append("单机查看pr信息！").build());


        int count = 0;
        StringBuilder exception = new StringBuilder();
        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    if (singlePlayer == null) {
                        chain = new MessageChainBuilder()
                                .append(String.format("[%s]%s玩家不存在或账户异常!", server == ApiConfig.Server.com ? "NA" : server, accountName))
                                .append(quoteReply)
                                .build();
                        sender.sendMessage(chain);
                        return;
                    }
                    ShipDataObj shipDataObj = AccountIdShipToPr(String.valueOf(singlePlayer.getAccount_id()), server);
                    if (shipDataObj == null) {
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

                    singleShipInfoPack(messageChainBuilder, shipDataObj, pr, Type.normal);

                    messList = new ForwardMessageBuilder(sender.getFromEvent().getSender())
                            .add(sender.getBot(), messageChainBuilder.build())
                            .add(sender.getBot(), messageLinePackEnd(new MessageChainBuilder(), null));
                }

                ForwardMessage build = messList.build();

                ForwardMessage record = new ForwardMessage(
                        preInfo.build().getPreview(),
                        String.format("[%s]%s", server == ApiConfig.Server.NA
                                || server == ApiConfig.Server.com
                                ? "NA"
                                : server, accountName),
                        "个人pr",
                        build.getSource(),
                        build.getSummary(),
                        build.getNodeList());

                sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
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

    @SubCommand({"searchShipName", "sps"})
    @Description("查询自己的指定船的pr")
    public void PrShip(CommandSenderOnMessage sender, String shipName) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    String shipId;
                    if (ShipNameToShipId(shipName) == null) {
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
                    } else {
                        shipId = ShipNameToShipId(shipName).get("shipId").asText();
                    }

                    ShipDataObj shipDataObj = AccountIdToPr(bind.getAccountId(), bind.getServer(), shipId);
                    List<ShipDataObj> shipDataObjs = new ArrayList<>();
                    shipDataObjs.add(shipDataObj);
                    messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, Type.normal);
                }

                sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
                ForwardMessage build = message.build();

                ForwardMessage record = new ForwardMessage(
                        build.getPreview(),
                        String.format("[%s]%s", bind.getServer() == ApiConfig.Server.NA
                                || bind.getServer() == ApiConfig.Server.com
                                ? "NA"
                                : bind.getServer(), bind.getAccountName()),
                        "PR:",
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

    @SubCommand({"战绩", "pr", "水表"})
    @Description("查询自己综合pr")
    public void PrMe(CommandSenderOnMessage sender) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        ForwardMessageBuilder messList = null;

        ForwardMessageBuilder preInfo = new ForwardMessageBuilder(sender.getFromEvent().getSender())
                .add(sender.getBot(), new MessageChainBuilder().append("单机查看pr信息！").build());

        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    ShipDataObj shipDataObj = AccountIdShipToPr(bind.getAccountId(), bind.getServer());
                    ShipPr pr = shipDataObj.getPR();
                    if (shipDataObj == null) {
                        chain = new MessageChainBuilder()
                                .append(String.format("[%s]%s战绩已隐藏!", bind.getServer(), bind.getAccountName()))
                                .append(quoteReply)
                                .build();
                        sender.sendMessage(chain);
                        return;
                    }

                    shipDataObj.update(AccountIdToAccountInfo(bind.getAccountId(), bind.getServer()));

                    MessageChainBuilder messageChainBuilder = new MessageChainBuilder()
                            .append(String.format("[%s]%s:", bind.getServer(), bind.getAccountName()))
                            .append("\r");

                    singleShipInfoPack(messageChainBuilder, shipDataObj, pr, Type.normal);

                    messList = new ForwardMessageBuilder(sender.getFromEvent().getSender())
                            .add(sender.getBot(), messageChainBuilder.build())
                            .add(sender.getBot(), messageLinePackEnd(new MessageChainBuilder(), null));

//                    chain = messageLinePackEnd(messageChainBuilder, quoteReply);
                }

                ForwardMessage build = messList.build();

                ForwardMessage record = new ForwardMessage(
                        preInfo.build().getPreview(),
                        String.format("[%s]%s", bind.getServer() == ApiConfig.Server.NA
                                || bind.getServer() == ApiConfig.Server.com
                                ? "NA"
                                : bind.getServer(), bind.getAccountName()),
                        "个人pr",
                        build.getSource(),
                        build.getSummary(),
                        build.getNodeList());

                sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
                sender.sendMessage(record);
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
    public void yesterdayPr(CommandSenderOnMessage sender, String stringType) throws InterruptedException {
        Type type = StringToType(stringType);
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());

        MessageChain chain = null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder message = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        int count = 0;
        StringBuilder exception = new StringBuilder();
        while (count <= ApiConfig.reTry) {
            try {
                Bind bind = findAccountId(qq);
                if (bind == null) {
                    chain = bindErrorPack(quoteReply);
                    sender.sendMessage(chain);
                    return;
                } else {
                    List<ShipDataObj> shipDataObjs = accountRecordAt(bind.getAccountId(), bind.getServer(), 1);
                    messagePackPr(shipDataObjs, messageItem, messageList, sender, bind, message, type);
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

    @SubCommand({"昨日战绩", "ypr"})
    @Description("查询自己昨天pr")
    public void yesterdayPr(CommandSenderOnMessage sender) throws InterruptedException {
        yesterdayPr(sender, "random");
    }

    @SubCommand({"时间段", "dpr"})
    @Description("查询时间段内的pr")
    public void datePr(CommandSenderOnMessage sender, String from, String to) throws InterruptedException {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        String qq = String.valueOf(sender.getFromEvent().getSender().getId());
        MessageChain chain = null;
        int count = 0;
        StringBuilder exception = new StringBuilder();

        while (count <= ApiConfig.reTry) {
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
                            singleShipInfoPack(messageChainBuilder, shipDataObj, shipDataObj.getPR(), Type.normal);
                        }
                    } else {
                        messageChainBuilder
                                .append("空")
                                .append("\r");
                    }
                    chain = messageLinePackEnd(messageChainBuilder, quoteReply);
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
        while (count <= ApiConfig.reTry) {
            try {
                JsonNode jsonNode = ShipNameToShipId(shipName);
                if (jsonNode == null) {
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

                String name = jsonNode.get("zh").asText();
                String shipId = jsonNode.get("shipId").asText();
                JsonNode jsonNode1 = ShipToExpected(shipId);
                String date = DateUtil.format(DateUtil.date(ShipExpected.get("time").asLong() * 1000), "YYYYMMdd");

                String[] average_damage_dealts = jsonNode1.get("average_damage_dealt").asText().split("\\.");
                String[] average_frags = jsonNode1.get("average_frags").asText().split("\\.");
                String[] win_rates = jsonNode1.get("win_rate").asText().split("\\.");

                chain = messageLinePackEnd(new MessageChainBuilder()
                        .append("服务器船只数据:")
                        .append("\r")
                        .append(String.format("数据更新时间:%s", date))
                        .append("\r")
                        .append(name)
                        .append("\r")
                        .append(String.format("均伤:%s.%s", average_damage_dealts[0], average_damage_dealts[1].substring(0, 2)))
                        .append("\r")
                        .append(String.format("均击杀:%s.%s", average_frags[0], average_frags[1].substring(0, 2)))
                        .append("\r")
                        .append(String.format("胜率:%s.%s%%", win_rates[0], win_rates[1].substring(0, 2)))
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

    @SubCommand({"help", "h"})
    @Description("防止某些憨憨非要在w里输入help")
    public void help(CommandSenderOnMessage sender){
        Help.INSTANCE.help(sender);
    };

    @SubCommand({"ah", "admin"})
    @Description("防止某些憨憨非要在w里输入help")
    public void helpAdmin(CommandSenderOnMessage sender){
        Help.INSTANCE.AdminHelp(sender);
    };
}
