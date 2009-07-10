package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.BasicController;

public class InvoicingGroupController extends BasicController {

	private static final Logger LOGGER = Logger.getLogger(InvoicingGroupController.class.getName());

	public void onRegistryChanged(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean registryBean = BeanManager.getManagerBean(Registry.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(registryBean.getFieldName(IRegistryAlias.REGISTRY_ID), event.getNewValue());
				Iterator iter = registryBean.getList(criteria).iterator();
				if(iter.hasNext()){
					Registry registry = (Registry)iter.next();
					((InvoicingGroup)this.getTo()).setParent(registry);
				}
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error loading registry with id=" + event.getNewValue(), e);
			}
		}
	}
	
	public void addRegistryExpression(ValueChangeEvent event) throws ManagerBeanException, ExpressionException {
		if(event.getNewValue() != null && !event.getNewValue().equals("")) {
			Criteria c = getCriteria();
			c.addExpression(getFieldName(IFinanceAlias.INVOICING_GROUP_PARENT_ID), event.getNewValue().toString());
			setCriteria(c);
		}
	}
}