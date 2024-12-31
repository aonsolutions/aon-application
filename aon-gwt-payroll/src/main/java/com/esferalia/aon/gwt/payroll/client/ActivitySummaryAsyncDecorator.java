/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryParams;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ActivitySummaryAsyncDecorator implements ActivitySummaryServiceAsync {

	private ActivitySummaryServiceAsync activitySummaryServiceAsync;

	public ActivitySummaryAsyncDecorator(ActivitySummaryServiceAsync activitySummaryServiceAsync) {
		this.activitySummaryServiceAsync = activitySummaryServiceAsync;
	}

	@Override
	public void getActivitySummary(String domain, String user, ActivitySummaryParams params, AsyncCallback<List<ActivitySummaryObject>> callback) throws IllegalArgumentException {
		AON.start();
		activitySummaryServiceAsync.getActivitySummary(domain, user, params, new AsyncCallbackWrapper<List<ActivitySummaryObject>>(callback));
	}

}
