package com.tc.sol.service.smml;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;

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

public class SendToEmail {

	public static void sendToEmail(String smId,String apiKey, String emailCredentials, String messageDesc) throws Exception
	{
		saveEmailAccount(apiKey, emailCredentials);
		getSignedLinkAndSend(apiKey, smId, emailCredentials, messageDesc);
	}
	
	private static void saveEmailAccount(String apiKey,String emailCredentials) throws Exception
	{
		JSONObject emailCreds = new JSONObject(emailCredentials);
		
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, KEYWORDS.ACCOUNT_ID+"="+emailCreds.getString(KEYWORDS.ACCOUNT_ID)+
				"&"+KEYWORDS.PASSWORD+"="+emailCreds.getString(KEYWORDS.PASSWORD));
		
		Request request = new Request.Builder()
		  .url(Utility.config.getProperty(KEYWORDS.EMAIL_ACC_URL))
		  .put(body)
		  .addHeader(KEYWORDS.API_KEY, apiKey)
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();
		
	}

	private static void getSignedLinkAndSend(String apiKey, String smId, String emailCredentials, String messageDesc) throws Exception
	{
		JSONObject emailCreds = new JSONObject(emailCredentials);
		String name = emailCreds.getString(KEYWORDS.NAME);
		String subject = emailCreds.getString(KEYWORDS.SUBJECT);
		
		JSONArray recipientsArray = emailCreds.getJSONArray(KEYWORDS.RECIPIENTS);
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
			
			sendMessageOnEmail(signed_link,user,apiKey,messageDesc,name,subject);
		}
	}
	
	
	public static void sendMessageOnEmail(String poll_link, String user, String apikey, String messageDesc, String name, String subject) throws IOException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		OkHttpClient client = new OkHttpClient();

		String body_str = KEYWORDS.DESTINATION+"="+user+"&"+KEYWORDS.TEXT+"="+messageDesc+"  "
		+URLEncoder.encode(poll_link, "UTF-8")+"&"+KEYWORDS.NAME+"="+name+"&"+KEYWORDS.SUBJECT+"="+subject;
		
		RequestBody body = RequestBody.create(mediaType, body_str);
		Request request = new Request.Builder()
				.url(Utility.config.getProperty(KEYWORDS.EMAIL_SEND_MSG_URL))
				.put(body)
				.addHeader(KEYWORDS.API_KEY, apikey)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		client.newCall(request).execute();
	}
	
}