package com.code.aon.ui.tas.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.tas.Model;
import com.code.aon.tas.dao.ITASAlias;
import com.code.aon.ui.form.event.ControllerSearchListener;

public class TasItemSearchListener extends ControllerSearchListener {

	private Model model;
		
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setModel((Model)BeanManager.getManagerBean(Model.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if ((getModel() != null) && (getModel().getId() != null)) {
			criteria.addEqualExpression(getFieldName(ITASAlias.TAS_ITEM_MODEL_ID), getModel().getId());			
		}
	}

}