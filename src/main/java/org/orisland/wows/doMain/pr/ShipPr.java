package org.orisland.wows.doMain.pr;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.ToString;

import static org.orisland.wows.dataPack.ShipData.PrStandard;
import static org.orisland.wows.dataPack.ShipData.ShipToExpected;

@ToString
@Data
public class ShipPr {
    private String shipId;
    private int battle;
    private double actualDmg;
    private double expectedDmg;
    private double actualWins;
    private double expectedWins;
    private double actualFrags;
    private double expectedFrags;

    private int PR;
    private String evaluate;
    private String distance;
    private String color;

    /**
     * pr计算
     * pr算法:<a href="https://wows-numbers.com/personal/rating">...</a>
     * @return 返回pr结果
     */
    public double PrCalculate(){
        double rDmg = actualDmg / expectedDmg;
        double rWins = actualWins / expectedWins;
        double rFrags = actualFrags / expectedFrags;

        double nDmg = Math.max(0, (rDmg - 0.4) / (1 - 0.4));
        double nFrags = Math.max(0, (rFrags - 0.1) / (1 - 0.1));
        double nWins = Math.max(0, (rWins - 0.7) / (1 - 0.7));
        return 700 * nDmg + 300 * nFrags + 150 * nWins;
    }

    /**
     * 通过初始化shipid自动计算
     */
    public void updateExpected(){
        JsonNode shipExpected = ShipToExpected(shipId);
        expectedDmg = shipExpected.get("average_damage_dealt").asDouble() * battle;
        expectedWins = shipExpected.get("win_rate").asDouble() / 100 * battle;
        expectedFrags =  shipExpected.get("average_frags").asDouble() * battle;
    }

    /**
     * 计算pr
     */
    public JsonNode update(){
        if (!shipId.equals("")){
            updateExpected();
        }

        JsonNode jsonNode = PrStandard(PrCalculate());
        this.setPR(jsonNode.get("pr").asInt());
        this.setEvaluate(jsonNode.get("evaluate").asText());
        this.setDistance(jsonNode.get("distance").asText());
        this.setColor(jsonNode.get("color").asText());
        return jsonNode;
    }
}
