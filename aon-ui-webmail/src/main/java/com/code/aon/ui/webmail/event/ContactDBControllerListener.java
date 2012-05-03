package com.code.aon.ui.webmail.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.webmail.db.Contact;
import com.code.aon.webmail.db.ContactData;

public class ContactDBControllerListener extends ContactControllerListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(ContactDBControllerListener.class);
	
	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		Contact contact = (Contact) event.getController().getTo();
		try {
			if ( contact.getContactGroup() ) {
				
			} else {
				IManagerBean bean = BeanManager.getManagerBean(ContactData.class);
				bean.insert(contact.getContactData());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		Contact contact = (Contact) event.getController().getTo();
		try {
			if ( contact.getContactGroup() ) {
				
			} else {
				IManagerBean bean = BeanManager.getManagerBean(ContactData.class);
				bean.update(contact.getContactData());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
	
	
}