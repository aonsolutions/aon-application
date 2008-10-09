package com.code.aon.ui.account.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.account.AccountEntry;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.AccountExpenseHeader;
import com.code.aon.account.AccountInvoiceDetail;
import com.code.aon.account.AccountInvoiceHeader;
import com.code.aon.account.AccountLeasingFeeHeader;
import com.code.aon.account.AccountLoanFeeHeader;
import com.code.aon.account.AccountSalaryHeader;
import com.code.aon.account.AccountSocialInsuranceHeader;
import com.code.aon.account.DefaultAccounts;
import com.code.aon.account.Leasing;
import com.code.aon.account.Loan;
import com.code.aon.account.Period;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.enumeration.TaxType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountEntryController.class.getName());
	
	private static final String ACCOUNT_INVOICE_CONTROLLER_NAME = "accountInvoice";
	
	private static final String ACCOUNT_EXPENSES_CONTROLLER_NAME = "accountExpense";
	
	private static final String ACCOUNT_SALARY_CONTROLLER_NAME = "accountSalary";
	
	private static final String ACCOUNT_SOCIAL_INSURANCE_CONTROLLER_NAME = "accountSocialInsurance";
	
	private static final String ACCOUNT_LOAN_CONTROLLER_NAME = "accountLoan";

	private static final String ACCOUNT_LOAN_FEE_CONTROLLER_NAME = "accountLoanFee";

	private static final String ACCOUNT_LEASING_CONTROLLER_NAME = "accountLeasing";

	private static final String ACCOUNT_LEASING_FEE_CONTROLLER_NAME = "accountLeasingFee";

    @SuppressWarnings("unused")
	public void onReset(MenuEvent event) throws ManagerBeanException {
        clearCriteria();
    }

    @Override
    public void onSearch(ActionEvent event) {
        super.onSearch(event);
        if (model.getRowCount() > 0) {
            model.setRowIndex(0);
            super.onSelect(null);
        }
    }

    @Override
    public void onRemove(ActionEvent event) {
        super.onRemove(event);
        try {
            if (this.getModel().getRowCount() > 0) {
                this.setTo((AccountEntry)this.getModel().getRowData());
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error removing Account Entry", e);
        }
    }

    @SuppressWarnings("unused")
	public void onSelectEntry(ActionEvent event) throws ManagerBeanException {
		AccountEntry entry = (AccountEntry)this.getModel().getRowData();
		this.setTo(entry);
		if(entry.getType().equals(AccountEntryType.SALES_INVOICE) ||
				entry.getType().equals(AccountEntryType.PURCHASE_INVOICE) ||
				entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)){
			loadAccountInvoiceController(entry);
		}
		if(entry.getType().equals(AccountEntryType.EXPENSES)){
			loadAccountExpensesController(entry);
		}
		if(entry.getType().equals(AccountEntryType.SALARY)){
			loadAccountSalaryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.SOCIAL_INSURANCE)){
			loadAccountSocialInsuranceController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LOAN)){
			loadAccountLoanController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LOAN_FEE)){
			loadAccountLoanFeeController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LEASING)){
			loadAccountLeasingController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LEASING_FEE)){
			loadAccountLeasingFeeController(entry);
		}
	}
	
	public String onNavigate() {
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.SALES_INVOICE) ||
				((AccountEntry)getTo()).getType().equals(AccountEntryType.PURCHASE_INVOICE) ||
				((AccountEntry)getTo()).getType().equals(AccountEntryType.EXPENSE_INVOICE)){
			return "accountInvoiceEntry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.EXPENSES)){
			return "accountExpenseEntry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.SALARY)){
			return "accountSalaryEntry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.SOCIAL_INSURANCE)){
			return "accountSocialInsuranceEntry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LOAN)){
			return "accountLoanEntry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LOAN_FEE)){
			return "accountLoanFeeEntry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LEASING)){
			return "accountLeasingEntry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LEASING_FEE)){
			return "accountLeasingFeeEntry";
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private void loadAccountInvoiceController(AccountEntry entry) {
		try {
			AccountInvoiceController accountInvoiceController = (AccountInvoiceController)AonUtil.getRegisteredBean(ACCOUNT_INVOICE_CONTROLLER_NAME);
			accountInvoiceController.onReset(null);
			accountInvoiceController.setNew(false);
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID), entry.getId());
			Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iter.next();
				accountInvoiceController.setAccountEntryInvoice(accountEntryInvoice);
				AccountInvoiceHeader header = new AccountInvoiceHeader();
				AccountEntryDetail detail = null;
				if(entry.getType().equals(AccountEntryType.SALES_INVOICE)){
					header.setType(InvoiceType.SALES);
					detail = obtainEntryDetailFromAccountPattern(entry, "70*");
					header.setAccount((detail!=null)?detail.getAccount():null);
				}
				if(entry.getType().equals(AccountEntryType.PURCHASE_INVOICE)){
					header.setType(InvoiceType.PURCHASE);
					detail = obtainEntryDetailFromAccountPattern(entry, "60*");
					header.setAccount((detail!=null)?detail.getAccount():null);
				}
				if(entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)){
					header.setType(InvoiceType.EXPENSES);
					detail = obtainEntryDetailFromAccountPattern(entry, "6*");
					header.setAccount((detail!=null)?detail.getAccount():null);
				}
				header.setDate(entry.getEntryDate());
				header.setDocument(accountEntryInvoice.getInvoice().getRegistryDocument());
				header.setName(accountEntryInvoice.getInvoice().getRegistryName());
				header.setSeries(accountEntryInvoice.getInvoice().getSeries());
				header.setNumber(accountEntryInvoice.getInvoice().getNumber());
				header.setReferenceCode(accountEntryInvoice.getInvoice().getReferenceCode());
				header.setPeriod(new Period());
				header.getPeriod().setId(entry.getAccountPeriod());
				header.setSecurityLevel(entry.getSecurityLevel());
				header.setRegistry(accountEntryInvoice.getInvoice().getRegistry());
				accountInvoiceController.setHeader(header);
				accountInvoiceController.setFinances(new ListDataModel(obtainFinances(accountEntryInvoice.getInvoice())));
				accountInvoiceController.setDetails(new ListDataModel(obtainDetails(accountEntryInvoice.getInvoice())));
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountInvoiceController", e);
		}
	}
	
	private void loadAccountExpensesController(AccountEntry entry) {
		AccountExpensesController accountExpensesController = (AccountExpensesController)AonUtil.getRegisteredBean(ACCOUNT_EXPENSES_CONTROLLER_NAME);
		accountExpensesController.onReset(null);
		accountExpensesController.setNew(false);
		accountExpensesController.setAccountEntry(entry);
		AccountExpenseHeader header = new AccountExpenseHeader();
		Period period = new Period();
		period.setId(entry.getAccountPeriod());
		header.setPeriod(period);
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, "6*");
		header.setAccount(accountEntryDetail.getAccount());
		header.setDescription(accountEntryDetail.getConcept());
		header.setAmount(accountEntryDetail.getDebit());
		header.setRBank(obtainRBank(accountEntryDetail.getBalancingAccount().getId()));
		header.setSecurityLevel(entry.getSecurityLevel());
		accountExpensesController.setHeader(header);
	}
	
	private void loadAccountSalaryController(AccountEntry entry) {
		AccountSalaryController salaryController = (AccountSalaryController)AonUtil.getRegisteredBean(ACCOUNT_SALARY_CONTROLLER_NAME);
		salaryController.onReset(null);
		salaryController.setNew(false);
		salaryController.setAccountEntry(entry);
		AccountSalaryHeader header = new AccountSalaryHeader();
		Period period = new Period();
		period.setId(entry.getAccountPeriod());
		header.setPeriod(period);
		header.setDate(entry.getEntryDate());
		header.setSecurityLevel(entry.getSecurityLevel());
		AccountEntryDetail accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, "6*");
		header.setGrossSalary(accountEntryDetail.getDebit());
		header.setDescription(accountEntryDetail.getConcept());
		accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, "47501");
		header.setRetention(accountEntryDetail.getCredit());
		accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, "476");
		header.setSocialInsurance(accountEntryDetail.getCredit());
		accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, "5*");
		header.setRBank(obtainRBank(accountEntryDetail.getAccount().getId()));
		salaryController.setHeader(header);
	}
	
	private void loadAccountSocialInsuranceController(AccountEntry entry) {
		AccountSocialInsuranceController socialInsController = (AccountSocialInsuranceController)AonUtil.getRegisteredBean(ACCOUNT_SOCIAL_INSURANCE_CONTROLLER_NAME);
		socialInsController.onReset(null);
		socialInsController.setNew(false);
		socialInsController.setAccountEntry(entry);
		AccountSocialInsuranceHeader header = new AccountSocialInsuranceHeader();
		Period period = new Period();
		period.setId(entry.getAccountPeriod());
		header.setPeriod(period);
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
		header.setAmount(accountEntryDetail.getCredit());
		header.setRegistryBank(obtainRBank(accountEntryDetail.getAccount().getId()));
		header.setDescription(accountEntryDetail.getConcept());
		header.setSecurityLevel(entry.getSecurityLevel());
		socialInsController.setHeader(header);
	}

	private void loadAccountLoanController(AccountEntry entry) {
		try {
			AccountLoanController loanController = (AccountLoanController)AonUtil.getRegisteredBean(ACCOUNT_LOAN_CONTROLLER_NAME);
			loanController.onReset(null);
			loanController.setNew(false);
			loanController.setAccountEntry(entry);
			Loan loan = obtainLoan(entry);
			loanController.setLoan(loan);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountLoanController", e);
		}
	}

	private void loadAccountLoanFeeController(AccountEntry entry) {
		try {
			AccountLoanFeeController loanFeeController = (AccountLoanFeeController)AonUtil.getRegisteredBean(ACCOUNT_LOAN_FEE_CONTROLLER_NAME);
			loanFeeController.onReset(null);
			loanFeeController.setNew(false);
			loanFeeController.setAccountEntry(entry);
			AccountLoanFeeHeader header = new AccountLoanFeeHeader();
			Loan loan = obtainLoan(entry);
			AccountEntryDetail accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.LOAN_ACCOUNT_PREFIX + "*");
			header.setAmortization(accountEntryDetail.getDebit());
			header.setDescription(accountEntryDetail.getConcept());
			header.setFeeDate(entry.getEntryDate());
			header.setLoan(loan);
			header.setRegistryBank(obtainRBank(accountEntryDetail.getBalancingAccount().getId()));
			accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT).getId() + "*");
			header.setInterest(accountEntryDetail.getDebit());
			loanFeeController.setHeader(header);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountLoanFeeController", e);
		}
	}
	
	private void loadAccountLeasingController(AccountEntry entry) {
		try {
			AccountLeasingController leasingController = (AccountLeasingController)AonUtil.getRegisteredBean(ACCOUNT_LEASING_CONTROLLER_NAME);
			leasingController.onReset(null);
			leasingController.setNew(false);
			leasingController.setAccountEntry(entry);
			Leasing leasing = obtainLeasing(entry);
			leasingController.setLeasing(leasing);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountLeasingController", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadAccountLeasingFeeController(AccountEntry entry) {
		try {
			AccountLeasingFeeController accountLeasingFeeController = (AccountLeasingFeeController)AonUtil.getRegisteredBean(ACCOUNT_LEASING_FEE_CONTROLLER_NAME);
			accountLeasingFeeController.onReset(null);
			accountLeasingFeeController.setNew(false);
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID), entry.getId());
			Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iter.next();
				accountLeasingFeeController.setAccountEntryInvoice(accountEntryInvoice);
				AccountLeasingFeeHeader header = new AccountLeasingFeeHeader();
				AccountEntryDetail detail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
				header.setRegistryBank(obtainRBank(detail.getAccount().getId()));
				detail = obtainEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT).getId() + "");
				header.setInterest(detail.getDebit());
				detail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.LEASING_ACCOUNT_PREFIX + "*");
				header.setAmortization(detail.getDebit());
				header.setTaxableBase(header.getAmortization() + header.getInterest());
				detail = obtainEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.FINANCIAL_EXPENSES_ACCOUNT).getId() + "");
				header.setExpenses(detail.getDebit());
				header.setLeasing(obtainLeasing(entry));
				header.setLeasingFeeDate(accountEntryInvoice.getInvoice().getIssueDate());
				header.setSecurityLevel(accountEntryInvoice.getInvoice().getSecurityLevel());
				header.setSeries(accountEntryInvoice.getInvoice().getSeries());
				header.setNumber(accountEntryInvoice.getInvoice().getNumber());
				header.setReferenceCode(accountEntryInvoice.getInvoice().getReferenceCode());
				
				accountLeasingFeeController.setHeader(header);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading AccountInvoiceController", e);
		}
	}

	@SuppressWarnings("unchecked")
	private List obtainFinances(Invoice invoice) {
		List<Finance> finances = new LinkedList<Finance>();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID), invoice.getId());
			Iterator iter = financeBean.getList(criteria).iterator();
			while(iter.hasNext()){
				finances.add((Finance)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining finances for invoice with id=" + invoice.getId(), e);
		}
		return finances;
	}
	
	@SuppressWarnings("unchecked")
	private List obtainDetails(Invoice invoice) {
		List<AccountInvoiceDetail> details = new LinkedList<AccountInvoiceDetail>();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator iter = invoiceDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				AccountInvoiceDetail detail = new AccountInvoiceDetail();
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				detail.setTaxableBase(invoiceDetail.getTaxableBase());

				Criteria taxCriteria = new Criteria();
				taxCriteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator taxIter= invoiceTaxBean.getList(taxCriteria).iterator();
				while(taxIter.hasNext()){
					InvoiceTax invoiceTax = (InvoiceTax)taxIter.next();
					if(invoiceTax.getTaxType().equals(TaxType.VAT)){
						detail.getVat().setPercentage(invoiceTax.getPercentage());
						detail.getVat().setSurcharge(invoiceTax.getSurcharge());
					} else if(invoiceTax.getTaxType().equals(TaxType.RETENTION)){
						detail.getRetention().setPercentage(invoiceTax.getPercentage());
						detail.getRetention().setSurcharge(invoiceTax.getSurcharge());
					}
				}

				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(invoiceAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator accountIter= invoiceAccountBean.getList(accountCriteria).iterator();
				if(accountIter.hasNext()){
					InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)accountIter.next();
					detail.setAccount(invoiceDetailAccount.getAccount().getId());
				}

				details.add(detail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading details for invoice with id=" + invoice.getId(), e);
		}
		return details;
	}

	@SuppressWarnings("unchecked")
	private AccountEntryDetail obtainEntryDetailFromAccountPattern(AccountEntry entry, String accountPattern) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
			criteria.addExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), accountPattern);
			Iterator iter = accountEntryDetailBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (AccountEntryDetail)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining the account with accountPattern= " + accountPattern, e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining the account with accountPattern= " + accountPattern, e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private RegistryBank obtainRBank(String account) {
		try {
			IManagerBean rBankAccountBean = BeanManager.getManagerBean(RegistryBankAccount.class);
			Criteria criteria = new Criteria();
			criteria.addExpression(rBankAccountBean.getFieldName(IAccountBridgeAlias.REGISTRY_BANK_ACCOUNT_ACCOUNT_ID), account);
			Iterator iter = rBankAccountBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return ((RegistryBankAccount)iter.next()).getRegistryBank();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining rBank related with accoount= " + account, e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining rBank related with accoount= " + account, e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Loan obtainLoan(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.LOAN_ACCOUNT_PREFIX + "*");
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LoanAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LOAN_ACCOUNT_ACCOUNT_ID), detail.getAccount().getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LoanAccount)iter.next()).getLoan();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Leasing obtainLeasing(AccountEntry entry) throws ManagerBeanException {
		AccountEntryDetail detail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.LEASING_ACCOUNT_PREFIX + "*");
		IManagerBean loanAccountBean = BeanManager.getManagerBean(LeasingAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(loanAccountBean.getFieldName(IAccountBridgeAlias.LEASING_ACCOUNT_ACCOUNT_ID), detail.getAccount().getId());
		Iterator iter = loanAccountBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return ((LeasingAccount)iter.next()).getLeasing();
		}
		return null;
	}

	public void addEqualExpression(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			String field = event.getComponent().getId();
			getCriteria().addEqualExpression(accountEntryBean.getFieldName(field), event.getNewValue());
		}
	}
	
	public void addEntryDateFromExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
				getCriteria().addGreaterThanOrEqualExpression(accountEntryBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENTRY_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding FROM date expression", e);
			}
		}
	}
	
	public void addEntryDateToExpression(ValueChangeEvent event){
		if(event.getNewValue() != null){
			try {
				IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
				getCriteria().addLessThanOrEqualExpression(accountEntryBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENTRY_DATE), event.getNewValue());
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error adding TO date expression", e);
			}
		}
	}

    public boolean isManual() {
        return (this.getTo() != null && ((AccountEntry)this.getTo()).getType() == AccountEntryType.MANUAL);
    }

    @SuppressWarnings("unchecked")
    public double getTotalDebit() {
        double debit = 0;
        try {
            Integer id = ((AccountEntry)this.getTo()).getId();
            IManagerBean detailsBean = BeanManager.getManagerBean(AccountEntryDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(detailsBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
            Iterator iterator = detailsBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                AccountEntryDetail detail = (AccountEntryDetail)iterator.next();
                debit += detail.getDebit();
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting Account Entry Details", e);
        }
        return debit;
    }

    @SuppressWarnings("unchecked")
    public double getTotalCredit() {
        double credit = 0;
        try {
            Integer id = ((AccountEntry)this.getTo()).getId();
            IManagerBean detailsBean = BeanManager.getManagerBean(AccountEntryDetail.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(detailsBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
            Iterator iterator = detailsBean.getList(criteria).iterator();
            while (iterator.hasNext()) {
                AccountEntryDetail detail = (AccountEntryDetail)iterator.next();
                credit += detail.getCredit();
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error getting Account Entry Details", e);
        }
        return credit;
    }
}