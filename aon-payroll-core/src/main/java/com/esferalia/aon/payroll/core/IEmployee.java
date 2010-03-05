package com.esferalia.aon.payroll.core;

import java.util.Date;

public interface IEmployee {

	ICompany getCompany();
	void setCompany(ICompany company);
	
	IPerson getPerson();
	void setPerson(IPerson person);
	
	Date getStartDate();
	void setStartDate();

	Date getEndDate();
	void setEndDate();
	
}
