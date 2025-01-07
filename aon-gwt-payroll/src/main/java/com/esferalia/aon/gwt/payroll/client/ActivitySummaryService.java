package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryParams;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("activity_summary")
public interface ActivitySummaryService extends RemoteService {
	
	List<ActivitySummaryObject> getActivitySummary(String domain, String user, ActivitySummaryParams params) throws IllegalArgumentException;

}
