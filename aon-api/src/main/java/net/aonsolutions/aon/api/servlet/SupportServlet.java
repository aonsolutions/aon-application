package net.aonsolutions.aon.api.servlet;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "SupportServlet", urlPatterns = {"/ms/api/support/*"})
public class SupportServlet extends AonApiHttpServlet {
		
	private static final Logger LOGGER  = Logger.getLogger(SupportServlet.class.getName());
		
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
		try {
			AonApiData api = initialize(req);
			String login = JsonUtils.getString(api.getData(), IJsonNames.LOGIN);
			
			if(AonStringUtils.isBlank(login)) 
				throw new AonApiException("El nombre de usuario es obligatorio.");
			
			User user = AON.getUser(api.getDomain(), login, f -> f.getDomainProperty().eq(api.getDomain().getId()).and(f.getLoginProperty().eq(login)));	

			if(user == null || user.isEmpty()) 
				throw new AonApiException("El usuario no existe en el dominio " + api.getDomain().getName());
			
			Occam occam = new Occam()
					.setDomain(api.getDomain().getId())
					.setDomainName(api.getDomain().getName())
					.setUser(login);
			
			ApplicationParameter param = new  ApplicationParameter()
					.setDomain(api.getDomain().getId())
					.setName(AppParam.AON_SUPPORT_ENABLED)
					.setValue(String.valueOf(new Date().getTime()));
			AON.saveApplicationParameter(occam, param);
			
			String token = null;
			user.getAuth().getUuid();
			if(user.getAuth().isEmpty()) {
				token = AonToken.build(user, AonDateUtils.addDays(new Date(), 1), api.getDomain().getName());
			} else {
				token = AonToken.build(user.getAuth(), AonDateUtils.addDays(new Date(), 1));
			}			
			JSONObject object = new JSONObject()
				.put(IJsonNames.TOKEN, token)
				.put(IJsonNames.URL, "https://" +  api.getDomain().getName() +"/login?token=" + token);
			response(req, resp, object);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
}
