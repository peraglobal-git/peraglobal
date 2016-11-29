package com.peraglobal.km.crawler.web.webmagic.pipeline;

import com.peraglobal.km.crawler.web.webmagic.Task;

/**
 * Implements PageModelPipeline to persistent your page model.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
public interface PageModelPipeline<T> {

    public void process(T t, Task task);

}
