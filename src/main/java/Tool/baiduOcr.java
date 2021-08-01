package Tool;

import com.baidu.aip.ocr.AipOcr;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONObject;

import java.util.HashMap;

import static Tool.ymlReader.getYmlConfig;

/**
 * @Author: zhaolong
 * @Time: 12:40
 * @Date: 2021年07月16日 12:40
 **/
public class baiduOcr {

    private static String APP_ID = null;
    private static String API_KEY = null;
    private static String SECRET_KEY = null;

    private static AipOcr client = null;

    private static final ObjectMapper JSON = new ObjectMapper();


    /**
     * 初始化ocr，必须提前调用读取ocr配置文件
     */
    public static void setOcr(){
        try {
            JsonNode jsonNode = getYmlConfig("BaiDuOcr");
            APP_ID = jsonNode.get("AppID").asText();
            API_KEY = jsonNode.get("APIKey").asText();
            SECRET_KEY = jsonNode.get("SecretKey").asText();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 对链接进行搜索
     * @param url
     * @return  返回结果json，判断words_result_num可获得几个位置有文字
     * @throws JsonProcessingException
     */
    public static JsonNode picWord(String url) throws JsonProcessingException {
        client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return JSON.readTree(client.basicGeneral(url, new HashMap<String, String>()).toString());
    }


    /**
     * 对比特数组进行同样的ocr。
     * @param imgs 包含图片的比特数组
     * @return  返回结果json，判断words_result_num可获得几个位置有文字
     * @throws JsonProcessingException
     */
    public static JsonNode picWord(byte[] imgs) throws JsonProcessingException {
        client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return JSON.readTree(client.basicGeneral(imgs, new HashMap<String, String>()).toString());
    }
}
