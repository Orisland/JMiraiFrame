import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.mamoe.mirai.message.data.Image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

/**
 * @Author: zhaolong
 * @Time: 12:28
 * @Date: 2021年07月11日 12:28
 **/
public class black_white {
    public static ObjectNode chargePic(Image image) throws IOException{
        String url = Image.Key.queryUrl(image);
        int[] rgb = new int[3];
        InputStream inputStream = new URL(url).openStream();
        BufferedImage bi = ImageIO.read(inputStream);
        int wight = bi.getWidth();
        int hight = bi.getHeight();
        int minx=bi.getMinX();
        int miny=bi.getMinY();

        int black_white = 0;
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
                if (rgb[0] == rgb[1] && rgb[1] == rgb[2] || (Math.abs(rgb[0] - rgb[1]) < 10 && Math.abs(rgb[1] - rgb[2]) <10 && Math.abs(rgb[2] - rgb[0]) < 10)){
                    grey++;
                }else {
                    color++;
                }

            }
        }

        double precent = (double) color / total;

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode jsonNodes = objectMapper.createObjectNode();
        jsonNodes.put("total",total);
        jsonNodes.put("grey",grey);
        jsonNodes.put("color",color);
        String per = String.format("%.2f",precent);
        String res = null;
        if (precent < 0.01){
            res = "grey pic!";
        }else if (precent > 0.99){
            res = "pure pic!";
        }else {
            res = "normal pic!";
        }
        jsonNodes.put("res", res);
        jsonNodes.put("per", precent);

        return jsonNodes;
    }
    public static void main(String[] args) throws IOException {
        int[] rgb = new int[3];
        File file = new File("/Users/zhaolong/Downloads/IMG_5260.PNG");
        BufferedImage bi = ImageIO.read(file);
        int wight = bi.getWidth();
        int hight = bi.getHeight();
        int minx=bi.getMinX();
        int miny=bi.getMinY();

        int black_white = 0;
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
                if (rgb[0] == rgb[1] && rgb[1] == rgb[2] || (Math.abs(rgb[0] - rgb[1]) < 10 && Math.abs(rgb[1] - rgb[2]) <10 && Math.abs(rgb[2] - rgb[0]) < 10)){
                    grey++;
                }else {
                    color++;
                }

            }
        }

        double precent = (double) color / total;

        System.out.println("total:"+total);
        System.out.println("grey:"+grey);
        System.out.println("color:"+color);
        System.out.println("%:"+precent);
        if (precent < 0.01){
            System.out.println("grey pic!");
        }else if (precent > 0.99){
            System.out.println("pure pic!");
        }else {
            System.out.println("normal pic!");
        }

//        System.out.println(wight);
//        System.out.println(hight);
    }
}
