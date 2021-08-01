package Tool;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.ktor.http.Url;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static Tool.ymlReader.getYmlConfig;

/**
 * @Author: zhaolong
 * @Time: 12:37 下午
 * @Date: 2021年07月07日 12:37
 **/
public class OSSTool {
    private static String POXY = null;
    private static String CDNBOOST = null;
    private static String PERSONALPOINT = null;
    private static String PUBLICPOINT = null;
    private static String ENDPOINT = CDNBOOST;
    private static String ACCESSKEYID = null;
    private static String ACCESSKEYSECRET = null;
    private static String BUCKET = null;
    private static String DOWNLOADDIR = "." + File.separator + "config" + File.separator + "picTemp";
    private static final ClientBuilderConfiguration CONFIGURATION = new ClientBuilderConfiguration();
    private static boolean LOCAL = true;

    public static void OSSset(){
        try {
            JsonNode jsonNode = getYmlConfig("OSSconfig");
        POXY = jsonNode.get("POXY").asText();
        CDNBOOST = jsonNode.get("CDNBOOST").asText();
        PERSONALPOINT = jsonNode.get("PERSONALPOINT").asText();
        PUBLICPOINT = jsonNode.get("PUBLICPOINT").asText();
        ACCESSKEYID = jsonNode.get("ACCESSKEYID").asText();
        ACCESSKEYSECRET = jsonNode.get("ACCESSKEYSECRET").asText();
        BUCKET = jsonNode.get("BUCKET").asText();
//        DOWNLOADDIR = jsonNode.get("DOWNLOADDIR").asText();   
        LOCAL = jsonNode.get("LOCAL").asBoolean();
        ENDPOINT = CDNBOOST;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 流式上传，URL，文件流
     *
     * @param Url
     * @param uploadDir
     * @param saveName
     * @throws IOException
     */
    public static void uploadAsStream(String Url, String uploadDir, String saveName) throws IOException {
        if (LOCAL) {
            CONFIGURATION.setSupportCname(true);
        }
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET, CONFIGURATION);
        InputStream stream;
        String key = uploadDir + File.separator + saveName;

        if (Url.indexOf("https") == 0) {
            System.out.println("https");
            stream = new URL(Url).openStream();
        } else {
            System.out.println("file");
            stream = new FileInputStream(Url);
        }
        ossClient.putObject(BUCKET, key, stream);
        ossClient.shutdown();
        stream.close();
    }

    /**
     * 读取本地单文件上传
     *
     * @param Url
     * @param uploadDir
     * @param saveName
     */
    public static void uploadAsFile(String Url, String uploadDir, String saveName) {
        if (LOCAL) {
            CONFIGURATION.setSupportCname(true);
        }
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET, CONFIGURATION);
        String key = uploadDir + File.separator + saveName;
        File file = new File(Url);

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET, key, file);
        ossClient.putObject(putObjectRequest);
        ossClient.shutdown();
    }

    /**
     * 文件独立下载
     *
     * @param year
     * @param id
     */
    public static void downloadAsFile(int year, long id) {
        if (LOCAL) {
            CONFIGURATION.setSupportCname(true);
        }
        String dir = "pic" + File.separator + year + File.separator + id + ".jpg";
        //本地需修改↓
        String downloadDir = DOWNLOADDIR + File.separator + id + ".jpg";
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET, CONFIGURATION);
        ossClient.getObject(new GetObjectRequest(BUCKET, dir), new File(downloadDir));
        ossClient.shutdown();
    }

    /**
     * 下载文件，使用后必须关闭流
     *
     * @param year
     * @param id
     * @return
     * @throws IOException
     */
    public static byte[] downloadAsBytes(int year, long id) throws IOException {
        if (LOCAL) {
            CONFIGURATION.setSupportCname(true);
        }
        String dir = "pic" + File.separator + year + File.separator + id + ".jpg";
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET ,CONFIGURATION);
        OSSObject ossObject = ossClient.getObject(BUCKET, dir);
        byte[] bytes = intoByte(ossObject.getObjectContent());
        ossClient.shutdown();
        return bytes;
    }

    /**
     * 判断该id的图片是否存在
     *
     * @param id
     * @return
     */
    public static boolean isExist(long year, long id) {
        String dir = "pic" + File.separator + year + File.separator + id + ".jpg";
        if (LOCAL) {
            CONFIGURATION.setSupportCname(true);
        }
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET, CONFIGURATION);
        boolean flag = ossClient.doesObjectExist(BUCKET, dir);
        ossClient.shutdown();
        return flag;
    }

    /**
     * 遍历指定年份下的所有文件
     *
     * @param year
     * @return
     */
    public static List<String> yearList(long year) {
        if (LOCAL) {
            CONFIGURATION.setSupportCname(true);
        }
        System.out.println();
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET, CONFIGURATION);
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(BUCKET);
        final int maxKeys = 1000;
        List<String> list = new ArrayList<>();
        listObjectsRequest.setMaxKeys(maxKeys);
        listObjectsRequest.setPrefix("pic" + File.separator + year);
        String nextMarker = null;
        ObjectListing objectListing;

        do {
            objectListing = ossClient.listObjects(new ListObjectsRequest(BUCKET).withMarker(nextMarker).withMaxKeys(maxKeys).withPrefix("pic" + File.separator + year));
            List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
            for (OSSObjectSummary s : sums) {
                list.add(s.getKey());
            }
            nextMarker = objectListing.getNextMarker();
        } while (objectListing.isTruncated());

        return list;
    }


    /**
     * 缩小图片并输出比特数组
     *
     * @param year
     * @param id
     * @return
     * @throws IOException
     */
    public static byte[] smallerPic(long year, long id) throws IOException {
        String dir = "pic" + File.separator + year + File.separator + id + ".jpg";
        if (LOCAL) {
            CONFIGURATION.setSupportCname(true);
        }
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, ACCESSKEYID, ACCESSKEYSECRET,CONFIGURATION);

        String style = "style/smallerPic";
        GetObjectRequest request = new GetObjectRequest(BUCKET, dir);
        request.setProcess(style);
        InputStream inputStream = ossClient.getObject(request).getObjectContent();
        byte[] bytes = intoByte(inputStream);
        ossClient.shutdown();
        return bytes;
    }

    /**
     * 将input流转为比特数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] intoByte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while (-1 != (n = inputStream.read(buffer))) {
            output.write(buffer, 0, n);
        }
        return output.toByteArray();
    }

    /**
     * 比特数组变成输入流
     *
     * @param byts
     * @return
     */
    public static InputStream bytesIntoInput(byte[] byts) {
        return new ByteArrayInputStream(byts);
    }

    /**
     * 克隆输入流
     *
     * @param in
     * @return
     * @throws IOException
     */
    @Deprecated
    public static ByteArrayOutputStream cloneInput(InputStream in) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) > -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();
        return baos;
    }

    /**
     * 比特数组保存文件
     * @param bytes
     * @param filePath
     * @param fileName
     */
    public static void getFileByBytes(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            // 判断文件目录是否存在
            if (!dir.exists() && dir.isDirectory()) {
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        OSSset();
        getFileByBytes(smallerPic(2019,74150128), "/Users/zhaolong/IdeaProjects/MiraiSetu", "test.jpg");
    }

}
