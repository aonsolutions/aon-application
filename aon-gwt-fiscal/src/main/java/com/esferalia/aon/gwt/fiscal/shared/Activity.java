package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Activity implements Serializable, IsSerializable{

	private String activity;
	private String activityKey;
	private String activityEpigraph;
	
	public String getActivity() {
		return activity;
	}
	public void setActivity(String activity) {
		this.activity = activity;
	}
	public String getActivityKey() {
		return activityKey;
	}
	public void setActivityKey(String activityKey) {
		this.activityKey = activityKey;
	}
	public String getActivityEpigraph() {
		return activityEpigraph;
	}
	public void setActivityEpigraph(String activityEpigraph) {
		this.activityEpigraph = activityEpigraph;
	}

}
