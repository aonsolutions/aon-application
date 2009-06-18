package com.code.aon.ui.infoweb.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.infoweb.WebInfo;
import com.code.aon.infoweb.dao.IWebInfoAlias;

public class CompanyWebInfoController extends BasicController {
	
	private static final String COMPANY_CONTROLLER_NAME = "company";

	@SuppressWarnings({"unchecked", "unused"})
	public void onLoadWebInfo(ActionEvent event) throws ManagerBeanException{
		IController companyController = (IController) AonUtil.getController(COMPANY_CONTROLLER_NAME);
		Company company = ((Company)companyController.getTo());
		IManagerBean webInfoBean = BeanManager.getManagerBean(WebInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(webInfoBean.getFieldName(IWebInfoAlias.WEB_INFO_COMPANY_ID), company.getId());
		Iterator iter = webInfoBean.getList(criteria).iterator();
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
}
