package org.orisland.wows.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.ToString;

/**
 * @Author: zhaolong
 * @Time: 19:05
 * @Date: 2022年02月21日 19:05
 **/
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class id_infoSearchResult {
    private String status;
    private JsonNode meta;
    private JsonNode data;
}




