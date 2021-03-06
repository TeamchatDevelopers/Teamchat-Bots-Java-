package com.teamchat.integration.news;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;

public class NewsBot
{
	@OnKeyword("help")
	public void help(TeamchatAPI api)
	{
		Form f = api.objects().form();
		f.addField(api.objects().input()
				.label("Topic:").name("topic"));

		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setQuestionHtml(Utility.help).setReplyScreen(f).setReplyLabel("Topic").alias("gettopic");
		api.perform(api.context().currentRoom().post(prime));
	}

	@OnAlias("gettopic")
	public void search(TeamchatAPI api) throws Exception
	{
		String topic = api.context().currentReply().getField("topic");

		System.out.println(topic);
		boolean isValidJson = true;
		try
		{
			new JSONObject(topic);
		}
		catch (JSONException e)
		{
			isValidJson = false;
		}
		if (!isValidJson)
		{
			topic  = URLEncoder.encode(topic, "UTF-8");
			String url = "http://content.guardianapis.com/search?q=" + topic + "&api-key=[Put your api key here];
			System.out.println(url);

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			in.close();

			// print result
			System.out.println(response.toString());
			JSONObject json = new JSONObject(response.toString());
			int pageSize = json.getJSONObject("response").getInt("pageSize");
			JSONArray arr = json.getJSONObject("response").getJSONArray("results");

			StringBuilder news = new StringBuilder();
			
			news.append("<div style='padding-left:5px; padding-right:5px; margin-bottom:5px'>");
			
			if (arr.length() != 0 && pageSize > 0)
			{
				for (int i = 0; i < arr.length(); i++)
				{
					JSONObject jsona = arr.getJSONObject(i);
					String section = jsona.getString("sectionName");
					String title = jsona.getString("webTitle");
					String urla = jsona.getString("webUrl");
					
					news.append("<div><b>" + section + "</b>" 
									+ "<ul>"
									+ "<li>" + title + "</li>"
									+ "<li><a href='" + urla + "' target='_blank'>go to this link</a></li>"
									+ "</ul>"
									+ "</div>");
					if (i==4)break;
				}
				news.append("</div>");
				api.perform(api.context().currentRoom().post(new PrimaryChatlet().setQuestionHtml("<center><img src='http://integration.teamchat.com/sol/bot-images/news.png' width='150' /></center>"
							+ "<div>" + news.toString() + "</div>")));
			}
			else
			{
				api.perform(api.context().currentRoom().post(new PrimaryChatlet().setQuestionHtml("<center><img src='http://integration.teamchat.com/sol/bot-images/news.png' width='150' /></center>"
						+ "<div>Sorry! no results.</div>")));
			}
		}
	}
}
