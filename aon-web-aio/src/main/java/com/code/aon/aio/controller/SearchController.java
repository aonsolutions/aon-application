package com.code.aon.aio.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;

import java.io.Serializable;

import com.code.aon.AonVersion;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class SearchController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;


	private String filter; 
	
	
	public String getFilter() {
		return filter;
	}
	
	public void setFilter(String filter) {
		this.filter = filter;
		getDomainSwitcher().setFilter(filter);
	}
	
	public DomainSwitcher getDomainSwitcher() {
		return (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
	}
	
	
	
}
