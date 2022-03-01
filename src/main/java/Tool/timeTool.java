package Tool;

import java.util.Calendar;
import java.util.Date;

public class timeTool {
    public static Long getToday(){
        Date date = new Date();
        String[] getDate = String.format("%tF", date).split("-");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(getDate[0]),Integer.parseInt(getDate[1])-1, Integer.parseInt(getDate[2]),0,0);
        return calendar.getTimeInMillis()/1000;
    }
}
