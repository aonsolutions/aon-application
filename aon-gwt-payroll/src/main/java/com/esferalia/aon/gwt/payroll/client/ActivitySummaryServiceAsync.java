package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ActivitySummaryServiceAsync {

	void getActivitySummary(Integer domainId, Date startDate, Date endDate,
			Boolean onlyStarts, Boolean onlyEnds,
			AsyncCallback<List<ActivitySummaryObject>> callback);
	
	void getParentDomain(AsyncCallback<Integer> callback);

}
