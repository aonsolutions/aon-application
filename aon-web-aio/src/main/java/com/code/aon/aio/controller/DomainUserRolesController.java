package com.code.aon.aio.controller;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.User;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;

public class DomainUserRolesController {
	
	private DomainUserRoles dur;
	
	public DomainUserRolesController() {
		loadDomainUserRoles();
	}
	
	public DomainUserRoles getDur() {
		if(dur == null) {
			loadDomainUserRoles();
		}
		return dur;
	}
	
	public void setDur(DomainUserRoles dur) {
		this.dur = dur;
	}
	
	public boolean isInvoice() {
		return getDur().isInvoice();
	}
	
	private void loadDomainUserRoles() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		User user = UserUtils.getInstance().getLoggedUser();
		this.dur = SECURITY.getDomainUserRoles(domainName, domainId, user.getLogin(), user.getId());			
	}
	
	
}
