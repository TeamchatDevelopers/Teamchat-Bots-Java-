// Suman Swaroop (Intern)

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyWordWithParam;
import com.teamchat.client.annotations.OnKeyword;

import com.teamchat.client.annotations.Param;
import com.teamchat.client.sdk.Field;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.HTML5Chatlet;
import com.teamchat.client.sdk.chatlets.PollChatlet;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;
import com.teamchat.client.sdk.pers.TeamchatRecord;

import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.*;
public class TwitBot {
	
	// auth key of bot
	private static final String authKey = "";
	
	// apikey to create poll and send it to twitter accounts , for other functionality this is not required
	private static final String apikey  = "";
	//private static Twitter twitter;
	//String myself = "naveenk61230959";
	
	public static void main(String[] args) throws TwitterException, IOException {
		
		
		
		
		// Class object
		TwitBot twitb = new TwitBot();
		
		
		

		//TeamchatBot
		TeamchatAPI api = TeamchatAPI.fromFile("config.json").setAuthenticationKey(authKey);
		api.startReceivingEvents(twitb);
		
		
		
		
	}
	// authentication through configurationbuilder
	private static Configuration configure() {
			
			
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		//configuring twitter credentials
		configurationBuilder.setDebugEnabled(false)
		        .setJSONStoreEnabled(true)
		        .setOAuthConsumerKey(CollectionTask.getConsumerKey())
		        .setOAuthConsumerSecret(CollectionTask.getConsumerSecret())
		        .setOAuthAccessToken(CollectionTask.getAccessToken())
		        .setOAuthAccessTokenSecret(CollectionTask.getAccessTokenSecret());
	
		Configuration configuration = configurationBuilder.build();
	return configuration;
		}
		
	//Get Notification of twitter on teamchat. Keyword to start recieveing notifications
	@OnKeyword("notify")
	public void notify(final TeamchatAPI api){
		UserStreamListener listener = new UserStreamListener(){
			public void onStatus(Status status)
			{
				
				api.performPostInCurrentRoom(new TextChatlet("onStatus @" + status.getUser().getScreenName() + " - " + status.getText()));
				
			}

	        @Override
	        public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	        	api.performPostInCurrentRoom(new TextChatlet("Got a status deletion notice id:" + statusDeletionNotice.getStatusId()));
	        }

	        public void onDeletionNotice(long directMessageId, long userId) {
	        	api.performPostInCurrentRoom(new TextChatlet("Got a direct message deletion notice id:" + directMessageId));
	        }

	        @Override
	        public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	        	api.performPostInCurrentRoom(new TextChatlet("Got a track limitation notice:" + numberOfLimitedStatuses));
	        }

