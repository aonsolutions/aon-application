package com.code.aon.ui.product.event;

import java.util.Date;

import com.code.aon.product.Catalogue;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.util.AonUtil;

public class CatalogueControllerListener extends ControllerAdapter implements IItemConstants {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Date endDate = ((Catalogue) this.getController().getTo()).getEndDate();
		if (endDate != null) {
			Date startDate = ((Catalogue) this.getController().getTo()).getStartDate();
			if (endDate.before(startDate)) {
				throw new ControllerListenerException(AonUtil.getMessage("productBundle", "product_catalogue_dates_error"));
			}
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		Date endDate = ((Catalogue) this.getController().getTo()).getEndDate();
		if (endDate != null) {
			Date startDate = ((Catalogue) this.getController().getTo()).getStartDate();
			if (endDate.before(startDate)) {
				throw new ControllerListenerException(AonUtil.getMessage("productBundle", "product_catalogue_dates_error"));
			}
		}
	}

}