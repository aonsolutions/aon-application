package com.code.aon.ui.accounting.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.AccountHelper;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.InvoiceEntryDetail;
import com.code.aon.accounting.InvoiceEntryHeader;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.account.controller.AccountCollectionsController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class InvoiceEntryController implements ISpecialAccountEntry {

	private static final Logger LOGGER = Logger.getLogger(InvoiceEntryController.class.getName());
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	private static final String ACCOUNT_APP_PARAM_CONTROLLER_NAME = "accAppParams";
	private static final String ACCOUNT_COLLECTIONS_CONTROLLER_NAME = "accountCollections";

	private AccountEntryInvoiceWriter writer;
	private FinanceGenerator financeGenerator;
	private AccountEntryInvoice accountEntryInvoice;
	private boolean isNew;
	private boolean isNewDetail;
	private boolean isNewFinance;
	private InvoiceEntryHeader header;
	private DataModel details;
	private DataModel finances;
	private InvoiceEntryDetail currentDetail;
	private Finance currentFinance;
	private Company company;
	private String onGenerateKey;
	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;
	private List<SelectItem> relatedAccounts;
	private List<InvoiceEntryDetail> removedDetails;
	private List<Finance> removedFinances;

	public AccountEntryInvoiceWriter getWriter() {
		if (writer == null) {
			writer = new AccountEntryInvoiceWriter();
		}
		return writer;
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}
	
	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}
	
	public AccountEntryInvoice getAccountEntryInvoice() {
		return accountEntryInvoice;
	}

	private List<InvoiceEntryDetail> getRemovedDetails() {
		if (removedDetails == null) {
			removedDetails = new LinkedList<InvoiceEntryDetail>();
		}
		return removedDetails;
	}
	private void setRemovedDetails(List<InvoiceEntryDetail> details) {
		removedDetails = details;	
	}
	
	private List<Finance> getRemovedFinances() {
		if (removedFinances == null) {
			removedFinances = new LinkedList<Finance>();
		}
		return removedFinances;
	}
	private void setRemovedFinances(List<Finance> details) {
		removedFinances = details;	
	}

	public void setAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice) {
		this.accountEntryInvoice = accountEntryInvoice;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public boolean isNewDetail() {
		return isNewDetail;
	}

	public void setNewDetail(boolean isNewDetail) {
		this.isNewDetail = isNewDetail;
	}

	public boolean isNewFinance() {
		return isNewFinance;
	}

	public void setNewFinance(boolean isNewFinance) {
		this.isNewFinance = isNewFinance;
	}

	public InvoiceEntryHeader getHeader() {
		return header;
	}

	public void setHeader(InvoiceEntryHeader header) {
		this.header = header;
	}

	public DataModel getDetails() {
		if (details == null) {
			details = new ListDataModel(new LinkedList<InvoiceEntryDetail>());
		}
		return details;
	}

	public void setDetails(DataModel details) {
		this.details = details;
	}

	public DataModel getFinances() {
		if (finances == null) {
			finances = new ListDataModel(new LinkedList<Finance>());
		}
		return finances;
	}

	public void setFinances(DataModel finances) {
		this.finances = finances;
	}

	public InvoiceEntryDetail getCurrentDetail() {
		return currentDetail;
	}

	public void setCurrentDetail(InvoiceEntryDetail currentDetail) {
		this.currentDetail = currentDetail;
	}

	public Finance getCurrentFinance() {
		return currentFinance;
	}

	public void setCurrentFinance(Finance currentFinance) {
		this.currentFinance = currentFinance;
	}


	public List<SelectItem> getRelatedAccounts() {
		if (relatedAccounts == null) {
			relatedAccounts = getAccounts();
		}
		return relatedAccounts;
	}

	public void setRelatedAccounts(List<SelectItem> relatedAccounts) {
		this.relatedAccounts = relatedAccounts;
	}

	public Company getCompany() {
		try {
			if (company == null) {
				IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
				Iterator<ITransferObject> iter = companyBean.getList(null, 0, 1).iterator();
				if (iter.hasNext()) {
					setCompany((Company) iter.next());
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("Error obtaining Company!");
		}
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	
	public boolean isRegistryFilled() {
		return (getHeader() != null && getHeader().getRegistry() != null && getHeader()
				.getRegistry().getId() != null);
	}

	public void onReset(ActionEvent event) {
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void reset() throws ManagerBeanException {
		setNew(true);
		initializeHeader();
		getHeader().setType(InvoiceType.SALES);

		setDetails(  new ListDataModel(new LinkedList<InvoiceEntryDetail>()));
		setCurrentDetail(null);
		setNewDetail(false);
		setFinances( new ListDataModel(new LinkedList<Finance>()) );
		setCurrentFinance( null );
		setNewFinance(false);
		setRemovedDetails(null);
		setRemovedFinances(null);
	}

	private void initializeHeader() throws ManagerBeanException {
		InvoiceType type = (header != null) ? getHeader().getType() : InvoiceType.SALES;
		Period period = (header != null && getHeader().getPeriod() != null) ? getHeader().getPeriod() : AccountingPeriodUtil.getDefaultPeriod();
		Date date = (header != null && getHeader().getDate() != null) ? getHeader().getDate() : new Date();
		Date taxDate = (header != null && getHeader().getTaxDate() != null) ? getHeader().getTaxDate() : new Date();
		SecurityLevel securityLevel = (header != null && getHeader().getSecurityLevel() != null) ? getHeader().getSecurityLevel() : SecurityLevel.OFFICIAL;
		AccountAppParamsController c = (AccountAppParamsController) AonUtil.getRegisteredBean(ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		ApplicationParameter param = c.getParameter(DefaultAccounts.DEFAULT_INVOICE_SERIES);
		String series = (header != null && !StringUtils.isEmpty(getHeader().getSeries())) ? getHeader().getSeries() : (param != null) ? param.getValue() : null;

		setHeader( new InvoiceEntryHeader() );
		getHeader().setAccount(null);
		getHeader().setType(type);
		getHeader().setSeries(series);
		getHeader().setPeriod(period);
		getHeader().setDate(date);
		getHeader().setTaxDate(taxDate);
		getHeader().setSecurityLevel(securityLevel);
		getHeader().setRegistry(new Registry());
		getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
		getHeader().setInvestment(false);
		getHeader().setTaxFree(false);
		getHeader().setWithholding(false);
		getHeader().setSurcharge(false);

		setRelatedAccounts( null );
	}

	public boolean isSales() {
		if (getHeader() != null) {
			return getHeader().getType().equals(InvoiceType.SALES);
		}
		return false;
	}

	public boolean isPurchase() {
		if (getHeader() != null) {
			return getHeader().getType().equals(InvoiceType.PURCHASE);
		}
		return false;
	}

	public boolean isExpense() {
		if (getHeader() != null) {
			return getHeader().getType().equals(InvoiceType.EXPENSES);
		}
		return false;
	}

	public void onAdjustTaxableBase(ActionEvent event) {
		if (getHeader().getTaxableBase() != null) {
			try {
				AccountAppParamsController c = (AccountAppParamsController) AonUtil
				.getRegisteredBean(ACCOUNT_APP_PARAM_CONTROLLER_NAME);
				ApplicationParameter param = c.getParameter(DefaultAccounts.DEFAULT_VAT_PERCENT);
				if (param != null) {
					String value = param.getValue();
					Integer id = Integer.parseInt(value);
					IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
					Tax tax = (Tax) taxBean.get(id);
					if (tax != null) {
						getHeader().setTaxableBase(CommonUtil.round(getHeader().getTaxableBase() / (1 + (tax.getPercentage()/100))));						
					} 
				}
				onTaxableBaseWizard(event);
			} catch (Exception e) {
				LOGGER.warning("NO SE PUEDE ASIGNAR EL PORCENTAJE DE IVA POR DEFECTO.");
			}
		} else {
			String msg="La Base Imponible es un dato requerido para esta utilidad.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		
	}
	public void onTaxableBaseWizard(ActionEvent event) {
		if (getHeader().getAccount() != null) {
			if (getHeader().getTaxableBase() != null) {
				onNewDetail(event);
				getCurrentDetail().setTaxableBase( getHeader().getTaxableBase()==null?0.0:getHeader().getTaxableBase());
				taxableBaseChanged(getHeader().getTaxableBase());
				onAddDetail(event);
				onCancelDetail(event);
			} else {
				String msg="La Base Imponible es un dato requerido para esta utilidad.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} else {
			String msg="La Cuenta Contable es un dato requerido para esta utilidad.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}

	}

	public void onChangeTaxableBase(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			double t = (Double) event.getNewValue();
			taxableBaseChanged(t);
		}
	}
	private void taxableBaseChanged(double taxableBase) {
		currentDetail.setVatQuota(0.0);
		currentDetail.setSurchargeQuota(0.0);
		currentDetail.setRetentionQuota(0.0);
		
		if(currentDetail.getVatPercent() != 0){
			currentDetail.setVatQuota(CommonUtil.round(taxableBase * currentDetail.getVatPercent() / 100, 2));
		}
		if(currentDetail.getSurchargePercent() != 0){
			currentDetail.setSurchargeQuota( CommonUtil.round(taxableBase * currentDetail.getSurchargePercent() / 100, 2));
		}
		if(currentDetail.getRetentionPercent() != 0){
			currentDetail.setRetentionQuota( CommonUtil.round(taxableBase * currentDetail.getRetentionPercent() / 100, 2));
		}
	}
	
	public void onChangeVatPercent(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			double p = (Double) event.getNewValue();

			currentDetail.setVatQuota(0.0);
			if(currentDetail.getVatPercent() != 0){
				currentDetail.setVatQuota(CommonUtil.round(currentDetail.getTaxableBase() * p / 100, 2));
			}
		}
	}
	
	public void onChangeSurchargePercent(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			double p = (Double) event.getNewValue();

			currentDetail.setSurchargeQuota(0.0);
			if(currentDetail.getSurchargePercent() != 0){
				currentDetail.setSurchargeQuota(CommonUtil.round(currentDetail.getTaxableBase() * p / 100, 2));
			}
		}
	}
	
	public void onChangeRetentionPercent(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			double p = (Double) event.getNewValue();

			currentDetail.setRetentionQuota(0.0);
			if(currentDetail.getRetentionPercent() != 0){
				currentDetail.setRetentionQuota(CommonUtil.round(currentDetail.getTaxableBase() * p / 100, 2));
			}
		}
	}

	public void onNewDetail(ActionEvent event) {
		setNewDetail(true);
		setCurrentDetail( new InvoiceEntryDetail() );
		Account a = (getHeader().getAccount() != null) ? getHeader().getAccount() : null;
		getCurrentDetail().setAccount(a);

		AccountAppParamsController c = (AccountAppParamsController) AonUtil
				.getRegisteredBean(ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		try {
			ApplicationParameter param = c.getParameter(DefaultAccounts.DEFAULT_VAT_PERCENT);
			if (param != null) {
				String value = param.getValue();
				Integer id = Integer.parseInt(value);
				IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
				Tax tax = (Tax) taxBean.get(id);
				if (tax != null) {
					getCurrentDetail().setVatPercent(tax.getPercentage());
					if (isWithSurcharge()) {
						getCurrentDetail().setSurchargePercent(tax.getSurcharge());
					}
				} else {
					LOGGER
							.warning("NO SE PUEDE ASIGNAR EL PORCENTAJE DE IVA POR DEFECTO. ENCONTRADO ["
									+ value + "]");
				}
			}
		} catch (Exception e) {
			LOGGER.warning("NO SE PUEDE ASIGNAR EL PORCENTAJE DE IVA POR DEFECTO.");
		}

		if (isWithHolding()) {
			try {
				ApplicationParameter param = c
						.getParameter(DefaultAccounts.DEFAULT_RETENTION_PERCENT);
				if (param != null) {
					String value = param.getValue();
					Integer id = Integer.parseInt(value);
					IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
					Tax tax = (Tax) taxBean.get(id);
					if (tax != null) {
						getCurrentDetail().setRetentionPercent(tax.getPercentage());
					}
				}
			} catch (Exception e) {
				LOGGER.warning("NO SE PUEDE ASIGNAR EL PORCENTAJE DE RETENCION POR DEFECTO.");
			}
		}
	}

	public void onSelectDetail(ActionEvent event) {
		setCurrentDetail((InvoiceEntryDetail) details.getRowData());
	}

	@SuppressWarnings("unchecked")
	public void onAddDetail(ActionEvent event) {
		if (validateDetail(getCurrentDetail())) {
			// applySurcharge();
			((List<InvoiceEntryDetail>) getDetails().getWrappedData()).add(getCurrentDetail());
			setCurrentDetail(new InvoiceEntryDetail());
			setNewDetail(false);
			onNewDetail(event);
		}
	}

	@SuppressWarnings("unchecked")
	public void onRemoveDetail(ActionEvent event) {
		if (getCurrentDetail().getId() != null) {
			getRemovedDetails().add(getCurrentDetail());	
		}
		((LinkedList<InvoiceEntryDetail>) getDetails().getWrappedData()).remove(getCurrentDetail());
	}

	public void onCancelDetail(ActionEvent event) {
		setCurrentDetail( new InvoiceEntryDetail() );
		setNewDetail(false);
	}

	@SuppressWarnings("unchecked")
	public void onUpdateDetail(ActionEvent event) {
		if (validateDetail(getCurrentDetail())) {
			// applySurcharge();
			int i = ((LinkedList<InvoiceEntryDetail>) getDetails().getWrappedData())
					.indexOf(getCurrentDetail());
			((LinkedList<InvoiceEntryDetail>) getDetails().getWrappedData()).remove(i);
			((LinkedList<InvoiceEntryDetail>) getDetails().getWrappedData()).add(i,
					getCurrentDetail());
			setCurrentDetail( new InvoiceEntryDetail() );
		}
	}

	private boolean validateDetail(InvoiceEntryDetail detail) {
		if (detail.getAccount() != null && !detail.getAccount().equals("")
				&& detail.getAccount().getId() != null) {
			try {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID),
						detail.getAccount().getId());
				Iterator<ITransferObject> iterator = accountBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					Account account = (Account) iterator.next();
					if (!account.isEntryEnabled()) {
						AonUtil.addErrorMessage("La Cuenta Contable " + detail.getAccount()
								+ " no permite apuntes.");
						return false;
					}
				} else {
					AonUtil.addErrorMessage("La Cuenta Contable " + detail.getAccount()
							+ " no existe.");
					return false;
				}
			} catch (ManagerBeanException e) {
				AonUtil.addErrorMessage("Error al obtener el pojo de la cuenta con id = "
						+ detail.getAccount());
				return false;
			}
		}
		return true;
	}

	public void onNewFinance(ActionEvent event) {
		try {
			setNewFinance( true );
			setCurrentFinance( initializeFinance() );
			Invoice invoice = isNew() ? new Invoice() : getAccountEntryInvoice().getInvoice();
			invoice = mergeInvoice(invoice);
			getCurrentFinance().setInvoice(invoice);
			getFinanceGenerator().initializeFinanceData(getCurrentFinance(), obtainInitialAmount());
			if (getCurrentFinance().getBank() == null) {
				getCurrentFinance().setBank(new Bank());
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error al inicializar el vencimiento");
		}
	}

	public void onSelectFinance(ActionEvent event) {
		Finance finance = (Finance) finances.getRowData();
		if (finance.getFinanceStatus() == FinanceStatus.PAID ||
			finance.getFinanceStatus() == FinanceStatus.BATCHED ||
			finance.getFinanceStatus() == FinanceStatus.SETTLED ) {

			String msg = "No se puede modificar un vencimiento no pendiente.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setCurrentFinance( (Finance) finances.getRowData() );
	}

	@SuppressWarnings("unchecked")
	public void onAddFinance(ActionEvent event) {
		((List<Finance>) getFinances().getWrappedData()).add(getCurrentFinance());
		setCurrentFinance( initializeFinance() );
		setNewFinance(false);
	}

	@SuppressWarnings("unchecked")
	public void onRemoveFinance(ActionEvent event) {
		if (getCurrentFinance().getId() != null) {
			getRemovedFinances().add(getCurrentFinance());
		}
		((List<Finance>) getFinances().getWrappedData()).remove(getCurrentFinance());
	}

	public void onCancelFinance(ActionEvent event) {
		setCurrentFinance( initializeFinance() );
		setNewFinance(false);
	}

	@SuppressWarnings("unchecked")
	public void onUpdateFinance(ActionEvent event) {
		int i = ((List<Finance>) getFinances().getWrappedData()).indexOf(getCurrentFinance() );
		((List<Finance>) getFinances().getWrappedData()).remove(i);
		((List<Finance>) getFinances().getWrappedData()).add(i, getCurrentFinance());
		setCurrentFinance( initializeFinance() );
	}

	private Finance initializeFinance() {
		return new Finance();
	}

	@SuppressWarnings("unchecked")
	private double obtainInitialAmount() {
		Iterator<InvoiceEntryDetail> iter = ((List<InvoiceEntryDetail>) getDetails()
				.getWrappedData()).iterator();
		double detailSum = 0;
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = iter.next();
			detailSum += detail.getTotal();
		}
		Iterator financeIter = ((LinkedList) getFinances().getWrappedData()).iterator();
		double financeSum = 0;
		while (financeIter.hasNext()) {
			Finance finance = (Finance) financeIter.next();
			financeSum += finance.getAmount();
		}
		return CommonUtil.round(detailSum - financeSum);
	}

	public void onTypeChanged(ActionEvent event) {
		try {
			initializeHeader();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public String generate() {
		return onGenerateKey;
	}

	public void onGenerate(ActionEvent event) {
		double invoiceTotal = getInvoiceTotal();
		double financeTotal = getFinanceTotal();
		if (financeTotal > 0 && invoiceTotal != financeTotal) {
			String msg = AonUtil.addErrorMessageFromBundle("financeBundle",
					"finance_unable_record_inaccuracy_error");
			throw new AbortProcessingException(msg);
		}
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			if (isNew) {
				generateInvoiceEntry(sessionName,invoiceTotal);
			} else {
				updateInvoiceEntry(sessionName,invoiceTotal);
			}
			setNew(false);
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			onViewAccountEntry(event);
			onGenerateKey = "accountEntry_form";
		} catch (Exception e) {
			onGenerateKey = null;
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.log(Level.SEVERE, msg, e);
			}
			String msg = "No se pudo generar el apunte contable. " + e.getMessage();
			LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void updateInvoiceEntry(String sessionName, double invoiceTotal) throws ManagerBeanException {
			//deleteFinances(getAccountEntryInvoice().getInvoice());
			deleteInvoiceDetails(getAccountEntryInvoice().getInvoice());
			deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());

			AccountEntry entry = getAccountEntryInvoice().getAccountEntry();
			Account account = fillAccountEntry(entry);

			Invoice invoice = insertOrUpdateInvoice(sessionName);
			insertInvoiceDetails(invoice);
			insertFinances(invoice);
			deleteRemovedFinances();
			entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
			entry = getWriter().insertOrUpdateAccountEntry(entry);
			getWriter().insertEntryDetails(entry, account,
					getWriter().obtainConcept(invoice, invoiceTotal), invoiceTotal,
					obtainRetentionQuotasPerAccount(invoice), obtainTaxQuotasPerAccount(invoice),
					obtainBasesPerAccount(details));
			getHeader().setAccountEntryId(entry.getId());
	}

	private void deleteRemovedFinances() throws ManagerBeanException {
		if (getRemovedFinances() != null) {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			for (Finance f: getRemovedFinances()) {
				financeBean.remove(f);
			}
		}
	}

	private void generateInvoiceEntry(String sessionName, double invoiceTotal) throws ManagerBeanException {
			AccountEntry entry = new AccountEntry();
			Account account = fillAccountEntry(entry);
			
			Invoice invoice = insertOrUpdateInvoice(sessionName);
			insertInvoiceDetails(invoice);
			insertFinances(invoice);
			entry = getWriter().insertOrUpdateAccountEntry(entry);
			setAccountEntryInvoice(getWriter().insertAccountEntryInvoice(entry, invoice));
			getWriter().insertEntryDetails(entry, account,
					getWriter().obtainConcept(invoice, invoiceTotal), invoiceTotal,
					obtainRetentionQuotasPerAccount(invoice), obtainTaxQuotasPerAccount(invoice),
					obtainBasesPerAccount(details));
			getHeader().setAccountEntryId(entry.getId());
	}

	private Account fillAccountEntry(AccountEntry entry) throws ManagerBeanException {
		Account account = null;
		entry.setAccountPeriod(getHeader().getPeriod().getId());
		entry.setEntryDate(getHeader().getDate());
		entry.setJournal(null);
		entry.setSecurityLevel(getHeader().getSecurityLevel());
		if (getHeader().getType().equals(InvoiceType.SALES)) {
			entry.setType(AccountEntryType.SALES_INVOICE);
			account = getAccountBridgeUtil().obtainCustomerAccount(getHeader().getRegistry());
		} else if (getHeader().getType().equals(InvoiceType.PURCHASE)) {
			entry.setType(AccountEntryType.PURCHASE_INVOICE);
			account = getAccountBridgeUtil().obtainSupplierAccount(getHeader().getRegistry());
		} else if (getHeader().getType().equals(InvoiceType.EXPENSES)) {
			entry.setType(AccountEntryType.EXPENSE_INVOICE);
			account = getAccountBridgeUtil().obtainCreditorAccount(getHeader().getRegistry());
		}
		return account;
	}

	/**
	 * Gets the invoice total.
	 * 
	 * @return the invoice total
	 */
	public double getInvoiceTotal() {
		double total = 0.0;
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			total += detail.getTotal();
		}
		return total;
	}

	/**
	 * Gets the invoice total.
	 * 
	 * @return the invoice total
	 */
	public double getFinanceTotal() {
		double total = 0.0;
		Iterator<?> iter = ((List<?>) finances.getWrappedData()).iterator();
		while (iter.hasNext()) {
			Finance finance = (Finance) iter.next();
			total += finance.getAmount();
		}
		return total;
	}

	public boolean isSettled() {
		return (finances.getRowCount() == 0 || getInvoiceTotal() == getFinanceTotal());
	}

	/**
	 * Obtain VA tand surcharge quota.
	 * 
	 * @return the double
	 */
	private Map<Account, Double> obtainTaxQuotasPerAccount(Invoice invoice)
			throws ManagerBeanException {
		Account account;
		if (invoice.getType().equals(InvoiceType.SALES)) {
			account = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.CHARGE_VAT_ACCOUNT);
		} else {
			account = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT);
		}

		double total = 0.0;
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			total += detail.getVatQuota() + detail.getSurchargeQuota();
		}

		Map<Account, Double> taxQuotasPerAccountMap = new HashMap<Account, Double>();
		taxQuotasPerAccountMap.put(account, new Double(total));
		return taxQuotasPerAccountMap;
	}

	/**
	 * Get the bases per account map.
	 * 
	 * @return the double
	 */
	private Map<Account, Double> obtainBasesPerAccount(DataModel details) {
		Map<Account, Double> basesPerAccount = new HashMap<Account, Double>();
		Iterator<?> iterator = ((List<?>) details.getWrappedData()).iterator();
		while (iterator.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iterator.next();
			Account account = detail.getAccount();
			double base = detail.getTaxableBase();
			base += (basesPerAccount.containsKey(account)) ? basesPerAccount.get(account)
					.doubleValue() : 0;
			basesPerAccount.put(account, new Double(base));
		}
		return basesPerAccount;
	}

	/**
	 * Obtain total retention.
	 * 
	 * @return the double
	 */
	private Map<Account, Double> obtainRetentionQuotasPerAccount(Invoice invoice)
			throws ManagerBeanException {
		Account account;
		if (invoice.getType().equals(InvoiceType.SALES)) {
			account = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT);
		} else {
			account = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT);
		}

		double total = 0.0;
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			total += detail.getRetentionQuota();
		}

		Map<Account, Double> retentionQuotasPerAccountMap = new HashMap<Account, Double>();
		retentionQuotasPerAccountMap.put(account, new Double(total));
		return retentionQuotasPerAccountMap;
	}

	public void onRemove(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName(Invoice.class.getName());
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			deleteAccountEntryInvoice(getAccountEntryInvoice());
			deleteFinances(getAccountEntryInvoice().getInvoice());
			deleteInvoiceDetails(getAccountEntryInvoice().getInvoice());
			deleteInvoice(getAccountEntryInvoice().getInvoice());
			deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());
			deleteAccountEntry(getAccountEntryInvoice().getAccountEntry());

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.log(Level.SEVERE, msg, e);
			}
			String msg = "No se pudo borrar la factura. " + e.getMessage();
			LOGGER.log(Level.SEVERE, msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private Invoice insertOrUpdateInvoice(String sessionName) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = isNew() ? new Invoice() : getAccountEntryInvoice().getInvoice();
		invoice = mergeInvoice(invoice);
		if (isNew()) {
			invoice = (Invoice) invoiceBean.insert(invoice);
		} else {
			invoice = (Invoice) HibernateUtil.getSession(sessionName).merge(invoice);
			invoice = (Invoice) invoiceBean.update(invoice);
		}
		// Al convertir el proceso en transaccional, el update
		// de invoice se realiza al momento del session.flush()
		return invoice;
	}

	private Invoice mergeInvoice(Invoice invoice) throws ManagerBeanException {
		invoice.setIssueDate(getHeader().getDate());
		invoice.setTaxDate(getHeader().getTaxDate());
		invoice.setSeries(getHeader().getSeries());
		if (getHeader().getType().equals(InvoiceType.SALES)) {
			if (getHeader().getNumber() == 0) {
				invoice.setNumber(calculateNextNumber(getHeader().getSeries(), getHeader()
						.getType()));
			} else {
				invoice.setNumber(getHeader().getNumber());
			}
			getHeader().setReferenceCode(
					obtainReferenceCode(invoice.getSeries(), invoice.getNumber()));
		}
		invoice.setReferenceCode(getHeader().getReferenceCode());
		invoice.setRegistry(getHeader().getRegistry());
		invoice.setRegistryDocument(getHeader().getDocument());
		invoice.setRegistryName(getHeader().getName());
		invoice.setStatus(InvoiceStatus.SCORED);
		invoice.setType(getHeader().getType());
		invoice.setSecurityLevel(getHeader().getSecurityLevel());
		invoice.setInvestment(getHeader().isInvestment());
		invoice.setTransaction(getHeader().getTransaction());
		invoice.setWithholding(getHeader().isWithholding());
		invoice.setTaxFree(getHeader().isTaxFree());
		invoice.setSurcharge(getHeader().isSurcharge());
		return invoice;
	}

	private String obtainReferenceCode(String series, int number) {
		StringBuilder sb = new StringBuilder();
		if (!StringUtils.isEmpty(series)) {
			sb.append(series);
			sb.append("/");
		}
		sb.append(number);

		return sb.toString();
	}

	private int calculateNextNumber(String series, InvoiceType invoiceType)
			throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES), series);
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE),
				invoiceType);
		Projection projection = Projection.max(invoiceBean
				.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	private void insertInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			InvoiceDetail invoiceDetail = new InvoiceDetail();
			invoiceDetail.setSourceId(null);
			invoiceDetail.setDiscountExpression(new DiscountExpression("0.0"));
			invoiceDetail.setInvoice(invoice);
			invoiceDetail.setItem(null);

			StringBuilder sb = new StringBuilder();
			sb.append("Fra. Nº: ");
			sb.append(invoice.getReferenceCode());
			sb.append(" del ");
			sb.append(getDateFormatter().format(invoice.getIssueDate()));
			invoiceDetail.setDescription(sb.toString());

			invoiceDetail.setSource(InvoiceSource.ACCOUNT);
			invoiceDetail.setWorkPlace(obtainWorkPlace());
			invoiceDetail.setTaxableBase(detail.getTaxableBase());
			invoiceDetail.setPrice(detail.getTaxableBase());
			invoiceDetail.setQuantity(1);
			invoiceDetail = (InvoiceDetail) invoiceDetailBean.insert(invoiceDetail);
			insertInvoiceTaxes(invoiceDetail, detail);
			insertInvoiceAccounts(invoiceDetail, detail);
		}
	}

	private DateFormat getDateFormatter() {
		return new SimpleDateFormat("dd/MM/yyyy");
	}

	private WorkPlace obtainWorkPlace() throws ManagerBeanException {
		IManagerBean workPlaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Iterator<ITransferObject> iter = workPlaceBean.getList(null).iterator();
		if (iter.hasNext()) {
			return (WorkPlace) iter.next();
		}
		return null;
	}

	private void insertInvoiceTaxes(InvoiceDetail invoiceDetail, InvoiceEntryDetail detail)
			throws ManagerBeanException {
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		InvoiceTax invoiceTax = new InvoiceTax();
		if (detail.getVatPercent() > 0) {
			invoiceTax.setInvoiceDetail(invoiceDetail);
			invoiceTax.setPercentage(detail.getVatPercent());
			invoiceTax.setQuota(detail.getVatQuota());
			invoiceTax.setSurcharge(detail.getSurchargePercent());
			invoiceTax.setSurchargeQuota(detail.getSurchargeQuota());
			invoiceTax.setTaxType(TaxType.VAT);
			invoiceTaxBean.insert(invoiceTax);
		}
		if (detail.getRetentionPercent() > 0) {
			invoiceTax = new InvoiceTax();
			invoiceTax.setInvoiceDetail(invoiceDetail);
			invoiceTax.setPercentage(detail.getRetentionPercent());
			invoiceTax.setQuota(detail.getRetentionQuota());
			invoiceTax.setSurcharge(0);
			invoiceTax.setTaxType(TaxType.RETENTION);
			invoiceTaxBean.insert(invoiceTax);
		}
	}

	private void insertInvoiceAccounts(InvoiceDetail invoiceDetail, InvoiceEntryDetail detail)
			throws ManagerBeanException {
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		if (detail.getAccount() != null && !detail.getAccount().equals("")) {
			Account account = detail.getAccount();

			InvoiceDetailAccount invoiceDetailAccount = new InvoiceDetailAccount();
			invoiceDetailAccount.setInvoiceDetail(invoiceDetail);
			invoiceDetailAccount.setAccount(account);
			invoiceAccountBean.insert(invoiceDetailAccount);
		}
	}

	@SuppressWarnings("unchecked")
	private void insertFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Iterator<Finance> iter = ((List<Finance>) finances.getWrappedData()).iterator();
		while (iter.hasNext()) {
			Finance finance = iter.next();
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			if (finance.getId() == null) {
				finance.setFinanceStatus(FinanceStatus.PENDING);
			}
			financeBean.insertOrUpdate(finance);
		}
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException {
		List<Finance> financeList = new LinkedList<Finance>();
		Invoice invoice = isNew() ? new Invoice() : getAccountEntryInvoice().getInvoice();
		invoice = mergeInvoice(invoice);
		financeList = getFinanceGenerator()
				.generateFinances(invoice, getInvoiceTotal(), false);
		setFinances( new ListDataModel(financeList) );
	}

	private void deleteAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean
				.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry
				.getId());
		Iterator<ITransferObject> iter = accountEntryDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			accountEntryDetailBean.remove(iter.next());
		}
	}

	private void deleteAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		accountEntryBean.remove(accountEntry);
	}

	private void deleteInvoice(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		invoiceBean.remove(invoice);
	}

	private void deleteInvoiceDetails(Invoice invoice) throws ManagerBeanException {
		IManagerBean invoiceDetailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
		IManagerBean invoiceAccountBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceDetailBean
				.getFieldName(IFinanceAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iter = invoiceDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) iter.next();
			Criteria taxCriteria = new Criteria();
			taxCriteria.addEqualExpression(invoiceTaxBean
					.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail
					.getId());
			Iterator<ITransferObject> taxIter = invoiceTaxBean.getList(taxCriteria).iterator();
			while (taxIter.hasNext()) {
				invoiceTaxBean.remove(taxIter.next());
			}

			Criteria accountCriteria = new Criteria();
			accountCriteria.addEqualExpression(invoiceAccountBean
					.getFieldName(IAccountBridgeAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID),
					invoiceDetail.getId());
			Iterator<ITransferObject> accountIter = invoiceAccountBean.getList(accountCriteria)
					.iterator();
			while (accountIter.hasNext()) {
				invoiceAccountBean.remove(accountIter.next());
			}
			invoiceDetailBean.remove(invoiceDetail);
		}
	}

	private void deleteFinances(Invoice invoice) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_INVOICE_ID),
				invoice.getId());
		Iterator<ITransferObject> iter = financeBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			financeBean.remove(iter.next());
		}
	}

	private void deleteAccountEntryInvoice(AccountEntryInvoice accountEntryInvoice)
			throws ManagerBeanException {
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		accountEntryInvoiceBean.remove(accountEntryInvoice);
	}

	public void onViewAccountEntry(ActionEvent event) {
		try {
			AccountEntry entry = getAccountEntryInvoice().getAccountEntry();
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IAccountingAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			String m = "Error loading AccountEntryController";
			AonUtil.addErrorMessage(m);
			LOGGER.log(Level.SEVERE, m, e);
		}
	}

	public void registryChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Company company = getCompany();
			if (isSales()) {
				Customer customer = (Customer) event.getNewValue();
				getHeader().setRegistry( customer.getRegistry());
				getHeader().setWithholding(company.isWithholding() && customer.isWithholding());
				getHeader().setSurcharge(customer.isSurcharge());
				getHeader().setTaxFree(customer.isTaxFree());
			} else if (isPurchase()) {
				Supplier supplier = (Supplier) event.getNewValue();
				getHeader().setRegistry( supplier.getRegistry());
				getHeader().setWithholding(supplier.isWithholding());
				getHeader().setSurcharge(company.isSurcharge());
				getHeader().setTaxFree(company.isTaxFree());
			} else if (isExpense()) {
				Creditor creditor = (Creditor) event.getNewValue();
				getHeader().setRegistry(creditor.getRegistry());
				getHeader().setWithholding(creditor.isWithholding());
				getHeader().setSurcharge(company.isSurcharge());
				getHeader().setTaxFree(company.isTaxFree());
			}
			getHeader().setDocument( getHeader().getRegistry().getDocument());
			getHeader().setName(getHeader().getRegistry().getFullName());
			
			setRelatedAccounts( null ); // se inicializa.
			List<SelectItem> list = getRelatedAccounts();
			getHeader().setAccount( null );
			if (list.size() > 0 ) { // Se asigna el primer elemento de la lista de cuentas.
				SelectItem i = list.get(0);
				if (!i.isDisabled()) {
					getHeader().setAccount(  (Account) i.getValue() );	
				}
			}
		} else {
			getHeader().setDocument(null);
			getHeader().setName(null);
			getHeader().setWithholding(false);
			getHeader().setSurcharge(false);
			getHeader().setTaxFree(false);
			getHeader().setAccount( null );
		}
	}

	public boolean isWithHolding() {
		return getHeader().isWithholding();
	}

	public boolean isWithSurcharge() {
		return ((isSales() || isPurchase()) && getHeader().isSurcharge());
	}

	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			currentFinance.setBank(new Bank());
			currentFinance.setBankAccount(new BankAccount());
		}
	}

	public void onBankChanged(LookupChangeEvent event) {
		currentFinance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			Bank bank = (Bank) event.getNewValue();
			currentFinance.getBankAccount().setEntity(bank.getCode());
		}
	}

	public void onRBankChanged(ValueChangeEvent event) {
		currentFinance.setBank(null);
		currentFinance.setBankAccount(new BankAccount());
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			RegistryBank rbank = (RegistryBank) event.getNewValue();
			currentFinance.setBank(rbank.getBank());
			currentFinance.setBankAccount(rbank.getBankAccount());
		}
	}

	public List<SelectItem> getBanks() throws ManagerBeanException {
		if (getCurrentFinance() != null && getCurrentFinance().getPayMethod() != null) {
			PayMethod pm = getCurrentFinance().getPayMethod();
			if ((isSales() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) || (!isSales() && pm.getType() == PayMethodType.BANK_TRANSFER)) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getRegistryBanks(getCurrentFinance().getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getCompanyBanks();
			
		}
		return new LinkedList<SelectItem>();
	}

	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		int number = obtainMaxNumber((String) event.getNewValue());
		SecurityLevel securityLevel = obtainSeriesSecurityLevel((String) event.getNewValue());
		if (getHeader() != null) {
			getHeader().setNumber(number);
			getHeader().setSecurityLevel(securityLevel);
		}
	}

	public void onDateChanged(ActionEvent event) {
		getHeader().setTaxDate(getHeader().getDate());
	}

	@SuppressWarnings("unchecked")
	private SecurityLevel obtainSeriesSecurityLevel(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IConfigAlias.SERIES_ID), seriesId);
		Iterator iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			Series series = (Series) iter.next();
			if (series.getSecurityLevel() != null) {
				return series.getSecurityLevel();
			}
		}
		return null;
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		if (StringUtils.isEmpty(seriesId)) {
			criteria.addNullExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES));
		} else {
			criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_SERIES),
					seriesId);
		}
		criteria.addEqualExpression(invoiceBean.getFieldName(IFinanceAlias.INVOICE_TYPE),
				InvoiceType.SALES);
		Projection projection = Projection.max(invoiceBean
				.getFieldName(IFinanceAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNew(false);
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean
				.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID),
				entry.getId());
		Iterator iter = accountEntryInvoiceBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice) iter.next();
			setAccountEntryInvoice(accountEntryInvoice);
			setHeader( new InvoiceEntryHeader() );
			AccountEntryDetail detail = null;
			if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
				getHeader().setType(InvoiceType.SALES);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "70*");
				getHeader().setAccount((detail != null) ? detail.getAccount() : null);
			}
			if (entry.getType().equals(AccountEntryType.PURCHASE_INVOICE)) {
				getHeader().setType(InvoiceType.PURCHASE);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "60*");
				getHeader().setAccount((detail != null) ? detail.getAccount() : null);
			}
			if (entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)) {
				getHeader().setType(InvoiceType.EXPENSES);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "6*");
				getHeader().setAccount((detail != null) ? detail.getAccount() : null);
			}
			getHeader().setDate(entry.getEntryDate());
			getHeader().setDocument(accountEntryInvoice.getInvoice().getRegistryDocument());
			getHeader().setName(accountEntryInvoice.getInvoice().getRegistryName());
			getHeader().setSeries(accountEntryInvoice.getInvoice().getSeries());
			getHeader().setNumber(accountEntryInvoice.getInvoice().getNumber());
			getHeader().setDate(entry.getEntryDate());
			getHeader().setTaxDate(accountEntryInvoice.getInvoice().getTaxDate());
			getHeader().setReferenceCode(accountEntryInvoice.getInvoice().getReferenceCode());
			getHeader().setPeriod(new Period());
			getHeader().getPeriod().setId(entry.getAccountPeriod());
			getHeader().setSecurityLevel(entry.getSecurityLevel());
			getHeader().setRegistry(accountEntryInvoice.getInvoice().getRegistry());
			getHeader().setWithholding(accountEntryInvoice.getInvoice().isWithholding());
			getHeader().setSurcharge(accountEntryInvoice.getInvoice().isSurcharge());
			getHeader().setTaxFree(accountEntryInvoice.getInvoice().isTaxFree());
			getHeader().setInvestment(accountEntryInvoice.getInvoice().isInvestment());
			getHeader().setTransaction(accountEntryInvoice.getInvoice().getTransaction());
			getHeader().setAccountEntryId(entry.getId());
			setFinances(new ListDataModel(
					obtainFinances(accountEntryInvoice.getInvoice())));
			setDetails(new ListDataModel(
					obtainDetails(accountEntryInvoice.getInvoice())));
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
				detail.setId(invoiceDetail.getId());
				if (invoiceDetail.getSource() != InvoiceSource.ACCOUNT) {
					String msg = "Asiento generado automáticamente. No se puede modificar.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				detail.setTaxableBase(invoiceDetail.getTaxableBase());

				Criteria taxCriteria = new Criteria();
				taxCriteria.addEqualExpression(invoiceTaxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<?> taxIter= invoiceTaxBean.getList(taxCriteria).iterator();
				while(taxIter.hasNext()){
					InvoiceTax invoiceTax = (InvoiceTax)taxIter.next();
					if(invoiceTax.getTaxType().equals(TaxType.VAT)){
						detail.setVatPercent(invoiceTax.getPercentage());
						detail.setVatQuota(invoiceTax.getQuota());
						// el siguiente IF --> para dar soporte a las facturas grabadas antes de la versión 4.0.0 donde no habia cuotas.
						if (invoiceTax.getPercentage() > 0 && invoiceTax.getQuota() == 0) {
							detail.setVatQuota(CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceTax.getPercentage() / 100, 2));
						}
						detail.setSurchargePercent(invoiceTax.getSurcharge());
						detail.setSurchargeQuota(invoiceTax.getSurchargeQuota());
						// el siguiente IF --> para dar soporte a las facturas grabadas antes de la versión 4.0.0 donde no habia cuotas.
						if (invoiceTax.getSurcharge() > 0 && invoiceTax.getSurchargeQuota() == 0) {
							detail.setSurchargeQuota( CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceTax.getSurcharge() / 100, 2));
						}
					} else if(invoiceTax.getTaxType().equals(TaxType.RETENTION)){
						detail.setRetentionPercent(invoiceTax.getPercentage());
						detail.setRetentionQuota(invoiceTax.getQuota());
						// el siguiente IF --> para dar soporte a las facturas grabadas antes de la versión 4.0.0 donde no habia cuotas.
						if (invoiceTax.getPercentage() > 0 && invoiceTax.getQuota() == 0) {
							detail.setVatQuota(CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceTax.getPercentage() / 100, 2));
						}
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

	@Override
	public String getNavigationKey() {
		return "account_invoice_entry";
	}
	
	private List<SelectItem> getAccounts() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			AccountCollectionsController acc = (AccountCollectionsController) AonUtil.getRegisteredBean( ACCOUNT_COLLECTIONS_CONTROLLER_NAME );
			if (isSales()) {
				if (getHeader().getRegistry() != null && getHeader().getRegistry().getId() != null) {
					Account a = getAccountBridgeUtil().getCustomerAccount(getHeader().getRegistry());
					list = getRelatedAccounts(a);
				}
				list = mergeLists(list, acc.getSalesAccounts() );
			} else if (isPurchase()) {
				if (getHeader().getRegistry() != null && getHeader().getRegistry().getId() != null) {
					Account a = getAccountBridgeUtil().getSupplierAccount(getHeader().getRegistry());
					list = getRelatedAccounts(a);
				}
				list = mergeLists(list, acc.getPurchaseAccounts() );
			} else {
				if (getHeader().getRegistry() != null && getHeader().getRegistry().getId() != null) {
					Account a = getAccountBridgeUtil().getCreditorAccount(getHeader().getRegistry());
					list = getRelatedAccounts(a);
				}
				list = mergeLists(list, acc.getExpensesAccounts() );
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error related accounts", e);
		} catch (ExpressionException e) {
			LOGGER.log(Level.SEVERE, "Error related accounts", e);
		}
		return list;
	}
	
	private List<SelectItem> mergeLists(List<SelectItem> related, List<SelectItem> global) {
		for (SelectItem i: global) {
			boolean found = false;
			for (SelectItem x: related) {
				if (x.getValue() != null && x.getValue().equals(i.getValue())) {
					found = true;
					break;
				}
			}
			if (!found) {
				related.add(i);
			}
		}
		return related;
	}

	private List<SelectItem> getRelatedAccounts(Account a) throws ManagerBeanException {
		List<SelectItem> retList = new LinkedList<SelectItem>();
		if (a != null) {
			IManagerBean helperBean = BeanManager.getManagerBean(AccountHelper.class);
			String accountAlias = helperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_ACCOUNT_ID);
			String balAccountAlias = helperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_BALANCING_ACCOUNT_ID);
			String counterAlias = helperBean.getFieldName(IAccountingAlias.ACCOUNT_HELPER_COUNTER);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accountAlias, a.getId());
			String c = "%";
			if (a.getId().startsWith("430")) {
				c = "7%";
			} else if (a.getId().startsWith("400")) {
				c = "6%";
			} else if (a.getId().startsWith("410")) {
				c = "6%";
			}
			Expression exp = ExpressionUtilities.getLikeExpression(balAccountAlias, c);	
			criteria.addExpression(exp);
			criteria.addOrder(counterAlias, false );
			List<ITransferObject> list = helperBean.getList(criteria);
			for (ITransferObject to : list) {
				AccountHelper ah = (AccountHelper) to;
				Account account = ah.getBalancingAccount();
				SelectItem item = new SelectItem(account, account.getFullDescription());
				retList.add(item);
			}
			retList.add(new SelectItem(null,"---------------","---------------",true) );
		}
		return retList;
	}
	
}

