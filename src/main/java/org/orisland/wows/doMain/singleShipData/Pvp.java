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
public class Pvp {

    private int max_xp = 0;
    private int damage_to_buildings = 0;
    private Main_battery main_battery = new Main_battery();
    private int suppressions_count = 0;
    private long max_damage_scouting = 0;
    private long art_agro = 0;
    private int ships_spotted = 0;
    private Second_battery second_battery;
    private long xp = 0;
    private int survived_battles = 0;
    private int dropped_capture_points = 0;
    private int max_damage_dealt_to_buildings = 0;
    private long torpedo_agro = 0;
    private int draws = 0;
    private int battles_since_510 = 0;
    private int planes_killed = 0;
    private int battles = 0;
    private int max_ships_spotted = 0;
    private long team_capture_points = 0;
    private int frags = 0;
    private long damage_scouting = 0;
    private long max_total_agro = 0;
    private int max_frags_battle = 0;
    private int capture_points = 0;
    private Ramming ramming ;
    private Torpedoes torpedoes;
    private Aircraft aircraft;
    private int survived_wins = 0;
    private long max_damage_dealt = 0;
    private int wins = 0;
    private int losses = 0;
    private long damage_dealt= 0;
    private int max_planes_killed= 0;
    private int max_suppressions_count= 0;
    private int team_dropped_capture_points= 0;
    private int battles_since_512= 0;
}