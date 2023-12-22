package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRS3Object implements Serializable {
	
	
	private static final long serialVersionUID = -3899262242464716704L;
	
	private String bucket;
	private String domain;
	private String user;
	private String key;
	
	public Optional<String> getBucket() {
		return Optional.ofNullable(bucket);
	}
	public OCRS3Object setBucket(String bucket) {
		this.bucket = bucket;
		return this;
	}
	
	public Optional<String> getUser() {
		return Optional.ofNullable(user);
	}
	public OCRS3Object setUser(String user) {
		this.user = user;
		return this;
	}

	public Optional<String> getKey() {
		return Optional.ofNullable(key);
	}
	public OCRS3Object setKey(String key) {
		this.key = key;
		return this;
	}
	
	public Optional<String> getDomain() {
		return Optional.ofNullable(domain);
	}
	public OCRS3Object setDomain(String domain) {
		this.domain = domain;
		return this;
	}
}
