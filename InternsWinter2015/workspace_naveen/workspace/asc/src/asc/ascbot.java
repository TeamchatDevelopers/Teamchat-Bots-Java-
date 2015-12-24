package asc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.quartz.*;
import org.quartz.SchedulerFactory;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.CronScheduleBuilder.*;


import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyWordWithParam;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.annotations.Param;
import com.teamchat.client.sdk.Field;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.BypassChatletCopy;
import com.teamchat.client.sdk.chatlets.HTML5Chatlet;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;


import org.quartz.impl.StdSchedulerFactory;

public class ascbot {
    public static final String authKey = "89d1b7534d6644e3c459a3f033f4ac6b";

    /**
     * This method creates instance of teamchat client, login using specified 
     * email/password and wait for messages from other users
     **/
    public static void main(String[] args) {
        TeamchatAPI api = TeamchatAPI.fromFile("config.json")
                .setAuthenticationKey(authKey);

        api.startReceivingEvents(new ascbot());
    }

    /**
     * This method posts Hello World message, when any user posts hi message to
     * the logged in user (see main method)
     **/
    @OnKeyword("hi")
    public void hello(TeamchatAPI api) 
    {   
        api.perform( 
                api.context().currentRoom().post(
                        new TextChatlet("Hello World")
                        )
                );
    }
    
    
    /**
     * This method is called when the user select movie keyword.
     * This return a form in which user can select the cities 
     * from the drop down list provided along with.
     **/
    
    /**
     * This keyword crawls the
     */
    
    @OnKeyword(value="movie")
    public void book (TeamchatAPI api) throws IOException
    {
    
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	chatlet.setQuestion("Chosse Your City From The List");
    	chatlet.showDetails(true);
    	chatlet.allowComments(false);
    	chatlet.setReplyLabel("Select City");
    	
    	Form form = api.objects().form();
    	form.addField(api.objects().select().addOption("Mumbai").addOption("Kolkata").addOption("Pune").addOption("Chennai").addOption("Hyderabad").addOption("Bengaluru").addOption("national-capital-region-ncr").label("Enter city").name("city"));
    
    	//form.addField(api.objects().select().addOption("Hindi").addOption("English").label("Language").name("language"));
  
    	chatlet.setReplyScreen(form);
    	chatlet.alias("getdata");
    	api.performPostInCurrentRoom(chatlet);

    }
    
    
    /**
     * This method is called after the user reply with the city in 
     * the previous form.This gives a drop down form of the current
     * movie showing in that city.The user can select a movie from 
     * the provided drop down list.
     **/
    
    @OnAlias(value="getdata")
    public void getdata(TeamchatAPI api) throws IOException
    {
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	chatlet.setQuestion("Choose Movie From The Movie List");
    	chatlet.showDetails(true);
    	chatlet.allowComments(false);
    	chatlet.setReplyLabel("Select Movie");
    	
    	Form form = api.objects().form();
    	Field field = api.objects().select().label("Movie").name("movie_name");
    
    	
    	
    	String city =api.context().currentReply().getField("city");
    	//String language = api.context().currentReply().getField("language");
    	
    	String movie_array="";
    	String href_array="";
    	int i=1;
    	Document doc = Jsoup.connect("http://in.bookmyshow.com/"+city+"/movies").get();
    	
    	Elements es = doc.getElementsByClass("__movie-name");
    	for (Element inputElement : es) 
    	{  
    	    String movie = inputElement.attr("title");
    	    String href = inputElement.attr("href");
    		//String l=inputElement.html();
    		
    	    
    	    field.addOption(i+"."+movie);
    	    
    	    if(movie_array.equals("")){movie_array=movie;}
    	    else{movie_array=movie_array+","+movie;}
    	    
    	    if(href_array.equals("")){href_array=href;}
    	    else{href_array=href_array+","+href;}
    	    
    	    i=i+1;
    	   // System.out.println("Param name: "+l);  
    	} 
    	
    	i=1;
        
    	/*
        String keywords = doc.select("meta[name=keywords]").first().attr("content");  
        System.out.println("Meta keyword : " + keywords);  
        String description = doc.select("meta[name=description]").get(0).attr("content");  
        System.out.println("Meta description : " + description);  
        */
        
    	form.addField(field);
    	
    	chatlet.setReplyScreen(form);
    	chatlet.setMetaInfo(href_array);
    	chatlet.alias("result");
    	api.performPostInCurrentRoom(chatlet);

    	

    	
    }
    
    
    /**
     * This method is called after the user select the city name
     * and the movie name.This post a html chatlet in the group
     * which shows the movie poster, available language, its rating,
     * director, musician, writer name along with some of the lead
     * cast actors.This chatlet also shows a breif synopsis of the
     * movie.
     **/
    
