package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationStatus;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.notification.NotificationRequest;

@SuppressWarnings("serial")
@WebServlet(name = "AonNotificationServlet", urlPatterns = {"/ms/api/notification/*"})
public class NotificationServlet extends AonApiHttpServlet{

	
	private static final Logger LOGGER  = Logger.getLogger(NotificationServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API NOTIFICATION SERVLET - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getNotification(api));
				break;
				case "/total-notification":
					response(req, resp, getTotalNotification(api));
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
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/mark-read-notification":
					response(req, resp, markReadNotification(api));
					break;
				case "/send":
					response(req, resp, sendNotification(api));
					break;
				case "/save-test":
					response(req, resp, saveNotificationTest(api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	


	private JSONObject sendNotification(AonApiData api) throws Exception {
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		AonToken authToken = SECURITY.getAonToken(api.getToken());
		LinkedList<Auth> auths = new LinkedList<>();
		if(api.getData().optString("type").equalsIgnoreCase("employee")){
			if(api.getData().opt("task_holder") != null) {
				TaskHolder th = AON.getTaskHolder(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(api.getData().optInt("task_holder")));
				User user = AON.getUser(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(th.getUserId()));
				Auth auth = new Auth().setAuth(user.getAuth());
				if(auth.getAuth()!=null) auths.add(auth);
			} else {
				AON.getDomainUserStream(domain.getName(), domain.getId(), login, f -> f.getAuthProperty().isNotNull()).forEach(user -> {
					Auth auth = new Auth().setAuth(user.getAuth());
					if(auth.getAuth()!=null) auths.add(auth);
				});
			}
		} else if(api.getData().opt("email") != null) {
			Auth auth = AON_SOLUTIONS.getAuth(api.getData().optString("email"));
			if(auth.getAuth()!=null) {
				auths.add(auth);
			} else {
				throw new Exception("El usuario no existe.");
			}
		} 
		
		if(auths.size()>0) {
	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(api.getData().optString("title"));
	    	notification.setBody(api.getData().optString("body"));
	    	notification.setSender(authToken.getAuth());
	    	notification.setDomain(api.getDomain());
	    	notification.setUser(api.getUser());
	    	notification.setAuths(auths);
		
	    	notification.send();
		}
		return new JSONObject();
	}

	
	private JSONArray getNotification(AonApiData api) {
		JSONArray array = new JSONArray();
		AonToken at = SECURITY.getAonToken(api.getToken());
		Integer page = api.getParams().optInt("page");
		Integer peerPage = api.getParams().optInt("peerPage");
		AON_SOLUTIONS.getNotificationStream(f->f.getAuthProperty().eq(at.getAuth()).or(f.getSenderProperty().eq(at.getAuth())).and(f.getStatusProperty().eq(NotificationStatus.UNREAD.value())), page, peerPage)
		.forEach(nt -> {
			array.put(nt.toJSON());
		});
		return array;
	}

	private JSONObject saveNotificationTest(AonApiData api) {
		AonToken authToken = SECURITY.getAonToken(api.getToken());
		LinkedList<Auth> auths = new LinkedList<Auth>();
		auths.add(new Auth().setAuth(authToken.getAuth()));
    	NotificationRequest notification = new NotificationRequest();
    	notification.setTitle("TITULO DE PRUEBA");
    	notification.setBody("CUERPO DE PRUEBA");
    	notification.setSender(authToken.getAuth());
    	notification.setDomain(api.getDomain());
    	notification.setUser(api.getUser());
    	notification.setAuths(auths);
		return notification.toJSON();
	}
	
	private JSONObject markReadNotification(AonApiData api) {
		AON_SOLUTIONS.markReadNotification(api.getDomain(), api.getUser().getLogin(), api.getData().optInt("id"));
		return new JSONObject().put("success", true);
	}
	
	private JSONObject getTotalNotification(AonApiData api) {
		AonToken at = SECURITY.getAonToken(api.getToken());
		Integer totalNotification = AON_SOLUTIONS.getTotalNotification(
				f->f.getAuthProperty().eq(at.getAuth())
				.and(f.getStatusProperty().eq(NotificationStatus.UNREAD.value())));
		JSONObject json = new JSONObject();
		json.put("notification", totalNotification);
		json.put("messenger", 0);
		return json;
	}
	
}
