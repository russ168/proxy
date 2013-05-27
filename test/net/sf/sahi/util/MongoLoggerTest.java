package net.sf.sahi.util;

import junit.framework.TestCase;
import net.sf.sahi.config.Configuration;
import net.sf.sahi.request.HttpRequest;
import net.sf.sahi.response.SimpleHttpResponse;

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

public class MongoLoggerTest extends TestCase {
	private static final long serialVersionUID = -3402971698869800021L;

	static{
		Configuration.init();
	}

    public void testSaveImageUrl(){
        HttpRequest request = new HttpRequest();
        request.stripHostName("/login?service=http://www.hostname.com/landing", "www.hostname.com", false);
        String s = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><meta http-equiv=\"zxz\"/></head>";
        SimpleHttpResponse response = new SimpleHttpResponse(s);
        response.setData("just for test".getBytes());
        response.setHeader("Content-Type", "image/jpeg");
        MongoLogger.storeImageUrl(request, response);
    }

}
