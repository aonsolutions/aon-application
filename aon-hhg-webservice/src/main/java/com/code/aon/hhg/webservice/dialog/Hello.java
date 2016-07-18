package com.code.aon.hhg.webservice.dialog;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Hello {
	
//	protected static final String URL = "http://intranet.hhg-hotels.net/services/hello/";
	protected static final String URL = "https://213.236.3.11/services/hello/";

	private String username;
	private String nonce;
	private String hash;
	
	//***** Gets & Sets *****/
	
	public String getUsername() {
		return username;
	}
	public Hello setUsername(String username) {
		this.username = username;
		return this;
	}
	public String getNonce() {
		return nonce;
	}
	public Hello setNonce(String nonce) {
		this.nonce = nonce;
		return this;
	}
	public String getHash() {
		return hash;
	}
	public Hello setHash(String hash) {
		this.hash = hash;
		return this;
	}

	//***** Utils *****/
	
	public JSONObject toJSON(){
		JSONObject json = new JSONObject();
		try {
			json.put("username", getUsername());
			json.put("nonce", getNonce());
			json.put("hash", getHash());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public Hello calculateHash(String password){
		String hash = "";
		hash = hash.concat(getUsername()).concat(getNonce()).concat(password);
		setHash(DigestUtils.md5Hex(hash));
		return this;
	}
	
}
