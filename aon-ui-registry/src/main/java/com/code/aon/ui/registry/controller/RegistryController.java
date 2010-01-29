package com.code.aon.ui.registry.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.WebMailConstants;
import com.code.aon.ui.webmail.controller.MessageController;

/**
 * Controller used in the supplier maintenance.
 */
public class RegistryController extends BasicController {

	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public void onSendEmail( ActionEvent event ) {
		MessageController messageController = (MessageController) AonUtil.getRegisteredBean(WebMailConstants.BEAN_MESSAGE);
		messageController.initNewMessage();
		FacesContext context = FacesContext.getCurrentInstance();
		Object email = context.getExternalContext().getRequestParameterMap().get("email");
		messageController.setRecipientsTo( email.toString() );
	}

}