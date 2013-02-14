package com.code.aon.ui.common.domain;

import com.code.aon.common.domain.IDomainProvider;
import com.code.aon.common.domain.IDomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class FacesDomainProvider implements IDomainProvider {
	
	private static String DOMAIN_SWITCHER_CONTROLLER = "domainSwitcher"; 
	
	@Override
	public synchronized Integer getCurrentDomain() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		return d.getDomainId();
	}
	
	@Override
	public Integer getUserDomain() {
		return AonUtil.getAuthPrincipal().getUserDomainId();
	}

	@Override
	public boolean isDomainManagementAvailable() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		return d.isDomainManagementAvailable();
	}

	@Override
	public Integer getParentDomain() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		return d.getParentDomainId();
	}

	@Override
	public boolean isEnableHeredity() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		return d.isEnableHeredity();
	}
	
}