package Tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;

/**
 * @Author: zhaolong
 * @Time: 13:18
 * @Date: 2021年07月10日 13:18
 **/
public class ymlReader {

    /**
     * yml读取器
     * @return 返回json
     * @throws JsonProcessingException
     */
    public static JsonNode getYmlConfig(String name) throws JsonProcessingException {
        Yaml yaml = new Yaml();
        LinkedHashMap<String, Object> linkedHashMap = null;
        try {
            linkedHashMap = yaml.load(ymlReader.class.getClassLoader().getResourceAsStream("config.yml"));
        }catch (Exception e){
            e.printStackTrace();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        assert linkedHashMap != null : "yml reader error: null";
        return objectMapper.readTree(objectMapper.writeValueAsString(linkedHashMap.get(name)));
    }
}
