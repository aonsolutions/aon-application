package net.aonsolutions.aon.api.servlet;

import java.io.PrintWriter;
import java.util.logging.Logger;

import org.json.JSONObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import com.esferalia.aon.occam.api.SECURITY;


@SuppressWarnings("serial")
@WebServlet(name = "AonGptUserVerificationServlet", urlPatterns = {"/ms/api/gptuserverification"})
public class GptUserVerificationServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(UserServlet.class.getName());

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req, false);
			LOGGER.info("[POST] /gptuserverification" + api.getPath());
			String token = api.getData().getString("token");
			if(token == null) throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			JSONObject userData = SECURITY.decodeJWT(token);
			resp.setContentType("application/json");
			PrintWriter out = resp.getWriter();
			out.print(userData);
			out.flush();
			

			
		
			
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
}
