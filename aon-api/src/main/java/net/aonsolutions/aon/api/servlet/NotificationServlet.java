package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.AuthDevice;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
import net.aonsolutions.aon.api.notification.NotificationRequest;

@SuppressWarnings("serial")
@WebServlet(name = "AonNotificationServlet", urlPatterns = {"/ms/api/notification/*"})
public class NotificationServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(NotificationServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API NOTIFICATION SERVLET - GET METHOD");
		try {
			super.doGet(req, resp);
			switch (getPath()) {
				case "/test":
					response(req, resp, notificationTest());
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API NOTIFICATION SERVLET - POST METHOD");
		try {
			super.doPost(req, resp);
			switch (getPath()) {
				case "/send":
					response(req, resp, sendNotification());
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	


	private JSONObject sendNotification() throws Exception {
		Domain domain = getDomain();
		String login = getUser().getLogin();
		LinkedList<AuthDevice> auths = new LinkedList<>();
		if(getData().optString("type").equalsIgnoreCase("employee")){
			if(getData().opt("task_holder") != null) {
				TaskHolder th = AON.getTaskHolder(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(getData().optInt("task_holder")));
				User user = AON.getUser(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(th.getUserId()));
				LinkedList<AuthDevice> aths = SECURITY.getAuthDevices(domain, login, f-> f.getAuthProperty().eq(user.getAuth()));
				auths.addAll(aths);
			} else {
				AON.getDomainUserStream(domain.getName(), domain.getId(), login, f -> f.getAuthProperty().isNotNull()).forEach(user -> {
					LinkedList<AuthDevice> ad = SECURITY.getAuthDevices(domain, login, f-> f.getAuthProperty().eq(user.getAuth()));
    				if(ad != null) auths.addAll(ad);
				});
			}
		} else if(getData().opt("email") != null) {
			System.out.println("email" + getData().optString("email"));
			Auth auth = AON_SOLUTIONS.getAuth(getData().optString("email"));
			if(auth.getAuth()!=null) {
				LinkedList<AuthDevice> aths = SECURITY.getAuthDevices(domain, login, f-> f.getAuthProperty().eq(auth.getAuth()));
				auths.addAll(aths);
			} else {
				throw new Exception("El usuario no existe.");
			}
		} 
		
		if(auths.size()>0) {
	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(getData().optString("title"));
	    	notification.setBody(getData().optString("body"));
	    	notification.setDeviceTokens(auths.stream().map(ad -> ad.getDeviceToken()).toArray(String[]::new));
	    	notification.send();
		}
		return new JSONObject();
	}



	private JSONObject notificationTest() {
		NotificationRequest notification = new NotificationRequest();
		notification.setTitle("TITULO DE PRUEBA");
    	notification.setBody("CUERPO DE PRUEBA");
    	notification.setDeviceTokens(new String[] {getParams().optString("tokenFCM")});
    	Boolean success = notification.send();
		return new JSONObject().put("success", success);
	}
	
}
