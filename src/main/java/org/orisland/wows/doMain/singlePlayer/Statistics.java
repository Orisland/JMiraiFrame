/**
  * Copyright 2022 json.cn 
  */
package org.orisland.wows.doMain.singlePlayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Statistics {

    private long distance;
    private int battles;
    private Pvp pvp;

}