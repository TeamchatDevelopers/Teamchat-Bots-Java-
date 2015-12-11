package mybot;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
//import com.teamchat.client.annotations.OnMemberAdded;
import com.teamchat.client.sdk.Expirable;
import com.teamchat.client.sdk.Field;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.HTML5Chatlet;
//import com.teamchat.client.sdk.chatlets.PollChatlet;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.TakeActionChatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;
import java.sql.*;
import java.text.SimpleDateFormat;

//import java.util.*;
import java.util.Date;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;

public class Mybot{
	private static final String authKey = "ebcbd25ac91548b0c85b20a2a80eb03b";
	private static final String sqlclass  = "com.mysql.jdbc.Driver";
	private static String sqlconnect = "jdbc:mysql://localhost/food";
	private String username = "root";
	private String password = "ucantcrack@12";
	public String time;
	private String date;
	private String roomid = "5661a66283f717b578e16fb3";
	private Scheduler sched;
	
	public static void main(String[] args) {
		
		TeamchatAPI api = TeamchatAPI.fromFile("config.json").setAuthenticationKey(authKey);
		api.startReceivingEvents(new Mybot());
		
		
	}
	@OnKeyword(value="help")
	public void help(TeamchatAPI api){
		TakeActionChatlet chatlet = new TakeActionChatlet();
		chatlet.setActionLabel("Hostel Menu").alias("Hostelmenu");
		chatlet.setActionLabel("Menu For All Hostels").alias("Allhostel");
		chatlet.setActionLabel("Rating of Hostel Mess").alias("Rating");
		chatlet.setActionLabel("Notifications").alias("notify");
		chatlet.setActionLabel("Off Notifications").alias("offnotify");
		api.performPostInCurrentRoom(chatlet);
	}
	
	@OnKeyword(value = "Hi")
	public void hello(TeamchatAPI api){
		api.perform( api.context().currentRoom().post(new TextChatlet("Hello") ));
		
	}
	
	@OnKeyword("start")
	public void setdatabase(TeamchatAPI api){
		
		
		PrimaryChatlet chatlet = new PrimaryChatlet();
		chatlet.setQuestion("Please reply Your Institute:");
		
		Form form = api.objects().form();
		Field field = api.objects().select().name("instituten").label("Institutename");
		try{
			Class.forName(sqlclass);
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/institutename",username,password);
			Statement stmt = con.createStatement();
			String sql = "select *from instituten";
			ResultSet rs = stmt.executeQuery(sql);
			
			while(rs.next()){
				field.addOption(rs.getString(1));
			}
			
			con.close();
			form.addField(field);
			chatlet.setReplyScreen(form);
			chatlet.alias("connectto");
			
			api.performPostInCurrentRoom(chatlet);
			
		}catch(Exception e){
			api.performPostInCurrentRoom(new TextChatlet("Sorry Something Went Wrong."));
		}
		
		
	}
	
	@OnAlias("connectto")
	public void sqldata(TeamchatAPI api){
		
		String database = api.context().currentReply().getField("instituten").toLowerCase().replaceAll("\\s", "");
		try{
			
			Class.forName(sqlclass);
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/institutename",username,password);
			Statement stmt = con.createStatement();
			String userid = api.context().currentReply().senderEmail();
			String sql = "replace into u_name values("+ '"'+ userid +'"'+','+ '"'+database+'"' + ")";
			stmt.executeUpdate(sql);
			
			
			
			con.close();
		}catch(Exception e){;}
		
		
	}
	
	//Notifications
	
	@OnAlias(value = "Notify")
	public void notify(TeamchatAPI api){

		// Starting Quartz scheduler
		try{
			SchedulerFactory sf = new StdSchedulerFactory();
			sched= sf.getScheduler();
			sched.start();
			
			//JOB
			JobDetail job = newJob(Schedule.class).withIdentity("menu","group1").build();
			job.getJobDataMap().put("API", api);
			
			//Trigger
			CronTrigger trigger = newTrigger().withIdentity("Trigger","group1").withSchedule(cronSchedule("0 0/240 6-18 * * ?")).build();
			
			sched.scheduleJob(job,trigger);
			api.performPostInCurrentRoom(new TextChatlet("Notifications On."));
		
		}catch(Exception e){api.performPostInCurrentRoom(new TextChatlet("Notifications are already On."));}
				
	}
	
