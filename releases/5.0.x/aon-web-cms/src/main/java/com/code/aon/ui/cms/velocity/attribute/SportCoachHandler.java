package com.code.aon.ui.cms.velocity.attribute;

import com.code.aon.cms.SportCoach;

public class SportCoachHandler {

	private String job;
	
	private String name;
	
	public SportCoachHandler(SportCoach sportCoach){
		this.job = sportCoach.getJob();
		this.name = sportCoach.getName();
	}

	public String getJob() {
		return job;
	}

	public String getName() {
		return name;
	}
	
}
