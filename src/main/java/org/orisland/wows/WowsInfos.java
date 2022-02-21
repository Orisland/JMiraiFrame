package org.orisland.wows;

import wows.player;

public interface WowsInfos {

    String getUserId(String username, int flag);

    player getUserBasicInfo(String uid, int flag);

    String getShipInfo(Long uid, int flag);

}
