package com.integrations;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;


public abstract class TestTranslate {

	/**
	 * @param args
	 * @throws IOException
	 * @throws GoogleAPIException
	 * */

	public static void main(String[] args) throws IOException {
		
		URL obj = new URL(
				"https://www.googleapis.com/language/translate/v2?key=[Put your api key here]&source=en&target=hi&q=how");
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("Response Code : " + responseCode);
		StringWriter writer = new StringWriter();
		if (responseCode == 200) {
			
			IOUtils.copy(con.getInputStream(), writer, "UTF-8");
			System.out.println(writer);
		}


			JSONObject org = new JSONObject(writer.toString());
			JSONObject obj1 = new JSONObject(org.getJSONObject("data").getJSONArray("translations").get(0).toString());
			String decodedText = URLDecoder.decode(obj1.getString("translatedText"), "UTF-8");
			
			System.out.println(decodedText);

		}
		
	}
