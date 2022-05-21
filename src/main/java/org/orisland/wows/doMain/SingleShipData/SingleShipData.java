/**
  * Copyright 2022 json.cn 
  */
package org.orisland.wows.doMain.SingleShipData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleShipData {

    private Pvp pvp;
    private long last_battle_time;
    private long account_id;
    private int distance;
    private long updated_at;
    private int battles;
    private long ship_id;
    private String private1;
}