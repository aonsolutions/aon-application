package com.code.aon.ui.accounting.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.AutoConcept;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.Leasing;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;

/**
 * Collections controller.
 * 
 * @author Consulting & Development.
 */
public class AccountingCollectionsController {

	private LinkedList<SelectItem> accountLevels;
	private LinkedList<SelectItem> balanceTypes;
	private LinkedList<SelectItem> accountEntryTypes;
	private LinkedList<SelectItem> amortizationPeriods;
	private LinkedList<SelectItem> periodStatuses;

	private List<SelectItem> autoConcepts;
	private List<String> concepts;

	private String periodStatusAlias;
	private String periodIdAlias;

	public String getPeriodStatusAlias() throws ManagerBeanException {
		if (periodStatusAlias == null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			periodStatusAlias = periodBean.getFieldName(IAccountingAlias.PERIOD_STATUS);
		}
		return periodStatusAlias;
	}

	public String getPeriodIdAlias() throws ManagerBeanException {
		if (periodIdAlias == null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			periodIdAlias = periodBean.getFieldName(IAccountingAlias.PERIOD_ID);
		}
		return periodIdAlias;
	}

	private List<SelectItem> getPeriods(Criteria criteria, boolean pojo)
			throws ManagerBeanException {
		List<SelectItem> accountPeriods = new LinkedList<SelectItem>();
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Iterator<?> iter = periodBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			Period period = (Period) iter.next();
			SelectItem item = new SelectItem(pojo ? period : period.getId(), period.getId());
			accountPeriods.add(item);
		}
		return accountPeriods;
	}

	private List<SelectItem> getEnabledAccountPeriods(boolean pojo) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		Expression e1 = ExpressionUtilities.getEqualExpression(getPeriodStatusAlias(),
				AccountPeriodStatus.ACTIVE);
		Expression e2 = ExpressionUtilities.getEqualExpression(getPeriodStatusAlias(),
				AccountPeriodStatus.OPENING);
		criteria.addExpression(ExpressionUtilities.getOrExpression(e1, e2));
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria, pojo);
	}

	public List<SelectItem> getEnabledAccountPeriods() throws ManagerBeanException {
		return getEnabledAccountPeriods(true);
	}

	public List<SelectItem> getEnabledAccountPeriodKeys() throws ManagerBeanException {
		return getEnabledAccountPeriods(false);
	}

	private List<SelectItem> getActiveAccountPeriods(boolean pojo) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(getPeriodStatusAlias(),
				AccountPeriodStatus.INACTIVE));
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria, pojo);
	}

	public List<SelectItem> getActiveAccountPeriods() throws ManagerBeanException {
		return getActiveAccountPeriods(true);
	}

	public List<SelectItem> getActiveAccountPeriodKeys() throws ManagerBeanException {
		return getActiveAccountPeriods(false);
	}

	public List<SelectItem> getClosedAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.CLOSED);
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria, true);
	}

	public List<SelectItem> getOperatingAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.OPERATING);
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria, true);
	}

	public List<SelectItem> getOpeningAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.OPENING);
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria, true);
	}

	private List<SelectItem> getAllAccountPeriods(boolean pojo) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria, true);
	}

	public List<SelectItem> getAllAccountPeriods() throws ManagerBeanException {
		return getAllAccountPeriods(true);
	}

	public List<SelectItem> getAllAccountPeriodKeys() throws ManagerBeanException {
		return getAllAccountPeriods(false);
	}

	public List<SelectItem> getAccountPeriodStatuses() {
		if (periodStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			periodStatuses = new LinkedList<SelectItem>();
			AccountPeriodStatus[] aeTypes = AccountPeriodStatus.values();
			for (int i = 0; i < aeTypes.length; i++) {
				AccountPeriodStatus status = aeTypes[i];
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				periodStatuses.add(item);
			}
		}
		return periodStatuses;
	}

	public void setAutoConcepts(List<SelectItem> autoConcepts ) {
		this.autoConcepts = autoConcepts;
	}
	public List<SelectItem> getAutoConcepts() throws ManagerBeanException {
		if (autoConcepts == null) {
			autoConcepts = new LinkedList<SelectItem>();
			IManagerBean conceptBean = BeanManager.getManagerBean(AutoConcept.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(conceptBean.getFieldName(IAccountingAlias.AUTO_CONCEPT_DESCRIPTION),
					false);
			Iterator<?> iter = conceptBean.getList(criteria).iterator();
			while (iter.hasNext()) {
				AutoConcept concept = (AutoConcept) iter.next();
				SelectItem item = new SelectItem(concept, concept.getDescription());
				autoConcepts.add(item);
			}
		}
		return autoConcepts;
	}

	public void setConceptsDescriptions(List<String> concepts ) {
		this.concepts = concepts ;
	}
	public List<String> getConceptsDescriptions() {
		try {
			if (concepts == null) {
				concepts = new LinkedList<String>();
				IManagerBean conceptBean = BeanManager.getManagerBean(AutoConcept.class);
				Criteria criteria = new Criteria();
				String field = conceptBean.getFieldName(IAccountingAlias.AUTO_CONCEPT_DESCRIPTION);
				criteria.addOrder(field);
				List<ITransferObject> list = conceptBean.getList(criteria);
				for (ITransferObject to : list) {
					AutoConcept concept = (AutoConcept) to;
					concepts.add(concept.getDescription());
				}
			}
			return concepts;

		} catch (ManagerBeanException e) {
			FacesContext context = FacesContext.getCurrentInstance();
			FacesMessage message = new FacesMessage(e.getMessage());
			context.addMessage(null, message);
			return null;
		}
	}

	public List<SelectItem> getAccountTypes() {
		if (accountEntryTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			accountEntryTypes = new LinkedList<SelectItem>();
			AccountEntryType[] aeTypes = AccountEntryType.values();
			for (int i = 0; i < aeTypes.length; i++) {
				AccountEntryType type = aeTypes[i];
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				accountEntryTypes.add(item);
			}
		}
		return accountEntryTypes;
	}

	public List<SelectItem> getAccountLevels() {
		if (accountLevels == null) {
			accountLevels = new LinkedList<SelectItem>();
			accountLevels.add(new SelectItem(1, "1"));
			accountLevels.add(new SelectItem(2, "2"));
			accountLevels.add(new SelectItem(3, "3"));
			accountLevels.add(new SelectItem(4, "4"));
			accountLevels.add(new SelectItem(5, "5"));
		}
		return accountLevels;
	}

	public List<SelectItem> getLoans() throws ManagerBeanException {
		List<SelectItem> loans = new LinkedList<SelectItem>();
		IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
		Iterator<?> iter = loanBean.getList(null).iterator();
		while (iter.hasNext()) {
			Loan loan = (Loan) iter.next();
			SelectItem item = new SelectItem(loan, loan.getDescription());
			loans.add(item);
		}
		return loans;
	}

	public List<SelectItem> getLeasings() throws ManagerBeanException {
		List<SelectItem> loans = new LinkedList<SelectItem>();
		IManagerBean leasingBean = BeanManager.getManagerBean(Leasing.class);
		Iterator<?> iter = leasingBean.getList(null).iterator();
		while (iter.hasNext()) {
			Leasing leasing = (Leasing) iter.next();
			SelectItem item = new SelectItem(leasing, leasing.getDescription());
			loans.add(item);
		}
		return loans;
	}

	public List<SelectItem> getInvoiceTypes() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		SelectItem item = new SelectItem(InvoiceType.SALES, InvoiceType.SALES.getName(locale));
		types.add(item);
		item = new SelectItem(InvoiceType.PURCHASE, InvoiceType.PURCHASE.getName(locale));
		types.add(item);
		item = new SelectItem(InvoiceType.EXPENSES, InvoiceType.EXPENSES.getName(locale));
		types.add(item);
		return types;
	}

	public List<SelectItem> getAmortizationTypes() throws ManagerBeanException {
		List<SelectItem> ats = new LinkedList<SelectItem>();
		IManagerBean atBean = BeanManager.getManagerBean(AmortizationType.class);
		Iterator<?> iter = atBean.getList(null).iterator();
		while (iter.hasNext()) {
			AmortizationType at = (AmortizationType) iter.next();
			SelectItem item = new SelectItem(at, at.getDescription());
			ats.add(item);
		}
		return ats;
	}

	public List<SelectItem> getAmortizationPeriods() {
		if (amortizationPeriods == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			amortizationPeriods = new LinkedList<SelectItem>();
			AmortizationPeriod[] periods = AmortizationPeriod.values();
			for (int i = 0; i < periods.length; i++) {
				AmortizationPeriod type = periods[i];
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				amortizationPeriods.add(item);
			}
		}
		return amortizationPeriods;
	}

	public List<SelectItem> getClosingBalances() throws ManagerBeanException {
		return getBalances(BalanceType.CLOSING);
	}

	public List<SelectItem> getOperatingBalances() throws ManagerBeanException {
		return getBalances(BalanceType.OPERATING);
	}

	public List<SelectItem> getPatrimonyBalances() throws ManagerBeanException {
		return getBalances(BalanceType.PATRIMONY);
	}

	public List<SelectItem> getCustomBalances() throws ManagerBeanException {
		return getBalances(BalanceType.CUSTOM);
	}

	public List<SelectItem> getBalances(BalanceType balanceType) throws ManagerBeanException {
		List<SelectItem> balances = new LinkedList<SelectItem>();
		IManagerBean balanceBean = BeanManager.getManagerBean(Balance.class);
		Criteria c = new Criteria();
		c.addEqualExpression(balanceBean.getFieldName(IAccountingAlias.BALANCE_TYPE), balanceType);
		Iterator<?> iter = balanceBean.getList(c).iterator();
		while (iter.hasNext()) {
			Balance b = (Balance) iter.next();
			SelectItem item = new SelectItem(b, b.getName());
			balances.add(item);
		}
		return balances;
	}

	public List<SelectItem> getBalanceTypes() {
		if (balanceTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			balanceTypes = new LinkedList<SelectItem>();
			BalanceType[] bTypes = BalanceType.values();
			for (int i = 0; i < bTypes.length; i++) {
				BalanceType type = bTypes[i];
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				balanceTypes.add(item);
			}
		}
		return balanceTypes;
	}

}