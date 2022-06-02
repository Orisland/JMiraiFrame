package org.orisland;

import Tool.YmlTool;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import static org.orisland.Config.*;

@Slf4j
public class DataInit {
    public static void init(){
        initJeffJokeContent();
        initJeffSplitChar();
    }

    public static void initJeffJokeContent(){
        if (FileUtil.exist(JeffContent)){
            JeffJoke = FileUtil.readUtf8Lines(JeffContent);
            log.info("Jeff Joke 内容读取完成，读取到{}条笑话！", JeffJoke.size());
        }else{
            FileUtil.writeFromStream(Plugin.class.getClassLoader().getResourceAsStream("jeff.txt"), JeffContent);
            log.info("初始化jeff joke!");
            initJeffJokeContent();
        }

    }

    public static void initJeffSplitChar(){
        if (FileUtil.exist(JeffConfig)){
            SplitChar = YmlTool.ReadYamlToString(JeffConfig, "split");
            log.info("分割符>{}<读取完成!", SplitChar);
        }else {
            FileUtil.writeFromStream(Plugin.class.getClassLoader().getResourceAsStream("config.yml"), JeffConfig);
            log.info("初始化JeffJoke配置文件！");
            initJeffSplitChar();
        }
    }
}
