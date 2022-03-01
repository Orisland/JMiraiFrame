package org.orisland.wows.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class boat {     //后来想想wiki还是免了，这要做wiki上上下下要写十几条方法

    public Long ship_id;
    public String ship_name;
}
