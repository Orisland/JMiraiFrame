package org.orisland.wows.doMain;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * 用于最终展示
 */
@Data
@ToString
public class PlayerObj {
    private Integer PR;
    private Date LastBattleTime;
    private Date CreateTime;
    private String winRate;
    private String DamageAve;
    private Long ExpAve;
    private Long Battle;
    private String SurviveForWinRate;
    private String SurviveRate;
    private String HitRate;
    private String PlaneAve;
}
