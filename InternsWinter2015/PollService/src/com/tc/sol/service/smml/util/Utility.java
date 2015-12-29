package com.tc.sol.service.smml.util;

import java.io.IOException;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class Utility
{
	public static Properties config;
	
	static
	{
		try
		{
			config = loadPropertyFromClasspath("config.properties", Utility.class);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static String getSignedLink(String name,String smId, String apikey) throws IOException, JSONException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaType, KEYWORDS.DESTINATION+"="+name);
		Request request = new Request.Builder()
				.url(config.getProperty(KEYWORDS.GET_SIGNED_LINK_URL.replace("_"+KEYWORDS.SM_ID, smId)))
				.post(body)
				.addHeader(KEYWORDS.API_KEY, apikey)
				.addHeader("cache-control", "no-cache")
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		Response response = client.newCall(request).execute();

		String jsonString = response.body().string();
		JSONArray jArray = new JSONArray(jsonString);
		JSONObject poll = jArray.getJSONObject(0);
		String id = poll.getString("id");
		//System.err.println(id);
		String link = config.getProperty(KEYWORDS.SIGNED_LINK_URL)+id;
		return link;
	}
	
	public static class KEYWORDS {
		public static final String MESSAGE_TYPE = "msgType";
		public static final String MESSAGE_DESC = "msgDesc";
		public static final String BANNER_TEXT = "bannerText";
		public static final String API_KEY = "apiKey";
		public static final String SM_ID = "smId";
		
		public static final String POLL = "poll";
		
		public static final String CONSUMER_KEY = "consumerkey";
		public static final String CONSUMER_SECRET = "consumerSecret";
		public static final String ACCESS_TOKEN = "accessToken";
		public static final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
		public static final String RECIPIENTS = "recipients";
		public static final String DESTINATION = "destination";
		public static final String TEXT = "text";
		
		public static final String TWITTER_CREDS = "twitterCredentials";
		public static final String TWITTER_ACC_URL = "saveTwitterAccountUrl";
		public static final String TWITTER_SEND_MSG_URL = "sendTwitterMsgUrl";
		public static final String GET_SIGNED_LINK_URL = "getSignedLinkUrl";
		public static final String SIGNED_LINK_URL = "signedLinkUrl";
	}
	
	public static Properties loadPropertyFromClasspath(String fileName, Class<?> type) throws IOException
	{
		Properties prop = new Properties();
		prop.load(type.getClassLoader().getResourceAsStream(fileName));
		return prop;
	}
}
