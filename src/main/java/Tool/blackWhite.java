package Tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.mamoe.mirai.message.data.Image;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static Tool.OSSTool.bytesIntoInput;
import static Tool.OSSTool.intoByte;

/**
 * @Author: zhaolong
 * @Time: 12:28
 * @Date: 2021年07月11日 12:28
 **/
public class blackWhite {
    /**
     * 坚定这个图片是否为黑白照片，只需要验证返回的json中的flag，所有大于0的图片均可以。
     * @param image
     * @return 若flag >0 则说明该图片为正常图片。
     * @throws IOException
     */
    public static ObjectNode chargePic(Image image) throws IOException{
        System.out.println("图片开始处理!");
        int[] rgb = new int[3];
        String temp = Image.queryUrl(image);
        System.out.println("ori_url:"+temp);
        String url = HttpClient.urlProxy(Image.queryUrl(image));
        if ("error".equals(url)){
            return null;
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .callTimeout(15, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Connection", "keep-alive")
                .build();

        System.out.println("catch inputted!");
        byte[] bytes = okHttpClient.newCall(request).execute().body().bytes();
        System.out.println("catch finished!");
        InputStream inputStream1 = bytesIntoInput(bytes);
        BufferedImage bi = ImageIO.read(inputStream1);

        int wight = bi.getWidth();
        int hight = bi.getHeight();
        int minx=bi.getMinX();
        int miny=bi.getMinY();

        int blackWhite = 0;
        int color = 0;
        int grey = 0;
        int total = minx * miny;
        int hightcolor = 0;

        for(int i=minx;i<wight;i++) {
            for(int j=miny;j<hight;j++)
            {
                total++;
                int pixel=bi.getRGB(i,j);
                rgb[0] = (pixel &  0xff0000) >> 16;
                rgb[1] = (pixel &  0xff00) >> 8;
                rgb[2] = (pixel &  0xff);
                if (rgb[0] == rgb[1] && rgb[1] == rgb[2]
                        || (Math.abs(rgb[0] - rgb[1]) < 10 && Math.abs(rgb[1] - rgb[2]) <10 && Math.abs(rgb[2] - rgb[0]) < 10)){
                    grey++;
                }else {
                    color++;
                }
            }
        }

        double precent = ((double) color / total) *100;

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNodes = objectMapper.createObjectNode();
        jsonNodes.put("total",total);
        jsonNodes.put("grey",grey);
        jsonNodes.put("color",color);
        String per = String.format("%.2f",precent);
        per += "%";
        String res = null;

        int flag = 0;
        if (precent < 1){
            res = "这是一张灰白图!";
            flag = 0;
            //99.5->99.5%色域即可，不准确，预估数值，仅做参考
        }else if (precent > 99.5){
            res = "这是一张纯色图!";
            flag = 1;
        }else {
            res = "这是一张正常的图!";
            flag = 2;
        }
        jsonNodes.put("res", res);
        jsonNodes.put("per", per);
        jsonNodes.put("flag", flag);
        return jsonNodes;
    }
}
