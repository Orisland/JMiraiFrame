package org.orisland;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.mamoe.mirai.console.command.java.JCompositeCommand;

/**
 * @Author: zhaolong
 * @Time: 00:05
 * @Date: 2021年07月30日 00:05
 **/
public class Mycommand extends JCompositeCommand {
    public static final Mycommand INSTANCE = new Mycommand();
    public static final ObjectMapper mapper =  new ObjectMapper();

    private Mycommand(){
        super(Plugin.INSTANCE, "pic", new String[]{"p"}, Plugin.INSTANCE.getParentPermission());
    }

    @SubCommand
    @Description("test")
    public void function(){

    }
}
