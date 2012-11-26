package com.esferalia.aon.salary;

import com.code.aon.common.enumeration.IResourceable;


public interface ISalaryItem<T extends Enum<T> & IResourceable> {
	
	public T getType();
	
	public String getName();
	
	public double getAmount();

	public String getDescription();
	

}
