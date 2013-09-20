package com.code.aon.ui.webmail.event;

import static com.code.aon.ui.common.ICommonMessages.CONTACT_USED;

import javax.faces.event.AbortProcessingException;

import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IContactController;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.webmail.IContact;

public class ContactControllerListener extends ControllerAdapter implements IWebMailConstants {

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		IContactController controller = (IContactController) event.getController();		
		IContact contact = (IContact) controller.getTo();
		if ( contact.getContactGroup() ) {
			controller.updateAvailableContacts();	
		}
	}
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		IContactController controller = (IContactController) event.getController();		
		IContact contact = (IContact) controller.getTo();
		String list = controller.isUsed(contact);
		if ( list != null ) {
			String message = AonUtil.addErrorMessageFromBundle( CONTACT_USED, contact.getDisplayName(), list );
			throw new AbortProcessingException( message );			
		}
	}

}