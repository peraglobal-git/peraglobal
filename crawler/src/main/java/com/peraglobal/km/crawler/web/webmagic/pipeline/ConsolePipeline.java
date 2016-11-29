package com.peraglobal.km.crawler.web.webmagic.pipeline;

import java.util.Map;

import com.peraglobal.km.crawler.web.webmagic.ResultItems;
import com.peraglobal.km.crawler.web.webmagic.Task;

/**
 * Write results in console.<br>
 * Usually used in test.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
