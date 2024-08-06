package com.esferalia.aon.occam.api.model.aonsolutions;

import org.json.JSONObject;

public class TimeControlDetailUserName extends TimeControlDetail{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String userName;
	
	public TimeControlDetailUserName() {
	}

	public String getUserName() {
		return userName;
	}

	public TimeControlDetailUserName setUserName(String userName) {
		this.userName = userName;
		return this;
	}
	
	@Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("user_name", getUserName());
        return json;
    }
}
