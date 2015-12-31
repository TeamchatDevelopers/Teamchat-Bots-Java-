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
		StringBuilder html = new StringBuilder("<table>");
		html.append("<thead>");
		html.append("<th>" + "Email" + "</th>");
		html.append("<th>" + "Amount Type" + "</th>");
		html.append("<th>" + "Amount" + "</th>");
		html.append("</thead>");
		
		for(ArrayList<String> key : report.keySet())
		{
			html.append("<tr>");
			
			html.append("<td>" + key.get(0) + "</td>");
			html.append("<td>" + key.get(1) + "</td>");
			html.append("<td>" + report.get(key) + "</td>");
			
			html.append("</tr>");
		}
		
		html.append("</table>");
		return html.toString();
	}
}
