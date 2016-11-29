package com.peraglobal.km.crawler.web.webmagic.utils;

import java.util.ArrayList;
import java.util.List;

import com.peraglobal.km.crawler.web.webmagic.model.annotation.ExtractBy;
import com.peraglobal.km.crawler.web.webmagic.selector.CssSelector;
import com.peraglobal.km.crawler.web.webmagic.selector.JsonPathSelector;
import com.peraglobal.km.crawler.web.webmagic.selector.RegexSelector;
import com.peraglobal.km.crawler.web.webmagic.selector.Selector;
import com.peraglobal.km.crawler.web.webmagic.selector.XpathSelector;

/**
 * Tools for annotation converting. <br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class ExtractorUtils {

    public static Selector getSelector(ExtractBy extractBy) {
        String value = extractBy.value();
        Selector selector;
        switch (extractBy.type()) {
            case Css:
                selector = new CssSelector(value);
                break;
            case Regex:
                selector = new RegexSelector(value);
                break;
            case XPath:
                selector = getXpathSelector(value);
                break;
            case JsonPath:
                selector = new JsonPathSelector(value);
                break;
            default:
                selector = getXpathSelector(value);
        }
        return selector;
    }

    private static Selector getXpathSelector(String value) {
        Selector selector = new XpathSelector(value);
        return selector;
    }

    public static List<Selector> getSelectors(ExtractBy[] extractBies) {
        List<Selector> selectors = new ArrayList<Selector>();
        if (extractBies == null) {
            return selectors;
        }
        for (ExtractBy extractBy : extractBies) {
            selectors.add(getSelector(extractBy));
        }
        return selectors;
    }
}
