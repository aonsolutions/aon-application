package com.code.aon.ui.supplier.event.deprecated;

import com.code.aon.supplier.Supplier;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SupplierNewWindowControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        Supplier supplier = (Supplier)event.getController().getTo();
        if (supplier.getSupplierSegment() != null && supplier.getSupplierSegment().getId() == null) {
            supplier.setSupplierSegment(null);
        }
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        Supplier supplier = (Supplier)event.getController().getTo();
        if (supplier.getSupplierSegment() != null && supplier.getSupplierSegment().getId() == null) {
            supplier.setSupplierSegment(null);
        }
	}
}