package space.mufeng.enums;

import lombok.Getter;

@Getter
public enum ContentSource {
    JUEJIN_BACKEND(58, "掘金后端本月最热"),
    CTO51(56, "51CTO"),
    TECH_HEADLINES(57, "技术头条"),
    JUEJIN_JAVA(59, "掘金 Java"),
    RUANYIFENG(3, "阮一峰的网络日志"),
    V2EX_HOT(8, "V2EX 最热主题"),
    FORTUNE_CHINA(9, "商业 - 财富中文网 - FORTUNEChina.com"),
    ZHIHU_DAILY(2, "知乎每日精选"),

    ;
    private final int id;
    private final String title;

    ContentSource(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public static ContentSource getById(int id) {
        for (ContentSource source : values()) {
            if (source.id == id) {
                return source;
            }
        }
        return null;
    }
}
