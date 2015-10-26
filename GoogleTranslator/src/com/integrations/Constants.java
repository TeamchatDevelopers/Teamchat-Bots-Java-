package com.integrations;

public class Constants
{
	public static final String resultHtml = "<style type='text/css'> .tg {border-collapse:collapse;border-spacing:0;} .tg td{font-family:Arial, sans-serif;font-size:14px;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;} .tg th{font-family:Arial, sans-serif;font-size:14px;font-weight:normal;padding:10px 5px;border-style:solid;border-width:1px;overflow:hidden;word-break:normal;} </style> <table class='tg'> <tr> <th class='tg-031e'></th> <th class='tg-031e'>Annual</th> <th class='tg-031e'>Per Pay Period</th> </tr> <tr> <td class='tg-031e'>Amount</td> <td class='tg-031e'>_amount1_</td> <td class='tg-031e'>_amount2_</td> </tr> <tr> <td class='tg-031e'>Fica</td> <td class='tg-031e'>_fica1_</td> <td class='tg-031e'>_fica2_</td> </tr> <tr> <td class='tg-031e'>Federal</td> <td class='tg-031e'>_fed1_</td> <td class='tg-031e'>_fed2_</td> </tr> </table>";
	public static final String url = "https://www.googleapis.com/language/translate/v2?key=";
	public static final String apikey = "Put your api key here";
	public static final String html = "<center><img src='http://integration.teamchat.com/sol/bot-images/googletranslate.jpg' width='150' /></center><h3 style='color:#159ceb'>Hi, I am the Google Translate Bot</h3><div>I will help you translate any text from & to any language. Just type in the text you need to translate along with the languages.</div><div><p>Click on Translate button to start</p></div>";

	public static String translateString = "<center><img src='http://integration.teamchat.com/sol/bot-images/googletranslate.jpg' width='150' /></center>" + "<p></p><div> <b>%s</b> : %s</div><div style='margin-top:5px;'><b>%s</b> : %s</div></div>";

}
