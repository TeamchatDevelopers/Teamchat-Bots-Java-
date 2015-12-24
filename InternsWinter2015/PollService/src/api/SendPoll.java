package api;

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
		
		String twitterRecipients = request.getParameter("twitterRecipients");
		String slackRecipients = request.getParameter("slackRecipients");
		
		String emailRecipients = request.getParameter("emailRecipients");
		String smsRecipients = request.getParameter("smsRecipients");
		
		String smid = new String();
		
		try {
			smid = hitGetPoll(question,apikey);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(twitterRecipients != null)
		{
			hitSendToTwitter(smid,apikey,twitterRecipients);
		}
		
		if(slackRecipients != null)
		{
			hitSendToSlack(smid, apikey, slackRecipients);
		}
		
		if(emailRecipients != null)
		{
			hitSendToEmail(smid, apikey, emailRecipients);
		}
	
		if(smsRecipients != null)
		{
			hitSendToSMS(smid, apikey, smsRecipients);
		}
		
		response.getWriter().append("SMID :");
		response.getWriter().append(smid);
		
	}
	
	public String hitGetPoll(String question, String apikey) throws IOException, JSONException
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "question="+question);
		Request request = new Request.Builder()
		  .url("http://interns.teamchat.com:8080/PollService/GetPoll")
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
	
	public void hitSendToTwitter(String smid,String apikey,String recipients) throws IOException
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "smid="+smid+"&recipients="+recipients);
		Request request = new Request.Builder()
		  .url("http://interns.teamchat.com:8080/PollService/SendToTwitter")
		  .post(body)
		  .addHeader("apikey", apikey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();
	}
	
	public void hitSendToSlack(String smid,String apikey,String recipients) throws IOException
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "smid="+smid+"&recipients="+recipients);
		Request request = new Request.Builder()
		  .url("http://interns.teamchat.com:8080/PollService/SendToSlack")
		  .post(body)
		  .addHeader("apikey", apikey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		client.newCall(request).execute();
	}

	public void hitSendToEmail(String smid,String apikey,String recipients)
	{
		
	}
	
	public void hitSendToSMS(String smid,String apikey,String recipients)
	{
		
	}
}
