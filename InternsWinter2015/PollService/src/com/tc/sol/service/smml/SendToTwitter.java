package com.tc.sol.service.smml;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.tc.sol.service.smml.util.Utility;
import com.tc.sol.service.smml.util.Utility.KEYWORDS;

public class SendToTwitter {

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendToTwitter() {
		super();

	}

	public static void sendToTwitter(String smId,String apiKey, String twitterCredentials, String messageDesc) throws Exception
	{
		saveTwitterAccount(apiKey, twitterCredentials);
		getSignedLinkAndSend(apiKey, smId, twitterCredentials, messageDesc);
	}

	private static void saveTwitterAccount(String apiKey,String twitterCredentials) throws Exception
	{
		JSONObject twitterCreds = new JSONObject(twitterCredentials);

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, KEYWORDS.CONSUMER_KEY+"="+twitterCreds.getString(KEYWORDS.SM_SERVICE_TWITTER_CONSUMER_KEY)+
				"&"+KEYWORDS.CONSUMER_SECRET+"="+twitterCreds.getString(KEYWORDS.CONSUMER_SECRET)
				+"&"+KEYWORDS.ACCESS_TOKEN+"="+twitterCreds.getString(KEYWORDS.ACCESS_TOKEN)+
				"&"+KEYWORDS.ACCESS_TOKEN_SECRET+"="+twitterCreds.getString(KEYWORDS.ACCESS_TOKEN_SECRET));

		Request request = new Request.Builder()
				.url(Utility.config.getProperty(KEYWORDS.TWITTER_ACC_URL))
				.put(body)
				.addHeader(KEYWORDS.API_KEY, apiKey)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		client.newCall(request).execute();

	}

	private static void getSignedLinkAndSend(String apiKey, String smId, String twitterCredentials, String messageDesc) throws Exception
	{
		JSONObject twitterCreds = new JSONObject(twitterCredentials);

		JSONArray recipientsArray = twitterCreds.getJSONArray(KEYWORDS.RECIPIENTS);
		String json = recipientsArray.toString();
		Type collectionType = new TypeToken<ArrayList<String>>(){}.getType();
		ArrayList<String> recipients = new Gson().fromJson(json, collectionType);

		String signed_link = new String();

		for(String user : recipients)
		{
			try {
				signed_link = Utility.getSignedLink(user.trim(),smId,apiKey);
				System.out.println(signed_link);
			} catch (JSONException e) {
				System.err.println("Error in call to SMapi get signed link");
				e.printStackTrace();
			}

			sendMessageOnTwitter(signed_link,user,apiKey,messageDesc);
		}
	}


	public static void sendMessageOnTwitter(String poll_link, String user, String apikey, String messageDesc) throws IOException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		OkHttpClient client = new OkHttpClient();

		String body_str = KEYWORDS.DESTINATION+"="+user+"&"+KEYWORDS.TEXT+"="+messageDesc+"  "
				+URLEncoder.encode(poll_link, "UTF-8");

		RequestBody body = RequestBody.create(mediaType, body_str);
		Request request = new Request.Builder()
				.url(Utility.config.getProperty(KEYWORDS.TWITTER_SEND_MSG_URL))
				.put(body)
				.addHeader(KEYWORDS.API_KEY, apikey)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		client.newCall(request).execute();
	}

}
