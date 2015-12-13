/**
 * Database should have 2 tables t1(uploader* varchar(50), course* varchar(10), rating float, count int, fileurl, varchar(50), description* varchar(255))
 * and t1(user* varchar(50), uploader* varchar(50), course* varchar(10), rating int, fileurl varchar(50), description* varchar(255))
 * *->primary keys
**/
package goodSamaritan;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Field;
//import com.teamchat.client.annotations.OnComment;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
//import com.teamchat.client.sdk.chatlets.TextChatlet;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;
//import com.teamchat.client.sdk.chatlets.HTML5Chatlet;
//import com.teamchat.client.sdk.Chatlet;
//import com.teamchat.client.sdk.impl.TeamchatAPIImpl;
//import com.teamchat.client.annotations.OnFileUpload;
//import com.teamchat.client.annotations.Param;
//import com.teamchat.client.utils.FileUploadDetails;
import java.sql.*;
import java.util.Arrays;
import java.util.Comparator;
import java.lang.Float;
import java.lang.Math.*;

public class goodSamaritanBot {
	//api key of the bot
	public static final String authKey = "e18725d77a4d4042c0d3b9ed9af9e773";
	
	//Step1 for handling the databases
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/mybot";

	//Database credentials
	static final String USER = "root";
    static final String PASS = "Cyanide#007";

    //a function to sort a 2D array according to 3rd column (average ratings)
    public static String[][] sortMyArray(String[][] arr)
    {
    	Arrays.sort
    	(
    		arr, new Comparator<String[]>()
    		{
    			@Override
    			public int compare(final String[] entry1, final String[] entry2)
    			{
    				float time1 = Float.valueOf(entry1[2]);
    				float time2 = Float.valueOf(entry2[2]);
    				return (int) java.lang.Math.signum(time2-time1);
    			}
    		}
    	);
    	return arr;
    }
    
