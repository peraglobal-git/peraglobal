package com.peraglobal.km.crawler.webservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.peraglobal.km.mongo.biz.MgDatumBiz;
import com.peraglobal.pdp.core.utils.AppConfigUtils;

@WebService(endpointInterface="com.peraglobal.km.crawler.webservice.AttachmentCrawlerWS",serviceName="AttachmentCrawlerWS")
public class AttachmentCrawlerWSImpl implements AttachmentCrawlerWS{
	@Resource
	private MgDatumBiz mgDatumBiz;
	final static int BUFFER_SIZE = 4096;
	/**
	 * 从MongoDB中获取文件流
	 * @param fileId
	 * @return
	 */
	public byte[] getAttachmentContent(String fileId) {
//		fileId="574512bb1c95b31e600c4b36";
		byte[] bytes = null;
		try {
			GridFSDBFile dbFile = mgDatumBiz.findFileById(fileId);
			bytes = this.InputStreamTOByte(dbFile.getInputStream());
			/*File file = new File("C:/Users/xiaodan.jing/Desktop/a.pdf");
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(bytes);
			fos.close();*/
		} catch (Exception e) {
			e.printStackTrace();
		} finally{

		}
	    return bytes;
	}
	/**
     * 将InputStream转换成byte数组
     * @param in InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in){
         
        ByteArrayOutputStream outStream = null;
		try {
			outStream = new ByteArrayOutputStream();
			byte[] data = new byte[BUFFER_SIZE];
			int count = -1;
			while((count = in.read(data,0,BUFFER_SIZE)) != -1)
			    outStream.write(data, 0, count);
			data = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return outStream.toByteArray();
    }
}
