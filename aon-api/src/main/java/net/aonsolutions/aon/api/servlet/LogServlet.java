package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.stream.Stream;

import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.LogData;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@WebServlet("/viewLogs")
@WebServlet(name = "LogServlet", urlPatterns = {"/ms/api/viewLogs"})

public class LogServlet extends AonApiHttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		response.setContentType("text/html");
		try {			
			String document = request.getParameter("document");
			
			Auth auth = AON_SOLUTIONS.getAuthByDocument(document);
			
			String token = AonToken.build(auth, null);
			
			DecodedJWT tokenJWT = JWT.decode(token);
			
			JSONObject object = new JSONObject(tokenJWT.getClaim("sub").asString());
			
			Integer domainId = Integer.parseInt(request.getParameter("domainId"));
			
			Domain domain = null;
			
			User user = null;
			
			if (token != null && !token.isEmpty() && domainId != null) {
				domain = AON_SOLUTIONS.getDomain(object.getString("schema"), domainId);				
				user = AON_SOLUTIONS.getUser(domain, token);
			}
			
			Stream<LogData> logStream = AON_SOLUTIONS.getLogsStream(domain.getName(), domain.getId(),
					user.getLogin());
			
			 StringBuilder htmlResponse = new StringBuilder();
		        htmlResponse.append("<html><head>");
		        
		        htmlResponse.append("<style>");
		        htmlResponse.append("body { margin: 0; height: 100vh; display: flex; flex-direction: column; }"); 
		        htmlResponse.append("h2 { margin: 10px; }"); 
		        htmlResponse.append(".table-wrapper { padding: 0 20px; flex: 1; }"); 
		        htmlResponse.append("table { width: 100%; border-collapse: collapse; }"); 
		        htmlResponse.append("th, td { border: 1px solid black; padding: 8px; text-align: left; }");
		        htmlResponse.append("th { background-color: #f2f2f2; }");
		        htmlResponse.append(".table-container { max-height: calc(100vh - 50px); overflow-y: auto; }"); 
		        htmlResponse.append("</style>");
		        htmlResponse.append("</head><body>");
		        
		        htmlResponse.append("<div class='table-wrapper'>"); 
		        htmlResponse.append("<h2>Lista de Logs</h2>");
		        htmlResponse.append("<div class='table-container'>");
		        htmlResponse.append("<table>");
		        htmlResponse.append("<thead>");
		        htmlResponse.append("<tr>");
		        htmlResponse.append("<th>ID</th>");
		        htmlResponse.append("<th>Fecha</th>");
		        htmlResponse.append("<th>Mensaje</th>");
		        htmlResponse.append("</tr>");
		        htmlResponse.append("</thead>");
		        htmlResponse.append("<tbody>");
		        
		        logStream.forEach(log -> {
		            htmlResponse.append("<tr>");
		            htmlResponse.append("<td>").append(log.getId()).append("</td>");
		            htmlResponse.append("<td>").append(log.getDate()).append("</td>");
		            htmlResponse.append("<td>").append(log.getMessage()).append("</td>");
		            htmlResponse.append("</tr>");
		        });
		        
		        htmlResponse.append("</tbody>");
		        htmlResponse.append("</table>");
		        htmlResponse.append("</div>"); 
		        htmlResponse.append("</div>"); 
		        
		        htmlResponse.append("</body></html>");
		        
		        response.getWriter().write(htmlResponse.toString());
		}

		catch (Exception e) {
			e.printStackTrace(); 
			try {
				response.getWriter().write("Error al recuperar los logs: " + e.getMessage());
			} catch (IOException ioException) {
				ioException.printStackTrace(); 
			}
		}

	}
}
