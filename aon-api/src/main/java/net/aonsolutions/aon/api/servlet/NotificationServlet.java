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
import com.esferalia.aon.occam.api.json.DomainJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Properties.NotificationProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationSource;
import com.esferalia.aon.occam.api.model.aonsolutions.NotificationStatus;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.task.TaskHolder;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
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
			AonApiData api = initialize(req);
			setDomain(api);
			switch (api.getPath()) {
			case "/":
				LOGGER.info("AON API GET NOTIFICATION SERVLET");
				response(req, resp, getNotification(api));
				break;
			case "/domain":
				LOGGER.info("AON API GET NOTIFICATION DOMAIN SERVLET");
				response(req, resp, getNotificationByDomain(api));
				break;
			case "/total-notification":
				response(req, resp, getTotalNotification(api));
				break;
			default:
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		LOGGER.info("AON API NOTIFICATION SERVLET - POST METHOD");
		try {
			AonApiData api = initialize(req);
			setDomain(api);
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
				throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private JSONObject sendNotification(AonApiData api) {
		Domain domain = api.getDomain();
		String login = api.getUser().getLogin();
		JSONObject params = api.getData();
		AonToken authToken = SECURITY.getAonToken(api.getToken());
		LinkedList<Auth> auths = new LinkedList<>();
		if(params.optString(IJsonNames.TYPE).equalsIgnoreCase("employee")){
			int taskHolderId = params.optInt(IJsonNames.TASK_HOLDER);
			if(taskHolderId!=0) {
				TaskHolder th = AON.getTaskHolder(domain.getName(), domain.getId(), login, f-> f.getIdProperty().eq(taskHolderId));
				User user = AON.getUser(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(th.getUserId()));
				Auth auth = user.getAuth();
				if(auth.getAuth()!=null) {					
					auths.add(auth);
				}
			} else {
				AON.getDomainUserStream(domain.getName(), domain.getId(), login, f -> f.getAuthProperty().isNotNull())
				.filter(user-> user.getAuth()!=null)
				.forEach(user -> 
					auths.add(user.getAuth())
				);
			}
		} else if(!params.optString(IJsonNames.EMAIL).isEmpty()) {
			Auth auth = AON_SOLUTIONS.getAuth(params.optString(IJsonNames.EMAIL));
			if(auth.getAuth()!=null) {
				auths.add(auth);
			} else {
				throw new AonApiException("El usuario no existe.");
			}
		} 
		
		if(!auths.isEmpty()) {
	    	NotificationRequest notification = new NotificationRequest();
	    	notification.setTitle(params.optString(IJsonNames.TITLE));
	    	notification.setBody(params.optString("body"));
	    	notification.setSender(authToken.getAuth());
	    	notification.setDomain(api.getDomain());
	    	notification.setUser(api.getUser());
	    	notification.setAuths(auths);
	    	notification.send();
		}
		return new JSONObject();
	}

	private JSONArray getNotification(AonApiData api) {
		JSONObject params = api.getData();
		Integer page    = params.optInt(IJsonNames.PAGE);
		Integer perPage = params.optInt(IJsonNames.PER_PAGE);
		
		JSONArray array = new JSONArray();
		AON_SOLUTIONS.getNotificationStream(f -> getFilterByUser(api, f), page, perPage)
		.forEach(nt -> array.put(nt.toJSON()));
		
		return array;
	}
	
	private JSONArray getNotificationByDomain(AonApiData api) {
		JSONArray array = new JSONArray();
		Domain domain = api.getDomain();
		User user = api.getUser();
		
		AON_SOLUTIONS.getNotificationStream(domain.getId(), domain.getName(), user.getLogin(),
			f -> getFilter(api, f)
		)
		.forEach(nt -> array.put(nt.toJSON()));
		return array;
	}

	private JSONObject saveNotificationTest(AonApiData api) {
		AonToken authToken = SECURITY.getAonToken(api.getToken());
		LinkedList<Auth> auths = new LinkedList<>();
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
		
		AON_SOLUTIONS.markReadNotification(api.getDomain(), api.getUser().getLogin(),
			f->  getFilterMark(api, f)
		);

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
	
	private Filter getFilter(AonApiData api, NotificationProperties f) {
		JSONObject params = api.getData();
		String search     = params.optString(IJsonNames.SEARCH);
		
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId()).and(f.getAuthProperty().eq(aonToken.getAuth()));
		
		NotificationStatus status = params.optBoolean("read") ? NotificationStatus.READ : NotificationStatus.UNREAD;
		filter = filter.and(f.getStatusProperty().eq(status.value()));
	
		NotificationSource source = NotificationSource.safeValueOf(params.getString(IJsonNames.SOURCE));
		filter = filter.and(f.getSourceProperty().eq(source.value()));
		
		if(!search.isEmpty()) {
			filter = filter.and(getSearchCombination(f, search));
		}


		return filter;
	}
	
	private Filter getFilterByUser(AonApiData api, NotificationProperties f) {
		JSONObject params = api.getData();
		String search     = params.optString(IJsonNames.SEARCH);
		
		AonToken aonToken = SECURITY.getAonToken(api.getToken());

		String status = params.optString(IJsonNames.STATUS);
		
		Filter filter = f.getAuthProperty().eq(aonToken.getAuth());
		
		if(status.isEmpty() ) {
			filter = filter.and(f.getAuthProperty().isNotNull());
		} else {
			filter = filter.and(f.getStatusProperty().eq(NotificationStatus.safeValueOf(status).value()));
		}
		
		if(!search.isEmpty()) {
			filter = filter.and(getSearchCombination(f, search));
		}
	

		return filter;
	}
	
	
	private Filter getFilterMark(AonApiData api, NotificationProperties f) {
		JSONObject params = api.getData();
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		int id = params.optInt(IJsonNames.ID);
		
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(id!=0) {
			filter = filter.and(f.getReceiverIdProperty().eq(id));
		} else {
			
			filter =  filter.and(f.getAuthProperty().eq(aonToken.getAuth()));
			
			Boolean read = params.optBoolean("read");
			
			Integer sourceId = params.optInt(IJsonNames.SOURCE_ID);
			
			NotificationStatus status = Boolean.TRUE.equals(read) ? NotificationStatus.READ : NotificationStatus.UNREAD;
			filter = filter.and(f.getStatusProperty().eq(status.value()));
		
			if(sourceId!=0) {
				filter = filter.and(f.getSourceIdProperty().eq(sourceId));
				
				String source = params.optString(IJsonNames.SOURCE);
				if(!source.isEmpty()) {
					filter = filter.and(f.getSourceProperty().eq(NotificationSource.safeValueOf(source).value()));
				}
			}
		}
		
		return filter;
	}
	
	private void setDomain(AonApiData api) {
		JSONObject domainJson = api.getData().optJSONObject(IJsonNames.DOMAIN);
		if(domainJson!=null) {
			Domain domain = DomainJSON.fromJSON(domainJson);
			api.setDomain(domain);
		}
	}
	
	
	private static Filter getSearchCombination(NotificationProperties f, String search) {
		Filter joinSequence = null;
		String[] comb = search.trim().split("\\s");
		
		for (String word : comb) {
			StringBuilder processed = new StringBuilder("%").append(word).append("%");
			if(joinSequence == null) {
				joinSequence = getSearch(f, processed.toString()); 
			} else {
				joinSequence = joinSequence.and(getSearch(f, processed.toString()));		
			}
		}			
		return joinSequence;
	}
	
	
	private static Filter getSearch(NotificationProperties f, String search) {
		return f.getTitleProperty().like(search)
		.or(f.getBodyProperty().like(search))
		;
	}	
}
