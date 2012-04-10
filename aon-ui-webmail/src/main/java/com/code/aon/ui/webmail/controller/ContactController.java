package com.code.aon.ui.webmail.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.USER;

import java.util.LinkedList;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.converter.ContactConverter;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class ContactController extends LdapBasicController implements IWebMailConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContactController.class);
	
	private List<SelectItem> availableContacts;
	
	private Converter converter;

	@Override
	public boolean updateBaseDN(Name parent) {
		String user = NameResolver.getFirstValue(parent);
		String domain = NameResolver.getValue(parent, 2);
		return updateBaseDN(domain, user);
	}

	@Override
	protected void initDAO() {
		AuthPrincipal auth = BasicPrincipal.getAuthPrincipal();
		updateBaseDN(auth.getDomain(), auth.getShortName());
	}

	private boolean updateBaseDN( String domain, String user )  {
		Name userDN = NameResolver.getUserDN(domain, user);
		if ( getLdapDAO().exists(userDN, USER) ) { 
			Name baseDN = NameResolver.getUserAddressBookDN(domain, user);
			if (! getLdapDAO().exists(baseDN, ORGANIZATIONAL_UNIT) ) {
				getLdapDAO().addOrganizationUnit(baseDN);
			}
			getLdapDAO().setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", userDN );	
		return false;				
	}	

	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, CONTACT_DUPLICATED, name);
	}	
	
	public List<SelectItem> getAvailableContacts() {
		return this.availableContacts;
	}
		
	public void updateAvailableContacts() {
		this.availableContacts = new LinkedList<SelectItem>();
    	try {
    		IManagerBean bean = getManagerBean();
			Criteria criteria = new Criteria();
			criteria.addNotEqualExpression(bean.getFieldName(IWebMailAlias.CONTACT_CONTACT_GROUP), Boolean.TRUE);
			criteria.addOrder(bean.getFieldName(IWebMailAlias.CONTACT_DISPLAY_NAME));
			for( ITransferObject to : bean.getList(criteria) ) {
            	Contact contact = (Contact) to;
           		SelectItem item = new SelectItem( contact, contact.getDisplayName() );
           		availableContacts.add( item );
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}
	
	public Converter getConverter() {
		if ( converter == null ) {
			this.converter = new ContactConverter(this);			
		}
		return converter;
	}	

	public void onResetGroup(ActionEvent event) {
		super.onReset(event);
		Contact contact = (Contact) getTo();
		contact.setContactGroup( Boolean.TRUE );
		updateAvailableContacts();
	}
	
}