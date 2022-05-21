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
public class Pvp {

    private int max_xp;
    private int damage_to_buildings;
    private Main_battery main_battery;
    private int suppressions_count;
    private long max_damage_scouting;
    private long art_agro;
    private int ships_spotted;
    private Second_battery second_battery;
    private long xp;
    private int survived_battles;
    private int dropped_capture_points;
    private int max_damage_dealt_to_buildings;
    private long torpedo_agro;
    private int draws;
    private int battles_since_510;
    private int planes_killed;
    private int battles;
    private int max_ships_spotted;
    private long team_capture_points;
    private int frags;
    private long damage_scouting;
    private long max_total_agro;
    private int max_frags_battle;
    private int capture_points;
    private Ramming ramming;
    private Torpedoes torpedoes;
    private Aircraft aircraft;
    private int survived_wins;
    private long max_damage_dealt;
    private int wins;
    private int losses;
    private long damage_dealt;
    private int max_planes_killed;
    private int max_suppressions_count;
    private int team_dropped_capture_points;
    private int battles_since_512;
}