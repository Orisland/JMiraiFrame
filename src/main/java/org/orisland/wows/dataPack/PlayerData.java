package org.orisland.wows.dataPack;

import Tool.HttpClient;
import Tool.JsonTool;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.Bind;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.singlePlayer.SinglePlayer;
import org.orisland.wows.doMain.singleShipData.Pvp;
import org.orisland.wows.doMain.singleShipData.SingleShipData;
import org.orisland.wows.doMain.SingleShipDataSimple;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.orisland.wows.ApiConfig.*;
import static org.orisland.wows.dataPack.ShipData.*;

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
     * 通过accountid获取该用户的单船只pr
     * @param accountId 用户id
     * @param server    用户所在服务器
     * @return          pr数据
     */
    public static ShipDataObj AccountIdToPr(String accountId, Server server, String shipId){
        List<SingleShipData> singleShipData = SearchAccountIdToShipInfo(accountId, shipId, server);
        double actDmg = 0.0;
        double expDmg = 0.0;
        double actWins = 0.0;
        double expWins = 0.0;
        double actFrags = 0.0;
        double expFrags = 0.0;
        Pvp pvp = null;
        int battles = 0;

        try {
            if (singleShipData.size() == 0)
                return null;
        }catch (Exception e){
            return null;
        }

        for (SingleShipData singleShipDatum : singleShipData) {
            try {
//                调用顺序问题
                pvp = singleShipDatum.getPvp();
                battles = pvp.getBattles();
                if (battles == 0){
                    battles = 1;
                }

                actDmg += pvp.getDamage_dealt();
                actFrags += pvp.getFrags();
                actWins += pvp.getWins();

                JsonNode shipExpected = ShipToExpected(String.valueOf(singleShipDatum.getShip_id()));
                expDmg += shipExpected.get("average_damage_dealt").asDouble() * battles;
                expWins += shipExpected.get("win_rate").asDouble() / 100 * battles;
                expFrags +=  shipExpected.get("average_frags").asDouble() * battles;
            }catch (Exception e){
                e.printStackTrace();
                log.warn("跳过异常船只！");
                continue;
            }
        }
        if (pvp == null){
            return null;
        }
        ShipDataObj shipDataObj = new ShipDataObj();
        shipDataObj.setShoot(pvp.getMain_battery().getShots());
        shipDataObj.setHit((long) (pvp.getMain_battery().getHits()));
        shipDataObj.setWins((long) (pvp.getWins()));
        shipDataObj.setDmg(pvp.getDamage_dealt());
        shipDataObj.setKill((long) (pvp.getFrags()));
        shipDataObj.setSurvive((long) (pvp.getSurvived_battles()));
        shipDataObj.setSurviveWin((long) (pvp.getSurvived_wins()));
        shipDataObj.setXp(pvp.getXp());
        shipDataObj.setBattle(battles);

        ShipPr shipPr = new ShipPr();
        shipPr.setActualFrags(actFrags);
        shipPr.setActualDmg(actDmg);
        shipPr.setActualWins(actWins);
        shipPr.setExpectedWins(expWins);
        shipPr.setExpectedFrags(expFrags);
        shipPr.setExpectedDmg(expDmg);
        shipPr.setShipId(shipId);
        shipPr.setBattle(battles);

        shipPr.update();

        shipDataObj.setPR(shipPr);
        if (!shipId.equals(""))
            shipDataObj.setShip(SearchShipIdToShipInfo(String.valueOf(shipId)));

        shipDataObj.update();


        return shipDataObj;
    }

    /**
     * 通过昵称查询用户全部船只的综合pr
     * @param NickName  昵称
     * @param server    区服
     * @return  pr信息
     */
    public static ShipDataObj NickNameToPr(String NickName, Server server){
        return AccountIdToPr(searchNickNameToAccountId(NickName, server), server, "");
    }

    /**
     * 查询指定用户指定船只id的pr
     * @param accountId 用户id
     * @param server    服务器
     * @param shipId    船只id
     * @return          pr结果
     */
    public static ShipDataObj AccountIdShipToPr(String accountId, Server server, String shipId){
        return AccountIdToPr(accountId, server, shipId);
    }

    /**
     * 查询指定用户id的pr
     * @param accountId 用户id
     * @param server    服务器
     * @return          pr结果
     */
    public static ShipDataObj AccountIdShipToPr(String accountId, Server server){
        return AccountIdToPr(accountId, server, "");
    }

    /**
     * 查询指定用户名指定昵称的pr
     * @param NickName  昵称
     * @param server    服务器
     * @param ship      船只
     * @return          数据
     */
    public static ShipDataObj NickNameShipToPr(String NickName, Server server, String ship){
        return AccountIdShipToPr(searchNickNameToAccountId(NickName, server), server, ship);
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

            ObjectNode temp = JsonTool.mapper.createObjectNode();
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
    public static void saveAccountShipInfo(String accountId, Server server){
        String date = DateUtil.format(DateUtil.date(), "YYYYMMdd");
        JsonNode data = shipDataStandard(accountId, server);
        originPlayerData(accountId,data);
        FileUtil.writeUtf8String(data.toString(), ServerToDir(server) + accountId + "," + date + ".json");
    }

    /**
     *
     * 储存3天的战绩数据，默认选择最前一天的
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
        if (accountId.equals("560642785")){
            System.out.println(1);
        }
        String s = selectData(accountId, server, true);
        if (s == null){
            saveAccountShipInfo(accountId, server);
            log.info("{}新增本日记录！",accountId);
        }else {
            String s1 = selectDataNewest(accountId, server);
            if (s1.split(",")[1].split("\\.")[0].equals(DateUtil.format(new Date(), "YYYYMMdd"))){
                log.warn("最新数据与待更新数据重叠，跳过更新！");
            }else {
                if (!shouldDel(accountId, server)){
                    saveAccountShipInfo(accountId, server);
                    log.info("{}更新完成!",accountId);
                }else {
                    boolean del = FileUtil.del(s);
                    if (!del){
                        log.warn("旧数据删除失败，数据更新失败！");
                    }
                    saveAccountShipInfo(accountId, server);
                    log.info("{}更新完成!",accountId);
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
    public static void updateAccountLocalDataAuto(){
        for (JsonNode jsonNode : Bind) {
            String accountId = jsonNode.get("id").asText();
            updateAccountLocalData(accountId, StringToServer(jsonNode.get("server").asText()));
        }
        log.info("用户数据更新完成！");
    }

    /**
     * 根据区服选择保存地址
     * @param server    区服
     * @return          保存地址
     */
    public static String ServerToDir(Server server){
        switch (server){
            case EU:
                return dataDirEu;
            case ASIA:
                return dataDirAsia;
            case RU:
                return dataDirRu;
            case NA:
            case com:
                return dataDirNa;
            default:
                log.error("出现了意料之外的数据！");
                return null;
        }
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
        File[] ls = FileUtil.ls(originData);
        for (File l : ls) {
            if (l.getName().contains(accountId)){
                log.info("{}源数据已存在!",accountId);
                return;
            }
        }
        JsonNode jsonNode = shipDataStandard(accountId, server);
        FileUtil.writeUtf8String(jsonNode.toString(), originData + accountId + ".json");
        log.info("{}数据已储存至源数据文件夹.", accountId);
    }

    /**
     * 用于直接存json跳过读取阶段
     * @param data 数据
     */
    public static void originPlayerData(String accountId, JsonNode data){
        File[] ls = FileUtil.ls(originData);
        for (File l : ls) {
            if (l.getName().contains(accountId)){
                log.info("{}源数据已存在!",accountId);
                return;
            }
        }
        FileUtil.writeUtf8String(data.toString(), originData + accountId + ".json");
        log.info("{}数据已储存至源数据文件夹.", accountId);
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
        bind.setAccountId(jsonNode.get("id").asText());
        bind.setQq(qq);
        bind.setServer(StringToServer(jsonNode.get("server").asText()));
        bind.setAccountName(jsonNode.get("name").asText());
        return bind;
    }

    /**
     * qq绑定游戏用户
     * @param qq        qq号
     * @param accountId 账户id
     * @param server    区服
     */
    public static void bindQQAccountId(String qq, String accountId, Server server){
        ObjectNode bind = (ObjectNode) Bind;
        JsonNode jsonNode = searchAccountIdToAccountInfo(accountId, server);
        saveAccountShipInfo(accountId, server);
        originPlayerData(accountId, server);
        ObjectNode objectNode = JsonTool.mapper.createObjectNode();
        objectNode.put("id", accountId);
        objectNode.put("server", server.toString());
        objectNode.put("name", jsonNode.get("nickname").asText());
        bind.set(qq, objectNode);
        Bind = bind;
        FileUtil.writeUtf8String(bind.toString(), dataDir + "Bind.json");
        log.info("{}绑定{}{}已完成!", qq, server, accountId);
    }

    /**
     * 更新qq绑定
     * @param qq        qq号
     * @param accountId 用户id
     * @param server    区服
     */
    public static void updateBind(String qq, String accountId, Server server){
        ObjectNode bind = (ObjectNode) Bind;
        JsonNode jsonNode = bind.get(qq);
        if (jsonNode == null){
            log.warn("该qq并未绑定账号数据！");
        }else {
            bind.remove(qq);
            bindQQAccountId(qq, accountId, server);
        }
    }

    /**
     * 将字符串识别为区服信息
     * @param str   待转化信息
     * @return      枚举变量区服
     */
    public static Server StringToServer(String str){
        str = str.toUpperCase();
        switch (str){
            case "EU":
                return Server.EU;
            case "ASIA":
                return Server.ASIA;
            case "COM":
            case "NA":
                return Server.com;
            case "RU":
                return Server.RU;
            default:
                log.error("出现了预料之外的问题！");
                return null;
        }
    }

    /**
     * 查找在线数据与本地数据的diff船只计算当日的打船数据
     * @param accountid 用户id
     * @param server    区服
     * @return          数据列表
     */
    public static List<ShipDataObj> diffShip(String accountid ,Server server) {
        log.info("开始数据对比！");
        JsonNode playerData = shipDataStandard(accountid,server);
        JsonNode LocalData = readAccountToday(accountid, server);
        List<ShipDataObj> shipDataObjs = diffDataPure(playerData, LocalData, 0);
        log.info("数据对比完成!");
        return shipDataObjs;
    }

    /**
     * 纯diff
     * @param nowData       新数据
     * @param originData    老数据
     * @return              结果
     */
    public static List<ShipDataObj> diffDataPure(JsonNode nowData, JsonNode originData, int dayLeft){
        nowData = nowData.get("data");
        originData = originData.get("data");
        if (originData == null){

        }
        DateTime today = DateUtil.date();

        List<ShipDataObj> res = new ArrayList<>();

        for (JsonNode datum : nowData) {
            try {
                DateTime LastBattleTime = DateUtil.date(datum.get("last_battle_time").asLong() * 1000);

                if (LastBattleTime.year() == today.year() && LastBattleTime.dayOfYear() == today.dayOfYear()-dayLeft){
                    SingleShipData nowSingleShipData = JsonTool.mapper.readValue(datum.toString(), SingleShipData.class);
                    SingleShipData originSingleShipData = null;
                    if (originData.get(String.valueOf(nowSingleShipData.getShip_id())) == null){
                        log.info("未发现该船只，配置为出现了数据本地数据之外的新船！");
                        originSingleShipData = new SingleShipData();
                        originSingleShipData.setPvp(new Pvp());
                    }else {
                        originSingleShipData = JsonTool.mapper.readValue(originData.get(String.valueOf(nowSingleShipData.getShip_id())).toString(), SingleShipData.class);
                    }
                    Pvp nowPvp = nowSingleShipData.getPvp();
                    Pvp originPvp = originSingleShipData.getPvp();
                    double Dmg = nowPvp.getDamage_dealt() - originPvp.getDamage_dealt();
                    int Battle = nowPvp.getBattles() - originPvp.getBattles();
                    double Wins = nowPvp.getWins() - originPvp.getWins();
                    double Frags = nowPvp.getFrags() - originPvp.getFrags();

                    ShipPr shipPr = new ShipPr();
                    shipPr.setActualWins(Wins);
                    shipPr.setActualFrags(Frags);
                    shipPr.setActualDmg(Dmg);
                    shipPr.setShipId(String.valueOf(nowSingleShipData.getShip_id()));
                    shipPr.setBattle(Battle);
                    try {
                        shipPr.update();
                    }catch (Exception e){
                        log.error("错误的数据！");
                        continue;
                    }

                    ShipDataObj shipDataObj = new ShipDataObj();
                    shipDataObj.setShoot(nowPvp.getMain_battery().getShots() - originPvp.getMain_battery().getShots());
                    shipDataObj.setHit((long) (nowPvp.getMain_battery().getHits() - originPvp.getMain_battery().getHits()));
                    shipDataObj.setWins((long) (nowPvp.getWins() - originPvp.getWins()));
                    shipDataObj.setDmg(nowPvp.getDamage_dealt() - originPvp.getDamage_dealt());
                    shipDataObj.setKill((long) (nowPvp.getFrags() - originPvp.getFrags()));
                    shipDataObj.setSurvive((long) (nowPvp.getSurvived_battles() - originPvp.getSurvived_battles()));
                    shipDataObj.setSurviveWin((long) (nowPvp.getSurvived_wins() - originPvp.getSurvived_wins()));
                    shipDataObj.setXp(nowPvp.getXp() - originPvp.getXp());
                    shipDataObj.setBattle(Battle);
                    shipDataObj.setPR(shipPr);
                    shipDataObj.setShip(SearchShipIdToShipInfo(String.valueOf(nowSingleShipData.getShip_id())));

                    shipDataObj.update();

                    res.add(shipDataObj);
                }

            }catch (JsonProcessingException e){
                e.printStackTrace();
            }

        }
        return res;
    }

    /**
     * 查询指定时间段之间的战绩
     * @param accountId 用户id
     * @param server    服务器
     * @param from      从YYYYMMdd
     * @param to        到YYYYMMdd
     * @return          数据包
     */
    public static List<ShipDataObj> diffDataBetween(String accountId, Server server, int from, int to){
        long today = Long.parseLong(DateUtil.format(new Date(), "YYYYMMdd"));
        JsonNode nowRecord;
        JsonNode originRecord;
        if (from == to){
            log.error("查找的日期为同一天！");
            return null;
        }
        if(to == today)
            nowRecord = shipDataStandard(accountId,server);
        else
            nowRecord = readPlayerData(accountId, server, String.valueOf(to));

        originRecord = readPlayerData(accountId, server, String.valueOf(from));
        if (nowRecord == null || originRecord == null){
            log.info("数据不存在！");
            return null;
        }
        return diffDataPure(nowRecord, originRecord, 0);
    }

    /**
     * 获取指定几天前的战绩，首先库里得有
     * @param accountId     用户id
     * @param server        服务器
     * @param day           几天前
     * @return              具体数据
     */
    public static List<ShipDataObj> accountRecordAt(String accountId, Server server, int day){
        log.info("开始数据获取!");
        int date = Integer.parseInt(DateUtil.format(new Date(), "YYYYMMdd"));
        if (day == 0){
            return diffShip(accountId, server);
        }
        int nowData = date;
        int originData = nowData - 1;
        JsonNode nowRecord = readPlayerData(accountId, server, String.valueOf(nowData));
        JsonNode originRecord = readPlayerData(accountId, server, String.valueOf(originData));
        if (nowRecord == null || originRecord == null){
            log.error("查找数据不存在!");
            return null;
        }
        List<ShipDataObj> shipDataObjs = diffDataPure(nowRecord, originRecord, day);
        log.info("{}数据获取完成!", nowData);
        return shipDataObjs;
    }
}
