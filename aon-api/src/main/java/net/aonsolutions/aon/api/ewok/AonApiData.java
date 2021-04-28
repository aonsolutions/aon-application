package net.aonsolutions.aon.api.ewok;

import java.io.Serializable;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;

public class AonApiData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String token;
	private Domain domain;
	private User user;
	private JSONObject data;
	private JSONObject params;
	private String path;
	
	public AonApiData() {
		
	}

	public String getToken() {
		return token;
	}

	public AonApiData setToken(String token) {
		this.token = token;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}

	public AonApiData setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public User getUser() {
		return user;
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

	public JSONObject getParams() {
		return params;
	}

	public AonApiData setParams(JSONObject params) {
		this.params = params;
		return this;
	}

	public String getPath() {
		return path;
	}

	public AonApiData setPath(String path) {
		this.path = path;
		return this;
	}
	
}