    @OnAlias(value="result")
    public void result(TeamchatAPI api) throws IOException
    {
    	String metastring = api.context().currentChatlet().getMetaInfo();
    	
    	String[] parts=metastring.split(",");
    	
    	String movie_name = api.context().currentReply().getField("movie_name");
    	
    	//System.out.println(movie_name);
    	String[] parts_movie = movie_name.split("\\.");
    	
    	String movie = parts_movie[0];
    	//System.out.println(movie);
    	int m_index = Integer.parseInt(movie);
    	
    	String href=parts[m_index-1];
    	
    	Document doc = Jsoup.connect("http://in.bookmyshow.com"+href).get();
    	
    	Elements es=doc.getElementsByTag("blockquote");
    	
    	
    	Elements es1=doc.select("img#poster");
    	
    	String st=es1.attr("data-src");
    	
    	Elements es2=doc.getElementsByClass("rating-stars");
    	//rating
    	Elements es21 = es2.eq(1);
    	String rate = es21.attr("data-value");
    	//critics rating
    	
    	Elements eslang=doc.getElementsByClass("__language");
    	String lang = eslang.html();
    	
    	Elements esdirect=doc.getElementsByClass("__director-name");
    	String director=esdirect.html();
    	//System.out.println(director);

    	Elements eswriter=doc.getElementsByClass("__writer-name");
    	String writer=eswriter.html();
    	//System.out.println(writer);
    	

    	Elements esmusic=doc.getElementsByClass("__composer-name");
    	String musician=esmusic.html();
    	//System.out.println(musician);
    	

    	Elements eslead=doc.getElementsByClass("__cast-member");
    	
    	Elements eslead1=eslead.eq(1);
    	Elements eslead2=eslead.eq(2);
    	Elements eslead3=eslead.eq(3);
    	Elements eslead4=eslead.eq(4);

    	Elements eslead5=eslead.eq(5);
    	Elements eslead6=eslead.eq(6);
    	
    	String lead1=eslead1.attr("content");
    	//System.out.println(lead1);
    	
    	String lead2=eslead2.attr("content");
    	//System.out.println(lead2);
    	
    	String lead3=eslead3.attr("content");
    	//System.out.println(lead3);
    	
    	String lead4=eslead4.attr("content");
    	//System.out.println(lead4);
    	
    	String lead5=eslead5.attr("content");
    	//System.out.println(lead3);
    	
    	String lead6=eslead6.attr("content");
    	//System.out.println(lead4);
    	
    	Elements esshow=doc.select("a[href^=/buytickets]");
    	String ticket_link = esshow.attr("href");
    	
    	//System.out.println(ticket_link);
    	
    	float rating;
    	
    	if(rate.equals("")){rating=0;}
    	else{rating=Float.parseFloat(rate);}
    	float empty=5-rating;
    	
    	String str=es.html();
    	
    	String html = "<img src='"+st+"' alt='Sorry Could not fetch data' >";
    	
    	html=html + "<br>"+"<h4>";
    	//html=html + rate;
    	
    	while(rating>=1.0)
    	{
    		html=html+"<img src="+'"'+"http://cdn4.iconfinder.com/data/icons/pretty_office_3/32/Star-Full.png"+'"'+">";
    		rating=rating - 1;
    	}
    	
    	if(rating!=0)
    	{
    		html=html+"<img src="+'"'+"http://cdn4.iconfinder.com/data/icons/pretty_office_3/32/Star-Half-Full.png"+'"'+">";
    		rating=rating - 1;
    	}
    	
    	if(empty!=5)
    	{
    		while(empty>=1.0)
    		{
    			html=html+"<img src="+'"'+"http://cdn4.iconfinder.com/data/icons/pretty_office_3/32/Star-Empty.png"+'"'+">";
    			empty=empty - 1;
    		}
    	}
    	
    	html=html + "</h4>"+lang+"<br>"+director+"<br>"+writer+"<br>"+musician+"<br>"+"<strong>Lead Cast:</strong>"+"<font color="+'"'+"#0066cc"+'"'+">"+lead1+","+lead2+","+lead3+","+lead4+","+lead5+","+lead6+"</font>"+"<br>"+ str;
    	
    	
    	
    	api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
    	
    	
    	Document doc2 = Jsoup.connect("http://in.bookmyshow.com"+ticket_link).get();
    	
    	Elements estkt=doc.getElementsByClass("list ");
    	
    	
    	
    }
    
