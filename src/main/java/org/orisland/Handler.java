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

import java.io.File;
import java.io.IOException;

import static Tool.blackWhite.chargePic;

/**
 * @Author: zhaolong
 * @Time: 3:07 下午
 * @Date: 2021年07月03日 15:07
 **/
public class Handler extends SimpleListenerHost {
    @EventHandler
    public ListeningStatus friendListener(FriendMessageEvent event) throws IOException {
        if (event.getMessage().equals("谢谢")){
            event.getSender().sendMessage("~");
        }

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus groupListener(GroupMessageEvent event) throws IOException {
        if (event.getMessage().equals("谢谢")){
            event.getSender().sendMessage("~");
        }

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus bothEven(MessageEvent event) throws IOException {

        return ListeningStatus.LISTENING;
    }


}
