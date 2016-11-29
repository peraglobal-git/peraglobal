package com.peraglobal.km.crawler.web.webmagic.pipeline;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.annotation.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.peraglobal.km.crawler.web.webmagic.ResultItems;
import com.peraglobal.km.crawler.web.webmagic.Task;
import com.peraglobal.km.crawler.web.webmagic.utils.FilePersistentBase;

/**
 * Store results in files.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.1.0
 */
@ThreadSafe
public class W3cFilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * create a FilePipeline with default path"/data/webmagic/"
     */
    public W3cFilePipeline() {
        setPath("/data/webmagic/");
    }

    public W3cFilePipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        try {
        	String title = "list";
        	for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if(entry.getKey().equals("title")){
                	title = entry.getValue().toString();
                	title = title.replace(":", " ");
                	break;
                }
            }
        	PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(getFile(path +title + ".html")),"utf-8"));
//           printWriter.println("url:\t" + resultItems.getRequest().getUrl());
            for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
                if (entry.getValue() instanceof Iterable) {
                    Iterable value = (Iterable) entry.getValue();
                    printWriter.println(entry.getKey() + ":");
                    for (Object o : value) {
                        printWriter.println(o);
                    }
                } else {
//                  printWriter.println(entry.getKey() + ":\t" + entry.getValue());
                    if(entry.getKey().equals("title")){
                    	continue;
                    }
                	printWriter.println(entry.getValue());
                }
            }
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
