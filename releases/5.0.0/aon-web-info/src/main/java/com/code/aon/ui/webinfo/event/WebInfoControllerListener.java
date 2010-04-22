package com.code.aon.ui.webinfo.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webinfo.controller.CompanyWebInfoController;
import com.code.aon.webinfo.WebInfo;

public class WebInfoControllerListener extends ControllerAdapter {
	
	private static final String COMPANY_CONTROLLER_NAME = "company";

	private static final String WEB_INFO_CONTROLLER_NAME = "companyWebInfo";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			CompanyWebInfoController webInfoController = (CompanyWebInfoController) AonUtil.getController(WEB_INFO_CONTROLLER_NAME);
			webInfoController.onLoadWebInfo(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error loading asociated Web Info", e);
		}
	}
	
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController companyController = (IController) AonUtil.getController(COMPANY_CONTROLLER_NAME);
		Company company = ((Company)companyController.getTo());
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> COMPANY: " + company.getId());

		CompanyWebInfoController webInfoController = (CompanyWebInfoController) AonUtil.getController(WEB_INFO_CONTROLLER_NAME);
		WebInfo wi = (WebInfo)webInfoController.getTo();
		wi.setCompany(company);
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GUARDANDO SLOGAN " + wi.getSlogan());
	}

}
