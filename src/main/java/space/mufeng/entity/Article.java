package space.mufeng.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import space.mufeng.enums.ContentSource;
import space.mufeng.utils.DateUtil;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article implements Serializable {
    private static final long serialVersionUID = 1L;

    @JSONField(name = "id")
    private int id;             // 唯一标识符

    @JSONField(name = "feed_id")
    private int feedId;        // 源标识符

    @JSONField(name = "title")
    private String title;       // 文章标题

    @JSONField(name = "author")
    private String author;      // 作者

    @JSONField(name = "html")
    private String html;        // 文章内容

    @JSONField(name = "url")
    private String url;         // 文章链接

    @JSONField(name = "is_saved")
    private int isSaved;    // 是否已保存

    @JSONField(name = "is_read")
    private int isRead;     // 是否已阅读

    @JSONField(name = "created_on_time")
    private long createdOnTime; // 创建时间（Unix 时间戳）

    public static String emailHtmlContent(List<Article> articleList) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                    <html>
                        <head>
                            <title>今日文章</title>
                            <style>
                                table {
                                    width: 100%;
                                    border-collapse: collapse;
                                }
                                th, td {
                                    border: 1px solid #ddd;
                                    padding: 8px;
                                    text-align: left;
                                }
                                th {
                                    background-color: #f2f2f2;
                                }
                                a {
                                    text-decoration: none;
                                    color: #007BFF;
                                }
                            </style>
                        </head>
                        <body>
                            <table>
                                <tr>
                                    <th>ID</th>
                                    <th>标题</th>
                                    <th>类型</th>
                                    <th>时间</th>
                                </tr>
                """);

        for (Article article : articleList) {
            sb.append("""
                        <tr>
                            <td>%d</td>
                            <td><a href="%s">%s</a></td>
                            <td>%s</td>
                            <td>%s</td>
                        </tr>
                    """.formatted(
                    article.getId(),
                    article.getUrl(),
                    article.getTitle(),
                    ContentSource.getById(article.getFeedId()).getTitle(),
                    DateUtil.format(article.getCreatedOnTime())
            ));
        }

        sb.append("""
                            </table>
                        </body>
                    </html>
                """);

        return sb.toString();
    }


}
