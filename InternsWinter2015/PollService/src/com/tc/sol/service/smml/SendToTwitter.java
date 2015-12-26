package com.tc.sol.service.smml;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

/**
 * Servlet implementation class SendToSlack
 */
@WebServlet("/SendToTwitter")
public class SendToTwitter extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendToTwitter() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String apikey = request.getHeader("apikey");
		String recipients = request.getParameter("recipients");
		String smid = request.getParameter("smid");
		
		recipients = recipients.trim();
		String[] users = recipients.split(",");
		
		String signed_link = new String();
		
		for(String user : users)
		{
			try {
				signed_link = getSignedLink(user,smid,apikey);
				System.out.println(signed_link);
			} catch (JSONException e) {
				System.err.println("Error in call to SMapi get signed link");
				e.printStackTrace();
			}
			
			sendOneTwitterPoll(signed_link,user,apikey);
		}
	}
	
	public String getSignedLink(String name,String smid, String apikey) throws IOException, JSONException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaType, "destination="+name);
		Request request = new Request.Builder()
				.url("http://dev-api.webaroo.com/SMApi/api/smartmsg/msg/"+smid+"/signedlink")
				.post(body)
				.addHeader("apikey", apikey)
				.addHeader("cache-control", "no-cache")
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		Response response = client.newCall(request).execute();

		String jsonString = response.body().string();
		JSONArray jArray = new JSONArray(jsonString);
		JSONObject poll = jArray.getJSONObject(0);
		String id = poll.getString("id");
		//System.err.println(id);
		String link = "http://dev-smapi.webaroo.com/SMApi/api/embed/"+id;
		return link;
	}
	
	public void sendOneTwitterPoll(String poll_link, String user, String apikey) throws IOException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		OkHttpClient client = new OkHttpClient();

		String body_str = "destination="+user+"&text=Please take this poll : "
		+URLEncoder.encode(poll_link, "UTF-8");
		
		RequestBody body = RequestBody.create(mediaType, body_str);
		Request request = new Request.Builder()
				.url("http://dev-api.webaroo.com/SMApi/api/twitter/msg")
				.put(body)
				.addHeader("apikey", apikey)
				.addHeader("cache-control", "no-cache")
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		client.newCall(request).execute();
	}
	
}
