package com.code.aon.ui.webmail.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.USER;

import java.util.LinkedList;
import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.BasicPrincipal;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.converter.ContactConverter;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.IContact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class ContactController extends LdapBasicController implements IWebMailConstants, IContactController {
	
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
	public String isUsed(IContact contact) {
		try {
			Criteria criteria = new Criteria();
			String contacts = getFieldName(IWebMailAlias.CONTACT_CONTACTS);
			criteria.addEqualExpression( contacts, ((Contact)contact).getId() );
			List<ITransferObject> list = getManagerBean().getList(criteria);
			if (! list.isEmpty() ) {
				List<String> groups = new LinkedList<String>();
				for( ITransferObject to : list ) {
					groups.add( ((Contact)to).getDisplayName() );
				}
				return StringUtils.join( groups, ", " );
			}
    	} catch (ManagerBeanException e) {
    		LOGGER.error( "Error getting suggestion emails", e );
		}	
    	return null;
	}
	
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<IContact> getEmailContacts() {
    	try {
    		Criteria criteria = new Criteria();			
    		String email = getFieldName(IWebMailAlias.CONTACT_EMAIL);
    		String contacts = getFieldName(IWebMailAlias.CONTACT_CONTACTS);
    		Expression exp1 = ExpressionUtilities.getNotNullExpression(email);
    		Expression exp2 = ExpressionUtilities.getNotNullExpression(contacts);
    		criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));				
    		criteria.addOrder(getFieldName(IWebMailAlias.CONTACT_DISPLAY_NAME));
    		return (List) getManagerBean().getList(criteria);
    	} catch (ManagerBeanException e) {
    		LOGGER.error( e.getMessage(), e );
		}
    	return null;
    }

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<IContact> suggestionEmails(String text) {
		try {
			Criteria criteria = new Criteria();
			String displayName = getFieldName(IWebMailAlias.CONTACT_DISPLAY_NAME);
			String email = getFieldName(IWebMailAlias.CONTACT_EMAIL);
			String contacts = getFieldName(IWebMailAlias.CONTACT_CONTACTS);
			Expression exp1 = ExpressionUtilities.getLikeExpression(displayName, text + "*");
			Expression exp2 = ExpressionUtilities.getLikeExpression(email, text + "*");
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			Expression exp3 = ExpressionUtilities.getNotNullExpression(email);
			Expression exp4 = ExpressionUtilities.getNotNullExpression(contacts);
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp3, exp4));
			criteria.addOrder(displayName);
			return (List) getManagerBean().getList(criteria);
    	} catch (ManagerBeanException e) {
    		LOGGER.error( "Error getting suggestion emails", e );
		}				
		return null;
	}
	
}