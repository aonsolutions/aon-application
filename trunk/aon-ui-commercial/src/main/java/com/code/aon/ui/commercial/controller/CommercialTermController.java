package com.code.aon.ui.commercial.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class CommercialTermController extends BasicController {

	public void onTypeChanged(ValueChangeEvent event) throws ManagerBeanException {
		((CommercialTerm)this.getTo()).setLine(calculateNextLine((Boolean)event.getNewValue()));
	}

	public	Integer calculateNextLine(boolean general) throws ManagerBeanException {
		IManagerBean commercialTermBean = BeanManager.getManagerBean(CommercialTerm.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(commercialTermBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_GENERAL), general);
		Projection projection = Projection.max(commercialTermBean.getFieldName(IEntityAlias.COMMERCIAL_TERM_LINE));
		Object value = commercialTermBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}