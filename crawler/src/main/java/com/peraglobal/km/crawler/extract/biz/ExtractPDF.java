package com.peraglobal.km.crawler.extract.biz;

import java.util.concurrent.Callable;
import com.peraglobal.pdp.util.ExtractInfoMain;

public class ExtractPDF implements Callable {
	private ExtractInfoMain extractInfoMain = new ExtractInfoMain();
	private String filePath;
	private String xmlPath;
	public ExtractPDF(String filePath, String xmlPath){
		this.filePath = filePath;
		this.xmlPath = xmlPath;
	}
	
	@Override
	public Object call() throws Exception {
		return extractInfoMain.extractInfo(this.filePath,this.xmlPath);
	}

}
