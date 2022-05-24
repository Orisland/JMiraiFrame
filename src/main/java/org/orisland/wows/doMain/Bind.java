package org.orisland.wows.doMain;

import lombok.Data;
import lombok.ToString;
import org.orisland.wows.ApiConfig;

@Data
@ToString
public class Bind {
    String qq;
    String accountId;
    String accountName;
    ApiConfig.Server Server;
}
