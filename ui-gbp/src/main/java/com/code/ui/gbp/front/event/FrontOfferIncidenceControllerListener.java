package com.code.ui.gbp.front.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.IncidenceSource;

public class FrontOfferIncidenceControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addEqualExpression(event.getController().getFieldName(IGBPAlias.INCIDENCE_SOURCE), IncidenceSource.OFFER);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.INCIDENCE_INCIDENCE_DATE), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
}