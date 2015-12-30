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

public class SendToSMS {

	public static void sendToSMS(String smId,String apiKey, String smsCredentials, String messageDesc) throws Exception
	{
		saveSMSAccount(apiKey, smsCredentials);
		getSignedLinkAndSend(apiKey, smId, smsCredentials, messageDesc);
	}

	private static void saveSMSAccount(String apiKey,String smsCredentials) throws Exception
	{
		JSONObject smsCreds = new JSONObject(smsCredentials);

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, KEYWORDS.ACCOUNT_ID+"="+smsCreds.getString(KEYWORDS.SM_SERVICE_ACCOUNT_ID)+
				"&"+KEYWORDS.PASSWORD+"="+smsCreds.getString(KEYWORDS.PASSWORD));

		Request request = new Request.Builder()
				.url(Utility.config.getProperty(KEYWORDS.SMS_ACC_URL))
				.put(body)
				.addHeader(KEYWORDS.API_KEY, apiKey)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		client.newCall(request).execute();

	}

	private static void getSignedLinkAndSend(String apiKey, String smId, String smsCredentials, String messageDesc) throws Exception
	{
		JSONObject smsCreds = new JSONObject(smsCredentials);
		String source = smsCreds.getString(KEYWORDS.SOURCE);

		JSONArray recipientsArray = smsCreds.getJSONArray(KEYWORDS.RECIPIENTS);
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

			sendMessageOnSMS(signed_link,user,apiKey,messageDesc,source);
		}
	}


	public static void sendMessageOnSMS(String poll_link, String user, String apikey, String messageDesc, String source) throws IOException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		OkHttpClient client = new OkHttpClient();

		String body_str = KEYWORDS.DESTINATION+"="+user+"&"+KEYWORDS.TEXT+"="+messageDesc+"  "
				+URLEncoder.encode(poll_link, "UTF-8")+"&"+KEYWORDS.SOURCE+"="+source;

		RequestBody body = RequestBody.create(mediaType, body_str);
		Request request = new Request.Builder()
				.url(Utility.config.getProperty(KEYWORDS.SMS_SEND_MSG_URL))
				.put(body)
				.addHeader(KEYWORDS.API_KEY, apikey)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		client.newCall(request).execute();
	}

}