package com.code.aon.ui.accounting.controller;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.AonVersion;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.accounting.AutoConcept;
import com.code.aon.accounting.Balance;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.enumeration.AccountPeriodStatus;
import com.code.aon.accounting.enumeration.AmortizationPeriod;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.enumeration.LoanStatus;
import com.code.aon.accounting.enumeration.Quarter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountingCollectionsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private LinkedList<SelectItem> accountLevels;
	private LinkedList<SelectItem> balanceTypes;
	private LinkedList<SelectItem> accountEntryTypes;
	private LinkedList<SelectItem> amortizationPeriods;
	private LinkedList<SelectItem> periodStatuses;
	private LinkedList<SelectItem> loanStatuses;
	private LinkedList<SelectItem> quarters;
	private LinkedList<SelectItem> templateTypes;

	private List<SelectItem> autoConcepts;
	private List<String> concepts;

	private String periodStatusAlias;
	private String periodIdAlias;
	
	public Period getPeriod() {
		return null;
	}
	public void setPeriod(Period period) {
	}

	public String getPeriodStatusAlias() throws ManagerBeanException {
		if (periodStatusAlias == null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			periodStatusAlias = periodBean.getFieldName(IEntityAlias.PERIOD_STATUS);
		}
		return periodStatusAlias;
	}

	public String getPeriodIdAlias() throws ManagerBeanException {
		if (periodIdAlias == null) {
			IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
			periodIdAlias = periodBean.getFieldName(IEntityAlias.PERIOD_ID);
		}
		return periodIdAlias;
	}

	private List<SelectItem> getPeriods(Criteria criteria) throws ManagerBeanException {
		List<SelectItem> accountPeriods = new LinkedList<SelectItem>();
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		List<ITransferObject> list = periodBean.getList(criteria);
		for (ITransferObject to : list) {
			Period period = (Period) to;
			SelectItem item = new SelectItem(period, period.getName());
			accountPeriods.add(item);
		}
		return accountPeriods;
	}

	public List<SelectItem> getEnabledAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		Expression e1 = ExpressionUtilities.getEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.ACTIVE);
		Expression e2 = ExpressionUtilities.getEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.OPENING);
		criteria.addExpression(ExpressionUtilities.getOrExpression(e1, e2));
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria);
	}

	public List<SelectItem> getActiveAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addExpression(ExpressionUtilities.getNotEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.INACTIVE));
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria);
	}

	public List<SelectItem> getClosedAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.CLOSED);
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria);
	}

	public List<SelectItem> getOperatingAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.OPERATING);
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria);
	}

	public List<SelectItem> getOpeningAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.OPENING);
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria);
	}

	public List<SelectItem> getAllAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getPeriodIdAlias(), false);
		return getPeriods(criteria);
	}

	public List<Period> getClassAllAccountPeriods() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addOrder(getPeriodIdAlias(), false);
		return getClassPeriods(criteria);
	}
	
	private List<Period> getClassPeriods(Criteria criteria) throws ManagerBeanException {
		List<Period> accountPeriods = new LinkedList<Period>();
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		List<ITransferObject> list = periodBean.getList(criteria);
		for (ITransferObject to : list) {
			Period period = (Period) to;
			accountPeriods.add(period);
		}
		return accountPeriods;
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

	public List<SelectItem> getLoanStatuses() {
		if (loanStatuses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			loanStatuses = new LinkedList<SelectItem>();
			LoanStatus[] aeTypes = LoanStatus.values();
			for (int i = 0; i < aeTypes.length; i++) {
				LoanStatus status = aeTypes[i];
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				loanStatuses.add(item);
			}
		}
		return loanStatuses;
	}

	public void setAutoConcepts(List<SelectItem> autoConcepts) {
		this.autoConcepts = autoConcepts;
	}

	public List<SelectItem> getAutoConcepts() throws ManagerBeanException {
		if (autoConcepts == null) {
			autoConcepts = new LinkedList<SelectItem>();
			IManagerBean conceptBean = BeanManager.getManagerBean(AutoConcept.class);
			Criteria criteria = new Criteria();
			criteria.addOrder(conceptBean.getFieldName(IEntityAlias.AUTO_CONCEPT_DESCRIPTION), false);
			List<ITransferObject> list = conceptBean.getList(criteria);
			for (ITransferObject to : list) {
				AutoConcept concept = (AutoConcept) to;
				SelectItem item = new SelectItem(concept, concept.getDescription());
				autoConcepts.add(item);
			}
		}
		return autoConcepts;
	}

	public void setConceptsDescriptions(List<String> concepts) {
		this.concepts = concepts;
	}

	public List<String> getConceptsDescriptions() {
		try {
			if (concepts == null) {
				concepts = new LinkedList<String>();
				IManagerBean conceptBean = BeanManager.getManagerBean(AutoConcept.class);
				Criteria criteria = new Criteria();
				String field = conceptBean.getFieldName(IEntityAlias.AUTO_CONCEPT_DESCRIPTION);
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

	public List<String> getConceptsDescriptions(Object prefix) {
		List<String> subList = new LinkedList<String>();
		for (String concept : getConceptsDescriptions()) {
			if (concept.startsWith((String) prefix)) {
				subList.add(concept);
			}
		}
		return subList;
	}

	public List<SelectItem> getAccountTypes() {
		if (accountEntryTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();

			accountEntryTypes = new LinkedList<SelectItem>();
			String r = "------------------";
			accountEntryTypes.add(new SelectItem(AccountEntryType.MANUAL, AccountEntryType.MANUAL.getName(locale)));
			accountEntryTypes.add(new SelectItem(null, r, r, true));
			accountEntryTypes.add(new SelectItem(AccountEntryType.SALES_INVOICE, AccountEntryType.SALES_INVOICE.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.PURCHASE_INVOICE, AccountEntryType.PURCHASE_INVOICE.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.EXPENSE_INVOICE, AccountEntryType.EXPENSE_INVOICE.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.PAYMENT, AccountEntryType.PAYMENT.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.COLLECTION, AccountEntryType.COLLECTION.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.RETURNED_PAYMENT, AccountEntryType.RETURNED_PAYMENT.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.RETURNED_COLLECTION, AccountEntryType.RETURNED_COLLECTION.getName(locale)));
			accountEntryTypes.add(new SelectItem(null, r, r, true));
			accountEntryTypes.add(new SelectItem(AccountEntryType.SALARY, AccountEntryType.SALARY.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.EXPENSES, AccountEntryType.EXPENSES.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.SOCIAL_INSURANCE, AccountEntryType.SOCIAL_INSURANCE.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.SOCIAL_INSURANCE_ADJUST, AccountEntryType.SOCIAL_INSURANCE_ADJUST.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.LOAN, AccountEntryType.LOAN.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.LOAN_FEE, AccountEntryType.LOAN_FEE.getName(locale)));
			accountEntryTypes.add(new SelectItem(null, r, r, true));
			accountEntryTypes.add(new SelectItem(AccountEntryType.AMORTIZATION, AccountEntryType.AMORTIZATION.getName(locale)));
			accountEntryTypes.add(new SelectItem(null, r, r, true));
			accountEntryTypes.add(new SelectItem(AccountEntryType.OPENING, AccountEntryType.OPENING.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.CLOSING, AccountEntryType.CLOSING.getName(locale)));
			accountEntryTypes.add(new SelectItem(AccountEntryType.OPERATING, AccountEntryType.OPERATING.getName(locale)));

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
		List<ITransferObject> list = loanBean.getList(null);
		for (ITransferObject to : list) {
			Loan loan = (Loan) to;
			SelectItem item = new SelectItem(loan, loan.getDescription());
			loans.add(item);
		}
		return loans;
	}

	public List<SelectItem> getActiveLoans() throws ManagerBeanException {
		List<SelectItem> loans = new LinkedList<SelectItem>();
		IManagerBean loanBean = BeanManager.getManagerBean(Loan.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanBean.getFieldName(IEntityAlias.LOAN_STATUS), LoanStatus.ACTIVE);
		List<ITransferObject> list = loanBean.getList(criteria);
		for (ITransferObject to : list) {
			Loan loan = (Loan) to;
			SelectItem item = new SelectItem(loan, loan.getDescription());
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
		Criteria c = new Criteria();
		String alias = atBean.getFieldName(IEntityAlias.AMORTIZATION_TYPE_DOMAIN);
		addParentDomainExpression(c,alias);
		List<ITransferObject> list = atBean.getList(c);
		for (ITransferObject to : list) {
			AmortizationType at = (AmortizationType) to;
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

		String alias = balanceBean.getFieldName(IEntityAlias.BALANCE_DOMAIN);
		addParentDomainExpression(c,alias);
		Integer domainId = DomainManager.getCurrentDomain();
		
		c.addEqualExpression(balanceBean.getFieldName(IEntityAlias.BALANCE_TYPE), balanceType);
		c.addOrder(alias, true);
		List<ITransferObject> list = balanceBean.getList(c);
		for (ITransferObject to : list) {
			Balance b = (Balance) to;
			String prefix = ""; 
			if (!ObjectUtils.equals(domainId, b.getDomain())) {
				prefix = " + ";	
			}
			SelectItem item = new SelectItem(b, prefix + b.getName());
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

	public List<SelectItem> getQuarters() {
		if (quarters == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			quarters = new LinkedList<SelectItem>();
			Quarter[] bTypes = Quarter.values();
			for (int i = 0; i < bTypes.length; i++) {
				Quarter type = bTypes[i];
				String name = type.getName(locale);
				SelectItem item = new SelectItem(type, name);
				quarters.add(item);
			}
		}
		return quarters;
	}

	public List<SelectItem> getTemplateTypes() {
		if (templateTypes == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			templateTypes = new LinkedList<SelectItem>();
			templateTypes.add(new SelectItem(RegistryAttachmentType.FISCAL_REPORTS, RegistryAttachmentType.FISCAL_REPORTS.getName(locale)));
			templateTypes.add(new SelectItem(RegistryAttachmentType.FISCAL_TEMPLATES, RegistryAttachmentType.FISCAL_TEMPLATES.getName(locale)));
		}
		return templateTypes;
	}

	public List<SelectItem> getReportTemplates() throws ManagerBeanException {
		List<SelectItem> reportTemplates = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE),
				RegistryAttachmentType.FISCAL_TEMPLATES);
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			RegistryAttachment ra = (RegistryAttachment) to;
			SelectItem item = new SelectItem(ra.getId(), ra.getDescription());
			reportTemplates.add(item);
		}
		return reportTemplates;
	}

	public List<SelectItem> getReportAttachments() throws ManagerBeanException {
		List<SelectItem> reportTemplates = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE),
				RegistryAttachmentType.FISCAL_REPORTS);
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_SECURITY_LEVEL), SecurityLevel.OFFICIAL);
		}
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			RegistryAttachment ra = (RegistryAttachment) to;
			SelectItem item = new SelectItem(ra.getId(), ra.getDescription());
			reportTemplates.add(item);
		}
		return reportTemplates;
	}

	public List<SelectItem> getEnabledAccountPeriodKeys() throws ManagerBeanException {
		List<SelectItem> accountPeriods = new LinkedList<SelectItem>();
		IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
		Criteria criteria = new Criteria();
		Expression e1 = ExpressionUtilities.getEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.ACTIVE);
		Expression e2 = ExpressionUtilities.getEqualExpression(getPeriodStatusAlias(), AccountPeriodStatus.OPENING);
		criteria.addExpression(ExpressionUtilities.getOrExpression(e1, e2));
		criteria.addOrder(getPeriodIdAlias(), false);
		List<ITransferObject> list = periodBean.getList(criteria);
		for (ITransferObject to : list) {
			Period period = (Period) to;
			SelectItem item = new SelectItem(period.getId(), period.getName());
			accountPeriods.add(item);
		}
		return accountPeriods;
	}

	private void addParentDomainExpression(Criteria criteria, String alias) {
		criteria.setSkipDomainFilter(true);
		Integer domainId = DomainManager.getCurrentDomain();
		Expression domainExpression = ExpressionUtilities.getEqualExpression(alias, domainId);
    	Integer parentDomainId = AdminUtil.getParentDomain(domainId);
    	if ( parentDomainId != null ) {
    		Expression parentDomainExpression = ExpressionUtilities.getEqualExpression(alias, parentDomainId);
    		domainExpression = ExpressionUtilities.getOrExpression(domainExpression, parentDomainExpression);	
    	}
    	criteria.addExpression(domainExpression);
	}
}