    //a function to be used to form the html statement for posting in HTML5 chatlet using the array 'response'
    public static String array2HTML(String[][] array)
	{
		StringBuilder html = new StringBuilder("<table>");
		html.append("<thead>");
		html.append("<th>" + "Row" + "</th>");
		html.append("<th>" + "Course" + "</th>");
		html.append("<th>" + "Description" + "</th>");
		html.append("<th>" + "Rating" + "</th>");
		html.append("<th>" + "Uploaded by" + "</th>");
		html.append("</thead>");
		for(int i = 0; i < array.length; i++)
		{
			
			html.append("<tr>");
			html.append("<td>" + String.valueOf(i+1) + "</td>");
			for(int j = 0 ; j < 4 ; j++)
			{
				if(j!=1)
				{
					html.append("<td>" + array[i][j] + "</td>");
				}
				else
				{
					html.append("<td> <a href='"+array[i][4]+"' target='_blank' download>" + array[i][j] + "</a> </td>");
				}
			}
			
			html.append("</tr>");
		}

		html.append("</table>");
		
		return html.toString();
	
	}
    
    
    public void showTable(TeamchatAPI api, String senderpara, String coursepara)
    {
    	Connection conn = null;
    	Statement stmt = null;
    	try
    	{
    		//STEP 2: Register JDBC driver
    		Class.forName("com.mysql.jdbc.Driver");

    		//STEP 3: Open a connection
    		System.out.println("Connecting to the selected database...");
    		conn = DriverManager.getConnection(DB_URL, USER, PASS);
    		System.out.println("Connection established!");
    		
    		//STEP 4: Execute a query
    		System.out.println("Creating statement...");
    		stmt = conn.createStatement();
    		
    		String searcherID = senderpara;
    		String courseName = coursepara;
    		
    		//.String sql = "UPDATE INTO T3 values (searcherID, course)";
    		//not a necessary step
    		String sql = "INSERT INTO T3 values ('"+searcherID+"', '"+courseName+"') ON DUPLICATE KEY UPDATE searcher='"+searcherID+"', course='"+courseName+"'";
    		stmt.executeUpdate(sql);
    		
    		String sql1;
      		sql1 = "SELECT uploader, course, rating, count, fileurl, description FROM T1 where course='"+courseName+"' ORDER BY rating DESC";
      		ResultSet rs = stmt.executeQuery(sql1);
            //now rs is the table visible to the user
      		rs.last();
            int len = rs.getRow();
            rs.first();
            String[][] response = new String[len][6];
            
            //STEP 5: Extract data from result set
            int i=0;
            while(i<len)
            {
               	//Retrieve by column name
               	String uploader_user = rs.getString("uploader");
               	String course = rs.getString("course");
               	String ave_rating = String.valueOf(rs.getFloat("rating"));
               	String num_user = String.valueOf(rs.getInt("count"));
               	String fileloc = rs.getString("fileurl");
               	String descr1 = rs.getString("description");
				
				response[i][0] = course;
				response[i][1] = descr1;
				response[i][2] = ave_rating;
				response[i][3] = uploader_user;
				response[i][4] = fileloc;
				response[i][5] = num_user;
				
				rs.next();
				i++;
            }
            
            response=sortMyArray(response);
            
            rs.close();
            
            PrimaryChatlet resp = new PrimaryChatlet();
            String htmlstr=array2HTML(response);
            htmlstr="   <style>  "  + 
            		"   * {  "  + 
            		"     -moz-box-sizing: border-box;  "  + 
            		"     -webkit-box-sizing: border-box;  "  + 
            		"     box-sizing: border-box;  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   table {  "  + 
            		"     border-collapse: separate;  "  + 
            		"     background: #fff;  "  + 
            		"     -moz-border-radius: 5px;  "  + 
            		"     -webkit-border-radius: 5px;  "  + 
            		"     border-radius: 5px;  "  + 
            		"     margin: 50px auto;  "  + 
            		"     -moz-box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);  "  + 
            		"     -webkit-box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);  "  + "     box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   thead {  "  + 
            		"     -moz-border-radius: 5px;  "  + 
            		"     -webkit-border-radius: 5px;  "  + 
            		"     border-radius: 5px;  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   thead th {  "  + 
            		"     font-family: 'Segoe UI';  "  + 
            		"     font-weight: 400;  "  + 
            		"     color: #000000;  "  + 
            		"     text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);  "  + 
            		"     text-align: center;  "  + 
            		"     padding: 10px;  "  + 
            		"     background-size: 100%;  "  + 
            		"     background-image: linear-gradient(#66ff66,#33ff33);  "  + 
            		"     background-color: rgb(77, 255, 77);  "  + 
            		"     border-top: 1px solid #858d99;  "  + 
            		"   }  "  + 
            		"   thead th:first-child {  "  + 
            		"     -moz-border-radius-topleft: 5px;  "  + 
            		"     -webkit-border-top-left-radius: 5px;  "  + 
            		"     border-top-left-radius: 5px;  "  + 
            		"   }  "  + 
            		"   thead th:last-child {  "  + 
            		"     -moz-border-radius-topright: 5px;  "  + 
            		"     -webkit-border-top-right-radius: 5px;  "  + 
            		"     border-top-right-radius: 5px;  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   tbody tr td {  "  + 
            		"     font-family: 'Open Sans', sans-serif;  "  + 
            		"     font-weight: 400;  "  + 
            		"     color: #5f6062;  "  + 
            		"     padding: 10px 10px 10px 10px;  "  + 
            		"     border-bottom: 1px solid #e0e0e0;  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   tbody tr:nth-child(2n) {  "  + 
            		"     background: #f0f3f5;  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   tbody tr:last-child td {  "  + 
            		"     border-bottom: none;  "  + 
            		"   }  "  + 
            		"   tbody tr:last-child td:first-child {  "  + 
            		"     -moz-border-radius-bottomleft: 5px;  "  + 
            		"     -webkit-border-bottom-left-radius: 5px;  "  + 
            		"     border-bottom-left-radius: 5px;  "  + 
            		"   }  "  + 
            		"   tbody tr:last-child td:last-child {  "  + 
            		"     -moz-border-radius-bottomright: 5px;  "  + 
            		"     -webkit-border-bottom-right-radius: 5px;  "  + 
            		"     border-bottom-right-radius: 5px;  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   tbody:hover > tr td {  "  + 
            		"     filter: progid:DXImageTransform.Microsoft.Alpha(Opacity=50);  "  + 
            		"     opacity: 0.5;  "  + 
            		"     @include text-shadow(0px 0px 2px rgba(0,0,0,0.8));*/  "  + 
            		"   }  "  + 
            		"     "  + 
            		"   tbody:hover > tr:hover td {  "  + 
            		"     text-shadow: none;  "  + 
            		"     color: #2d2d2d;  "  + 
            		"     filter: progid:DXImageTransform.Microsoft.Alpha(enabled=false);  "  + 
            		"     opacity: 1;  "  + 
            		"   }  "  + 
            		" </style>  " + htmlstr;
            resp.setQuestionHtml(htmlstr);
            resp.setMetaInfo(courseName);
            // Create reply form for this primary chatlet and add fields to it to allow rating the notes
    		Form f2 = api.objects().form();
    		
    		Field serial = api.objects().select().name("row").label("Row").addRegexValidation(".+", "No row selected!");
    		int j=1;
    		String jstr="";
    		for(j=1; j<=len; j++)
    		{
    			jstr=String.valueOf(j);
    			serial.addOption(jstr);
    		}
    		
    		//a dropdown to select S. No. or row
    		f2.addField(serial);
    		
    		// a dropdown to select rating
    		f2.addField(api.objects().select().name("rating").label("Rating").addRegexValidation(".+", "No rating given!")
    				.addOption("1").addOption("2").addOption("3")
    				.addOption("4").addOption("5").addOption("6")
    				.addOption("7").addOption("8").addOption("9")
    				.addOption("10")
    				);
    		
    		// finally add this Form to primary chatlet as replyscreen
    		resp.setReplyScreen(f2);
    		
    		// Decide if replies will be visible to everyone in the room
    		// Also, label can be changed
    		resp.showDetails(true);
    		resp.setDetailsLabel("users replied");

    		// Decide if commenting is enabled on this chatlet or not
    		resp.allowComments(true);

    		// Can change reply button label
    		resp.setReplyLabel("Rate");
    		
    		// Can assign an alias, if you want to handle replies on this chatlet
    		// The method with @OnAlias annotation will be called, when anyone
    		// replies
    		// to this chatlet. For example, look at method handleReply in this
    		// class
    		// it has annotation @OnAlias("profile"), which we are setting below.
    		resp.alias("rateNotes");
            
    		// finally post the chatlet in the current room
        	api.performPostInCurrentRoom(resp);
        }
    	catch(SQLException se)
    	{
    		//Handle errors for JDBC
    		se.printStackTrace();
    	}
    	catch(Exception e)
    	{
    		//Handle errors for Class.forName
    		e.printStackTrace();
    	}
    	finally
    	{
    		//finally block used to close resources
    		try
    		{
    			if(stmt!=null)
    				conn.close();
            }
    		catch(SQLException se)
    		{
    		}// do nothing
    		try
    		{
    			if(conn!=null)
    				conn.close();
            }
    		catch(SQLException se)
    		{
    			se.printStackTrace();
            }//end finally try
        }//end try
        	
    	// email of the user who searched for notes
    	System.out.println("Query by: "+senderpara);
    }
    
