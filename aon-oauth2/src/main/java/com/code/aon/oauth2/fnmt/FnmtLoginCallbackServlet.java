package com.code.aon.oauth2.fnmt;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.oauth2.Utils;
import com.code.aon.oauth2.sessionInfo.SessionInfo;

public class FnmtLoginCallbackServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static String getUsername(String dni) {
		
		return "Fnmt_Dni=" + dni ;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String key = req.getServerName();
		String dni = req.getParameter("dni");
		
		
		
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/login/popupclose.jsp");
		req.setAttribute("name", key);
		req.setAttribute("act", SessionInfo.table.get(key).getAction());
		req.setAttribute("username", getUsername(dni));
		req.setAttribute("password", getPassword() );
		
		java.util.Collections.list(req.getAttributeNames())
		.forEach( name -> System.out.println( "[FnmtLoginCallbackServlet] " +  name + " : '" + req.getAttribute(name.toString()) + "'"));

		dispatcher.forward(req, resp);

		
	}

	protected String getPassword() {
		return Utils.PasswordGenerator.getPassword(
		Utils.PasswordGenerator.MINUSCULAS
		+ Utils.PasswordGenerator.MAYUSCULAS
		+ Utils.PasswordGenerator.NUMEROS, 10);
	}


}
