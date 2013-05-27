package net.sf.sahi.util;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import net.sf.sahi.config.Configuration;
import net.sf.sahi.request.HttpRequest;
import net.sf.sahi.response.HttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Sahi - Web Automation and Test Tool
 * 
 * Copyright  2006  V Narayan Raman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


public class MongoLogger {
	static String trafficDir;

	static {
		String logsRoot = Configuration.getLogsRoot();
		trafficDir = logsRoot + "/traffic";
	}

	private String reqFileName;
	private File threadDir;
	private final String type;
	private final boolean log;
	private final Date time;

	public MongoLogger(String reqFileName, String type, boolean log, final Date time) {
		this.type = type;
		this.log = log;
		this.time = time;
		if (log) init(reqFileName);
	}


	private void init(String reqFileName) {
		this.reqFileName = FileUtils.cleanFileName(reqFileName);
		this.threadDir = getThreadDir();
	}


	protected synchronized String createThreadId() {
		return new SimpleDateFormat("HH_mm_ss_SSS").format(time);
	}

	public String storeRequestHeader(byte[] bytes) {
		return store(bytes, "request.header_" + type + ".txt");

	}

	public String storeRequestBody(byte[] bytes) {
		return store(bytes, "request.body_" + type + ".txt");
	}

	public String storeResponseHeader(byte[] bytes) {
		return store(bytes, "response.header_" + type + ".txt");
	}

	public String storeResponseBody(byte[] bytes) {
		//return store(bytes, "response.body"  + "_" + type + (reqFileName==null?"":"_"+reqFileName));
        return store(bytes, "response.body"  + "_" + type + (reqFileName==null?"":"_"+reqFileName));
	}

	public String store(byte[] bytes, String fileName) {
		//if (!log) return "";
		if (bytes == null) return "";
		File file = new File(threadDir, fileName);
		FileOutputStream out = null;
		try {
			if (!file.exists()) file.createNewFile();
			out = new FileOutputStream(file, true);
			out.write(bytes);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        return threadDir + "/" +  fileName;
	}

	public File getThreadDir() {
		String threadId = createThreadId();
		File threadDir = new File(trafficDir + "/" + (new SimpleDateFormat("yyyy_MM_dd").format(time)) + "/" + threadId + (reqFileName==null?"":"_"+reqFileName));
		threadDir.mkdirs();
		return threadDir;
	}

	public static MongoLogger getLoggerForThread(String type){
        MongoLogger logger = (MongoLogger) ThreadLocalMap.get("mongoLogger_" + type);
        if(logger != null) {
             return logger;
         } else {
            final String fileName = "cache";
            Date time = new Date();
           MongoLogger.createLoggerForThread(fileName, type, true, time);
           return (MongoLogger) ThreadLocalMap.get("mongoLogger_" + type);
        }
	}

	public static void createLoggerForThread(String fileName, String type, boolean log, Date time) {
		MongoLogger logger = new MongoLogger(fileName, type, log, time);
		ThreadLocalMap.put("mongoLogger_" + type, logger);
	}
	
	public static String storeRequestHeader(byte[] bytes, String type) {
		final MongoLogger loggerForThread = getLoggerForThread(type);
		return loggerForThread.storeRequestHeader(bytes);
	}
	
	public static String storeRequestBody(byte[] bytes, String type) {
		final MongoLogger loggerForThread = getLoggerForThread(type);
		return loggerForThread.storeRequestBody(bytes);
	}	
	
	public static String storeResponseHeader(byte[] bytes, String type) {
		final MongoLogger loggerForThread = getLoggerForThread(type);
		return   loggerForThread.storeResponseHeader(bytes);
	}	
	
	public static String storeResponseBody(byte[] bytes, String type) {
		final MongoLogger loggerForThread = getLoggerForThread(type);
		 return  loggerForThread.storeResponseBody(bytes);
	}

    public static void storeImageUrl(HttpRequest request, HttpResponse response) {
            if(response.contentTypeHeader().equals("image/jpeg")) {
                String uri = request.uri();
                String url = request.url();
                String file = MongoLogger.storeResponseBody(response.data(), "unmodified");
                MongoClient mongoClient = null;
                try {
                    mongoClient = new MongoClient();
                    DB db = mongoClient.getDB("test");
                    DBCollection coll = db.getCollection("imgurls");
                    BasicDBObject doc = new BasicDBObject()
                            .append("url", url)
                            .append("filepath", file);
                    //coll.insert(doc);
                    BasicDBObject q = new BasicDBObject()
                            .append("url", url);

                    coll.update(q, doc, true, false);
                    mongoClient.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    /* abc abc */
                }
        }
    }

}