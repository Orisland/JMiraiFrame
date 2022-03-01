package Tool;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @Author: zhaolong
 * @Time: 00:25
 * @Date: 2022年02月22日 00:25
 **/
public class JsonTool {
    public static ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
    }
}
