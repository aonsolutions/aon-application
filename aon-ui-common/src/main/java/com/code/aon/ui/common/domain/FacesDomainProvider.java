package com.code.aon.ui.common.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.domain.IDomainProvider;
import com.code.aon.common.domain.IDomainSwitcher;
import com.code.aon.ui.util.AonUtil;

public class FacesDomainProvider implements IDomainProvider {
	
	public static String DOMAIN_SWITCHER_CONTROLLER = "domainSwitcher";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FacesDomainProvider.class);
	
	private ThreadLocal<IDomainSwitcher> domainSwitcher = new ThreadLocal<IDomainSwitcher>();
	
	public void setDomainSwitcher( IDomainSwitcher ds ) {
		if ( ds != null ) {
			domainSwitcher.set(ds);
		} else {
			domainSwitcher.remove();
		}
	}
	
	private IDomainSwitcher getDomainSwitcher() {
		IDomainSwitcher ds = domainSwitcher.get();
		if ( ds == null ) {
			ds = (IDomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER_CONTROLLER);
		}
		return ds;
	}
	
	@Override
	public synchronized Integer getCurrentDomain() {
		return getDomainSwitcher().getDomainId();
	}
	
	@Override
	public Integer getUserDomain() {
		return AonUtil.getAuthPrincipal().getUserDomainId();
	}

	@Override
	public boolean isDomainManagementAvailable() {
		return getDomainSwitcher().isDomainManagementAvailable();
	}

	@Override
	public Integer getParentDomain() {
		return getDomainSwitcher().getParentDomainId();
	}

	@Override
	public boolean isEnableHeredity() {
		return getDomainSwitcher().isEnableHeredity();
	}
	
}