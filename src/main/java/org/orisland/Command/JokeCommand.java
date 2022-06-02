package org.orisland.Command;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage;
import net.mamoe.mirai.console.command.java.JCompositeCommand;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;
import org.orisland.Plugin;

import static org.orisland.Config.JeffJoke;
import static org.orisland.Config.SplitChar;
import static org.orisland.JokeUtil.addJoke;
import static org.orisland.JokeUtil.getJokeRandom;

/**
 * @Author: zhaolong
 * @Time: 00:05
 * @Date: 2021年07月30日 00:05
 **/

@Slf4j
public class JokeCommand extends JCompositeCommand {
    public static final JokeCommand INSTANCE = new JokeCommand();

    private JokeCommand(){
        super(Plugin.INSTANCE, "JeffJoke", new String[]{"j", "j"}, Plugin.INSTANCE.getParentPermission());
    }

    @SubCommand({"joke", "myjoke", "mj"})
    @Description("获取一条以自己名字填充的jeff joke")
    public void function(CommandSenderOnMessage sender){
        getJoke(sender, 1);
    }

    @SubCommand({"joke", "myjoke", "me", "j"})
    @Description("获取一条以自己名字填充的jeff joke")
    public void getJoke(CommandSenderOnMessage sender, int size){
        if (size > JeffJoke.size())
            size = JeffJoke.size();

        String jokeRandom = getJokeRandom(size, sender.getName());

        try {
            sender.sendMessage(String.format(jokeRandom, sender.getName()));
        }catch (Exception e){
            e.printStackTrace();
            log.warn("出错的joke:{}", jokeRandom);
            sender.sendMessage(String.format("出错啦！\n%s", e.getMessage()));
        }
    }

    @SubCommand({"addJoke", "aj"})
    @Description("添加joke，必须含有指定字符!")
    public void AddJoke(CommandSenderOnMessage sender, String joke){
        if (addJoke(joke)){
            sender.sendMessage("Joke添加完成！");
        }else {
            sender.sendMessage(String.format("Joke添加失败，请检查添加字符串是否符合>%s<", SplitChar));
        }
    }

    @SubCommand({"diyjoke", "dj"})
    @Description("diy一个joke")
    public void getJoke(CommandSenderOnMessage sender, String content, int size){
        String jokeRandom = getJokeRandom(size, content);
        try {
            sender.sendMessage(String.format(jokeRandom, sender.getName()));
        }catch (Exception e){
            e.printStackTrace();
            log.warn("出错的joke:{}", jokeRandom);
            sender.sendMessage(String.format("出错啦！\n%s", e.getMessage()));
        }
    }

    @SubCommand({"diyjoke", "dj"})
    @Description("diy一个joke")
    public void getJoke(CommandSenderOnMessage sender, String content){
        getJoke(sender, content, 1);
    }
}
