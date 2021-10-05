package Tool;

import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

/**
 * @Author: zhaolong
 * @Time: 11:47
 * @Date: 2021年10月04日 11:47
 **/
public class MiraiTool {

    /**
     * 从url中获取img并构造img,ex为临时
     * @param sender    发送者
     * @param url   图片链接
     * @return
     */
    public Image getImg(CommandSenderOnMessage sender, String url){
        try {
            return ExternalResource.uploadAsImage(ExternalResource.createAutoCloseable(ExternalResource.create(HttpClient.getUrlByByte(url))), sender.getSubject());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
