package com.code.aon.ui.groupware.dao;

import java.security.Principal;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.code.aon.common.bean.BeanConfig;
import com.code.aon.common.dao.IDAO;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.AonDN;
import com.code.aon.ldap.DistinguishedName;
import com.code.aon.ui.config.util.UserUtils;

public class GroupWareFactory {
	
	private static final Logger LOGGER = Logger.getLogger(GroupWareFactory.class.getName());

	private static final GroupWareFactory SINGLETON = new GroupWareFactory(); 
	
	/**
	 * Gets the single instance of LdapDAOFactory.
	 * 
	 * @return single instance of LdapDAOFactory
	 */
	public static GroupWareFactory getInstance() {
		return SINGLETON;
	}
	
	/**
	 * Gets the dAO.
	 * 
	 * @param config the config
	 * 
	 * @return the dAO
	 */
	public IDAO getContactDAO( BeanConfig config ) {
		LdapDAO dao = new LdapDAO(config.getPojoClass());
		AuthPrincipal principal = UserUtils.getInstance().getPrincipal();
		DistinguishedName baseDN = AonDN.getUserAddressBookDN(principal.getDomain(), principal.getShortName());
		LOGGER.info( "Contact DAO DN:" + baseDN );
		dao.setBaseDN( baseDN.toString() );
		return dao;
	}
	
}
