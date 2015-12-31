package main;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import callback.Report;

/**
 * Servlet implementation class Bot
 */
@WebServlet("/Bot")
public class Bot extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Bot() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("in doPost");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String apiKey = request.getHeader("apiKey");

		String action = request.getParameter("action");
		String groupId = request.getParameter("groupId");

		final String fields = "[   {     'type': 'input',     'label': 'Amount',     'key': 'amount'   }]";
		final String callbackUrlPlan = "http://localhost:8080/PlanVsActual/FormCallback";
		final String callbackUrlActual = "http://localhost:8080/PlanVsActual/FormActualCallback";
		final String html = null;
		final String commentsEnabled = "true";
		final String detailsEnabled = "true";


		if(action.equalsIgnoreCase("plan"))
		{
			String text = "Please enter your sales plan for today";
			hitGroupSendForm(apiKey, groupId, text, callbackUrlPlan, html, commentsEnabled, detailsEnabled, fields);
		}

		if(action.equalsIgnoreCase("actual"))
		{
			String text = "Please enter your Actual Sales for today";
			hitGroupSendForm(apiKey, groupId, text, callbackUrlActual, html, commentsEnabled, detailsEnabled, fields);
		}

		if(action.equalsIgnoreCase("report"))
		{
			String b = "";
			String rep = Report.getReport();
			System.out.println(rep);
			sendTextHtml(apiKey, groupId, rep);
		}
	}

	public void sendTextHtml(String apiKey, String groupId, String htmltext)
	{
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "text="+htmltext);
		Request request = new Request.Builder()
				.url("http://dev-api.webaroo.com/TeamchatRestCore/api/groups/"+groupId+"/messages/textmessage")
				.put(body)
				.addHeader("apikey", apiKey)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void hitGroupSendForm(String apiKey,String groupId,String text,String callbackUrl,String html,String commentsEnabled,
			String detailsEnabled, String fields)
	{
		OkHttpClient client = new OkHttpClient();

		String encodedCallbackUrl = null;
		try {
			encodedCallbackUrl = URLEncoder.encode(callbackUrl, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = null;
		if(html == null){
			body = RequestBody.create(mediaType, "text="+text+"&callbackUrl="+callbackUrl+"&html1="
					+"&commentsEnabled="+commentsEnabled+"&detailsEnabled="+detailsEnabled+"&fields="+fields);
		}
		else
		{
			body = RequestBody.create(mediaType, "html="+html+"&commentsEnabled="+commentsEnabled+"&detailsEnabled="+detailsEnabled);
		}
		Request request = new Request.Builder()
				.url("http://dev-api.webaroo.com/TeamchatRestCore/api/groups/"+groupId+"/messages/form")
				.put(body)
				.addHeader("apikey", apiKey)
				.addHeader("content-type", "application/x-www-form-urlencoded")
				.build();

		try {
			Response response = client.newCall(request).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}