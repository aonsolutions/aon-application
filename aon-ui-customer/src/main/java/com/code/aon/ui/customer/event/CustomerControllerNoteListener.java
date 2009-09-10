package com.code.aon.ui.customer.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.NoteType;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CustomerControllerNoteListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IController c = event.getController();
			Expression expression = ExpressionUtilities.getNotEqualExpression(c.getFieldName(IRegistryAlias.REGISTRY_NOTE_NOTETYPE), NoteType.OBSERVATION);
			event.getController().getCriteria().addExpression(expression);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
}