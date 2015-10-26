package com.teamchat.integrations.appearin;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;

public class AppearInBot
{

	String logoHtml = "<center><img src='http://integration.teamchat.com/sol/bot-images/appear.in.png' width='150' /></center><p></p>";
	String html = logoHtml
			+ "<h3 style='color:#159ceb'>Hi, I am the Appear.in Bot</h3><div>I can help you start an instant video call with your group. Just fill in the details of the call here and I will share a meeting link with the group</div>"
			+ "<div>You can also use the keyword <b>appearin</b> to start the video call</div>";

	@OnKeyword("appearin")
	public void help(TeamchatAPI api)
	{
		start(api);
	}

	@OnKeyword("help")
	public void start(TeamchatAPI api)
	{
		api.perform(api.context().currentRoom()
				.post(new PrimaryChatlet().setQuestionHtml(html).setReplyScreen(api.objects().form().addField(api.objects().input().label("String").name("str"))).alias("start")));
	}

	@OnAlias("start")
	public void connect(TeamchatAPI api)
	{

		String name = (api.context().currentReply().getField("str"));
		String print = "<a href=\"https://appear.in/" + name + "\" target=\"_blank\">Click here to Join Meeting (opens in Browser)</a>";
		String url = "https://appear.in/" + name;
		String print2 = createEmbeddedLink(url, name, "https");
		String print1 = "<a href=" + print2 + ">Click here to Join Meeting (inside Teamchat)</a>";
		String note = "<h5>Note : Currently supported on android and web only </h5>";
		api.perform(api.context().currentRoom()
				.post(new PrimaryChatlet().setQuestionHtml(logoHtml + print1 + "<br><br><div style='color:#ccc'>If above link doesn't work try the link below</div><br>" + print + note)));
	}

	public static String createEmbeddedLink(String url, String title, String protocol)
	{
		JSONObject object = new JSONObject();
		JSONObject web = new JSONObject();
		web.put("title", title);
		web.put("cancelBtnName", "Back");
		web.put("minWidth", "200");
		web.put("draggable", "true");
		web.put("newWindow", "true");
		web.put("url", url);
		object.put("web", web);
		// System.out.println(object.toString());
		byte[] byteArray = Base64.encodeBase64(object.toString().getBytes());
		String encodedString = new String(byteArray);
		String fUrl = protocol + "://teamchat:data=" + encodedString;

		return fUrl;

	}
}
