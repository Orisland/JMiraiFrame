package org.orisland;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.extern.slf4j.Slf4j;

import static org.orisland.Config.*;

@Slf4j
public class JokeUtil {
    public static String getJokeRandom(String name){
        return getJokeRandom(1, name);
    }

    public static String getJokeRandom(int size, String name){
        int[] ints = NumberUtil.generateRandomNumber(0, JeffJoke.size(), size);
        StringBuilder stringBuilder = new StringBuilder();
        for (int anInt : ints) {
            stringBuilder.append(JeffJoke.get(anInt));
            stringBuilder.append("\n");
        }
        return HandlerJoke(name, stringBuilder.toString());
    }

    public static String HandlerJoke(String name, String joke){
        String[] split = joke.split(SplitChar);
        StringBuilder stringBuilder = new StringBuilder();

        //TODO: 那么问题来了，为什么我不用replace呢？………………因为我忘了。
        for (String s1 : split) {
            stringBuilder.append(s1);
            stringBuilder.append(name);
        }

        return stringBuilder.substring(0, stringBuilder.length() - name.length());
    }

    public static boolean addJoke(String content){
        if (!content.contains(SplitChar)){
            log.warn("内容不包含分割字符！");
            return false;
        }

        for (String s : JeffJoke) {
            if (s.equals(content)){
                log.warn("内容重复!");
                return false;
            }
        }

        JeffJoke.add(content);
        FileUtil.writeUtf8Lines(JeffJoke, JeffContent);
        return true;
    }
}
