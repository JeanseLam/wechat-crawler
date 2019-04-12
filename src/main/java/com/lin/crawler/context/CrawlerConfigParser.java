package com.lin.crawler.context;

import java.util.HashSet;
import java.util.Set;

public class CrawlerConfigParser {

    public Set<CrawlerConfig> parse() {

        String[] wechatTopoic = new String[]{
                 "采蜜app",
                "正解局",
                "姜超宏观债券研究",
                "中国基金报",
                "徐瑾经济人",
                "牛弹琴",
                "进击波财经",
                "界面楼谈",
                "iFeng科技",
                "洞见资本",
                "半导体行业观察",
                "读娱",
                "财经十一人",
                "新经济100人",
                "姜汁满头",
                "鹿鸣财经",
                "王雅媛港股圈",
                "采蜜云享派",
                "财经连环话",
                "DT财经",
                "饭统戴老板",
                "韭菜说投资社区",
                "寻瑕记",
                "交易门",
                "功夫财经",
                "金融观察团",
                "蓝鲸新财富",
                "读财社",
                "资管网",
                "证券时报网",
                "虎嗅APP",
                "简七读财",
                "金融八卦女频道",
                "三公子的人生记录仪",
                "石榴询财",
                "力哥理财",
                // 海外财富，保险
                "baoxian5203344",
                "baoxian720",//*
                "保险一键说",
                "深蓝保",
                "慧保天下",
                "保险在线",
                "保险一哥",//*
                "保险新闻网",
                "A智慧保",
                "保险岛",
                "保销之声",
                "人民精算师",
                "母基金研究中心",
                "资管云",
                "投中网",
                "海外置业政策",
                "中华元智库",
                "泰国置业攻略",
                "优投房UTOFUN",
                "Zillow",//*
                "外居乐",
                "Flyhomes",
                "港险宝宝PRO",
                "邦海外",
                "海外请报社",
                "米宅海外",
                "海外眼",
                "金斧子财富",
                "中融财富"
        };

        Set<CrawlerConfig> configs = new HashSet<>();

        for(String topic : wechatTopoic) {
            CrawlerConfig crawlerConfig = new CrawlerConfig();
            crawlerConfig.sourceType = 0;
            crawlerConfig.source = "wechat";
            crawlerConfig.topic = topic;
            crawlerConfig.processClass = "com.lin.crawler.crawler.wechat.WechatProcessor";
            configs.add(crawlerConfig);
        }

        return configs;
    }
}
