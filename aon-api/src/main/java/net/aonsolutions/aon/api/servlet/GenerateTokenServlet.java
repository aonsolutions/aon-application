package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;


@SuppressWarnings("serial")
@WebServlet(name = "GenerateTokenServlet", urlPatterns = {"/ms/api/generate_token/*"})
public class GenerateTokenServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(GenerateTokenServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD INVOICE PDF AK");
		try {
			JSONObject json = new JSONObject(decode(req.getParameter("json").getBytes()));
			JSONObject data = buildTokenJSON(json);
			responseFile(resp, "serviceAccount", data.toString().getBytes(), MimeType.JSON, "attachment");
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	private JSONObject buildTokenJSON(JSONObject json) {
		Integer domainId = JsonUtils.getInteger(json, IJsonNames.DOMAIN_ID);
		String domainName = JsonUtils.getString(json, IJsonNames.DOMAIN_NAME);
		String login = JsonUtils.getString(json, IJsonNames.DOMAIN_LOGIN);
		Integer id = JsonUtils.getInteger(json, IJsonNames.ID); 
		
		User user = AON.getUser(new Domain().setId(domainId).setName(domainName), login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getIdProperty().eq(id)));

		return new JSONObject()
			.put("domain_name", domainName)
			.put("domain_id", domainId)
			.put("domain_login", user.getLogin())
			.put("session_id", AonToken.build(user, null, domainName));
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}

}
