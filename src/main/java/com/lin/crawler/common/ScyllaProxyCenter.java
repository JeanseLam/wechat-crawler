package com.lin.crawler.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lin.crawler.common.entity.SimpleProxyIp;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by linjinzhi on 2018/12/25.
 *
 * scylla代理池服务.
 *
 */
@Component
@ConfigurationProperties(prefix = "scylla")
public class ScyllaProxyCenter {

    private static final Logger logger = LoggerFactory.getLogger(ScyllaProxyCenter.class);

    private String address;

    private Set<SimpleProxyIp> activePool = new HashSet<>();

    private String on;

    public synchronized SimpleProxyIp applyProxyIp() {
        try {
            HttpRequestData httpRequestData = new HttpRequestData();
            String json = RequestUtils.get(httpRequestData, address);
            JSONObject jsonObject = JSON.parseObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("proxies");
            for(int i = 0; i < jsonArray.size(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                String ip = jsonObject.getString("ip");
                int port = jsonObject.getInteger("port");
                boolean isValid = jsonObject.getBoolean("is_valid");
                boolean isHttps = jsonObject.getBoolean("is_https");

                SimpleProxyIp simpleProxyIp = new SimpleProxyIp();
                simpleProxyIp.setIp(ip);
                simpleProxyIp.setPort(port);
                simpleProxyIp.setValid(isValid);
                simpleProxyIp.setHttps(isHttps);
                simpleProxyIp.setBusy(false);

                if(!activePool.contains(simpleProxyIp)) {
                    activePool.add(simpleProxyIp);
                    return simpleProxyIp;
                }
            }

        } catch (Exception e) {
            logger.error("init scylla proxies occurs an error, message:{}, exception:{}", e.getMessage(), e);
        }
        return null;
    }


    public synchronized void releaseProxyIp(SimpleProxyIp simpleProxyIp) {
        if(simpleProxyIp != null) {
            activePool.remove(simpleProxyIp);
        }
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOn() {
        return on;
    }

    public void setOn(String on) {
        this.on = on;
    }
}
