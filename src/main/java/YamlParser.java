import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class YamlParser {
     public Map<String, Object> readyaml( ) {
        String yamlFilePath="config.yaml";
        Yaml yaml = new Yaml(); 
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(yamlFilePath);
        Map<String, Object> obj = yaml.load(inputStream);
        return obj;
    }

    public YamlParser(){}

    @Override
    public String toString() {
        return "Hello World";
    }
}