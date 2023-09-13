package com.esferalia.aon.occam.api.model.seres;

public class SeresInfo {
	
	private String server;
	private Integer port;
	private String user;
	private String password;
	private SeresPath seresPath;

	public String getServer() {
		return server;
	}

	public SeresInfo setServer(String server) {
		this.server = server;
		return this;
	}

	public Integer getPort() {
		if(port == null) {
			port = 22;
		}
		return port;
	}

	public SeresInfo setPort(Integer port) {
		this.port = port;
		return this;
	}

	public String getUser() {
		return user;
	}

	public SeresInfo setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public SeresInfo setPassword(String password) {
		this.password = password;
		return this;
	}
	
	public SeresPath getSeresPath() {
		return seresPath;
	}
	
	public SeresInfo setSeresPath(SeresPath seresPath) {
		this.seresPath = seresPath;
		return this;
	}
}
