package com.peraglobal.km.crawler.web.webmagic.handler;

import com.peraglobal.km.crawler.web.webmagic.ResultItems;
import com.peraglobal.km.crawler.web.webmagic.Task;

/**
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public interface SubPipeline extends RequestMatcher {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param page
     * @param task
     * @return whether continue to match
     */
    public MatchOther processResult(ResultItems resultItems, Task task);

}
