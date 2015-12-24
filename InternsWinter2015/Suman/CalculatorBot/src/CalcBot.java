//Suman Swaroop (intern)

import com.teamchat.client.annotations.OnKeyWordWithParam;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.annotations.Param;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.HTML5Chatlet;
import com.teamchat.client.sdk.chatlets.TextChatlet;

import javax.script.ScriptEngineManager;
import javax.script.ScriptEngine;
public class CalcBot {
	//Auth key of bot
	public static final String authKey = "";
	
	
	public static void main(String[] args) {
		
		TeamchatAPI api = TeamchatAPI.fromFile("config.json").setAuthenticationKey(authKey);
		api.startReceivingEvents(new CalcBot());
		
		
	}
	
	@OnKeyWordWithParam("Calc")
	public void calculate(TeamchatAPI api, @Param String exp){
		
		try{
		
			
			
			ScriptEngineManager mgr = new ScriptEngineManager();
			ScriptEngine engine = mgr.getEngineByName("JavaScript");
			
			String exp2=exp.replaceAll("[a-z]{3,10}","Math.$0");
			
			System.out.println(exp2);
			
			
			String answer = engine.eval(exp2).toString();
			
			api.performPostInCurrentRoom(new TextChatlet(answer));
			System.out.println(answer);
			
			
		
			
		}catch(Exception e){
			api.performPostInCurrentRoom(new TextChatlet("Wrong Expression. Please see help"));
		}

	}
	
	@OnKeyword("help")
	public void help(TeamchatAPI api){
		String htmlconvert= "<table><tr><th style='width:20%'>Method</th><th>Description</th></tr><tr><td>abs(x)</a></td><td>Returns the absolute value of x</td>"+
  "</tr><tr><td>acos(x)</a></td><td>Returns the arccosine of x, in radians</td></tr><tr><td>asin(x)</a></td>    <td>Returns the arcsine of x, in radians</td>"+
  "</tr><tr><td>atan(x)</a></td><td>Returns the arctangent of x as a numeric value between-PI/2 and PI/2 radians</td></tr><tr><td>atan2(y,x)</a></td>"+
   "<td>Returns the arctangent of the quotient of its arguments</td>"+
  "</tr><tr><td>ceil(x)</a></td><td>Returns x, rounded upwards to the nearest integer</td></tr>"+
  "<tr><td>cos(x)</a></td><td>Returns the cosine of x (x is in radians)</td></tr>"+
  "<tr><td>exp(x)</a></td><td>Returns the value of E<sup>x</sup></td></tr>"+
  "<tr><td>floor(x)</a></td><td>Returns x, rounded downwards to the nearest integer</td></tr>"+
  "<tr><td>log(x)</a></td><td>Returns the natural logarithm (base E) of x</td></tr>"+
  "<tr><td>max(x,y,z,...,n)</a></td><td>Returns the number with the highest value</td></tr>"+
  "<tr><td>min(x,y,z,...,n)</a></td><td>Returns the number with the lowest value</td></tr>"+
  "<tr><td>pow(x,y)</a></td><td>Returns the value of x to the power of y</td></tr>"+
  "<tr><td>random()</a></td><td>Returns a random number between 0 and 1</td></tr>"+
  "<tr><td>round(x)</a></td><td>Rounds x to the nearest integer</td></tr>"+
  "<tr><td>sin(x)</a></td><td>Returns the sine of x (x is in radians)</td></tr>"+
  "<tr><td>sqrt(x)</a></td><td>Returns the square root of x</td></tr>"+
  "<tr><td>tan(x)</a></td><td>Returns the tangent of an angle</td></tr>"+
  "</table>";
		
		htmlconvert = htmlconvert + "<h4>Type 'Calc mathematical expression' </h4> <br>E.g: calc cos(4)";
		api.performPostInCurrentRoom(new HTML5Chatlet().setQuestionHtml(htmlconvert));
	
	}

}
