package com.teamchat.integration.qrcodegenerator;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.SimpleJsonChatlet;

public class QRCodeGeneratorBot
{
	@OnKeyword("help")
	public void help(TeamchatAPI api)
	{
		Form f = api.objects().form();
		f.addField(api.objects().input().label("Enter text/url").name("enteredtext").addRegexValidation(".+", "field cannot be left blank"));

		api.perform(api.context().currentRoom().post(new PrimaryChatlet().setQuestionHtml(Utility.help).setReplyScreen(f).setReplyLabel("Enter").alias("generate")));
	}

	@OnKeyword("qrcode")
	public void convert(TeamchatAPI api) throws Exception
	{
		help(api);	
	}

	@OnAlias("generate")
	public void converted(TeamchatAPI api) throws Exception
	{
		String text = api.context().currentReply().getField("enteredtext");
		String encodedText = URLEncoder.encode(text, "UTF-8");
		//String fileName = encodedText.substring(0,10) + ".png";

		String url = Utility.apiUrl.replace("__text", encodedText);
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		con.setRequestProperty("X-Mashape-Key", Utility.apiKey);

		int responseCode = con.getResponseCode();

		if (responseCode == 200)
		{
			InputStream input = con.getInputStream();
			byte[] bytes = IOUtils.toByteArray(input);
			byte[] bytes64 = Base64.encodeBase64(bytes);

			String content = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + text;
			api.performPostInCurrentRoom(new SimpleJsonChatlet(new JSONObject(Utility.htmlLink.replaceAll("__html", Utility.resp.replaceAll("__fileUrl", content)))));
		}
		else
		{
			api.perform(api.context().currentRoom().post(new PrimaryChatlet().setQuestionHtml(Utility.sorry)));
		}
	}
}
