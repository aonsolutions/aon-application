package com.code.aon.ui.company.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompanyAppParamControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			companyController.setPrintHeader(companyController.obtainPrintHeader());
			companyController.setPrintRecordData(companyController.obtainPrintRecordData());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			ApplicationParameter printHeaderParam = companyController.obtainApplicationParameter(CompanyController.printHeaderParam);
			ApplicationParameter printRecorDataParam = companyController.obtainApplicationParameter(CompanyController.printRecordDataParam);
			if(printHeaderParam != null && companyController.isPrintHeader() != new Boolean(printHeaderParam.getValue()).booleanValue()){
				printHeaderParam.setValue(new Boolean(companyController.isPrintHeader()).toString());
				updateParam(printHeaderParam);
			}
			if(printRecorDataParam != null && companyController.isPrintRecordData() != new Boolean(printRecorDataParam.getValue()).booleanValue()){
				printRecorDataParam.setValue(new Boolean(companyController.isPrintRecordData()).toString());
				updateParam(printRecorDataParam);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			ApplicationParameter printHeaderParam = companyController.obtainApplicationParameter(CompanyController.printHeaderParam);
			ApplicationParameter printRecorDataParam = companyController.obtainApplicationParameter(CompanyController.printRecordDataParam);
			if(printHeaderParam != null && companyController.isPrintHeader() != new Boolean(printHeaderParam.getValue()).booleanValue()){
				printHeaderParam.setValue(new Boolean(companyController.isPrintHeader()).toString());
				updateParam(printHeaderParam);
			}
			if(printRecorDataParam != null && companyController.isPrintRecordData() != new Boolean(printRecorDataParam.getValue()).booleanValue()){
				printRecorDataParam.setValue(new Boolean(companyController.isPrintRecordData()).toString());
				updateParam(printRecorDataParam);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	private void updateParam(ApplicationParameter param) throws ManagerBeanException {
		IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
		appParamBean.update(param);
	}
}