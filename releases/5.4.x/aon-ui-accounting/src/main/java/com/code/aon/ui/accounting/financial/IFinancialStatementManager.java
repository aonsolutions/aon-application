package com.code.aon.ui.accounting.financial;

import java.util.List;

import javax.faces.model.DataModel;

import com.code.aon.common.ManagerBeanException;

public interface IFinancialStatementManager {

	void initialize();
	String getLabel();
	List<FinancialStatement> getFinancialStatements();
	void setFinancialStatements(List<FinancialStatement> list);
	DataModel getModel();
	void search(FinancialStatementParams params) throws ManagerBeanException;	
	double getTotal();
	void setTotal(double total);
	double getTotalForSummary();
	
}
