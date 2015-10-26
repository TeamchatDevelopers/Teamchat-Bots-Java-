package com.teamchat.integration.quotes;

public class Utility
{
	public static final String help = "<center><img src='http://integration.teamchat.com/sol/bot-images/quotes.jpg' width='150' /></center><h3 style='color:#159ceb'>Hi, I am the Quotes Bot</h3><div></div><div><p>I can fetch you famous quotes and movie quotes on your request. Please click Categories button to select category</p></div><div>You can also use the keyword <b>quote</b> to get the quotes</div>";
	public static final String apiUrl = "https://andruxnet-random-famous-quotes.p.mashape.com/cat=__category";
	public static final String apiKey = "[api-key]";
	public static final String response = "<center><img src='http://integration.teamchat.com/sol/bot-images/quotes.jpg' width='150' /></center>"
						+ "<div><p style='padding-left:5px; padding-right:5px;align:center; margin-top:5px'><b>Quote: </b>__quote<br/><b>Author: </b>__author"
								+ "<br/><b>Category: </b>__category</p></div>";
	public static final String sorry = "<center><img src='http://integration.teamchat.com/sol/bot-images/quotes.jpg' width='150' /></center>"
						+ "<div><p style='padding-left:5px; padding-right:5px;align:center; margin-top:5px'>Sorry! no results.</p></div>";
}
