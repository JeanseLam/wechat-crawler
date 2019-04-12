package com.lin.crawler.common;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @author yangchong
 *
 */
public class TagMatch {

    private static Map tagWordMap = null;

    public static int minMatchTYpe = 1;      //最小匹配规则

    public static int maxMatchType = 2;      //最大匹配规则

    public static Map<String, String> initTag(Set<String> tagList) {
        //将标签加入到HashMap中
    	addTagToHashMap(tagList);
        return tagWordMap;
    }
    
    public static String getTagWord(String txt) {
    	Set<String> tagWordList = getTagWord(txt, maxMatchType);
    	String tag = null;
    	if(!CollectionUtils.isEmpty(tagWordList)) {
    		for(String t : tagWordList) {
    			//只取第一个
    			if(!StringUtils.isEmpty(t)) {
    				tag = t;
    				break;
    			}
    		}
    	}
    	return tag;
    }

    /**
     * 获取文字中的标签
     */
    public static Set<String> getTagWord(String txt, int matchType) {
        Set<String> tagWordList = new HashSet<String>();

        for (int i = 0; i < txt.length(); i++) {
            int length = checkTag(txt, i, matchType);    //判断是否包含敏感字符
            if (length > 0) {    //存在,加入list中
            	tagWordList.add(txt.substring(i, i + length));
                i = i + length - 1;    //减1的原因，是因为for会自增
            }
        }

        return tagWordList;
    }

    public static int checkTag(String txt, int beginIndex, int matchType) {
        boolean flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况
        int matchFlag = 0;     //匹配标识数默认为0
        char word = 0;
        Map nowMap = tagWordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);     //获取指定key
            if (nowMap != null) {     //存在，则判断是否为最后一个
                matchFlag++;     //找到相应key，匹配标识+1
                if ("1".equals(nowMap.get("isEnd"))) {       //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;       //结束标志位为true
                    if (TagMatch.minMatchTYpe == matchType) {    //最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            } else {     //不存在，直接返回
                break;
            }
        }
        if (matchFlag < 2 || !flag) {        //长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }

    private static void addTagToHashMap(Set<String> tagList) {
    	tagWordMap = new HashMap<String, String>(tagList.size());     //初始化敏感词容器，减少扩容操作
		Map nowMap = null;
        Map newWorMap = null;
        //迭代keyWordSet
        for(String key : tagList) {
            nowMap = tagWordMap;
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);       //转换成char型
                Object wordMap = nowMap.get(keyChar);       //获取

                if (wordMap != null) {        //如果存在该key，直接赋值
                    nowMap = (Map) wordMap;
                } else {     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<String, String>();
                    newWorMap.put("isEnd", "0");     //不是最后一个
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }

                if (i == key.length() - 1) {
                    nowMap.put("isEnd", "1");    //最后一个
                }
            }
        }
    }
}
