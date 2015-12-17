//INTEGRATION: SLACK

package slack;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.*;

public class SlackDB {

	static String JDBC_DRIVER = new String();
	static String DB_URL = new String();

	static String USER = new String();
	static String PASS = new String();
	
	public static Properties loadPropertyFromClasspath(String fileName,	Class<?> type) throws IOException 
	{
		Properties prop = new Properties();
		prop.load(type.getClassLoader().getResourceAsStream(fileName));
		return prop;
	}

	public static void initDB() throws IOException
	{
		Properties prop1 = loadPropertyFromClasspath("slack.properties",
				Slack.class);

		//System.out.println("Reading database credentials from properties file.");

		JDBC_DRIVER=prop1.getProperty("JDBC_DRIVER");
		DB_URL = prop1.getProperty("DB_URL");
		USER = prop1.getProperty("USER").trim();
		PASS = prop1.getProperty("PASS");
	}
	
	public static Map<String,String> getTeamNames(String email) throws IOException
	{
		Connection conn = null;
		PreparedStatement stmt = null;
		Map<String,String> result = new HashMap<String,String>();
		initDB();
		
		try 
		{
			Class.forName(JDBC_DRIVER);

			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.prepareStatement("SELECT team_name,team_id FROM Slack WHERE email = ?");

			stmt.setString(1, email);

			ResultSet rs = stmt.executeQuery();
			
			while(rs.next())
			{
				result.put(rs.getString("team_name"), rs.getString("team_id"));
			}
			result.put("Authorize to a New Team", "NOTATEAMID");
			
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
				
		return result;
	}
	

	public static void saveCode(String email, String code) throws IOException 
	{
		Connection conn = null;
		PreparedStatement stmt = null;

		Properties prop1 = loadPropertyFromClasspath("slack.properties",
				Slack.class);

		//System.out.println("Reading database credentials from properties file.");

		JDBC_DRIVER=prop1.getProperty("JDBC_DRIVER");
		DB_URL = prop1.getProperty("DB_URL");
		USER = prop1.getProperty("USER").trim();
		PASS = prop1.getProperty("PASS");

		try 
		{
			Class.forName(JDBC_DRIVER);

			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			//System.out.println("Connected database successfully...");

			stmt = conn.prepareStatement("INSERT INTO SlackCode (email, code) VALUES (?, ?) ON DUPLICATE KEY UPDATE code = ?");

			stmt.setString(1, email);
			stmt.setString(2, code);
			stmt.setString(3, code);
								

			//System.out.println("Inserting records into the table...");
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
	
	public static String getCode(String email) throws IOException 
	{
		Connection conn = null;
		PreparedStatement stmt = null;

		String output = new String();

		Properties prop1 = loadPropertyFromClasspath("slack.properties",
				Slack.class);


		JDBC_DRIVER=prop1.getProperty("JDBC_DRIVER");
		DB_URL = prop1.getProperty("DB_URL");

		USER = prop1.getProperty("USER").trim();
		PASS = prop1.getProperty("PASS");

		try 
		{
			Class.forName(JDBC_DRIVER);

			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.prepareStatement("SELECT code FROM SlackCode WHERE email = ?");

			stmt.setString(1, email);

			ResultSet rs = stmt.executeQuery();

			if(!rs.last())
			{
				//System.err.println("No code found for the given email");
				output = "nocodefound";
				return output;
			}
			else
			{
				rs.first();
				output = rs.getString("code");
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

		//System.out.println("Exiting from getCode function!");
		return output;
	}
	
	public static void saveToken(String email, String token) throws IOException
	{
		saveToken(email, token, "EMPTY", "EMPTY");
	}

	public static void saveToken(String email, String token, String teamid, String teamname) throws IOException 
	{
		Connection conn = null;
		PreparedStatement stmt = null;

		Properties prop1 = loadPropertyFromClasspath("slack.properties",
				Slack.class);

		JDBC_DRIVER=prop1.getProperty("JDBC_DRIVER");
		DB_URL = prop1.getProperty("DB_URL");
		USER = prop1.getProperty("USER").trim();
		PASS = prop1.getProperty("PASS");

		try 
		{
			Class.forName(JDBC_DRIVER);

			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			stmt = conn.prepareStatement("INSERT INTO Slack (email, access_token, team_id, team_name) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE access_token = ?");

			stmt.setString(1, email);					stmt.setString(3, teamid);
			stmt.setString(2, token);					stmt.setString(4, teamname);
			stmt.setString(5, token);
			
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
	
	public static String getToken(String email) throws IOException
	{
		return getToken(email, "EMPTY");
	}

	public static String getToken(String email, String teamid) throws IOException 
	{
		Connection conn = null;
		PreparedStatement stmt = null;

		String output = new String();

		Properties prop1 = loadPropertyFromClasspath("slack.properties",
				Slack.class);


		JDBC_DRIVER=prop1.getProperty("JDBC_DRIVER");
		DB_URL = prop1.getProperty("DB_URL");

		USER = prop1.getProperty("USER").trim();
		PASS = prop1.getProperty("PASS");

		try 
		{
			Class.forName(JDBC_DRIVER);

			conn = DriverManager.getConnection(DB_URL, USER, PASS);


			stmt = conn.prepareStatement("SELECT access_token FROM Slack WHERE email = ? AND team_id = ?");

			stmt.setString(1, email);
			stmt.setString(2, teamid);

			ResultSet rs = stmt.executeQuery();

			if(!rs.last())
			{
				//System.err.println("No token found for the given email");
				output="notokenfound";
				return output;
			}
			else
			{
				rs.first();
				output = rs.getString("access_token");
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

		//System.out.println("Exiting from getToken function!");
		return output;
	}
}
