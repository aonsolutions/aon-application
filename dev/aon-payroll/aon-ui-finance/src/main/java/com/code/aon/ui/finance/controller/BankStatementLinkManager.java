package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.ITransferObject;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.enumeration.StatementConcept;

public class BankStatementLinkManager {

	private BankStatement currentStatement;
	private String selectedTab;

	private List<ITransferObject> financeList;
	private DataModel financeModel;
	private List<ITransferObject> linkedFinanceList = new LinkedList<ITransferObject>();
	private ArrayList<Finance> financeChecks= new ArrayList<Finance>();

	private List<ITransferObject> fbatchList;
	private DataModel fbatchModel;
	private List<ITransferObject> linkedFbatchList = new LinkedList<ITransferObject>();
	private ArrayList<FinanceBatch> fbatchChecks= new ArrayList<FinanceBatch>();


	/****************** CURRENT STATEMENT ***************************/
	public BankStatement getCurrentStatement() {
		return currentStatement;
	}
	public void setCurrentStatement(BankStatement currentStatement) {
		this.currentStatement = currentStatement;
	}

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isFbatchSource() {
		return (currentStatement != null && currentStatement.getConcept() == StatementConcept.COLLECTION_BATCH);
	}

	/****************** FINANCE LISTS METHODS ***************************/
	public List<ITransferObject> getFinanceList() {
		return financeList;
	}
	
	public void setFinanceList(List<ITransferObject> financeList) {
		this.financeList = financeList;
	}
	
	public void setFinanceListFiltered(List<ITransferObject> financeList) {
		for (ITransferObject ito : getLinkedFinanceList()) {
			if (financeList.contains(ito)) {
				financeList.remove(ito);
			}
		}
		setFinanceList(financeList);
	}
	
	public DataModel getFinanceModel() {
		if (financeModel == null) {
			financeModel = new ListDataModel(financeList);
		}
		return financeModel;
	}

	public void setFinanceModel(DataModel model) {
		this.financeModel = model;
	}

	public List<ITransferObject> getLinkedFinanceList() {
		return linkedFinanceList;
	}
	
	public void setLinkedFinanceList(List<ITransferObject> linkedFinanceList) {
		this.linkedFinanceList = linkedFinanceList;
	}
	
	/****************** FINANCE CHECK LIST METHODS ***************************/
	public void financeRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectFinanceRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectFinanceRow(boolean rowChecked) {
		if (financeModel.isRowAvailable()) {
			Finance finance = (Finance)financeModel.getRowData();
			setFinanceRowChecked(finance, rowChecked);
		}
	}

	public boolean getFinanceRowChecked() {
		Finance finance = (Finance)financeModel.getRowData();
		return financeChecks.contains(finance);
	}
	
	public void setFinanceRowChecked(boolean rowChecked) {
	}

	public void setFinanceRowChecked(Finance finance, boolean rowChecked) {
		if (rowChecked) {
			if (!financeChecks.contains(finance)) {
				financeChecks.add(finance);
			}
		} else {
			if (financeChecks.contains(finance)) {
				financeChecks.remove(finance);
			}
		}
	}
	
	public ArrayList<Finance> getCheckedFinance() {
		return financeChecks;
	}
	
	public void clearCheckedFinance() {
		financeChecks = new ArrayList<Finance>();
	}
	
	/****************** FBATCH LISTS METHODS ***************************/
	public List<ITransferObject> getFbatchList() {
		return fbatchList;
	}
	
	public void setFbatchList(List<ITransferObject> fbatchList) {
		this.fbatchList = fbatchList;
	}
	
	public void setFbatchListFiltered(List<ITransferObject> fbatchList) {
		for (ITransferObject ito : getLinkedFbatchList()) {
			if (fbatchList.contains(ito)) {
				fbatchList.remove(ito);
			}
		}
		setFbatchList(fbatchList);
	}
	
	public DataModel getFbatchModel() {
		if (fbatchModel == null) {
			fbatchModel = new ListDataModel(fbatchList);
		}
		return fbatchModel;
	}

	public void setFbatchModel(DataModel model) {
		this.fbatchModel = model;
	}

	public List<ITransferObject> getLinkedFbatchList() {
		return linkedFbatchList;
	}
	
	public void setLinkedFbatchList(List<ITransferObject> linkedFbatchList) {
		this.linkedFbatchList = linkedFbatchList;
	}

	/****************** FBATCH CHECK LIST METHODS ***************************/
	public void fbatchRowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			selectFbatchRow(((Boolean)event.getNewValue()).booleanValue());
		}
	}

	private void selectFbatchRow(boolean rowChecked) {
		if (fbatchModel.isRowAvailable()) {
			FinanceBatch fBatch = (FinanceBatch)fbatchModel.getRowData();
			setFbatchRowChecked(fBatch, rowChecked);
		}
	}

	public boolean getFbatchRowChecked() {
		FinanceBatch fBatch = (FinanceBatch)fbatchModel.getRowData();
		return fbatchChecks.contains(fBatch);
	}
	
	public void setFbatchRowChecked(boolean rowChecked) {
	}

	public void setFbatchRowChecked(FinanceBatch fBatch, boolean rowChecked) {
		if (rowChecked) {
			if (!fbatchChecks.contains(fBatch)) {
				fbatchChecks.add(fBatch);
			}
		} else {
			if (fbatchChecks.contains(fBatch)) {
				fbatchChecks.remove(fBatch);
			}
		}
	}
	
	public ArrayList<FinanceBatch> getCheckedFbatch() {
		return fbatchChecks;
	}
	
	public void clearCheckedFbatch() {
		fbatchChecks = new ArrayList<FinanceBatch>();
	}
	
}
