package com.code.aon.ui.common.domain;

import javax.faces.context.FacesContext;

import com.code.aon.common.domain.IDomainProvider;
import com.code.aon.common.domain.IDomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class FacesDomainProvider implements IDomainProvider {
	
	@Override
	public synchronized Integer getCurrentDomain() {
		IDomainSwitcher d = (IDomainSwitcher) AonUtil.getRegisteredBean("domainSwitcher");
		return d.getDomainId();
	}

	@Override
	public synchronized boolean accept() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		return (ctx != null);
	}

}
