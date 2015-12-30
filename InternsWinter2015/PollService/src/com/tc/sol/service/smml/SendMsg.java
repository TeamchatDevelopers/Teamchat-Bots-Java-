package com.tc.sol.service.smml;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.tc.sol.service.smml.util.Utility.KEYWORDS;

/**
 * Servlet implementation class SendPoll
 */
@WebServlet("/sendMsg")
public class SendMsg extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendMsg()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		String apikey = request.getHeader(KEYWORDS.SM_SERVICE_API_KEY);
		String question = request.getParameter(KEYWORDS.QUESTION);
		String messageType = request.getParameter(KEYWORDS.MESSAGE_TYPE);
		String messageDesc = request.getParameter(KEYWORDS.MESSAGE_DESC);

		String smId = null;
		try
		{
			if (messageType != null)
			{

				if (messageType.equalsIgnoreCase(KEYWORDS.POLL))
					smId = GetSMId.getPollSMID(question, apikey);

				if (messageType.equalsIgnoreCase(KEYWORDS.SURVEY))
				{
					String options = request.getParameter(KEYWORDS.OPTIONS);
					smId = GetSMId.getSurveySMID(question, options, apikey);
				}

			} else
			{
				response.getWriter().write(new JSONObject().put(KEYWORDS.STATUS, KEYWORDS.FAILURE).put(KEYWORDS.DESC, KEYWORDS.MSG_TYPE_NOT_FOUND).toString());
			}

			String twitterCredentials = request.getParameter(KEYWORDS.TWITTER_CREDS);
			if (twitterCredentials != null)
				SendToTwitter.sendToTwitter(smId, apikey, twitterCredentials, messageDesc);

			String smsCredentials = request.getParameter(KEYWORDS.SMS_CREDS);
			if (smsCredentials != null)
				SendToSMS.sendToSMS(smId, apikey, smsCredentials, messageDesc);

			String emailCredentials = request.getParameter(KEYWORDS.EMAIL_CREDS);
			if (emailCredentials != null)
				SendToEmail.sendToEmail(smId, apikey, emailCredentials, messageDesc);

			String slackCredentials = request.getParameter(KEYWORDS.SLACK_CREDS);
			if (slackCredentials != null)
				SendToSlack.sendToSlack(smId, apikey, slackCredentials, messageDesc);

			if (smId != null)
			{
				response.getWriter().write(new JSONObject().put(KEYWORDS.STATUS, KEYWORDS.SUCCESS).put(KEYWORDS.SM_ID, smId).toString());
			} else {
				response.getWriter().write(new JSONObject().put(KEYWORDS.STATUS, KEYWORDS.SUCCESS).put(KEYWORDS.DESC, KEYWORDS.MESSAGE_SEND_SUCESS).toString());
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}

		response.getWriter().flush();
		response.getWriter().close();
	}

}
