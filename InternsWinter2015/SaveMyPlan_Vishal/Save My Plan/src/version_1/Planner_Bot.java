package version_1;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.*;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.*;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.lang.Integer;
import java.text.SimpleDateFormat;
import java.text.DateFormatSymbols;


public class Planner_Bot 
{
	public static final String AuthKey = "10a9984386cc4276c2848f351bf913bb";
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/";
	static final String DB_NAME = "smpv1";
	static final String TABLE_NAME = "timedata";

	static final String USER = "root";
	static final String PASS = "12lokvahkoor12";

	public String plan_name;
	public Integer sHour, sMinute, eHour, eMinute, intInMin , noOfInt, noOfDays, day, year;
	//these times here are in 24-hour format

	public static void main(String[] args) 
	{
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
	

	/**
	 * This method posts a form , where users give their plan a Name and a Description.
	 * Replies to the form , will result in posting of another chatlet which displays plan's name and description.
	 * @param api
	 */
		
	
	//@OnKeyword(value = "newplan", isCaseSensitive = false, description = "Creates a chatlet for a new plan, where you give the plan a name and a description. ")
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

	/**
	 * Posts the chatlet for users to give their time preferences.
	 * This method is called when a reply to the "newplan" chatlet is made
	 * @param api
	 */
	
	@OnAlias("create")
	public void ReplyForm(TeamchatAPI api)
	{
		String q = api.context().currentReply().getField("desc");
		plan_name = api.context().currentReply().getField("alias");
		String room = api.context().currentRoom().getId();
		postReplyForm(api, q, plan_name, room);
	}

	/**
	 * Does the main work for the above ReplyForm function
	 */
	
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

	/**
	 * Stores replies to the timing form in a database
	 * @param api
	 */
	
	@OnAlias("Form")
	public void handleFormReply(TeamchatAPI api) 
	{
		String[] Record = new String[5];
		Record[0] = api.context().currentReply().senderEmail();
		Record[1] = api.context().currentReply().getField("date");
		Record[2] = api.context().currentReply().getField("time");	
		Record[3] = api.context().currentRoom().getId();
		Record[4] = api.context().currentChatlet().getMetaInfo();
		
		Integer index = durToInteger(Record[4],Record[1],Record[2]);

		String part[] = Record[4].split(",");
		String parts[] = Record[1].split(", ");
		String plan = part[0];
		Record[1] = formatDate(Record[1]);
		Connection Conn = null;
		PreparedStatement Stmt = null;

		try
		{
			Class.forName(JDBC_DRIVER);

			Conn = DriverManager.getConnection(DB_URL+DB_NAME, USER, PASS);

			Stmt = Conn.prepareStatement("INSERT INTO timedata VALUES (?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE EmailId = ?, Date = ?, Day = ?, "
					+"Time = ?, PlanId = ?, GroupId = ?, TIndex = ?");
				
			Stmt.setString(1, Record[0]);			Stmt.setString(8, Record[0]);
			Stmt.setString(2, Record[1]);			Stmt.setString(9, Record[1]);
			Stmt.setString(3, parts[1]);			Stmt.setString(10, parts[1]);
			Stmt.setString(4, Record[2]);			Stmt.setString(11, Record[2]);
			Stmt.setString(5, plan);				Stmt.setString(12, plan);
			Stmt.setString(6, Record[3]);			Stmt.setString(13, Record[3]);
			Stmt.setInt(7, index);					Stmt.setInt(14, index);	

			Stmt.executeUpdate();

		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(Stmt!=null){Conn.close();}	
			}
			catch(SQLException se)
			{se.printStackTrace();}
			try
			{
				if(Conn!=null){Conn.close();}	
			}
			catch(SQLException se)
			{
				se.printStackTrace();
			}
		}
	}

