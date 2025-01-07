package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryParams;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DomainActivitySumaryServiceAsync {
	
	private ActivitySummaryServiceAsync activitySummaryServiceAsync;
	
	public static DomainActivitySumaryServiceAsync newInstance() {
		ActivitySummaryServiceAsync activitySummaryServiceAsync = GWT.create(ActivitySummaryService.class);
		ActivitySummaryServiceAsync activitySummaryServiceAsyncDecorator = new ActivitySummaryAsyncDecorator(activitySummaryServiceAsync);
		return new DomainActivitySumaryServiceAsync(activitySummaryServiceAsyncDecorator);
	}
	
	private DomainActivitySumaryServiceAsync(ActivitySummaryServiceAsync activitySummaryServiceAsync) {
		this.activitySummaryServiceAsync = activitySummaryServiceAsync;
	}

	
	public void getActivitySummary(ActivitySummaryParams params, AsyncCallback<List<ActivitySummaryObject>> callback) {
		activitySummaryServiceAsync.getActivitySummary(getCurrentDomainName(), getCurrentUser(), params, callback);
	}
	
	// ----------------------------------------------------------------- static
	
	private static String getToken() {
		return Wnd.getToken();
	}
	
	private static String getCurrentUser() {
		return Wnd.getCurrentUser();
	}

	private static String getCurrentDomainName() {
		return Wnd.getCurrentDomainNameURL();
	}

	

}
