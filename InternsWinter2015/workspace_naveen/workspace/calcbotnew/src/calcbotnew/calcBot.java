package calcbotnew;

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
import com.teamchat.client.sdk.chatlets.HTML5Chatlet;
import com.teamchat.client.sdk.chatlets.PollChatlet;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
import com.teamchat.client.sdk.chatlets.TakeActionChatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;

import com.teamchat.client.sdk.impl.TeamchatAPIImpl;
import com.teamchat.client.sdk.Expirable;



//import com.teamchat.client.annotations.Expirable;

/**
 * A simple bot which help to maintain transaction/debt
 * among persons on teamchat.
 * 
 * Please remember to update the authkey
 */
public class calcBot {
    public static final String authKey = "c1fe434a365d432bc3ce668d6bbe8abd";

    /**
     * This method creates instance of teamchat client, login using specified 
     * email/password and wait for messages from other users
     **/
    
    
    
    
    
    public static void main(String[] args) {
        TeamchatAPI api = TeamchatAPI.fromFile("config.json")
                .setAuthenticationKey(authKey);

        api.startReceivingEvents(new calcBot());
    }

    /**
     * This method is called when a bot is added in a group or a p2p.
     * This method creates a table named account_manager which stores
     * the information about the transaction amount, lender mail id,
     * and borrower mail id.If the table is already present in 
     * database this method does nothing.
     **/
    
    
 
    
    @OnBotAdded()
    public void add(TeamchatAPI api)
    {
    	//System.out.println("database");
        try{
    		Class.forName("com.mysql.jdbc.Driver");
    		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
    		Statement statement = connect.createStatement();
    		
    		statement.executeUpdate("create table account_manager(email_id_lender varchar(50),email_id_borrower varchar(50),transaction integer)");
    		
    		System.out.println("sddsffd");
    		
    	}catch(Exception e){System.out.println(e);}

    }
    
    /**
     * This method return hello world when hi is choosen from 
     * the help menu.
     **/
    
    
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
    
    /**
     * This method is called when the user choose the option "execute"
     * from the help menu of this bot.
     * This execute method calculates the final balance of each user
     * in the database and returns the final balance in each account.
     * The balance is negative when the user is in debt.
     **/
    
    
    @OnKeyword(value = "execute")
    public void execute(TeamchatAPI api)
    {
    	try{
	      		Class.forName("com.mysql.jdbc.Driver");
	      		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
	      		Statement statement = connect.createStatement();
	     
	      		statement.executeUpdate("create table temp(email_id varchar(50),balance integer)");
	      	}catch(Exception e){System.out.println(e);}

        try{
      		Class.forName("com.mysql.jdbc.Driver");
      		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
      		Statement statement = connect.createStatement();
      		

    		String sql = "select distinct email_id_lender from account_manager order by email_id_lender";
      
      		ResultSet rs = statement.executeQuery(sql);
      		while(rs.next())
      		{
      			String email=rs.getString("email_id_lender");
      	        try{
      	      		Class.forName("com.mysql.jdbc.Driver");
      	      		Connection connect1 = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
      	      		Statement statement1 = connect1.createStatement();
      	     
      	      		ResultSet rs1 = statement1.executeQuery("select sum(transaction) from account_manager where email_id_lender="+'"'+email+'"');
      	      		if(rs1.next())
      	      		{
      	      			int x=rs1.getInt(1);
      	      			statement1.executeUpdate("insert into temp values(" + '"' +email+'"' +","+x+")");
      	      		}
      	      		
      	      	}catch(Exception e){System.out.println(e);}

      		
      		
      		}
      		
      	}catch(Exception e){System.out.println(e);}
        try{
      		Class.forName("com.mysql.jdbc.Driver");
      		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
      		Statement statement = connect.createStatement();
      		

    		String sql = "select distinct email_id_borrower from account_manager order by email_id_borrower";
      
      		ResultSet rs = statement.executeQuery(sql);
      		while(rs.next())
      		{
      			String email=rs.getString("email_id_borrower");
      	        try{
      	      		Class.forName("com.mysql.jdbc.Driver");
      	      		Connection connect1 = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
      	      		Statement statement1 = connect1.createStatement();
      	     
      	      		ResultSet rs1 = statement1.executeQuery("select sum(transaction) from account_manager where email_id_borrower="+'"'+email+'"');
      	      		if(rs1.next())
      	      		{
      	      			int x=rs1.getInt(1);
      	      			x=0-x;
      	      			statement1.executeUpdate("insert into temp values(" + '"' +email+'"' +","+x+")");
      	      		}
      	      		
      	      	}catch(Exception e){System.out.println(e);}

      		
      		
      		}
      		
      	}catch(Exception e){System.out.println(e);}
        
        try{
      		Class.forName("com.mysql.jdbc.Driver");
      		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
      		Statement statement = connect.createStatement();
      		
      		
      		
      		statement.executeUpdate("drop table account_balance");
      		System.out.println("asdg");
      	}catch(Exception e){System.out.println(e);}

        
        try{
      		Class.forName("com.mysql.jdbc.Driver");
      		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
      		Statement statement = connect.createStatement();
     
      		statement.executeUpdate("create table account_balance(email_id varchar(50),balance integer)");
      	}catch(Exception e){System.out.println(e);}

    try{
  		Class.forName("com.mysql.jdbc.Driver");
  		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
  		Statement statement = connect.createStatement();
  		

		String sql = "select distinct email_id from temp order by email_id";
  
  		ResultSet rs = statement.executeQuery(sql);
  		
  		String html="<table style="+'"'+"width:100%"+'"'+">";
  				html=html+"<tr>";
  				html=html+"<td>email id</td>";
  				html=html+"<td>final amount</td>";
  				html=html+"</tr>";
  	  

  		while(rs.next())
  		{
  			String email=rs.getString("email_id");
  	        try{
  	      		Class.forName("com.mysql.jdbc.Driver");
  	      		Connection connect1 = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
  	      		Statement statement1 = connect1.createStatement();
  	     
  	      		ResultSet rs1 = statement1.executeQuery("select sum(balance) from temp where email_id="+'"'+email+'"');
  	      		if(rs1.next())
  	      		{
  	      			int x=rs1.getInt(1);
  	      			statement1.executeUpdate("insert into account_balance values(" + '"' +email+'"' +","+x+")");
  	      			
  	      		
  	      		html=html+"<tr>";
  	      		html=html+"<td>"+email+"</td>";
  	      		html=html+"<td>"+x+"</td>";
  	      		html=html+"</tr>";
  	      		
  	      		}
  	      		
  	      	}catch(Exception e){System.out.println(e);}

  		
  		
  		}
  		
  		api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(html));
  		
  	}catch(Exception e){System.out.println(e);}
    
