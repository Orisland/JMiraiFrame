package org.orisland.wows.dataPack;

import Tool.JsonTool;
import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.orisland.wows.ApiConfig;

import static org.orisland.wows.ApiConfig.Bind;
import static org.orisland.wows.ApiConfig.dataDir;
import static org.orisland.wows.dataPack.PlayerData.*;
import static org.orisland.wows.dataPack.ServerData.ServerToDir;

@Slf4j
public class BindData {
    /**
     * qq绑定游戏用户
     * @param qq        qq号
     * @param accountId 账户id
     * @param server    区服
     */
    public static void bindQQAccountId(String qq, String accountId, ApiConfig.Server server){
        ObjectNode bind = (ObjectNode) Bind;
        JsonNode jsonNode = searchAccountIdToAccountInfo(accountId, server);
        String s = ServerToDir(server);
        saveAccountShipInfo(accountId, server);
        originPlayerData(accountId, server);
        ObjectNode objectNode = JsonTool.mapper.createObjectNode();
        objectNode.put("id", accountId);
        objectNode.put("server", server.toString());
        objectNode.put("name", jsonNode.get("nickname").asText());
        bind.set(qq, objectNode);
        Bind = bind;
        FileUtil.writeUtf8String(bind.toString(), dataDir + "Bind.json");
        log.info("{}绑定{}{}已完成!", qq, server, accountId);
    }

    /**
     * 更新qq绑定
     * @param qq        qq号
     * @param accountId 用户id
     * @param server    区服
     */
    public static void updateBind(String qq, String accountId, ApiConfig.Server server){
        ObjectNode bind = (ObjectNode) Bind;
        JsonNode jsonNode = bind.get(qq);
        if (jsonNode == null){
            log.warn("该qq并未绑定账号数据！");
        }else {
            bind.remove(qq);
            bindQQAccountId(qq, accountId, server);
        }
    }
}
