package space.mufeng.reader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import space.mufeng.utils.HttpClientUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GptHelper {

    public static String API_KEY = "";

    public static String API_URL = "";

    public static final String PROMPT = "你现在是一个信息筛选角色。根据标题内容选择出有关于教程和推荐类型的标题，我会给出 id 和标题，只需要返回 id 即可，无需返回多余内容，例如：[25172,21473...]。";

    /**
     * {
     *   "model": "gpt-3.5-turbo",
     *   "messages": [
     *     {
     *       "role": "system",
     *       "content": "你现在是一个信息筛选角色。根据标题内容选择出有关于教程和推荐类型的标题，我会给出 id 和标题，只需要返回 id 即可，无需返回多余内容。"
     *     },
     *     {
     *       "role": "user",
     *       "content": ""
     *     }
     *   ]
     * }
     * @return
     */
    public static String getPayload(String prompt, String content) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("model", "gpt-3.5-turbo");
        JSONArray message = new JSONArray();
        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", prompt);
        message.add(systemMessage);
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", content);
        message.add(userMessage);
        jsonObject.put("messages", message);
        return jsonObject.toJSONString();
    }

    public static String sendGptRequest(String prompt , String content) throws IOException {
        String payload = getPayload(prompt, content);
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Type", "application/json");
        headerMap.put("Authorization", "Bearer " + API_KEY);
        String response = HttpClientUtil.sendPostRequest(API_URL, headerMap, payload, null);
        return response;
    }


}
