package com.code.aon.ui.accounting.controller.amortization;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AmortizationController extends BasicController {

	private boolean salePanelVisible;

	public boolean isSalePanelVisible() {
		return salePanelVisible;
	}
	public void setSalePanelVisible(boolean salePanelVisible) {
		this.salePanelVisible = salePanelVisible;
	}

	public boolean isUpdatable() {
		try {
			if (isNew()) {
				return true;
			}
			AmortizationDetailController adc = (AmortizationDetailController) AonUtil.getRegisteredBean(IAccountingConstants.AMORTIZATION_DETAIL_CONTROLLER);
			return adc.hasScoredOrBlockedDetails();
		} catch (ManagerBeanException e) {
			return true;
		}
	}

	public List<SelectItem> getFixedAssetAccounts() {
		try {
			Amortization am = (Amortization) getTo();
			Account a = am.getAmortizationType().getFixedAssetAccount();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), a.getId() + IAccountingConstants.ASTERISK);
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), true);
			return getAccounts(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public List<SelectItem> getAccumulatedAccounts() {
		try {
			Amortization am = (Amortization) getTo();
			Account a = am.getAmortizationType().getAccumulatedAccount();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), a.getId() + IAccountingConstants.ASTERISK);
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), true);
			return getAccounts(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public List<SelectItem> getAllocationAccounts() {
		try {
			Amortization am = (Amortization) getTo();
			Account a = am.getAmortizationType().getAllocationAccount();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), a.getId() + IAccountingConstants.ASTERISK);
			criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), true);
			return getAccounts(criteria);
		} catch (ManagerBeanException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (ExpressionException e) {
			String msg = "Imposible cargar la lista de cuentas";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	private List<SelectItem> getAccounts(Criteria criteria) throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Iterator<?> iter = accountBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Account account = (Account) iter.next();
			SelectItem item = new SelectItem(account, account.getFullDescription());
			list.add(item);
		}
		return list;
	}

	public void onSale(ActionEvent event) {
		try {
			Amortization a = (Amortization) getTo();
			if (a.getDeadline() == null && a.getSaleAmount() != null) {
				String msg = "Debe indicar una fecha de baja.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			if (a.getDeadline() != null && a.getSaleAmount() == null) {
				String msg = "Debe indicar una importe de venta.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
			accept(event);
			if (a.getDeadline() != null) {
				AmortizationManager am = new AmortizationManager();
				am.checkSale(a);
				am.sale(a);
				AmortizationDetailController ad = (AmortizationDetailController) AonUtil.getRegisteredBean(IAccountingConstants.AMORTIZATION_DETAIL_CONTROLLER);
				ad.initModel();
				ad.onSearch(event);
			} else {
				onCalculate(event);			
			}
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar la cancelación de la ficha. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void onCalculate(ActionEvent event) {
		try {
			accept(event);
			Amortization a = (Amortization) getTo();
			AmortizationManager am = new AmortizationManager();
			am.generateDetails(a);
			AmortizationDetailController ad = (AmortizationDetailController) AonUtil.getRegisteredBean(IAccountingConstants.AMORTIZATION_DETAIL_CONTROLLER);
			ad.initModel();
			ad.onSearch(event);
		} catch (ManagerBeanException e) {
			String msg = "Imposible realizar el cálculo de la ficha. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}

	}
	
}