	        @Override
	        public void onScrubGeo(long userId, long upToStatusId) {
	        	api.performPostInCurrentRoom(new TextChatlet("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId));
	        }

	        @Override
	        public void onStallWarning(StallWarning warning) {
	        	api.performPostInCurrentRoom(new TextChatlet("Got stall warning:" + warning));
	        }


	        public void onFavorite(User source, User target, Status favoritedStatus) {
	        	api.performPostInCurrentRoom(new TextChatlet("onFavorite source:@"
	                + source.getScreenName() + " target:@"
	                + target.getScreenName() + " @"
	                + favoritedStatus.getUser().getScreenName() + " - "
	                + favoritedStatus.getText()));
	        }

	        public void onUnfavorite(User source, User target, Status unfavoritedStatus) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUnFavorite source:@"
	                + source.getScreenName() + " target:@"
	                + target.getScreenName() + " @"
	                + unfavoritedStatus.getUser().getScreenName()
	                + " - " + unfavoritedStatus.getText()));
	        }

	        public void onFollow(User source, User followedUser) {
	        	api.performPostInCurrentRoom(new TextChatlet("onFollow source:@"
	                + source.getScreenName() + " target:@"
	                + followedUser.getScreenName()));
	        }

	        public void onUnfollow(User source, User followedUser) {
	        	api.performPostInCurrentRoom(new TextChatlet("onFollow source:@"
	                + source.getScreenName() + " target:@"
	                + followedUser.getScreenName()));
	        }

	        public void onDirectMessage(DirectMessage directMessage) {
	        	api.performPostInCurrentRoom(new TextChatlet("onDirectMessage text:"
	                + directMessage.getText()));
	        }

	        public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserListMemberAddition added member:@"
	                + addedMember.getScreenName()
	                + " listOwner:@" + listOwner.getScreenName()
	                + " list:" + list.getName()));
	        }

	        public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserListMemberDeleted deleted member:@"
	                + deletedMember.getScreenName()
	                + " listOwner:@" + listOwner.getScreenName()
	                + " list:" + list.getName()));
	        }

	        public void onUserListSubscription(User subscriber, User listOwner, UserList list) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserListSubscribed subscriber:@"
	                + subscriber.getScreenName()
	                + " listOwner:@" + listOwner.getScreenName()
	                + " list:" + list.getName()));
	        }

	        public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserListUnsubscribed subscriber:@"
	                + subscriber.getScreenName()
	                + " listOwner:@" + listOwner.getScreenName()
	                + " list:" + list.getName()));
	        }

	        public void onUserListCreation(User listOwner, UserList list) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserListCreated  listOwner:@"
	                + listOwner.getScreenName()
	                + " list:" + list.getName()));
	        }

	        public void onUserListUpdate(User listOwner, UserList list) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserListUpdated  listOwner:@"
	                + listOwner.getScreenName()
	                + " list:" + list.getName()));
	        }

	        public void onUserListDeletion(User listOwner, UserList list) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserListDestroyed  listOwner:@"
	                + listOwner.getScreenName()
	                + " list:" + list.getName()));
	        }

	        public void onUserProfileUpdate(User updatedUser) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserProfileUpdated user:@" + updatedUser.getScreenName()));
	        }

	        public void onUserDeletion(long deletedUser) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserDeletion user:@" + deletedUser));
	        }

	        public void onUserSuspension(long suspendedUser) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUserSuspension user:@" + suspendedUser));
	        }

	        public void onBlock(User source, User blockedUser) {
	        	api.performPostInCurrentRoom(new TextChatlet("onBlock source:@" + source.getScreenName()
	                + " target:@" + blockedUser.getScreenName()));
	        }

	        public void onUnblock(User source, User unblockedUser) {
	        	api.performPostInCurrentRoom(new TextChatlet("onUnblock source:@" + source.getScreenName()
	                + " target:@" + unblockedUser.getScreenName()));
	        }

	        public void onRetweetedRetweet(User source, User target, Status retweetedStatus) {
	        	api.performPostInCurrentRoom(new TextChatlet("onRetweetedRetweet source:@" + source.getScreenName()
	                + " target:@" + target.getScreenName()
	                + retweetedStatus.getUser().getScreenName()
	                + " - " + retweetedStatus.getText()));
	        }

	        public void onFavoritedRetweet(User source, User target, Status favoritedRetweet) {
	        	api.performPostInCurrentRoom(new TextChatlet("onFavroitedRetweet source:@" + source.getScreenName()
	                + " target:@" + target.getScreenName()
	                + favoritedRetweet.getUser().getScreenName()
	                + " - " + favoritedRetweet.getText()));
	        }

	        public void onQuotedTweet(User source, User target, Status quotingTweet) {
	        	api.performPostInCurrentRoom(new TextChatlet("onQuotedTweet" + source.getScreenName()
	                + " target:@" + target.getScreenName()
	                + quotingTweet.getUser().getScreenName()
	                + " - " + quotingTweet.getText()));
	        }

	        @Override
	        public void onException(Exception ex) {
	            ex.printStackTrace();
	            api.performPostInCurrentRoom(new TextChatlet("onException:" + ex.getMessage()));
	        }

			@Override
			public void onFriendList(long[] arg0) {
				// TODO Auto-generated method stub
				
			}

			
			
		};
		
		TwitterStream twitterstream = new TwitterStreamFactory(configure()).getInstance();
		twitterstream.addListener(listener);
		
		twitterstream.user();

	}
	
	// getting the list of followers of the authorized followers
	private ArrayList<String> GetFollowers() throws TwitterException{
		
		// twitter
		TwitterFactory tf = new TwitterFactory(configure());
		Twitter twitter = tf.getInstance();
						
		long cursor = -1;
		IDs ids;
		ArrayList<String> userids = new ArrayList<String>();
		
		do{
			ids = twitter.getFollowersIDs(twitter.getScreenName(),cursor);
		
			for(long id : ids.getIDs()){
				String name = twitter.showUser(id).getScreenName();
				userids.add(name);
			}
		}while((cursor = ids.getNextCursor())!=0);
		
		return userids;
		
		
	}
	
	// Poll created in Teamchat can be send to all others but replies are not added up together yet
	
	public void send(TeamchatAPI api){
		String _roomId = api.context().currentRoom().getId();
		String _msgId =api.context().currentChatlet().getFormId();
		TeamchatRecord tc = (TeamchatRecord)api.data();
		String _userId = tc.getUserId();
		String _token=tc.getToken();
		
		
		String msgurl = "https://enterprise.teamchat.com/webjim-echat/clientApi/html/cmsg.html?gId="+_roomId+"&mId="+_msgId+"&tk="+_token+"&uId="+_userId;
		System.out.println(msgurl);
	}
	
	
	
	public void createpoll(String question, ArrayList<String> userids ) throws IOException, TwitterException{
		
		// put request 
		// Making a poll chatlet
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "question="+question);
		Request request = new Request.Builder()
		  .url("http://dev-api.webaroo.com/SMApi/api/smartmsg/poll")
		  .put(body)
		  .addHeader("apikey", apikey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "890acc9b-724d-6360-12b2-cfd303ce2fb1")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		Response response = client.newCall(request).execute();
		// extracting ID of the chatlet from the json response
		String resp = response.body().string();
		JSONObject js = new JSONObject(resp);
		String id = js.getString("id");
		
		// Linking the users who can poll to the chatlet
		getlink(id,question,userids);
		System.out.println(id);
		
	}
	
	public void getlink(String id,String question,ArrayList<String> userids) throws IOException, TwitterException{
		
		// twitter
		TwitterFactory tf = new TwitterFactory(configure());
		Twitter twitter = tf.getInstance();
				
		
		//getlink request
		
		
		
		// Registering each follower in the chatlet so that they can reply
		
		for (String userid : userids) {
			
		OkHttpClient client = new OkHttpClient();
		String destination = userid;

		MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		RequestBody body = RequestBody.create(mediaType, "destination="+destination);
		
		Request request = new Request.Builder()
		  .url("http://dev-api.webaroo.com/SMApi/api/smartmsg/msg/"+id+"/signedlink") // id from the put method
		  .post(body)
		  .addHeader("apikey",apikey)
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "41bba040-6d95-001c-d22e-e3cb9d0c01d3")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();

		Response response = client.newCall(request).execute();
		//Getting the json data from response after registering user
		
		String resp = response.body().string();
		JSONArray js = new JSONArray(resp);
		JSONObject jsobj = js.getJSONObject(0);
		
		String idlink = jsobj.getString("id");
		
		//tweeting followers
		twitter.updateStatus("@"+userid+" "+question+"\n Reply on "+gethtml(idlink));
		System.out.println("tweet");
		}
		
	}
	
	// To get the url of indidiual follower registered to reply on chatlet
	public String gethtml(String idlink) throws IOException{
		
		String url = "http://dev-smapi.webaroo.com/SMApi/api/embed/"+idlink;
		//get
		OkHttpClient client = new OkHttpClient();
		
		Request request = new Request.Builder()
		  .url(url)
		  .get()
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "4fc7d4b1-e000-7a83-b3ea-6c4034ff9d5a")
		  .build();

		Response response = client.newCall(request).execute();
		System.out.println(response);
	return url;
	}
	
	// keyword to send the poll to twitter followers from teamchat
	@OnKeyword("TweetPoll")
	public void polltweet(TeamchatAPI api) throws TwitterException{
		
		
		PrimaryChatlet chatlet = new PrimaryChatlet();
		chatlet.setQuestion("Reply poll question :--");
		Form form = api.objects().form();
		form.addField(api.objects().input().label("Question:").name("question"));
		
		// Field for followers.
		Field field = api.objects().select().name("followers").label("Followers");
		
		// This field is a drop down with one selection
		ArrayList<String> followers = new ArrayList<String>();
		followers = this.GetFollowers();
		
		for(String el : followers){
			field.addOption(el);
		}
		field.addOption("All Followers");			// Modification :  Multiple Follower Selection
		form.addField(field);
		
		
		
		chatlet.alias("reply");
		chatlet.setReplyScreen(form);
		api.performPostInCurrentRoom(chatlet);
		
	}
	
	@OnAlias("reply")
	public void post(TeamchatAPI api) throws IOException, TwitterException{
		
		String question = api.context().currentReply().getField("question");
		final String list = api.context().currentReply().getField("followers");
		
		//All or one follower
		//creating the poll url that can be used by non-teamchat users also.
		if(list.equals("All Followers")){
			createpoll(question, this.GetFollowers());
			PollChatlet chatlet = new PollChatlet();
			chatlet.setQuestion(question);
			api.performPostInCurrentRoom(chatlet);
		}
		else{
			createpoll(question, new ArrayList<String>(Arrays.asList(list)));
			api.performPostInCurrentRoom(new PollChatlet().setQuestion(question));
		}
	}

	// Get the timeline of your twitter account in Teamchat
	@OnKeyword("My Timeline")
	public void mytimeline(TeamchatAPI api) throws TwitterException{
		// twitter
		TwitterFactory tf = new TwitterFactory(configure());
		Twitter twitter = tf.getInstance();
				
		
		
		List<Status> statuses = twitter.getUserTimeline(twitter.getScreenName());
		String html = "";
		for(Status el : statuses){
			html = html + el.getText()+"<br>";
		}
		api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
	}
	
	// get the timeline of any user write timeline userid
	@OnKeyWordWithParam("Timeline")
	public void timeline(TeamchatAPI api, @Param String username) throws TwitterException{
		// twitter
		TwitterFactory tf = new TwitterFactory(configure());
		Twitter twitter = tf.getInstance();
						
		String[] srch = new String[] { username } ;
		ResponseList<User> responselist = twitter.lookupUsers(srch);
		
		for(User user: responselist){
			if(user.getStatus()!=null){
				List<Status> statuses = twitter.getUserTimeline(user.getId());
				String html = "";
				html = html+user.getName()+"<br>";
				
				for(Status el : statuses){
					html = html + el.getText()+"<br>";
				}
				
				api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
			}
		}
	}
	
	
	// Search Twitter
	
	// find <keyword> to search twitter
	@OnKeyword("find")
	public void find(TeamchatAPI api)
	{

		PrimaryChatlet chatlet = new PrimaryChatlet();
		chatlet.setQuestion("Reply poll question :--");
		Form form = api.objects().form();
		form.addField(api.objects().input().label("keyword").name("keyword"));
		
		form.addField(api.objects().input().name("from").label("from"));
		
		form.addField(api.objects().input().name("to").label("to"));
		
		//form.addField(api.objects().input().name("tag").label("set referencing person"));
		
		form.addField(api.objects().input().name("since").label("date since"));
		
		form.addField(api.objects().input().name("until").label("date until"));
	
		chatlet.setReplyScreen(form);
		chatlet.alias("findx");
		
		api.performPostInCurrentRoom(chatlet);
	
	}
	
	@OnAlias(value="findx")
	public void findx(TeamchatAPI api) throws TwitterException{
		String keyword = api.context().currentReply().getField("keyword");
		String from = api.context().currentReply().getField("from");
		String to = api.context().currentReply().getField("to");
		String since = api.context().currentReply().getField("since");
		String until = api.context().currentReply().getField("until");
		
		String findtweet=keyword;
		
		if(!from.equals(""))findtweet=findtweet+" from:"+from;
		if(!to.equals(""))findtweet=findtweet+" to:"+to;
		if(!since.equals(""))findtweet=findtweet+" since:"+since;
		if(!until.equals(""))findtweet=findtweet+" until:"+until;
		
		for(int page = 1;page<2;page++){
			Query query = new Query(findtweet);
			query.count(4);		//set default count to 2
			query.lang("en");	//set default english
			
			
			//query.get
			// twitter
			TwitterFactory tf = new TwitterFactory(configure());
			Twitter twitter = tf.getInstance();
			
			
			//query.set
			QueryResult queryresult = twitter.search(query);
			List<Status> querytweets = queryresult.getTweets();
			
			if(querytweets.size() == 0)
			{
				break;
			}
			String html="";
			for(Status querytweet:querytweets)
			{
				html="";
				html=html+"posted from:"+querytweet.getSource()+"<br>"+querytweet.getCreatedAt()+"<br>"+querytweet.getText()+"<br>";
				api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
			}
		}
		
	}
	
	// To send tweets
	@OnKeyword(value="tweet")
	public void tweet(TeamchatAPI api) throws TwitterException{
		PrimaryChatlet chatlet = new PrimaryChatlet();
		chatlet.setQuestion("What do you want to tweet");
		
		Form form = api.objects().form();
		form.addField(api.objects().input().label("Tweet").name("tweet"));
		
		// Field for followers.
		Field field = api.objects().select().name("followers").label("Followers");
		
		// This field is a drop down with one selection
		ArrayList<String> followers = new ArrayList<String>();
		followers = this.GetFollowers();
		
		for(String el : followers){
			field.addOption(el);
		}
		field.addOption("All Followers");			// Modification :  Multiple Follower Selection
		form.addField(field);
		
		
		chatlet.setReplyScreen(form);
		chatlet.alias("tweetreply");
		
		api.performPostInCurrentRoom(chatlet);
	
	
	}
	
	
	@OnAlias("tweetreply")
	public void tweeetreply(TeamchatAPI api) throws TwitterException
	{
		// twitter
		TwitterFactory tf = new TwitterFactory(configure());
		Twitter twitter = tf.getInstance();
				
		String tweet = api.context().currentReply().getField("tweet");
		String followers = api.context().currentReply().getField("followers");
		
		String html="";
		if(followers.equals("ALL Followers"))
		{
			ArrayList<String> followerlist = new ArrayList<String>();
			followerlist = this.GetFollowers();
			
			for(String el : followerlist){
				html=html+"@"+el+" ";
			}
				
		}
		
		else html=html+"@"+followers+" ";
		
		html=html+tweet+"\n";
		
		twitter.updateStatus(html);
	}
	
	
}
