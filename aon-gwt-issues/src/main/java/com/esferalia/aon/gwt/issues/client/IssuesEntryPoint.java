package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.common.shared.AonData;
import com.google.gwt.core.client.EntryPoint;

public class IssuesEntryPoint implements EntryPoint {
	
	private static final String ENTRY_POINT_PARAM = "entryPoint";
	private static final String ISSUES = "issues";
	private static final String TASK_STAT = "taskStat";
	
	AonData aonData;

	
	public IssuesEntryPoint(AonData aonData) {
		this.aonData = aonData;
	}
	
	public IssuesEntryPoint() {

	}
	
	public void onModuleLoad(String entryPoint){	
		if(TASK_STAT.equalsIgnoreCase(entryPoint)) {
			new TaskStat(aonData).onModuleLoad();
		} else new Issues(aonData).onModuleLoad();
	}
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(ISSUES);
	}
}
