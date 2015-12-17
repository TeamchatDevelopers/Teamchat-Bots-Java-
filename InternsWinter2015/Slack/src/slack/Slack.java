//Owner: Amey Ambade
//Task 2
//Date of Submission: Friday, June 5, 2015
//Note: Authentication available only for one user: ameyambade@gmail.com

package slack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Field;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;

public class Slack {

	private static String client_id;
	private static String client_secret;
	private static String redirect_URI;
	
	EventListener eL ;
	String token = new String();
	String code = new String();
	String channelID = new String();
	String timestamp = new String();
	
	public Vector<String> name = new Vector<String>();
	public Vector<String> chid = new Vector<String>();
	public static Integer wait = 0;

	public static final String AuthKey = "c1e68efa9b4c472dc73606b223ade9c1";
	
	public static void main(String[] args) throws IOException
	{
		
		Properties prop1 = SlackDB.loadPropertyFromClasspath("slack.properties",
				Slack.class);
		
		client_id = prop1.getProperty("client_id");
		client_secret = prop1.getProperty("client_secret");
		redirect_URI = prop1.getProperty("redirect_uri");
		
		TeamchatAPI api = TeamchatAPI.fromFile("config.json")
							.setAuthenticationKey(AuthKey);
						
		api.startReceivingEvents(new Slack());
	}
	
	public Field getTeamField(TeamchatAPI api, Map<String,String> map)
	{
		Field f = api.objects().select().label("Teams").name("teams").addRegexValidation(".+", "Please select a team option");
		for(String key : map.keySet())
		{
			f.addOption(key);
		}
		
		return f;
	}
	
	@OnKeyword("slack")
	public void formToConnect(TeamchatAPI api) throws IllegalStateException,
	IOException, InterruptedException {
		
		eL = new EventListener();
		eL.setAPI(api);
		
		String EmailID = api.context().currentSender().getEmail();
		
		PrimaryChatlet p = new PrimaryChatlet();
		p.setQuestion("Please specify whether you want to authenticate as an Admin or not.");
		
		Form f = api.objects().form();
		
		f.addField(api.objects().radio().name("isAdmin").label("Select account type : ")
				.addOption("Admin")
				.addOption("Regular"));
		
		f.addField(getTeamField(api,SlackDB.getTeamNames(EmailID)));
		
		p.setReplyScreen(f);
		p.showDetails(true);
		p.allowComments(false);
		p.alias("connect");
		api.performPostInCurrentRoom(p);
	}
	
	@OnAlias("connect")
	public void ConnectToSlack(TeamchatAPI api) throws IllegalStateException,
			IOException, InterruptedException {
		
		String email = api.context().currentReply().senderEmail();
		
		String ans = api.context().currentReply().getField("isAdmin");
		String teamname = api.context().currentReply().getField("teams");
		
		String teamid = SlackDB.getTeamNames(email).get(teamname);
		
		String tok;
		
		if(!teamid.equals("NOTATEAMID"))
		{
			tok = SlackDB.getToken(email, teamid);
		}
		else
		{
			tok = SlackDB.getToken(email);
		}
		
		if(tok.equals("EMPTY")||tok.equals("notokenfound")){ // TODO check token value here if team is known
		//byte[] bytes = email.getBytes("UTF-8");
		//String encoded = Base64.getEncoder().encodeToString(bytes);
		
		StringBuilder sb = new StringBuilder("<a href='https://slack.com/oauth/authorize?response_type=code&client_id=");
		sb.append(client_id + "&redirect_uri=");
		sb.append(redirect_URI);
		
		/*******/
		//sb.append("&team=T0GCSS0G4");
		/******/
		
		if(!teamid.equals("NOTATEAMID"))
		{
			sb.append("&team="+teamid);
		}
		
		sb.append("&scope=");
				
		// TODO update scopes whenever adding a new functionality using a SLACK WEB API METHOD
		
		sb.append("channels:write,chat:write:user,team:read,users:read,search:read");
		if(ans.equals("Admin"))
		{
			sb.append(",admin&state=");
		}
		else
		{
			sb.append("&state=");
		}
		
		sb.append(email); // .append(encoded); 
		sb.append("' target='_blank'>Click Here to authorize</a>");
		
		
		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestionHtml(sb.toString())));
		
		
		System.out.println("Wait till I find your authorization info.");

