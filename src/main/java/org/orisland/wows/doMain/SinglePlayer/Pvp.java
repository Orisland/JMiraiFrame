/**
  * Copyright 2022 json.cn 
  */
package org.orisland.wows.doMain.SinglePlayer;

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
    private long max_ships_spotted_ship_id;
    private long max_damage_scouting;
    private long art_agro;
    private long max_xp_ship_id;
    private int ships_spotted;
    private Second_battery second_battery;
    private long max_frags_ship_id;
    private long xp;
    private int survived_battles;
    private int dropped_capture_points;
    private int max_damage_dealt_to_buildings;
    private long torpedo_agro;
    private int draws;
    private int control_captured_points;
    private int battles_since_510;
    private long max_total_agro_ship_id;
    private int planes_killed;
    private int battles;
    private int max_ships_spotted;
    private String max_suppressions_ship_id;
    private int survived_wins;
    private int frags;
    private long damage_scouting;
    private long max_total_agro;
    private int max_frags_battle;
    private int capture_points;
    private Ramming ramming;
    private int suppressions_count;
    private int max_suppressions_count;
    private Torpedoes torpedoes;
    private long max_planes_killed_ship_id;
    private Aircraft aircraft;
    private long team_capture_points;
    private int control_dropped_points;
    private long max_damage_dealt;
    private String max_damage_dealt_to_buildings_ship_id;
    private long max_damage_dealt_ship_id;
    private int wins;
    private int losses;
    private long damage_dealt;
    private int max_planes_killed;
    private long max_scouting_damage_ship_id;
    private long team_dropped_capture_points;
    private int battles_since_512;
}