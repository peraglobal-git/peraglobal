package com.peraglobal.km.crawler.web.webmagic.scheduler.component;

import com.peraglobal.km.crawler.web.webmagic.Request;
import com.peraglobal.km.crawler.web.webmagic.Task;

/**
 * Remove duplicate requests.
 * @author code4crafer@gmail.com
 * @since 0.5.1
 */
public interface DuplicateRemover {
    /**
     *
     * Check whether the request is duplicate.
     *
     * @param request
     * @param task
     * @return
     */
    public boolean isDuplicate(Request request, Task task);

    /**
     * Reset duplicate check.
     * @param task
     */
    public void resetDuplicateCheck(Task task);

    /**
     * Get TotalRequestsCount for monitor.
     * @param task
     * @return
     */
    public int getTotalRequestsCount(Task task);

}
