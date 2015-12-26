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
 * Servlet implementation class GetPoll
 */
@WebServlet("/GetPoll")
public class GetPoll extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetPoll() {
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
		
		String question = request.getParameter("question");
		String apikey = request.getHeader("apikey");

		String resp="";
		
		try {
			resp = getSMID(question,apikey);
		} catch (JSONException e) {
			System.err.println("Error in call to smapi create poll");
			e.printStackTrace();
		}
		
		response.getWriter().append(resp);
	}



	public String getSMID(String question, String apikey) throws JSONException, IOException
	{
		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

		OkHttpClient client = new OkHttpClient();

		RequestBody body = RequestBody.create(mediaType, "question="+question);
		Request request = new Request.Builder()
				.url("http://dev-api.webaroo.com/SMApi/api/smartmsg/poll")
				.put(body)
				.addHeader("apikey", apikey)
				.addHeader("cache-control", "no-cache")
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		Response response = client.newCall(request).execute();

		String jsonString = response.body().string();
		JSONObject poll = new JSONObject(jsonString);

		String id = poll.getString("id");
		System.err.println(id);
		return jsonString;
	}

}
