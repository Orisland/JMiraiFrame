package org.orisland.wows.command;

import cn.hutool.core.date.DateUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.*;
import org.jsoup.helper.DataUtil;
import org.orisland.WowsPlugin;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.DataInit;

import static org.orisland.wows.dataPack.DataHandler.addForwardLine;
import static org.orisland.wows.dataPack.DataHandler.addForwardPack;
import static org.orisland.wows.dataPack.StringToMeaningful.isAdmin;

public class Help  extends JCompositeCommand {

    public static final Help INSTANCE = new Help();

    public Help() {
        super(WowsPlugin.INSTANCE, "wws-help", new String[]{"wh"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"帮助", "help", "h"})
    @Description("用户版插件帮助")
    public void help(CommandSenderOnMessage sender){
        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder preMessage = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        Bot bot = sender.getBot();

        addForwardLine(preMessage, messageItem, bot, "WowsHelper使用帮助");
        addForwardLine(preMessage, messageItem, bot, "作者:Orisland");
        addForwardLine(preMessage, messageItem, bot, "[EU]2434");

        MessageChainBuilder preInfo = new MessageChainBuilder().append("正确的插件使用顺序：绑定->查询！\n" +
                "[ ]内容为用户自行填入，( )为[ ]的待选项\n" +
                "[ ]后为空则由用户输入！");

//        绑定功能
        String[] binds = new String[4];
        String[] bindPre = {"绑定说明书", "在任何情况下都应该先绑定"};
        binds[0] = "wb bn(bindname) [用户昵称] [区服](eu/asia/na/ru)\n" +
                "绑定用户 \n" +
                "例如： wb bn orisland_ex eu";
        binds[1] = "wb ub(updatebind/更新绑定) [用户昵称] [区服](eu/asia/na/ru)\n" +
                "更新绑定 \n" +
                "例如： wb bn orisland_ex eu";
        binds[2] = "wb db(jb,解绑)\n" +
                "解除自己的账号绑定，注意该操作会导致数据更新停止！\n" +
                "例如在已绑定的情况下：wb db";
        binds[3] = "wb me\n" +
                "查看自己绑定的账号！\n" +
                "例如在已绑定的情况下：wb me";
        ForwardMessage bindFunction = addForwardPack(binds, "绑定", "绑定说明书", bindPre, sender, bot);

//        查询功能
        String[] accountPre = {"查询说明书", "在任何情况下都应该绑定后使用!"};
        String[] accounts = new String[10];
        accounts[0] = "请注意：今日战绩，昨日战绩，这些功能仅在绑定的那一刻开始计算！\n" +
                "您不能在刚绑定的时候立即查询您的今日战绩和昨日战绩！";
        accounts[1] = "w(wws) me(today/今日/recent)\n" +
                "查询自己的当日随机战绩\n" +
                "例如：w me";
        accounts[2] = "w(wws) all(al/全部)\n" +
                "查询自己当日的随机，排位综合战绩\n" +
                "例如：w all";
        accounts[3] = "w(wws) rank(软壳/排位/pw)\n" +
                "查询自己当日战绩 \n" +
                "例如：w rank";
        accounts[4] = "w(wws) ypr [类型](rank(排位)/all(全部)/random(随机))\n" +
                "查询自己的昨天战绩，类型为空默认随机\n" +
                "例如：w ypr rank";
        accounts[5] = "w(wws) ts(todayship,今日单船) [船名(汉语，英语，同音字，不允许出现错别字和空格)]\n" +
                "显示今天打的指定船水表\n" +
                "例如：w ts 蒙大拿";
        accounts[6] = "w(wws) spp [用户昵称] [区服]\n" +
                "查询指定用户综合pr\n" +
                "例如：w spp orisland_ex eu";
        accounts[7] = "w(wws) sps(searchShipname) [船名]\n" +
                "查询自己指定船的pr\n" +
                "例如：w sps 蒙大拿";
        accounts[8] = "w(wws) pr(战绩,水表)\n" +
                "查询自己账号的综合pr\n" +
                "例如：w pr";
        accounts[9] = "w(wws) es(预期,expship) [船名]\n" +
                "查询服务器指定船只数据\n" +
                "例如：w es 蒙大拿";
        ForwardMessage accountFunction = addForwardPack(accounts, "查询", "查询说明书", accountPre, sender, bot);

        String[] exPre = {"额外查询", "查询一些额外的数据。"};
        String[] exs = new String[1];
        exs[0] = "wh(wws-help) pr\n" +
                "等级划分\n" +
                "例如：wh pr";
        ForwardMessage helpFunction = addForwardPack(exs, "额外", "额外查询说明书", exPre, sender, bot);

        String[] expsPre = {"实验说明书", "这些命令不稳定，不应该过多使用！"};
        String [] exps = new String[1];
        exps[0] = "w(wws) dpr(时间段) [from](YYYYMMdd) [to](YYYYMMdd)\n" +
                "查询时间段内自己的pr\n" +
                "例如：w dpr 20220525 20220531";
        ForwardMessage expFunction = addForwardPack(exps, "实验", "实验说明书", expsPre, sender, bot);

        int UnixTime = ((Long)DateUtil.currentSeconds()).intValue();

        messageList.add(bot, preInfo.build(), ++UnixTime);
        messageList.add(bot, bindFunction, ++UnixTime);
        messageList.add(bot, accountFunction, ++UnixTime);
        messageList.add(bot, helpFunction, ++UnixTime);
        messageList.add(bot, expFunction, ++UnixTime);

        if (isAdmin(sender))
            messageList.add(bot, AdminHelp(sender), ++UnixTime);

        ForwardMessage build = messageList.build();

        ForwardMessage record = new ForwardMessage(
                preMessage.build().getPreview(),
                "WowsHelper使用说明",
                "WowsHelper使用说明",
                build.getSource(),
                build.getSummary(),
                build.getNodeList());

        sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
        sender.sendMessage(record);
    }

    @SubCommand({"pr"})
    @Description("查看等级划分pr")
    public void pr(CommandSenderOnMessage sender) {
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        MessageChain chain = new MessageChainBuilder()
                .append("还需努力：0~750")
                .append("\r")
                .append("低于平均:750~1100")
                .append("\r")
                .append("平均水平:1100~1350")
                .append("\r")
                .append("好:1350~1550")
                .append("\r")
                .append("很好:1550~1750")
                .append("\r")
                .append("非常好:1750~2100")
                .append("\r")
                .append("大佬平均:2100~2450")
                .append("\r")
                .append("神佬平均:2450~5000")
                .append("\r")
                .append("Easter eggs！:<9999")
                .append("\r")
                .append("您吓到我了:>9999")
                .append("\r")
                .append("=========")
                .append("\r")
                .append("↑▲p▲i▲全速下沉▲n▲g▲↓")
                .append(quoteReply)
                .build();
        sender.sendMessage(chain);
    }

    @SubCommand({"chelp", "ah", "adminhelp", "admin"})
    public ForwardMessage AdminHelp(CommandSenderOnMessage sender){
        if (!isAdmin(sender))
            return null;

        ForwardMessageBuilder messageList = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        ForwardMessageBuilder preMessage = new ForwardMessageBuilder(sender.getFromEvent().getSender());
        MessageChainBuilder messageItem = new MessageChainBuilder();

        Bot bot = sender.getBot();

        addForwardLine(preMessage, messageItem, bot, "WowsHelper管理员说明书");
        addForwardLine(preMessage, messageItem, bot, "作者:Orisland");
        addForwardLine(preMessage, messageItem, bot, "[EU]2434");

        String[] admins = new String[4];
        String[] adminPre = {"管理员说明书", "包含数据相关高危指令，请谨慎使用！"};
        admins[0] = "wc(wws-controller) re(reload)\n" +
                "重载本地配置\n" +
                "在管理员账户下：wc re";
        admins[1] = "wc(wws-controller) redata(refresh)\n" +
                "安全的刷新玩家数据 -> 等同于每日玩家数据的刷新\n" +
                "在管理员账户下：wc redata";
        admins[2] = "wc(wws-controller) redataF(refreshForce)\n" +
                "强制（危险的）刷新玩家数据，该操作会导致所有玩家丢失刷新前的今日数据，请谨慎使用\n" +
                "在管理员账户下：wc redataF";
        admins[3] = "wc(wws-controller) reDFP(refreshForceP)\n" +
                "强制（危险的）刷新指定玩家数据，该操作会导致指定玩家丢失刷新前的今日数据，请谨慎使用\n" +
                "在管理员账户下：wc reDFP";
        ForwardMessage adminFunction = addForwardPack(admins, "管理员", "管理员说明书", adminPre, sender, bot);

        int UnixTime = ((Long)DateUtil.currentSeconds()).intValue();
        messageList.add(bot, adminFunction, ++UnixTime);
        ForwardMessage build = messageList.build();

        ForwardMessage record = new ForwardMessage(
                preMessage.build().getPreview(),
                "WowsHelper管理员使用说明",
                "WowsHelper管理员使用说明",
                build.getSource(),
                build.getSummary(),
                build.getNodeList());

        sender.sendMessage(new At(sender.getFromEvent().getSender().getId()));
        sender.sendMessage(record);

        return adminFunction;
    }
}
