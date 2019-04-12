package com.lin.crawler.crawler.wechat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 *
 * 生成接口代理并执行真正的js代码.
 *
 * @author linjinzhi
 * @date 2019/4/1
 */
public class WeixinRequestParserLoader {

    private static final Logger logger = LoggerFactory.getLogger(WeixinRequestParserLoader.class);

    private static ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("js");

    private WeixinRequestParserLoader() {

    }

    public static WeixinRequestParser loadJS(String scriptStr) {

        if(StringUtils.isBlank(scriptStr)) {
            logger.error("script string is empty");
            return null;
        }

        WeixinRequestParser weixinRequestParser = null;
        try {
            InputStream inputStream = new ByteArrayInputStream(scriptStr.getBytes(StandardCharsets.UTF_8));
            scriptEngine.eval(new BufferedReader(new InputStreamReader(inputStream)));
            if(scriptEngine instanceof Invocable) {
                Invocable invocable = (Invocable) scriptEngine;
                weixinRequestParser = invocable.getInterface(WeixinRequestParser.class);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return weixinRequestParser;
    }
}
