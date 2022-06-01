package Tool;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.yamlkt.Yaml;
import net.mamoe.yamlkt.YamlMap;
import org.orisland.WowsPlugin;

import java.io.*;
import java.nio.file.Path;

@Slf4j
public class FileTool {
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
     * 若出现重复则覆盖，删除旧文件，放入新文件
     * @param userData
     * @return
     */
    public static boolean saveFile(JsonNode userData, String url, String name){
        FileWriter fileWriter;
        File file = new File(url +  name);
        if (file.exists()){
            log.info("del已存在的json"+file + ":" + file.delete());
        }
        try {
            fileWriter = new FileWriter(url + File.separator + name);
            fileWriter.write(userData.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 保存，并读取返回
     * @param userData
     * @return
     */
    public static boolean saveAndReadFile(JsonNode userData, String url, String name){
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
        if (new File(url).exists()){
            return new File(url).listFiles();
        }else {
            return null;
        }
    }

    /**
     * 初始化，一般用于第一次使用插件，创建玩家数据目录
     */
    public static boolean initFile(){
        return newFile(WowsPlugin.INSTANCE.getDataFolderPath() + File.separator + "playerData");
    }

    /**
     * 指定文件是否存在
     * @param path 文件地址
     * @return 是否存在
     */
    public static boolean fileExists(String path){
        return new File(path).exists();
    }

    /**
     * 删除指定位置的文件,非级联删除
     * @param path
     * @return
     */
    public static boolean delteFile(String path){
        File file = new File(path);
        if (file.exists() && file.isFile()){
            System.out.println("delete " + file);
            return file.delete();
        }else {
            System.out.println("文件不存在!");
            return false;
        }
    }

    /**
     * 将指定文件读取为String
     * @param path 文件路径
     * @return 返回结果String
     */
    public static String ReadDirToString(String path) {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(path));
            stringBuilder = new StringBuilder();
            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                bufferedReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 从指定路径中读取yaml并返回yaml对象
     * @param content 待转化内容
     * @return  转化为yamlMap
     */
    public static YamlMap ReadStringToYaml(String content){
        Yaml yaml = Yaml.Default;
        return yaml.decodeYamlMapFromString(content);
    }
}