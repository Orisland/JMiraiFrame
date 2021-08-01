package org.orisland;

import net.mamoe.mirai.console.command.CommandOwner;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JSimpleCommand;
import net.mamoe.mirai.console.permission.Permission;
import net.mamoe.mirai.console.permission.PermissionId;
import net.mamoe.mirai.contact.User;
import org.jetbrains.annotations.NotNull;

/**
 * @Author: zhaolong
 * @Time: 00:05
 * @Date: 2021年07月30日 00:05
 **/
public class Mycommand extends JSimpleCommand {
    public static final Mycommand INSTANCE = new Mycommand();

    private Mycommand(){
        super(Plugin.INSTANCE, "test", new String[]{"s"}, Plugin.INSTANCE.getParentPermission());
        setDescription("这是一个测试command");
    }

    @Handler
    public void onCommand(CommandSender sender, String mes){
        sender.sendMessage("wocao ");
    }
}
