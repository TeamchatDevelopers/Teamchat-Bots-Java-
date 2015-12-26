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

/**
 * Servlet implementation class SendPoll
 */
@WebServlet("/SendPoll")
public class SendPoll extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendPoll() {
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
		String apikey = request.getHeader("apikey");
		String question = request.getParameter("question");
		
		String messageType = request.getParameter("messageType");
		
		String twitterRecipients = request.getParameter("twitterRecipients");
		String twitterConsumerkey = request.getParameter("twitterConsumerkey");
		String twitterConsumerSecret = request.getParameter("twitterConsumerSecret");
		String twitterAccessToken = request.getParameter("twitterAccessToken");
		String twitterAccessTokenSecret = request.getParameter("twitterAccessTokenSecret");
		
		String slackRecipients = request.getParameter("slackRecipients");
		String slackToken = request.getParameter("slackToken");
		
		String emailRecipients = request.getParameter("emailRecipients");
		String smsRecipients = request.getParameter("smsRecipients");
		
		String smId = null;
		
		if (messageType != null && messageType.equalsIgnoreCase("poll")) {
			try
			{
				smId = getPoll(question,apikey);
			} catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(twitterRecipients != null && smId != null)
		{
			sendToTwitter(smId,apikey,twitterRecipients,twitterConsumerkey,twitterConsumerSecret,twitterAccessToken,twitterAccessTokenSecret);
		}
		
		if(slackRecipients != null && smId != null)
		{
			sendToSlack(smId, apikey, slackRecipients, slackToken);
		}
		
		if(emailRecipients != null && smId != null)
		{
			sendToEmail(smId, apikey, emailRecipients);
		}
	
		if(smsRecipients != null && smId != null)
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
		  .url("http://localhost:8080/PollService/GetPoll")
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
	
	public void sendToTwitter(String smId,String apiKey,String recipients
			,String twitterConsumerkey, String twitterConsumerSecret, 
			String twitterAccessToken, String twitterAccessTokenSecret) throws IOException
	{
		saveTwitterAccount(apiKey, twitterConsumerkey, twitterConsumerSecret, 
				twitterAccessToken, twitterAccessTokenSecret);
		sendMessageOnTwitter(apiKey, smId, recipients);
	}
	
	private void saveTwitterAccount(String apiKey,String twitterConsumerkey, String twitterConsumerSecret, 
			String twitterAccessToken, String twitterAccessTokenSecret) throws IOException
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "consumerkey="+twitterConsumerkey+"&consumerSecret="+twitterConsumerSecret
				+"&accessToken="+twitterAccessToken+"&accessTokenSecret="+twitterAccessTokenSecret);
		Request request = new Request.Builder()
		  .url("http://dev-api.webaroo.com/SMApi/api/twitter/account")
		  .put(body)
		  .addHeader("apikey", apiKey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();
		
	}

	private void sendMessageOnTwitter(String apiKey, String smId, String recipients) throws IOException
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "smid="+smId+"&recipients="+recipients);
		Request request = new Request.Builder()
		  .url("http://localhost:8080/PollService/SendToTwitter")
		  .post(body)
		  .addHeader("apikey", apiKey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();

		
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
		  .url("http://localhost:8080/PollService/SendToSlack")
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
