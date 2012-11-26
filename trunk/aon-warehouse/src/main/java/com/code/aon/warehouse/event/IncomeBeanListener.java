package com.code.aon.warehouse.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class IncomeBeanListener extends ManagerBeanListenerAdapter {
	
	@Override
	public void beanUpdated(ManagerBeanEvent evt) throws ManagerBeanException {
		Income income = (Income) evt.getTo();

		Project project = (income.getProject() != null && income.getProject().getId() != null) ? income.getProject() : null;
		IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), income.getId());
		for (ITransferObject ito : incomeDetailBean.getList(criteria)) {
			IncomeDetail incomeDetail = (IncomeDetail)ito;
			if (incomeDetail.getProject() == null || incomeDetail.getProject().getId() == null) {
				incomeDetail.setProject(project);
				incomeDetailBean.update(incomeDetail);
			}
		}
	}

}
