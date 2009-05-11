package com.code.aon.ui.infoweb.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.infoweb.WebInfo;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.infoweb.controller.CompanyWebInfoController;
import com.code.aon.ui.infoweb.controller.IInfoWebConstants;

public class WebInfoControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			CompanyWebInfoController webInfoController = (CompanyWebInfoController) FormUtil.getController(IInfoWebConstants.WEB_INFO_CONTROLLER_NAME);
			webInfoController.onLoadWebInfo(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error loading asociated Web Info", e);
		}
	}
	
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			IController companyController = (IController) FormUtil.getController(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			Company company = ((Company)companyController.getTo());
	
			CompanyWebInfoController webInfoController = (CompanyWebInfoController) FormUtil.getController(IInfoWebConstants.WEB_INFO_CONTROLLER_NAME);
			WebInfo wi = (WebInfo)webInfoController.getTo();
			wi.setCompany(company);
		} catch (Exception e) {
			throw new ControllerListenerException("Error associating Web Info", e);
		}
	}

}
