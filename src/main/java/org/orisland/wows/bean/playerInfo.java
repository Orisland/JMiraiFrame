package org.orisland.wows.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @Author: zhaolong
 * @Time: 11:19
 * @Date: 2022年02月22日 11:19
 **/
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class playerInfo {
    //需要展示的部分:
    private Long battles;
    private String winRate;
    private Long aveDmg;
    private Long aveExp;
    private String KD;
    private String hitRate;

    //展示所需要的计算部分:
    private Long win;
    private Long Dmg;
    private Long xp;
    private Long kill;
    private Long die;
    private Long hit;
    private Long shoot;

    public void update(){
        BigDecimal hundred = new BigDecimal(100.0);
        winRate = new BigDecimal((1.0 * win) / battles).setScale(4, RoundingMode.HALF_UP).doubleValue() * 100 + "%";
        aveDmg = new BigDecimal((1.0 * Dmg) / battles).setScale(1, RoundingMode.HALF_UP).longValue();
    }


}
