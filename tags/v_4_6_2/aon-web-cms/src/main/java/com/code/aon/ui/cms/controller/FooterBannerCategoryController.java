package com.code.aon.ui.cms.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.cms.Footer;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;

public class FooterBannerCategoryController extends BasicController {

	private Footer currentFooter;

	
	public Footer getCurrentFooter() {
		return currentFooter;
	}


	public void setCurrentFooter(Footer currentFooter) {
		this.currentFooter = currentFooter;
	}

	/**
	 * Removes all the selected objects.
	 * 
	 * @param event the event
	 */
	public void onRemoveRow(ActionEvent event){
		try{
			ITransferObject to = (ITransferObject) model.getRowData();
			getManagerBean().remove(to);
			onSearch( event );
		} catch (ManagerBeanException e) {
			addMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	
}
