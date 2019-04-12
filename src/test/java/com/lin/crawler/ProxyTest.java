package com.lin.crawler;

import com.lin.crawler.common.ScyllaProxyCenter;
import com.lin.crawler.common.entity.SimpleProxyIp;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProxyTest {

    @Resource
    private ScyllaProxyCenter scyllaProxyCenter;

    @Test
    public void test() {

        List<SimpleProxyIp> ips = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            SimpleProxyIp simpleProxyIp = scyllaProxyCenter.applyProxyIp();
            ips.add(simpleProxyIp);
        }

        for(SimpleProxyIp simpleProxyIp : ips) {
            try {
                HttpRequestData httpRequestData = new HttpRequestData(simpleProxyIp.getIp(), simpleProxyIp.getPort(), null, false);
                String html = RequestUtils.get(httpRequestData, "https://www.baidu.com");
                if(!html.contains("百度一下")) {
                    System.out.println("proxy ip is not used:" + simpleProxyIp);
                } else {
                    System.out.println("request with proxy ip success:" + simpleProxyIp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("proxy ip is not used:" + simpleProxyIp);
            } finally {
                scyllaProxyCenter.releaseProxyIp(simpleProxyIp);
            }
        }
    }


    @Test
    public void test1() throws Exception {

        HttpRequestData httpRequestData = new HttpRequestData("10.250.100.42", 8081, null, false);
        String html = RequestUtils.get(httpRequestData, "http://www.baidu.com");
        System.out.println(html);
    }
}
