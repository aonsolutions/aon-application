package com.code.aon.ui.manager.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.manager.controller.ContactController;
import com.code.aon.webmail.db.Contact;

public class ContactConverter implements Converter {

	private ContactController controller;
	
	public ContactConverter(ContactController controller) {
		this.controller = controller;
	}

	@Override
	public Object getAsObject(FacesContext context,
			UIComponent component, String value) {
		if (! StringUtils.isEmpty(value) ) {
			for( SelectItem item : controller.getAvailableContacts() ) {
				Contact contact = (Contact) item.getValue();
				if ( contact.getDisplayName().equals(value) ) {
					return contact;
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
		Contact contact = (Contact) value;
		return contact.getDisplayName();
	}

}