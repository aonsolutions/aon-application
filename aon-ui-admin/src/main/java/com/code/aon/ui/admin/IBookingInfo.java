package com.code.aon.ui.admin;

import java.util.List;

import com.code.aon.common.ManagerBeanException;

public interface IBookingInfo {

	public void init() throws ManagerBeanException;
	
	public void save() throws ManagerBeanException;
	
	public List<DomainModuleInfo> getBookingModules();
	
	public List<DomainModuleInfo> getDisplayModules();
	
}