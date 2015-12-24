package mybot;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.TextChatlet;

public class Schedule implements Job{
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		System.out.println("Mess Menu");
		TeamchatAPI api = (TeamchatAPI)dataMap.get("API");
		String timeStamp = new SimpleDateFormat("HHmm").format(new Date());
		int t = Integer.parseInt(timeStamp);
		if(t>1800 && t < 0500){
			api.perform(api.context().currentRoom().post(new TextChatlet("Reminder! Rate Mess Everyday for Improvement")));
		}
		else{
			api.perform(api.context().currentRoom().post(new TextChatlet("Mess Menu")));
		}
		
	}

}
