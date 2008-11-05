package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoicingGroupDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.LinesController;

public class InvoicingGroupDetailController extends LinesController {

	private static final Logger LOGGER = Logger.getLogger(InvoicingGroupDetailController.class.getName());

	public void onRegistryChanged(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(registryBean.getFieldName(IRegistryAlias.REGISTRY_ID), event.getNewValue());
				Iterator iter = registryBean.getList(criteria).iterator();
				if(iter.hasNext()){
					Registry registry = (Registry)iter.next();
					((InvoicingGroupDetail)this.getTo()).setChild(registry);
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error loading registry with id=" + event.getNewValue(), e);
			}
		}
	}
}
