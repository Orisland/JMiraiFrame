package Tool;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.SimpleFormatter;

public class timeTool {
    /**
     * 获取今天0点0分0秒的秒
     * @return  时间
     */
    public static Long getToday(){
        Date date = new Date();
        String[] getDate = String.format("%tF", date).split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(getDate[0]),Integer.parseInt(getDate[1])-1, Integer.parseInt(getDate[2]),0,0);
        return calendar.getTimeInMillis()/1000;
    }

    public static String getTime(String format){
        Date date = new Date();
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");

        return null;
    }

    /**
     * 获取今天的日期
     * @return
     */
    public static String[] getYMD(){
        Date date = new Date();
        String[] strings = new String[4];
        for (int i=0; i<3; i++){
            strings[i] = String.format("%tF", date).split("-")[i];
        }
        strings[3] = String.format("%tF", date).split("-")[0]+String.format("%tF", date).split("-")[1]+String.format("%tF", date).split("-")[2];
        return strings;
    }
}
