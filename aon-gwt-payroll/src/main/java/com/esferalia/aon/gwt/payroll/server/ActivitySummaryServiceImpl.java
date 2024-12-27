package com.esferalia.aon.gwt.payroll.server;

import java.util.List;

import com.esferalia.aon.gwt.payroll.client.ActivitySummaryService;
import com.esferalia.aon.gwt.payroll.jooq.JooqActivitySummary;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryParams;

@SuppressWarnings("serial")
public class ActivitySummaryServiceImpl extends AonRemoteServiceServlet
		implements ActivitySummaryService {

	@Override
	public List<ActivitySummaryObject> getActivitySummary(ActivitySummaryParams params) throws IllegalArgumentException {
		List<ActivitySummaryObject> list = JooqActivitySummary.getActivitySummary(params);
		return list;
	}

}
