package com.code.aon.ui.purchase.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.ItemSupplier;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;

public class ItemSupplierController extends LinesController {

	public void onSupplierChanged( LookupChangeEvent event) {
		Supplier supplier = (Supplier) event.getNewValue();
		ItemSupplier itemSupplier = (ItemSupplier) getTo();
		itemSupplier.setSupplier(supplier);
	}
	
    public void onMoveUp(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (ItemSupplier) getSelectedTO(), -1);
    }

    public void onMoveDown(ActionEvent event) throws ManagerBeanException {
    	moveMenuOption( (ItemSupplier) getSelectedTO(), 1);    	
    }

	@SuppressWarnings("unchecked")
	private void moveMenuOption( ItemSupplier itemSupplier, int movement ) throws ManagerBeanException {
		int oldPosition = itemSupplier.getPriority();
		int newPosition = oldPosition + movement;
		itemSupplier.setPriority( newPosition );
		getManagerBean().update( itemSupplier );
    	List<ItemSupplier> list = (List<ItemSupplier>) getModel().getWrappedData();
    	ItemSupplier movedMenuOption = list.get( newPosition );
		movedMenuOption.setPriority( oldPosition );
		getManagerBean().update( movedMenuOption );
		list.set( newPosition, itemSupplier );
		list.set( oldPosition, movedMenuOption );
	}
}

