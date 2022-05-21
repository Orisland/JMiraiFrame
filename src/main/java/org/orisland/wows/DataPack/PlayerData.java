package org.orisland.wows.DataPack;

import Tool.HttpClient;
import Tool.JsonTool;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.SinglePlayer.SinglePlayer;
import org.orisland.wows.doMain.SingleShipDataSimple;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.orisland.wows.ApiConfig.*;

@Slf4j
public class PlayerData {

    /**
     * 通过昵称获取该账号的info
     * @param username 昵称
     * @param server 服务器
     * @return 单个玩家数据
     */
    public static SinglePlayer NickNameToAccountInfo(String username, ApiConfig.Server server) {
        SinglePlayer singlePlayer = null;
        Long accountId = 0L;
        try {
            log.info("{}调用开始！",username);
            accountId = searchNickNameToAccountId(username, server);
            JsonNode jsonNode = searchAccountIdToAccountInfo(accountId, server);
            if (!jsonNode.get("status").asText().equals("ok")){
                log.info("api访问错误！");
                return null;
            }
            singlePlayer = JsonTool.mapper.readValue(jsonNode.get("data").get(String.valueOf(accountId)).toString(), SinglePlayer.class);
            System.out.println(jsonNode.get("data").get(String.valueOf(accountId)).toString());
        }catch (JsonProcessingException e){
            log.info("json解析错误！");
            e.printStackTrace();
        }
        log.info("{}调用结束！",username);
        return singlePlayer;
    }

    /**
     * 用昵称找账号
     * @param username 用户名
     * @param server 服务器
     * @return 返回uid
     */
    public static Long searchNickNameToAccountId(String username, ApiConfig.Server server){
        String format = String.format(ApiConfig.NICKNAME_ACCOUNTID, server, ApiConfig.APPID, username);
        JsonNode urlByJson = HttpClient.getUrlByJson(format);
        if (!urlByJson.get("status").asText().equals("ok")){
            log.error("用户api错误！");
            return null;
        }
        //TODO: 提供多用户对话式选择用户功能，目前仅默认仅考虑单用户
        if (urlByJson.get("meta").get("count").asLong() != 1){
            log.error("用户复数或不存在！");
            return null;
        }else {
            return urlByJson.get("data").get(0).get("account_id").asLong();
        }
    }

    /**
     * 用id找详细数据
     * @param uid uid
     * @param server 区服
     * @return 详细用户数据
     */
    public static JsonNode searchAccountIdToAccountInfo(Long uid, ApiConfig.Server server){
        String format = String.format(ApiConfig.ACCOUNTID_ACCOUNTINFO, server, ApiConfig.APPID, uid);
        return HttpClient.getUrlByJson(format);
    }

    /**
     * 通过账户id和指定日期查询单日或多日战绩
     * @param accountId 用户id
     * @param server    区服
     * @param date  时间
     * @return  包括数据的list
     */
    public static List<SingleShipDataSimple> SearchAccountIdToAccountInfoByDate(String accountId, ApiConfig.Server server, String date)  {
        String[] dates = date.split(",");
        JsonNode urlByJson = HttpClient.getUrlByJson(String.format(DATE_PLAYERINFO, server, APPID, accountId, date, API_LANGUAGE));
        List<SingleShipDataSimple> res = new ArrayList<>();
        try {
            for (String s : dates) {
                JsonNode jsonNode = urlByJson.get("data").get(accountId).get("pvp").get(s);
                res.add(JsonTool.mapper.readValue(jsonNode.toString(), SingleShipDataSimple.class));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return res;
    }
}
