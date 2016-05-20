package com.esferalia.aon.gwt.payroll.server;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.client.ActivitySummaryService;
import com.esferalia.aon.gwt.payroll.jooq.JooqActivitySummary;
import com.esferalia.aon.gwt.payroll.shared.ActivitySummaryObject;

@SuppressWarnings("serial")
public class ActivitySummaryServiceImpl extends AonRemoteServiceServlet
		implements ActivitySummaryService {

	@Override
	public Integer getParentDomainId() {
		try {
			initFacesContext();
			return getParentDomainID();
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public Integer getDomainId() {
		try {
			initFacesContext();
			return getDomainID();
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public String getDomainName() {
		try {
			initFacesContext();
			return getAuthPrincipal().getDomain();
		} finally {
			releaseFacesContext();
		}
	}

	@Override
	public List<ActivitySummaryObject> getActivitySummary(String domainName,
			boolean parentDomain, Integer domainId, Date startDate,
			Date endDate, Boolean starts, Boolean ends, Boolean salary,
			Boolean salaryExtra, Boolean salarySettle, Boolean salaryOther,
			Boolean itCommonDisease, Boolean itOccupationalDisease,
			Boolean itMaternity, Boolean itOther) {

		List<ActivitySummaryObject> list = JooqActivitySummary
				.getActivitySummary(domainName, parentDomain, domainId,
						startDate, endDate, starts, ends, salary, salaryExtra,
						salarySettle, salaryOther, itCommonDisease,
						itOccupationalDisease, itMaternity, itOther);
		return list;

	}

}
