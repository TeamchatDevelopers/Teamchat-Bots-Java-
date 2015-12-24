package asc;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.BypassChatletCopy;
import com.teamchat.client.sdk.chatlets.HTML5Chatlet;


/**
 * Quartz schedular to give output in user specified
 * time interval.
 **/

public class schedule implements Job
{
	
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		TeamchatAPI api = (TeamchatAPI)dataMap.get("API");
		String meta = dataMap.getString("META");
		
		try
		{
			Document doc = Jsoup.connect(meta).get();
		
			Elements el1 = doc.select("div[id^=ssr-matchldate-label]");
			String date = new SimpleDateFormat("dd MMM").format(new Date());
    	
			Elements es1=doc.getElementsByClass("ssr-matchno");
		
		
			Elements es2=doc.getElementsByClass("ssr-tour-venue-name");
		
			Elements es3=doc.getElementsByClass("ssr-cricket-series-fixtures-teamname-div");
		
		
		
			Elements es5=doc.getElementsByClass("ssr-cricket-series-fixtures-status-div");
		
	
			int j=0;
			int count =0;
			for(Element e : el1)
			{
    		
				String[] datef = e.html().split(" - ");
				//System.out.println(datef[0]);
    		
				if(datef.length==1)							//only 1 day in case of 1day and t20 matches
				{
					String[] datefparts = datef[0].split("<");
					datef[0] = datefparts[0];
					//System.out.println(datef[0]);
				}
    		
				DateFormat date1 = new SimpleDateFormat("dd MMM");
				Date date11 = date1.parse(datef[0]);
				Date date33 = date1.parse(date);
    		
				int y=date11.compareTo(date33);
    		
				//System.out.println(y);
				int x=1;
				
				if(datef.length>1)
				{							//span of 5 days in case of test matches
					Date date22 = date1.parse(datef[1]);
					x=date11.compareTo(date33)*date22.compareTo(date33);
				}
    		
    		
				//check if the cricket match is running today
				//both for 1-day/t20(y) as well as test match(x)
				if(x<=0 || y==0)
				{
    			
					String html="";
    			
					//System.out.println(es1.eq(j).html());
					//System.out.println(es2.eq(j).html());
					//System.out.println(es3.eq(j).html());
					//System.out.println(es5.eq(j).html());
    			
					html=html+es1.eq(j).html()+"<br>";
					html=html+es2.eq(j).html()+"<br>";
					html=html+es3.eq(j).html()+"<br>";
					html = html+es5.eq(j).html();
					api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
					count =count+1;
    	    	
				}
    		
    		
    		
				j=j+1;
    		
			}
		}catch(Exception e){}
    		
    		
    }
    	
}