	// off notifications
	@OnAlias(value = "Offnotify")
	public void notifoff(TeamchatAPI api){
		try {
			if(sched!=null){
			sched.shutdown();
			api.performPostInCurrentRoom(new TextChatlet("Notifiations off."));
			}
		} catch (SchedulerException e) {
			api.performPostInCurrentRoom(new TextChatlet("Notifiations are already off."));
		}
	}
	
	
	// Mess Menu setup for mess managers
	
	@OnAlias(value = "Mess Menu")
	public void form(TeamchatAPI api){
		//Getting the time of day
		
		if(roomid.equals(api.context().currentRoom().getId())){
			time = null;
//			String timeStamp = new SimpleDateFormat("HHmm").format(new Date());
//			int t = Integer.parseInt(timeStamp);
			date = new SimpleDateFormat("dd/MM").format(new Date());
			
//			if(t>1000 && t < 930){
//				time = "BreakFast";
//			}
//			else if(t>= 930 && t < 1400){
//				time = "Lunch";
//			}
//			else if(t>=1400 && t<1800){
//				time = "Snacks";
//			}
//			else {
//				time = "Dinner";
//			}
			
			
			PrimaryChatlet chatlet = new PrimaryChatlet();
			chatlet.setQuestion(date + " :Reply the Mess Menu");
			chatlet.showDetails(true);
			chatlet.allowComments(true);
			
			Form form = api.objects().form();
			
			Field field = api.objects().select().label("Hostel").name("hostel");
			int i = 1;
			while(i<=16){
				field.addOption("Hostel_" + i);
				i=i+1;
			}
			form.addField(field);
			form.addField(api.objects().select().label("Time").name("time").addOption("Breakfast").addOption("Lunch").addOption("Snacks").addOption("Dinner"));
			form.addField(api.objects().input().label("Food 1:").name("f_one"));
			form.addField(api.objects().input().label("Food 2:").name("f_two"));
			form.addField(api.objects().input().label("Food 3:").name("f_three"));
			form.addField(api.objects().input().label("Food 4:").name("f_four"));
			form.addField(api.objects().input().label("Food 5:").name("f_five"));
			
			chatlet.setReplyScreen(form);
			
			chatlet.alias("getdata");
			api.performPostInCurrentRoom(chatlet);
		}
		else {
			api.performPostInCurrentRoom(new TextChatlet("You dont have permission to access the form."));
		}
}
	
	// get the mess menu and save in database
	@OnAlias("getdata")
	public void onData(TeamchatAPI api){
		
		String hostel = api.context().currentReply().getField("hostel");
		String food1 = api.context().currentReply().getField("f_one");
		String food2 = api.context().currentReply().getField("f_two");
		String food3 = api.context().currentReply().getField("f_three");
		String food4 = api.context().currentReply().getField("f_four");
		String food5 = api.context().currentReply().getField("f_five");
		String time = api.context().currentReply().getField("time");
		int rate = 0;
		
		String datab = "replace into " + hostel + " values("+ '"' + date+'"'+ ","+'"' + time+ '"'+ ", " +'"' + food1 + '"' + "," +'"' +food2+'"' + ","+ '"'+food3+'"' + ","+'"'+food4+'"' + ","+'"'+food5+'"'+","+0+","+rate+")";
		
		try{
			
			Class.forName(sqlclass);
			Connection con = DriverManager.getConnection(sqlconnect,username,password);
			
			Statement stmt = con.createStatement();
			stmt.executeUpdate(datab);
			api.performPostInCurrentRoom(new TextChatlet("Menu Updated."));
			con.close();
		}catch(Exception e){ System.out.println(e);}
		
		
	}
	
	
	//Specific Hostel Keywords
	
	@OnAlias(value = "Hostelmenu" )
	public void menuh(TeamchatAPI api)
	{
		
		PrimaryChatlet chatlet = new PrimaryChatlet();
		chatlet.setQuestion("Reply the date and time for menu");
		chatlet.showDetails(true);
		chatlet.allowComments(true);
		
		Form form = api.objects().form();
		form.addField(api.objects().input().value("dd/mm").label("Date").name("date"));
		form.addField(api.objects().select().addOption("Breakfast").addOption("Lunch").addOption("Snacks").addOption("Dinner").label("Time").name("time"));
		
		Field field = api.objects().select().label("Hostel").name("hostel");
		int i = 1;
		while(i<=16){
			field.addOption("Hostel_" + i);
			i=i+1;
		}
		form.addField(field);
		chatlet.setReplyScreen(form);
		
		chatlet.alias("getmenu");
		api.performPostInCurrentRoom(chatlet);
		
	}
	
