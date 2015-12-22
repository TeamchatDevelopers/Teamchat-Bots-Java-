package webcrawler;

import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.teamchat.client.sdk.TeamchatAPI;

public class Schedule implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		TeamchatAPI api = (TeamchatAPI)dataMap.get("API");
		webCrawler wc = (webCrawler) dataMap.get("WC");
		try
		{
			wc.showNews(api);
		}
		catch (IOException e)
		{	
			e.printStackTrace();
		}
	}
	
}
