package org.orisland.wows.DataPack;

import Tool.FileTool;
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
import org.orisland.wows.doMain.Pr.ShipPr;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.SinglePlayer.SinglePlayer;
import org.orisland.wows.doMain.SingleShipData.Pvp;
import org.orisland.wows.doMain.SingleShipData.SingleShipData;
import org.orisland.wows.doMain.SingleShipDataSimple;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static org.orisland.wows.ApiConfig.*;
import static org.orisland.wows.DataPack.ShipData.*;

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
            JsonNode jsonNode = searchAccountIdToAccountInfo(accountId, server);
            if (!jsonNode.get("status").asText().equals("ok")){
                log.info("api访问错误！");
                return null;
            }
            singlePlayer = JsonTool.mapper.readValue(jsonNode.get("data").get(String.valueOf(accountId)).toString(), SinglePlayer.class);
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
    public static String searchNickNameToAccountId(String username, ApiConfig.Server server){
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

    /**
     * 通过accountid获取该用户的pr
     * @param accountId 用户id
     * @param server    用户所在服务器
     * @return          pr数据
     */
    public static JsonNode AccountIdToPr(String accountId, Server server, String shipId){
        List<SingleShipData> singleShipData = SearchAccountIdToShipInfo(accountId, shipId, server);
        double actDmg = 0.0;
        double expDmg = 0.0;
        double actWins = 0.0;
        double expWins = 0.0;
        double actFrags = 0.0;
        double expFrags = 0.0;
        for (SingleShipData singleShipDatum : singleShipData) {
            Pvp pvp = singleShipDatum.getPvp();
            actDmg += pvp.getDamage_dealt();
            actFrags += pvp.getFrags();
            actWins += pvp.getWins();
            int battles = pvp.getBattles();
            JsonNode shipExpected = ShipToExpected(String.valueOf(singleShipDatum.getShip_id()));
            expDmg += shipExpected.get("average_damage_dealt").asDouble() * battles;
            expWins += shipExpected.get("win_rate").asDouble() / 100 * battles;
            expFrags +=  shipExpected.get("average_frags").asDouble() * battles;
        }

        ShipPr shipPr = new ShipPr();
        shipPr.setActualFrags(actFrags);
        shipPr.setActualDmg(actDmg);
        shipPr.setActualWins(actWins);
        shipPr.setExpectedWins(expWins);
        shipPr.setExpectedFrags(expFrags);
        shipPr.setExpectedDmg(expDmg);

        return PrStandard(shipPr.PrCalculate());
    }

    /**
     * 通过昵称查询用户全部船只的综合pr
     * @param NickName  昵称
     * @param server    区服
     * @return  pr信息
     */
    public static JsonNode NickNameToPr(String NickName, Server server){
        return AccountIdToPr(searchNickNameToAccountId(NickName, server), server, "");
    }

    /**
     * 查询指定用户指定船只id的pr
     * @param accountId 用户id
     * @param server    服务器
     * @param shipId    船只id
     * @return          pr结果
     */
    public static JsonNode AccountIdShipToPr(String accountId, Server server, String shipId){
        return AccountIdToPr(accountId, server, shipId);
    }

    /**
     * 查询指定用户名指定昵称的pr
     * @param NickName  昵称
     * @param server    服务器
     * @param ship      船只
     * @return          数据
     */
    public static JsonNode NickNameShipToPr(String NickName, Server server, String ship){
        return AccountIdShipToPr(searchNickNameToAccountId(NickName, server), server, ship);
    }

    /**
     * 读取指定用户id的当日json数据
     * @param accountid 用户id
     * @param server    服务器
     * @return          读取的json数据
     */
    public static JsonNode readAccount(String accountid, Server server){
        File[] files = null;

        switch (server){
            case EU:
                files = FileTool.readDir(dataDirEu);
                return readJson(files, accountid);
            case NA:
                files = FileTool.readDir(dataDirNa);
                return readJson(files, accountid);
            case RU:
                files = FileTool.readDir(dataDirRu);
                return readJson(files, accountid);
            case ASIA:
                files = FileTool.readDir(dataDirAsia);
                return readJson(files, accountid);
            default:
                log.warn("出现了意料之外的变量！");
                return null;
        }
    }

//    私有数据处理方法
    private static JsonNode readJson(File[] files, String accountid){
        for (File file : files) {
            if (file.getName().contains(accountid)){
                try {
                    System.out.println(file.getName());
                    return JsonTool.mapper.readTree(FileUtil.readUtf8String(dataDirEu + file.getName()));
                }catch (JsonProcessingException e){
                    e.printStackTrace();
                }
            }
        }
        return null;
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

    /**
     * 根据账户id将数据存入data目录
     * 仅在每日0时数据更新时启用!
     * 仅在每日0时数据更新时启用!
     * @param accountId 账户id
     * @param server    区服
     */
    public static void saveAccountShipInfo(String accountId, Server server){
        String date = DateUtil.format(DateUtil.date(), "YYYYMMdd");
        JsonNode data = shipDataStandard(accountId, server);
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
        if (accountDataList.size() >= 3){
            accountDataList.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    Long l1 = Long.parseLong(o1.split(",")[1].split("\\.")[0]);
                    Long l2 = Long.parseLong(o2.split(",")[1].split("\\.")[0]);
                    return order ? ((Long)(l1-l2)).intValue() : ((Long)(l2-l1)).intValue();
                }
            });
            log.info("需要进行删除替换的文件为{}",accountDataList.get(0));
            return ServerToDir(server) + accountDataList.get(0);
        }else {
            log.info("数据量不足三个无需进行删除！");
            return null;
        }
    }

    /**
     * 数据更新操作，删除旧的替换为新的，删除默认最久数据
     * @param accountId 用户id
     * @param server    区服信息
     */
    public static void updateAccountLocalData(String accountId, Server server){
        String s = selectData(accountId, server, true);
        if (s == null){
            log.info("更新被跳过！");
        }else {
            if (FileUtil.del(s)){
                saveAccountShipInfo(accountId, server);
            }else {
                log.warn("旧数据删除失败，数据更新失败！");
            }
        }
    }

    /**
     * 初始化时执行的执行操作
     * 该操作自动更新已绑定的所有用户数据
     */
    public static void updateAccountLocalDataAuto(){
        for (JsonNode jsonNode : Bind) {
            updateAccountLocalData(jsonNode.get("accountid").asText(), StringToServer(jsonNode.get("server").asText()));
        }
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
                return dataDirNa;
            default:
                log.error("出现了意料之外的数据！");
                return null;
        }
    }

    /**
     * 选择最新的数据
     * @param accountId
     * @param server
     * @return
     */
    public static String selectDataNewest(String accountId, Server server){
        return selectData(accountId, server, false);
    }


    /**
     * 读取指定日期的
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
            return JsonTool.mapper.readTree(FileUtil.readUtf8String(serverDir + fileName));

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
     * 将qq转为具体的账号信息
     * @param qq    qq
     * @return      具体信息
     */
    public static Bind findAccountId(String qq){
        Bind bind = new Bind();
        JsonNode jsonNode = Bind.get(qq);
        if (jsonNode == null){
            log.warn("改qq未绑定账号数据！");
            return null;
        }
        bind.setAccountId(jsonNode.get("accountid").asText());
        bind.setQq(qq);
        bind.setServer(StringToServer(jsonNode.get("server").asText()));
        return bind;
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
            case "NA":
                return Server.NA;
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
        JsonNode LocalData = readAccount(accountid, server);

        JsonNode onlineDate = playerData.get("date");
        JsonNode onlineData = playerData.get("data");

        JsonNode offlineDate = LocalData.get("date");
        JsonNode offlineData = LocalData.get("data");

        DateTime today = DateUtil.date();

        List<ShipDataObj> res = new ArrayList<>();

        for (JsonNode datum : onlineData) {
            try {
                DateTime LastBattleTime = DateUtil.date(datum.get("last_battle_time").asLong() * 1000);
                if (LastBattleTime.year() == today.year() && LastBattleTime.dayOfYear() == today.dayOfYear()){
                    SingleShipData OnlineSingleShipData = JsonTool.mapper.readValue(datum.toString(), SingleShipData.class);
                    SingleShipData OffineSingleShipData = null;
                    if (offlineData.get(String.valueOf(OnlineSingleShipData.getShip_id())) == null){
                        log.info("未发现该船只，配置为出现了数据本地数据之外的新船！");
                        OffineSingleShipData = new SingleShipData();
                        OffineSingleShipData.setPvp(new Pvp());
                    }else {
                        OffineSingleShipData = JsonTool.mapper.readValue(offlineData.get(String.valueOf(OnlineSingleShipData.getShip_id())).toString(), SingleShipData.class);
                    }
                    Pvp OnlinePvp = OnlineSingleShipData.getPvp();
                    Pvp OfflinePvp = OffineSingleShipData.getPvp();
                    double Dmg = OnlinePvp.getDamage_dealt() - OfflinePvp.getDamage_dealt();
                    int Battle = OnlinePvp.getBattles() - OfflinePvp.getBattles();
                    double Wins = OnlinePvp.getWins() - OfflinePvp.getWins();
                    double Frags = OnlinePvp.getFrags() - OfflinePvp.getFrags();

                    ShipPr shipPr = new ShipPr();
                    shipPr.setActualWins(Wins);
                    shipPr.setActualFrags(Frags);
                    shipPr.setActualDmg(Dmg);
                    shipPr.setShipId(String.valueOf(OnlineSingleShipData.getShip_id()));
                    shipPr.setBattle(Battle);
                    shipPr.update();

                    ShipDataObj shipDataObj = new ShipDataObj();
                    shipDataObj.setShoot(OnlinePvp.getMain_battery().getShots() - OfflinePvp.getMain_battery().getShots());
                    shipDataObj.setHit((long) (OnlinePvp.getMain_battery().getHits() - OfflinePvp.getMain_battery().getHits()));
                    shipDataObj.setWins((long) (OnlinePvp.getWins() - OfflinePvp.getWins()));
                    shipDataObj.setDmg(OnlinePvp.getDamage_dealt() - OfflinePvp.getDamage_dealt());
                    shipDataObj.setKill((long) (OnlinePvp.getFrags() - OfflinePvp.getFrags()));
                    shipDataObj.setSurvive((long) (OnlinePvp.getSurvived_battles() - OfflinePvp.getSurvived_battles()));
                    shipDataObj.setSurviveWin((long) (OnlinePvp.getSurvived_wins() - OfflinePvp.getSurvived_wins()));
                    shipDataObj.setXp(OnlinePvp.getXp() - OfflinePvp.getXp());
                    shipDataObj.setBattle(Battle);
                    shipDataObj.setPR(shipPr);
                    shipDataObj.setShip(SearchShipIdToShipInfo(String.valueOf(OnlineSingleShipData.getShip_id())));

                    shipDataObj.update();

                    res.add(shipDataObj);
                }

            }catch (JsonProcessingException e){
                e.printStackTrace();
            }

        }
        log.info("数据对比完成!");
        return res;
    }
}
