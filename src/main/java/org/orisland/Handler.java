package org.orisland;

import Tool.HttpClient;
import com.fasterxml.jackson.databind.JsonNode;
import net.mamoe.mirai.console.command.CommandManager;
import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.console.command.FriendCommandSenderOnMessage;

import net.mamoe.mirai.console.command.MemberCommandSenderOnMessage;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;

import net.mamoe.mirai.event.events.MessageEvent;

import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;

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

        FriendCommandSenderOnMessage to = new FriendCommandSenderOnMessage(event);
        CommandManager.INSTANCE.executeCommand(to, event.getMessage(),false);

        if (event.getMessage().contentToString().contains("找图")){
            Image image = null;
            MessageChain chain1 = event.getMessage();
            for (Object obj : chain1){
                if (obj instanceof Image){
                    image = (Image) obj;
                    break;
                }
            }


            if (image == null) {
                System.out.println("图片为空，退出。");
                return ListeningStatus.LISTENING;
            }


            JsonNode jsonNode= HttpClient.apiGetByJson("https://saucenao.com/search.php",
                    "api_key=360f83817bf28a2d97e099c590490bb1b655d36f",
                    "numres=5",
                    "output_type=2",
                    "testmode=1",
                    "url="+Image.queryUrl(image));


            System.out.println(jsonNode.get("results").get(0));
            String similarity = jsonNode.get("results").get(0).get("header").get("similarity").asText();
            String imgurl = jsonNode.get("results").get(0).get("header").get("thumbnail").asText();
            ExternalResource ex = ExternalResource.Companion.create(HttpClient.getUrlByByte(imgurl));
            Image img = ExternalResource.uploadAsImage(ex, event.getSubject());
            System.out.println("upload finish");

            MessageChain chain = new MessageChainBuilder()
                    .append(similarity)
                    .append(img)
                    .build();

            event.getSubject().sendMessage(chain);
            ex.close();
        }

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus groupListener(GroupMessageEvent event) throws IOException {

        CommandSenderOnMessage to = new MemberCommandSenderOnMessage(event);
        CommandManager.INSTANCE.executeCommand(to, event.getMessage(),false);

        return ListeningStatus.LISTENING;
    }

    @EventHandler
    public ListeningStatus bothEven(MessageEvent event) throws IOException {


        if (event.getMessage().contentToString().contains("鉴图")){
            System.out.println("catch use!");
            Image image = null;
            MessageChain chain1 = event.getMessage();
            for (Object obj : chain1){
                if (obj instanceof Image){
                    image = (Image) obj;
                    break;
                }
            }

            if (image == null) {
                System.out.println("图片为空，退出。");
                return ListeningStatus.LISTENING;
            }

            JsonNode jsonNode = chargePic(image);
            if (jsonNode == null){
                event.getSubject().sendMessage("错误，发现未定义的图片访问链接头！");
            }
            System.out.println(jsonNode);

            MessageChain chain = new MessageChainBuilder().append("总色块:"+jsonNode.get("total").asText()+"\n")
                    .append("灰色块:"+jsonNode.get("grey").asText()+"\n")
                    .append("彩色块:"+jsonNode.get("color").asText()+"\n")
                    .append("彩色占比:"+jsonNode.get("per").asText()+"\n")
                    .append("结论:"+jsonNode.get("res").asText()).build();

            event.getSubject().sendMessage(chain);
        } else if (event.getMessage().contentToString().contains("找图")){
            Image image = null;
            MessageChain chain1 = event.getMessage();
            event.getSubject().sendMessage("正在尝试搜索!");
            for (Object obj : chain1){
                if (obj instanceof Image){
                    image = (Image) obj;
                    break;
                }
            }

            if (image == null) {
                System.out.println("图片为空，退出。");
                return ListeningStatus.LISTENING;
            }


            JsonNode jsonNode= HttpClient.apiGetByJson("https://saucenao.com/search.php",
                    "api_key=360f83817bf28a2d97e099c590490bb1b655d36f",
                    "numres=5",
                    "output_type=2",
                    "testmode=1",
                    "url="+Image.queryUrl(image));

            System.out.println(jsonNode.get("results").get(0));
            String similarity = jsonNode.get("results").get(0).get("header").get("similarity").asText();
            String imgurl = jsonNode.get("results").get(0).get("header").get("thumbnail").asText();
            String title = jsonNode.get("results").get(0).get("data").get("title").asText();
            String pid = jsonNode.get("results").get(0).get("data").get("pixiv_id").asText();
            String member_name = jsonNode.get("results").get(0).get("data").get("member_name").asText();



            JsonNode node = HttpClient.getUrlByJson("https://orislandapi.herokuapp.com/api/pixiv/illust?id=" + pid);
            String midpic = node.get("illust").get("image_urls").get("medium").asText();
            midpic = HttpClient.pixyProxy(midpic);
            ExternalResource ex = ExternalResource.Companion.create(HttpClient.getUrlByByte(midpic));
            Image img = ExternalResource.uploadAsImage(ex, event.getSubject());
            System.out.println("upload finish");

            MessageChain chain = new MessageChainBuilder()
                    .append(img)
                    .append("\n")
                    .append("相似度:" + similarity +"%")
                    .append("\n")
                    .append("标题:" + title)
                    .append("\n")
                    .append("id:" + pid)
                    .append("\n")
                    .append("作者:" + member_name)
                    .append("\n")
                    .append("地址:" + midpic)
                    .build();

            event.getSubject().sendMessage(chain);
            ex.close();
        }else if (event.getMessage().contentToString().contains("提图")){
            String id = event.getMessage().toString().split("提图")[1];
            event.getSubject().sendMessage("正在尝试提取图片!");

            String url = "https://pixiv.cat/"+id+".jpg";
            System.out.println(url);
            ExternalResource ex = ExternalResource.Companion.create(HttpClient.getUrlByByte(url));
            Contact.Companion.sendImage(event.getSubject(), ex);
            ex.close();

        }

        return ListeningStatus.LISTENING;
    }


}
