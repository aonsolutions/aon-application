package com.transtools.jdbc.metadata;

public interface IPropertiesBean {
	public String[] getPropertyNames();
	public String getProperty(String propertyName);
	public String getProperty(int idx);
	public void setProperty(String propertyName, String value);
	public void setProperty(int idx, String value);
	public int getPropertyIndex(String propertyName);
	
}
