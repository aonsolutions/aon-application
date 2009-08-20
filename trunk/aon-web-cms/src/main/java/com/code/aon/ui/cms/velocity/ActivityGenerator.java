package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Activity;
import com.code.aon.cms.ActivityDetail;
import com.code.aon.cms.Company;
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
		try {
			IManagerBean bean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ACTIVITY_ID), ident);
			List<ITransferObject> l = bean.getList(criteria);
			if (l.isEmpty()){
				getLogger().warning("ACTIVIDAD "+ident+" REFERENCIADA NO EXISTE !!!");
				return null;
			}
			Activity a = (Activity) l.get(0);
			
			IManagerBean beanDetail = BeanManager.getManagerBean(ActivityDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ACTIVITY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ACTIVITY_DETAIL_ACTIVITY_ID), ident);
			List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
			if (ld.isEmpty()){
				getLogger().warning("La actividad "+a.getAlias()+" no esta internacionalizada");
			}else{
				ActivityDetail lcd = (ActivityDetail)ld.get(0);
				List<CompanyHandler> lstCompanies = new ArrayList<CompanyHandler>();
				IManagerBean beanCompanyActivity = BeanManager.getManagerBean(CompanyActivity.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanCompanyActivity.getFieldName(ICMSAlias.COMPANY_ACTIVITY_ACTIVITY_ID), ident);
				criteria.addOrder(beanCompanyActivity.getFieldName(ICMSAlias.COMPANY_ACTIVITY_COMPANY_NAME));
				List<ITransferObject> l_company = (List<ITransferObject>)beanCompanyActivity.getList(criteria);
				if ( l_company.isEmpty() ) {
					getLogger().warning("La actividad "+a.getAlias()+" no tiene empresas.");	
				}
				for (ITransferObject to : l_company){
					Company company = ((CompanyActivity)to).getCompany();
					lstCompanies.add( new CompanyHandler(company) );
				}
				ActivityHandler lch = new ActivityHandler(lcd,lstCompanies);
				return lch;
			}
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}

}
