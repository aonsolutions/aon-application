package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

@SuppressWarnings("serial")
public class Activity implements Serializable, IsSerializable{

	public static final ProvidesKey<Activity> PROVIDES_KEY = new ProvidesKey<Activity>() {
		@Override
		public Object getKey(Activity activity) {
			return activity == null ? null : activity.getEpigraph();
		}
	};

	private String description;
	private String key;
	private String epigraph;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public void setEpigraph(String epigraph) {
		this.epigraph = epigraph;
	}
	

}
