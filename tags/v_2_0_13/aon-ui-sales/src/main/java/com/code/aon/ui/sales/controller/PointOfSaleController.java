package com.code.aon.ui.sales.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.sales.PointOfSale;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the workplace maintenance.
 */
public class PointOfSaleController extends BasicController {

	/**
	 * On accept. Sets the current workPlace to null after adding it.
	 * 
	 * @param event the event
	 */
	@Override
	public void onAccept(ActionEvent event) {
		super.accept(event);
		super.resetTo();
		super.onSearch(event);
	}

	/**
	 * On remove. Sets the current workPlace to null after removing it.
	 * 
	 * @param event the event
	 */
	@Override
	public void onRemove(ActionEvent event) {
		remove( event );
		resetTo();
	}
	
	@SuppressWarnings("unchecked")
	public void addressChange(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),event.getNewValue());
			Iterator iter = rAddressBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryAddress address = (RegistryAddress)iter.next();
				((PointOfSale)this.getTo()).setRAddress(address);
			}
		}
	}
}