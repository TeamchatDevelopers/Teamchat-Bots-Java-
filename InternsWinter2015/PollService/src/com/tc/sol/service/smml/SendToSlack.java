package com.tc.sol.service.smml;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.tc.sol.service.smml.util.Utility;
import com.tc.sol.service.smml.util.Utility.KEYWORDS;

public class SendToSlack {
       
    public SendToSlack() {
        super();
    }

	
    public static void sendToSlack(String smId,String apiKey,String slackCredentials, String msgDesc) throws Exception
	{
		saveSlackAccount(apiKey, slackCredentials);
		getLinkAndSendToSlack(apiKey, smId, slackCredentials, msgDesc);
	}
	
	private static void saveSlackAccount(String apiKey,String slackCredentials) throws Exception
	{
		JSONObject slackCreds = new JSONObject(slackCredentials);
		String slackToken = slackCreds.getString(KEYWORDS.SLACK_TOKEN);
		
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, KEYWORDS.SLACK_TOKEN+"="+slackToken);
		Request request = new Request.Builder()
		  .url(Utility.config.getProperty(KEYWORDS.SLACK_ACC_URL))
		  .put(body)
		  .addHeader(KEYWORDS.API_KEY, apiKey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();
		
	}
	
	protected static void getLinkAndSendToSlack(String apiKey, String smId, String slackCredentials, String msgDesc) throws Exception {
				
		JSONObject slackCreds = new JSONObject(slackCredentials);
		String recipients = slackCreds.getString(KEYWORDS.RECIPIENTS);
		
		recipients = recipients.trim();
		String[] users = recipients.split(",");
		
		String signed_link = new String();
		
		for(String user : users)
		{
			try {
				signed_link = Utility.getSignedLink(user,smId,apiKey);
				System.out.println(signed_link);
			} catch (JSONException e) {
				System.err.println("Error in call to SMapi get signed link");
				e.printStackTrace();
			}
			
			sendMessageOnSlack(signed_link,user,KEYWORDS.BOT_NAME,apiKey,msgDesc);
		}
	}
	
	public static void sendMessageOnSlack(String poll_link, String user, String botName, String apikey, String msgDesc) throws IOException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		OkHttpClient client = new OkHttpClient();

		String body_str = KEYWORDS.DESTINATION+"="+KEYWORDS.USER+"&"+KEYWORDS.TEXT+"="+msgDesc
		+URLEncoder.encode(poll_link, "UTF-8")+"&"+KEYWORDS.NAME+"="+botName;
		
		RequestBody body = RequestBody.create(mediaType, body_str);
		Request request = new Request.Builder()
				.url(Utility.config.getProperty(KEYWORDS.SLACK_SEND_MSG_URL))
				.put(body)
				.addHeader(KEYWORDS.API_KEY, apikey)
				.addHeader("cache-control", "no-cache")
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		client.newCall(request).execute();
	}

}
