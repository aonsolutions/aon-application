package com.code.aon.ui.accounting.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class LoanController extends BasicController{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoanController.class.getName()); 

	private static final String STATEMENT_CONTROLLER_NAME = "statement";

	public Account getRelatedAccount() throws ManagerBeanException {
		Loan loan = (Loan) getTo();
		return (loan != null)?obtainLoanAccount( loan ):null;	
	}
	

	/* NO se llama a AccountUtil porque este aquí no se genera si no existe */	
	@SuppressWarnings("unchecked")
	private Account obtainLoanAccount(Loan loan) throws ManagerBeanException {
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_LOAN_ID), loan.getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			LoanAccount loanAccount = (LoanAccount)iter.next();
			return loanAccount.getAccount();
		}
		return null;
	}

	public Double getOutstandingBalance() {
		try {
			SummaryProvider sp = new SummaryProvider();
			SummaryProviderParameters params = new SummaryProviderParameters();
			if (getRelatedAccount() != null) {
				Loan loan = (Loan) getTo();
				params.setAccountExpression( getRelatedAccount().getId());
				params.setAccountLevel(5);
				params.setBudgeted(false);
				params.setFromDate(loan .getLoanDate());
				params.setSecurityLevel(loan.getSecurityLevel());
				SummaryCollection sc = sp.getSummaryCollection(params);
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
			StatementController c = (StatementController) AonUtil.getRegisteredBean(STATEMENT_CONTROLLER_NAME);
			c.onReset(event);
			SummaryProviderParameters spp = new SummaryProviderParameters();
			spp.setAccountExpression(account.getId());
			
			spp.setFromDate(loan.getLoanDate());
			spp.setToDate(new Date());
			spp.setSecurityLevel(loan.getSecurityLevel());
			c.setParams(spp);
			c.setBackAction("loan_form");
			
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IAccountAlias.ACCOUNT_ID);
			criteria.addExpression(alias, account.getId() + "*");
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
			throw new AbortProcessingException(msg,e);
		} catch (ExpressionException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg,e);
		}
	}

}
