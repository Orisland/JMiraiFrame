package org.orisland;

import net.mamoe.mirai.console.MiraiConsole;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.CompositeCommand;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Image;

import java.awt.*;

/**
 * @Author: zhaolong
 * @Time: 17:55
 * @Date: 2021年07月30日 17:55
 **/
public class comCommand extends JCompositeCommand {
    public static final comCommand INSTANCE = new comCommand();

    public comCommand(){
        super(Plugin.INSTANCE, "compose", new String[]{"com"}, Plugin.INSTANCE.getParentPermission());
    }

    @SubCommand
    public void f1(CommandSender sender, User user){
        sender.sendMessage("test");
        user.sendMessage(user.getNick());
        sender.sendMessage("f1");
    }
}
