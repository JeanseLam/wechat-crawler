package com.lin.crawler.crawler.wechat;

/**
 *
 * js执行接口, 用于调用js脚本解析微信公众号请求.
 *
 * @author linjinzhi
 * @date 2019/4/1
 */
public interface WeixinRequestParser {

    /**
     * 从脚本中解析微信请求链接.
     * @return 微信公众号首页请求链接
     */
    String parseWeixinRequest();
}
