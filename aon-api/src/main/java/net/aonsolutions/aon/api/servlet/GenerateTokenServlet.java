package net.aonsolutions.aon.api.servlet;

import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.AonApiData;


@SuppressWarnings("serial")
@WebServlet(name = "GenerateTokenServlet", urlPatterns = {"/ms/api/generate_token/*"})
public class GenerateTokenServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(GenerateTokenServlet.class.getName());
	
	private static final String GENERATE_TOKEN = "/";
    private static final String GENERATE_TOKEN_JSON = "/json";
	private static final String GENERATE_TOKEN_SIG = "/sig";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		get(req, resp);
	}

	private void get(HttpServletRequest req, HttpServletResponse resp) {
        LOGGER.info("[" + req.getMethod() + "] " + req.getRequestURI());
        try {
            AonApiData api = initialize(req, false);
    		if(AonStringUtils.isBlank(req.getPathInfo())) {
    			JSONObject json = new JSONObject(decode(req.getParameter("json").getBytes()));
    			api.setData(json);
    			Object object = new AonRouting(api)
                		.addRoute(GENERATE_TOKEN, GenerateTokenServlet::generateToken)
                		.apply();
                
    			responseFile(resp, "serviceAccount", object.toString().getBytes(), MimeType.JSON, "attachment");
    		} else {
    			Object object = new AonRouting(api)
    					.addRoute(GENERATE_TOKEN_JSON, GenerateTokenServlet::generateTokenJson)
    					.addRoute(GENERATE_TOKEN_SIG, GenerateTokenServlet::generateTokenSig)
    					.apply();
   
    			response(req, resp, object);
    		}
        } catch (Exception e) {
            error(req, resp, e);
        }
    }

	private static JSONObject generateToken(AonApiData api){
		return  buildTokenJSON(api.getData());
	}
	
	private static JSONObject generateTokenJson(AonApiData api){
		Integer id = Integer.parseInt(api.getRequest().getHeader(IJsonNames.ID)); 
		Integer time = Integer.parseInt(api.getRequest().getHeader("time"));
		
		User user = AON.getUser(api.getDomain(), api.getUser().getLogin(), f -> 
			f.getDomainProperty().eq(api.getDomain().getId())
			.and(f.getIdProperty().eq(id)));
		Date expireDate = getExpireDate(time); 
		JSONObject userTokenInfo = new JSONObject()
			.put("domain_name", api.getDomain().getName())
			.put("domain_id", api.getDomain().getId())
			.put("domain_login", user.getLogin())
			.put("session_id", AonToken.build(user, expireDate, api.getDomain().getName()));
		
		return userTokenInfo;
	}

	private static JSONObject generateTokenSig(AonApiData api){
		String domainName = AON.getApplicationParameter(api.getDomain().getName(), 0, api.getUser().getLogin(), AppParam.SIG_DOMAIN_NAME).getValue();
		String domainId = AON.getApplicationParameter(api.getDomain().getName(), 0, api.getUser().getLogin(), AppParam.SIG_DOMAIN_ID).getValue();
		String login = AON.getApplicationParameter(api.getDomain().getName(), 0, api.getUser().getLogin(), AppParam.SIG_LOGIN).getValue();

		Date expireDate = AonDateUtils.addHours(new Date(), 1);
		User user = new User().setLogin(login);
		JSONObject userTokenInfo = new JSONObject()
			.put("domain_name", domainName)
			.put("domain_id", domainId)
			.put("domain_login", login)
			.put("session_id", AonToken.build(user, expireDate, api.getDomain().getName()));
		
		return userTokenInfo;
	}
	
	private static JSONObject buildTokenJSON(JSONObject json) {
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

	private static Date getExpireDate(Integer time) {
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
