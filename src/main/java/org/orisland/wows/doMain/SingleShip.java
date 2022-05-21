package org.orisland.wows.doMain;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class SingleShip {
    @JsonRawValue
    private JsonNode Images;
    private String Nation;
    private boolean isPremium;
    private String ShipId;
    private int Tier;
    private String type;
    private boolean isSpecial;
    private String name;
}
