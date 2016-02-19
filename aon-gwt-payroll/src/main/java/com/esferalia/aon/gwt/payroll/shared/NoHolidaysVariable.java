package com.esferalia.aon.gwt.payroll.shared;

import java.util.Date;

import com.esferalia.aon.gwt.common.shared.DateUtils;

public class NoHolidaysVariable extends StringVariable {
	
	private Double days;
	private int prevDays ;
	private SalaryDraft salaryDraft;
	
	public void setDays(Double days) {
		this.days = days;
	}
	
	public void setSalaryDraft(SalaryDraft salaryDraft) {
		this.salaryDraft = salaryDraft;
	}
	
	public void setPrevDays(int prevDays) {
		this.prevDays = prevDays;
	}
	
	
	// ------------------------------------------------------------------------
	
	@Override
	public Date getEndDate() {
		Date endDate = DateUtils.copyDateOnly(salaryDraft.getIssueDate());
		return DateUtils.addDays2Date(endDate, (int)Math.ceil(days + prevDays));
	}
	
	@Override
	public Date getStartDate() {
		Date startDate = DateUtils.copyDateOnly(salaryDraft.getIssueDate());
		return DateUtils.addDays2Date(startDate, prevDays + 1);
	}
	
	@Override
	public String getExpression() {
		return days != null ? Double.toString(days) : null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Variable))
			return false;
		Variable var = (Variable) obj;
		
		Date myEndDate = getEndDate();
		Date myStartDate = getStartDate();
		Date otherEndDate = var.getEndDate();
		Date otherStartDate = var.getStartDate();

		return ((name == var.name) || ((name != null) && name.equals(var.name)))
				&& ((myEndDate == otherEndDate) || ((myEndDate != null) && myEndDate.equals(otherEndDate)))
				&& ((myStartDate == otherStartDate) || ((myStartDate != null) && myStartDate.equals(otherStartDate)))
				;
	}
	
}
