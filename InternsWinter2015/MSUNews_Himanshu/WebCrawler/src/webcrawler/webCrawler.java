package webcrawler;

import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Room;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PollChatlet;

import java.util.Properties;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.quartz.*;
import org.quartz.SchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.lang.*;

public class webCrawler {
	// api key of the bot
	public static final String authKey = "2b7b3f733e724b32cba0241d1a3e4e20";
	public Vector<String> storyTitles = new Vector<String>();
	public Vector<String> links = new Vector<String>();
	public Vector<String> pubDates = new Vector<String>();
	public Vector<String> storyDescriptions = new Vector<String>();
	public static Properties properties = new Properties();

	/**
	 * This method creates instance of teamchat client, login using specified
	 * email/password and wait for messages from other users
	 **/
	public static void main(String[] args) {

		TeamchatAPI api = TeamchatAPI.fromFile("config.json").setAuthenticationKey(authKey);
		webCrawler wc = new webCrawler();
		api.startReceivingEvents(wc);

		try {
			InputStream input = new FileInputStream("webCrawler.properties");
			// Initialize the properties
			properties.load(input);
			input.close();
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			sched.start();
			// Job

			JobDetail job = newJob(Schedule.class).withIdentity("news", "group1").build();
			job.getJobDataMap().put("API", api);
			job.getJobDataMap().put("WC", wc);
			
			String schedulerParam=properties.getProperty("scheduler_param");
			//String schedulerParam="0 0/1 * * * ?";
			// Trigger
			CronTrigger trigger = newTrigger().withIdentity("Trigger", "group1")
					.withSchedule(cronSchedule(schedulerParam)).build();
			sched.scheduleJob(job, trigger);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String formhtmlString(int i) {
		String mystr = "";
		String datestr = "";
		String timestr = "";
		String detailedDate = pubDates.elementAt(i);
		int len = detailedDate.length();
		datestr = detailedDate.substring(0, (len - 12));
		timestr = detailedDate.substring((len - 12));

		mystr =  properties.getProperty("styleString")
				+ "   <body>  " 
				+ "       <div class='story'>  " 
				+ "           <div class='storyTitle'>  "
				+ "               <h5 class='theTitle'>  " 
				+ "                   <a href='" 
				+ links.elementAt(i)
				+ "' title='Click here to read the whole story' target='_blank'>  " 
				+ storyTitles.elementAt(i)
				+ "                   </a>  " 
				+ "               </h5>  " 
				+ "           </div>  "
				+ "           <div class='date'>  " 
				+ "               <p class='datePara' title=' at " 
				+ timestr
				+ " '>  " 
				+ "                   <i>" 
				+ datestr 
				+ "</i>  " 
				+ "               </p>  "
				+ "           </div>  " 
				+ "           <div class='descr'>  "
				+ "               <p class='description'>  " 
				+ storyDescriptions.elementAt(i) 
				+ "               </p>  "
				+ "           </div>  " 
				+ "       </div>  " 
				+ "  </body>  ";
		return mystr;
	}

	public void setData(String urlAdd) throws IOException {
		System.out.println("entered setData");
		storyTitles.clear();
		links.clear();
		pubDates.clear();
		storyDescriptions.clear();
		//String siteUrl=properties.getProperty("hostlink");
		String siteUrl=urlAdd;
		Document doc = Jsoup.connect(siteUrl).get();
		Elements titleList = doc.getElementsByTag("title");// ignore the first
		Elements linkList = doc.getElementsByTag("guid");
		Elements dateList = doc.getElementsByTag("pubDate");
		Elements descrList = doc.getElementsByTag("description");// ignore the first

		int count = 0;
		String tempstr = "";
		for (Element inputElement : titleList) {
			if (count != 0) {
				tempstr = inputElement.html();
				storyTitles.add(tempstr);
			}
			count++;
		}

		count = 0;
		tempstr = "";
		for (Element inputElement : linkList) {
			tempstr = inputElement.html();
			links.add(tempstr);
			count++;
		}

		count = 0;
		tempstr = "";
		for (Element inputElement : dateList) {
			tempstr = inputElement.html();
			pubDates.add(tempstr);
			count++;
		}

		count = 0;
		tempstr = "";
		for (Element inputElement : descrList) {
			if (count != 0) {
				tempstr = inputElement.html();
				storyDescriptions.add(tempstr);
			}
			count++;
		}
		System.out.println("exiting setData");
	}

	public void showData(TeamchatAPI api, String roomID) {
		System.out.println("entered showData");
		int i = 0;
		for (i = 0; i < pubDates.size(); i++) {
			// form basic table string
			String htmlstr = formhtmlString(i);

			// add styling for the table
			PollChatlet p2 = new PollChatlet();

			// Set the html question for the chatlet
			p2.setQuestionHtml(htmlstr);

			// Decide if replies will be visible to everyone in the room
			// Also, label can be changed
			p2.showDetails(true);
			p2.setDetailsLabel("Responses");
			/*
			 * // Decide if commenting is enabled on this chatlet or not
			 * p2.allowComments(true);
			 * 
			 * // Can change reply button label p2.setReplyLabel("Rate");
			 */

			// finally post the chatlet in the current room
			//String RoomID=properties.getProperty("roomID");
			String RoomID=roomID;
			Room R = api.context().byId(RoomID);
			api.perform(R.post(p2));
		}
		System.out.println("exiting showData");
	}
	
	public void showNews(TeamchatAPI api) throws IOException {		

		String[] url_list=(properties.getProperty("hostlink")).split(",");
		String[] room_list=(properties.getProperty("roomID")).split(",");
		int len=url_list.length;
		for(int i=0; i<len; i++)
		{
			System.out.println("calling setData");
			setData(url_list[i]);
			System.out.println("calling showData");
			showData(api, room_list[i]);
		}
	}
	
	@OnKeyword(value="msunews")
	public void showNewsOnKeyword (TeamchatAPI api) throws IOException
	{
		String[] url_list=(properties.getProperty("hostlink")).split(",");
		String[] room_list=(properties.getProperty("roomID")).split(",");
		int len=url_list.length;
		for(int i=0; i<len; i++)
		{
			if(room_list[i].equals(api.context().currentRoom().getId()))
			{
				System.out.println("calling setData");
				setData(url_list[i]);
				System.out.println("calling showData");
				showData(api, room_list[i]);
				break;
			}
			else
			{
				continue;
			}
		}
		
	}
	
}