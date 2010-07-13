package com.code.aon.ui.webmail.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.bridge.plugin.Utils;
import com.code.aon.common.BasicManagerBean;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.GroupContact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class ContactController extends BasicController implements WebMailConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContactController.class);

	private LdapDAO contactDAO;
	
	private LdapDAO groupContactDAO;
	
	private BasicManagerBean ldapManagerBean;
	
	private List<SelectItem> groupContacts;

	private LdapDAO getDAO( Class<? extends ITransferObject> _class ) {
		LdapDAO dao = new LdapDAO(_class);
		AuthPrincipal principal = Utils.getAuthPrincipal();
		Name baseDN = NameResolver.getUserAddressBookDN(principal.getDomain(), principal.getShortName());
		LOGGER.info( "Contact DAO DN: {}", baseDN );
		dao.setBaseDN( baseDN );
		return dao;
	}

	public LdapDAO getGroupContactDAO() {
		if ( this.groupContactDAO == null ) {
			this.groupContactDAO = getDAO(GroupContact.class);
		}
		return this.groupContactDAO;
	}
	
	public LdapDAO getContactDAO() {
		if ( this.contactDAO == null ) {
			this.contactDAO = getDAO(Contact.class);
		}
		return this.contactDAO;
	}		
	
	@Override
	public IManagerBean getManagerBean() throws ManagerBeanException {
		if (this.ldapManagerBean == null) {
			this.ldapManagerBean = new BasicManagerBean(getContactDAO());
		}
		return this.ldapManagerBean;
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

	@Override
	public void accept(ActionEvent event) {		
		try {
			ITransferObject to = getTo();
			Name currentId = getContactDAO().calculateDN(to);
			if (! isNew() ) {
				Name oldId = (Name) this.savedToId;
				if (! oldId.equals(currentId) ) {
					if ( getContactDAO().exists(currentId) ) {
						AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, CONTACT_DUPLICATED );
						return;
					}			
					getManagerBean().setId( to, oldId );
					getManagerBean().remove( to );
					getManagerBean().setId( to, null );
					setNew(true);
				}
			}
		} catch (ManagerBeanException e) {
	        LOGGER.error(">>>> accept", e );
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		} catch (DAOException e) {
	        LOGGER.error(">>>> accept ", e );
	        addMessage(e.getMessage());
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}				
		super.accept(event);
	}
	
}