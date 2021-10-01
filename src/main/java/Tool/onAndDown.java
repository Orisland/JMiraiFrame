package Tool;

import net.mamoe.mirai.console.command.CommandSender;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.*;
import net.mamoe.mirai.event.events.GroupEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import java.util.Date;

/**
 * @Author: zhaolong
 * @Time: 20:21
 * @Date: 2021年09月21日 20:21
 **/
public class onAndDown implements Runnable{
    CommandSender sender;

    public onAndDown(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        EventChannel eventChannel = GlobalEventChannel.INSTANCE.filterIsInstance(GroupMessageEvent.class)
                .filter(groupEvent -> groupEvent.getGroup().getId() == sender.getSubject().getId());
//        eventChannel.subscribeOnce(GroupMessageEvent.class, event->{
//
//        })
        sender.sendMessage("结束");
    }
}
