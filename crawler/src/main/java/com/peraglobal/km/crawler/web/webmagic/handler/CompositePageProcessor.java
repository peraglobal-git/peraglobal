package com.peraglobal.km.crawler.web.webmagic.handler;

import java.util.ArrayList;
import java.util.List;

import com.peraglobal.km.crawler.web.webmagic.Page;
import com.peraglobal.km.crawler.web.webmagic.Site;
import com.peraglobal.km.crawler.web.webmagic.processor.PageProcessor;

/**
 * @author code4crafter@gmail.com
 * @date 14-4-5
 */
public class CompositePageProcessor implements PageProcessor {

    private Site site;

    private List<SubPageProcessor> subPageProcessors = new ArrayList<SubPageProcessor>();

    public CompositePageProcessor(Site site) {
        this.site = site;
    }

    @Override
    public void process(Page page) {
        for (SubPageProcessor subPageProcessor : subPageProcessors) {
            if (subPageProcessor.match(page.getRequest())) {
                SubPageProcessor.MatchOther matchOtherProcessorProcessor = subPageProcessor.processPage(page);
                if (matchOtherProcessorProcessor == null || matchOtherProcessorProcessor != SubPageProcessor.MatchOther.YES) {
                    return;
                }
            }
        }
    }

    public CompositePageProcessor setSite(Site site) {
        this.site = site;
        return this;
    }

    public CompositePageProcessor addSubPageProcessor(SubPageProcessor subPageProcessor) {
        this.subPageProcessors.add(subPageProcessor);
        return this;
    }

    public CompositePageProcessor setSubPageProcessors(SubPageProcessor... subPageProcessors) {
        this.subPageProcessors = new ArrayList<SubPageProcessor>();
        for (SubPageProcessor subPageProcessor : subPageProcessors) {
            this.subPageProcessors.add(subPageProcessor);
        }
        return this;
    }

    @Override
    public Site getSite() {
        return site;
    }
}