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
public class Ramming {

    private int max_frags_battle;
    private int frags;

}