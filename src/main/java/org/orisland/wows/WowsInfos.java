package org.orisland.wows;

import org.orisland.wows.bean.player;

import java.io.IOException;

public interface WowsInfos {

    String getUserId(String username, int flag) throws IOException;

    player getUserBasicInfo(String uid, int flag) throws IOException;

    String getShipInfo(Long uid, int flag) throws IOException;

}
