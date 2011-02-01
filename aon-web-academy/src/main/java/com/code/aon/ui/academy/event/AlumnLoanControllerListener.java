package com.code.aon.ui.academy.event;

import com.code.aon.academy.dao.IAcademyAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AlumnLoanControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IAcademyAlias.ALUMN_LOAN_END_DATE), false);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IAcademyAlias.ALUMN_LOAN_MATERIAL));
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IAcademyAlias.ALUMN_LOAN_ID));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}