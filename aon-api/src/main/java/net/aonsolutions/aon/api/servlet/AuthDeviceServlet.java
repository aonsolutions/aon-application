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

@SuppressWarnings("serial")
@WebServlet(name = "AonNotificationTokenServlet", urlPatterns = {"/ms/api/auth-device/*"})
public class AuthDeviceServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(AuthDeviceServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			LOGGER.info("AON AUTH-DEVICE SERVLET - GET METHOD");
			super.doGet(req, resp);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		try {
			super.doPost(req, resp);
			LOGGER.info("AON AUTH-DEVICE  SERVLET - GET METHOD");
			Object responseObject = null;

			switch (getPath()) {
			case "/save":
				responseObject = saveAuthDevice();
			break;
			case "/delete":
				responseObject = deleteAuthDevice();
			break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
			
			response(req, resp, responseObject);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}	
	
	private JSONObject saveAuthDevice() {
		AonToken aonToken = SECURITY.getAonToken(getToken());

		Domain domain = new Domain().setName(aonToken.getSchemaFirstDomain()).setId(0);
		AuthDevice authDevice = new AuthDevice()
				.setId(getData().optInt("id"))
				.setAuth(aonToken.getAuth())
				.setDeviceType(DeviceType.safeValueOf(getData().optString("device_type")))
				.setDeviceToken(getData().optString("tokenFCM"));
		return SECURITY.saveAuthDevice(domain, getUser().getLogin(), authDevice).toJSON();
	}
	
	private JSONObject deleteAuthDevice() {
		SECURITY.deleteAuthDevice(getDomain(), getUser().getLogin(), f-> f.getIdProperty().eq(getData().optInt("id")));
		return new JSONObject();
	}
}
