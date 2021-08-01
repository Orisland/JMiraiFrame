import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @Author: zhaolong
 * @Time: 12:48 下午
 * @Date: 2021年07月07日 12:48
 **/
public class streaminstand {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = new FileInputStream("/Users/zhaolong/Downloads/IMG_5723.JPG");
        InputStream inputStream1 = new URL("https://help.aliyun.com/document_detail/84781.html?spm=a2c4g.11186623.6.944.55c146a1EyOIjl").openStream();
        System.out.println(inputStream instanceof FileInputStream);
        System.out.println(inputStream1 instanceof FileInputStream);

    }
}
