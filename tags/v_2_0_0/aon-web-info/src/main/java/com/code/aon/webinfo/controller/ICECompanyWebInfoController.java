package com.code.aon.webinfo.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.WebInfo;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ICECompanyWebInfoController extends BasicController {
	
	private static final String COMPANY_CONTROLLER_NAME = "company";

	@SuppressWarnings({"unchecked", "unused"})
	public void onLoadWebInfo(ActionEvent event) throws ManagerBeanException{
		ICECompanyController companyController = (ICECompanyController)AonUtil.getController(COMPANY_CONTROLLER_NAME);
		Company company = ((Company)companyController.getTo());
		IManagerBean webInfoBean = BeanManager.getManagerBean(WebInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(webInfoBean.getFieldName(ICompanyAlias.WEB_INFO_COMPANY_ID), company.getId());
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