		start(api, email);
		}
		else
		{
			token = tok;
			try 
			{
				eL.listenForEvents(token);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			api.performPostInCurrentRoom(new TextChatlet("You have already authenticated slack bot to connect to your slack."));
			saveTokenWithTeam(email,token);
		}

	}

	public void start(TeamchatAPI api, String email) throws IllegalStateException, IOException 
	{
		
		long endmillis = System.currentTimeMillis() + 120000;
		while(endmillis >  System.currentTimeMillis())
		{
			code = SlackDB.getCode(email);
			if(!(code.equals("nocodefound")||code.equals("EMPTY")))
			{
				break;
			}
		}
		
		if(code.equals("nocodefound")||code.equals("EMPTY"))
		{
			api.performPostInCurrentRoom(new TextChatlet("Timeout, the application could not authorize in a timely manner."));
		}
				
		SlackDB.saveCode(email, "EMPTY");
		
		StringBuilder sb1 = new StringBuilder("https://slack.com/api/oauth.access?client_id=");			sb1.append(client_id);
		sb1.append("&client_secret=");				sb1.append(client_secret);
		sb1.append("&code=");						sb1.append(code);
		sb1.append("&redirect_uri=");				sb1.append(redirect_URI);
		
		
		//String s = "https://slack.com/api/oauth.access?client_id=16434884548.16595583125&client_secret=eba80acbb9f18f3037c1e22f9024a669&code="
			//	+ code
			//	+ "&redirect_uri=http://localhost:8080/Slack/Slack";
		
		String s = sb1.toString();

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(s);

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();

		JSONObject acc = new JSONObject(output);
		Boolean value = (Boolean) acc.get("ok");

		if (!value) {
			String status = (String) acc.get("error");

			System.out.println("Sorry, could not get access token");

			api.performPostInCurrentRoom(new TextChatlet(
					" Could not connect, error:" + status));

		} else {

			token = acc.get("access_token").toString(); // This token is unique
														// to the user

			
			String scp = acc.getString("scope").toString();

			System.err.println(scp);
			
			System.out.println("Recieved access token.You are free to access your data now");

			saveTokenWithTeam(email,token);
			
			api.performPostInCurrentRoom(new TextChatlet(
					"Successfully connected"));
							
			try
			{
				eL.listenForEvents(token);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	@OnKeyword("help")
	public void help(TeamchatAPI api) {

		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestionHtml("Please enter the following keywords to perform functions-"
								+ " <br><b>Slack: To connect your slack accounts to teamchat</b>"
								+ " <br>channel: To Create new Channel "
								+ " <br>teaminfo: To get information about your team "
								+ " <br>userlist: To get list of your Slack team members "
								+ " <br>search: To search your Slack messages "
								+ " <br>message: To post message to a channel"
								+ " <br>delete: To delete the most recent message you posted")));

		System.out.println("Posting help data.");
	}

	@OnKeyword("channel")
	public void CreateChannel(TeamchatAPI api) {
		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion(
								"Please enter the name of the channel you want to create:")
						.setReplyScreen(
								api.objects()
										.form()
										.addField(
												api.objects()
														.input()
														.label("Name of Channel")
														.name("channel")))
						.alias("chname")));

		System.out.println("Acquired the channel name from form.");

	}

	@OnAlias("chname")
	public void Chname(TeamchatAPI api) throws ClientProtocolException,
			IOException {

		String channelname = api.context().currentReply().getField("channel");
		api.data().addField("channel", "name", channelname);

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(
				"https://slack.com/api/channels.create?token=" + token
						+ "&name=" + channelname + "&pretty=1");

		System.out.println("Request to create channel sent.");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();

		JSONObject j = new JSONObject(output);
		Boolean value = (Boolean) j.get("ok");

		if (!value) {

			System.out.println("Error encountered creating channel.");

			String status = (String) j.get("error");

			api.performPostInCurrentRoom(new TextChatlet(" Channel: "
					+ channelname + " already exists, error:" + status));

		} else {

			System.out.println("Posting successfully created channel.");

			api.performPostInCurrentRoom(new TextChatlet("Channel "
					+ channelname + " successfully created"));
		}

	}

	/*
	@OnAlias("createdchannel")
	public void created(TeamchatAPI api) {

		String channelname = api.data().getField("channel", "name");

		System.out.println("Created channel.");

		api.performPostInCurrentRoom(new TextChatlet("New Channel : "
				+ channelname + " created"));

	}*/

	public Field getOptions(TeamchatAPI api)
			throws ClientProtocolException, IOException {

		// Get Options from JSON
		name.clear();
		chid.clear();

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(
				"https://slack.com/api/channels.list?token=" + token
						+ "&pretty=1");

		System.out.println("Request to get list of channels sent.");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();
		JSONObject ch = new JSONObject(output);

		Boolean ok = (Boolean) ch.get("ok");
		if (ok) {

			System.out.println("Recieved list of channels. Displaying.");

			JSONArray channellist = ch.getJSONArray("channels");
			for (int i = 0; i < channellist.length(); i++) {
				JSONObject channel = (JSONObject) channellist.get(i);
				
				name.add(channel.get("name").toString());
				chid.add(channel.get("id").toString());
			}
		}

		else {
			//api.perform(api.context().currentRoom()
					//.post(new TextChatlet("Invalid")));
			System.err.println("Could not get channel list");
		}

		Field f = api.objects().select().label("Channels").name("ch").addRegexValidation(".+", "Please select a channel");
		for (int i = 0; i < name.size(); i++) {
			f.addOption(name.get(i));
		}

		return f;

	}

	@OnKeyword("message")
	public void postMessage(TeamchatAPI api) throws ClientProtocolException,
			IOException {

		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion(
								"Please fill the details to post a message in Slack ")
						.setReplyScreen(
								api.objects()
										.form()
										.addField(
												api.objects().input()
														.addOption("Message")
														.label("Message")
														.name("msg"))
										.addField(getOptions(api)))
						.alias("sendmsg")));

		System.out.println("Details extracted from form.");

	}

	@OnAlias("sendmsg")
	public void sendMessage(TeamchatAPI api) throws IllegalStateException,
			IOException {
		String msgToSend = api.context().currentReply().getField("msg");
		String actualmessage = msgToSend.replace(" ", "%20")
				.replace("?", "%3F"); // For get request to url
		String channelToSend = api.context().currentReply().getField("ch");
		
		for (int i = 0; i < name.size(); i++) {
			if (channelToSend.equals(name.get(i))) {
				channelID = chid.get(i); // Received Channel ID and the message to
										// send to this channel

				// Create a new HTTP request

				CloseableHttpClient client = HttpClientBuilder.create().build();
				HttpGet request = new HttpGet(
						"https://slack.com/api/chat.postMessage?token=" + token
								+ "&channel=" + channelID + "&text="
								+ actualmessage+ "&as_user=true" + "&pretty=1"); // Add
				// as_user=<username>
				// After OAuth

				System.out.println("Request to send message sent.");

				HttpResponse response = client.execute(request);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String line = "";
				StringBuilder sb = new StringBuilder();
				while ((line = rd.readLine()) != null)
					sb.append(line);
				String output = sb.toString();
				JSONObject ch = new JSONObject(output);

				Boolean ok = (Boolean) ch.get("ok");
				if (ok) {

					System.out.println("Posting message.");

					api.perform(api
							.context()
							.currentRoom()
							.post(new TextChatlet(
									"Successfully posted message in Slack")));
					timestamp = ch.getString("ts");
					System.out.println(timestamp);
				}

				else {
					api.perform(api.context().currentRoom()
							.post(new TextChatlet("Invalid")));
				}

				break;
			}

		}
		name.clear();
		chid.clear();

	}

	@OnKeyword("delete")
	public void deleteMsg(TeamchatAPI api) throws ClientProtocolException,
			IOException {
		
		System.err.println(channelID + "   " + timestamp);
		
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(
				"https://slack.com/api/chat.delete?token=" + token
				+ "&ts=" + timestamp + "&channel=" + channelID + "&pretty=1"
						); // Add
										// as_user=<username>
										// After OAuth
		System.out.println("Request to delete previous message sent.");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();
		JSONObject ch = new JSONObject(output);

		Boolean ok = (Boolean) ch.get("ok");
		if (ok) {

			System.out.println("Printing delete success message.");

			api.perform(api
					.context()
					.currentRoom()
					.post(new TextChatlet(
							"Successfully deleted most recent message in Slack")));
			timestamp = ch.getString("ts");
			System.out.println(timestamp);
		}

		else {
			String status = (String) ch.get("error");
			api.perform(api.context().currentRoom()
					.post(new TextChatlet("Could not delete , error : " + status)));
		}

	}
	
	public JSONObject getTeamJSON()throws ClientProtocolException, IOException 
	{
		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://slack.com/api/team.info?token="
				+ token + "&pretty=1");
		System.out.println("Request for team information sent.");

		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();

		JSONObject j = new JSONObject(output);
		JSONObject team = j.getJSONObject("team");
		
		return team;
	}
	
	public void saveTokenWithTeam(String email, String tok)throws ClientProtocolException, IOException 
	{
		JSONObject team = getTeamJSON();
		
		String id = (String) team.get("id");
		String teamname = (String) team.get("name");
		SlackDB.saveToken(email, tok, id, teamname);
		
	}

		
	@OnKeyword("teaminfo")
	public void teamInfo(TeamchatAPI api) throws ClientProtocolException,
			IOException {

		JSONObject team = getTeamJSON();
		
		String id = (String) team.get("id");
		String teamname = (String) team.get("name");
		String domain = (String) team.get("domain");

		System.out.println("Posting the team info.");

		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion("Your Slack team name is: " + teamname
								+ ", ID is: " + id + " ,and domain is: "
								+ domain)));
		
	}

	@OnKeyword("userlist")
	public void userlist(TeamchatAPI api) throws ClientProtocolException,
			IOException {

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://slack.com/api/users.list?token="
				+ token + "&pretty=1");
		System.out.println("Request for user list sent.");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();

		JSONObject j = new JSONObject(output);
		Boolean ok = (Boolean) j.get("ok");
		if (ok) {
			JSONArray mem = j.getJSONArray("members");
			String out[] = new String[100];

			StringBuilder allnames = new StringBuilder();
			for (int i = 0; i < mem.length(); i++) {
				JSONObject name = (JSONObject) mem.get(i);

				String names = (String) name.get("name");
				String userid = (String) name.get("id");

				out[i] = names;
				Boolean isadmin = (Boolean) name.get("is_admin");
				if (isadmin) {
					names = names.concat("(admin)");
				}

				allnames.append(", ").append(names); // Make a list of all names
				System.out.println(userid);
			}
			String listallnames = allnames.toString();

			System.out.println("Posting list of users.");

			api.perform(api
					.context()
					.currentRoom()
					.post(new PrimaryChatlet()
							.setQuestion("These are the users in your Slack team:"
									+ listallnames)));

		} else {
			api.perform(api.context().currentRoom()
					.post(new TextChatlet("Invalid")));
		}
	}

	@OnKeyword("url")
	public void getURl(TeamchatAPI api) throws IllegalStateException,
			IOException {

		/*
		 * HttpClient client = new DefaultHttpClient(); HttpGet request = new
		 * HttpGet(
		 * "https://slack.com/api/rtm.start?token=xoxp-5090557084-5090557086-5142984056-81c64e&pretty=1"
		 * );
		 * 
		 * HttpResponse response = client.execute(request);
		 * 
		 * BufferedReader rd = new BufferedReader(new InputStreamReader(response
		 * .getEntity().getContent())); String line = "";
		 * 
		 * StringBuilder sb = new StringBuilder(); while ((line = rd.readLine())
		 * != null) sb.append(line); String output = sb.toString();
		 * 
		 * JSONObject j = new JSONObject(output);
		 * 
		 * String url = j.get("url").toString(); System.out.println(url);
		 * 
		 * try { Properties properties = new Properties();
		 * properties.setProperty("url", url);
		 * 
		 * File file = new File("test.properties"); FileOutputStream fileOut =
		 * new FileOutputStream(file); properties.store(fileOut, "URL");
		 * fileOut.close(); } catch (FileNotFoundException e) {
		 * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
		 * // Add call to javascript code for running the websocket url
		 */}

	@OnKeyword("search")
	public void SearchMessages(TeamchatAPI api) {
		api.perform(api
				.context()
				.currentRoom()
				.post(new PrimaryChatlet()
						.setQuestion(
								"Please enter the keywords you want to search in your Slack messages:")
						.setReplyScreen(
								api.objects()
										.form()
										.addField(
												api.objects().input()
														.label("Search query")
														.name("searchquery")))
						.alias("query")));
		System.out.println("Details taken from form.");
	}

	@OnAlias("query")
	public void Query(TeamchatAPI api) throws ClientProtocolException,
			IOException {

		String query = api.context().currentReply().getField("searchquery");

		CloseableHttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(
				"https://slack.com/api/search.messages?token=" + token
						+ "&query=" + query + "&pretty=1");
		System.out.println("Search request sent.");
		HttpResponse response = client.execute(request);
		BufferedReader rd = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = "";
		StringBuilder sb = new StringBuilder();
		while ((line = rd.readLine()) != null)
			sb.append(line);
		String output = sb.toString();

		JSONObject j = new JSONObject(output);

		JSONObject msgs = j.getJSONObject("messages");
		JSONArray matches = msgs.getJSONArray("matches");

		StringBuilder strtodisp = new StringBuilder();

		if (matches.length() == 0) {
			api.perform(api.context().currentRoom()
					.post(new PrimaryChatlet().setQuestion("Nothing found!")));
		} else {

			for (int i = 0; i < matches.length(); i++) {
				JSONObject name = (JSONObject) matches.get(i);

				JSONObject name2 = (JSONObject) matches.get(i);
				String poster = (String) name2.get("username"); // Who posted
																// message

				JSONObject channel = name2.getJSONObject("channel"); // In which
																		// channel
																		// it
																		// was
																		// posted
				String channelname = (String) channel.get("name");

				String text = (String) name.get("text"); // What was posted

				strtodisp.append("User ").append(poster).append(" had posted ")
						.append(text).append(" in channel ")
						.append(channelname).append("<br>");
			}

		}

		String str = strtodisp.toString();
		System.out.println("Searching...");
		api.performPostInCurrentRoom(new PrimaryChatlet().setQuestionHtml(str));
	}

}
