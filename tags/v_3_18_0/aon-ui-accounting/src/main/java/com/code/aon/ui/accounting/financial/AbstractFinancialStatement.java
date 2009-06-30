package com.code.aon.ui.accounting.financial;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.controller.FinancialStatementController;
import com.code.aon.ui.accounting.controller.StatementController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.util.AonUtil;

public abstract class AbstractFinancialStatement implements IFinancialStatementManager {

	private static final String FINANCIAL_STATEMENT_CONTROLLER_NAME = "financialStatement";
	private static final String STATEMENT_CONTROLLER_NAME = "statement";

	private List<FinancialStatement> list;
	private DataModel model;
	private double total;

	@Override
	public void initialize() {
		list = null;
		model = null;
		total = 0;
	}
	
	@Override
	public List<FinancialStatement> getFinancialStatements(){
		return list;
	}
	@Override
	public void setFinancialStatements(List<FinancialStatement> list){
		this.list = list;
	}
	@Override
	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel( getFinancialStatements() );
		}
		return model;
	}

	@Override
	public double getTotal() {
		return total;
	}

	@Override
	public void setTotal(double total) {
		this.total = total;
	}

	@Override
	public double getTotalForSummary() {
		return total;
	}

	@Override
	public abstract void search(FinancialStatementParams params) throws ManagerBeanException;
	@Override
	public abstract String getLabel();
	
	public void onAccountStatement(ActionEvent event) {
		try {
			FinancialStatement fs = (FinancialStatement) getModel().getRowData();
			String accounts = fs.getCode();
			if (accounts != null && accounts.indexOf(",") > 0 ) {
				accounts = accounts.replaceAll(",", "*|");
				accounts += "*";
			}
			FinancialStatementController fsc = (FinancialStatementController) AonUtil
				.getRegisteredBean(FINANCIAL_STATEMENT_CONTROLLER_NAME);
			StatementController c = (StatementController) AonUtil
					.getRegisteredBean(STATEMENT_CONTROLLER_NAME);
			c.onReset(event);
			SummaryProviderParameters spp = new SummaryProviderParameters();
			spp.setAccountExpression(accounts);
			Period period = AccountingPeriodUtil.getPeriod(fsc.getFinancialDate());
			spp.setPeriod(period);
			spp.setFromDate(period.getInitiationDate());
			spp.setToDate(period.getDeadline());
			c.setParams(spp);
			c.setBackAction("financialStatement_list");
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IAccountAlias.ACCOUNT_ID);
			criteria.addExpression(alias, accounts + (accounts.indexOf("*") > 0?"":"*") );
			alias = c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
			criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));

			c.onSearch(event);
			if (c.getModel().getRowCount() > 0) {
				c.getModel().setRowIndex(0);
				c.onSelect(event);
			} else {
				String msg = "No existen cuentas contables para la cuenta.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}

		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onDisable(ActionEvent event) {
		FinancialStatement fs = (FinancialStatement) getModel().getRowData();
		fs.setDisabled(!fs.isDisabled());
		double amount = CommonUtil.round(  fs.getAmount() * (fs.isDisabled()?-1:1) );
		setTotal( CommonUtil.round( getTotal() + amount ) );
	}
}
