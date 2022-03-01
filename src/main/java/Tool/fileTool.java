package Tool;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
public class fileTool {
    /**
     * 创建文件夹
     * @param path  文件路径
     * @return  返回是否创建成功
     */
    public static boolean newFile(String path){
        File file = new File(path);
        return file.mkdir();
    }

    /**
     * 保存json到玩家文件夹
     * @param userData
     * @return
     */
    public static boolean saveFile(JsonNode userData, String url, String name){
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(url + File.separator + name);
            log.info(url + File.separator + name);
            fileWriter.write(userData.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 读取目录下的所有文件名
     * @param url       文件路径
     * @return          有文件返回列表路径，没文件返回空或null
     */
    public static File[] readDir(String url){
        if (new File("url").exists()){
            return new File(url).listFiles();
        }else {
            return null;
        }

    }

    /**
     * 初始化，一般用于第一次使用插件，创建玩家数据目录
     */
    public static boolean initFile(){
        return newFile(Plugin.INSTANCE.getDataFolderPath() + "/playerData");
    }
}
