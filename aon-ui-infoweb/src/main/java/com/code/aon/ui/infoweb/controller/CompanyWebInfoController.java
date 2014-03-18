package com.code.aon.ui.infoweb.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.infoweb.WebInfo;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.entity.IEntityAlias;

public class CompanyWebInfoController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public void onLoadWebInfo(ActionEvent event) throws ManagerBeanException{
		IController companyController = (IController) FormUtil.getController(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = ((Company)companyController.getTo());
		IManagerBean webInfoBean = BeanManager.getManagerBean(WebInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(webInfoBean.getFieldName(IEntityAlias.WEB_INFO_COMPANY_ID), company.getId());
		Iterator<ITransferObject> iter = webInfoBean.getList(criteria).iterator();
		if(iter.hasNext()){
			this.setTo((WebInfo)iter.next());
			this.setNew(false);
		}else{
			WebInfo info = new WebInfo();
			info.setCompany(company);
			this.setTo(info);
			this.setNew(true);
		}
	}
	
	public void onInit(ActionEvent event) {
		CompanyController companyController = (CompanyController) FormUtil.getController(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		if(companyController.getTo() == null){
			companyController.onLoad(event);
		}
	}
	
}
