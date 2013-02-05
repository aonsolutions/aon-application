package com.code.aon.ui.accounting.controller;

import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.report.StatementController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class LoanController extends BasicController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoanController.class.getName()); 

	public Account getRelatedAccount() throws ManagerBeanException {
		Loan loan = (Loan) getTo();
		return (loan!=null) ? loan.getAccount() : null;	
	}
	
	public Double getOutstandingBalance() {
		try {
			SummaryProvider sp = new SummaryProvider();
			SummaryProviderParameters params = new SummaryProviderParameters();
			if (getRelatedAccount() != null) {
				Loan loan = (Loan) getTo();
				params.setAccountExpression( getRelatedAccount().getCode());
				params.setAccountLevel(5);
				params.setFromDate(loan .getLoanDate());
				params.setSecurityLevel(loan.getSecurityLevel());
				SummaryCollection sc = sp.getSummaryCollection(params,true);
				double p = CommonUtil.round(sc.getCreditBalance())==0.0?
						CommonUtil.round(sc.getUnpaidBalance()*-1):sc.getCreditBalance(); 
				return p;
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("No se puede obtener el saldo pendiente: " + e.getMessage());
		}
		return null;
	}

	public void onAccountStatement(ActionEvent event) {
		try {
			Loan loan = (Loan) getTo();
			Account account = getRelatedAccount();
			StatementController c = (StatementController) AonUtil.getRegisteredBean(IAccountingConstants.STATEMENT_CONTROLLER_NAME);
			c.onReset(event);
			SummaryProviderParameters spp = new SummaryProviderParameters();
			spp.setAccountExpression(account.getCode());
			spp.setAccountLevel(5);
			spp.setLowerLevelVisible(false);
			spp.setFromDate(loan.getLoanDate());
			spp.setToDate(new Date());
			spp.setSecurityLevel(loan.getSecurityLevel());
			c.setParams(spp);
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IEntityAlias.ACCOUNT_CODE);
			criteria.addExpression(alias, account.getCode() + IAccountingConstants.ASTERISK);
			alias = c.getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENABLED);
			criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));
			c.onSearch(event);
			if (c.getModel().getRowCount() > 0) {
				c.getModel().setRowIndex(0);
				c.onSelect(event);
				c.setBackAction(IAccountingConstants.LOAN_FORM_NAVKEY);
			} else {
				String msg = "No existen cuentas contables para la cuenta.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}			

		} catch (ManagerBeanException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		} catch (ExpressionException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

}
