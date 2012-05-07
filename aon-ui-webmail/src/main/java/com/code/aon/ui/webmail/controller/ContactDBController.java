package com.code.aon.ui.webmail.controller;

import static com.code.aon.ui.webmail.controller.IWebMailConstants.BUNDLE_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.CONTACT_DUPLICATED;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.converter.TransferObjectConverter;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.IContact;
import com.code.aon.webmail.ISignature;
import com.code.aon.webmail.db.Contact;
import com.code.aon.webmail.db.ContactDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class ContactDBController extends MailDBController implements IContactController {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContactDBController.class);

	private List<SelectItem> availableContacts;
	
	public ContactDBController() {
		TransferObjectConverter converter = new TransferObjectConverter();
		converter.setType(Contact.class);
		setConverter(converter);
	}

	@Override
	public void onReset(ActionEvent event) {
		super.onReset(event);
		Contact contact = (Contact) getTo();
		contact.setContactGroup( Boolean.FALSE );
		contact.setUser(getUser());
	}


	public void onResetGroup(ActionEvent event) {
		onReset(event);
		Contact contact = (Contact) getTo();
		contact.setContactGroup( Boolean.TRUE );
		updateAvailableContacts();
	}
	
	@Override
	protected String getDuplicatedMessage( String name ) {
		return AonUtil.getMessage(BUNDLE_NAME, CONTACT_DUPLICATED, name);
	}
	
	@Override
	protected String getToName() {
		ISignature signature = (ISignature) getTo();
		return signature.getName();
	}

	@Override
	protected String getUserAlias() throws ManagerBeanException {
		return getFieldName( IEntityAlias.CONTACT_USER_ID );
	}

	@Override
	protected String getNameAlias() throws ManagerBeanException {
		return getFieldName( IEntityAlias.CONTACT_DISPLAY_NAME );
	}
	
	@Override
	public List<SelectItem> getAvailableContacts() {
		return this.availableContacts;
	}

	@Override
	public void updateAvailableContacts() {
		this.availableContacts = new LinkedList<SelectItem>();
    	try {
			Criteria criteria = new Criteria();
			completeCriteria(criteria);
			criteria.addNotNullExpression(getFieldName(IEntityAlias.CONTACT_CONTACT_DATA_ID));
			criteria.addOrder(getFieldName(IEntityAlias.CONTACT_DISPLAY_NAME));
			for( ITransferObject to : getManagerBean().getList(criteria) ) {
            	Contact contact = (Contact) to;
           		SelectItem item = new SelectItem( contact, contact.getDisplayName() );
           		availableContacts.add( item );
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}		
	}

	@Override
	public String isUsed(IContact contact) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ContactDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTACT_DETAIL_CONTACT_ID), ((Contact)contact).getId());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				List<String> groups = new LinkedList<String>();
				for( ITransferObject to : list ) {
					ContactDetail cd = (ContactDetail) to;
					groups.add( cd.getContactGroup().getDisplayName() );
				}
				return StringUtils.join( groups, ", " );				
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("isUsed for " + contact, e);
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}	
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<IContact> getEmailContacts() {
    	try {
    		Criteria criteria = new Criteria();
    		setUser( UserUtils.getInstance().getLoggedUser() );
    		completeCriteria(criteria);
    		criteria.addNotNullExpression("Contact.contactData.email");
    		criteria.addOrder(getFieldName(IEntityAlias.CONTACT_DISPLAY_NAME));
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
    		setUser( UserUtils.getInstance().getLoggedUser() );
    		completeCriteria(criteria);
			String displayName = getFieldName(IEntityAlias.CONTACT_DISPLAY_NAME);
			String email = "Contact.contactData.email";
			Expression exp1 = ExpressionUtilities.getLikeExpression(displayName, text + "%");
			Expression exp2 = ExpressionUtilities.getLikeExpression(email, text + "%" );
			criteria.addExpression(ExpressionUtilities.getOrExpression(exp1, exp2));
			criteria.addOrder(displayName);
			return (List) getManagerBean().getList(criteria);
    	} catch (ManagerBeanException e) {
    		LOGGER.error( "Error getting suggestion emails", e );
		}				
		return null;
	}
	
}
