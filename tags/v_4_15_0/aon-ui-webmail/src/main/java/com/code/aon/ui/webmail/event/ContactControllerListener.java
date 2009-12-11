package com.code.aon.ui.webmail.event;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.naming.Name;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.dao.ldap.LdapDAO;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.ContactController;
import com.code.aon.webmail.Contact;
import com.code.aon.webmail.dao.IWebMailAlias;

public class ContactControllerListener extends ControllerAdapter implements WebMailConstants {

	private static final Logger LOGGER = Logger.getLogger(ContactControllerListener.class.getName());
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		ContactController controller = (ContactController) event.getController();
		Contact contact = (Contact) controller.getTo();
		if ( contact.getContactGroup() ) {
			controller.updateAvailableContacts();	
		}
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
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		ContactController controller = (ContactController) event.getController();
		LdapDAO contactDAO = controller.getContactDAO();
		try {
			Name currentId = contactDAO.calculateDN(controller.getTo());
			if ( contactDAO.exists(currentId) ) {
				String message = AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, CONTACT_DUPLICATED );
				throw new AbortProcessingException( message );
			}
		} catch (DAOException e) {
	        LOGGER.severe(">>>> beforeBeanAdded " + e.getMessage());
	        AonUtil.addErrorMessage( e.getMessage() );
	        throw new AbortProcessingException(e.getMessage(), e);	        
		}
	}

	private String isUsed( ContactController controller, Contact contact ) {
		try {
			IManagerBean bean = controller.getManagerBean();
			Criteria criteria = new Criteria();
			String contacts = bean.getFieldName(IWebMailAlias.CONTACT_CONTACTS);
			criteria.addEqualExpression( contacts, contact.getId() );
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				List<String> groups = new LinkedList<String>();
				for( ITransferObject to : list ) {
					groups.add( ((Contact)to).getDisplayName() );
				}
				return StringUtils.join( groups, ", " );
			}
    	} catch (ManagerBeanException e) {
    		LOGGER.log( Level.SEVERE, "Error getting suggestion emails", e );
		}	
    	return null;
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		ContactController controller = (ContactController) event.getController();		
		Contact contact = (Contact) controller.getTo();
		String list = isUsed( controller, contact);
		if ( list != null ) {
			String message = AonUtil.addErrorMessageFromBundle(BUNDLE_NAME, CONTACT_USED, contact.getDisplayName(), list );
			throw new AbortProcessingException( message );			
		}
	}

}