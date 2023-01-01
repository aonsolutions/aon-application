package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class OLD_Activity implements Serializable {

	private static final long serialVersionUID = -1202033752419394011L;
	
	private String description;
	private String key;
	private String epigraph;
	
	public String getDescription() {
		return description;
	}
	public OLD_Activity setDescription(String description) {
		this.description = description;
		return this;
	}
	public String getKey() {
		return key;
	}
	public OLD_Activity setKey(String key) {
		this.key = key;
		return this;
	}
	public String getEpigraph() {
		return epigraph;
	}
	public OLD_Activity setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
}
