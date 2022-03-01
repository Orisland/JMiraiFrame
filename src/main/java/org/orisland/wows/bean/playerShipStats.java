package org.orisland.wows.bean;

import Tool.JsonTool;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import kotlinx.serialization.json.Json;
import lombok.Data;
import lombok.ToString;

import java.util.*;

/**
 * 封装了玩家的统计信息
 * 查询了错误的uid时，会返回null
 * {
 * "status": "ok",
 * "meta": {
 * "count": 1,
 * "hidden": null
 * },
 * "data": {
 * "5663164445": null
 * }
 * }
 */
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class playerShipStats {
    private String status;
    private JsonNode meta;
    private JsonNode data;
    private String uid;

    public void setData(JsonNode data) throws JsonProcessingException {
        Iterator<Map.Entry<String, JsonNode>> fields = data.fields();
        this.uid = fields.next().getKey();
        this.data = data.get(uid);
    }
}
