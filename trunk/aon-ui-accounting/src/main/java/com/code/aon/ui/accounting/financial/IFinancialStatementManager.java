package com.code.aon.ui.accounting.financial;

import java.util.List;

import com.code.aon.common.ManagerBeanException;

public interface IFinancialStatementManager {

	void initialize();
	String getLabel();
	List<FinancialStatement> getFinancialStatements();
	void search(FinancialStatementParams params) throws ManagerBeanException;	
	double getTotal();
	double getTotalForSummary();
	
}
