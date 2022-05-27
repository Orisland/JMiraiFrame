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
import org.orisland.wows.doMain.singleShipData.Main_battery;
import org.orisland.wows.doMain.singleShipData.Pvp;
import org.orisland.wows.doMain.singleShipData.Rank_solo;
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
                        originSingleShipData.setRank_solo(new Rank_solo());
                    }else {
                        originSingleShipData = JsonTool.mapper.readValue(originData.get(String.valueOf(nowSingleShipData.getShip_id())).toString(), SingleShipData.class);
                    }

                    Rank_solo nowRank = nowSingleShipData.getRank_solo();
                    Rank_solo originRank = originSingleShipData.getRank_solo();

                    Pvp nowPvp = nowSingleShipData.getPvp();
                    Pvp originPvp = originSingleShipData.getPvp();

                    double Dmg = nowPvp.getDamage_dealt() - originPvp.getDamage_dealt();
                    int Battle = nowPvp.getBattles() - originPvp.getBattles();
                    double Wins = nowPvp.getWins() - originPvp.getWins();
                    double Frags = nowPvp.getFrags() - originPvp.getFrags();

                    double rankDmg = nowRank.getDamage_dealt() - originRank.getDamage_dealt();
                    int rankBattle = nowRank.getBattles() - originRank.getBattles();
                    double rankWins = nowRank.getWins() - originRank.getWins();
                    double rankFrags = nowRank.getFrags() - originRank.getFrags();

                    ShipPr shipPr =  new ShipPr();
                    ShipPr shipPrRank = new ShipPr();

                    shipPr.setShipId(String.valueOf(nowSingleShipData.getShip_id()));
                    shipPrRank.setShipId(String.valueOf(nowSingleShipData.getShip_id()));

//                    随机为0
                    if (Battle == 0){
//                        这个船没打随机又没打排位
//                        推测为人机或者活动
                        if (rankBattle == 0){
                            log.info("判定为人机或者活动，船只跳过");
                            continue;
//                            只打了rank
                        }else {
                            shipPrRank.setActualWins(rankWins);
                            shipPrRank.setActualFrags(rankFrags);
                            shipPrRank.setActualDmg(rankDmg);
                            shipPrRank.setBattle(rankBattle);
                        }
//                        打了随机
                    }else {
//                        只打了随机
                        if (rankBattle == 0){
                            shipPr.setActualWins(Wins);
                            shipPr.setActualFrags(Frags);
                            shipPr.setActualDmg(Dmg);
                            shipPr.setBattle(Battle);
//                            这个船既打了随机又打了rank
                        }else {
//                            考虑两种情况
                            shipPr.setActualWins(Wins);
                            shipPr.setActualFrags(Frags);
                            shipPr.setActualDmg(Dmg);
                            shipPr.setBattle(rankBattle);

                            shipPrRank.setActualWins(rankWins);
                            shipPrRank.setActualFrags(rankFrags);
                            shipPrRank.setActualDmg(rankDmg);
                            shipPrRank.setBattle(rankBattle);
                        }
                    }

                    try {
                        if (shipPr.getBattle() != 0)
                            shipPr.update();
                        if (shipPrRank.getBattle() != 0)
                            shipPrRank.update();
                    }catch (Exception e){
                        e.printStackTrace();
                        log.error("错误的数据！");
                        continue;
                    }

                    ShipDataObj shipDataObj = new ShipDataObj();
                    ShipDataObj shipDataObjRank = new ShipDataObj();

                    if (shipPr.getBattle() != 0)
                        shipDataPack(Battle,
                                shipPr,
                                shipDataObj,
                                nowPvp.getMain_battery(),
                                originPvp.getMain_battery(),
                                nowPvp.getWins(),
                                originPvp.getWins(),
                                nowPvp.getDamage_dealt(),
                                originPvp.getDamage_dealt(),
                                nowPvp.getFrags(),
                                originPvp.getFrags(),
                                nowPvp.getSurvived_battles(),
                                originPvp.getSurvived_battles(),
                                nowPvp.getSurvived_wins(),
                                originPvp.getSurvived_wins(),
                                nowPvp.getXp(),
                                originPvp.getXp(),
                                nowRank,
                                originRank);

                    if (shipPrRank.getBattle() != 0)
                        shipDataPack(rankBattle,
                                shipPrRank,
                                shipDataObjRank,
                                nowRank.getMain_battery(),
                                originRank.getMain_battery(),
                                nowRank.getWins(),
                                originRank.getWins(),
                                nowRank.getDamage_dealt(),
                                originRank.getDamage_dealt(),
                                nowRank.getFrags(),
                                originRank.getFrags(),
                                nowRank.getSurvived_battles(),
                                originRank.getSurvived_battles(),
                                nowRank.getSurvived_wins(),
                                originRank.getSurvived_wins(),
                                nowRank.getXp(),
                                nowRank.getXp(),
                                nowRank,
                                originRank);

                    shipDataObj.setShip(SearchShipIdToShipInfo(String.valueOf(nowSingleShipData.getShip_id())));
                    shipDataObj.setRank(false);
                    shipDataObjRank.setShip(SearchShipIdToShipInfo(String.valueOf(nowSingleShipData.getShip_id())));
                    shipDataObjRank.setRank(true);

                    if (shipDataObj.getBattle() != 0)
                        shipDataObj.update();
                    if (shipDataObjRank.getBattle() != 0)
                        shipDataObjRank.update();

                    if (shipDataObj.getBattle() != 0)
                        res.add(shipDataObj);
                    if (shipDataObjRank.getBattle() != 0)
                        res.add(shipDataObjRank);
                }

            }catch (JsonProcessingException e){
                e.printStackTrace();
            }

        }
        return res;
    }

    /**
     * 数据打包
     */
    private static void shipDataPack(int battle, ShipPr shipPr, ShipDataObj shipDataObj, Main_battery main_battery, Main_battery main_battery2, int wins, int wins2, long damage_dealt, long damage_dealt2, int frags, int frags2, int survived_battles, int survived_battles2, int survived_wins, int survived_wins2, long xp, long xp2, Rank_solo nowRank, Rank_solo originRank) {
        shipDataObj.setShoot(main_battery.getShots() - main_battery2.getShots());
        shipDataObj.setHit((long) (main_battery.getHits() - main_battery2.getHits()));
        shipDataObj.setWins((long) (wins - wins2));
        shipDataObj.setDmg((long)damage_dealt - damage_dealt2);
        shipDataObj.setKill((long) (frags - frags2));
        shipDataObj.setSurvive((long) (survived_battles - survived_battles2));
        shipDataObj.setSurviveWin((long) (survived_wins - survived_wins2));
        shipDataObj.setXp((long)xp - xp2);
        shipDataObj.setBattle(battle);
        shipDataObj.setPR(shipPr);
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

    public static List<ShipDataObj> diffRank(String accountId, ApiConfig.Server server){
        log.info("开始数据对比！");
        JsonNode playerData = shipDataStandard(accountId,server);
        JsonNode LocalData = readAccountToday(accountId, server);
        log.info("数据对比结束！");
        return null;
    }
}
