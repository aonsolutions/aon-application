package com.code.aon.ui.accounting.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

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

	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	private static final String STATEMENT_CONTROLLER_NAME = "statement";

	public boolean isUpdatable() {
		AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(ACCOUNT_ENTRY_CONTROLLER_NAME);
		if (c.getTo() != null && c.isUpdatable()) {
			if (c.isAonInvoice()) {
				try {
					AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
					String account = detail.getAccount().getId();
					if (detail != null) {
						return (!StringUtils.startsWith(account, "4"));
					}
				} catch (ManagerBeanException e) {
					AonUtil.addErrorMessage(e.getMessage());
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public void onBalanceDebit(ActionEvent event) {
		try {
			AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(ACCOUNT_ENTRY_CONTROLLER_NAME);
			if (c.getTotalDebit() != null && c.getTotalCredit() != null) {
				AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
				double d = CommonUtil.round(CommonUtil.round(c.getTotalDebit() - c.getTotalCredit())
						- CommonUtil.round(detail.getDebit() - detail.getCredit()));
				if (d < 0) {
					detail.setDebit(CommonUtil.round(d * (-1)));
				} else {
					detail.setCredit(d);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onBalanceCredit(ActionEvent event) {
		try {
			AccountEntryController c = (AccountEntryController) AonUtil.getRegisteredBean(ACCOUNT_ENTRY_CONTROLLER_NAME);
			if (c.getTotalDebit() != null && c.getTotalCredit() != null) {
				AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
				double cr = CommonUtil.round(CommonUtil.round(c.getTotalCredit() - c.getTotalDebit())
						- CommonUtil.round(detail.getCredit() - detail.getDebit()));
				if (cr < 0) {
					detail.setCredit(CommonUtil.round(cr * (-1)));
				} else {
					detail.setDebit(cr);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onBalance(ActionEvent event) {

	}

	@SuppressWarnings("unchecked")
	public void onAccept(ActionEvent event) {
		try {
			boolean adding = isNew();
			super.onAccept(event);
			if (adding) {
				List<AccountEntryDetail> list = (List<AccountEntryDetail>) getModel().getWrappedData();
				double imp = getBalance(list);
				if (imp != 0.0) {
					super.onReset(event);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}

	}

	private double getBalance(List<AccountEntryDetail> list) {
		double imp = 0;
		for (AccountEntryDetail detail : list) {
			imp = CommonUtil.round(imp + detail.getDebit());
			imp = CommonUtil.round(imp - detail.getCredit());
		}
		return imp;
	}

	public void onAccountStatement(ActionEvent event) {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();
			Account account = detail.getAccount();
			onStatement(account, event);
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

	private void onStatement(Account account, ActionEvent event) throws ManagerBeanException, ExpressionException {

		StatementController c = (StatementController) AonUtil.getRegisteredBean(STATEMENT_CONTROLLER_NAME);
		c.onReset(event);

		AccountEntryDetail detail = (AccountEntryDetail) getModel().getRowData();

		SummaryProviderParameters spp = new SummaryProviderParameters();
		spp.setAccountExpression(account.getId());
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Period period = (Period) periodBean.get(detail.getAccountEntry().getAccountPeriod());
		spp.setPeriod(period);
		spp.setFromDate(period.getInitiationDate());
		spp.setToDate(period.getDeadline());
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
