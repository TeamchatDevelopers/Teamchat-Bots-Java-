package callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Report {

	public static Map<ArrayList<String>,String> report = new HashMap<ArrayList<String>,String>();
	
	public static void insertInReportMap(String email,String type, String value)
	{
		ArrayList<String> details = new ArrayList<String>();
		details.add(email);
		details.add(type);
		
		report.put(details, value);
	}
	
	public static String getReport()
	{
		StringBuilder html = new StringBuilder();
		
		
		html.append("<table style='border: 1px solid black;'>");
		
		html.append("<thead>");
		html.append("<th style='border: 1px solid black;'>" + "Email" + "</th>");
		html.append("<th style='border: 1px solid black;'>" + "Amount Type" + "</th>");
		html.append("<th style='border: 1px solid black;'>" + "Amount" + "</th>");
		html.append("</thead>");
		
		for(ArrayList<String> key : report.keySet())
		{
			html.append("<tr style='border: 1px solid black;'>");
			
			html.append("<td style='border: 1px solid black;'>" + key.get(0) + "</td>");
			html.append("<td style='border: 1px solid black;'>" + key.get(1) + "</td>");
			html.append("<td style='border: 1px solid black;'>" + report.get(key) + "</td>");
			
			html.append("</tr>");
		}
		
		html.append("</table>");
		return html.toString();
	}
	

}
