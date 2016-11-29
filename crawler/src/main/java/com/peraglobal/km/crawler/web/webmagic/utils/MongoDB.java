package com.peraglobal.km.crawler.web.webmagic.utils;

import java.io.File;
import java.util.Date;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;

public class MongoDB {

	public static void main(String[] args) {
		// initData();
		// query();
		initData4GridFS();
	}

	private static void initData4GridFS() {
		long start = new Date().getTime();
		try {
			Mongo db = new Mongo("127.0.0.1", 27017);
			// or
			// MongoClient db = new MongoClient( "192.168.31.191" , 27017 );
			DB mydb = db.getDB("test");
			// MongoDatabase mydb = mongoClient.getDatabase("test");
			File f = new File("C:/MyDocuments/工作文档/mongo.conf");
			GridFS myFS = new GridFS(mydb);
			GridFSInputFile inputFile = myFS.createFile(f);
			inputFile.save();

			DBCursor cursor = myFS.getFileList();
			while (cursor.hasNext()) {
				System.out.println(cursor.next());
			}
			db.close();
			long endTime = new Date().getTime();
			System.out.println(endTime - start);
			System.out.println((endTime - start) / 10000000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
