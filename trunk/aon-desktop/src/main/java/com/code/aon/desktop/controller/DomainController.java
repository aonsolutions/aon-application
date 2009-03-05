package com.code.aon.desktop.controller;

import java.util.logging.Logger;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.desktop.Domain;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ui.form.BasicController;

public class DomainController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(DomainController.class.getName());

	private LdapDAO dao;
	
	private BasicManagerBean ldapManagerBean;

	public LdapDAO getDAO( AuthPrincipal principal ) {
		LdapDAO dao = new LdapDAO(Domain.class);
		DistinguishedName baseDN = AonDN.getDomainsDN();
		LOGGER.info( "Domain DAO DN:" + baseDN );
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			this.dao = getDAO(Utils.getAuthPrincipal());
			this.ldapManagerBean = new BasicManagerBean(dao);
		}
		return this.ldapManagerBean;
	}	
	
}