    /**
     * This method is called when the user select the keyword
     * "cricket".This returns a form with two field.
     * The first field is the drop down list of the currently
     * running international cricket series, in which user can
     * select one.
     * The second field of the form contains the time in 
     * minutes in the form of drop down list.The user have to select
     * one option from this list and the notification of the match 
     * is shown after every selected interval of time.
     * If there is no live match currently running in that series,
     * a sorry chatlet is returned.
     * If the user wants to see the score only once,he can left 
     * the time field unselected.
     **/
    
    @OnKeyword(value="cricket")
    public void cricket(TeamchatAPI api) throws IOException
    {
    	Document doc = Jsoup.connect("http://www.starsports.com/cricket/index.html").get();
    	Elements el1 = doc.select("a[href^=http://www.starsports.com/cricket/tour");
    	
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	Form form = api.objects().form();
    	Field field = api.objects().select().name("live").label("Live Matches");
    	
    	chatlet.setQuestion("Chosse Your Live Series From The List");
    	chatlet.showDetails(true);
    	chatlet.allowComments(false);
    	chatlet.setReplyLabel("Select Series");
    	
    	
    	String metadata="";
    	int i =1;
    	for(Element e : el1){
    		
    		field.addOption(i+"."+e.html());
    		if(metadata.equals("")){
    			metadata = e.attr("href");
    		}
    		else
    			metadata = metadata+","+e.attr("href");
    			
    		i = i+1;
    	}
    	chatlet.setMetaInfo(metadata);
    	form.addField(field);
    	
    	form.addField(api.objects().select().addOption("5").addOption("10").addOption("15").addOption("30").addOption("45").addOption("60").addOption("90").addOption("120").label("notify duration in minutes").name("interval"));
    	
    	chatlet.setReplyScreen(form);
    	chatlet.alias("score");
    	api.performPostInCurrentRoom(chatlet);
    	
    }

