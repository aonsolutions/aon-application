package com.code.aon.ui.webmail.controller;

import java.util.List;

import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.ITransferObject;
import com.code.aon.ui.form.IController;
import com.code.aon.webmail.IContact;

public interface IContactController extends IController {

	Converter getConverter();
	
	void onResetGroup(ActionEvent event);
	
	List<SelectItem> getAvailableContacts();
	
	List<IContact> getEmailContacts();
	
	List<IContact> suggestionEmails( String text );
		
	void updateAvailableContacts();
	
	String isUsed( IContact contact );

}
