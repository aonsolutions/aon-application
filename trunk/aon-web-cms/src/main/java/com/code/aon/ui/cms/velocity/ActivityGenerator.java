package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.ActivityDetail;
import com.code.aon.cms.CompanyActivity;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.attribute.ActivityHandler;
import com.code.aon.ui.cms.velocity.attribute.CompanyHandler;

public class ActivityGenerator extends Generator {

	public static Object getActivityHandler(Integer ident) {
		List<ITransferObject> ld;
		List<ITransferObject> l_company;
		try {
			IManagerBean beanDetail = BeanManager.getManagerBean(ActivityDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ACTIVITY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ACTIVITY_DETAIL_ACTIVITY_ID), ident);
			ld = (List<ITransferObject>)beanDetail.getList(criteria);
			if (!ld.isEmpty()){
				ActivityDetail lcd = (ActivityDetail)ld.get(0);
				List<CompanyHandler> lstCompanies = new ArrayList<CompanyHandler>();
				IManagerBean beanCompanyActivity = BeanManager.getManagerBean(CompanyActivity.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanCompanyActivity.getFieldName(ICMSAlias.COMPANY_ACTIVITY_ACTIVITY_ID), ident);
				l_company = (List<ITransferObject>)beanCompanyActivity.getList(criteria);
				for (ITransferObject company : l_company){
					lstCompanies.add(new CompanyHandler(((CompanyActivity)company).getCompany()));
				}
				ActivityHandler lch = new ActivityHandler(lcd,lstCompanies);
				return lch;
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

}
