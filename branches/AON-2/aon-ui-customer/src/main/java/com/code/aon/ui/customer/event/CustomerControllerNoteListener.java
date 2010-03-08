package com.code.aon.ui.customer.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.ui.customer.controller.CustomerNoteController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CustomerControllerNoteListener extends ControllerAdapter {
	
	private static final String REGISTRY_NOTE_CONTROLLER_NAME = "rnote";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		applyCustomerCriteria(event);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		applyCustomerCriteria(event);
	}

	private void applyCustomerCriteria(ControllerEvent event) throws ControllerListenerException {
		Customer customer = (Customer)event.getController().getTo();
		CustomerNoteController rNoteController = (CustomerNoteController)AonUtil.getController(REGISTRY_NOTE_CONTROLLER_NAME);
		try {
			IManagerBean rNoteBean = BeanManager.getManagerBean(RegistryNote.class);
			rNoteController.setFromDate(null);
			rNoteController.setToDate(null);
			rNoteController.setNoteType(null);
			rNoteController.clearCriteria();
			rNoteController.getCriteria().addEqualExpression(rNoteBean.getFieldName(IRegistryAlias.REGISTRY_NOTE_REGISTRY_ID), customer.getId());
			Expression expression = ExpressionUtilities.getNotEqualExpression(rNoteBean.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTETYPE), NoteType.OBSERVATION);
			rNoteController.getCriteria().addExpression(expression);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}