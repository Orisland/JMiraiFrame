import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GetObjectRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;

import java.io.File;
import java.util.List;

/**
 * @Author: zhaolong
 * @Time: 1:26 下午
 * @Date: 2021年07月06日 13:26
 **/
public class osstest2 {
    private static String endpoint = "oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI5t8y8ifpCxgRJsfc6RLF";
    private static String accessKeySecret = "ab5ljhUljdTFrXjZaDe6pbyDbVedWy";
    private static String bucketName = "orislandex";


    private static String key = "test";
    public static void main(String[] args) {
// 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

// 下载Object到本地文件，并保存到指定的本地路径中。如果指定的本地文件存在会覆盖，不存在则新建。
// 如果未指定本地路径，则下载后的文件默认保存到示例程序所属项目对应本地路径中。
//        ossClient.getObject(new GetObjectRequest(bucketName, key), new File("ori.png"));

        ObjectListing objectListing = ossClient.listObjects(bucketName, key);
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        System.out.println(sums.size());
        for (OSSObjectSummary s : sums) {
            System.out.println("\t" + s.getKey());
        }

// 关闭OSSClient。
        ossClient.shutdown();
    }
}

