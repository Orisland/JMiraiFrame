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
import org.orisland.wows.ApiConfig;

import java.util.Date;

import static org.orisland.wows.DataInit.init;
import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.StringToMeaningful.isAdmin;

@Slf4j
public class PluginController extends JCompositeCommand {
    public static final PluginController INSTANCE = new PluginController();

    public PluginController() {
        super(WowsPlugin.INSTANCE, "wws-controller", new String[]{"wc"}, WowsPlugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"reload", "re"})
    @Description("重载配置")
    public void reloadConfig(CommandSenderOnMessage sender){
        if (!isAdmin(sender))
            return;

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
        if (!isAdmin(sender))
            return;

        int count = 0;
        while (count <= 10){
            try {
                updateAccountLocalDataAuto(false);
                QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
                MessageChain chain = null;
                chain = new MessageChainBuilder()
                        .append(String.format("所有绑定玩家%s数据信息重载完成！", DateUtil.format(new Date(), "YYYYMMdd")))
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

    @SubCommand({"refreshForce", "redataF"})
    @Description("强制以覆盖的形式刷新绑定的玩家数据，危险操作，该操作会让所有绑定玩家丢失刷新前的今日数据！")
    public void refreshDataForce(CommandSenderOnMessage sender){
        if (!isAdmin(sender))
            return;

        int count = 0;
        while (count <= 10){
            try {
                updateAccountLocalDataAuto(true);
                QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
                MessageChain chain = null;
                chain = new MessageChainBuilder()
                        .append(String.format("所有绑定玩家%s数据信息重载完成！", DateUtil.format(new Date(), "YYYYMMdd")))
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

    @SubCommand({"refreshForceP", "reDFP"})
    @Description("覆盖数据，危险操作，该操作会让指定玩家丢失刷新前的今日数据！")
    public void refreshDataForce(CommandSenderOnMessage sender, String qq){
        QuoteReply quoteReply = new QuoteReply(sender.getFromEvent().getSource());
        MessageChain chain = null;

        if (!isAdmin(sender))
            return;

        org.orisland.wows.doMain.Bind bind = findAccountId(qq);

        if (bind == null){
            chain = new MessageChainBuilder()
                    .append("该用户未绑定任何账号！")
                    .append(quoteReply)
                    .build();
        }else {
            saveAccountShipInfo(bind.getAccountId(), bind.getServer(), true);
            chain = new MessageChainBuilder()
                    .append(String.format("[%s]%s-%s,强制数据更新已完成！",
                            bind.getServer() == ApiConfig.Server.com ? "NA" : bind.getServer(),
                            bind.getAccountName(),
                            bind.getAccountId()))
                    .append(quoteReply)
                    .build();
        }

        sender.sendMessage(chain);
    }
}
