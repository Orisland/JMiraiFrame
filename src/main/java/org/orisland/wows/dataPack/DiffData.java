package org.orisland.wows.dataPack;

import Tool.JsonTool;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.singleShipData.Pvp;
import org.orisland.wows.doMain.singleShipData.SingleShipData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.ShipData.SearchShipIdToShipInfo;

@Slf4j
public class DiffData {

    /**
     * 查找在线数据与本地数据的diff船只计算当日的打船数据
     * @param accountid 用户id
     * @param server    区服
     * @return          数据列表
     */
    public static List<ShipDataObj> diffShip(String accountid , ApiConfig.Server server) {
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
    public static List<ShipDataObj> diffDataBetween(String accountId, ApiConfig.Server server, int from, int to){
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
}
