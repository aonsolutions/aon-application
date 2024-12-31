package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.util.List;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.client.ActivitySummaryService;
import com.esferalia.aon.gwt.payroll.jooq.JooqActivitySummary;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryParams;

@SuppressWarnings("serial")
public class ActivitySummaryServiceImpl extends AonRemoteServiceServlet
		implements ActivitySummaryService {

	@Override
	public List<ActivitySummaryObject> getActivitySummary(String domainName, String userLogin, ActivitySummaryParams params) throws IllegalArgumentException {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			List<ActivitySummaryObject> list = JooqActivitySummary.getActivitySummary(connection, domainId, parentDomainId, userId, params);
			return list;
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		
		
	}

}
