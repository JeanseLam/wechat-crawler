package com.lin.crawler.common.file;

import com.alibaba.fastjson.JSON;
import com.lin.crawler.common.httpclient.RequestUtils;
import com.lin.crawler.common.httpclient.HttpRequestData;
import org.apache.commons.codec.digest.DigestUtils;
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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Created by linjinzhi on 2018-12-11.
 *
 * 图片服务器封装.
 *
 */
@Component
@ConfigurationProperties(prefix = "file")
public class FileServerHelper {

    private static final Logger logger = LoggerFactory.getLogger(FileServerHelper.class);

    /**
     * 图片上传接口
     */
    private String fileUploadEndpoint = "https://file.***.com/upload/file";

    /**
     * 临时图片文件存储，需要区分开发和线上环境
     */
    private String imageTempPath;

    private FileServerHelper() {}

    public String upload(String remoteLinkOrLocalPath) throws IOException {
        if(StringUtils.isBlank(remoteLinkOrLocalPath) || (!remoteLinkOrLocalPath.startsWith("http") && !remoteLinkOrLocalPath.startsWith("https"))) {
            return "";
        }

        logger.info("processing image link:{}", remoteLinkOrLocalPath);

        HttpRequestData httpRequestData = new HttpRequestData();
        if(remoteLinkOrLocalPath.startsWith("http") || remoteLinkOrLocalPath.startsWith("https")) {
            Map<String, String> headers = new HashMap<>();
            headers.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            headers.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
            byte[] imageBytes = RequestUtils.getBytes(httpRequestData, remoteLinkOrLocalPath, null, headers);
            if(imageBytes != null && imageBytes.length != 0) {
                return upload(DigestUtils.md5Hex(remoteLinkOrLocalPath.getBytes()), imageBytes, httpRequestData.getCloseableHttpClient());
            }
        } else {
            byte[] imageBytes = IOUtils.toByteArray(new FileInputStream(new File(remoteLinkOrLocalPath)));
            if(imageBytes != null && imageBytes.length != 0) {
                return upload(DigestUtils.md5Hex(remoteLinkOrLocalPath.getBytes()), imageBytes, httpRequestData.getCloseableHttpClient());
            }
        }
        return "";
    }

    private String upload(String imageName, byte[] imageBytes, CloseableHttpClient client) {

        File file = new File(imageTempPath + imageName + Thread.currentThread().getId() + ".jpeg");
        try {
            FileUtils.writeByteArrayToFile(file, imageBytes);

            HttpPost post = new HttpPost(fileUploadEndpoint);
            FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
//        StringBody stringBody1 = new StringBody("Message 1", ContentType.MULTIPART_FORM_DATA);
//        StringBody stringBody2 = new StringBody("Message 2", ContentType.MULTIPART_FORM_DATA);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addPart("file", fileBody);
            HttpEntity entity = builder.build();

            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            String responseContent = EntityUtils.toString(response.getEntity(), "UTF-8");
            EntityUtils.consumeQuietly(response.getEntity());
            return JSON.parseObject(responseContent).getString("link");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            file.delete();
        }
        return "";
    }

    public String getFileUploadEndpoint() {
        return fileUploadEndpoint;
    }

    public void setFileUploadEndpoint(String fileUploadEndpoint) {
        this.fileUploadEndpoint = fileUploadEndpoint;
    }

    public String getImageTempPath() {
        return imageTempPath;
    }

    public void setImageTempPath(String imageTempPath) {
        this.imageTempPath = imageTempPath;
    }

    public static void main(String[] args) throws IOException {
        String path = new FileServerHelper().upload("https://mmbiz.qpic.cn/mmbiz_jpg/x6iaHWKibUzk1Yx4GhRhH9YbAOMicXFZ6nS4NibOTqoibgBF4cUOYicPMPGJqkeUJhMzdLCjLA7N0s3oauZYf6JN8tIw/?wx_fmt=jpeg");
        System.out.println(path);
    }
}
