package com.lin.crawler.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class CrawlArticleController {

    private static final Logger logger = LoggerFactory.getLogger(CrawlArticleController.class);

    @RequestMapping(value = "test", method = RequestMethod.GET)
    public ResponseEntity<String> test() {
        for(int i = 0; i < 10; i++) {
            try {
                Integer.parseInt("");
                Thread.sleep(10L);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        return new ResponseEntity<>("test finish", HttpStatus.OK);
    }
}