    /**
     * This method creates instance of teamchat client, login using specified 
     * email/password and wait for messages from other users
     **/
    public static void main(String[] args)
    {
    	
    	TeamchatAPI api = TeamchatAPI.fromFile("config.json").setAuthenticationKey(authKey);

        api.startReceivingEvents(new goodSamaritanBot());
    }
    
    /**
     *This method shows a chatlet which allows users to upload notes for a particular course 
    **/
    //@OnKeyword(isCaseSensitive=false, value="upload", description="To upload notes")
    public void uploadnotes(TeamchatAPI api)
    {    	
    	PrimaryChatlet p = new PrimaryChatlet();
    	
    	// Set the question for the chatlet
    	p.setQuestion("Please upload your notes");
    	
    	// Create reply form for this primary chatlet and add fields to it
    	Form f = api.objects().form();
    	
    	// a dropdown to select course
    	f.addField(
    		api.objects().select().name("course").label("Course").addRegexValidation(".+", "No course selected!").addOption("CS 207")
    		.addOption("CS 213").addOption("CS 215").addOption("CS 251").addOption("EE 101")
    	);
    	
    	f.addField
    	(
    		api.objects().input().name("description").addRegexValidation(".+", "No description given!").label("Description")
    	);
    	
    	// a file field with size in bytes
    	f.addField
    	(
    		api.objects().attachFile("10000000", "*").name("myfile").label("File").addRegexValidation(".+", "No file selected!")
    	);
    		
    	// finally add this Form to primary chatlet as replyscreen
    	p.setReplyScreen(f);
    	
    	// Decide if replies will be visible to everyone in the room
    	// Also, label can be changed
    	p.showDetails(true);
    	p.setDetailsLabel("users replied");

    	// Decide if commenting is enabled on this chatlet or not
    	p.allowComments(true);

    	// Can change reply button label
    	p.setReplyLabel("Upload");
    		
    	// Can assign an alias, if you want to handle replies on this chatlet
    	// The method with @OnAlias annotation will be called, when anyone
    	// replies
    	// to this chatlet. For example, look at method handleReply in this
    	// class
    	// it has annotation @OnAlias("profile"), which we are setting below.
    	p.alias("uploadNotes");
    	
    	// finally post the chatlet in the current room
        api.performPostInCurrentRoom(p);
    }
    