    @OnAlias("score")
    public void score(TeamchatAPI api) throws NumberFormatException, IOException, ParseException{
    	
    	String str = api.context().currentReply().getField("live");
    	String interval=api.context().currentReply().getField("interval");
    	
    	
    	String[] strparts =str.split("\\.");
    	
    	String Meta = api.context().currentChatlet().getMetaInfo();
    	String[] metaparts = Meta.split(",");
    	
    	Document doc = Jsoup.connect(metaparts[Integer.parseInt(strparts[0])-1]).get();
    	Elements el1 = doc.select("div[id^=ssr-matchldate-label]");
    	String date = new SimpleDateFormat("dd MMM").format(new Date());
    	
    	Elements es1=doc.getElementsByClass("ssr-matchno");
		
		Elements es2=doc.getElementsByClass("ssr-tour-venue-name");
		
		Elements es3=doc.getElementsByClass("ssr-cricket-series-fixtures-teamname-div");
		
		
		Elements es5=doc.getElementsByClass("ssr-cricket-series-fixtures-status-div");
		
    
		
    	
    	int j=0;
    	int count =0;
    	for(Element e : el1){
    		
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
    		if(datef.length>1){							//span of 5 days in case of test matches
    			Date date22 = date1.parse(datef[1]);
    			x=date11.compareTo(date33)*date22.compareTo(date33);
    		}
    		
    		
    		if(x<=0 || y==0){
    			
    			String html="";
    			
    			//System.out.println(es1.eq(j).html());
    			//System.out.println(es2.eq(j).html());
    			//System.out.println(es3.eq(j).html());
    			//System.out.println(es5.eq(j).html());
    			
    			html=html+es1.eq(j).html()+"<br>";
    			html=html+es2.eq(j).html()+"<br>";
    			html=html+es3.eq(j).html()+"<br>";
    			html=html+es5.eq(j).html();
    			
    	    	api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
    	    	count =count+1;
    		}
    		
    		
    		
    		j=j+1;
    		
    		
    		
    		
    	}
    	
    	if(count==0) 
    	{
    		api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml("no live matches in this series"));
    	}
    	
