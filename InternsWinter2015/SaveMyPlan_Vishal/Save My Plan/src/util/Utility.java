package util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

import java.util.*;
import java.util.concurrent.TimeUnit;

import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.HTML5Chatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;

public class Utility {

	static String JDBC_DRIVER ;
	static String DB_URL ;
	static String DB_NAME ;
	static String TABLE_NAME ;
	static String USER ;
	static String PASS ;

	public static Properties config;

	static
	{
		try
		{
			config = loadPropertyFromClasspath("config.properties", Utility.class);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static void initDB()
	{
		JDBC_DRIVER = config.getProperty("JDBC_DRIVER");  
		DB_URL = config.getProperty("DB_URL"); 
		DB_NAME = config.getProperty("DB_NAME"); 
		TABLE_NAME = config.getProperty("TABLE_NAME"); 
		USER = config.getProperty("USER"); 
		PASS = config.getProperty("PASS"); 
	}

	public static void cleanDB()
	{
		initDB();
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try
		{
			Class.forName(JDBC_DRIVER);
			
			conn = DriverManager.getConnection(DB_URL+DB_NAME, USER, PASS);
			
			stmt = conn.prepareStatement("DELETE FROM timedata WHERE NOW() >= ADDDATE(sqlDate, INTERVAL 7 DAY)");
			
			stmt.executeUpdate();
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
				if(stmt!=null){conn.close();}	
			}
			catch(SQLException se)
			{se.printStackTrace();}
			try
			{
				if(conn!=null){conn.close();}	
			}
			catch(SQLException se)
			{
				se.printStackTrace();
			}
		}
		
	}
	
	public static Map<TimeUnit,Long> computeDiff(Date date1, Date date2) {
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

	public static String formatDate(String date)
	{
		String[] spl = date.split(", ");
		String[] spl2 = spl[0].split(" ");
		String x = new DateFormatSymbols().getMonths()[Integer.valueOf(spl2[1])-1];
		String str = spl2[0] + " " + x.substring(0, 3) + ", " + spl[1];
		return str;
	}

	public static Integer durToInteger(String meta, String date, String time)
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
		Map<TimeUnit,Long> DiffMap = Utility.computeDiff(d1,d2);

		y = DiffMap.get(TimeUnit.DAYS)*24*60 + DiffMap.get(TimeUnit.HOURS)*60 + DiffMap.get(TimeUnit.MINUTES) ;
		y = y/interInMinutes + 1;
		index = (int) (long) y;
		return index;

	}

	public static void insertReply(String email,String date,String day,String time,String planid,String groupid,Integer tIndex)
	{
		initDB();
		//cleanDB();
		
		Connection Conn = null;
		PreparedStatement Stmt = null;

		java.sql.Date sqlDate = new java.sql.Date(new java.util.Date().getTime());
		
		try
		{
			Class.forName(JDBC_DRIVER);

			Conn = DriverManager.getConnection(DB_URL+DB_NAME, USER, PASS);

			Stmt = Conn.prepareStatement("INSERT INTO timedata VALUES (?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE EmailId = ?, Date = ?, Day = ?, "
					+"Time = ?, PlanId = ?, GroupId = ?, TIndex = ?, sqlDate = ?");

			Stmt.setString(1, email);			Stmt.setString(9, email);
			Stmt.setString(2, date);			Stmt.setString(10, date);
			Stmt.setString(3, day);			Stmt.setString(11, day);
			Stmt.setString(4, time);			Stmt.setString(12, time);
			Stmt.setString(5, planid);				Stmt.setString(13, planid);
			Stmt.setString(6, groupid);			Stmt.setString(14, groupid);
			Stmt.setInt(7, tIndex);					Stmt.setInt(15, tIndex);	

			Stmt.setDate(8, sqlDate);			Stmt.setDate(16, sqlDate);
			
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

	public static void displayResult(String param, TeamchatAPI api)
	{
		initDB();
		//cleanDB();
		
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

	public static Vector<String> tableOfUsers(Integer index , String g_id, String p_id)
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

	public static String vector2HTML(Vector<String> names)
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

	static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public static String array2HTML(String[][] array, String[] arrNames) throws IOException
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

		str = readFile("tablecss.css",Charset.defaultCharset()) + str; 

		return str;
	}

	public static Properties loadPropertyFromClasspath(String fileName, Class<?> type) throws IOException
	{
		Properties prop = new Properties();
		prop.load(type.getClassLoader().getResourceAsStream(fileName));
		return prop;
	}

}
