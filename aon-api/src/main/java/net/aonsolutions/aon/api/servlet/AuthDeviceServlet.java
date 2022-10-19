package net.aonsolutions.aon.api.servlet;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.DeviceType;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonAuthDeviceServlet", urlPatterns = {"/ms/api/authdevice/*"})
public class AuthDeviceServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthDeviceServlet.class.getName());
	private static final String TOKEN_FCM = "tokenFCM";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			LOGGER.info("AON AUTHDEVICE SERVLET - GET METHOD");
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/save":
					LOGGER.info("AON AUTHDEVICE SAVE  SERVLET - GET METHOD");
					response(req, resp, save(api));
				break;
				case "/delete":
					LOGGER.info("AON AUTHDEVICE DELETE  SERVLET - GET METHOD");
					response(req, resp, delete(api));
				break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
	
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	private JSONObject save(AonApiData api) {
		JSONObject json = new JSONObject();
		if(!api.getData().optString(TOKEN_FCM).isEmpty()) {
			AonToken aonToken = SECURITY.getAonToken(api.getToken());
			Domain domain = new Domain().setName(aonToken.getSchemaFirstDomain()).setId(0);
			AuthDevice authDevice = new AuthDevice()
					.setId(api.getData().optInt("id"))
					.setAuth(aonToken.getAuth())
					.setDeviceType(DeviceType.safeValueOf(api.getData().optString("device_type")))
					.setDeviceToken(api.getData().optString(TOKEN_FCM));
			json = toJSON(SECURITY.saveAuthDevice(domain, api.getUser().getLogin(), authDevice));
		}
		return json;
	}
	
	public JSONObject toJSON(AuthDevice d) {
		JSONObject json = new JSONObject();
		json.put("id", d.getId());
		json.put("auth", d.getAuth());
		json.put("device_type", d.getDeviceType().value());
		json.put("device_token", d.getDeviceToken());
		return json;
	}
	private JSONObject delete(AonApiData api) {
		if(!api.getData().isNull(TOKEN_FCM))
			SECURITY.deleteAuthDevice(api.getDomain(), api.getUser().getLogin(), 
					f -> f.getDeviceTokenProperty().eq(api.getData().optString(TOKEN_FCM)));
		return new JSONObject();
	}
}
