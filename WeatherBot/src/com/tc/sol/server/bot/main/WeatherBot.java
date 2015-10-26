package com.tc.sol.server.bot.main;

/*
 * *@author : Akshit Sheth
 * 
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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

public class WeatherBot
{	
	@OnKeyword("help")
	public void help(TeamchatAPI api)
	{
		Form f = api.objects().form();
		f.addField(api.objects().input()
				.label("City:").name("City"));

		PrimaryChatlet prime = new PrimaryChatlet();
		prime.setQuestionHtml(Utility.help).setReplyScreen(f).setReplyLabel("Search").alias("getdata");
		api.perform(api.context().currentRoom().post(prime));
	}
	
	
	@OnKeyword("weather")
	public void weather(TeamchatAPI api)
	{
		help(api);
	}

	@OnAlias("getdata")
	public void getdata(TeamchatAPI api) throws IOException
	{

		String city = api.context().currentReply().getField("City");
		try
		{
			//String url = "https://ericpeng-weather-v1.p.mashape.com/forecastrss?w="+city;
			String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city;

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("GET");

			// add request header
			/*con.setRequestProperty("X-Mashape-Authorization", "n8khh8NWOBGgBMwq872seL5Aalk8m5GZ");
			con.setRequestProperty("Accept", "application/json");*/

			int responseCode = con.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
			{
				response.append(inputLine);
			}
			
			System.out.println(response);
			JSONObject weatherResponse = new JSONObject(response.toString());
			String main = weatherResponse.getJSONArray("weather").getJSONObject(0).getString("main");
			double tempKelvin = weatherResponse.getJSONObject("main").getDouble("temp");
			double tempCelcius = tempKelvin - 273.15;
			double tempFahrenheit = convertCelsiusToFahrenheit(tempCelcius);
			double pressure = weatherResponse.getJSONObject("main").getDouble("pressure");
			double humidity = weatherResponse.getJSONObject("main").getDouble("humidity");
			double temp_min = weatherResponse.getJSONObject("main").getDouble("temp_min");
			double temp_max = weatherResponse.getJSONObject("main").getDouble("temp_max");
			
			in.close();
			
			api.perform(api
					.context()
					.currentRoom()
					.post(new PrimaryChatlet().setQuestionHtml(Utility.centerTag
							+ getWeatherHtml( city,  main,  tempKelvin,  tempCelcius,  tempFahrenheit,  pressure,  humidity))));

			
		} catch (Exception e)
		{
			api.perform(api
					.context()
					.currentRoom()
					.post(new PrimaryChatlet().setQuestionHtml("<h4 style='color:#159ceb'>Weather Update for: <b> "+city+"</b></h4> - <div>No Such city found</div>")));
		}

	}
	
	private static String getWeatherHtml(String city, String main, double tempKelvin, double tempCelcius, double tempFahrenheit, double pressure, double humidity)
	{
		String html = "<div><h4 style='color:#159ceb'>Weather Update for: <b> "+city+"</b></h4></div>"
				+ "<div></div>"
				+ "<div style='color:#764D4D'><b>Condition:</b> "+main+"</div>"
				+ "<div style='color:#764D4D'><b>Temperature:</b> </div><ul><li>"+(int)tempKelvin+" deg. Kelvin</li><li>"+(int)tempCelcius+" deg. Celcius</li><li>"+(int)tempFahrenheit+" deg. Fahrenheit</li></ul>"
				+ "<div style='color:#764D4D'><b>Pressure:</b> "+pressure+" bar</div>"
				+ "<div style='color:#764D4D'><b>Humidity:</b> "+humidity+" % </div>"
				/*+ "<div style='color:#908080'><b>Min Temperature:</b> "+temp_min+" deg. Kelvin</div>"
				+ "<div style='color:#908080'><b>Max Temperature:</b> "+temp_max+" deg. Kelvin</div>"*/;
		
		return html;
	}
	
	private double convertCelsiusToFahrenheit(double celsius) {
		return ((celsius * 9) / 5) + 32;
	}

}
