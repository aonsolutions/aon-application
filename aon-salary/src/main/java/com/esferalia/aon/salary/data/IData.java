package com.esferalia.aon.salary.data;

import java.io.Serializable;
import java.util.Date;

public interface IData extends Serializable{
	public String getName();
	public String getValue();
	public Date getStartDate();
	public Date getEndDate();
}
