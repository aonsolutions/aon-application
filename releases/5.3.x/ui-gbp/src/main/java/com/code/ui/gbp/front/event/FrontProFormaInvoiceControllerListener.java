package com.code.ui.gbp.front.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.Supplier;
import com.code.gbp.dao.IGBPAlias;
import com.code.ui.gbp.front.util.FrontUtil;

public class FrontProFormaInvoiceControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			Supplier currentSupplier = FrontUtil.getCurrentSupplier();
			event.getController().getCriteria().addEqualExpression(event.getController().getFieldName(IGBPAlias.PRO_FORMA_INVOICE_SUPPLIER_ID), currentSupplier.getId());
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.PRO_FORMA_INVOICE_INVOICE_DATE), false);
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.PRO_FORMA_INVOICE_CAMPAIGN_CODE));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}	
	
}