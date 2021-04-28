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

import net.aonsolutions.aon.api.ewok.AonApiData;

@SuppressWarnings("serial")
@WebServlet(name = "AonAuthDeviceServlet", urlPatterns = {"/ms/api/authdevice/*"})
public class AuthDeviceServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthDeviceServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			LOGGER.info("AON AUTHDEVICE SERVLET - GET METHOD");
			AonApiData api = initialize(req, resp);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req, resp);
			LOGGER.info("AON AUTHDEVICE  SERVLET - GET METHOD");
			Object responseObject = null;

			switch (api.getPath()) {
			case "/save":
				responseObject = save(api);
			break;
			case "/delete":
				responseObject = delete(api);
			break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
			
			response(req, resp, responseObject);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	private JSONObject save(AonApiData api) {
		AonToken aonToken = SECURITY.getAonToken(api.getToken());

		Domain domain = new Domain().setName(aonToken.getSchemaFirstDomain()).setId(0);
		AuthDevice authDevice = new AuthDevice()
				.setId(api.getData().optInt("id"))
				.setAuth(aonToken.getAuth())
				.setDeviceType(DeviceType.safeValueOf(api.getData().optString("device_type")))
				.setDeviceToken(api.getData().optString("tokenFCM"));
		return SECURITY.saveAuthDevice(domain, api.getUser().getLogin(), authDevice).toJSON();
	}
	
	private JSONObject delete(AonApiData api) {
		SECURITY.deleteAuthDevice(api.getDomain(), api.getUser().getLogin(), f-> f.getIdProperty().eq(api.getData().optInt("id")));
		return new JSONObject();
	}
}
