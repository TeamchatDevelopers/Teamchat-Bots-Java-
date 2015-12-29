package com.tc.sol.service.smml;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tc.sol.service.smml.util.Utility.KEYWORDS;

/**
 * Servlet implementation class SendPoll
 */
@WebServlet("/sendMsg")
public class SendMsg extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMsg() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String apikey = request.getHeader(KEYWORDS.API_KEY);
		String bannerText = request.getParameter(KEYWORDS.BANNER_TEXT);		
		String messageType = request.getParameter(KEYWORDS.MESSAGE_TYPE);
		String messageDesc = request.getParameter(KEYWORDS.MESSAGE_DESC);
		
		String smId = null;
		
		if (messageType != null && messageType.equalsIgnoreCase(KEYWORDS.POLL)) {
			try
			{
				smId = getPoll(bannerText,apikey);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(smId != null)
		{
			String twitterCredentials = request.getParameter(KEYWORDS.TWITTER_CREDS);
			if (twitterCredentials != null)
				SendToTwitter.sendToTwitter(smId,apikey,twitterCredentials,messageDesc);
			
			
		}
		
		if(slackRecipients != null)
		{
			sendToSlack(smId, apikey, slackRecipients, slackToken);
		}
		
		if(emailRecipients != null)
		{
			sendToEmail(smId, apikey, emailRecipients);
		}
	
		if(smsRecipients != null)
		{
			sendToSMS(smId, apikey, smsRecipients);
		}
		
		response.getWriter().append("SMID :");
		response.getWriter().append(smId);
		
	}
	
	public String getPoll(String question, String apikey) throws IOException, JSONException
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "question="+question);
		Request request = new Request.Builder()
		  .url("http://stag-solutions.teamchat.com/SmartMessageService/GetPoll")
		  .post(body)
		  .addHeader("apikey", apikey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		Response response = client.newCall(request).execute();
		
		
		String jsonString = response.body().string();
		JSONObject poll = new JSONObject(jsonString);

		String id = poll.getString("id");
		
		return id;
	}
	
	

	public void sendToSlack(String smId,String apiKey,String recipients, String slackToken) throws IOException
	{
		saveSlackAccount(apiKey, slackToken);
		sendMessageOnSlack(smId, recipients, apiKey);
	}
	
	private void saveSlackAccount(String apiKey,String slackToken) throws IOException
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "slackToken="+slackToken);
		Request request = new Request.Builder()
		  .url("http://dev-api.webaroo.com/SMApi/api/slack/account")
		  .put(body)
		  .addHeader("apikey", apiKey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();
		
	}
	
	public void sendMessageOnSlack(String smId, String recipients, String apiKey) throws IOException {
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "smid="+smId+"&recipients="+recipients);
		Request request = new Request.Builder()
		  .url("http://stag-solutions.teamchat.com/SmartMessageService/SendToSlack")
		  .post(body)
		  .addHeader("apikey", apiKey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();
	}

	public void sendToEmail(String smid,String apikey,String recipients)
	{
		
	}
	
	public void sendToSMS(String smid,String apikey,String recipients)
	{
		
	}
}