    	else
    	{
    		try
    		{
    			SchedulerFactory sf = new StdSchedulerFactory();
    			Scheduler sched = sf.getScheduler();
    			sched.start();
    			//Job
    		
    			JobDetail job = newJob(schedule.class).withIdentity("score","group1").build();
    			job.getJobDataMap().put("API", api);
    			job.getJobDataMap().put("META", metaparts[Integer.parseInt(strparts[0])-1]);
    		
    			//Trigger
    			CronTrigger trigger = newTrigger().withIdentity("Trigger","group1").withSchedule(cronSchedule("0 0/"+interval+" * * * ?")).build();
    		
    			sched.scheduleJob(job,trigger);
    			api.performPostInCurrentRoom(new TextChatlet("Notifications On"));
    		
    		
    		}catch(Exception e){;}
    		
    	}
    	
    }
    
    @OnKeyword(value="football")
    public void football(TeamchatAPI api) throws IOException
    {
    	Document doc = Jsoup.connect("http://www.starsports.com/football/index.html").get();
    	Elements el1 = doc.select("a[href^=http://www.starsports.com/football/leagues");
    	
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	Form form = api.objects().form();
    	Field field = api.objects().select().name("live").label("Live Matches");
    	
    	chatlet.setQuestion("Chosse The League From The List");
    	chatlet.showDetails(true);
    	chatlet.allowComments(false);
    	chatlet.setReplyLabel("Select League");
    	
    	
    	String metadata="";
    	int i =1;
    	for(Element e : el1){
    		
    		field.addOption(i+"."+e.html());
    		if(metadata.equals("")){
    			metadata = e.attr("href");
    		}
    		else
    			metadata = metadata+","+e.attr("href");
    			
    		i = i+1;
    	}
    	chatlet.setMetaInfo(metadata);
    	form.addField(field);
    	
    	//form.addField(api.objects().select().addOption("5").addOption("10").addOption("15").addOption("30").addOption("45").addOption("60").addOption("90").addOption("120").label("notify duration in minutes").name("interval"));
    	
    	chatlet.setReplyScreen(form);
    	chatlet.alias("goal");
    	api.performPostInCurrentRoom(chatlet);
    
    }
    
    
    @OnAlias(value="goal")
    public void goal(TeamchatAPI api) throws NumberFormatException, IOException
    {
    	String str = api.context().currentReply().getField("live");
    	//String interval=api.context().currentReply().getField("interval");
    	//System.out.println(str);
    	
    	String[] strparts =str.split("\\.");
    	
    	String Meta = api.context().currentChatlet().getMetaInfo();
    	//System.out.println(Meta);
    	String[] metaparts = Meta.split(",");
    	
    	Document doc = Jsoup.connect(metaparts[Integer.parseInt(strparts[0])-1]).get();
    	
    	Elements es1=doc.getElementsByClass("ssr-mc-slot-tour-name");
		
		Elements es2=doc.getElementsByClass("ssr-football-mc-slot-team-conatiner");
		
		Elements es3=doc.getElementsByClass("ssr-football-mc-slot-match-status");
		
		int i=0;
		for(Element e:es1){i=i+1;}
		
		int j=0;
		while(j<i)
		{
			String html=es1.eq(j).html()+"<br>"+es2.eq(j).html()+es3.eq(j).html();
			api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
			j=j+1;
		}
		
    	
    }
    
    /*
    @OnKeyword(value="badminton")
    public void badminton(TeamchatAPI api) throws IOException
    {
    	Document doc = Jsoup.connect("http://www.starsports.com/cricket/index.html").get();
    	Elements el1 = doc.select("a[href^=http://www.starsports.com/badminton/");
	
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	Form form = api.objects().form();
    	Field field = api.objects().select().name("live").label("Live Matches");
    	
    	String metadata="";
    	int i =1;
    	for(Element e : el1){
    		
    		field.addOption(i+"."+e.html());
    		if(metadata.equals("")){
    			metadata = e.attr("href");
    		}
    		else
    			metadata = metadata+","+e.attr("href");
    			
    		i = i+1;
    	}
    	chatlet.setMetaInfo(metadata);
    	form.addField(field);
    	
    	//form.addField(api.objects().select().addOption("5").addOption("10").addOption("15").addOption("30").addOption("45").addOption("60").addOption("90").addOption("120").label("notify duration in minutes").name("interval"));
    	
    	chatlet.setReplyScreen(form);
    	chatlet.alias("point");
    	api.performPostInCurrentRoom(chatlet);

    }
    
    @OnAlias(value="point")
    public void point(TeamchatAPI api) throws NumberFormatException, IOException
    {
    	String str = api.context().currentReply().getField("live");
    	//String interval=api.context().currentReply().getField("interval");
    	System.out.println(str);
    	
    	String[] strparts =str.split("\\.");
    	
    	String Meta = api.context().currentChatlet().getMetaInfo();
    	System.out.println(Meta);
    	String[] metaparts = Meta.split(",");
    	
    	Document doc = Jsoup.connect(metaparts[Integer.parseInt(strparts[0])-1]).get();
    	
    
    }
    */
    
    /*
    @OnKeyword(value="hockey")
    public void hockey(TeamchatAPI api) throws IOException
    {
    	Document doc = Jsoup.connect("http://www.starsports.com/cricket/index.html").get();
    	Elements el1 = doc.select("a[href^=/hockey/");
    	
    	el1=el1.eq(0);	//more than 1 elements in el1 all repeated
    	
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	Form form = api.objects().form();
    	Field field = api.objects().select().name("live").label("Live Matches");
    	
    	String metadata="";
    	int i =1;
    	for(Element e : el1){
    		
    		field.addOption(i+"."+e.html());
    		if(metadata.equals("")){
    			metadata = e.attr("href");
    		}
    		else
    			metadata = metadata+","+e.attr("href");
    			
    		i = i+1;
    	}
    	chatlet.setMetaInfo(metadata);
    	form.addField(field);
    	
    	//form.addField(api.objects().select().addOption("5").addOption("10").addOption("15").addOption("30").addOption("45").addOption("60").addOption("90").addOption("120").label("notify duration in minutes").name("interval"));
    	
    	chatlet.setReplyScreen(form);
    	chatlet.alias("point");
    	api.performPostInCurrentRoom(chatlet);
    	
    }
*/
    

    @OnKeyword("cricketapi")	
    public void crickapi(TeamchatAPI api) throws IOException
    {
    	String url = "http://www.crm.wherrelz.com/api/cricket";
    	
    	URL obj = new URL(url);
    	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    	
    	con.setRequestMethod("GET");
    	
    	int responseCode=con.getResponseCode();
    	
    	System.out.println("\nSending 'GET' request to URL : " +url);
    	System.out.println("Response Code : " + responseCode);
    	
    	String metadata="";
    	
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	Form form = api.objects().form();
    	Field field = api.objects().select().name("live").label("Live Matches");
    	
    	chatlet.setQuestion("Chosse Match From The List");
    	chatlet.showDetails(true);
    	chatlet.allowComments(false);
    	chatlet.setReplyLabel("Select Match");
    	
    	if(responseCode==200)
    	{
    		BufferedReader in = new BufferedReader (new InputStreamReader(con.getInputStream()));
    		String inputLine;
    		StringBuffer response = new StringBuffer();
    		
    		while((inputLine = in.readLine())!=null)
    		{
    			response.append(inputLine);
    		}
    		
    		in.close();
    		
    		JSONObject json = new JSONObject(response.toString());
    		
    		JSONArray data = json.getJSONArray("data");
    		
    		StringBuilder news = new StringBuilder();
    		
    		news.append("<ul>");
    		int j;
    		for(int i=0;i<data.length();i++)
			{
    			j=i+1;
				JSONObject jsona =  data.getJSONObject(i);
				String news1 = jsona.getString("description");
				String news2 = jsona.getString("unique_id");
				String newsa=news1+"&nbsp ";
				news.append("<li>"+newsa+"</li>");
				if(metadata.equals("")){metadata=news2;}
				else{metadata=metadata+","+news2;}
				field.addOption(j+"."+newsa);
    		}
    		news.append("</ul");
    		
    		chatlet.setMetaInfo(metadata);
        	form.addField(field);
        	
        	chatlet.setReplyScreen(form);
        	chatlet.alias("pointapi");
        	api.performPostInCurrentRoom(chatlet);

    	}
    	
    	else
    	{
    		api.perform(api.context().currentRoom().post(new PrimaryChatlet().setQuestionHtml("<center><img src='http://integration.teamchat.com/sol/bot-images/disaster-logo.jpg' width='150' /></center>"
					+ "<div>Sorry! no results.</div>")));
    	}
    	
    	
    	//form.addField(api.objects().select().addOption("5").addOption("10").addOption("15").addOption("30").addOption("45").addOption("60").addOption("90").addOption("120").label("notify duration in minutes").name("interval"));
    	

    }
    
    @OnAlias("pointapi")
    public void pointapi(TeamchatAPI api) throws IOException
    {
    	String str = api.context().currentReply().getField("live");
    	//String interval=api.context().currentReply().getField("interval");
    	System.out.println(str);
    	
    	String[] strparts =str.split("\\.");
    	
    	String Meta = api.context().currentChatlet().getMetaInfo();
    	System.out.println(Meta);
    	String[] metaparts = Meta.split(",");
    	
    	String id=metaparts[Integer.parseInt(strparts[0])-1];
    	
    	String url = "http://www.crm.wherrelz.com/api/cricketScore?unique_id="+id;
    	
    	URL obj = new URL(url);
    	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
    	
    	con.setRequestMethod("GET");
    	
    	int responseCode=con.getResponseCode();
    	
    	System.out.println("\nSending 'GET' request to URL : " +url);
    	System.out.println("Response Code : " + responseCode);
    	
    	if(responseCode==200)
    	{
    		BufferedReader in = new BufferedReader (new InputStreamReader(con.getInputStream()));
    		String inputLine;
    		StringBuffer response = new StringBuffer();
    		
    		while((inputLine = in.readLine())!=null)
    		{
    			response.append(inputLine);
    		}
    		
    		in.close();
    		
    		JSONObject json = new JSONObject(response.toString());
    		
    		String data = json.getString("score");
    		
    		
    		api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(data));
    		System.out.println(data);

    	}
    	
    }
    
    
}

