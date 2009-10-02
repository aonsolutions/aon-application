package com.code.aon.ui.webmail.controller;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.Contact;

public class ContactController extends BasicController {
	
	private static final String RESOURCE_BUNDLE = "com.code.aon.ui.groupware.i18n.messages";
	
	private static final String CONTACT_DUPLICATED = "contact_duplicated";
	
	private static final Logger LOGGER = Logger.getLogger(ContactController.class.getName());

	private LdapDAO dao;
	
	private BasicManagerBean ldapManagerBean;

	public LdapDAO getDAO( AuthPrincipal principal ) {
		LdapDAO dao = new LdapDAO(Contact.class);
		Name baseDN = NameResolver.getUserAddressBookDN(principal.getDomain(), principal.getShortName());
		LOGGER.info( "Contact DAO DN:" + baseDN );
		dao.setBaseDN( baseDN );
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
	
	private void addMessageExpression( String messageId ) {
		Locale locale = AonUtil.getCurrentLocale();
		ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BUNDLE, locale);
		addMessage( bundle.getString(messageId) );
	}
	
	@Override
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		Contact contact = (Contact) getTo();
		if (! StringUtils.isEmpty(contact.getName()) ) {
			contact.setOutlookName( contact.getName() );
			contact.setName(null);
		}
		if (! StringUtils.isEmpty(contact.getCity()) ) {
			contact.setOutlookCity( contact.getCity() );
			contact.setCity(null);
		}
		if (! StringUtils.isEmpty(contact.getCategory()) ) {
			contact.setTitle( contact.getCategory() );
			contact.setCategory(null);
		}
	}

	@Override
	public void accept(ActionEvent event) {		
		try {
			Name currentId = this.dao.calculateDN(getTo());
			if ( isNew() ) {
				if ( dao.exists(currentId) ) {
					addMessageExpression(CONTACT_DUPLICATED);
		            return;
				}			
			} else {
				Name oldId = (Name) this.savedToId;
				if (! oldId.equals(currentId) ) {
					if ( dao.exists(currentId) ) {
						addMessageExpression(CONTACT_DUPLICATED);
						return;
					}			
					getManagerBean().setId( getTo(), oldId );
					getManagerBean().remove( getTo() );
					getManagerBean().setId( getTo(), null );
					setNew(true);
				}
			}
		} catch (ManagerBeanException e) {
	        LOGGER.severe(">>>> accept " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		} catch (DAOException e) {
	        LOGGER.severe(">>>> accept " + e.getMessage());
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}				
		super.accept(event);
	}
	
}