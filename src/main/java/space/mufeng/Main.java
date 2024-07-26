package space.mufeng;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import space.mufeng.dao.ArticleDAO;
import space.mufeng.entity.Article;
import space.mufeng.entity.ChatCompletion;
import space.mufeng.reader.FeverReader;
import space.mufeng.reader.GptHelper;
import space.mufeng.utils.DateUtil;
import space.mufeng.utils.EmailUtil;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        YamlConfigLoader.init();
//        getNewMessage();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        // 一天更行一次
        scheduler.scheduleAtFixedRate(Main::getNewMessage, 0, 1, TimeUnit.DAYS);
        System.out.println("Hello world!");
    }

    private static void getNewMessage() {
        try {
            int lastId = ArticleDAO.getInstance().getLastId();
            List<Article> articles = FeverReader.getArticles(lastId);
            ArticleDAO.getInstance().batchSaveOrUpdate(articles);
            List<Article> needReadMessages = getNeedReadMessages();
            // 2024/7/26 发送邮件
            String emailHtmlContent = Article.emailHtmlContent(needReadMessages);

            EmailUtil.sendEmail(EmailUtil.to, DateUtil.formatDate(System.currentTimeMillis() / 1000) + "-今日消息", emailHtmlContent);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("定时执行报错");
        }
    }

    private static List<Article> getNeedReadMessages() {
        ArticleDAO articleDAO = ArticleDAO.getInstance();
        List<Article> todayArticles = new ArrayList<>();
        for (Integer type : YamlConfigLoader.NEED_TYPE_LIST) {
            List<Article> articles = articleDAO.getTodayArticles(type);
            if (articles != null && articles.size() > 0){
                todayArticles.addAll(articles);
            }
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