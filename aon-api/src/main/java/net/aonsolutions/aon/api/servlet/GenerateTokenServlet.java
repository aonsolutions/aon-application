package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
@WebServlet(name = "GenerateTokenServlet", urlPatterns = {"/ms/api/generate_token/*"})
public class GenerateTokenServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(GenerateTokenServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD INVOICE PDF AK");
		System.out.println("GenerateTokenServlet Path : " + req.getPathInfo());
		if(AonStringUtils.isBlank(req.getPathInfo())) {
			try {
				JSONObject json = new JSONObject(decode(req.getParameter("json").getBytes()));
				JSONObject data = buildTokenJSON(json);
				responseFile(resp, "serviceAccount", data.toString().getBytes(), MimeType.JSON, "attachment");
			} catch (IOException e) {
				error(req, resp, e);
			}
		} else if(AonStringUtils.equalsIgnoreCase(req.getPathInfo(), "/json/")) {
			try {
				Integer domainId = Integer.parseInt(req.getHeader(IJsonNames.DOMAIN_ID));
				String domainName = req.getHeader(IJsonNames.DOMAIN_NAME);
				String login = req.getHeader(IJsonNames.DOMAIN_LOGIN);
				Integer id = Integer.parseInt(req.getHeader(IJsonNames.ID)); 
				Integer time = Integer.parseInt(req.getHeader("time"));
				
				User user = AON.getUser(new Domain().setId(domainId).setName(domainName), login, f -> 
					f.getDomainProperty().eq(domainId)
					.and(f.getIdProperty().eq(id)));
				Date expireDate = getExpireDate(time); 
				JSONObject userTokenInfo = new JSONObject()
					.put("domain_name", domainName)
					.put("domain_id", domainId)
					.put("domain_login", user.getLogin())
					.put("session_id", AonToken.build(user, expireDate, domainName));
				
				response(req, resp, userTokenInfo);
			} catch (Exception e) {
				error(req, resp, e);
			}
		}
	}

	private JSONObject buildTokenJSON(JSONObject json) {
		Integer domainId = JsonUtils.getInteger(json, IJsonNames.DOMAIN_ID);
		String domainName = JsonUtils.getString(json, IJsonNames.DOMAIN_NAME);
		String login = JsonUtils.getString(json, IJsonNames.DOMAIN_LOGIN);
		Integer id = JsonUtils.getInteger(json, IJsonNames.ID); 
		Integer time = JsonUtils.getInteger(json, "time");
	
		User user = AON.getUser(new Domain().setId(domainId).setName(domainName), login, f -> 
			f.getDomainProperty().eq(domainId)
			.and(f.getIdProperty().eq(id)));
		Date expireDate = getExpireDate(time); 
		return new JSONObject()
			.put("domain_name", domainName)
			.put("domain_id", domainId)
			.put("domain_login", user.getLogin())
			.put("session_id", AonToken.build(user, expireDate, domainName));
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}

	
	private Date getExpireDate(Integer time) {
		if(time == null) return null;
		else if(time == 0) {
			return AonDateUtils.addMonths(new Date(), 1);			
		} else if(time == 1) {
			return AonDateUtils.addMonths(new Date(), 3);
		} else if(time == 2) {
			return AonDateUtils.addYears(new Date(), 1);
		} else if(time == 3) {
			return AonDateUtils.getDate(2099, 1, 1);
		} else return null;

	}
}
