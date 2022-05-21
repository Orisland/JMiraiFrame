package org.orisland.wows.doMain;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class SingleShipSimple {
    private String Nation;
    private String ShipId;
    private int Tier;
    private String type;
    private String CNname;
    private String ENname;
    private boolean premium;
}
