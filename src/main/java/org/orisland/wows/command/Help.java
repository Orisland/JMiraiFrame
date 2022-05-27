package org.orisland.wows.command;

import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.orisland.WowsPlugin;

public class Help  extends JCompositeCommand {

    public static final Help INSTANCE = new Help();

    public Help() {
        super(WowsPlugin.INSTANCE, "wws-help", new String[]{"wh"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"帮助", "help", "h"})
    @Description("查询自己综合pr")
    public void help(CommandSenderOnMessage sender){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());

        MessageChain chain = new MessageChainBuilder()
                .append("wb(wws-bind) bn(bindname) [用户昵称] [区服](eu/asia/na/ru) 绑定用户")
                .append("\r")
                .append("wb(wws-bind) ub(updatebind/更新绑定) [用户昵称] [区服](eu/asia/na/ru) 更新绑定")
                .append("\r")
                .append("w(wws) me(today/今日/recent) 查询自己的当日随机战绩")
                .append("\r")
                .append("w(wws) all(al/全部) 查询自己当日的随机，排位综合战绩")
                .append("\r")
                .append("w(wws) rank(软壳/排位/pw) 查询自己当日战绩")
                .append("\r")
                .append("w(wws) ypr [类型](rank(排位)/all(全部)/random(随机)) 查询自己的昨天战绩，不输入默认随机")
                .append("\r")
                .append("w(wws) ts(todayship,今日单船) [船名] 显示今天打的指定船水表")
                .append("\r")
                .append("w(wws) spp [用户昵称] [区服] 查询指定用户综合pr")
                .append("\r")
                .append("w(wws) sps(searchShipname) [船名] 查询自己指定船的pr")
                .append("\r")
                .append("w(wws) pr(战绩,水表) 查询自己账号的综合pr")
                .append("\r")
                .append("w(wws) es(预期,expship) [船名] 查询服务器指定船只数据")
                .append("\r")
                .append("wh(wws-help) pr 等级划分")
                .append("不稳定的命令（可能会出现意想不到的问题）：")
                .append("w(wws) dpr(时间段) [from](YYYYMMdd) [to](YYYYMMdd) 查询时间段内自己的pr,第一次绑定无效")
                .append("\r")
                .append(quoteReply)
                .build();

        sender.sendMessage(chain);
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
}
