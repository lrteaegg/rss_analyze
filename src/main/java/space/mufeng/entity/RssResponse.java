package space.mufeng.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.List;

@Data
public class RssResponse {
    //{
    //    "api_version": 3,
    //    "auth": 1,
    //    "total_items": 21521,
    //    "items": [
    //    ],
    //    "last_refreshed_on_time": "1721826325"
    //}

    @JSONField(name = "api_version")
    private int apiVersion;

    @JSONField(name = "auth")
    private int auth;

    @JSONField(name = "total_items")
    private int totalItems;

    @JSONField(name = "last_refreshed_on_time")
    private int lastRefreshedOnTime;

    @JSONField(name = "items")
    private List<Article> items;

}
