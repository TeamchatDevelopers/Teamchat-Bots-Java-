package com.teamchat.integrations.wikipedia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.annotations.OnMsg;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;

public class WikiSearch
{
	@OnKeyword("help")
	public void help(TeamchatAPI api)
	{
		Form f = api.objects().form();
		f.addField(api.objects().input().label("Enter a subject to search in wikipedia").name("search"));

		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setQuestionHtml(
				"<center><img src='http://integration.teamchat.com/sol/bot-images/wikipedia.png' width='150' /></center><h3 style='color:#159ceb'>Hi, I am the Wikipedia Bot.</h3><div>Search for any topic and I will fetch you any information available on Wikipedia on that topic. Just type in a topic you'd like to know about</div><div><p> Click on Search Button </p></div> <div>Also you can type in keyword <b>wiki</b> to start me</div>")
				.setReplyScreen(f).setReplyLabel("Search").alias("searchWikipedia");
		api.perform(api.context().currentRoom().post(prime));

	}
	
	@OnKeyword("wiki")
	public void wiki(TeamchatAPI api)
	{
		
		help(api);
	}

	@OnAlias("searchWikipedia")
	public void SearchWikipedia(TeamchatAPI api) throws IllegalStateException, IOException
	{
		search(api, api.context().currentReply().getField("search"));
	}

	public void search(TeamchatAPI api, String searchKeyword) throws IllegalStateException, IOException
	{

		String searchEncoded = URLEncoder.encode(searchKeyword, "UTF-8");

		String url = "https://en.wikipedia.org/w/api.php?action=opensearch&search=" + searchEncoded + "&format=json";

		System.out.println(url);

		HttpClient client = HttpClientBuilder.create().build();

		HttpGet request = new HttpGet(url);

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();

		System.out.println("Query executed.");

		JSONArray result = new JSONArray(output);

		JSONArray firstResultArray = (JSONArray) result.get(1);
		String firstResultTitle = (String) firstResultArray.get(0);
		System.out.println(firstResultTitle.toString());

		JSONArray details = (JSONArray) result.get(2);
		String detailsOfSearch = (String) details.get(0);
		System.out.println(detailsOfSearch.toString());

		JSONArray linkList = (JSONArray) result.get(3);
		String link = (String) linkList.get(0);

		// String toDisplay = "Read more at:" + link;
		api.performPostInCurrentRoom(new PrimaryChatlet()
				.setQuestionHtml("<center><img src='http://integration.teamchat.com/sol/bot-images/wikipedia.png' width='150' /></center><h3 style='color:#159ceb'>" + firstResultTitle
						+ "</h3><br><h5>" + detailsOfSearch + "</h5><br><a href='" + link + "' target='_blank'>Read more</a>"));

	}

}
