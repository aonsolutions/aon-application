package com.code.aon.ui.product.event;

import java.util.logging.Logger;
import com.code.aon.product.Catalogue;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;
import com.code.aon.ui.util.AonUtil;

public class CatalogueControllerListener extends ControllerAdapter implements
		IItemConstants {

	private static final Logger LOGGER = Logger
			.getLogger(CatalogueControllerListener.class.getName());

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {

		if (((Catalogue) this.getController().getTo()).getEndDate().before(
				((Catalogue) this.getController().getTo()).getStartDate())) {
			
			throw new ControllerListenerException(AonUtil.getMessage("productBundle",
			"product_catalogue_dates_error"));
		}
		
		
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {


		if (((Catalogue) this.getController().getTo()).getEndDate().before(
				((Catalogue) this.getController().getTo()).getStartDate())) {
			
			throw new ControllerListenerException(AonUtil.getMessage("productBundle",
			"product_catalogue_dates_error"));
		}

	}

	
	
}