package com.code.aon.ui.sales.util.print;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;

import net.sf.jasperreports.engine.JRDefaultScriptlet;

import com.code.aon.AonVersion;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;

public class SalesReportScriptlet extends JRDefaultScriptlet implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private CompanyController getCompanyController(){
		return (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
	}
	
	public InputStream getBackgroundFile(){
		byte[] data = getCompanyController().getSalesBackgroundFile().getData();
		if(data != null && data.length>0){
			return new ByteArrayInputStream(data);
		}
		return null;
	}
		
}