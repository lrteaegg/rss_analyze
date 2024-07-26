package space.mufeng;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import space.mufeng.dao.MysqlConfig;
import space.mufeng.reader.GptHelper;
import space.mufeng.utils.EmailUtil;
import space.mufeng.utils.HttpClientUtil;
import space.mufeng.utils.MD5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YamlConfigLoader {

    public static  List<Integer> NEED_TYPE_LIST;

    public static void init() {
        Yaml yaml = new Yaml(new Constructor(Map.class));
        Map<String, Object> data = null;
        try {
            File configFile = new File(System.getProperty("user.dir") + File.separator + "application.yml");
            InputStream inputStream = new FileInputStream(configFile);
            data = yaml.load(inputStream);
        } catch (Exception e) {
            System.out.println("Error in reading YAML file");
            throw new RuntimeException(e);
        }

        System.out.println(data);
        Map<String, Object> rss = (Map<String, Object>) data.get("rss");
        if (rss != null) {
            HttpClientUtil.BASE_URL = (String) rss.get("url");
            String user = (String) rss.get("user");
            String password = (String) rss.get("password");
            HttpClientUtil.API_KEY = MD5Util.encrypt(user + ":" + password);
            // string "8,46" 转 list
            String[] needReads = ((String) rss.get("need-read")).replace(" ", "").split(",");
            NEED_TYPE_LIST = java.util.Arrays.stream(needReads).map(Integer::parseInt).toList();
        }


        // mysql
        Map<String, Object> mysql = (Map<String, Object>) data.get("mysql");
        if (mysql != null) {
            if (mysql != null) {
                MysqlConfig.url = (String) mysql.get("url");
                MysqlConfig.username = (String) mysql.get("username");
                MysqlConfig.password = (String) mysql.get("password");

            }
        }

        // gpt
        Map<String, Object> gpt = (Map<String, Object>) data.get("gpt");
        if (gpt != null) {
            GptHelper.API_URL = (String) gpt.get("url");
            GptHelper.API_KEY = (String) gpt.get("api-key");
        }

        // email
        Map<String, Object> email = (Map<String, Object>) data.get("email");
        if (email != null) {
            EmailUtil.host = (String) email.get("host");
            EmailUtil.port = email.get("port") + "";
            EmailUtil.username = (String) email.get("username");
            EmailUtil.password = (String) email.get("password");
            EmailUtil.to = (String) email.get("to");
        }
    }
}