    try{
  		Class.forName("com.mysql.jdbc.Driver");
  		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
  		Statement statement = connect.createStatement();
  		
  		
  		
  		statement.executeUpdate("drop table temp");
  		statement.executeUpdate("drop table account_balance");
  		System.out.println("asdg");
  	}catch(Exception e){System.out.println(e);}



    }
    
    /**
     * This method is called when the lend option is choosen from the
     * help menu.This method return a form on which the user can fill
     * the mail id of the borrower and the amount of money.The reciever 
     * of the amount has to hit yes to approve which is only one time 
     * reply and after that the poll chatlet is expired.
     * If the reciever approves the amount that amount is inserted 
     * into the database table. 
     **/
    
    @OnKeyword(value = "lend")
    public void lend(TeamchatAPI api) 
    {   
    	String roomId = api.context().currentRoom().getId();
        
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
    	chatlet.setMetaInfo(roomId);
    	chatlet.alias("getdata");
    	api.performPostInCurrentRoom(chatlet);
  
        
        
        
       
        
    }
    
    @OnAlias(value="getdata")
    public void getdata(TeamchatAPI api)
    {
    	String amountGiven =api.context().currentReply().getField("amount_given");
    	String emailBorrower = api.context().currentReply().getField("email_id_borrower");
    	
    	int amount_given = Integer.parseInt(amountGiven);
    	
    	String roomId=api.context().currentChatlet().getMetaInfo();
    	
    	String uname = api.context().currentReply().senderEmail();
    	
    	String metastring = amountGiven+","+emailBorrower+","+uname+","+roomId;
    	
    	Expirable c=(Expirable) api.context().currentChatlet();
		api.perform(api.context().currentRoom().reply(c.expireNow()));
		
    	
    	PollChatlet poll = (PollChatlet) new PollChatlet().setQuestion(uname+" lend you "+amountGiven+".Hit yes to aprove.").alias("pollresult");
    	poll.setMetaInfo(metastring);
    	
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
    	
    	String metastring = api.context().currentChatlet().getMetaInfo();
    	
    	String[] parts = metastring.split(",");
    	
    	String uname=parts[2];
    	String email=parts[1];
    	int amountgiven=Integer.parseInt(parts[0]);
    	String roomId=parts[3];
    	
    	if(answer.equals(YES))
    	{
            try{
          		Class.forName("com.mysql.jdbc.Driver");
          		Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/user_balance","root","ucantcrack@2016");
          		Statement statement = connect.createStatement();
          		
          		
          		
          		statement.executeUpdate("insert into account_manager values( "+'"'+uname+'"'+" , "+'"'+email+'"'+" , "+amountgiven+" )");
          		
          	}catch(Exception e){System.out.println(e);}

    		
    		Room room = api.context().byId(roomId);
    		room.post(new TextChatlet("amount of "+amountgiven+" aproved by "+email));
    		api.perform(room);
    		
    		
    		Expirable c=(Expirable) api.context().currentChatlet();
    		api.perform(api.context().currentRoom().reply(c.expireNow()));
    		
    		api.perform( 
                    api.context().currentRoom().post(
                            new TextChatlet("You aproved the amount of "+amountgiven+" given to you by "+uname)
                            )
                    );
            
            
    	}
    	
    	else if(answer.equals(NO))
    	{
    		Room room = api.context().byId(roomId);
    		room.post(new TextChatlet("amount of "+amountgiven+" rejected by "+email));
    		api.perform(room);

    		Expirable c=(Expirable) api.context().currentChatlet();
    		api.perform(api.context().currentRoom().reply(c.expireNow()));
    		
    		
    		api.perform( 
                    api.context().currentRoom().post(
                            new TextChatlet("You rejected the amount of "+amountgiven+" given to you by "+uname)
                            )
                    );
    	}
    }
    
  
    /**
     * Help Keyword.
     **/
    /*
@OnKeyword(value="help")
public void help(TeamchatAPI api)
{
		TakeActionChatlet chatlet = new TakeActionChatlet();
		chatlet.setActionLabel("Lend").alias("lend");
		chatlet.setActionLabel("Find current balance").alias("execute");

		chatlet.setActionLabel("Hi").alias("Hi");
		api.performPostInCurrentRoom(chatlet);
	}
    	
    */
}