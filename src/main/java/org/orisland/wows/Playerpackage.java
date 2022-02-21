package org.orisland.wows;


import wows.player;

//组装信息
public class Playerpackage {
    String spackage = "";
    String pic;
    public Playerpackage(String msg, int flag){
        wows.WowsInfosImpl tst = new wows.WowsInfosImpl();
        String[] msgSplit = msg.split( " ");
        String uid;
        player player = null;
        System.out.println("查水表进程启动");
        if (flag == 0){
            uid = tst.getUserId(msgSplit[1],0);
            System.out.println("欧服模式");
            player = tst.getUserBasicInfo(uid, 0);
        }
        else {
            uid = tst.getUserId(msgSplit[1],1);
            System.out.println("亚服模式");
            player = tst.getUserBasicInfo(uid, 1);
        }
        System.out.println("用户数据获取完成");
        spackage += "查询id："+player.nickname+"\n";
        spackage += "战斗场数："+player.battles + "\n";
        spackage += "平均胜率："+player.win_rate + "%\n";
        spackage += "平均伤害："+player.ave_damage + "\n";
        spackage += "平均经验："+player.ave_xp + "\n";
        spackage += "KD："+player.KD + "\n";
        spackage += "存活率："+player.survived_rate + "%\n";
        spackage += "存活胜利率："+player.win_servived_rate + "%\n";
        spackage += "主炮命中率："+player.hit_rate + "%\n";
        spackage += "副炮命中率："+player.sen_hit_rate + "%\n";
        spackage += "鱼雷命中率："+player.torpedoes_rate + "%\n";
        spackage += "最高伤害："+player.name_max_damage_boat_id+"："+player.max_damage_dealt+"\n";
        spackage += "最高经验："+player.name_max_xp_ship_id+"："+player.max_xp+"\n";
        spackage += "最高击杀："+player.name_max_frags_boat_id+"："+player.max_single_kill+"\n";
        spackage += "最高鱼雷击杀："+player.name_torpedoes_max_frags_ship_id+"："+player.torpedoes_max_frags_battle + "\n";
        pic = pic(String.valueOf(player.user_id));
        pic = "[netpic:"+ pic + "]";
        //spackage += pic + "\n";
        //System.out.println(pic);
        System.out.println("打包完毕");
    }

    public String getSpackage() {
        return spackage;
    }

    public void setSpackage(String spackage) {
        this.spackage = spackage;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public static String pic(String uid){
        String url = "https://static.wows-numbers.com/wows/";
        url += uid + ".png";

        return url;
    }
}