	/**
	 * This method returns a table, containing the data which is requested by user.
	 * @param param (Used to store the user's query options)
	 * @param api
	 */
	//@OnKeyword(value = "show", isCaseSensitive = false, description = "Creates a chatlet for users to give details of which plan they want to get results of. ")
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
		displayResult(name+"@@"+day,api);
	}
	
	public void displayResult(String param, TeamchatAPI api)
	{
		HTML5Chatlet list = new HTML5Chatlet();
		list.alias("ResultList");
		boolean flagAll = true;
		String parts[] = param.split("@@");

		if(parts[1].equals("All"))
		{
			flagAll = true;
		}
		else
		{
			flagAll = false;
		}
		Connection Conn = null;
		PreparedStatement Stmt = null;
		PreparedStatement Stmt2 = null;
		
		String g_id = api.context().currentRoom().getId();
		String p_id = parts[0];

		try{

			Class.forName(JDBC_DRIVER);

			Conn = DriverManager.getConnection(DB_URL+DB_NAME, USER, PASS);

			Stmt = Conn.prepareStatement("select count(TIndex) as numOfPeople, TIndex, Time, Date from timedata where GroupId = ? AND PlanId = ? AND Day = ?"
					+" group by TIndex order by numOfPeople desc, TIndex LIMIT 10");

			Stmt2 = Conn.prepareStatement("select count(TIndex) as numOfPeople, TIndex, Time, Date from timedata where GroupId = ? AND PlanId = ?"
					+" group by TIndex order by numOfPeople desc, TIndex LIMIT 10");

			ResultSet rs;
			if(!flagAll)
			{
				Stmt.setString(1, g_id);
				Stmt.setString(2, p_id);
				Stmt.setString(3, parts[1]);

				rs = Stmt.executeQuery();
			}
			else
			{
				Stmt2.setString(1, g_id);
				Stmt2.setString(2, p_id);
				
				rs = Stmt2.executeQuery();
			}
		
			if(!rs.last())
			{
				api.performPostInCurrentRoom(new TextChatlet("Your query gave an empty result."));
				return;
			}

			Integer length = rs.getRow();
			String[][] arr = new String[3][length];
			String[] arr2 = new String[length];
			
			Integer i = 0;
			rs.first();

			while(i < length)
			{
				Integer count = rs.getInt("numOfPeople");
				Integer index = rs.getInt("TIndex");
				String y = rs.getString("Time");
				String x  = rs.getString("Date");
				
				arr[0][i] = String.valueOf(count);		
				arr[1][i] = x.substring(0,7)+x.substring(8,11);
				arr[2][i] = y.substring(0,8)+y.charAt(9)+y.substring(11);
				
				arr2[i] = vector2HTML(tableOfUsers(index, g_id, p_id));
				
				rs.next();
				i++;
			}

			String str = array2HTML(arr,arr2);
			list.setQuestionHtml(str);
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(Stmt!=null){Conn.close();}	
			}
			catch(SQLException se)
			{}
			try
			{
				if(Conn!=null){Conn.close();}	
			}
			catch(SQLException se)
			{
				se.printStackTrace();
			}
		}

		api.performPostInCurrentRoom(list);
	}
	

	/**
	 * Returns a map which contains the duration between two date objects.
	 * @param date1
	 * @param date2
	 * @return
	 */
	
	public Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
		long diffInMillies = date2.getTime() - date1.getTime();
		List<TimeUnit> units = new ArrayList<TimeUnit>(EnumSet.allOf(TimeUnit.class));
		Collections.reverse(units);
		Map<TimeUnit,Long> result = new LinkedHashMap<TimeUnit,Long>();
		long milliesRest = diffInMillies;
		for ( TimeUnit unit : units ) {
			long diff = unit.convert(milliesRest,TimeUnit.MILLISECONDS);
			long diffInMilliesForUnit = unit.toMillis(diff);
			milliesRest = milliesRest - diffInMilliesForUnit;
			result.put(unit,diff);
		}
		return result;
	}

	/**
	 * Uses diffCompute function to find interval between two dates(stored in strings first) in MINUTES and assigns an index to it.
	 * @param meta (Contains date1)
	 * @param date (Contains date2)
	 * @param time (Contains date2)
	 * @return
	 */
	
	public Integer durToInteger(String meta, String date, String time)
	{
		Integer index =  0;
		Long y;	
		Integer hh,mm,day,year,mo,interInMinutes;
		String part[] = meta.split(",");

		hh = Integer.valueOf(part[1]);
		mm = Integer.valueOf(part[2]);
		day = Integer.valueOf(part[3]);
		year = Integer.valueOf(part[4]);
		interInMinutes = Integer.valueOf(part[5]);

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY,hh);
		cal.set(Calendar.MINUTE,mm);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.DAY_OF_YEAR,day);
		cal.set(Calendar.YEAR,year);

		Date d1 = cal.getTime();

		Date t = new Date();
		SimpleDateFormat tf = new SimpleDateFormat("hh:mm a");
		SimpleDateFormat pf = new SimpleDateFormat("HH:mm");
		try{t = tf.parse(time);}
		catch(java.text.ParseException e){e.printStackTrace();}
		String[] strs = pf.format(t).split(":");

		hh = Integer.valueOf(strs[0]);
		mm = Integer.valueOf(strs[1]);

		String[] spl = date.split(", ");
		String[] spl2 = spl[0].split(" ");

		day = Integer.valueOf(spl2[0]);
		mo = Integer.valueOf(spl2[1]);
		year = Integer.valueOf(spl2[2]);
		Calendar cal2 = Calendar.getInstance();		

		cal2.set(Calendar.HOUR_OF_DAY,hh);
		cal2.set(Calendar.MINUTE,mm);
		cal2.set(Calendar.SECOND,0);
		cal2.set(Calendar.DATE,day);
		cal2.set(Calendar.MONTH,mo-1);
		cal2.set(Calendar.YEAR,year);

		Date d2 = cal2.getTime();

		date = spl2[0] + " " + new DateFormatSymbols().getMonths()[mo-1] + ", " + spl[1];
		Map<TimeUnit,Long> DiffMap = computeDiff(d1,d2);

		y = DiffMap.get(TimeUnit.DAYS)*24*60 + DiffMap.get(TimeUnit.HOURS)*60 + DiffMap.get(TimeUnit.MINUTES) ;
		y = y/interInMinutes + 1;
		index = (int) (long) y;
		return index;
	}

	/**
	 * Just a date formatting function
	 * @param date
	 * @return
	 */
	public String formatDate(String date)
	{
		String[] spl = date.split(", ");
		String[] spl2 = spl[0].split(" ");
		String x = new DateFormatSymbols().getMonths()[Integer.valueOf(spl2[1])-1];
		String str = spl2[0] + " " + x.substring(0, 3) + ", " + spl[1];
		return str;
	}
	
	public Vector<String> tableOfUsers(Integer index , String g_id, String p_id)
	{
		Connection Conn = null;
		PreparedStatement Stmt = null;
		Vector<String> names = new Vector<String>();
		
		try{

			Class.forName(JDBC_DRIVER);

			Conn = DriverManager.getConnection(DB_URL+DB_NAME, USER, PASS);

			Stmt = Conn.prepareStatement("select EmailId from timedata where GroupId = ? AND PlanId = ? AND TIndex = ? ");
			
			Stmt.setString(1, g_id);
			Stmt.setString(2, p_id);
			Stmt.setInt(3, index);

			ResultSet rs = Stmt.executeQuery();
			
			rs.last();
			
			Integer length = rs.getRow();
			Integer i = 0;
			rs.first();
			
			while(i < length)
			{
				names.addElement(rs.getString("EmailId"));
				i++;
				rs.next();
			}
			
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(Stmt!=null){Conn.close();}	
			}
			catch(SQLException se)
			{}
			try
			{
				if(Conn!=null){Conn.close();}	
			}
			catch(SQLException se)
			{
				se.printStackTrace();
			}
		}
		
		return names;
	}
	
	public String vector2HTML(Vector<String> names)
	{
		StringBuilder html = new StringBuilder("<table class = 'hid'>");
		
		for(Integer i = 0; i < names.size(); i++)
		{
			html.append("<tr>");
				html.append("<td>" + names.get(i) + "</td>");
			html.append("</tr>");
		}
		
		html.append("</table>");
		
		return html.toString();
		
	}
	 
	/**
	 * Helper function to create a HTML table string to be used in setQuestionHtml method
	 * @param array
	 * @return
	 */
	public String array2HTML(String[][] array, String[] arrNames)
	{
		StringBuilder html = new StringBuilder("<table>");
		html.append("<thead>");
		html.append("<th>" + "Count" + "</th>");
		html.append("<th>" + "Day " + "</th>");
		html.append("<th>" + "Time " + "</th>");
		html.append("</thead>");

		for(int i = 0; i < array[0].length; i++)
		{
			html.append("<tr>");
			for(int j = 0 ; j < array.length ; j++)
			{
				if(j==0)
				{
					html.append("<td>");
						html.append("<label class='btn' >");
						html.append(array[j][i]+"</label>");
						html.append(arrNames[i]);
					html.append("</td>");
				}
				else
				{
					html.append("<td>" + array[j][i] + "</td>");
				}
			}
			html.append("</tr>");
		}
		
		html.append("</table>");
		String str = html.toString();
		
		/********************************/ 
		// Adding CSS
		
		str = "   <style>  "  + 
				 "   @import url(http://fonts.googleapis.com/css?family=Open+Sans);  "  + 
				 "   * {  "  + 
				 "     box-sizing: border-box;  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   table {  "  + 
				 "     border-collapse: separate;  "  + 
				 "     background: #fff;  "  + 
				 "     border-radius: 5px;  "  + 
				 "     margin: 50px auto;  "  + 
				 "     box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   thead {  "  + 
				 "     border-radius: 5px;  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   thead th {  "  + 
				 "     font-family: 'Segoe UI';  "  + 
				 "     font-size: 16px;  "  + 
				 "     font-weight: 400;  "  + 
				 "     color: #ffffff;  "  + 
				 "     text-shadow: 0px 2px 2px rgba(255, 255, 255, 0.3);  "  + 
				 "     text-align: center;  "  + 
				 "     padding: 20px;  "  + 
				 "     background-size: 100%;  "  + 
				 "     background-image: linear-gradient(#159CEB,#3199FA);  "  + 
				 "     background-color: rgb(77, 255, 77);  "  + 
				 "     border-top: 1px solid #858d99;  "  + 
				 "   }  "  + 
				 "   thead th:first-child {  "  + 
				 "     border-top-left-radius: 5px;  "  + 
				 "   }  "  + 
				 "   thead th:last-child {  "  + 
				 "     border-top-right-radius: 5px;  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   tbody tr td {  "  + 
				 "     font-family: 'Open Sans', sans-serif;  "  + 
				 "     font-weight: 400;  "  + 
				 "     color: #5f6062;  "  + 
				 "     font-size: 13px;  "  + 
				 "     padding: 20px 20px 20px 20px;  "  + 
				 "     border-bottom: 1px solid #e0e0e0;  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   tbody tr:nth-child(2n) {  "  + 
				 "     background: #EBEBEB;  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   tbody tr:last-child td {  "  + 
				 "     border-bottom: none;  "  + 
				 "   }  "  + 
				 "   tbody tr:last-child td:first-child {  "  + 
				 "     border-bottom-left-radius: 5px;  "  + 
				 "   }  "  + 
				 "   tbody tr:last-child td:last-child {  "  + 
				 "     border-bottom-right-radius: 5px;  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   tbody:hover > tr td {  "  + 
				 "     filter: progid:DXImageTransform.Microsoft.Alpha(Opacity=50);  "  + 
				 "     opacity: 0.5;  "  + 
				 "     @include text-shadow(0px 0px 2px rgba(0,0,0,0.8));*/  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   tbody:hover > tr:hover td {  "  + 
				 "     text-shadow: none;  "  + 
				 "     color: #2d2d2d;  "  + 
				 "     filter: progid:DXImageTransform.Microsoft.Alpha(enabled=false);  "  + 
				 "     opacity: 1;  "  + 
				 "   }  "  +  
				 "   .hid {  "  + 
				 "       display: none;  "  + 
				 "   }  "  + 
				 "     "  + 
				 "   .btn:active + table{  "  + 
				 "       display : block;  "  + 
				 "  }  " +
				 "  </style>  "+ str;
		
		return str;
	}
}