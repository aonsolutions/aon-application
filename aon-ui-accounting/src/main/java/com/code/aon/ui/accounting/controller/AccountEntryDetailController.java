package com.code.aon.ui.accounting.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryDetailController extends LinesController {
	
	public void onBalanceAmount(ActionEvent event) {
		
	}
	
	public void onBalance(ActionEvent event) {
		
	}

	@SuppressWarnings("unchecked")
	public void onAccept(ActionEvent event) {
		try {
			boolean adding = isNew();
			super.onAccept(event);
			if (adding) {
				List<AccountEntryDetail> list = (List <AccountEntryDetail>) getModel().getWrappedData();
				double imp = getBalance(list);
				if (imp != 0.0) {
					super.onReset(event);	
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage());
		}
		 
	}

	private double getBalance(List<AccountEntryDetail> list) {
		double imp = 0;
		for (AccountEntryDetail detail: list) {
			imp = CommonUtil.round(imp + detail.getDebit());
			imp = CommonUtil.round(imp - detail.getCredit());
		}
		return imp;
	}
	
	private static final String STATEMENT_CONTROLLER_NAME = "statement";
	
	public void onBalancingAccountStatement(ActionEvent event) {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
			Account account = detail.getBalancingAccount();
			onStatement(account, event);
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
	public void onAccountStatement(ActionEvent event) {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
			Account account = detail.getAccount();
			onStatement(account, event);
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
	private void onStatement(Account account,ActionEvent event) throws ManagerBeanException, ExpressionException {

			StatementController c = (StatementController) AonUtil.getRegisteredBean(STATEMENT_CONTROLLER_NAME);
			c.onReset(event);
			
			AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
			
			SummaryProviderParameters spp = new SummaryProviderParameters();
			spp.setAccountExpression(account.getId());
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			Period period = (Period) periodBean.get(detail.getAccountEntry().getAccountPeriod()); 
			spp.setPeriod( period );
			spp.setFromDate( period.getInitiationDate() );
			spp.setToDate( period.getDeadline() );
			spp.setSecurityLevel(detail.getAccountEntry().getSecurityLevel());
			c.setParams(spp);
			c.setBackAction("accountEntry_form");
			
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
		
	}
}
