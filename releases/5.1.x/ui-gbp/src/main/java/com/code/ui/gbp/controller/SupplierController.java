package com.code.ui.gbp.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.SupplierStatus;

public class SupplierController extends BasicController {

	private String entity;
	
	private String office;
	
	private String control;
	
	private String account;

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public String getOffice() {
		return office;
	}

	public void setOffice(String office) {
		this.office = office;
	}

	public String getControl() {
		return control;
	}

	public void setControl(String control) {
		this.control = control;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		List list = new LinkedList();
		list.add(getTo());
		return list;
	}

	public void onInitialModel(ActionEvent event){
		try {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(this.getFieldName(IGBPAlias.SUPPLIER_STATUS), SupplierStatus.ACTIVE);
			this.setCriteria(criteria);
			this.onSearch(event);
		} catch (ManagerBeanException e) {
		}
	}
	
}
