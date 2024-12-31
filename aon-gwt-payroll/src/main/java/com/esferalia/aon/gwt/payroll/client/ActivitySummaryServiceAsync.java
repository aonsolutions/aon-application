package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ActivitySummaryServiceAsync {
	
	void getActivitySummary(String domain, String user, ActivitySummaryParams params, AsyncCallback<List<ActivitySummaryObject>> callback) throws IllegalArgumentException;

}
