package com.code.aon.ui.ecommerce.hibernate;

import static com.code.aon.ui.common.ICommonConstants.DOMAIN_RESOLVER_CONTROLLER_NAME;

import javax.faces.context.FacesContext;

import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.util.AonUtil;

public class DomainSessionNameProvider implements ISessionFactoryNameProvider {

	@Override
	public String getName(String pojoClass) {
		DomainResolver resolver = (DomainResolver) AonUtil.getRegisteredBean(DOMAIN_RESOLVER_CONTROLLER_NAME);
    	String domain = resolver.getDomain();
    	FacesContext ctx = FacesContext.getCurrentInstance();
    	String context = ctx.getExternalContext().getRequestContextPath();
		return domain + "/" + context;
	}

}
