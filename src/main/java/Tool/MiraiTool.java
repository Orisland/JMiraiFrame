package Tool;

import net.mamoe.mirai.console.command.CommandSenderOnMessage;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import org.orisland.Plugin;

import java.io.File;
import java.io.InputStream;

/**
 * @Author: zhaolong
 * @Time: 11:47
 * @Date: 2021年10月04日 11:47
 **/
public class MiraiTool {
    /**
     * 从url中获取img并构造img,ex为临时
     * @param sender    发送者
     * @return
     */
    public static Image getImg(CommandSenderOnMessage sender, String pic){
        try {
            InputStream resourceAsStream = Plugin.class.getClassLoader().getResourceAsStream(File.separator + pic);
            assert resourceAsStream != null;
            return net.mamoe.mirai.contact.Contact.uploadImage((Contact) sender, resourceAsStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
