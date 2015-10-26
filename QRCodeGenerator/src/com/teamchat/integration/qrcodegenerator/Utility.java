package com.teamchat.integration.qrcodegenerator;

public class Utility
{
	public static final String help = "<center><img src='http://integration.teamchat.com/sol/bot-images/qr-code.jpg' width='150' /></center><h3 style='color:#159ceb'>Hi, I am the QR Code Generator Bot </h3><div></div><div><p>I will generate the QR Code for the entered text/url</p><p>Please click 'Enter' button to generate</p></div><div>You can also use the keyword <b>qrcode</b> to generate QR Code</div>";
	public static final String apiUrl = "https://sjehutch-barcode-generator.p.mashape.com/qrcode/6/6/?text=__text";
	public static final String apiKey = "api key";
	public static final String sorry = "<center><img src='http://integration.teamchat.com/sol/bot-images/qr-code.jpg' width='150' /></center>"
						+ "<div><p style='padding-left:5px; padding-right:5px;align:center; margin-top:5px'>Sorry! no results.</p></div>";
	public static final String resp = "<div><center><img src='http://integration.teamchat.com/sol/bot-images/qr-code.jpg' width='150' /></center></div>"
						+ "<div><b>Your QR Code Image: </b></div><div><center><img style = 'width:100%;height:250px;' src='__fileUrl'/></center></div><br/><div><center><a href='__fileUrl'>Click here to download the image</a></center></div>";
	public static final String path = "";
	public static final String server = "";
	
	public static final String htmlLink = "{\"content\":{\"text\":\"QR Code\",\"html\":\"__html\"},\"type\":\"primary\",\"actions\":{\"actitem\":[]},\"displayname\":\"HTML5\",\"xslt\":\"primary-2.7.2\",\"minCrv\":\"3\",\"comments\":{\"addcomment\":\"true\"}}";
}
