package com.code.aon.ui.customer.controller;

import java.util.ArrayList;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.registry.controller.RegistryController;

public class CustomerListController extends RegistryController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ArrayList<Customer> checks = new ArrayList<Customer>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		Customer to = (Customer) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			Customer to = (Customer) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			Customer to = (Customer) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<Customer> getCheckedCustomers() {
		return checks;
	}
	
	public void clearCheckedCustomers() {
		checks = new ArrayList<Customer>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		for (ITransferObject ito : this.getManagerBean().getList(this.getCriteria())) {
			Customer customer = (Customer)ito;
			if (!checks.contains(customer)) {
				checks.add(customer);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedCustomers();
	}

	public int getCheckedCount() {
		return getCheckedCustomers().size();
	}

}