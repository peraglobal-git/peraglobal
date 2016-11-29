package com.peraglobal.km.crawler.db;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Test t = new Test();
		/*String htmlStr = "<html><head><script>alert(1);</script></head><body><img src=\"df\" /><div id=\"userbar\">abc</div><ul><li>1.a</li><li>2.b</li><li>3.c</li></ul></body></html>";
		System.out.println(t.getTextFromTHML(htmlStr));*/
		DBObject obj = new BasicDBObject();
		obj.put("key1", "value1");
		obj.put("key2", "value2");
		System.out.println(obj.toString());
	}

	public static String getTextFromTHML(String htmlStr) {
		  Document doc = Jsoup.parse(htmlStr);
		  String text = doc.text();
		  // remove extra white space
		  StringBuilder builder = new StringBuilder(text);
		  int index = 0;
		  while(builder.length()>index){
		    char tmp = builder.charAt(index);
		    if(Character.isSpaceChar(tmp) || Character.isWhitespace(tmp)){
		      builder.setCharAt(index, ' ');
		    }
		    index++;
		  }
		  text = builder.toString().replaceAll(" +", " ").trim();
		  return text;
		}
}
