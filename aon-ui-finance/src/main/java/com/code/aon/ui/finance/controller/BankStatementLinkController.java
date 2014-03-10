package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.ui.form.BasicController;

public class BankStatementLinkController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private ArrayList<BankStatementLink> checks= new ArrayList<BankStatementLink>();

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean)event.getNewValue()).booleanValue());
		}
	}
	
	public boolean getRowChecked() {
		BankStatementLink to = (BankStatementLink) model.getRowData();
		return checks.contains(to);
	}
	
	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			BankStatementLink to = (BankStatementLink) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			BankStatementLink to = (BankStatementLink) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}
	
	public ArrayList<BankStatementLink> getCheckedStatementLinks() {
		return checks;
	}
	
	public void clearCheckedStatementLinks() {
		checks = new ArrayList<BankStatementLink>();
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		List<ITransferObject> list = this.getManagerBean().getList(this.getCriteria());
		for (ITransferObject to : list ) {
			BankStatementLink statementLink = (BankStatementLink) to;
			if (!checks.contains(statementLink)) {
				checks.add(statementLink);
			}
		}
	}

	public void checkNone(ActionEvent event) {
		clearCheckedStatementLinks();
	}

}