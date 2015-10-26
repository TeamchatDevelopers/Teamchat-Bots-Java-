package com.integrations;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;

public class Translator
{
	private static Properties langProps;
	static
	{
		try
		{
			langProps = loadPropertyFromClasspath("language.properties", Translator.class);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnKeyword("help")
	public void onHelp(TeamchatAPI api)
	{

		onTranslate(api);
	}

	@OnKeyword("translate")
	public void onTranslate(TeamchatAPI api)
	{

		String introHtml = Constants.html;

		Form translateForm = api.objects().form();

		translateForm.addField(api.objects().input().label("Enter your text to translate").addRegexValidation(".+", "Please enter a value").name("entertext"));
		translateForm.addField(api.objects().input().label("From Language (e.g. english , french )").addRegexValidation(".+", "Please enter a value").name("fromlanguage"));
		translateForm.addField(api.objects().input().label("To Language (e.g. russian , polish )").addRegexValidation(".+", "Please enter a value").name("tolanguage"));

		api.performPostInCurrentRoom(new PrimaryChatlet().setQuestionHtml(introHtml).showDetails(true).setReplyScreen(translateForm).setReplyLabel("Translate").alias("alias_translate"));
	}

	@OnAlias("alias_translate")
	public <JsonNode> void onTranslateAlias(TeamchatAPI api) throws NumberFormatException
	{

		String text = api.context().currentReply().getField("entertext");
		String fromLang = api.context().currentReply().getField("fromlanguage");
		String toLang = api.context().currentReply().getField("tolanguage");
		StringBuffer response = new StringBuffer();
		try
		{
			if (langProps.getProperty(langString(toLang)) != null)
			{
				URL obj = new URL(Constants.url + Constants.apikey + "&source=" + langProps.getProperty(langString(fromLang)).trim() + "&target=" + langProps.getProperty(langString(toLang)).trim()
						+ "&q=" + URLEncoder.encode(text, "UTF-8"));
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();

				// optional default is GET
				con.setRequestMethod("GET");
				con.setRequestProperty("content-type", "application/json; charset=UTF-8");

				int responseCode = con.getResponseCode();
				// System.out.println("Response Code : " + responseCode);

				/*
				 * StringWriter writer = new StringWriter();
				 * IOUtils.copy(con.getInputStream(), writer, "UTF-8");
				 */

				StringWriter writer = new StringWriter();
				if (responseCode == 200)
				{

					IOUtils.copy(con.getInputStream(), writer, "UTF-8");
					System.out.println(response);
				}

				JSONObject org = new JSONObject(writer.toString());
				JSONObject obj1 = new JSONObject(org.getJSONObject("data").getJSONArray("translations").get(0).toString());
				// System.out.println(obj1.getString("translatedText"));
				String decodedText = URLDecoder.decode(obj1.getString("translatedText"), "UTF-8");
				
				String responseHtml =  String.format(Constants.translateString, fromLang,text,toLang,decodedText);
				api.performPostInCurrentRoom(new PrimaryChatlet().setQuestionHtml(responseHtml));

			}
			else
			{
				api.performPostInCurrentRoom(new PrimaryChatlet().setQuestionHtml("<i> Please check the language entered</i>"));
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}

	}

	public static Properties loadPropertyFromClasspath(String fileName, Class<?> type) throws IOException
	{

		Properties prop = new Properties();
		prop.load(type.getClassLoader().getResourceAsStream(fileName));
		return prop;

	}

	public static String langString(String lang)
	{
		String cap = lang.substring(0, 1).toUpperCase() + lang.substring(1).toLowerCase();
		System.out.println(cap);
		return cap;
	}
}
