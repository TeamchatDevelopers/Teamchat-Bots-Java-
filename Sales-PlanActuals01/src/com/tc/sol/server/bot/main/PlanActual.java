package com.tc.sol.server.bot.main;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.tc.sol.util.ZohoConfig;
import com.tc.sol.util.ZohoReportException;
import com.tc.sol.util.ZohoReportUtility;
import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;

public class PlanActual
{

	private static Properties zohoConfigProperties;
	private static ZohoReportUtility zohoUtility;
	private static ZohoConfig zohoConfig;
	private static Logger logger;
	private static String tableName;
	private static String[] columnNames;

	public PlanActual()
	{
		super();
		logger = Logger.getLogger(PlanActual.class.getSimpleName());
		try
		{
			zohoConfigProperties = loadPropertyFromClasspath("zoho-report-config.properties", this.getClass());
			zohoUtility = new ZohoReportUtility(zohoConfigProperties, logger);
			tableName = zohoConfigProperties.getProperty("tableName");
			columnNames = zohoConfigProperties.getProperty("columnNames").split(",");
			zohoConfig = new ZohoConfig(zohoConfigProperties.getProperty("loginEmailId"), zohoConfigProperties.getProperty("authToken"), zohoConfigProperties.getProperty("databaseName"));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@OnKeyword("help")
	public void help(TeamchatAPI api)
	{

		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestionHtml("<center><img src='http://integration.teamchat.com/sol/bot-images/plan-actual.jpg' width='150' /></center><h3 style='color:#159ceb'>Hi I am Sales - Plan vs Actuals Bot </h3><div><p> I help you evaluate your team's performance on a daily basis by capturing their sales plan and comparing it with their actual sales through charts and reports</p></div>"
								+ "<br />"
								+ "<ul type=\"square\"; style=\"color:#359FD8\"; ><li><a1 style=\"color:black\";><b>clearsales - </b></a1><a2 style=\"color:#359FD8\"; align=\"justify\";>Deletes all data</a2></li>"
								+ "<li><a3 style=\"color:black\";><b>REPORT : </b></a3><a4 style=\"color:#359FD8\"; align=\"justify\";>To get a comparitive report and charts of every member plan vs actual</a4></li>"
								+ "<li><a5 style=\"color:black\";><b>PLAN :</b></a5><a6 style=\"color:#359FD8\"; align=\"justify\";>To post the chatlet where your team can fill in the Sales Plan for the day</a6></li>"
								+ "<li><a7 style=\"color:black\";><b>ACTUAL : </b></a7><a8 style=\"color:#359FD8\"; align=\"justify\";>To post the chatlet where your team can fill in the Actual Sales done for the day</a8></li></ul>")));

	}

	@OnKeyword("clearsales")
	public void clearsales(TeamchatAPI api)
	{
		try
		{
			zohoUtility.deleteData(tableName, "roomId = '" + api.context().currentRoom().getId() + "'");
		}
		catch (ZohoReportException e)
		{
			e.printStackTrace();
		}
		String reportHTML = "All data cleared";
		sendHTML(api, reportHTML, api.context().currentRoom().getId());
	}

	@OnKeyword("plan")
	public void plan(TeamchatAPI api)
	{
		Form f = api.objects().form();
		f.addField(api.objects().input().label("Amount").name("plan"));

		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setDetailsLabel("entries").showDetails(true);
		prime.setQuestionHtml("<h5 style='color:#410303'>Please enter your sales plan for today</h5>").setReplyScreen(f).setReplyLabel("Enter").alias("getplan");
		api.perform(api.context().currentRoom().post(prime));
	}

