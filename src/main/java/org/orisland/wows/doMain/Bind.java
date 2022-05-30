package org.orisland.wows.doMain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;
import org.orisland.wows.ApiConfig;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bind {
    String qq;
    String accountId;
    String accountName;
    Long regTime;
    ApiConfig.Server Server;
}
