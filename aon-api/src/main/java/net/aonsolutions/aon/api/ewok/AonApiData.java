package net.aonsolutions.aon.api.ewok;

import java.io.Serializable;

import org.json.JSONObject;


import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.User;

import jakarta.servlet.http.HttpServletRequest;

public class AonApiData implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String method;
	private String token;
	private Domain domain;
	private User user;
	private JSONObject data;
	private Options options;
	
	private String path;
	private DomainUserRoles dur;
	
	private HttpServletRequest request;
	
	public String getMethod() {
		return method;
	}
	
	public AonApiData setMethod(String method) {
		this.method = method;
		return this;
	}
	
	public boolean isGet() {
		return "GET".equalsIgnoreCase(getMethod());
	}
	
	public boolean isPost() {
		return "POST".equalsIgnoreCase(getMethod());
	}
	
	public boolean isPut() {
		return "PUT".equalsIgnoreCase(getMethod());
	}
	
	public boolean isDelete() {
		return "DELETE".equalsIgnoreCase(getMethod());
	}
	
	public String getToken() {
		return token;
	}

	public AonApiData setToken(String token) {
		this.token = token;
		return this;
	}

	public Domain getDomain() {
		return isValid(domain) ? domain : getDomain(token);
	}

	public AonApiData setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public User getUser() {
		return isValid(user) ? user : getUser(token);
	}

	public AonApiData setUser(User user) {
		this.user = user;
		return this;
	}

	public JSONObject getData() {
		return data;
	}

	public AonApiData setData(JSONObject data) {
		this.data = data;
		return this;
	}
	
	public String getPath() {
		return path;
	}

	public AonApiData setPath(String path) {
		this.path = path;
		return this;
	}
	

	public DomainUserRoles getDur() {
		return dur;
	}

	public AonApiData setDur(DomainUserRoles dur) {
		this.dur = dur;
		return this;
	}
	
	public Options getOptions() {
        return options;
    }
	
	public void setOptions(Options options) {
        this.options = options;
    }
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public AonApiData setRequest(HttpServletRequest request) {
		this.request = request;
		return this;
	}
	
	public Occam getOccam() {
		if (getDomain() == null) throw new IllegalStateException("Domain not set!");
		if (getUser() == null) throw new IllegalStateException("User not set!");

		return new Occam()
			.setDomain(getDomain().getId())
			.setDomainName(getDomain().getName())
			.setUser( getUser().getLogin() )
		;
	}
	
	public boolean isPredefinedToken() {
		return "AONd95770f269e711eb94390242ac130002".equals(getToken());
	}
	
	private static Domain getDomain(String token) {
		if ( token == null )
			return null;
		AonToken aonToken = SECURITY.getAonToken(token);
		Integer id = aonToken.getDomain();
		String name = aonToken.getDomainName();
		if ( id == null || name == null )
			return null;
		return new Domain().setId(id).setName(name);
	}
	
	private static User getUser(String token) {
		if ( token == null )
			return null;
		AonToken aonToken = SECURITY.getAonToken(token);
		Integer id = aonToken.getUser();
		String login = aonToken.getLogin();
		if ( id == null || login == null )
			return null;
		return new User().setId(id).setLogin(login);
	}
	
	private static boolean isValid(User user) {
		return user != null 
				&& user.getId() != null
				&& user.getLogin() != null;
	}

	private static boolean isValid(Domain domain) {
		return domain != null 
				&& domain.getId() != null
				&& domain.getName() != null;
	}
	
}
