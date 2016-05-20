package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ActivitySummaryServiceAsync {
	
	void getParentDomainId(AsyncCallback<Integer> callback);
	
	void getDomainId(AsyncCallback<Integer> callback);
	
	void getDomainName(AsyncCallback<String> callback);

	void getActivitySummary(String domainName, boolean parentDomain,
			Integer domainId, Date startDate, Date endDate, Boolean starts,
			Boolean ends, Boolean salary, Boolean salaryExtra,
			Boolean salarySettle, Boolean salaryOther, Boolean itCommonDisease,
			Boolean itOccupationalDisease, Boolean itMaternity,
			Boolean itOther, AsyncCallback<List<ActivitySummaryObject>> callback);

}
