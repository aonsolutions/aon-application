package com.code.aon.company.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseActivity;

public class EnterpriseActivityBeanListener extends ManagerBeanListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beanInserted(ManagerBeanEvent event) throws ManagerBeanException {
		EnterpriseActivity activity = (EnterpriseActivity)event.getTo();
		if (activity.isPrincipal()) {
			secureOnlyOnePrincipal(activity);
		}
	}
	
	@Override
	public void beanUpdated(ManagerBeanEvent event) throws ManagerBeanException {
		EnterpriseActivity activity = (EnterpriseActivity)event.getTo();
		if (activity.isPrincipal()) {
			secureOnlyOnePrincipal(activity);
		}
	}

	private void secureOnlyOnePrincipal(EnterpriseActivity activity) throws ManagerBeanException {
		IManagerBean activityBean = BeanManager.getManagerBean(EnterpriseActivity.class);
		Criteria criteria = new Criteria();
		if (activity.getId() != null) {
			criteria.addNotEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ID), activity.getId());
		}
		criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_ENTERPRISE_ID), activity.getEnterprise().getId());
		criteria.addEqualExpression(activityBean.getFieldName(IEntityAlias.ENTERPRISE_ACTIVITY_PRINCIPAL), Boolean.TRUE);
		for (ITransferObject ito : activityBean.getList(criteria)) {
			EnterpriseActivity enterpriseActivity = (EnterpriseActivity)ito;
			enterpriseActivity.setPrincipal(false);
			activityBean.update(enterpriseActivity);
		}
	}

}
