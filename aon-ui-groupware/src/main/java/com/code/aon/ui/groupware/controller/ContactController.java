package com.code.aon.ui.groupware.controller;

import java.util.logging.Logger;

import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.IDAO;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.groupware.Contact;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;

public class ContactController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(ContactController.class.getName());

	private BasicManagerBean ldapManagerBean;

	public IDAO getDAO() {
		LdapDAO dao = new LdapDAO(Contact.class);
		AuthPrincipal principal = UserUtils.getInstance().getPrincipal();
		DistinguishedName baseDN = AonDN.getUserAddressBookDN(principal.getDomain(), principal.getShortName());
		LOGGER.info( "Contact DAO DN:" + baseDN );
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}
	
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			this.ldapManagerBean = new BasicManagerBean(getDAO());
		}
		return this.ldapManagerBean;
	}	
	
}