package org.orisland.wows.command;

import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;
import org.orisland.WowsPlugin;

import static org.orisland.wows.DataInit.init;

public class PluginController extends JCompositeCommand {
    public static final PluginController INSTANCE = new PluginController();

    public PluginController() {
        super(WowsPlugin.INSTANCE, "wws", new String[]{"w"}, WowsPlugin.INSTANCE.getParentPermission());
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


}
