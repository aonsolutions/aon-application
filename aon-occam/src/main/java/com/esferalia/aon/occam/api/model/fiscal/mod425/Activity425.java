package com.esferalia.aon.occam.api.model.fiscal.mod425;

import java.io.Serializable;

public class Activity425 implements Serializable {

	private static final long serialVersionUID = -8328120296129674616L;
	
	private String epigraph;
	private String description;
	private String key;	
	private String regime;
	private double provisionalProrate;
	private double finalProrate;
	private boolean specialProrate;
	
	public String getEpigraph() {
		return epigraph;
	}
	public Activity425 setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	public String getDescription() {
		return description;
	}
	public Activity425 setDescription(String description) {
		this.description = description;
		return this;
	}
	public String getKey() {
		return key;
	}
	public Activity425 setKey(String key) {
		this.key = key;
		return this;
	}
//	public ActivityType getType() {
//		return type;
//	}
//	public Activity425 setType(ActivityType type) {
//		this.type = type;
//		return this;
//	}
	public String getRegime() {
		return regime;
	}
	public Activity425 setRegime(String regime) {
		this.regime = regime;
		return this;
	}
	public double getProvisionalProrate() {
		return provisionalProrate;
	}
	public Activity425 setProvisionalProrate(double provisionalProrate) {
		this.provisionalProrate = provisionalProrate;
		return this;
	}
	public double getFinalProrate() {
		return finalProrate;
	}
	public Activity425 setFinalProrate(double finalProrate) {
		this.finalProrate = finalProrate;
		return this;
	}
	public boolean isSpecialProrate() {
		return specialProrate;
	}
	public Activity425 setSpecialProrate(boolean specialProrate) {
		this.specialProrate = specialProrate;
		return this;
	}
	
}
