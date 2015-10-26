package com.tc.sol.server.bot.main;

/*
 * *@author : Akshit Sheth
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.teamchat.client.annotations.OnAlias;
import com.teamchat.client.annotations.OnKeyword;
import com.teamchat.client.sdk.Form;
import com.teamchat.client.sdk.TeamchatAPI;
import com.teamchat.client.sdk.chatlets.PrimaryChatlet;

public class StockBot
{
	
	private static Map<String, String> symbolMap = new HashMap<String, String>();
	static {
		symbolMap.put("ACC LIMITED","ACC");
		symbolMap.put("ADANI ENTERPRISES LIMITED","ADANIENT");
		symbolMap.put("ADANI PORTS AND SPECIAL ECONOMIC ZONE LIMITED","ADANIPORTS");
		symbolMap.put("ADANI POWER LIMITED","ADANIPOWER");
		symbolMap.put("ADITYA BIRLA NUVO LIMITED","ABIRLANUVO");
		symbolMap.put("AJANTA PHARMA LIMITED","AJANTPHARM");
		symbolMap.put("ALLAHABAD BANK","ALBK");
		symbolMap.put("AMARA RAJA BATTERIES LIMITED","AMARAJABAT");
		symbolMap.put("AMBUJA CEMENTS LIMITED","AMBUJACEM");
		symbolMap.put("AMTEK AUTO LIMITED","AMTEKAUTO");
		symbolMap.put("ANDHRA BANK","ANDHRABANK");
		symbolMap.put("APOLLO HOSPITALS ENTERPRISE LIMITED","APOLLOHOSP");
		symbolMap.put("APOLLO TYRES LIMITED","APOLLOTYRE");
		symbolMap.put("ARVIND LIMITED","ARVIND");
		symbolMap.put("ASHOK LEYLAND LIMITED","ASHOKLEY");
		symbolMap.put("ASIAN PAINTS LIMITED","ASIANPAINT");
		symbolMap.put("AUROBINDO PHARMA LIMITED","AUROPHARMA");
		symbolMap.put("AXIS BANK LIMITED","AXISBANK");
		symbolMap.put("BAJAJ AUTO LIMITED","BAJAJ-AUTO");
		symbolMap.put("BAJAJ FINANCE LIMITED","BAJFINANCE");
		symbolMap.put("BANK OF BARODA","BANKBARODA");
		symbolMap.put("BANK OF INDIA","BANKINDIA");
		symbolMap.put("BATA INDIA LIMITED","BATAINDIA");
		symbolMap.put("BHARAT ELECTRONICS LIMITED","BEL");
		symbolMap.put("BHARAT FORGE LIMITED","BHARATFORG");
		symbolMap.put("BHARAT HEAVY ELECTRICALS LIMITED","BHEL");
		symbolMap.put("BHARAT PETROLEUM CORPORATION LIMITED","BPCL");
		symbolMap.put("BHARTI AIRTEL LIMITED","BHARTIARTL");
		symbolMap.put("BIOCON LIMITED","BIOCON");
		symbolMap.put("BOSCH LIMITED","BOSCHLTD");
		symbolMap.put("BRITANNIA INDUSTRIES LIMITED","BRITANNIA");
		symbolMap.put("CAIRN INDIA LIMITED","CAIRN");
		symbolMap.put("CANARA BANK","CANBK");
		symbolMap.put("CASTROL INDIA LIMITED","CASTROLIND");
		symbolMap.put("CEAT LIMITED","CEATLTD");
		symbolMap.put("CENTURY TEXTILES & INDUSTRIES LIMITED","CENTURYTEX");
		symbolMap.put("CESC LIMITED","CESC");
		symbolMap.put("CIPLA LIMITED","CIPLA");
		symbolMap.put("COAL INDIA LIMITED","COALINDIA");
		symbolMap.put("COLGATE PALMOLIVE (INDIA) LIMITED","COLPAL");
		symbolMap.put("CROMPTON GREAVES LIMITED","CROMPGREAV");
		symbolMap.put("DABUR INDIA LIMITED","DABUR");
		symbolMap.put("DEWAN HOUSING FINANCE CORPORATION LIMITED","DHFL");
		symbolMap.put("DISH TV INDIA LIMITED","DISHTV");
		symbolMap.put("DIVI'S LABORATORIES LIMITED","DIVISLAB");
		symbolMap.put("DLF LIMITED","DLF");
		symbolMap.put("DR. REDDY'S LABORATORIES LIMITED","DRREDDY");
		symbolMap.put("EICHER MOTORS LIMITED","EICHERMOT");
		symbolMap.put("ENGINEERS INDIA LIMITED","ENGINERSIN");
		symbolMap.put("EXIDE INDUSTRIES LIMITED","EXIDEIND");
		symbolMap.put("GAIL (INDIA) LIMITED","GAIL");
		symbolMap.put("GLENMARK PHARMACEUTICALS LIMITED","GLENMARK");
		symbolMap.put("GMR INFRASTRUCTURE LIMITED","GMRINFRA");
		symbolMap.put("GODREJ INDUSTRIES LIMITED","GODREJIND");
		symbolMap.put("GRASIM INDUSTRIES LIMITED","GRASIM");
		symbolMap.put("HAVELLS INDIA LIMITED","HAVELLS");
		symbolMap.put("HCL TECHNOLOGIES LIMITED","HCLTECH");
		symbolMap.put("HDFC BANK LIMITED","HDFCBANK");
		symbolMap.put("HERO MOTOCORP LIMITED","HEROMOTOCO");
		symbolMap.put("HEXAWARE TECHNOLOGIES LIMITED","HEXAWARE");
		symbolMap.put("HINDALCO INDUSTRIES LIMITED","HINDALCO");
		symbolMap.put("HINDUSTAN PETROLEUM CORPORATION LIMITED","HINDPETRO");
		symbolMap.put("HINDUSTAN UNILEVER LIMITED","HINDUNILVR");
		symbolMap.put("HINDUSTAN ZINC LIMITED","HINDZINC");
		symbolMap.put("HOUSING DEVELOPMENT AND INFRASTRUCTURE LIMITED","HDIL");
		symbolMap.put("HOUSING DEVELOPMENT FINANCE CORPORATION LIMITED","HDFC");
		symbolMap.put("ICICI BANK LIMITED","ICICIBANK");
		symbolMap.put("IDBI BANK LIMITED","IDBI");
		symbolMap.put("IDEA CELLULAR LIMITED","IDEA");
		symbolMap.put("IDFC LIMITED","IDFC");
		symbolMap.put("IFCI LIMITED","IFCI");
		symbolMap.put("INDIABULLS HOUSING FINANCE LIMITED","IBULHSGFIN");
		symbolMap.put("INDIABULLS REAL ESTATE LIMITED","IBREALEST");
		symbolMap.put("INDIAN OIL CORPORATION LIMITED","IOC");
		symbolMap.put("INDIAN OVERSEAS BANK","IOB");
		symbolMap.put("INDRAPRASTHA GAS LIMITED","IGL");
		symbolMap.put("INDUSIND BANK LIMITED","INDUSINDBK");
		symbolMap.put("INFOSYS LIMITED","INFY");
		symbolMap.put("IRB INFRASTRUCTURE DEVELOPERS LIMITED","IRB");
		symbolMap.put("ITC LIMITED","ITC");
		symbolMap.put("JAIN IRRIGATION SYSTEMS LIMITED","JISLJALEQS");
		symbolMap.put("JAIPRAKASH ASSOCIATES LIMITED","JPASSOCIAT");
		symbolMap.put("JAIPRAKASH POWER VENTURES LIMITED","JPPOWER");
		symbolMap.put("JINDAL STEEL & POWER LIMITED","JINDALSTEL");
		symbolMap.put("JSW ENERGY LIMITED","JSWENERGY");
		symbolMap.put("JSW STEEL LIMITED","JSWSTEEL");
		symbolMap.put("JUBILANT FOODWORKS LIMITED","JUBLFOOD");
		symbolMap.put("JUST DIAL LIMITED","JUSTDIAL");
		symbolMap.put("KAVERI SEED COMPANY LIMITED","KSCL");
		symbolMap.put("KOTAK MAHINDRA BANK LIMITED","KOTAKBANK");
		symbolMap.put("L&T FINANCE HOLDINGS LIMITED","L&TFH");
		symbolMap.put("LARSEN & TOUBRO LIMITED","LT");
		symbolMap.put("LIC HOUSING FINANCE LIMITED","LICHSGFIN");
		symbolMap.put("LUPIN LIMITED","LUPIN");
		symbolMap.put("MAHINDRA & MAHINDRA FINANCIAL SERVICES LIMITED","M&MFIN");
		symbolMap.put("MAHINDRA & MAHINDRA LIMITED","M&M");
		symbolMap.put("MARUTI SUZUKI INDIA LIMITED","MARUTI");
		symbolMap.put("MCLEOD RUSSEL INDIA LIMITED","MCLEODRUSS");
		symbolMap.put("MINDTREE LIMITED","MINDTREE");
		symbolMap.put("MOTHERSON SUMI SYSTEMS LIMITED","MOTHERSUMI");
		symbolMap.put("MRF LIMITED","MRF");
		symbolMap.put("NHPC LIMITED","NHPC");
		symbolMap.put("NMDC LIMITED","NMDC");
		symbolMap.put("NTPC LIMITED","NTPC");
		symbolMap.put("OIL & NATURAL GAS CORPORATION LIMITED","ONGC");
		symbolMap.put("OIL INDIA LIMITED","OIL");
		symbolMap.put("ORACLE FINANCIAL SERVICES SOFTWARE LIMITED","OFSS");
		symbolMap.put("ORIENTAL BANK OF COMMERCE","ORIENTBANK");
		symbolMap.put("PAGE INDUSTRIES LIMITED","PAGEIND");
		symbolMap.put("PETRONET LNG LIMITED","PETRONET");
		symbolMap.put("PIDILITE INDUSTRIES LIMITED","PIDILITIND");
		symbolMap.put("POWER FINANCE CORPORATION LIMITED","PFC");
		symbolMap.put("POWER GRID CORPORATION OF INDIA LIMITED","POWERGRID");
		symbolMap.put("PTC INDIA LIMITED","PTC");
		symbolMap.put("PUNJAB NATIONAL BANK","PNB");
		symbolMap.put("RELIANCE CAPITAL LIMITED","RELCAPITAL");
		symbolMap.put("RELIANCE COMMUNICATIONS LIMITED","RCOM");
		symbolMap.put("RELIANCE INDUSTRIES LIMITED","RELIANCE");
		symbolMap.put("RELIANCE INFRASTRUCTURE LIMITED","RELINFRA");
		symbolMap.put("RELIANCE POWER LIMITED","RPOWER");
		symbolMap.put("RURAL ELECTRIFICATION CORPORATION LIMITED","RECLTD");
		symbolMap.put("VEDANTA LIMITED","VEDL");
		symbolMap.put("SHRIRAM TRANSPORT FINANCE COMPANY LIMITED","SRTRANSFIN");
		symbolMap.put("SIEMENS LIMITED","SIEMENS");
		symbolMap.put("SKS MICROFINANCE LIMITED","SKSMICRO");
		symbolMap.put("SRF LIMITED","SRF");
		symbolMap.put("STATE BANK OF INDIA","SBIN");
		symbolMap.put("STEEL AUTHORITY OF INDIA LIMITED","SAIL");
		symbolMap.put("STRIDES ARCOLAB LIMITED","STAR");
		symbolMap.put("SUN PHARMACEUTICALS INDUSTRIES LIMITED","SUNPHARMA");
		symbolMap.put("SUN TV NETWORK LIMITED","SUNTV");
		symbolMap.put("SYNDICATE BANK","SYNDIBANK");
		symbolMap.put("TATA CHEMICALS LIMITED","TATACHEM");
		symbolMap.put("TATA COMMUNICATIONS LIMITED","TATACOMM");
		symbolMap.put("TATA CONSULTANCY SERVICES LIMITED","TCS");
		symbolMap.put("TATA GLOBAL BEVERAGES LIMITED","TATAGLOBAL");
		symbolMap.put("TATA MOTORS LIMITED","TATAMOTORS");
		symbolMap.put("TATA MOTORS LIMITED","TATAMTRDVR");
		symbolMap.put("TATA POWER COMPANY LIMITED","TATAPOWER");
		symbolMap.put("TATA STEEL LIMITED","TATASTEEL");
		symbolMap.put("TECH MAHINDRA LIMITED","TECHM");
		symbolMap.put("THE FEDERAL BANK LIMITED","FEDERALBNK");
		symbolMap.put("THE INDIA CEMENTS LIMITED","INDIACEM");
		symbolMap.put("THE KARNATAKA BANK LIMITED","KTKBANK");
		symbolMap.put("THE SOUTH INDIAN BANK LIMITED","SOUTHBANK");
		symbolMap.put("TITAN COMPANY LIMITED","TITAN");
		symbolMap.put("TVS MOTOR COMPANY LIMITED","TVSMOTOR");
		symbolMap.put("UCO BANK","UCOBANK");
		symbolMap.put("ULTRATECH CEMENT LIMITED","ULTRACEMCO");
		symbolMap.put("UNION BANK OF INDIA","UNIONBANK");
		symbolMap.put("UNITECH LIMITED","UNITECH");
		symbolMap.put("UNITED BREWERIES LIMITED","UBL");
		symbolMap.put("UPL LIMITED","UPL");
		symbolMap.put("VOLTAS LIMITED","VOLTAS");
		symbolMap.put("WIPRO LIMITED","WIPRO");
		symbolMap.put("WOCKHARDT LIMITED","WOCKPHARMA");
		symbolMap.put("YES BANK LIMITED","YESBANK");
		symbolMap.put("ZEE ENTERTAINMENT ENTERPRISES LIMITED","ZEEL");

	}
	
	@OnKeyword("help")
	public void help(TeamchatAPI api)
	{

		Form f = api.objects().form();
		f.addField(api.objects().select().addOption("ACC LIMITED")
				.addOption("ADANI ENTERPRISES LIMITED")
				.addOption("ADANI PORTS AND SPECIAL ECONOMIC ZONE LIMITED")
				.addOption("ADANI POWER LIMITED")
				.addOption("ADITYA BIRLA NUVO LIMITED")
				.addOption("AJANTA PHARMA LIMITED")
				.addOption("ALLAHABAD BANK")
				.addOption("AMARA RAJA BATTERIES LIMITED")
				.addOption("AMBUJA CEMENTS LIMITED")
				.addOption("AMTEK AUTO LIMITED")
				.addOption("ANDHRA BANK")
				.addOption("APOLLO HOSPITALS ENTERPRISE LIMITED")
				.addOption("APOLLO TYRES LIMITED")
				.addOption("ARVIND LIMITED")
				.addOption("ASHOK LEYLAND LIMITED")
				.addOption("ASIAN PAINTS LIMITED")
				.addOption("AUROBINDO PHARMA LIMITED")
				.addOption("AXIS BANK LIMITED")
				.addOption("BAJAJ AUTO LIMITED")
				.addOption("BAJAJ FINANCE LIMITED")
				.addOption("BANK OF BARODA")
				.addOption("BANK OF INDIA")
				.addOption("BATA INDIA LIMITED")
				.addOption("BHARAT ELECTRONICS LIMITED")
				.addOption("BHARAT FORGE LIMITED")
				.addOption("BHARAT HEAVY ELECTRICALS LIMITED")
				.addOption("BHARAT PETROLEUM CORPORATION LIMITED")
				.addOption("BHARTI AIRTEL LIMITED")
				.addOption("BIOCON LIMITED")
				.addOption("BOSCH LIMITED")
				.addOption("BRITANNIA INDUSTRIES LIMITED")
				.addOption("CAIRN INDIA LIMITED")
				.addOption("CANARA BANK")
				.addOption("CASTROL INDIA LIMITED")
				.addOption("CEAT LIMITED")
				.addOption("CENTURY TEXTILES & INDUSTRIES LIMITED")
				.addOption("CESC LIMITED")
				.addOption("CIPLA LIMITED")
				.addOption("COAL INDIA LIMITED")
				.addOption("COLGATE PALMOLIVE (INDIA) LIMITED")
				.addOption("CROMPTON GREAVES LIMITED")
				.addOption("DABUR INDIA LIMITED")
				.addOption("DEWAN HOUSING FINANCE CORPORATION LIMITED")
				.addOption("DISH TV INDIA LIMITED")
				.addOption("DIVI'S LABORATORIES LIMITED")
				.addOption("DLF LIMITED")
				.addOption("DR. REDDY'S LABORATORIES LIMITED")
				.addOption("EICHER MOTORS LIMITED")
				.addOption("ENGINEERS INDIA LIMITED")
				.addOption("EXIDE INDUSTRIES LIMITED")
				.addOption("GAIL (INDIA) LIMITED")
				.addOption("GLENMARK PHARMACEUTICALS LIMITED")
				.addOption("GMR INFRASTRUCTURE LIMITED")
				.addOption("GODREJ INDUSTRIES LIMITED")
				.addOption("GRASIM INDUSTRIES LIMITED")
				.addOption("HAVELLS INDIA LIMITED")
				.addOption("HCL TECHNOLOGIES LIMITED")
				.addOption("HDFC BANK LIMITED")
				.addOption("HERO MOTOCORP LIMITED")
				.addOption("HEXAWARE TECHNOLOGIES LIMITED")
				.addOption("HINDALCO INDUSTRIES LIMITED")
				.addOption("HINDUSTAN PETROLEUM CORPORATION LIMITED")
				.addOption("HINDUSTAN UNILEVER LIMITED")
				.addOption("HINDUSTAN ZINC LIMITED")
				.addOption("HOUSING DEVELOPMENT AND INFRASTRUCTURE LIMITED")
				.addOption("HOUSING DEVELOPMENT FINANCE CORPORATION LIMITED")
				.addOption("ICICI BANK LIMITED")
				.addOption("IDBI BANK LIMITED")
				.addOption("IDEA CELLULAR LIMITED")
				.addOption("IDFC LIMITED")
				.addOption("IFCI LIMITED")
				.addOption("INDIABULLS HOUSING FINANCE LIMITED")
				.addOption("INDIABULLS REAL ESTATE LIMITED")
				.addOption("INDIAN OIL CORPORATION LIMITED")
				.addOption("INDIAN OVERSEAS BANK")
				.addOption("INDRAPRASTHA GAS LIMITED")
				.addOption("INDUSIND BANK LIMITED")
				.addOption("INFOSYS LIMITED")
				.addOption("IRB INFRASTRUCTURE DEVELOPERS LIMITED")
				.addOption("ITC LIMITED")
				.addOption("JAIN IRRIGATION SYSTEMS LIMITED")
				.addOption("JAIPRAKASH ASSOCIATES LIMITED")
				.addOption("JAIPRAKASH POWER VENTURES LIMITED")
				.addOption("JINDAL STEEL & POWER LIMITED")
				.addOption("JSW ENERGY LIMITED")
				.addOption("JSW STEEL LIMITED")
				.addOption("JUBILANT FOODWORKS LIMITED")
				.addOption("JUST DIAL LIMITED")
				.addOption("KAVERI SEED COMPANY LIMITED")
				.addOption("KOTAK MAHINDRA BANK LIMITED")
				.addOption("L&T FINANCE HOLDINGS LIMITED")
				.addOption("LARSEN & TOUBRO LIMITED")
				.addOption("LIC HOUSING FINANCE LIMITED")
				.addOption("LUPIN LIMITED")
				.addOption("MAHINDRA & MAHINDRA FINANCIAL SERVICES LIMITED")
				.addOption("MAHINDRA & MAHINDRA LIMITED")
				.addOption("MARUTI SUZUKI INDIA LIMITED")
				.addOption("MCLEOD RUSSEL INDIA LIMITED")
				.addOption("MINDTREE LIMITED")
				.addOption("MOTHERSON SUMI SYSTEMS LIMITED")
				.addOption("MRF LIMITED")
				.addOption("NHPC LIMITED")
				.addOption("NMDC LIMITED")
				.addOption("NTPC LIMITED")
				.addOption("OIL & NATURAL GAS CORPORATION LIMITED")
				.addOption("OIL INDIA LIMITED")
				.addOption("ORACLE FINANCIAL SERVICES SOFTWARE LIMITED")
				.addOption("ORIENTAL BANK OF COMMERCE")
				.addOption("PAGE INDUSTRIES LIMITED")
				.addOption("PETRONET LNG LIMITED")
				.addOption("PIDILITE INDUSTRIES LIMITED")
				.addOption("POWER FINANCE CORPORATION LIMITED")
				.addOption("POWER GRID CORPORATION OF INDIA LIMITED")
				.addOption("PTC INDIA LIMITED")
				.addOption("PUNJAB NATIONAL BANK")
				.addOption("RELIANCE CAPITAL LIMITED")
				.addOption("RELIANCE COMMUNICATIONS LIMITED")
				.addOption("RELIANCE INDUSTRIES LIMITED")
				.addOption("RELIANCE INFRASTRUCTURE LIMITED")
				.addOption("RELIANCE POWER LIMITED")
				.addOption("RURAL ELECTRIFICATION CORPORATION LIMITED")
				.addOption("VEDANTA LIMITED")
				.addOption("SHRIRAM TRANSPORT FINANCE COMPANY LIMITED")
				.addOption("SIEMENS LIMITED")
				.addOption("SKS MICROFINANCE LIMITED")
				.addOption("SRF LIMITED")
				.addOption("STATE BANK OF INDIA")
				.addOption("STEEL AUTHORITY OF INDIA LIMITED")
				.addOption("STRIDES ARCOLAB LIMITED")
				.addOption("SUN PHARMACEUTICALS INDUSTRIES LIMITED")
				.addOption("SUN TV NETWORK LIMITED")
				.addOption("SYNDICATE BANK")
				.addOption("TATA CHEMICALS LIMITED")
				.addOption("TATA COMMUNICATIONS LIMITED")
				.addOption("TATA CONSULTANCY SERVICES LIMITED")
				.addOption("TATA GLOBAL BEVERAGES LIMITED")
				.addOption("TATA MOTORS LIMITED")
				.addOption("TATA MOTORS LIMITED")
				.addOption("TATA POWER COMPANY LIMITED")
				.addOption("TATA STEEL LIMITED")
				.addOption("TECH MAHINDRA LIMITED")
				.addOption("THE FEDERAL BANK LIMITED")
				.addOption("THE INDIA CEMENTS LIMITED")
				.addOption("THE KARNATAKA BANK LIMITED")
				.addOption("THE SOUTH INDIAN BANK LIMITED")
				.addOption("TITAN COMPANY LIMITED")
				.addOption("TVS MOTOR COMPANY LIMITED")
				.addOption("UCO BANK")
				.addOption("ULTRATECH CEMENT LIMITED")
				.addOption("UNION BANK OF INDIA")
				.addOption("UNITECH LIMITED")
				.addOption("UNITED BREWERIES LIMITED")
				.addOption("UPL LIMITED")
				.addOption("VOLTAS LIMITED")
				.addOption("WIPRO LIMITED")
				.addOption("WOCKHARDT LIMITED")
				.addOption("YES BANK LIMITED")
				.addOption("ZEE ENTERTAINMENT ENTERPRISES LIMITED")
				.label("Stock:").name("Stock"));

		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setQuestionHtml(Utility.help).setReplyScreen(f).setReplyLabel("Enter").alias("getdata");
		api.perform(api.context().currentRoom().post(prime));

	}
	
	@OnKeyword("stock")
	public void stock(TeamchatAPI api)
	{
		help(api);
	}

	// getting the data from glassdoor server
	@OnAlias("getdata")
	public void getdata(TeamchatAPI api) throws IOException
	{

		String stockSymbol = symbolMap.get(api.context().currentReply().getField("Stock"));
		String StockValueFetcherURL = "http://live-nse.herokuapp.com/?symbol=" + stockSymbol;
		try
		{
			String inputLine;
			String respJson = new String();
			
			URL urldemo = new URL(StockValueFetcherURL);
	        URLConnection yc = urldemo.openConnection();
	        
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	                yc.getInputStream()));
	        
	        while ((inputLine = in.readLine()) != null){
	            respJson+=(inputLine);
	        }

	        if (!respJson.contains("Invalid")){
	        	
	        	JSONObject respJSON = new JSONObject(respJson);
	            
	            //System.out.println("Response string is "+respJSON.get("lastUpdateTime"));
	            JSONArray data = respJSON.getJSONArray("data");
	        	
	        	
	            String companyName = data.getJSONObject(0).getString("companyName").replaceAll("&", "and");
	            System.out.println("");
	            String currentPrice = data.getJSONObject(0).getString("lastPrice");
	            String openPrice = data.getJSONObject(0).getString("open");
	            String closePrice;
	            if (data.getJSONObject(0).getString("closePrice").equals("-")){
	            	closePrice =  "NA";
	            }else {
	            	closePrice = data.getJSONObject(0).getString("closePrice");
	            }
	            String avgPrice = data.getJSONObject(0).getString("averagePrice");
	            String dayHighPrice = data.getJSONObject(0).getString("dayHigh");
	            String dayLowPrice = data.getJSONObject(0).getString("dayLow");
	            String Week52High = data.getJSONObject(0).getString("high52");
	            String Week52Low = data.getJSONObject(0).getString("low52");
	            
	            api.perform(api
						.context()
						.currentRoom()
						.post(new PrimaryChatlet().setQuestionHtml("<center><img src='http://integration.teamchat.com/sol/bot-images/nse_190.jpg' width='150' /></center>"
								+ "<div><h4 style='color:#159ceb'>Stock Update for: <b> "+companyName+"</b></h4></div>"
								+ "<div></div>"
								+ "<div><b>Current Price:</b> "+currentPrice+"</div>"
								+ "<div><b>Open Price:</b> "+openPrice+"</div>"
								+ "<div><b>Close Price:</b> "+closePrice+"</div>"
								+ "<div><b>Average Price:</b> "+avgPrice+"</div>"
								+ "<div><b>Day High Price:</b> "+dayHighPrice+"</div>"
								+ "<div><b>Day Low Price:</b> "+dayLowPrice+"</div>"
								+ "<div><b>52 Week High Price:</b> "+Week52High+"</div>"
								+ "<div><b>52 Week Low Price:</b> "+Week52Low+"</div>")));
	            
	        }else{
	        	System.out.println("Invalid stock symbol");
	        }
			
			
			
			
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
