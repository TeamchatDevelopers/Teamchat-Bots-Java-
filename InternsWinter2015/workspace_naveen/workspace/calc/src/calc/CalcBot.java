package calc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnBotAdded;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.Room;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PollChatlet;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;

//import com.teamchat.client.annotations.Expirable;

/**
 * A simple bot that response to the word "hi" that is posted in
 * any room containing the bot.
 * 
 * Please remember to update the authkey
 */
public class CalcBot {
    public static final String authKey = "c1fe434a365d432bc3ce668d6bbe8abd";

    /**
     * This method creates instance of teamchat client, login using specified 
     * email/password and wait for messages from other users
     **/
    
    public String roomId="";
    public String email="";
    public int amountgiven;
    public String lender;
    
    public static void main(String[] args) {
        TeamchatAPI api = TeamchatAPI.fromFile("config.json")
                .setAuthenticationKey(authKey);

        api.startReceivingEvents(new CalcBot());
    }

    /**
     * This method posts Hello World message, when any user posts hi message to
     * the logged in user (see main method)
     **/
    
    @OnBotAdded()
    public void add(TeamchatAPI api)
    {
    	System.out.println("asd");
        try{
    		Class.forName("com.mysql.jdbc.Driver");
    		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
    		Statement statement = connect.createStatement();
    		
    		statement.executeUpdate("create table account_manager(email_id_lender varchar(50),email_id_borrower varchar(50),transaction integer)");
    		
    		System.out.println("sddsffd");
    		
    	}catch(Exception e){System.out.println(e);}

    }
    
    @OnKeyword("hi")
    public void hello(TeamchatAPI api) {   
        api.perform( 
                api.context().currentRoom().post(
                        new TextChatlet("Hello World")
                        )
                );
        
        //String roomId = api.context().currentRoom().getId();
        
        //System.out.println(roomId);
        
       //Room room = api.context().p2p("sumanswar12@gmail.com").post(new TextChatlet("hello"));
       //api.perform(room);
       
       //System.out.println(room);
        
    }
    
    @OnKeyword(value = "lend",description="used by lender to enter the amount in the database")
    public void lend(TeamchatAPI api) 
    {   
    	roomId = api.context().currentRoom().getId();
        
        System.out.println(roomId);
    	
    	
    	PrimaryChatlet chatlet = new PrimaryChatlet();
    	chatlet.setQuestion("Enter the details");
    	chatlet.showDetails(true);
    	chatlet.allowComments(true);
    	chatlet.setReplyLabel("Fill Details");
    	
    	
    	
    	Form form = api.objects().form();
    	form.addField(api.objects().input().label("Enter borrower email id").name("email_id_borrower"));
    	
    	form.addField(api.objects().input().label("Enter Amount Given").name("amount_given").addRegexValidation("^[0-9]{1,6}$", "input must be numeric only"));
    	  
    	chatlet.setReplyScreen(form);
    	chatlet.alias("getdata");
    	api.performPostInCurrentRoom(chatlet);
  
        
        
        
       
        
    }
    
    @OnAlias(value="getdata")
    public void getdata(TeamchatAPI api)
    {
    	String amountGiven =api.context().currentReply().getField("amount_given");
    	String emailBorrower = api.context().currentReply().getField("email_id_borrower");
    	
    	int amount_given = Integer.parseInt(amountGiven);
    	
    	email = emailBorrower;
    	amountgiven = amount_given;
    	
    	String uname = api.context().currentReply().senderEmail();
    	
    	lender=uname;
    	
    	PollChatlet poll = (PollChatlet) new PollChatlet().setQuestion(uname+" lend you "+amountGiven+".Hit yes to aprove.").alias("pollresult");

    	Room room = api.context().p2p(emailBorrower).post(poll);
    	poll.alias("pollresult");
    	api.perform(room);
    	
    	api.perform( 
                api.context().currentRoom().post(
                        new TextChatlet("Waiting for "+emailBorrower+" to aprove")
                        )
                );
        
    			
    			
    }
    
    @OnAlias(value="pollresult")
    public void pollresult(TeamchatAPI api)
    {
    	String answer = api.context().currentReply().getField("resp");
    	String YES="yes";
    	String NO = "no";
    	
    	if(answer.equals(YES))
    	{
            try{
          		Class.forName("com.mysql.jdbc.Driver");
          		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
          		Statement statement = connect.createStatement();
          		
          		statement.executeUpdate("insert into account_manager values( "+'"'+lender+'"'+" , "+'"'+email+'"'+" , "+amountgiven+" )");
          		
          	}catch(Exception e){System.out.println(e);}

    		
    		Room room = api.context().byId(roomId);
    		room.post(new TextChatlet("amount of "+amountgiven+" aproved by "+email));
    		api.perform(room);
    		
    		
    		
    		api.perform( 
                    api.context().currentRoom().post(
                            new TextChatlet("You aproved the amount of "+amountgiven+" given to you by "+lender)
                            )
                    );
            
            
    	}
    	
    	else if(answer.equals(NO))
    	{
    		Room room = api.context().byId(roomId);
    		room.post(new TextChatlet("amount of "+amountgiven+" rejected by "+email));
    		api.perform(room);

    		api.perform( 
                    api.context().currentRoom().post(
                            new TextChatlet("You rejected the amount of "+amountgiven+" given to you by "+lender)
                            )
                    );
    	}
    }
    
}