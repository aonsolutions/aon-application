package com.code.aon.ui.webmail.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.webmail.controller.IContactController;
import com.code.aon.webmail.db.Contact;
import com.code.aon.webmail.db.ContactData;

public class ContactDBControllerListener extends ContactControllerListener {

	public static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ContactDBControllerListener.class);
	
	private ContactData data;
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		Contact contact = (Contact) event.getController().getTo();
		data = contact.getContactData();
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanSelected(event);
		Contact contact = (Contact) event.getController().getTo();
		data = contact.getContactData();
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		IContactController controller = (IContactController) event.getController();
		Contact contact = (Contact) controller.getTo();
		try {
			if ( contact.getContactGroup() ) {
				contact.insertContacts(true);
			} else {
				IManagerBean bean = BeanManager.getManagerBean(ContactData.class);
				bean.insert(data);
				contact.setContactData(data);
				controller.getManagerBean().update(contact);
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
				contact.insertContacts(false);
			} else {
				IManagerBean bean = BeanManager.getManagerBean(ContactData.class);
				bean.update(contact.getContactData());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		super.beforeBeanRemoved(event);
		Contact contact = (Contact) event.getController().getTo();
		if ( contact.getContactGroup() ) {
			try {
				contact.deleteContacts();
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ControllerListenerException( e.getMessage(), e );
			}
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		Contact contact = (Contact) event.getController().getTo();
		if (! contact.getContactGroup() ) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContactData.class);
				bean.remove(this.data);
				this.data = null;
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ControllerListenerException( e.getMessage(), e );
			}
		}
	}
	
}