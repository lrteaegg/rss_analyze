package space.mufeng;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Synchronized;
import space.mufeng.dao.ArticleDAO;
import space.mufeng.entity.Article;
import space.mufeng.entity.ChatCompletion;
import space.mufeng.enums.ContentSource;
import space.mufeng.reader.FeverReader;
import space.mufeng.reader.GptHelper;
import space.mufeng.utils.DateUtil;
import space.mufeng.utils.EmailUtil;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    public static Map<Integer, Long> SYNC_MAP = new HashMap<>();

    public static void main(String[] args) {
        YamlConfigLoader.init();
//        getNewMessage();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(YamlConfigLoader.NEED_TYPE_LIST.size() + 1);
        // 一天更行一次
        scheduler.scheduleAtFixedRate(Main::getNewMessage, 0, 1, TimeUnit.DAYS);
        // 需要发送的类型
        for (Integer type : YamlConfigLoader.NEED_TYPE_LIST) {
            // 发送消息邮箱
            scheduler.scheduleAtFixedRate(()->{
                Main.sendNeedReadMessages(type);
            }, 0, 1, TimeUnit.DAYS);
        }
        System.out.println("Hello world!");
    }

    @Synchronized
    private static void sendNeedReadMessages(int type) {
        try {


            List<Article> needReadMessages = getNeedReadMessages(type);
            // 2024/7/26 发送邮件
            String emailHtmlContent = Article.emailHtmlContent(needReadMessages);

            EmailUtil.sendEmail(EmailUtil.to, DateUtil.formatDate(System.currentTimeMillis() / 1000) + "-" + ContentSource.getById(type).getTitle() + "-今日消息", emailHtmlContent);

            SYNC_MAP.put(type, System.currentTimeMillis() / 1000);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getNewMessage() {
        try {
            int lastId = ArticleDAO.getInstance().getLastId();
            List<Article> articles = FeverReader.getArticles(lastId);
            ArticleDAO.getInstance().batchSaveOrUpdate(articles);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("定时执行报错");
        }
    }

    private static List<Article> getNeedReadMessages(int type) {
        ArticleDAO articleDAO = ArticleDAO.getInstance();
        List<Article> todayArticles = new ArrayList<>();
//        for (Integer type : YamlConfigLoader.NEED_TYPE_LIST) {
//        }
        Long sendStartTime = SYNC_MAP.getOrDefault(type, DateUtil.getTodayStartTime());
        List<Article> articles = articleDAO.getTodayArticles(sendStartTime, type);
        if (articles != null && articles.size() > 0){
            todayArticles.addAll(articles);
        }
        if (todayArticles != null && todayArticles.size() > 0) {
            List<String> content = todayArticles.stream().map(article -> article.getId() + ":" + article.getTitle()).collect(Collectors.toList());
            String response = null;
            try {
                response = GptHelper.sendGptRequest(GptHelper.PROMPT, JSONArray.toJSONString(content));
                ChatCompletion javaObject = JSONObject.toJavaObject(JSONObject.parseObject(response), ChatCompletion.class);
                System.out.println(javaObject.getChoices().get(0).getMessage().getContent());
                // 数组转 list
                List<String> idStrList = javaObject.getChoices().get(0).getMessage().getContent();
                // 转 int 类型
                String idStrs = idStrList.get(0);
                // [25172, 25178, 25179, 25180, 25181, 25182, 2] 转 List
                List<Integer> ids = JSONArray.parseArray(idStrs, Integer.class);

                List<Article> byIds = articleDAO.getByIds(ids);
                return byIds;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new ArrayList<>();
    }
}