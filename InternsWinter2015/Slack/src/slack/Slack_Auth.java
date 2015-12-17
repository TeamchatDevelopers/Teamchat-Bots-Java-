package slack;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class Slack_Auth
 */
@WebServlet(name="Slack_Auth",urlPatterns={"/Slack"})
public class Slack_Auth extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public String email;
	public String code;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		email = request.getParameter("state");
		code = request.getParameter("code");
		String encoded = request.getParameter("state");
		
		SlackDB.saveCode(encoded, code);
		System.out.println("Saved code to database");
	
		PrintWriter out = response.getWriter();
		out.println("<script>window.close();</script>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String token = request.getParameter("token");
		System.out.println("URI is :" + request.getRequestURI());
		System.out.println("Token is :"+token);
	}

}