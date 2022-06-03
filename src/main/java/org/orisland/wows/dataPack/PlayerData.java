package org.orisland.wows.dataPack;

import Tool.HttpClient;
import Tool.JsonTool;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.Bind;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;
import org.orisland.wows.doMain.SingleShipDataSimple;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.orisland.wows.ApiConfig.*;
import static org.orisland.wows.dataPack.BindData.bindQQAccountId;
import static org.orisland.wows.dataPack.StringToMeaningful.ServerToDir;
import static org.orisland.wows.dataPack.StringToMeaningful.StringToServer;

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
        String accountId;
        try {
            log.info("{}调用开始！",username);
            accountId = searchNickNameToAccountId(username, server);
            JsonNode jsonNode;
            if (accountId == null){
                log.warn("用户未找到！");
                return null;
            }else {
                jsonNode = searchAccountIdToAccountInfo(accountId, server);
            }
            singlePlayer = JsonTool.mapper.readValue(jsonNode.toString(), SinglePlayer.class);
        }catch (JsonProcessingException e){
            log.info("json解析错误！");
            e.printStackTrace();
        }
        log.info("{}调用结束！",username);
        return singlePlayer;
    }

    /**
     * 直接通过id找到精确找到用户
     * @param accountId 用户id
     * @param server    区服
     * @return          数据包
     */
    public static SinglePlayer AccountIdToAccountInfo(String accountId, Server server){
        JsonNode jsonNode = searchAccountIdToAccountInfo(accountId, server);
        if (jsonNode == null){
            log.error("{}不存在!",accountId);
            return null;
        }else {
            try {
                return JsonTool.mapper.readValue(jsonNode.toString(), SinglePlayer.class);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 用昵称找账号
     * @param username 用户名
     * @param server 服务器
     * @return 返回uid
     */
    public static String searchNickNameToAccountId(String username, ApiConfig.Server server){
        String format = String.format(ApiConfig.NICKNAME_ACCOUNTID, server, ApiConfig.APPID, username);
        JsonNode urlByJson = HttpClient.getUrlByJson(format);
        if (!urlByJson.get("status").asText().equals("ok")){
            log.error("用户api错误！");
            return null;
        }
        //TODO: 提供多用户对话式选择用户功能，目前仅默认仅考虑单用户
        if (urlByJson.get("meta").get("count").asLong() > 1){
            log.warn("用户复数！");
            return urlByJson.get("data").get(0).get("account_id").asText();
        }else if (urlByJson.get("meta").get("count").asLong() == 0){
            return null;
        } else {
            return urlByJson.get("data").get(0).get("account_id").asText();
        }
    }

    /**
     * 用id找详细数据
     * @param uid uid
     * @param server 区服
     * @return 详细用户数据
     */
    public static JsonNode searchAccountIdToAccountInfo(String uid, ApiConfig.Server server){
        String format = String.format(ApiConfig.ACCOUNTID_ACCOUNTINFO, server, ApiConfig.APPID, uid);
        JsonNode jsonNode = accountDataStandard(HttpClient.getUrlByJson(format));
        if (jsonNode != null && jsonNode.get("statistics") != null){
            return jsonNode;
        }else {
            log.warn("玩家隐藏了战绩!");
            return null;
        }

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

    /**
     * 用户数据标准化去除杂项
     * @return              纯化数据
     */
    public static JsonNode accountDataStandard(JsonNode data){
        if (data.get("status").asText().equals("ok")){
            for (JsonNode jsonNode : data.get("data")) {
                return jsonNode;
            }
            log.info("没有数据！");
            return null;
        }else {
            log.error("API错误！");
            return null;
        }
    }

    /**
     * 读取指定用户id的当日json数据
     * @param accountid 用户id
     * @param server    服务器
     * @return          读取的json数据
     */
    public static JsonNode readAccountToday(String accountid, Server server){
        String path = selectDataNewest(accountid, server);
        try {
            if (path == null){
                return null;
            }
            return JsonTool.mapper.readTree(FileUtil.readUtf8String(path));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 用于信息获取与标准化，去除无用的数据
     * @param accountId 用户id
     * @param server    区服
     * @return          pureData
     */
    public static JsonNode shipDataStandard(String accountId, Server server){
        JsonNode urlByJson = HttpClient.getUrlByJson(String.format(ACCOUNT_SHIP, server, APPID, accountId, "", API_LANGUAGE));
        if (urlByJson.get("status").asText().equals("ok")){
            ObjectNode objectNode = JsonTool.mapper.createObjectNode();
            objectNode.put("date", DateUtil.format(DateUtil.date(), "YYYYMMdd"));
            objectNode.put("accountid", accountId);

//            新增了rank数据
            ObjectNode temp = JsonTool.mapper.createObjectNode();
            if (temp.size() == 0){
                log.info("玩家{}发现了奇怪的战绩，跳过获取！", accountId);
                return null;
            }
            for (JsonNode data : urlByJson.get("data").get(accountId)) {
                temp.set(data.get("ship_id").asText(), data);
            }
            objectNode.set("data", temp);
            return objectNode;
        }else {
            log.error("错误的数据！");
            return null;
        }
    }

    public static JsonNode shipDataStandard(JsonNode urlByJson){
        if (urlByJson.get("status").asText().equals("ok")){
            ObjectNode objectNode = JsonTool.mapper.createObjectNode();
            objectNode.put("date", DateUtil.format(DateUtil.date(), "YYYYMMdd"));
            objectNode.put("accountid", urlByJson.get("account_id").asText());

            ObjectNode temp = JsonTool.mapper.createObjectNode();

            if (temp.size() == 0){
                log.info("发现了奇怪的战绩，跳过获取！");
                return null;
            }

            for (JsonNode data : urlByJson.get("data").get(urlByJson.get("account_id").asText())) {
                temp.set(data.get("ship_id").asText(), data);
            }
            objectNode.set("data", temp);
            return objectNode;
        }else {
            log.error("错误的数据！");
            return null;
        }
    }

    /**
     * 根据账户id将数据存入data目录
     * @param accountId 账户id
     * @param server    区服
     */
    public static void saveAccountShipInfo(String accountId, Server server, boolean force){
        String s = ServerToDir(server);
        String date = DateUtil.format(DateUtil.date(), "YYYYMMdd");
        JsonNode data = shipDataStandard(accountId, server);
        if (data == null)
            return;
        originPlayerData(accountId,data);
        if (force){
            FileUtil.writeUtf8String(data.toString(), ServerToDir(server) + accountId + "," + date + ".json");
            log.info("{}新增{}记录！", accountId, date);
        }else {
            if (!FileUtil.exist(s + accountId + "," + date + ".json")){
                FileUtil.writeUtf8String(data.toString(), ServerToDir(server) + accountId + "," + date + ".json");
                log.info("{}新增{}记录！", accountId, date);
            }else {
                log.info("{},{}记录已存在.", accountId, date);
            }
        }
    }

    /**
     *
     * 储存n天的战绩数据，默认选择最前一天的
     * @param accountId 账号id
     * @param server    区服
     * @param order     次序,true : 选择最旧， false : 选择最新
     * @return          被选择的文件名
     */
    public static String selectData(String accountId, Server server, boolean order){
        File[] ls = FileUtil.ls(ServerToDir(server));
        List<String> accountDataList = new ArrayList<>();
        for (File l : ls) {
            if (l.getName().contains(accountId)){
                accountDataList.add(l.getName());
            }
        }
        if (accountDataList.size() == 0){
            log.warn("数据选择失败，本地没有文件!");
            return null;
        }
        accountDataList.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Long l1 = Long.parseLong(o1.split(",")[1].split("\\.")[0]);
                Long l2 = Long.parseLong(o2.split(",")[1].split("\\.")[0]);
                return order ? ((Long)(l1-l2)).intValue() : ((Long)(l2-l1)).intValue();
            }
        });
        return ServerToDir(server) + accountDataList.get(0);

    }

    /**
     * 数据更新操作，删除旧的替换为新的，删除默认最久数据
     * @param accountId 用户id
     * @param server    区服信息
     */
    public static void updateAccountLocalData(String accountId, Server server){
        String s = selectData(accountId, server, true);
        if (s == null){
            saveAccountShipInfo(accountId, server, false);
        }else {
            String s1 = selectDataNewest(accountId, server);
            if (s1.split(",")[1].split("\\.")[0].equals(DateUtil.format(new Date(), "YYYYMMdd"))){
                log.warn("最新数据与待更新数据重叠，跳过更新！");
            }else {
                if (!shouldDel(accountId, server)){
                    saveAccountShipInfo(accountId, server, false);
                }else {
                    boolean del = FileUtil.del(s);
                    if (!del){
                        log.warn("旧数据删除失败，数据更新失败！");
                    }
                    saveAccountShipInfo(accountId, server, false);
                }
            }
        }
    }

    /**
     * 数据应该删除吗？
     * @param accountId
     * @param server
     * @return
     */
    public static boolean shouldDel(String accountId, Server server){
        File[] ls = FileUtil.ls(ServerToDir(server));
        List<String> accountDataList = new ArrayList<>();
        for (File l : ls) {
            if (l.getName().contains(accountId)){
                accountDataList.add(l.getName());
            }
        }
        return accountDataList.size() >= maxSavePlayerData;
    }

    /**
     * 初始化时执行的执行操作
     * 该操作自动更新已绑定的所有用户数据
     */
    public static void updateAccountLocalDataAuto(boolean force){
        if (force){
            for (JsonNode jsonNode : Bind) {
                String accountId = jsonNode.get("id").asText();
                saveAccountShipInfo(accountId, StringToServer(jsonNode.get("server").asText()), true);
            }
        }else {
            for (JsonNode jsonNode : Bind) {
                String accountId = jsonNode.get("id").asText();
                updateAccountLocalData(accountId, StringToServer(jsonNode.get("server").asText()));
            }
        }
        log.info("用户数据更新完成！");

    }

    /**
     * 选择最新的数据
     * @param accountId 用户id
     * @param server    区服
     * @return          地址
     */
    public static String selectDataNewest(String accountId, Server server){
        return selectData(accountId, server, false);
    }

    /**
     * 选择最旧的数据
     * @param accountId 用户id
     * @param server    区服
     * @return          地址
     */
    public static String selectDataOldest(String accountId, Server server){
        return selectData(accountId, server, true);
    }

    /**
     * 读取指定日期的数据
     * @param accountId 账号id
     * @param server    服务器
     * @param date      日期
     * @return          读取的json
     */
    public static JsonNode readPlayerData(String accountId,Server server, Date date){
        try {
            String dateString = DateUtil.format(date, "YYYYMMdd");
            String fileName = String.format("%s,%s.json",accountId, dateString);
            String serverDir = ServerToDir(server);
            boolean exist = FileUtil.exist(serverDir + fileName);
            if (exist){
                return JsonTool.mapper.readTree(FileUtil.readUtf8String(serverDir + fileName));
            }else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 允许直接写入date字符串
     * @param accountId 账号id
     * @param server    服务器
     * @param date      日期
     * @return          调用上面的方法
     */
    public static JsonNode readPlayerData(String accountId,Server server, String date){
        if (date.length() != 8){
            log.warn("非标准日期字符串!");
            return null;
        }
        try {
            return readPlayerData(accountId, server, new SimpleDateFormat("yyyyMMdd").parse(date));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户第一次绑定时储存原始数据
     * @param accountId 用户id
     * @param server    区服
     */
    public static void originPlayerData(String accountId, Server server){
        if (!FileUtil.exist(originData + accountId + ".json")){
            JsonNode jsonNode = shipDataStandard(accountId, server);
            FileUtil.writeUtf8String(jsonNode.toString(), originData + accountId + ".json");
            log.info("{}数据已储存至源数据文件夹.", accountId);
        }else {
            log.info("{}源数据已存在!",accountId);
        }
    }

    /**
     * 用于直接存json跳过读取阶段
     * @param data 数据
     */
    public static void originPlayerData(String accountId, JsonNode data){
        if (!FileUtil.exist(originData + accountId + ".json")){
            FileUtil.writeUtf8String(data.toString(), originData + accountId + ".json");
            log.info("{}数据已储存至源数据文件夹.", accountId);
        }else {
            log.info("{}源数据已存在!",accountId);
        }
    }

    /**
     * 将qq转为具体的账号信息
     * @param qq    qq
     * @return      具体信息
     */
    public static Bind findAccountId(String qq){
        Bind bind = new Bind();
        JsonNode jsonNode = Bind.get(qq);
        if (jsonNode == null){
            log.warn("该qq未绑定账号数据！");
            return null;
        }

        if (jsonNode.get("regTime") == null){
            log.info("发现了没有绑定时间的账号，重新进行绑定！");
            bindQQAccountId(qq, jsonNode.get("id").asText(), StringToServer(jsonNode.get("server").asText()));
        }

        bind.setAccountId(jsonNode.get("id").asText());
        bind.setQq(qq);
        bind.setServer(StringToServer(jsonNode.get("server").asText()));
        bind.setAccountName(jsonNode.get("name").asText());
        return bind;
    }


    /**
     * 鉴权是否允许绑定和修改
     * 非管理员禁止重复绑定
     * @param accountId
     * @param qq
     * @return
     */
    public static boolean findBindAccountId(String accountId, String qq){
        for (JsonNode jsonNode : Bind) {
            if (jsonNode.get("id").asText().equals(accountId) && !Arrays.asList(Admin).contains(qq)){
                return false;
            }
        }
        return true;
    }
}
