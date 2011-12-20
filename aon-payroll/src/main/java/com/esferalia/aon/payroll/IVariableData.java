package com.esferalia.aon.payroll;

import java.util.Date;

import com.esferalia.aon.salary.expression.IExpression;

public interface IVariableData extends IExpression {
	
	public Integer getId();
	public void setId(Integer id);
	
	public String getName();
	public void setName(String name);

	public String getExpression();
	public void setExpression(String expression);

    public Date getStartDate();
	public void setStartDate(Date startDate);

    public Date getEndDate();
	public void setEndDate(Date endDate);
}
