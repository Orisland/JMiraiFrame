import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.javac.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhaolong
 * @Time: 12:54 上午
 * @Date: 2021年07月04日 00:54
 **/
public class jacksontest {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Person person = new Person();
        person.setName("Tom");
        person.setAge(40);

        //写成text
        String text = mapper.writeValueAsString(person);

        //写成字节流
        byte[] bytes = mapper.writeValueAsBytes(person);

        //写成文件
        mapper.writeValue(new File("Person.json"),person);

        //读字节流，读string，读文件
//        System.out.println(mapper.readValue(bytes, Person.class));
//        System.out.println(mapper.readValue(text, Person.class));
//        System.out.println(mapper.readValue(new File("Person.json"), Person.class));

        Map<String, Object> maps = new HashMap<>();
        maps.put("new", 10);
        maps.put("what", 20);
        maps.put("boat", "Montana");
        maps.put("list", new String[]{"what", "shit"});

        String text2 = mapper.writeValueAsString(maps);
        System.out.println(text2);

        Map<String, Object> mp2 = mapper.readValue(text2, new TypeReference<Map<String, Object>>() {});

        JsonNode jsonNode = mapper.readTree(text2);
        String newstr = jsonNode.get("new").asText();
        int what = jsonNode.get("what").asInt();

        List<JsonNode> strings = jsonNode.findValues("list");
        System.out.println(strings);

        System.out.println(mp2);


//        System.out.println(text);
    }
}

class Person {
    String name;
    int age;

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
