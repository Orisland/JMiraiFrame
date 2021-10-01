package org.orisland;

import Tool.HttpClient;
import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.Image;

/**
 * @Author: zhaolong
 * @Time: 17:55
 * @Date: 2021年07月30日 17:55
 **/
public class ScanImg extends JCompositeCommand {
    public static final ScanImg INSTANCE = new ScanImg();

    public ScanImg(){
        super(Plugin.INSTANCE, "搜图", new String[]{"findpic"}, Plugin.INSTANCE.getParentPermission());
    }

    @SubCommand
    public void scanImage(CommandSender sender, Image image){
        String img = HttpClient.urlProxy(Image.queryUrl(image));
        
    }
}
