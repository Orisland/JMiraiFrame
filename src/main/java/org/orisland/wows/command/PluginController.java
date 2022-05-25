package org.orisland.wows.command;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.orisland.WowsPlugin;

import java.util.Date;

import static org.orisland.wows.DataInit.init;
import static org.orisland.wows.dataPack.PlayerData.updateAccountLocalDataAuto;

@Slf4j
public class PluginController extends JCompositeCommand {
    public static final PluginController INSTANCE = new PluginController();

    public PluginController() {
        super(WowsPlugin.INSTANCE, "wws-controller", new String[]{"wc"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"reload", "re"})
    @Description("重载配置")
    public void reloadConfig(CommandSenderOnMessage sender){
        init();
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        MessageChain chain = null;
        chain = new MessageChainBuilder()
                .append(new PlainText("wows插件配置文件重载完成！"))
                .append(quoteReply)
                .build();
        sender.sendMessage(chain);
    }

    @SubCommand({"refresh", "redata"})
    @Description("刷新绑定的玩家数据")
    public void refreshData(CommandSenderOnMessage sender){
        int count = 0;
        while (count <= 10){
            try {
                updateAccountLocalDataAuto();
                QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
                MessageChain chain = null;
                chain = new MessageChainBuilder()
                        .append(String.format("绑定玩家%s数据信息重载完成！", DateUtil.format(new Date(), "YYYYMMdd")))
                        .append(quoteReply)
                        .build();
                sender.sendMessage(chain);
                return;
            }catch (Exception e){
                log.warn("访问第{}次出错！",++count);
            }
        }
        log.error("{}更新错误！", DateUtil.format(new Date(), "YYYYMMdd"));
    }
}
