package space.mufeng.reader;

import com.alibaba.fastjson.JSONObject;
import space.mufeng.YamlConfigLoader;
import space.mufeng.dao.ArticleDAO;
import space.mufeng.entity.Article;
import space.mufeng.entity.RssResponse;
import space.mufeng.utils.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mufeng on 2017/7/25.
 * fever api 获取 rss 内容
 */
public class FeverReader {


    public static List<Article> getArticles(int sinceId) {
        List<Article> articles = new ArrayList<>();
        try {
            while (true) {
                RssResponse javaObject = getRssResponse(sinceId);
                System.out.println(javaObject);
                List<Article> items = javaObject.getItems();
                if (items == null || items.size() == 0) {
                    return articles;
                } else {
                    articles.addAll(items);
                    if (items.get(0).getId() < sinceId) {
                        return articles;
                    }
                    sinceId = sinceId + 50;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static RssResponse getRssResponse(int sinceId) throws IOException {
        Map<String, String> params = null;
        params = new HashMap<>();
        params.put("api", "");
        params.put("api_key", HttpClientUtil.API_KEY);
        params.put("items", "");
        params.put("since_id", sinceId + "");
        params.put("max_id", sinceId + 50 + "");
        String result = HttpClientUtil.sendPostRequest(HttpClientUtil.BASE_URL, "", params);
        RssResponse javaObject = JSONObject.toJavaObject(JSONObject.parseObject(result), RssResponse.class);
        return javaObject;
    }
}
