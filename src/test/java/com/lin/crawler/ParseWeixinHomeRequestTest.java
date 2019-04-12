package com.lin.crawler;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author linjinzhi
 * @date 2019/4/1
 */
public class ParseWeixinHomeRequestTest {

    @Test
    public void testParse() throws Exception {
        StringBuilder stringBuilder = new StringBuilder();
        List lines = IOUtils.readLines(new FileInputStream("C:\\Users\\admin\\Desktop\\test.html"));
        for(Object line : lines) {
            String str = (String) line;
            if(StringUtils.isNotBlank(str) && str.contains("'")) {
                str = str.substring(str.indexOf('\'') + 1, str.lastIndexOf('\''));
                stringBuilder.append(str);
            }
        }
        String url = stringBuilder.toString();
        System.out.println(url);
    }
}
