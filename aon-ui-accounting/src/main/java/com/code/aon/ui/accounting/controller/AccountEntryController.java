package com.code.aon.ui.accounting.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.ListDataModel;

import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.LeasingAccount;
import com.code.aon.account.bridge.LoanAccount;
import com.code.aon.account.bridge.RegistryBankAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.account.bridge.util.AccountUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.ExpenseEntryHeader;
import com.code.aon.accounting.InvoiceEntryDetail;
import com.code.aon.accounting.InvoiceEntryHeader;
import com.code.aon.accounting.Leasing;
import com.code.aon.accounting.LeasingFeeEntryHeader;
import com.code.aon.accounting.Loan;
import com.code.aon.accounting.LoanFeeEntryHeader;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.SalaryEntryHeader;
import com.code.aon.accounting.SocialInsuranceEntryHeader;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class AccountEntryController extends BasicController {
	
	private static final Logger LOGGER = Logger.getLogger(AccountEntryController.class.getName());
	
	private static final String INVOICE_ENTRY_CONTROLLER_NAME = "invoiceEntry";
	
	private static final String EXPENSE_ENTRY_CONTROLLER_NAME = "expenseEntry";
	
	private static final String SALARY_ENTRY_CONTROLLER_NAME = "salaryEntry";
	
	private static final String SOCIAL_INSURANCE_ENTRY_CONTROLLER_NAME = "socialInsuranceEntry";
	
	private static final String LOAN_ENTRY_CONTROLLER_NAME = "loanEntry";

	private static final String LOAN_FEE_ENTRY_CONTROLLER_NAME = "loanFeeEntry";

	private static final String LEASING_ENTRY_CONTROLLER_NAME = "leasingEntry";

	private static final String LEASING_FEE_ENTRY_CONTROLLER_NAME = "leasingFeeEntry";
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
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

	public void onSelectEntry(ActionEvent event) throws ManagerBeanException {
		AccountEntry entry = (AccountEntry)this.getModel().getRowData();
		this.setTo(entry);
		if(entry.getType().equals(AccountEntryType.SALES_INVOICE) ||
				entry.getType().equals(AccountEntryType.PURCHASE_INVOICE) ||
				entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)){
			loadInvoiceEntryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.EXPENSES)){
			loadExpenseEntryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.SALARY)){
			loadSalaryEntryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.SOCIAL_INSURANCE)){
			loadSocialInsuranceEntryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LOAN)){
			loadLoanEntryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LOAN_FEE)){
			loadLoanFeeEntryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LEASING)){
			loadLeasingEntryController(entry);
		}
		if(entry.getType().equals(AccountEntryType.LEASING_FEE)){
			loadLeasingFeeEntryController(entry);
		}
	}
	
	public String onNavigate() {
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.SALES_INVOICE) ||
				((AccountEntry)getTo()).getType().equals(AccountEntryType.PURCHASE_INVOICE) ||
				((AccountEntry)getTo()).getType().equals(AccountEntryType.EXPENSE_INVOICE)){
			return "account_invoice_entry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.EXPENSES)){
			return "account_expense_entry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.SALARY)){
			return "account_salary_entry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.SOCIAL_INSURANCE)){
			return "account_social_insurance_entry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LOAN)){
			return "account_loan_entry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LOAN_FEE)){
			return "account_loan_fee_entry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LEASING)){
			return "account_leasing_entry";
		}
		if(((AccountEntry)getTo()).getType().equals(AccountEntryType.LEASING_FEE)){
			return "account_leasing_fee_entry";
		}
		return "";
	}
	
	@SuppressWarnings("unchecked")
	private void loadInvoiceEntryController(AccountEntry entry) {
		try {
			InvoiceEntryController invoiceEntryController = (InvoiceEntryController)AonUtil.getRegisteredBean(INVOICE_ENTRY_CONTROLLER_NAME);
			invoiceEntryController.onReset(null);
			invoiceEntryController.setNew(false);
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID), entry.getId());
			Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iter.next();
				invoiceEntryController.setAccountEntryInvoice(accountEntryInvoice);
				InvoiceEntryHeader header = new InvoiceEntryHeader();
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
				header.setWithholding(accountEntryInvoice.getInvoice().isWithholding());
				header.setSurcharge(accountEntryInvoice.getInvoice().isSurcharge());
				header.setTaxFree(accountEntryInvoice.getInvoice().isTaxFree());
				header.setInvestment(accountEntryInvoice.getInvoice().isInvestment());
				header.setTransaction(accountEntryInvoice.getInvoice().getTransaction());
				header.setAccountEntryId(entry.getId());
				invoiceEntryController.setHeader(header);
				invoiceEntryController.setFinances(new ListDataModel(obtainFinances(accountEntryInvoice.getInvoice())));
				invoiceEntryController.setDetails(new ListDataModel(obtainDetails(accountEntryInvoice.getInvoice())));
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading InvoiceEntryController", e);
		}
	}
	
	private void loadExpenseEntryController(AccountEntry entry) {
		ExpenseEntryController expenseEntryController = (ExpenseEntryController)AonUtil.getRegisteredBean(EXPENSE_ENTRY_CONTROLLER_NAME);
		expenseEntryController.onReset(null);
		expenseEntryController.setNew(false);
		expenseEntryController.setAccountEntry(entry);
		ExpenseEntryHeader header = new ExpenseEntryHeader();
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
		expenseEntryController.setHeader(header);
	}
	
	private void loadSalaryEntryController(AccountEntry entry) {
		SalaryEntryController salaryController = (SalaryEntryController)AonUtil.getRegisteredBean(SALARY_ENTRY_CONTROLLER_NAME);
		salaryController.onReset(null);
		salaryController.setNew(false);
		salaryController.setAccountEntry(entry);
		SalaryEntryHeader header = new SalaryEntryHeader();
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
	
	private void loadSocialInsuranceEntryController(AccountEntry entry) {
		SocialInsuranceEntryController socialInsController = (SocialInsuranceEntryController)AonUtil.getRegisteredBean(SOCIAL_INSURANCE_ENTRY_CONTROLLER_NAME);
		socialInsController.onReset(null);
		socialInsController.setNew(false);
		socialInsController.setAccountEntry(entry);
		SocialInsuranceEntryHeader header = new SocialInsuranceEntryHeader();
		Period period = new Period();
		period.setId(entry.getAccountPeriod());
		header.setPeriod(period);
		header.setDate(entry.getEntryDate());
		AccountEntryDetail accountEntryDetail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
		header.setAmount(accountEntryDetail.getCredit());
		header.setRBank(obtainRBank(accountEntryDetail.getAccount().getId()));
		header.setDescription(accountEntryDetail.getConcept());
		header.setSecurityLevel(entry.getSecurityLevel());
		socialInsController.setHeader(header);
	}

	private void loadLoanEntryController(AccountEntry entry) {
		try {
			LoanEntryController loanController = (LoanEntryController)AonUtil.getRegisteredBean(LOAN_ENTRY_CONTROLLER_NAME);
			loanController.onReset(null);
			loanController.setNew(false);
			loanController.setAccountEntry(entry);
			Loan loan = obtainLoan(entry);
			loanController.setLoan(loan);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading LoanEntryController", e);
		}
	}

	private void loadLoanFeeEntryController(AccountEntry entry) {
		try {
			LoanFeeEntryController loanFeeController = (LoanFeeEntryController)AonUtil.getRegisteredBean(LOAN_FEE_ENTRY_CONTROLLER_NAME);
			loanFeeController.onReset(null);
			loanFeeController.setNew(false);
			loanFeeController.setAccountEntry(entry);
			LoanFeeEntryHeader header = new LoanFeeEntryHeader();
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
			LOGGER.log(Level.SEVERE, "Error loading LoanFeeEntryController", e);
		}
	}
	
	private void loadLeasingEntryController(AccountEntry entry) {
		try {
			LeasingEntryController leasingController = (LeasingEntryController)AonUtil.getRegisteredBean(LEASING_ENTRY_CONTROLLER_NAME);
			leasingController.onReset(null);
			leasingController.setNew(false);
			leasingController.setAccountEntry(entry);
			Leasing leasing = obtainLeasing(entry);
			leasingController.setLeasing(leasing);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading LeasingEntryController", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadLeasingFeeEntryController(AccountEntry entry) {
		try {
			LeasingFeeEntryController leasingEntryFeeController = (LeasingFeeEntryController)AonUtil.getRegisteredBean(LEASING_FEE_ENTRY_CONTROLLER_NAME);
			leasingEntryFeeController.onReset(null);
			leasingEntryFeeController.setNew(false);
			IManagerBean accountEntryInvoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountEntryInvoiceBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID), entry.getId());
			Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
			if(iter.hasNext()){
				AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice)iter.next();
				leasingEntryFeeController.setAccountEntryInvoice(accountEntryInvoice);
				LeasingFeeEntryHeader header = new LeasingFeeEntryHeader();
				AccountEntryDetail detail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.BANK_ACCOUNT_PREFIX + "*");
				header.setRBank(obtainRBank(detail.getAccount().getId()));
				detail = obtainEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.DEBT_INTEREST_ACCOUNT).getId() + "");
				header.setInterest(detail.getDebit());
				detail = obtainEntryDetailFromAccountPattern(entry, AccountConstants.LEASING_ACCOUNT_PREFIX + "*");
				header.setAmortization(detail.getDebit());
				detail = obtainEntryDetailFromAccountPattern(entry, AccountUtil.obtainDefaultAccount(DefaultAccounts.FINANCIAL_EXPENSES_ACCOUNT).getId() + "");
				header.setExpenses(detail.getDebit());
				header.setLeasing(obtainLeasing(entry));
				header.setLeasingFeeDate(accountEntryInvoice.getInvoice().getIssueDate());
				header.setSecurityLevel(accountEntryInvoice.getInvoice().getSecurityLevel());
				header.setSeries(accountEntryInvoice.getInvoice().getSeries());
				header.setNumber(accountEntryInvoice.getInvoice().getNumber());
				header.setReferenceCode(accountEntryInvoice.getInvoice().getReferenceCode());
				
				leasingEntryFeeController.setHeader(header);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading InvoiceEntryController", e);
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
	
	private List<InvoiceEntryDetail> obtainDetails(Invoice invoice) {
		List<InvoiceEntryDetail> details = new LinkedList<InvoiceEntryDetail>();
		try {
			IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InvoiceEntryDetail detail = new InvoiceEntryDetail();
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				detail.setTaxableBase(invoiceDetail.getTaxableBase());

				Criteria taxCriteria = new Criteria();
				taxCriteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<?> taxIter= invoiceTaxBean.getList(taxCriteria).iterator();
				while(taxIter.hasNext()){
					InvoiceTax invoiceTax = (InvoiceTax)taxIter.next();
					if(invoiceTax.getTaxType().equals(TaxType.VAT)){
						detail.setVatPercent(invoiceTax.getPercentage());
						detail.setSurchargePercent(invoiceTax.getSurcharge());
						
					} else if(invoiceTax.getTaxType().equals(TaxType.RETENTION)){
						detail.setRetentionPercent(invoiceTax.getPercentage());
					}
				}

				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(invoiceAccountBean.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<?> accountIter= invoiceAccountBean.getList(accountCriteria).iterator();
				if(accountIter.hasNext()){
					InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)accountIter.next();
					detail.setAccount(invoiceDetailAccount.getAccount());
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
			criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
			criteria.addExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), accountPattern);
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
		if(event.getNewValue() != null ){
			IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
			String field = event.getComponent().getId();
			getCriteria().addEqualExpression(accountEntryBean.getFieldName(field), event.getNewValue());
		}
	}

    public boolean isManual() {
        return (this.getTo() != null && ((AccountEntry)this.getTo()).getType() == AccountEntryType.MANUAL);
    }

    @SuppressWarnings("unchecked")
    public double getTotalDebit() {
        double debit = 0;
        if (this.getTo() != null ) {
	        try {
	            Integer id = ((AccountEntry)this.getTo()).getId();
	            IManagerBean detailsBean = BeanManager.getManagerBean(AccountEntryDetail.class);
	            Criteria criteria = new Criteria();
	            criteria.addEqualExpression(detailsBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
	            Iterator iterator = detailsBean.getList(criteria).iterator();
	            while (iterator.hasNext()) {
	                AccountEntryDetail detail = (AccountEntryDetail)iterator.next();
	                debit += detail.getDebit();
	            }
	        } catch (ManagerBeanException e) {
	            LOGGER.log(Level.SEVERE, "Error getting Account Entry Details", e);
	        }
        }
        return debit;
    }

    @SuppressWarnings("unchecked")
    public double getTotalCredit() {
        double credit = 0;
        if (this.getTo() != null ) {
	        try {
	            Integer id = ((AccountEntry)this.getTo()).getId();
	            IManagerBean detailsBean = BeanManager.getManagerBean(AccountEntryDetail.class);
	            Criteria criteria = new Criteria();
	            criteria.addEqualExpression(detailsBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), id);
	            Iterator iterator = detailsBean.getList(criteria).iterator();
	            while (iterator.hasNext()) {
	                AccountEntryDetail detail = (AccountEntryDetail)iterator.next();
	                credit += detail.getCredit();
	            }
	        } catch (ManagerBeanException e) {
	            LOGGER.log(Level.SEVERE, "Error getting Account Entry Details", e);
	        }
        }
        return credit;
    }

}