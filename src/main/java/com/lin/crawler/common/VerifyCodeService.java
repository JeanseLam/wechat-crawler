package com.lin.crawler.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;

/**
 * Created by linjinzhi on 2018-12-13.
 * <p>
 * 封装云打码服务.
 */
@Component
@ConfigurationProperties(prefix = "vc")
public class VerifyCodeService {

    private static final Logger logger = LoggerFactory.getLogger(VerifyCodeService.class);

    private String imageTempPath;

    private String ydmEndpoint;

    public static void main(String[] args) throws Exception {
        VerifyCodeService verifyCodeService = new VerifyCodeService();
        verifyCodeService.setYdmEndpoint("http://api.yundama.com/api.php");
        verifyCodeService.setImageTempPath("C:\\Users\\admin\\Desktop\\");
        byte[] vc = IOUtils.toByteArray(new FileInputStream(new File("C:\\Users\\admin\\Desktop\\verifycode")));
        String text = verifyCodeService.vc(vc, 1004, new HttpRequestData());
        System.out.println(text);
    }

    public String vc(byte[] vc, int codeType, HttpRequestData httpRequestData) {

        CloseableHttpClient client = httpRequestData.getCloseableHttpClient();

        File file = new File(imageTempPath + "\\vc_" + Thread.currentThread().getId() + ".jpeg");
        try {
            FileUtils.writeByteArrayToFile(file, vc);

            HttpPost post = new HttpPost(ydmEndpoint);
            FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
            StringBody stringBody1 = new StringBody("****", ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody2 = new StringBody("****", ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody3 = new StringBody(codeType + "", ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody4 = new StringBody("1", ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody5 = new StringBody("***********", ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody6 = new StringBody("60", ContentType.MULTIPART_FORM_DATA);
            StringBody stringBody7 = new StringBody("upload", ContentType.MULTIPART_FORM_DATA);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("username", stringBody1);
            builder.addPart("password", stringBody2);
            builder.addPart("codetype", stringBody3);
            builder.addPart("appid", stringBody4);
            builder.addPart("appkey", stringBody5);
            builder.addPart("timeout", stringBody6);
            builder.addPart("method", stringBody7);
            builder.addPart("file", fileBody);
            HttpEntity entity = builder.build();

            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");

            logger.info("云打码返回:{}", responseContent);

            // 需要轮询获取结果接口
            JSONObject jsonObject = JSON.parseObject(responseContent);
            if(jsonObject.containsKey("cid") && StringUtils.isBlank(jsonObject.getString("text"))) {
                long cid = jsonObject.getLong("cid");
                return getResult(Long.toString(cid), httpRequestData);
            }

            EntityUtils.consumeQuietly(response.getEntity());
            return JSON.parseObject(responseContent).getString("text");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            file.delete();
        }
        return "";
    }

    public String getImageTempPath() {
        return imageTempPath;
    }

    public void setImageTempPath(String imageTempPath) {
        this.imageTempPath = imageTempPath;
    }

    public String getYdmEndpoint() {
        return ydmEndpoint;
    }

    public void setYdmEndpoint(String ydmEndpoint) {
        this.ydmEndpoint = ydmEndpoint;
    }

    /**
     * 用于获取验证码结果.
     * @param cid cid
     * @return 打码结果
     */
    public String getResult(String cid, HttpRequestData httpRequestData) {
        if(StringUtils.isBlank(cid)) {
            return "";
        }

        try {

            long timeStart = System.currentTimeMillis();

            while (true) {
                String url = "http://api.yundama.com/api.php?cid={0}&method=result";
                String result = RequestUtils.get(httpRequestData, MessageFormat.format(url, cid));
                JSONObject jsonObject = JSON.parseObject(result);
                if(jsonObject.containsKey("text") && jsonObject.getInteger("ret") == 0) {
                    return jsonObject.getString("text");
                }

                Thread.sleep(100L);
                long timeEnd = System.currentTimeMillis();
                if(timeEnd - timeStart > 60 * 1000) {
                    logger.error("读取云打码返回结果超时");
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return "";
    }
}
