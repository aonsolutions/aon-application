package com.code.aon.ui.cms.velocity;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.Activity;
import com.code.aon.cms.ActivityConfig;
import com.code.aon.cms.ActivityDetail;
import com.code.aon.cms.Company;
import com.code.aon.cms.CompanyActivity;
import com.code.aon.cms.Section;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.controller.GeneratorConfigController;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.VelocityUtil;
import com.code.aon.ui.cms.velocity.attribute.ActivityHandler;
import com.code.aon.ui.cms.velocity.attribute.CompanyHandler;

public class ActivityGenerator extends Generator {

	private static final String ACTIVITIES_KEY = "activities";
	
	private static final String ACTIVITY_KEY = "activity";
	
	public static final String ACTIVITY_LIST_PAGE = "main";
	
	public void generate() {
		generate(null);
	}
	
	public void generate(Activity selectedActivity) {
		VelocityUtil vu = context.initVelocityUtil();		
		
		try {
			Section configSection = GeneratorConfigController.currentSection(ActivityConfig.class);;
			
			IManagerBean bean = BeanManager.getManagerBean(Activity.class);
			Criteria criteria = new Criteria();
			if ( selectedActivity != null ) {
				criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ACTIVITY_ID), selectedActivity.getId());
			}
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.ACTIVITY_DETAILS_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addOrder(bean.getFieldName(ICMSAlias.ACTIVITY_DETAILS_DESCRIPTION));
			List<ITransferObject> l = bean.getList(criteria);
			List<ActivityHandler> activityHandlerList = new ArrayList<ActivityHandler>();
			for (int i=0; i < l.size(); i++) {
				Activity activity = (Activity)l.get(i);
				ActivityHandler ah = getActivityHandler(activity);
				activityHandlerList.add( ah );
				vu.put(ACTIVITY_KEY, ah);
				if (activity.getSection()!=null){
					context.changeSection(vu, activity.getSection());
				} else {
					if (configSection!=null) {
						context.changeSection(vu, configSection);
					} else {
						context.changeDefaultSection(vu);
					}
				}				
				generate(vu, Templates.ACTIVITY, "ACTIVITY_" + activity.getId());
				vu.remove(ACTIVITY_KEY);
			}
			vu.put(ACTIVITIES_KEY, activityHandlerList);
			logger.info(" Generando listado actividades.");
			context.changeSection(vu, configSection);
			generate(vu, Templates.ACTIVITY, ACTIVITY_LIST_PAGE);
			vu.remove(ACTIVITIES_KEY);
		} catch (ManagerBeanException e) {
			logger.error(e.getMessage());
		}
	}
	
	public static ActivityHandler getActivityHandler(Integer ident, String message) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Activity.class);
			Activity activity = (Activity) bean.get(ident);
			if ( activity == null ){
				getLogger().error( message + " REFERENCIA A UNA ACTIVIDAD ("+ident+") INEXISTENTE");
				return null;
			}		
			return getActivityHandler(activity);
		} catch (ManagerBeanException e) {
			getLogger().error(e.getMessage());
		}
		return null;
	}
	
	private static ActivityHandler getActivityHandler(Activity activity) {
		try {
			IManagerBean beanDetail = BeanManager.getManagerBean(ActivityDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ACTIVITY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			criteria.addEqualExpression(beanDetail.getFieldName(ICMSAlias.ACTIVITY_DETAIL_ACTIVITY_ID), activity.getId());
			List<ITransferObject> ld = (List<ITransferObject>)beanDetail.getList(criteria);
			if (ld.isEmpty()) {
				getLogger().warning("La actividad "+activity.getAlias()+" no esta internacionalizada");
			} else {
				ActivityDetail lcd = (ActivityDetail)ld.get(0);
				List<CompanyHandler> lstCompanies = new ArrayList<CompanyHandler>();
				IManagerBean beanCompanyActivity = BeanManager.getManagerBean(CompanyActivity.class);
				criteria = new Criteria();
				criteria.addEqualExpression(beanCompanyActivity.getFieldName(ICMSAlias.COMPANY_ACTIVITY_ACTIVITY_ID), activity.getId());
				criteria.addOrder(beanCompanyActivity.getFieldName(ICMSAlias.COMPANY_ACTIVITY_COMPANY_NAME));
				List<ITransferObject> l_company = (List<ITransferObject>)beanCompanyActivity.getList(criteria);
				if ( l_company.isEmpty() ) {
					getLogger().warning("La actividad "+activity.getAlias()+" no tiene empresas.");	
				}
				for (ITransferObject _company : l_company) {
					Company company = ((CompanyActivity)_company).getCompany();
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