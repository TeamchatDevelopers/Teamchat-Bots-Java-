package com.tc.sol.service.smml;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * Servlet implementation class GetReplies
 */
@WebServlet("/getReplies")
public class GetReplies extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetReplies() {
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
		String smid = request.getParameter("smid");

		OkHttpClient client = new OkHttpClient();

		Request request1 = new Request.Builder()
				.url("http://dev-api.webaroo.com/SMApi/api/smartmsg/msg/"+smid)
				.get()
				.addHeader("apikey", apikey)
				.addHeader("cache-control", "no-cache")
				.build();

		Response response1 = client.newCall(request1).execute();
		
		JSONObject poll;JSONObject payload;
		String question = new String();
		String jsonString = response1.body().string();

		
		try {
			poll = new JSONObject(jsonString);
			payload = poll.getJSONObject("payload");
			question = payload.getString("content");
		} catch (JSONException e) {
			e.printStackTrace();
		}
			
		response.getWriter().flush();
		response.getWriter().append("Question : " );
		response.getWriter().append(question);
		
		OkHttpClient client2 = new OkHttpClient();

		Request request2 = new Request.Builder()
		  .url("http://dev-api.webaroo.com/SMApi/api/smartmsg/msg/"+smid+"/replies?apikey="+apikey)
		  .get()
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response response2 = client2.newCall(request2).execute();
		
		String replies = response2.body().string();
		
		response.getWriter().append(replies);

	}

}
