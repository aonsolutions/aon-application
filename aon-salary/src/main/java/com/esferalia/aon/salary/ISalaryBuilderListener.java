package com.esferalia.aon.salary;

public interface ISalaryBuilderListener {

	public boolean isDebugEnabled();
	
	public void onError(String msg);
	public void onWarning(String msg); 
	public void onInfo(String msg);
	public void onDebug(String msg);
	 
	
}
