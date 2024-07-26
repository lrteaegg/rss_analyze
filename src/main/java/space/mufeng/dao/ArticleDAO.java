package space.mufeng.dao;


import space.mufeng.entity.Article;
import space.mufeng.utils.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ArticleDAO {

    //INSERT INTO article(id, feed_id, title, author, html, url, is_saved, is_read, created_on_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
    public static final String INSERT_SQL = "INSERT INTO article(id, feed_id, title, author, html, url, is_saved, is_read, created_on_time) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
    //UPDATE article SET feed_id = ?, title = ?, author = ?, html = ?, url = ?, is_saved = ?, is_read = ?, created_on_time = ? WHERE id = ?
    public static final String UPDATE_SQL = "UPDATE article SET feed_id = ?, title = ?, author = ?, html = ?, url = ?, is_saved = ?, is_read = ?, created_on_time = ? WHERE id = ?";
    //DELETE FROM article WHERE id = ?
    public static final String DELETE_SQL = "DELETE FROM article WHERE id = ?";
    //SELECT * FROM article WHERE id = ?
    public static final String SELECT_SQL = "SELECT * FROM article WHERE id = ?";
    //SELECT * FROM article WHERE is_saved = ? AND is_read = ?
    public static final String SELECT_SQL_BY_SAVED_AND_READ = "SELECT * FROM article WHERE is_saved = ? AND is_read = ?";
    //SELECT * FROM article WHERE created_on_time >= ? AND created_on_time <= ?
    public static final String SELECT_SQL_BY_TIME = "SELECT * FROM article WHERE created_on_time >= ? AND created_on_time <= ?";

    public static final String SELECT_SQL_BY_TIME_AND_TYPE = "SELECT * FROM article WHERE created_on_time >= ? AND created_on_time <= ? AND feed_id = ?";

    private static ArticleDAO instance = null;

    private ArticleDAO() {

    }

    public static ArticleDAO getInstance() {
        if (instance == null) {
            instance = new ArticleDAO();
        }
        return instance;
    }

    /**
     * 获取最新的 id
     */
    public int getLastId() {
        try {
            Statement statement = MysqlConfig.getStatement();
            ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM article");
            if (rs.next()) {
                int maxId = rs.getInt(1);
                return maxId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 保存文章
     *
     * @return
     */
    public int save(Article article) {
        try {
            Connection connection = MysqlConfig.getConnection();


            PreparedStatement statement = connection.prepareStatement(INSERT_SQL);
            statement.setInt(1, article.getId());
            statement.setInt(2, article.getFeedId());
            statement.setString(3, article.getTitle());
            statement.setString(4, article.getAuthor());
            statement.setString(5, article.getHtml());
            statement.setString(6, article.getUrl());
            statement.setInt(7, article.getIsSaved());
            statement.setInt(8, article.getIsRead());
            statement.setLong(9, article.getCreatedOnTime());

            int affectedRows = statement.executeUpdate();
            System.out.println("插入成功");
            return affectedRows;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 批量保存
     *
     * @param articles
     */
    public void batchSaveOrUpdate(List<Article> articles) {
        if (articles != null && articles.size() > 0) {
            for (Article article : articles) {
                this.saveOrUpdate(article);
            }
        }
    }

    public void update(Article article) {
        try {
            Connection connection = MysqlConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_SQL);
            statement.setInt(1, article.getFeedId());
            statement.setString(2, article.getTitle());
            statement.setString(3, article.getAuthor());
            statement.setString(4, article.getHtml());
            statement.setString(5, article.getUrl());
            statement.setInt(6, article.getIsSaved());
            statement.setInt(7, article.getIsRead());
            statement.setLong(8, article.getCreatedOnTime());
            statement.setInt(9, article.getId());
            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("更新成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据 id 获取文章
     *
     * @param id
     * @return
     */
    public Article get(int id) {
        try {
            Connection connection = MysqlConfig.getConnection();
            PreparedStatement statement = connection.prepareStatement(SELECT_SQL);
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setFeedId(rs.getInt("feed_id"));
                article.setTitle(rs.getString("title"));
                article.setAuthor(rs.getString("author"));
                article.setHtml(rs.getString("html"));
                article.setUrl(rs.getString("url"));
                article.setIsSaved(rs.getInt("is_saved"));
                article.setIsRead(rs.getInt("is_read"));
                article.setCreatedOnTime(rs.getLong("created_on_time"));
                return article;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据 ids 批量获取
     */
    public List<Article> getByIds(List<Integer> ids) {
        List<Article> articles = new ArrayList<>();
        if (ids != null && ids.size() > 0) {
            for (Integer id : ids) {
                Article article = this.get(id);
                articles.add(article);
            }
        }
        return articles;
    }

    /**
     * 新增或更新
     */
    public void saveOrUpdate(Article article) {
        Article oldArticle = this.get(article.getId());
        if (oldArticle != null) {
            this.update(article);
        } else {
            this.save(article);
        }
    }

    /**
     * 获取类型是 t 的今天的文章
     */
    public List<Article> getTodayArticles(int type) {
        try {
            Connection connection = MysqlConfig.getConnection();

            PreparedStatement statement = connection.prepareStatement(SELECT_SQL_BY_TIME_AND_TYPE);
            statement.setLong(1, DateUtil.getTodayStartTime());
            statement.setLong(2, DateUtil.getTodayEndTime());
            statement.setInt(3, type);
            ResultSet rs = statement.executeQuery();
            List<Article> articles = new ArrayList<>();
            while (rs.next()) {
                Article article = new Article();
                article.setId(rs.getInt("id"));
                article.setFeedId(rs.getInt("feed_id"));
                article.setTitle(rs.getString("title"));
                article.setAuthor(rs.getString("author"));
                article.setHtml(rs.getString("html"));
                article.setUrl(rs.getString("url"));
                article.setIsSaved(rs.getInt("is_saved"));
                article.setIsRead(rs.getInt("is_read"));
                article.setCreatedOnTime(rs.getLong("created_on_time"));
                articles.add(article);
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
