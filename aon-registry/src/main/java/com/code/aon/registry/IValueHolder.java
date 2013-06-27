package com.code.aon.registry;

import java.util.Date;

public interface IValueHolder {

	public String getText();
	public void setText(String text);
	
	public Date getDate();
	public void setDate(Date date);

	public Double getNumber();
	public void setNumber(Double number);
	
	public void copyValues( IValueHolder vh );
	
}