    /**
	 *This method prints the replies to the chatlet that was posted in the
	 *create method.
	**/
	
    @OnAlias("uploadNotes")
	public void handleReply(TeamchatAPI api)
	{	
    	Connection conn = null;
    	Statement stmt = null;
    	try
    	{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to the selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection established!");
            
            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
            String uploaderID=api.context().currentReply().senderEmail();
            String courseName = api.context().currentReply().getField("course");
            String descr = api.context().currentReply().getField("description");
            String fileloc = api.context().currentReply().getFileUploadDetails("myfile").getFileUrl();
            
            //.String sql = "UPDATE INTO T1 values ('"+uploaderID+"', course, 0.0, 0, fileurl, description)";
            String sql = "INSERT INTO T1 values ('"+uploaderID+"', '"+courseName+"', 0.0, 0, '"+fileloc+"', '"+descr+"') ON DUPLICATE KEY UPDATE uploader='"+uploaderID+"', course='"+courseName+"', rating=0.0, count=0, fileurl='"+fileloc+"', description='"+descr+"'";
            
            stmt.executeUpdate(sql);

        }
    	catch(SQLException se)
    	{
            //Handle errors for JDBC
            se.printStackTrace();
        }
    	catch(Exception e)
    	{
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    	finally
    	{
            //finally block used to close resources
            try
            {
               if(stmt!=null)
                  conn.close();
            }
            catch(SQLException se)
            {
            }// do nothing
            try
            {
               if(conn!=null)
                  conn.close();
            }
            catch(SQLException se)
            {
               se.printStackTrace();
            }//end finally try
        }//end try
    	
    	// email of the user who uploaded the file
		System.out.println("Uploaded by: "+api.context().currentReply().senderEmail());
	}
    
    /***
     *shows a chatlet which asks for course name and then 
     *shows all the uploaded notes under that course along with their rating and a rate option  
    **/
    //@OnKeyword(isCaseSensitive=false, value="search", description="To search the notes uploaded under a specific course and rate them")
    public void ratenotes(TeamchatAPI api)
    {	
    	PrimaryChatlet p1 = new PrimaryChatlet();
    	
    	// Set the question for the chatlet
		p1.setQuestion("Please select a course...");
		
		// Create reply form for this primary chatlet and add fields to it
		Form f1 = api.objects().form();
		
		// a dropdown to select course
		f1.addField(api.objects().select().name("courseOpt").label("Course").addRegexValidation(".+", "No course selected!")
				.addOption("CS 207").addOption("CS 213").addOption("CS 215")
				.addOption("CS 251").addOption("EE 101")
				);
		
		// finally add this Form to primary chatlet as replyscreen
		p1.setReplyScreen(f1);
		
		// Decide if replies will be visible to everyone in the room
		// Also, label can be changed
		p1.showDetails(true);
		p1.setDetailsLabel("users replied");

		// Decide if commenting is enabled on this chatlet or not
		p1.allowComments(true);

		// Can change reply button label
		p1.setReplyLabel("Select Course");
		
		// Can assign an alias, if you want to handle replies on this chatlet
		// The method with @OnAlias annotation will be called, when anyone
		// replies
		// to this chatlet. For example, look at method handleReply in this
		// class
		// it has annotation @OnAlias("profile"), which we are setting below.
		p1.alias("findNotes");
    	
		// finally post the chatlet in the current room
    	api.performPostInCurrentRoom(p1);
    }
    
    @OnAlias("findNotes")
    public void handleReply1(TeamchatAPI api)
    {
    	String sender=api.context().currentReply().senderEmail();
    	String course=api.context().currentReply().getField("courseOpt");
    	showTable(api, sender, course);
    }
    
    @OnAlias("rateNotes")
    public void handleReply2(TeamchatAPI api)
    {
    	Connection conn = null;
    	Statement stmt = null;
    	try
    	{
    		//STEP 2: Register JDBC driver
    		Class.forName("com.mysql.jdbc.Driver");

    		//STEP 3: Open a connection
    		System.out.println("Connecting to the selected database...");
    		conn = DriverManager.getConnection(DB_URL, USER, PASS);
    		System.out.println("Connection established!");
    		
    		//STEP 4: Execute a query
    		System.out.println("Creating statement...");
    		stmt = conn.createStatement();
    		
    		String raterID = api.context().currentReply().senderEmail();
    		String row = api.context().currentReply().getField("row");
    		String strRatingGiven = api.context().currentReply().getField("rating");
    		String courseSelected=api.context().currentChatlet().getMetaInfo();
    		
    		String sql2;
      		sql2 = "SELECT uploader, course, rating, count, fileurl, description FROM T1 where course='"+courseSelected+"' ORDER BY rating DESC";
      		ResultSet rs1 = stmt.executeQuery(sql2);
      		//////////////////////////////////////////////////////////////////////////

      		while(rs1.next())
      		{
      			
      			System.out.println(rs1.getString("description"));
      		}
      		//////////////////////////////////////////////////////////////////////////
      		//here rs1 is the table visible to user
      		Integer numrow = Integer.valueOf(row);
      		Integer ratingGiven = Integer.valueOf(strRatingGiven);
            rs1.absolute(numrow);
            //credentials of the file which the user wants to rate, using rs1
            String uploader_user1 = rs1.getString("uploader");
           	String course1 = rs1.getString("course");
           	float ave_rating1 = rs1.getFloat("rating");
           	Integer num_users = rs1.getInt("count");
           	String fileloc1 = rs1.getString("fileurl");
           	String descr2 = rs1.getString("description");
           	rs1.close();
           	
            boolean ratedBefore=false;
            String sql3;
            sql3 = "SELECT user, uploader, course, rating, fileurl, description FROM T2 where user='"+raterID+"' and uploader='"+uploader_user1+"' and course='"+course1+"' and description= '"+descr2+"'";
            ResultSet rs2 = stmt.executeQuery(sql3);
            ratedBefore=rs2.first();
            
            //to be used for updating T1;
            String sql4;
            //to be used for updating T2;
            String sql5="INSERT INTO T2 values ('"+raterID+"', '"+uploader_user1+"', '"+course1+"', "+ratingGiven+", '"+fileloc1+"', '"+descr2+"') ON DUPLICATE KEY UPDATE user= '"+raterID+"', uploader='"+uploader_user1+"', course='"+course1+"', rating="+ratingGiven+", fileurl='"+fileloc1+"', description='"+descr2+"'";
            //to find the old rating given by raterID to uploader_user1 for descr2 of course1
            
            if(!ratedBefore)
            {
            	//not rated before
            	float newAveRating1=(ave_rating1*num_users+ratingGiven)/(num_users+1);
            	sql4="update T1 set rating="+newAveRating1+", count=count+1 where uploader= '"+uploader_user1+"' and course= '"+course1+"' and description= '"+descr2+"'";
            }
            else
            {
            	//rated before
            	int oldRating=rs2.getInt("rating");
            	float newAveRating2=(ave_rating1*num_users+ratingGiven-oldRating)/(num_users);
            	sql4="update T1 set rating="+newAveRating2+" where uploader= '"+uploader_user1+"' and course= '"+course1+"' and description= '"+descr2+"'";
            }
            
            stmt.executeUpdate(sql4);
            stmt.executeUpdate(sql5);
            showTable(api,raterID,courseSelected);
        }
    	catch(SQLException se)
    	{
    		//Handle errors for JDBC
    		se.printStackTrace();
    	}
    	catch(Exception e)
    	{
    		//Handle errors for Class.forName
    		e.printStackTrace();
    	}
    	finally
    	{
    		//finally block used to close resources
    		try
    		{
    			if(stmt!=null)
    				conn.close();
            }
    		catch(SQLException se)
    		{
    		}// do nothing
    		try
    		{
    			if(conn!=null)
    				conn.close();
            }
    		catch(SQLException se)
    		{
    			se.printStackTrace();
            }//end finally try
        }//end try
    	// email of the user who searched for notes
    	System.out.println("Rated by: "+api.context().currentReply().senderEmail());
    }
    
    @OnKeyword(isCaseSensitive=false, value="help")
    public void helpMenu(TeamchatAPI api)
    {
    	PrimaryChatlet p4 = new PrimaryChatlet();
    	String s1 = "";
    		s1= "   <style>  "  + 
        		"   * {  "  + 
        		"     -moz-box-sizing: border-box;  "  + 
        		"     -webkit-box-sizing: border-box;  "  + 
        		"     box-sizing: border-box;  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   table {  "  + 
        		"     border-collapse: separate;  "  + 
        		"     background: #fff;  "  + 
        		"     -moz-border-radius: 5px;  "  + 
        		"     -webkit-border-radius: 5px;  "  + 
        		"     border-radius: 5px;  "  + 
        		"     margin: 50px auto;  "  + 
        		"     -moz-box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);  "  + 
        		"     -webkit-box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);  "  + "     box-shadow: 0px 0px 5px rgba(0, 0, 0, 0.3);  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   thead {  "  + 
        		"     -moz-border-radius: 5px;  "  + 
        		"     -webkit-border-radius: 5px;  "  + 
        		"     border-radius: 5px;  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   thead th {  "  + 
        		"     font-family: 'Segoe UI';  "  + 
        		"     font-weight: 400;  "  + 
        		"     color: #000000;  "  + 
        		"     text-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3);  "  + 
        		"     text-align: center;  "  + 
        		"     padding: 10px;  "  + 
        		"     background-size: 100%;  "  + 
        		"     background-image: linear-gradient(#66ff66,#33ff33);  "  + 
        		"     background-color: rgb(77, 255, 77);  "  + 
        		"     border-top: 1px solid #858d99;  "  + 
        		"   }  "  + 
        		"   thead th:first-child {  "  + 
        		"     -moz-border-radius-topleft: 5px;  "  + 
        		"     -webkit-border-top-left-radius: 5px;  "  + 
        		"     border-top-left-radius: 5px;  "  + 
        		"   }  "  + 
        		"   thead th:last-child {  "  + 
        		"     -moz-border-radius-topright: 5px;  "  + 
        		"     -webkit-border-top-right-radius: 5px;  "  + 
        		"     border-top-right-radius: 5px;  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   tbody tr td {  "  + 
        		"     font-family: 'Open Sans', sans-serif;  "  + 
        		"     font-weight: 400;  "  + 
        		"     color: #5f6062;  "  + 
        		"     padding: 10px 10px 10px 10px;  "  + 
        		"     border-bottom: 1px solid #e0e0e0;  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   tbody tr:nth-child(2n) {  "  + 
        		"     background: #f0f3f5;  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   tbody tr:last-child td {  "  + 
        		"     border-bottom: none;  "  + 
        		"   }  "  + 
        		"   tbody tr:last-child td:first-child {  "  + 
        		"     -moz-border-radius-bottomleft: 5px;  "  + 
        		"     -webkit-border-bottom-left-radius: 5px;  "  + 
        		"     border-bottom-left-radius: 5px;  "  + 
        		"   }  "  + 
        		"   tbody tr:last-child td:last-child {  "  + 
        		"     -moz-border-radius-bottomright: 5px;  "  + 
        		"     -webkit-border-bottom-right-radius: 5px;  "  + 
        		"     border-bottom-right-radius: 5px;  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   tbody:hover > tr td {  "  + 
        		"     filter: progid:DXImageTransform.Microsoft.Alpha(Opacity=50);  "  + 
        		"     opacity: 0.5;  "  + 
        		"     @include text-shadow(0px 0px 2px rgba(0,0,0,0.8));*/  "  + 
        		"   }  "  + 
        		"     "  + 
        		"   tbody:hover > tr:hover td {  "  + 
        		"     text-shadow: none;  "  + 
        		"     color: #2d2d2d;  "  + 
        		"     filter: progid:DXImageTransform.Microsoft.Alpha(enabled=false);  "  + 
        		"     opacity: 1;  "  + 
        		"   }  "  + 
        		" </style>  " +
        		"<table>" +
        		"<thead>" +
        		"<th>Action</th>" +
        		"<th>Description</th>" +
        		"</thead>" +
        		"<tr>" +
        		"<td>Upload Notes</td>" +
        		"<td>Choose this option to upload your notes under the course of your choice</td>" +
        		"</tr>" +
        		"<td>Search Notes</td>" +
        		"<td>Choose this option to search the notes of a particular course</td>" +
        		"</tr>" +
        		"</table>";
        p4.setQuestionHtml(s1);
        
        Form f3=api.objects().form();
        f3.addField
        (
        		api.objects().select().name("action").label("Action").addRegexValidation(".+", "No action selected!")
        		.addOption("Upload Notes").addOption("Search Notes")
        );
        
        // finally add this Form to primary chatlet as replyscreen
        p4.setReplyScreen(f3);
     		
        // Decide if replies will be visible to everyone in the room
        // Also, label can be changed
        p4.showDetails(true);
        p4.setDetailsLabel("users replied");

        // Decide if commenting is enabled on this chatlet or not
        p4.allowComments(true);

        // Can change reply button label
        p4.setReplyLabel("Reply");
     		
        // Can assign an alias, if you want to handle replies on this chatlet
        // The method with @OnAlias annotation will be called, when anyone
        // replies
        // to this chatlet. For example, look at method handleReply in this
        // class
        // it has annotation @OnAlias("profile"), which we are setting below.
    	p4.alias("helpReply");
    	api.performPostInCurrentRoom(p4);
    }
    @OnAlias("helpReply")
    public void handleReply4(TeamchatAPI api)
    {
    	String actionstr= api.context().currentReply().getField("action");
    	if(actionstr.compareTo("Upload Notes")==0)
    	{
    		uploadnotes(api);
    	}
    	else if (actionstr.compareTo("Search Notes")==0)
    	{
    		ratenotes(api);
    	}
    }
    /**
     * This method posts Hello World message, when any user posts hi message to
     * the logged in user (see main method)
     **/
    
    /*
    @OnKeyword(isCaseSensitive=false, value="hi")
    public void HelloWorld(TeamchatAPI api)
    {
    	Connection conn = null;
    	Statement stmt = null;
    	try
    	{
            //STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");

            //STEP 3: Open a connection
            System.out.println("Connecting to the selected database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection established!");
            
            //STEP 4: Execute a query
            System.out.println("Creating statement...");
            stmt = conn.createStatement();
            
            //choose csv file
            //.String sql = "UPDATE INTO T4 values ('values from csv file')";
            String sql5 = "INSERT INTO T4 values ('')";
            
            stmt.executeUpdate(sql5);

        }
    	catch(SQLException se)
    	{
            //Handle errors for JDBC
            se.printStackTrace();
        }
    	catch(Exception e)
    	{
            //Handle errors for Class.forName
            e.printStackTrace();
        }
    	finally
    	{
            //finally block used to close resources
            try
            {
               if(stmt!=null)
                  conn.close();
            }
            catch(SQLException se)
            {
            }// do nothing
            try
            {
               if(conn!=null)
                  conn.close();
            }
            catch(SQLException se)
            {
               se.printStackTrace();
            }//end finally try
        }//end try
    	
    	// email of the user who uploaded the file
		System.out.println("Courses added by: "+api.context().currentReply().senderEmail());
    	
    }
    */
    
    /*
    @OnKeyword("abcd")
    public void HelloWorld1(TeamchatAPI api) {
    	PollChatlet mychat = new PollChatlet();
        mychat.setQuestion("this is a ques");
        mychat.alias("poll");
        api.performPostInCurrentRoom(mychat);
    }
    
    @OnComment("poll")
    public void onacomment(TeamchatAPI api){
    	api.perform(
    			api.context().currentRoom().post(
    					new TextChatlet("a comment was made in a poll")
    					)
    			);
    }
    */
}