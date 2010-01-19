package com.code.aon.ui.ecommerce.hibernate;

import javax.faces.context.FacesContext;

import com.code.aon.bridge.session.DomainResolver;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;
import com.code.aon.ui.util.AonUtil;

public class DomainSessionNameProvider implements ISessionFactoryNameProvider {

	@Override
	public String getName(String pojoClass) {
    	DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DomainConfigurationFactory.DOMAIN_RESOLVER);
    	String domain = resolver.getDomain();
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	String context = ctx.getExternalContext().getRequestContextPath();
		return domain + "/" + context;
	}

}
