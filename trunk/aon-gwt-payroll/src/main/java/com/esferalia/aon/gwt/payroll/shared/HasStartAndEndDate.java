package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

public interface HasStartAndEndDate {
	
	Date getStartDate();
	
	Date getEndDate();
	
	void setStartDate(Date startDate);

	void setEndDate(Date endDate);
}
