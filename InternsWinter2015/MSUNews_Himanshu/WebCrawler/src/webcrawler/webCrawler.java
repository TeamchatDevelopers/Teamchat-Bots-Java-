package webcrawler;

//import com.teamchat.client.annotations.OnAlias;
//import com.teamchat.client.annotations.OnKeyword;
//import com.teamchat.client.sdk.Field;
//import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.Room;
import com.teamchat.client.sdk.TeamchatAPI;
//import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
//import com.teamchat.client.sdk.chatlets.TextChatlet;
import com.teamchat.client.sdk.chatlets.PollChatlet;
import java.util.Vector;

//import org.jsoup.Connection;
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

//import java.util.Comparator;
import java.io.IOException;

public class webCrawler {
	// api key of the bot
	public static final String authKey = "2b7b3f733e724b32cba0241d1a3e4e20";
	// public Vector <String> categories = new Vector <String>();
	public Vector<String> storyTitles = new Vector<String>();
	public Vector<String> links = new Vector<String>();
	public Vector<String> pubDates = new Vector<String>();
	public Vector<String> storyDescriptions = new Vector<String>();

	/**
	 * This method creates instance of teamchat client, login using specified
	 * email/password and wait for messages from other users
	 **/
	public static void main(String[] args) {

		TeamchatAPI api = TeamchatAPI.fromFile("config.json").setAuthenticationKey(authKey);
		webCrawler wc = new webCrawler();
		api.startReceivingEvents(wc);

		try {
			SchedulerFactory sf = new StdSchedulerFactory();
			Scheduler sched = sf.getScheduler();
			sched.start();
			// Job

			JobDetail job = newJob(Schedule.class).withIdentity("news", "group1").build();
			job.getJobDataMap().put("API", api);
			job.getJobDataMap().put("WC", wc);

			String interval = "5";
			// Trigger
			CronTrigger trigger = newTrigger().withIdentity("Trigger", "group1")
					.withSchedule(cronSchedule("0 0/1 * * * ?")).build();
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

		mystr = "   <head>  " + "       <style>  " +
				/*
				 * unvisited link "           a : link " + "           {  " +
				 * "               color: red;  " + "           }  " +
				 */
				/*
				 * visited link "           a : visited " + "           {  " +
				 * "               color: green;  " + "           }  " +
				 */
				/* mouse over link */
		"           a : hover " + "           {  " + "               font-size: 110% ;  " + "           }  " +
				/*
				 * selected link "           a : active " + "           {  " +
				 * "               color: blue;  " + "           }  " +
				 */
				"           .story  " + "           {  " + "               padding: 2%;  " + "           }  "
				+ "           h5.theTitle  " + "           {  " + "               text-align: center;  "
				+ "               font-size: 120%;  " + "               text-transform: uppercase;  "
				+ "               text-shadow: 1px 1px #737373;  " + "           }  " + "           p.datePara  "
				+ "           {  " + "               text-align: left;  " + "               color: #999999;  "
				+ "           }  " + "           p.description  " + "           {  "
				+ "               text-align: left;  " + "           }  " + "       </style>  " + "   </head  "
				+ "     " + "   <body>  " + "       <div class='story'>  " + "           <div class='storyTitle'>  "
				+ "               <h5 class='theTitle'>  " + "                   <a href='" + links.elementAt(i)
				+ "' title='Click here to read the whole story' target='_blank'>  " + storyTitles.elementAt(i)
				+ "                   </a>  " + "               </h5>  " + "           </div>  "
				+ "           <div class='date'>  " + "               <p class='datePara' title=' at " + timestr
				+ " '>  " + "                   <i>" + datestr + "</i>  " + "               </p>  "
				+ "           </div>  " + "           <div class='descr'>  "
				+ "               <p class='description'>  " + storyDescriptions.elementAt(i) + "               </p>  "
				+ "           </div>  " + "       </div>  " + "  </body>  ";
		return mystr;
	}

	public void setData() throws IOException {
		System.out.println("entered setData");
		storyTitles.clear();
		links.clear();
		pubDates.clear();
		storyDescriptions.clear();
		Document doc = Jsoup.connect("http://msutoday.msu.edu/rss/sports/").get();
		Elements titleList = doc.getElementsByTag("title");// ignore the first
		Elements linkList = doc.getElementsByTag("guid");
		Elements dateList = doc.getElementsByTag("pubDate");
		Elements descrList = doc.getElementsByTag("description");// ignore the
																	// first

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

	public void showData(TeamchatAPI api) {
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

			Room R = api.context().byId("566124a730654fd96f9c36a1");
			api.perform(R.post(p2));
		}
		System.out.println("exiting showData");
	}

	// @OnKeyword(isCaseSensitive=false, value="msu")
	public void searchCategories(TeamchatAPI api) throws IOException {
		System.out.println("calling setData");
		setData();
		System.out.println("calling showData");
		showData(api);
	}
}