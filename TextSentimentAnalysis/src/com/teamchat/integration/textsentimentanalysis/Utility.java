package com.teamchat.integration.textsentimentanalysis;

public class Utility
{
	public static final String help = "<center><img src='http://integration.teamchat.com/sol/bot-images/sentiment-analysis.jpg' width='150' /></center><h3 style='color:#159ceb'>Hi, I am the Sentiment Analysis Bot</h3><div></div><div><p>I can tell you how positive or negative you are feeling. Also if you give me a website url I will do sentiment analysis of the website</p><br/><p>Please click analyse button to start</p></div><div>You can also use the keyword <b>analysis</b> to start analysis</div>";
	public static final String apiUrl = "https://loudelement-free-natural-language-processing-service.p.mashape.com/nlp-text/?text=__text";
	public static final String apiKey = "[api-key]";
	public static final String response = "<center><img src='http://integration.teamchat.com/sol/bot-images/sentiment-analysis.jpg' width='150' /></center>"
						+ "<div><p style='padding-left:5px; padding-right:5px;align:center; margin-top:5px'><b>Entered __CopyHere: </b>__EnteredText<br/><b>Sentiment Text: </b>__SentiText"
								+ "<br/><b>Sentiment Score: </b>__SentiScore</p></div>";
	public static final String sorry = "<center><img src='http://integration.teamchat.com/sol/bot-images/sentiment-analysis.jpg' width='150' /></center>"
						+ "<div><p style='padding-left:5px; padding-right:5px;align:center; margin-top:5px'>Sorry! no results.</p></div>";
	public static final String resp = "<br/><div><p style='padding-left:5px; padding-right:5px;align:center;'>"
			+ "<b>Extracted Content: </b><br/>__ExtractedContent</p></div>";
}

