package main;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.*;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.*;

import util.Utility;

import java.lang.Integer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.text.DateFormatSymbols;


public class Planner_Bot 
{
	private static String AuthKey;

	public String plan_name;
	public Integer sHour, sMinute, eHour, eMinute, intInMin , noOfInt, noOfDays, day, year;


	public static void main(String[] args) 
	{
		AuthKey = Utility.config.getProperty("AuthKey");
		TeamchatAPI api = TeamchatAPI.fromFile("config.json")
				.setAuthenticationKey(AuthKey);

		api.startReceivingEvents(new Planner_Bot());
	}

	@OnKeyword(value = "help", isCaseSensitive = false, description = "Creates a form from where you can specify what you want to to do , and it will be done.")
	public void sendHelp(TeamchatAPI api)
	{
		PrimaryChatlet help = new PrimaryChatlet();
		help.setQuestion("What do you want to do ?");

		Form f = api.objects().form();
		f.addField(api.objects().radio().name("input").label("Choose one from the following :")
				.addOption("Create a new plan")
				.addOption("View results of previous plans"));

		help.setReplyScreen(f);
		help.showDetails(false);
		help.allowComments(false);
		help.setReplyLabel("Answer Here");
		help.alias("HelpIsOnTheWay");
		api.performPostInCurrentRoom(help);

	}

	@OnAlias("HelpIsOnTheWay")
	public void handleUserInput(TeamchatAPI api)
	{
		String Reply = api.context().currentReply().getField("input");
		if(Reply.equals("Create a new plan"))
		{
			postForm(api);
		}
		else
		{
			askQuery(api);
		}
	}


	public void postForm(TeamchatAPI api)
	{
		PrimaryChatlet PChat = new PrimaryChatlet();
		PChat.setQuestion("Please enter your plan's details.");

		Form f = api.objects().form();

		f.addField(api.objects().input().name("desc").label("Description :").addRegexValidation(".+", "Please enter a descrption"));
		f.addField(api.objects().input().name("alias").label("Name :").addRegexValidation(".+", "Please enter a name"));

		PChat.setReplyScreen(f);
		PChat.showDetails(false);
		PChat.allowComments(false);
		PChat.setReplyLabel("Answer Here");
		PChat.alias("create");
		api.performPostInCurrentRoom(PChat);
	}


	@OnAlias("create")
	public void ReplyForm(TeamchatAPI api)
	{
		String q = api.context().currentReply().getField("desc");
		plan_name = api.context().currentReply().getField("alias");
		String room = api.context().currentRoom().getId();
		postReplyForm(api, q, plan_name, room);
	}


	public void postReplyForm(TeamchatAPI api, String question, String plan, String room_id ) 
	{
		PrimaryChatlet PChat = new PrimaryChatlet();
		PChat.setQuestionHtml("<center><h3><b>"+plan_name+"</b></h3></center>"
				+ "<h4>"+question+"</h4>Please enter your time preferences.");

		Calendar c = Calendar.getInstance();
		sHour = 8; sMinute = 30; eHour = 23; eMinute = 30; intInMin = 30; noOfDays = 7;
		noOfInt = ((eHour-sHour)*60 + eMinute - sMinute)/intInMin;
		day = c.get(Calendar.DAY_OF_YEAR);
		year = c.get(Calendar.YEAR);

		Form f = api.objects().form();

		SimpleDateFormat df = new SimpleDateFormat("dd MM YYYY, EEEE");
		SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");

		java.util.Date date = new java.util.Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, sHour);
		cal.set(Calendar.MINUTE, sMinute);

		Calendar cal2 = Calendar.getInstance();
		cal2.set(Calendar.HOUR_OF_DAY, sHour);
		cal2.set(Calendar.MINUTE, intInMin+sMinute);

		Field field1 = api.objects().select().name("date").label("Date : ").addRegexValidation(".+", "Please select a date");
		Field field2 = api.objects().select().name("time").label("Time : ").addRegexValidation(".+", "Please select a time");

		field1.value(df.format(cal.getTime()));

		for(Integer i = 0 ; i < noOfDays ; i++)
		{
			field1.addOption(df.format(cal.getTime())); cal.add(Calendar.DATE, 1);
		}

		field2.value(tf.format(cal.getTime())+"-"+tf.format(cal2.getTime()));
		for(Integer i = 0 ; i < noOfInt ; i++)
		{
			field2.addOption(tf.format(cal.getTime())+" - "+tf.format(cal2.getTime()));
			cal.add(Calendar.MINUTE, intInMin);
			cal2.add(Calendar.MINUTE, intInMin);
		}

		f.addField(field1);
		f.addField(field2);

		PChat.setReplyScreen(f);
		PChat.showDetails(true);
		PChat.allowComments(true);
		PChat.setReplyLabel("Enter Here");
		PChat.alias("Form");
		PChat.setMetaInfo(plan+","+sHour+","+sMinute+","+day+","+year+","+intInMin);
		api.performPostInCurrentRoom(PChat);

	}



	@OnAlias("Form")
	public void handleFormReply(TeamchatAPI api) 
	{
		String[] Record = new String[5];
		Record[0] = api.context().currentReply().senderEmail();
		Record[1] = api.context().currentReply().getField("date");
		Record[2] = api.context().currentReply().getField("time");	
		Record[3] = api.context().currentRoom().getId();
		Record[4] = api.context().currentChatlet().getMetaInfo();

		Integer index = Utility.durToInteger(Record[4],Record[1],Record[2]);

		String part[] = Record[4].split(",");
		String plan = part[0];
		String parts[] = Record[1].split(", ");
		Record[1] = Utility.formatDate(Record[1]);

		Utility.insertReply(Record[0], Record[1], parts[1], Record[2], plan, Record[3], index);

	}


	public void askQuery(TeamchatAPI api)
	{

		PrimaryChatlet P = new PrimaryChatlet();
		P.setQuestion("Please enter details of the plan, you want to view the results of.");

		Form f = api.objects().form();

		f.addField(api.objects().input().name("name").label("Plan's Name :").addRegexValidation(".+", "Please enter a name"));
		Field field2 = api.objects().select().name("day").label("Day :").addRegexValidation(".+", "Please select a day");

		field2.addOption("All");

		for(Integer i = 0 ; i < 7 ;i++)
		{
			field2.addOption(new DateFormatSymbols().getWeekdays()[i+1]);
		}

		f.addField(field2);

		P.setReplyScreen(f);
		P.showDetails(true);
		P.allowComments(false);
		P.setReplyLabel("Enter Here");
		P.alias("result");
		api.performPostInCurrentRoom(P);
	}

	@OnAlias("result")
	public void getQuery(TeamchatAPI api)
	{
		String name = api.context().currentReply().getField("name");
		String day = api.context().currentReply().getField("day");
		Utility.displayResult(name+"@@"+day,api);
		
	}


}