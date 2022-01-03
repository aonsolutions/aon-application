package com.esferalia.aon.omega;

import java.util.List;

public class Activity {

	Integer activity;		// Activity Id (se rellena en el proceso)
	
	String description;		// Descipcion centro de trabajo
	String iae;				// IAE formato (seccion-epigrafe) ej.: 1-942.1
	String cnae;			// CNAE codigo
	String cnae2009;		// CNAE2009 codigo
	
	List<Ccc> cccs;
	
	protected Activity() {
		super();
	}

	public Integer getActivity() {
		return activity;
	}

	public Activity setActivity(Integer activity) {
		this.activity = activity;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Activity setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getIae() {
		return iae;
	}

	public Activity setIae(String iae) {
		this.iae = iae;
		return this;
	}

	public String getCnae() {
		return cnae;
	}

	public Activity setCnae(String cnae) {
		this.cnae = cnae;
		return this;
	}

	public String getCnae2009() {
		return cnae2009;
	}

	public Activity setCnae2009(String cnae2009) {
		this.cnae2009 = cnae2009;
		return this;
	}

	public List<Ccc> getCccs() {
		return cccs;
	}

	public Activity setCccs(List<Ccc> cccs) {
		this.cccs = cccs;
		return this;
	}
	
}
