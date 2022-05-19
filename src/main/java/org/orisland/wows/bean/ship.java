package org.orisland.wows.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonObject;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ship {
    private JsonNode pvp;
    private String last_battle_time;
    private String account_id;
    private String distance;
    private String updated_at;
    private String battles;
    private String ship_id;

    private String xp;
    private String hits;
    private String shots;
    private String frags;


    public void setPvp(JsonNode pvp) {
        this.xp = pvp.get("xp").asText();
        this.hits = pvp.at("/main_battery/hits").asText();
        this.shots = pvp.at("/main_battery/shots").asText();
        this.frags = pvp.at("/main_battery/frags").asText();
        this.pvp = pvp;
    }
}
