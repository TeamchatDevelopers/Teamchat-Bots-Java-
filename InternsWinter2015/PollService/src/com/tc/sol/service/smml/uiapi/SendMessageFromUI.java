package com.tc.sol.service.smml.uiapi;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tc.sol.service.smml.util.Utility.KEYWORDS;

/**
 * Servlet implementation class SendMessageFromUI
 */
@WebServlet("/SendMessageFromUI")
public class SendMessageFromUI extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMessageFromUI() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter writer = response.getWriter();
		String code = request.getParameter("code");
		
		try {

			if ((code == null || !code.equalsIgnoreCase("password@123")))
			{
				writer.write("Invalid auth code");
				writer.flush();
				writer.close();
			}
			
			String smId = request.getParameter(KEYWORDS.SM_SERVICE_SM_ID);
			if (smId != null && !smId.equalsIgnoreCase("")) {
				//This is the place where the user wants the replies
				String apiKey = request.getParameter("apiKey2");
				
				MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
				OkHttpClient client = new OkHttpClient();
				client.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
				client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
				
				String body_str = KEYWORDS.SM_SERVICE_SM_ID+"="+smId;
						
				RequestBody body = RequestBody.create(mediaType, body_str);
				
				Request sendMsgRequest = new Request.Builder()
						.url("http://localhost:8080/SmartMessageService/getReplies")
						.post(body)
						.addHeader(KEYWORDS.SM_SERVICE_API_KEY, apiKey)
						.addHeader("content-type", "application/x-www-form-urlencoded")
						.build();

				Response res = client.newCall(sendMsgRequest).execute();
				
				String replies = res.body().string();
				writer.write("<html>"+replies+"</html>");
				res.body().close();
				
				return;
			}
			
			String apiKey = request.getParameter(KEYWORDS.SM_SERVICE_API_KEY);
			String msgType = request.getParameter(KEYWORDS.MESSAGE_TYPE);
			String question = request.getParameter(KEYWORDS.QUESTION);
			String msgDesc = request.getParameter(KEYWORDS.MESSAGE_DESC);
			
			JSONObject twitterCreds = new JSONObject();
			twitterCreds.put(KEYWORDS.SM_SERVICE_TWITTER_CONSUMER_KEY, request.getParameter("twitterconsumerKey"));
			twitterCreds.put(KEYWORDS.CONSUMER_SECRET, request.getParameter("twitterconsumerSecret"));
			twitterCreds.put(KEYWORDS.ACCESS_TOKEN, request.getParameter("twitteraccessToken"));
			twitterCreds.put(KEYWORDS.ACCESS_TOKEN_SECRET, request.getParameter("twitteraccessTokenSecret"));
			twitterCreds.put(KEYWORDS.RECIPIENTS, new ArrayList<String>(Arrays.asList(request.getParameter("twitterrecipients").split(","))));
			
			JSONObject slackCreds = new JSONObject();
			slackCreds.put(KEYWORDS.SLACK_TOKEN, request.getParameter(KEYWORDS.SLACK_TOKEN));
			slackCreds.put(KEYWORDS.RECIPIENTS, new ArrayList<String>(Arrays.asList(request.getParameter("slackrecipients").split(","))));
			
			JSONObject smsCreds = new JSONObject();
			smsCreds.put(KEYWORDS.SM_SERVICE_ACCOUNT_ID, request.getParameter("smsaccountId"));
			smsCreds.put(KEYWORDS.PASSWORD, request.getParameter("smspassword"));
			smsCreds.put(KEYWORDS.SOURCE, request.getParameter("smssource"));
			smsCreds.put(KEYWORDS.RECIPIENTS, new ArrayList<String>(Arrays.asList(request.getParameter("smsrecipients").split(","))));
			
			JSONObject emailCreds = new JSONObject();
			emailCreds.put(KEYWORDS.SM_SERVICE_ACCOUNT_ID, request.getParameter("smsaccountId"));
			emailCreds.put(KEYWORDS.PASSWORD, request.getParameter("smspassword"));
			emailCreds.put(KEYWORDS.NAME, request.getParameter("emailname"));
			emailCreds.put(KEYWORDS.SUBJECT, request.getParameter("emailsubject"));
			emailCreds.put(KEYWORDS.RECIPIENTS, new ArrayList<String>(Arrays.asList(request.getParameter("emailrecipients").split(","))));
			
			
			
			MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
			OkHttpClient client = new OkHttpClient();
			client.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
			client.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout
			
			String body_str = KEYWORDS.QUESTION+"="+question+"&"+KEYWORDS.MESSAGE_TYPE+"="+
								msgType+"&"+KEYWORDS.MESSAGE_DESC+"="+msgDesc
								+"&"+KEYWORDS.TWITTER_CREDS+"="+twitterCreds
								+"&"+KEYWORDS.SLACK_CREDS+"="+slackCreds
								+"&"+KEYWORDS.SMS_CREDS+"="+smsCreds
								+"&"+KEYWORDS.EMAIL_CREDS+"="+emailCreds;
					
			RequestBody body = RequestBody.create(mediaType, body_str);
			
			Request sendMsgRequest = new Request.Builder()
					.url("http://localhost:8080/SmartMessageService/sendMsg")
					.post(body)
					.addHeader(KEYWORDS.SM_SERVICE_API_KEY, apiKey)
					.addHeader("content-type", "application/x-www-form-urlencoded")
					.build();

			Response res = client.newCall(sendMsgRequest).execute();
			
			System.out.println("Response message: " + res.message());
			String smIdReceived = new JSONObject(res.body().string()).getString(KEYWORDS.SM_ID);
			writer.write(smIdReceived);
			res.body().close();
			
		} catch(Exception e) {
			writer.write(e.getMessage());
		}
		
		writer.flush();
		writer.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
