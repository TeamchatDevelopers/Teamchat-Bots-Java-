package com.teamchat.integration.meetup.bot;

/*
 * *@author : Akshit Sheth
 * 
 */
import java.io.IOException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.integration.meetup.test.EventTest;

public class MeetupBot
{
	public static final String helpHtml = "<center><img src='http://integration.teamchat.com/sol/bot-images/meetup.png' width='150' /></center><h3 style='color:#159ceb'>Hi, I am the Meetup Bot</h3><div><p>I will help you find interesting meetups in a city you select here.</p></div><div>You can also use the keyword <b>meetup</b> to search for the meetups</div>";
	
	@OnKeyword("help")
	public void help(TeamchatAPI api)
	{

		Form f = api.objects().form();
		f.addField(api.objects().select().addOption("Delhi").addOption("Mumbai").addOption("Chennai").addOption("Kolkata").addOption("Hyderabad").label("City:").name("City"));

		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setQuestionHtml(helpHtml).setReplyScreen(f).setReplyLabel("Reply").alias("getdata");
		api.perform(api.context().currentRoom().post(prime));

	}

	// Use this keyword to search any company
	@OnKeyword("meetup")
	public void meetup(TeamchatAPI api)
	{
		help(api);
	}

	// getting the data from glassdoor server
	@OnAlias("getdata")
	public void getdata(TeamchatAPI api) throws IOException
	{

		String city = api.context().currentReply().getField("City");
		
		EventTest et = new EventTest();
		try
		{
			String eventsArray = et.callGetEventByCity(city);
			JSONArray eventsDetails = (new JSONObject(eventsArray)).getJSONArray("results");
			String eventName = "";
			String eventDescription = "";
			String eventUrl = "";
			Date eventTime = null;
			
			for (int i = 0; i < eventsArray.length(); i++)
			{
				try
				{
					JSONObject event = eventsDetails.getJSONObject(i);
					eventName = event.getString("name");
					eventDescription = event.getString("description");
					eventUrl = event.getString("event_url");
					eventTime = new Date(event.getLong("time"));
					
					System.out.println(eventName);
					System.out.println(eventDescription);
					System.out.println(eventUrl);
					System.out.println(eventTime);
					
					
					api.perform(api
							.context()
							.currentRoom()
							.post(new PrimaryChatlet().setQuestionHtml("<div style='width:300px;'><div><b>Name:</b> "+eventName+"</div>"
									+ "<div><b>Description:</b> "+eventDescription.substring(0, 200)+"...</div>"
											+ "<div><b>URL:</b> <a href = '"+eventUrl+"' target = '_blank'>"+"Click Here"+"</a></div>"
													+ "<div><b>Time:</b> "+eventTime+"</div></div>")));

					if (i == 10)break;
					
					
				} catch (Exception e)
				{
					System.out.println("Error occurred");
				}
			}
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