	// Get the menu of specified hostel
	@OnAlias("getmenu")
	public void menu(TeamchatAPI api ){
		
		String hname = api.context().currentReply().getField("hostel");
		String dat = api.context().currentReply().getField("date");
		String tim = api.context().currentReply().getField("time");
				
		try{
			//Connecting to Database
			Class.forName(sqlclass);
			Connection con = DriverManager.getConnection(sqlconnect,username,password);
			
			
			//Quering Database
			
			String getdata = "select *from "+ hname +" where date = " + '"'+dat+ '"'+ " and time = "+'"'+ tim +'"';
			
			Statement stmt = con.createStatement();
			
			
			ResultSet rs = stmt.executeQuery(getdata);
			
			if(rs.next()){
				
				//PrimaryChatlet
				
				Form form = api.objects().form();
				//Rate Option
				
				Field field1 = api.objects().select().label("Rate").name("rate");
				int i1 = 1;
				
				while(i1<=10){
					field1.addOption(""+i1);
					i1=i1+1;
				}
				form.addField(field1);
				
				String htmlconvert= "<style>table,td,th{border: 1px solid green;} th {background-color: green;color: white;}</style><table> <tr><th>" + hname + "</th></tr><tr><td>"+ rs.getString(3)+ "</tr><tr><td>" + rs.getString(4) + "</tr><tr><td>" + rs.getString(5) + "</tr><tr><td>" + rs.getString(6)+"</tr><tr><td>" + rs.getString(7) + "</tr></table><br> Reply to Rate" ;
				PrimaryChatlet chatlet = new PrimaryChatlet();
				
				chatlet.setQuestionHtml(htmlconvert);
				String metadata= hname + ","+dat + ","+tim;
				chatlet.setMetaInfo(metadata);
				chatlet.setReplyScreen(form);
				
				
				chatlet.alias("saverate");
				api.perform(api.context().currentRoom().post(chatlet));
				
				}
			else{
				api.perform(api.context().currentRoom().post(new TextChatlet("Mess Menu not available for this Hostel")));
			}
			con.close();
			
		}catch(Exception e){System.out.println(e);}
	}
		
	@OnAlias(value = "saverate")
	public void rate(TeamchatAPI api){
		
		try {
			//connecting to database
			Class.forName(sqlclass);
			Connection con = DriverManager.getConnection(sqlconnect,username,password);
			Statement stmt = con.createStatement();
			
			String metadata = api.context().currentChatlet().getMetaInfo();
			String[] meta = metadata.split(",");
			String rate = api.context().currentReply().getField("rate");
			
			
			String sql = "select *from ratet where hostel = "+ '"'+ meta[0] +'"';
			
			String sql2 = "select *from "+ meta[0]+ " where date = " + '"'+meta[1]+'"' + " and time = " + '"'+ meta[2]+'"';
			
			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			int num = rs.getInt(2);
			
			//updating rates
			Float nrate = (rs.getFloat(3)*num + Integer.parseInt(rate))/(num+1);
			String update = "replace into ratet values(" + '"'+meta[0]+'"'+ ","+ (num+1) +","+nrate+ ")";
			stmt.execute(update);
			
			ResultSet rs2 = stmt.executeQuery(sql2);
			rs2.next();
			int num2 = rs2.getInt(8);
			Float rate2 = (rs2.getFloat(9)*num2 + Integer.parseInt(rate))/(num2+1);;
			String update2 = "update " + meta[0] + " set num = " + (num2+1) + ", rate = " + rate2 + " where date = " + '"'+ meta[1]+'"' + " and time = "+ '"'+ meta[2]+'"';
			
			
			stmt.execute(update2);
			
			Expirable c= (Expirable)api.context().currentChatlet();
			api.perform (api.context().currentRoom().reply(c.expireNow()));
			
			con.close();
			
		} catch (Exception e) {
			
			api.performPostInCurrentRoom(new TextChatlet("Sorry. Something Happened."));
			e.printStackTrace();
		}
		

	}
	
	
	// All hostel menu
	@OnAlias(value = "Allhostel")
	public void allhostel(TeamchatAPI api){
		PrimaryChatlet chatlet = new PrimaryChatlet();
		chatlet.setQuestion("Date and time of Menu:");
		chatlet.showDetails(true);
		chatlet.allowComments(true);
		
		Form form = api.objects().form();
		form.addField(api.objects().input().value("dd/mm").label("Date").name("date"));
		form.addField(api.objects().select().addOption("Breakfast").addOption("Lunch").addOption("Snacks").addOption("Dinner").label("Time").name("time"));
		
		chatlet.setReplyScreen(form);
		
		chatlet.alias("all");
		api.performPostInCurrentRoom(chatlet);
		
	}
	
