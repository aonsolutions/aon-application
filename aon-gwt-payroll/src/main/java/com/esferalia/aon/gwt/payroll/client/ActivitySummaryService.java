package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("activity_summary")
public interface ActivitySummaryService extends RemoteService {
	
	List<ActivitySummaryObject> getActivitySummary(Date startDate, Date endDate);
	
	Integer getParentDomain();
	
}
