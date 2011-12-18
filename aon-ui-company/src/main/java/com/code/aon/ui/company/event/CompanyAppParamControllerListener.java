package com.code.aon.ui.company.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.enumeration.ReportPrintOption;
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
			companyController.setPrintLogo(companyController.obtainPrintLogo());
			companyController.setPrintName(companyController.obtainPrintName());
			companyController.setPrintNif(companyController.obtainPrintNif());
			companyController.setPrintAddress(companyController.obtainPrintAddress());
			companyController.setPrintInternetData(companyController.obtainPrintInternetData());
			companyController.setSmartCard(companyController.obtainSmartCard());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event)throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			updateParams(companyController);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event)throws ControllerListenerException {
		ICompanyController companyController = (ICompanyController)event.getController();
		try {
			updateParams(companyController);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	private void updateParams( ICompanyController companyController ) throws ManagerBeanException {
		updateParam(companyController, CompanyController.PRINT_HEADER_PARAM, companyController.isPrintHeader());
		updateParam(companyController, CompanyController.PRINT_RECORD_DATA_PARAM, companyController.isPrintRecordData());
		updateParam(companyController, CompanyController.PRINT_LOGO_PARAM, companyController.isPrintLogo());
		updateParam(companyController, CompanyController.PRINT_NAME_PARAM, companyController.getPrintName());
		updateParam(companyController, CompanyController.PRINT_NIF_PARAM, companyController.getPrintNif());
		updateParam(companyController, CompanyController.PRINT_ADDRESS_PARAM, companyController.getPrintAddress());
		updateParam(companyController, CompanyController.PRINT_INTERNET_DATA_PARAM, companyController.getPrintInternetData());
		updateParam(companyController, CompanyController.SMART_CARD_PARAM, companyController.isSmartCard());		
	}
	
	private void updateParam(ICompanyController companyController, String paramName, boolean value) throws ManagerBeanException {
		boolean update = true;
		ApplicationParameter param = companyController.obtainApplicationParameter(paramName);
		if ( param == null ) {
			param = new ApplicationParameter();
			param.setName(paramName);
		} else if ( value == new Boolean(param.getValue()).booleanValue() ) {
			update = false;
		}
		if ( update ) {
			param.setValue(new Boolean(value).toString());
			IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
			appParamBean.insertOrUpdate(param);			
		}
	}
	
	private void updateParam(ICompanyController companyController, String paramName, ReportPrintOption value) throws ManagerBeanException {
		boolean update = true;
		ApplicationParameter param = companyController.obtainApplicationParameter(paramName);
		if ( param == null ) {
			param = new ApplicationParameter();
			param.setName(paramName);
		} else if ( value == (param.getValue()==null?null:ReportPrintOption.values()[Integer.parseInt(param.getValue())]) ) {
			update = false;
		}
		if ( update ) {
			param.setValue(value==null?null:String.valueOf(value.ordinal()));
			IManagerBean appParamBean = BeanManager.getManagerBean(ApplicationParameter.class);
			appParamBean.insertOrUpdate(param);			
		}
	}
	
}