	// For All Hostels
	@OnAlias("all")
	public void allmenu(TeamchatAPI api){
		try{
			Class.forName(sqlclass);
			Connection con = DriverManager.getConnection(sqlconnect,username,password);
			
			Statement stmt = con.createStatement();
			String tim = api.context().currentReply().getField("time");
			String dat = api.context().currentReply().getField("date");
			
			
			//Query the database
			int i =1;
			String htmlconvert = "<style>table,td,th{border: 1px solid black;} th {background-color: black;color: white;}</style><table>";
			htmlconvert = htmlconvert + "<tr><th>Hostel Name</th><th>Item 1</th><th>Item 2</th><th>Item 3</th><th>Item 4</th><th>Item 5</th><th>Rating</th>";
			
			while(i<=16){
				ResultSet rs = stmt.executeQuery("select *from Hostel_"+i+ " where date = " + '"'+dat+ '"'+ " and time = "+'"'+ tim +'"');
				
				if(rs.next()){
					
					// Designing data of all hostels
					htmlconvert= htmlconvert + " <tr><td>" + "Hostel "+ i + "</td><td>"+ rs.getString(3)+ "</td><td>" + rs.getString(4) + "</td><td>" + rs.getString(5) + "</td><td>" + rs.getString(6)+"</td><td>" + rs.getString(7) + "</td><td>"+ rs.getString(9) + "</td></tr>" ;
			
					}
					else {
						htmlconvert = htmlconvert + "<tr><td>Hostel "+i+ "</td><td>" + "Mess Menu Not Available </td></tr>";
					}
				i=i+1;
				
			}
			//posting the data
			htmlconvert = htmlconvert + "</table>";
			api.perform(api.context().currentRoom().post(new HTML5Chatlet().setQuestionHtml(htmlconvert)));
			
		}catch(Exception e){System.out.println(e);}
		
	}
	
// Rating of hostels
	@OnKeyword(value = "Rating",description = "Displays the rating of all hostels over the specified period")
	public void rated(TeamchatAPI api){
		
		PrimaryChatlet chatlet = new PrimaryChatlet();
		chatlet.setQuestion("Rating Period");
		chatlet.showDetails(true);
		chatlet.allowComments(true);
		
		Form form = api.objects().form();
		form.addField(api.objects().select().addOption("Over All").addOption("Today").label("Duration").name("duration"));
		
		chatlet.setReplyScreen(form);
		
		chatlet.alias("report");
		api.performPostInCurrentRoom(chatlet);
	
	}
	
	// rating table overall or today
	@OnAlias("report")
	public void report(TeamchatAPI api){
		String period = api.context().currentReply().getField("duration");
		try{
			Class.forName(sqlclass);
			Connection con = DriverManager.getConnection(sqlconnect,username,password);
			
			Statement stmt = con.createStatement();
			
			
			if(period.equals("Over All")){
				
				String sorted = "select *from ratet ORDER BY rate DESC , hostel ASC";
				String htmlconvert = "<style>table,td,th{border: 1px solid black;} th {background-color: black;color: white;}</style><table>";
				htmlconvert = htmlconvert + "<tr><th>Hostel Name</th><th>Rating</th>";
				ResultSet rs = stmt.executeQuery(sorted);
				
				while(rs.next()){
					htmlconvert = htmlconvert + "<tr><td>" + rs.getString(1) + "</td><td>" + rs.getInt(3)+"</td></tr>";
				}
				api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(htmlconvert));
				
			}
			else{
				
				date = new SimpleDateFormat("dd/MM").format(new Date());
				int i =1;
				String htmlconvert = "<style>table,td,th{border: 1px solid black;} th {background-color: black;color: white;}</style><table>";
				htmlconvert = htmlconvert + "<tr><th>Hostel Name</th><th>Time</th><th>Rating</th>";
				while(i<=16){
					
					String sql = "select *from Hostel_" + i + " where date = "+'"' + date + '"';
					ResultSet rs = stmt.executeQuery(sql);
					while(rs.next()){
					htmlconvert = htmlconvert + "<tr><td>Hostel "+i+ "</td><td>"+ rs.getString(2) + "</td><td>"+ rs.getString(9) + "</td></tr>";
					}
					
					i=i+1;
				}
				api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(htmlconvert));
			}
		}catch(Exception e){api.performPostInCurrentRoom(new TextChatlet("Sorry the operation cant be performed now"));}
	}

	
	
}
