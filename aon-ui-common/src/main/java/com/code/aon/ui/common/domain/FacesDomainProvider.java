package com.code.aon.ui.common.domain;

import java.util.Collection;

import javax.faces.context.FacesContext;

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
	public synchronized boolean accept() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return (ctx != null);
	}

	@Override
	public boolean isParentDomain() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		return d.isParentDomain();
	}

	@Override
	public boolean isDomainManagementAvailable() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		return d.isDomainManagementAvailable();
	}

	@Override
	public Collection<Integer> getDomainFilter() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		return d.getDomainFilter();
	}

}
