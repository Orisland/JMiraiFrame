package org.orisland.wows.doMain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleShipDataSimple {
    private int capture_points;
    private long account_id;
    private int max_xp;
    private int wins;
    private int planes_killed;
    private int battles;
    private long damage_dealt;
    private String battle_type;
    private String date;
    private long xp;
    private int frags;
    private int survived_battles;
    private int dropped_capture_points;
}
