package com.code.aon.ui.webmail.controller;

import static com.code.aon.ldap.IAonObjectClasses.ORGANIZATIONAL_UNIT;
import static com.code.aon.ldap.IAonObjectClasses.USER;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.GroupContact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class ContactController extends LdapBasicController implements IWebMailConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

	private LdapDAO groupContactDAO;
	
	private List<SelectItem> groupContacts;

	@Override
	public boolean updateBaseDN(Name parent) {
		String user = NameResolver.getFirstValue(parent);
		String domain = NameResolver.getValue(parent, 2);
		return updateBaseDN(domain, user);
	}
	
	@Override
	protected void initDAO() {
		this.groupContactDAO = new LdapDAO(GroupContact.class);
		AuthPrincipal auth = Utils.getAuthPrincipal();
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
			this.groupContactDAO.setBaseDN( baseDN );
			return true;
		}
		LOGGER.warn( "LDAP entry not found: {}", userDN );	
		return false;				
	}	

	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, CONTACT_DUPLICATED, name);
	}	
	
	public LdapDAO getGroupContactDAO() {
		return this.groupContactDAO;
	}
	
	public List<SelectItem> getAvailableContacts() {
		return this.groupContacts;
	}
		
	public void updateAvailableContacts() {
		this.groupContacts = new LinkedList<SelectItem>();
    	try{
    		LdapDAO dao = getGroupContactDAO();
			Criteria criteria = new Criteria();
			criteria.addNotNullExpression(dao.getFieldName(IWebMailAlias.GROUP_CONTACT_EMAIL));
			criteria.addOrder(dao.getFieldName(IWebMailAlias.GROUP_CONTACT_DISPLAY_NAME));
			for( ITransferObject to : dao.getList(criteria) ) {
            	GroupContact gc = (GroupContact) to;
           		SelectItem item = new SelectItem( gc, gc.getDisplayName() );
           		groupContacts.add( item );
			}
    	} catch (DAOException e) {
    		LOGGER.error( e.getMessage(), e );
		}		
	}
	
	public Converter getGroupContactConverter() {
		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {
				if (! StringUtils.isEmpty(value) ) {
					for( SelectItem item : groupContacts ) {
						GroupContact gc = (GroupContact) item.getValue();
						if ( gc.getDisplayName().equals(value) ) {
							return gc;
						}
					}
				}
				return null;
			}

			@Override
			public String getAsString(FacesContext context,	UIComponent component, Object value) {
				if (value == null) {
					return null;
				}
				GroupContact gc = (GroupContact) value;
				return gc.getDisplayName();
			}
			
		};
	}

	public void onResetGroup(ActionEvent event) {
		super.onReset(event);
		Contact contact = (Contact) getTo();
		contact.setContactGroup( Boolean.TRUE );
		updateAvailableContacts();
	}
	
}