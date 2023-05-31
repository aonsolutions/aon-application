package net.aonsolutions.api.http;

import java.io.Serializable;

import org.json.JSONObject;

import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.User;

public class AonApiRequest implements Serializable{

	private static final long serialVersionUID = -2301299154607565543L;
	
	private Domain domain;
	private User user;
	private String token;
	private String path;
	private AonApiRequestMethod method;
	private JSONObject data;
	
	public Domain getDomain() {
		return domain;
	}
	public AonApiRequest setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public User getUser() {
		return user;
	}
	public AonApiRequest setUser(User user) {
		this.user = user;
		return this;
	}

	public String getToken() {
		return token;
	}
	public AonApiRequest setToken(String token) {
		this.token = token;
		return this;
	}

	public String getPath() {
		return path;
	}
	public AonApiRequest setPath(String path) {
		this.path = path;
		return this;
	}

	public AonApiRequestMethod getMethod() {
		return method;
	}
	public AonApiRequest setMethod(AonApiRequestMethod method) {
		this.method = method;
		return this;
	}
	
	public JSONObject getData() {
		return data;
	}
	public AonApiRequest setData(JSONObject data) {
		this.data = data;
		return this;
	}
	
	public boolean isPredefinedToken() {
		return "SIGd95770f269e711eb94390242ac130002".equals(getToken())
			|| "AONd95770f269e711eb94390242ac130002".equals(getToken());
	}
	
	
	public static void main(String[] args) {
		
		
	}
	
	
	
}
