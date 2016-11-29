package com.peraglobal.km.crawler.webservice;

import javax.jws.WebService;

@WebService
public interface AttachmentCrawlerWS {
	/**
	 * 从MongoDB中获取文件流
	 * @param fileId
	 * @return
	 */
	public byte[] getAttachmentContent(String fileId);
}
