/**
  * Copyright 2022 json.cn 
  */
package org.orisland.wows.doMain.singleShipData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Main_battery {

    private int max_frags_battle = 0;
    private int frags = 0;
    private int hits = 0;
    private long shots = 0;

}