package com.esferalia.aon.occam.api.model.task;

public enum Priority  {
	
	NONE,
	LOW,
	NORMAL,
	HIGH;
	
    public String getName() {
    	return this.toString();
    }
}