	@OnAlias("getplan")
	public void getplan(TeamchatAPI api) throws IOException
	{

		String plan = api.context().currentReply().getField("plan");
		List<String> dataValues = new ArrayList<String>();
		String username = api.context().currentReply().senderName();
		String roomId = api.context().currentRoom().getId();
		dataValues.add(username);
		dataValues.add(new Date().toString());
		dataValues.add(roomId);
		try
		{
			if (getIfDataExists(username, roomId))
			{
				updateData(new String[]
				{
					zohoConfigProperties.getProperty("planColumn")
				}, Arrays.asList(plan), "username = '" + username + "' and roomId = '" + roomId + "'");
			}
			else
			{
				dataValues.addAll(Arrays.asList(plan));
				dataValues.add("0");
				saveData(columnNames, dataValues);
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished processing plan data");
	}

	@OnKeyword("actual")
	public void actual(TeamchatAPI api)
	{
		Form f = api.objects().form();
		f.addField(api.objects().input().label("Amount").name("actual"));
		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setDetailsLabel("entries").showDetails(true);
		prime.setQuestionHtml("<h5 style='color:#410303'>Please enter your Actual Sales for today</h5>").setReplyScreen(f).setReplyLabel("Enter").alias("getactual");
		api.perform(api.context().currentRoom().post(prime));
	}

	@OnAlias("getactual")
	public void getactual(TeamchatAPI api) throws IOException
	{

		String actual = api.context().currentReply().getField("actual");
		List<String> dataValues = new ArrayList<String>();
		String username = api.context().currentReply().senderName();
		String roomId = api.context().currentRoom().getId();
		dataValues.add(username);
		dataValues.add(new Date().toString());
		dataValues.add(roomId);
		try
		{
			if (getIfDataExists(username, roomId))
			{
				updateData(new String[]
				{
					zohoConfigProperties.getProperty("actualColumn")
				}, Arrays.asList(actual), "username = '" + username + "' and roomId = '" + roomId + "'");
			}
			else
			{
				dataValues.addAll(Arrays.asList(actual));
				dataValues.add("0");
				saveData(columnNames, dataValues);
			}

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finished processing actual data");
	}

	private void sendHTML(TeamchatAPI api, String html, String roomId)
	{
		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setQuestionHtml(html);
		api.perform(api.context().currentRoom().post(prime));
	}

	@OnKeyword("report")
	public void report(TeamchatAPI api)
	{
		String reportHTML = null;
		String roomId = api.context().currentRoom().getId();
		try
		{
			reportHTML = zohoUtility.getReportChartAsHTML("BarChart", "roomId = '" + roomId + "'");
			sendHTML(api, reportHTML, roomId);

			reportHTML = zohoUtility.getReportChartAsHTML("PlanPieChart", "roomId = '" + roomId + "'");
			sendHTML(api, reportHTML, roomId);

			reportHTML = zohoUtility.getReportChartAsHTML("ActualPieChart", "roomId = '" + roomId + "'");
			sendHTML(api, reportHTML, roomId);
		}
		catch (ZohoReportException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean getIfDataExists(String userName, String roomId) throws Exception
	{
		String query = zohoConfigProperties.getProperty("data-exists-query");
		query = String.format(query, userName, roomId);

		String uri = zohoUtility.getZohoURI(zohoConfig.getLoginEmailId(), zohoConfig.getDatabaseName(), tableName);
		logger.info("Read query : " + query);
		ByteArrayOutputStream jsonOutput = new ByteArrayOutputStream();
		zohoConfig.getReportClient().exportDataUsingSQL(uri, "JSON", jsonOutput, query, null);
		JSONObject json = new JSONObject(jsonOutput.toString());
		JSONArray rowValues = json.getJSONObject("response").getJSONObject("result").getJSONArray("rows");
		if (rowValues.length() > 0)
		{
			System.out.println("chart data already exist.");
			return true;
		}
		return false;
	}

	private void saveData(String[] columns, List<String> dataValues) throws ZohoReportException
	{
		zohoUtility.addData(tableName, columns, dataValues);
	}

	private void updateData(String[] columns, List<String> dataValues, String updateCriteria) throws ZohoReportException
	{
		zohoUtility.updateData(tableName, columns, dataValues, updateCriteria);
	}

	public static Properties loadPropertyFromClasspath(String fileName, Class<?> type) throws IOException
	{
		Properties prop = new Properties();
		prop.load(type.getClassLoader().getResourceAsStream(fileName));
		return prop;
	}

}
