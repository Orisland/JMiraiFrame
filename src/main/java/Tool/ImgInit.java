package Tool;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @Author: zhaolong
 * @Time: 22:53
 * @Date: 2022年02月25日 22:53
 **/
public class ImgInit {
    public BufferedImage getImg(int x, int y){
        BufferedImage bi = new BufferedImage(x,y,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bi.createGraphics();
        graphics2D.setColor(Color.white);
        graphics2D.fillRect(0,0, x, y);
        graphics2D.dispose();
        return bi;
    }



}
