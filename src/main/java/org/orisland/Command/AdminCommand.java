package org.orisland.Command;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import org.orisland.Plugin;

import static org.orisland.Config.SplitChar;
import static org.orisland.DataInit.initJeffJokeContent;

/**
 * @Author: zhaolong
 * @Time: 00:05
 * @Date: 2021年07月30日 00:05
 **/

@Slf4j
public class AdminCommand extends JCompositeCommand {
    public static final AdminCommand INSTANCE = new AdminCommand();

    private AdminCommand(){
        super(Plugin.INSTANCE, "JeffJokeController", new String[]{"jc"}, Plugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"reload"})
    @Description("重载本地joke")
    public void function(CommandSenderOnMessage sender){
        initJeffJokeContent();
    }

    @SubCommand({"reSplit", "rs"})
    @Description("指定分隔符，临时修改")
    public void updateSplit(CommandSenderOnMessage sender, String split){
        SplitChar = split;
    }
}
