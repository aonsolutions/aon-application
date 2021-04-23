package net.aonsolutions.aon.api.servlet;

import java.util.LinkedList;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationStatus;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.security.Auth;
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
				case "/":
					response(req, resp, getNotification());
				break;
				case "/total-notification":
					response(req, resp, getTotalNotification());
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
				case "/mark-read-notification":
					response(req, resp, markReadNotification());
					break;
				case "/send":
					response(req, resp, sendNotification());
					break;
				case "/save-test":
					response(req, resp, saveNotificationTest());
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
		AonToken authToken = SECURITY.getAonToken(getToken());
		LinkedList<Auth> auths = new LinkedList<>();
		if(getData().optString("type").equalsIgnoreCase("employee")){
			if(getData().opt("task_holder") != null) {
				TaskHolder th = AON.getTaskHolder(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(getData().optInt("task_holder")));
				User user = AON.getUser(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(th.getUserId()));
				Auth auth = new Auth().setAuth(user.getAuth());
				if(auth.getAuth()!=null) auths.add(auth);
			} else {
				AON.getDomainUserStream(domain.getName(), domain.getId(), login, f -> f.getAuthProperty().isNotNull()).forEach(user -> {
					Auth auth = new Auth().setAuth(user.getAuth());
					if(auth.getAuth()!=null) auths.add(auth);
				});
			}
		} else if(getData().opt("email") != null) {
			Auth auth = AON_SOLUTIONS.getAuth(getData().optString("email"));
			if(auth.getAuth()!=null) {
				auths.add(auth);
			} else {
				throw new Exception("El usuario no existe.");
			}
		} 
		
		if(auths.size()>0) {
	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(getData().optString("title"));
	    	notification.setBody(getData().optString("body"));
	    	notification.setSender(authToken.getAuth());
	    	notification.setDomain(getDomain());
	    	notification.setUser(getUser());
	    	notification.setAuths(auths);
		
	    	notification.send();
		}
		return new JSONObject();
	}

	
	private JSONArray getNotification() {
		JSONArray array = new JSONArray();
		AonToken at = SECURITY.getAonToken(getToken());
		Integer page = getParams().optInt("page");
		Integer peerPage = getParams().optInt("peerPage");
		AON_SOLUTIONS.getNotificationStream(f->f.getAuthProperty().eq(at.getAuth()).or(f.getSenderProperty().eq(at.getAuth())).and(f.getStatusProperty().eq(NotificationStatus.UNREAD.value())), page, peerPage)
		.forEach(nt -> {
			array.put(nt.toJSON());
		});
		return array;
	}

	private JSONObject saveNotificationTest() {
		AonToken authToken = SECURITY.getAonToken(getToken());
		LinkedList<Auth> auths = new LinkedList<Auth>();
		auths.add(new Auth().setAuth(authToken.getAuth()));
    	NotificationRequest notification = new NotificationRequest();
    	notification.setTitle("TITULO DE PRUEBA");
    	notification.setBody("CUERPO DE PRUEBA");
    	notification.setSender(authToken.getAuth());
    	notification.setDomain(getDomain());
    	notification.setUser(getUser());
    	notification.setAuths(auths);
		return notification.toJSON();
	}
	
	private JSONObject markReadNotification() {
		AON_SOLUTIONS.markReadNotification(getDomain(), getUser().getLogin(), getData().optInt("id"));
		return new JSONObject().put("success", true);
	}
	
	private JSONObject getTotalNotification() {
		AonToken at = SECURITY.getAonToken(getToken());
		Integer totalNotification = AON_SOLUTIONS.getTotalNotification(
				f->f.getAuthProperty().eq(at.getAuth())
				.and(f.getStatusProperty().eq(NotificationStatus.UNREAD.value())));
		JSONObject json = new JSONObject();
		json.put("notification", totalNotification);
		json.put("messenger", 0);
		return json;
	}
	
}
