package org.orisland;

import Tool.HttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.FriendCommandSenderOnMessage;

import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import net.mamoe.mirai.event.events.MessageEvent;

import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.orisland.wows.Playerpackage;

import java.io.IOException;

/**
 * @Author: zhaolong
 * @Time: 3:07 下午
 * @Date: 2021年07月03日 15:07
 **/
public class Handler extends SimpleListenerHost {
    @EventHandler
    public ListeningStatus friendListener(FriendMessageEvent event) throws IOException {
        

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus groupListener(GroupMessageEvent event) throws IOException {
        String msg = event.getMessage().contentToString();
        

        if (msg.indexOf("欧服水表") == 0){
            if(msg.split( " ").length != 2){
                event.getGroup().sendMessage("命令格式错误，请修正。");
            }
            event.getGroup().sendMessage("正在尝试获取数据~请稍后~");
            Playerpackage playerpackage = new Playerpackage(msg,0);
            String atqq = "[@"+event.getSender().getNick()+"]来了来了~" + "\r";
            event.getGroup().sendMessage(atqq + playerpackage.getSpackage());
            System.out.println(playerpackage.getPic());

        }else if(msg.indexOf("亚服水表") == 0) {
            if (msg.split(" ").length != 2) {
                event.getGroup().sendMessage("命令格式错误，请修正。");

            }
            event.getGroup().sendMessage("正在尝试获取数据~请稍后~");
            Playerpackage playerpackage = new Playerpackage(msg, 1);
            String atqq = "[@"+event.getSender().getNick()+"]来了来了~" + "\r";
            event.getGroup().sendMessage(atqq + playerpackage.getSpackage());
            System.out.println(playerpackage.getPic());
        }
//        }else if (msg.indexOf("欧服表单")==0){
//            if (msg.split(" ").length < 2){
//                Core.sendGroupMessages(selfQQ,fromGroup,"命令格式错误，请修正。",0);
//                Core.sendGroupMessages(selfQQ,fromGroup,"0：全图模式  1：区域模式",0);
//                return;
//            }
//            Core.sendGroupMessages(selfQQ,fromGroup,"正在尝试获取数据~请稍后~",0);
//            String base64 = Chart.chart(msg,0);
//            if (base64.equals("?")){
//                return;
//            }
//            String atqq = "[@"+fromQQ+"]来了来了~" + "\r";
//            Core.sendGroupMessagesPicText(selfQQ,fromGroup,atqq + "[pic:"+base64+"]",0);
//        }else if (msg.indexOf("亚服表单")==0){
//            if (msg.split(" ").length < 2){
//                Core.sendGroupMessages(selfQQ,fromGroup,"命令格式错误，请修正。",0);
//                Core.sendGroupMessages(selfQQ,fromGroup,"0：全图模式  1：区域模式",0);
//                return;
//            }
//            Core.sendGroupMessages(selfQQ,fromGroup,"正在尝试获取数据~请稍后~",0);
//            String base64 = Chart.chart(msg,1);
//            if (base64.equals("?")){
//                return;
//            }
//            String atqq = "[@"+fromQQ+"]来了来了~" + "\r";
//            Core.sendGroupMessagesPicText(selfQQ,fromGroup,atqq + "[pic:"+base64+"]",0);
//        }else if (msg.equals("水表功能")){
//            Core.sendGroupMessages(selfQQ,fromGroup,"[pic,hash=B9433B085F1C592C29137BDE4FEAD556]",0);
//        }else if (msg.equals("表单功能")){
//            Core.sendGroupMessages(selfQQ,fromGroup,"[pic,hash=9640C8A87A25B56B6F0BC0C354BD3944]",0);
//        }else if (msg.equals("wows功能")){
//            Core.sendGroupMessages(selfQQ,fromGroup,"输入“水表功能”获取水表功能帮助！\r输入“表单功能”获取水表功能帮助！",0);


        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus bothEven(MessageEvent event) throws IOException {

        return ListeningStatus.LISTENING;
    }


}
