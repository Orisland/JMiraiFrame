package Tool;

import org.orisland.wows.bean.rankSingle;

import static org.orisland.wows.WowsApiConfig.RankList;

/**
 * @Author: zhaolong
 * @Time: 00:57
 * @Date: 2022年02月26日 00:57
 **/
public class findRank {
    //获取选择框的里的颜色
    public static String findColor(String str){
        for (rankSingle s : RankList) {
            if (str.contains(s.getEu())){
                return s.getZh();
            }
        }
        return null;
    }
}
