package com.personalityextractor.url.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class URLContent {
	
	public static String fetchURLContent(String urlStr){
		StringBuffer buf = new StringBuffer();
		try {
			URL url = new URL(urlStr);
			URLConnection yc = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				buf.append(inputLine);
				buf.append("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return buf.toString();
	}
	
	public static String fetchTitleString(String urlContent){
		if(!urlContent.contains("<title>"))
			return null;
		int startIndex= urlContent.indexOf("<title>");
		int endIndex= urlContent.indexOf("</title>");
		return urlContent.substring(startIndex+7, endIndex);
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		URLContent uc = new URLContent();
		uc.fetchTitleString("http://www.nytimes.com/2003/08/07/us/first-test-for-freshmen-picking-roommates.html");
	}

}
