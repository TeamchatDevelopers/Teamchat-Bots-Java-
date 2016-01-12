package com.tc.sol.service.smml;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
import com.tc.sol.service.smml.util.Utility;
import com.tc.sol.service.smml.util.Utility.KEYWORDS;

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

		String apikey = request.getHeader(KEYWORDS.SM_SERVICE_API_KEY);
		String smid = request.getParameter(KEYWORDS.SM_SERVICE_SM_ID);

		OkHttpClient client = new OkHttpClient();

		Request request1 = new Request.Builder()
				.url((Utility.config.getProperty(KEYWORDS.GET_MSG_DETAILS_URL))+smid)
				.get()
				.addHeader(KEYWORDS.API_KEY, apikey)
				.build();

		Response response1 = client.newCall(request1).execute();

		JSONObject poll;
		JSONObject payload;
		String question = new String();
		String jsonString = response1.body().string();


		try {
			poll = new JSONObject(jsonString);
			payload = poll.getJSONObject(KEYWORDS.PAYLOAD);
			question = payload.getString(KEYWORDS.CONTENT);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		response.getWriter().flush();
		response.getWriter().append("Question : " );
		response.getWriter().append(question);

		OkHttpClient client2 = new OkHttpClient();
		client2.setConnectTimeout(15, TimeUnit.SECONDS); // connect timeout
		client2.setReadTimeout(15, TimeUnit.SECONDS);    // socket timeout

		Request request2 = new Request.Builder()
				.url(Utility.config.getProperty(KEYWORDS.GET_REPLIES_URL).replace("_"+KEYWORDS.SM_ID, smid).replace("_"+KEYWORDS.API_KEY, apikey))
				.get()
				.addHeader("cache-control", "no-cache")
				.build();

		Response response2 = client2.newCall(request2).execute();

		String replies = response2.body().string();
		System.out.println(replies);

		response.getWriter().write(replies);


	}

}
