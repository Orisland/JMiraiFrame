package org.orisland.wows.bean;

/**
 * @Author: zhaolong
 * @Time: 16:47
 * @Date: 2022年02月21日 16:47
 **/
/**
 * Copyright 2022 json.cn
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Auto-generated: 2022-02-21 16:47:3
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class name_idSearchResult {

    @JsonProperty("status")
    private String status;

    private JsonNode meta;
    private List<single> data;
}

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
class single{
    private String nickname;
    private Long account_id;
}
