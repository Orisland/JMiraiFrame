package org.orisland.wows.dataPack;

import Tool.JsonTool;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;
import org.orisland.wows.doMain.ShipDataObj;
import org.orisland.wows.doMain.pr.ShipPr;
import org.orisland.wows.doMain.singleShipData.Pvp;
import org.orisland.wows.doMain.singleShipData.Rank_solo;
import org.orisland.wows.doMain.singleShipData.SingleShipData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.orisland.wows.dataPack.PlayerData.searchNickNameToAccountId;
import static org.orisland.wows.dataPack.ShipData.*;
import static org.orisland.wows.dataPack.ShipData.SearchShipIdToShipInfo;

@Slf4j
public class PrData {
    /**
     * 单船的pr查询
     * @param ship 船只战斗数据
     * @return  处理后的pr结果
     */
    public static JsonNode ShipPr(JsonNode ship){
        String shipId = ship.get("shipId").asText();
        JsonNode shipExpected = ShipToExpected(shipId);
        long battle = ship.get("battle").asLong();
        double actualDmg = ship.get("Dmg").asDouble();
        double expectedDmg = shipExpected.get("average_damage_dealt").asDouble() * battle;
        double actualWins = ship.get("Wins").asDouble();
        double expectedWins = shipExpected.get("win_rate").asDouble() / 100 * battle;
        double actualFrags = ship.get("Frags").asDouble();
        double expectedFrags =  shipExpected.get("average_frags").asDouble() * battle;

        ShipPr shipPr = new ShipPr();
        shipPr.setActualFrags(actualFrags);
        shipPr.setExpectedFrags(expectedFrags);
        shipPr.setActualDmg(actualDmg);
        shipPr.setExpectedDmg(expectedDmg);
        shipPr.setActualWins(actualWins);
        shipPr.setExpectedWins(expectedWins);

        return PrStandard(shipPr.PrCalculate());
    }

    /**
     * 由pr数据得知具体级别和颜色
     * @param pr pr
     * @return  打包数据
     */
    public static JsonNode PrInfo(BigDecimal pr){
        int score = Integer.parseInt(String.valueOf(pr));
        String evaluate;
        int distance;
        String color;
        if (score < 750){
            evaluate = "还需努力";
            distance = 750 - score;
            color = "番茄";
        }else if (score < 1100){
            evaluate = "低于平均";
            distance = 1100 - score;
            color = "玉米";
        }else if (score < 1350){
            evaluate = "平均水平";
            distance = 1350 - score;
            color = "蛋黄";
        }else if (score < 1550){
            evaluate = "好";
            distance = 1550 - score;
            color = "小葱";
        }else if (score < 1750){
            evaluate = "很好";
            distance = 1750 - score;
            color = "白菜";
        }else if (score < 2100){
            evaluate = "非常好";
            distance = 2100 - score;
            color = "青菜";
        }else if (score < 2450){
            evaluate = "大佬平均";
            distance = 2450 - score;
            color = "茄子";
        }else if (score < 5000){
            evaluate = "神佬平均";
            distance = score - 2450;
            color = "大茄子";
        } else if (score < 9999){
            evaluate = "您";
            distance = score - 5000;
            color = "钛合金茄子";
        }else {
            log.warn("score异常！");
            evaluate = "未知";
            distance = 0;
            color = "灰";
        }

        ObjectNode objectNode = JsonTool.mapper.createObjectNode();
        objectNode.put("evaluate", evaluate);
        objectNode.put("distance",  "+" + String.valueOf(distance));
        objectNode.put("color", color);

        return objectNode;
    }

    /**
     * pr标准化并添加info数据
     * @param PR 需要标准化的pr
     * @return 标准化pr
     */
    public static JsonNode PrStandard(double PR){
        BigDecimal PrStandard = new BigDecimal(PR).setScale(0, RoundingMode.HALF_UP);
        JsonNode jsonNode = PrInfo(PrStandard);
        ObjectNode objectNode = JsonTool.mapper.createObjectNode();
        objectNode.put("pr", String.valueOf(PrStandard));
        objectNode.setAll((ObjectNode) jsonNode);
        return objectNode;
    }

    /**
     * 通过accountid获取该用户的单船只pr
     * @param accountId 用户id
     * @param server    用户所在服务器
     * @return          pr数据
     */
    public static ShipDataObj AccountIdToPr(String accountId, ApiConfig.Server server, String shipId){
        List<SingleShipData> singleShipData = SearchAccountIdToShipInfo(accountId, shipId, server);
        double actDmg = 0.0;
        double expDmg = 0.0;
        double actWins = 0.0;
        double expWins = 0.0;
        double actFrags = 0.0;
        double expFrags = 0.0;
        Pvp pvp = null;
        Rank_solo  rank_solo  = null;
        int battles = 0;
        int rankBattle = 0;

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
//                rank_solo = singleShipDatum.getRank_solo();

//                battles = singleShipDatum.getBattles() - rank_solo.getBattles();

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
//                if (e.getMessage().equals("Cannot invoke \"com.fasterxml.jackson.databind.JsonNode.get(String)\" because \"shipExpected\" is null")){
//                    log.info("船只期望数据为空！");
//                }
                log.info("跳过异常船只！");
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
    public static ShipDataObj NickNameToPr(String NickName, ApiConfig.Server server){
        return AccountIdToPr(searchNickNameToAccountId(NickName, server), server, "");
    }

    /**
     * 查询指定用户指定船只id的pr
     * @param accountId 用户id
     * @param server    服务器
     * @param shipId    船只id
     * @return          pr结果
     */
    public static ShipDataObj AccountIdShipToPr(String accountId, ApiConfig.Server server, String shipId){
        return AccountIdToPr(accountId, server, shipId);
    }

    /**
     * 查询指定用户id的pr
     * @param accountId 用户id
     * @param server    服务器
     * @return          pr结果
     */
    public static ShipDataObj AccountIdShipToPr(String accountId, ApiConfig.Server server){
        return AccountIdToPr(accountId, server, "");
    }

    /**
     * 查询指定用户名指定昵称的pr
     * @param NickName  昵称
     * @param server    服务器
     * @param ship      船只
     * @return          数据
     */
    public static ShipDataObj NickNameShipToPr(String NickName, ApiConfig.Server server, String ship){
        return AccountIdShipToPr(searchNickNameToAccountId(NickName, server), server, ship);
    }

}
