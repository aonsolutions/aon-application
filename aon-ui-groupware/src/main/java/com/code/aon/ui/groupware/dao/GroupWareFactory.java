package com.code.aon.ui.groupware.dao;

import java.security.Principal;
import java.util.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.code.aon.common.bean.BeanConfig;
import com.code.aon.common.dao.IDAO;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.dao.ldap.LdapDAOFactory;
import com.code.aon.jaas.auth.AuthPrincipal;

public class GroupWareFactory extends LdapDAOFactory {
	
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
	
	private AuthPrincipal getPrincipal() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		AuthPrincipal user = null;
		Principal principal = ec.getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			user = (AuthPrincipal) principal;
		} else {
			user = new AuthPrincipal( principal.getName() );
		}
		LOGGER.info( "Current User:" + user );
		return user;
	}

	private String getUserDN( AuthPrincipal principal ) {
		return "uid=" + principal.getShortName() + ",ou=users,cn=" + principal.getDomain() + ",ou=domains";
	}

	private String getAddressBookDN() {
		AuthPrincipal principal = getPrincipal();
		return "ou=addressbook," + getUserDN(principal); 
	}
	
	/**
	 * Gets the dAO.
	 * 
	 * @param config the config
	 * 
	 * @return the dAO
	 */
	public IDAO getContactDAO( BeanConfig config ) {
		LdapDAO dao = getDAO(config);
		String baseDN = getAddressBookDN();
		LOGGER.info( "Contact DAO DN:" + baseDN );
		dao.setBaseDN( baseDN );
		return dao;
	}
	
}
