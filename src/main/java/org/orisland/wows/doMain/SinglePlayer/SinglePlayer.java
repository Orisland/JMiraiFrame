/**
  * Copyright 2022 json.cn 
  */
package org.orisland.wows.doMain.SinglePlayer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SinglePlayer {
    private long last_battle_time;
    private long account_id;
    private int leveling_tier;
    private long created_at;
    private int leveling_points;
    private long updated_at;
    @JsonProperty("private")
    private String private1;
    private boolean hidden_profile;
    private long logout_at;
    private String karma;
    private Statistics statistics;
    private String nickname;
    private long stats_updated_at;

}