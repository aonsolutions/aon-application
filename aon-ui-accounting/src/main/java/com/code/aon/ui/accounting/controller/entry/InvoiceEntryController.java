package com.code.aon.ui.accounting.controller.entry;

import static com.code.aon.ui.common.ICommonMessages.UNABLE_RECORD_INACCURACY_ERROR_KEY;
import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.AccountEntryDetail.ACCOUNT_ENTRY_DETAIL;

import java.io.Serializable;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.conf.ParamType;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.InvoiceEntryDetail;
import com.code.aon.accounting.InvoiceEntryHeader;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.BankAccount;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IBankAccountContainerProvider;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.account.controller.AccountCollectionsController;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.AccountAppParamsController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.config.BankAccountHelper;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.finance.controller.SaleInvoiceController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

public class InvoiceEntryController implements ISpecialAccountEntry, Serializable, IBankAccountContainerProvider {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceEntryController.class.getName());

	private AccountEntryInvoiceWriter writer;
	private FinanceGenerator financeGenerator;
	private AccountEntryInvoice accountEntryInvoice;
	private boolean isNevv;
	private boolean isNewDetail;
	private boolean isNewFinance;
	private InvoiceEntryHeader header;
	private DataModel details;
	private DataModel finances;
	private InvoiceEntryDetail currentDetail;
	private Finance currentFinance;
	private boolean showBankManualInput;
	private RegistryBank currentBank;
	private Company company;
	private String onGenerateKey;
	private AccountingUtil accountingUtil;
	private AccountBridgeUtil accountBridgeUtil;
	private List<SelectItem> relatedAccounts;
	private List<InvoiceEntryDetail> removedDetails;
	private List<Finance> removedFinances;
	private BankAccountHelper accountHelper;
	
	public InvoiceEntryController() {
    	this.accountHelper = new BankAccountHelper(this);
	}

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

	public boolean isNevv() {
		return isNevv;
	}

	public void setNevv(boolean isNevv) {
		this.isNevv = isNevv;
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
			details = new SerializableListDataModel(new LinkedList<InvoiceEntryDetail>());
		}
		return details;
	}

	public void setDetails(DataModel details) {
		this.details = details;
	}

	public DataModel getFinances() {
		if (finances == null) {
			finances = new SerializableListDataModel(new LinkedList<Finance>());
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

	public boolean isShowBankManualInput() throws ManagerBeanException {
		return showBankManualInput;
	}

	public void setShowBankManualInput(boolean showBankManualInput) {
		this.showBankManualInput = showBankManualInput;
	}

	public RegistryBank getCurrentBank() {
		return currentBank;
	}

	public void setCurrentBank(RegistryBank currentBank) {
		if (currentBank == null) {
			currentBank = new RegistryBank();
			currentBank.setBankAccount(new BankAccount());
		}
		this.currentBank = currentBank;
	}
	private void initializeCurrentBank(Finance currentFinance) {
		try {
			RegistryBank rBank = null;
			if (currentFinance != null) {
				if (StringUtils.isNotEmpty(currentFinance.getBankAccount().getBban())) {
					for (SelectItem selectItem : getAllBanks()) {
						RegistryBank tmpRBank = (RegistryBank)selectItem.getValue();
						if (StringUtils.equals(currentFinance.getBankAccount().getIban(), tmpRBank.getBankAccount().getIban())) {
							rBank = tmpRBank;
							if (StringUtils.equals(currentFinance.getBankAlias(), tmpRBank.getAlias())) {
								break;
							}
						}
					}
				}
			}
			setCurrentBank(rBank);
		} catch (ManagerBeanException e) {
			setCurrentBank(null);
		}
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
			getSaleInvoiceController().initSeries(false);
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void reset() throws ManagerBeanException {
		setNevv(true);
		initializeHeader();
		getHeader().setType(InvoiceType.SALES);

		setDetails(  new SerializableListDataModel(new LinkedList<InvoiceEntryDetail>()));
		setCurrentDetail( resetDetail() );
		setNewDetail(false);
		setFinances( new SerializableListDataModel(new LinkedList<Finance>()) );
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
		VatDeductionType vatDeductionType = (header != null && getHeader().getVatDeductionType() != null) ? getHeader().getVatDeductionType() : VatDeductionType.WITH_RIGHT;
		WithholdingType withholdingType = (header != null && getHeader().getWithholdingType() != null) ? getHeader().getWithholdingType() : WithholdingType.PROFESSIONAL;
		SecurityLevel securityLevel = (header != null && getHeader().getSecurityLevel() != null) ? getHeader().getSecurityLevel() : SecurityLevel.OFFICIAL;
		AccountAppParamsController c = (AccountAppParamsController) AonUtil.getRegisteredBean(IAccountingConstants.ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		ApplicationParameter param = c.getParameter(AppParam.ACC_DEFAULT_INVOICE_SERIES);
		String series = (header != null && !StringUtils.isBlank(getHeader().getSeries())) ? getHeader().getSeries() : (param != null) ? param.getValue() : null;
		ApplicationParameter taxParam = c.getParameter(AppParam.ACC_DEFAULT_VAT_PERCENT);
		Double taxPercent = null;
		Double surPercent = null;
		if (header != null) {
			taxPercent = getHeader().getTaxPercent();
		} else {
			if (taxParam != null) {
				String value = taxParam.getValue();
				try {
					Integer id = Integer.parseInt(value);
					IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
					Tax tax = (Tax) taxBean.get(id);
					if (tax != null) {
						taxPercent = tax.getPercentage();
						surPercent = tax.getSurcharge();
					}
				} catch (NumberFormatException e) {
					// Valor inválido en parámetros.
				}
			}
		}
		ApplicationParameter retParam = c.getParameter(AppParam.ACC_DEFAULT_RETENTION_PERCENT);
		Double retPercent = null;
		if (header != null) {
			retPercent = getHeader().getRetPercent();
		} else {
			if (retParam != null) {
				String value = retParam.getValue();
				try {
					Integer id = Integer.parseInt(value);
					IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
					Tax tax = (Tax) taxBean.get(id);
					if (tax != null) {
						retPercent = tax.getPercentage();						
					} 
				} catch (NumberFormatException e) {
					// Valor inválido en parámetros.
				}
			}
		}

		setHeader( new InvoiceEntryHeader() );
		getHeader().setAccount(null);
		getHeader().setType(type);
		getHeader().setSeries(series);
		seriesChanged(series);
		getHeader().setPeriod(period);
		getHeader().setDate(date);
		getHeader().setTaxDate(taxDate);
		getHeader().setTaxPercent(taxPercent);
		getHeader().setSurchargePercent(surPercent);
		getHeader().setRetPercent(retPercent);
		getHeader().setVatDeductionType(vatDeductionType);
		getHeader().setWithholdingType(withholdingType);
		getHeader().setSecurityLevel(securityLevel);
		getHeader().setRegistry(new Registry());
		getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
		getHeader().setInvestment(false);
		getHeader().setWithholding(false);
		getHeader().setSurcharge(false);
		getHeader().setVatAccrualPayment(false);
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
		if (getHeader().getAccount() != null) {
			if (getHeader().getTaxableBase() != null) {
				try {
					double total = getHeader().getTaxableBase();
					double coef = (1 + (getHeader().getTaxPercent()/100));
					if (getHeader().isSurcharge()) {
						coef = coef + (getHeader().getSurchargePercent()/100);
					}
					if (getHeader().isWithholding()) {
						coef = coef - (getHeader().getRetPercent()/100);	
					}
					double tb = total / coef; 
					getHeader().setTaxableBase(tb);
					onNewDetail(event);
					onTaxableBaseWizard();
					getCurrentDetail().setTaxableBase(CommonUtil.round(total-currentDetail.getVatQuota()-currentDetail.getSurchargeQuota()+currentDetail.getRetentionQuota()));
					onAddDetail(event);
					onCancelDetail(event);
					getHeader().setTaxableBase( 0.0 ); 
				} catch (Exception e) {
					String msg = "No se puede realizar el cálculo. Revise los datos introducidos.";
					LOGGER.warn(msg);
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
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
	
	public void onTaxableBaseWizard(ActionEvent event) {
		if (getHeader().getAccount() != null) {
			if (getHeader().getTaxableBase() != null) {
				onNewDetail(event);
				onTaxableBaseWizard();
				onAddDetail(event);
				onCancelDetail(event);
				getHeader().setTaxableBase( 0.0 ); 
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

	private void onTaxableBaseWizard() {
		getCurrentDetail().setTaxableBase( getHeader().getTaxableBase()==null?0.0:getHeader().getTaxableBase());
		getCurrentDetail().setVatPercent( getHeader().getTaxPercent()==null?0.0:getHeader().getTaxPercent());
		if (getHeader().isSurcharge()) {
			getCurrentDetail().setSurchargePercent( getHeader().getSurchargePercent()==null?0.0:getHeader().getSurchargePercent());
		}
		if (getHeader().isWithholding()) {
			getCurrentDetail().setRetentionPercent( getHeader().getRetPercent()==null?0.0:getHeader().getRetPercent());
		}
		taxableBaseChanged(getHeader().getTaxableBase());
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
			if(p != 0){
				currentDetail.setVatQuota(CommonUtil.round(currentDetail.getTaxableBase() * p / 100, 2));
			}
		}
	}
	
	public void onChangeSurchargePercent(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			double p = (Double) event.getNewValue();
			currentDetail.setSurchargeQuota(0.0);
			if(p != 0){
				currentDetail.setSurchargeQuota(CommonUtil.round(currentDetail.getTaxableBase() * p / 100, 2));
			}
		}
	}
	
	public void onChangeRetentionPercent(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			double p = (Double) event.getNewValue();
			currentDetail.setRetentionQuota(0.0);
			if(p != 0){
				currentDetail.setRetentionQuota(CommonUtil.round(currentDetail.getTaxableBase() * p / 100, 2));
			}
		}
	}

	private InvoiceEntryDetail resetDetail() {
		InvoiceEntryDetail detail =  new InvoiceEntryDetail();
		Account a = (getHeader().getAccount() != null) ? getHeader().getAccount() : new Account();
		detail.setAccount(a);
		detail.setTransaction(getHeader().getTransaction());
		return detail;
	}
	
	
	public void onNewDetail(ActionEvent event) {
		setNewDetail(true);
		setCurrentDetail( resetDetail() );

		AccountAppParamsController c = (AccountAppParamsController) AonUtil
				.getRegisteredBean(IAccountingConstants.ACCOUNT_APP_PARAM_CONTROLLER_NAME);
		try {
			ApplicationParameter param = c.getParameter(AppParam.ACC_DEFAULT_VAT_PERCENT);
			if (param != null) {
				String value = param.getValue();
				Integer id = Integer.parseInt(value);
				IManagerBean taxBean = BeanManager.getManagerBean(Tax.class);
				Tax tax = (Tax) taxBean.get(id);
				if (tax != null) {
					getCurrentDetail().setVatPercent(tax.getPercentage());
					if (getHeader().isSurcharge()) {
						getCurrentDetail().setSurchargePercent(tax.getSurcharge());
					}
				} else {
					LOGGER.warn("NO SE PUEDE ASIGNAR EL PORCENTAJE DE IVA POR DEFECTO. ENCONTRADO [" + value + "]");
				}
			}
		} catch (Exception e) {
			LOGGER.warn("NO SE PUEDE ASIGNAR EL PORCENTAJE DE IVA POR DEFECTO.");
		}

		if (getHeader().isWithholding()) {
			try {
				ApplicationParameter param = c.getParameter(AppParam.ACC_DEFAULT_RETENTION_PERCENT);
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
				LOGGER.warn("NO SE PUEDE ASIGNAR EL PORCENTAJE DE RETENCION POR DEFECTO.");
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
			getCurrentDetail().setTransaction(getHeader().getTransaction());
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
		getCurrentDetail().setTransaction(getHeader().getTransaction());
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
			getCurrentDetail().setTransaction(getHeader().getTransaction());
		}
	}

	private boolean validateDetail(InvoiceEntryDetail detail) {
		if (detail.getAccount() != null && !detail.getAccount().equals("")
				&& detail.getAccount().getId() != null) {
			try {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ID),
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
		if (isSales() && getHeader().getTransaction() == InvoiceTransactionType.INTRACOMMUNITY) {
			if (detail.getVatQuota() != 0) { 
				String msg="A las Ventas intracomunitarias no se les debe aplicar IVA.";
				AonUtil.addInfoMessage(msg);
			}
		} else if (isSales() && getHeader().getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY) {
			if (detail.getVatQuota() != 0) { 
				String msg="A las Ventas extracomunitarias no se les debe aplicar IVA.";
				AonUtil.addInfoMessage(msg);
			}
		}

		return true;
	}

	public void onNewFinance(ActionEvent event) {
		try {
			setNewFinance( true );
			setCurrentFinance( initializeFinance() );
			Invoice invoice = isNevv() ? new Invoice() : getAccountEntryInvoice().getInvoice();
			invoice = mergeInvoice(invoice);
			getCurrentFinance().setInvoice(invoice);
			getFinanceGenerator().initializeFinanceData(getCurrentFinance(), obtainInitialAmount());
			if (getCurrentFinance().getBankAccount() == null) {
				getCurrentFinance().setBankAccount(new BankAccount());
			}
			initializeCurrentBank(getCurrentFinance());
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("Error al inicializar el vencimiento");
		}
	}

	public void onSelectFinance(ActionEvent event) {
		Finance finance = (Finance) finances.getRowData();
		if (finance.isPaid() || finance.isBatched() || finance.isSettled()) {
			String msg = "No se puede modificar un vencimiento no pendiente.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		setCurrentFinance( (Finance) finances.getRowData() );
		initializeCurrentBank(getCurrentFinance());
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
		Iterator<?> financeIter = ((List<?>) getFinances().getWrappedData()).iterator();
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
	public void onInvestmentChanged(ActionEvent event) {
		setRelatedAccounts( null );			
	}

	public String generate() {
		return onGenerateKey;
	}
	
	public void onGenerateNew(ActionEvent event) {
		onGenerate();
		
		onReset(event);
	}
	
	public void onGenerate(ActionEvent event) {
		onGenerate();
		
		onViewAccountEntry(event);
		onGenerateKey = IAccountingConstants.ACCOUNT_ENTRY_FORM_NAVKEY;
	}

	public void onGenerate() {
		double invoiceTotal = getInvoiceTotal();
		double financeTotal = getFinanceTotal();
		if (financeTotal > 0 && invoiceTotal != financeTotal) {
			String msg = AonUtil.addErrorMessageFromBundle(UNABLE_RECORD_INACCURACY_ERROR_KEY);
			throw new AbortProcessingException(msg);
		}
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			if (isNevv) {
				generateInvoiceEntry(sessionName,invoiceTotal);
			} else {
				updateInvoiceEntry(sessionName,invoiceTotal);
			}
			setNevv(false);
			insertOrUpdateInvoice(sessionName); //Grabar los totales de factura.
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			onGenerateKey = null;
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg, e);
			}
			String msg = "No se pudo generar el apunte contable. " + e.getMessage();
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private void updateInvoiceEntry(String sessionName, double invoiceTotal) throws ManagerBeanException {
		deleteInvoiceDetails(getAccountEntryInvoice().getInvoice());
		deleteAccountEntryDetails(getAccountEntryInvoice().getAccountEntry());

		AccountEntry entry = getAccountEntryInvoice().getAccountEntry();
		Account account = fillAccountEntry(entry);

		Invoice invoice = insertOrUpdateInvoice(sessionName);
		insertInvoiceDetails(invoice);
		insertFinances(invoice,sessionName);
		deleteRemovedFinances();
		entry = (AccountEntry) HibernateUtil.getSession(sessionName).merge(entry);
		entry = getWriter().insertOrUpdateAccountEntry(entry);
		String concept = getHeader().getConcept();
		if (StringUtils.isEmpty(concept)) {
			concept = getWriter().obtainConcept(invoice, invoiceTotal);
		} else {
			concept = getWriter().obtainConcept(invoice, invoiceTotal) + " [" + concept;
			concept = StringUtils.abbreviate(concept, 31);
			concept += "]";
		}
		concept = StringUtils.abbreviate(concept, 32);
		getHeader().setConcept(concept);
		getWriter().insertEntryDetails(entry, account, getHeader().getConcept(),
				invoice.getDocumentNumber(), invoiceTotal,
				obtainRetentionQuotasPerAccount(invoice), obtainTaxQuotasPerAccount(invoice),
				obtainBasesPerAccount(details),true);
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
		insertFinances(invoice,sessionName);
		entry = getWriter().insertOrUpdateAccountEntry(entry);
		setAccountEntryInvoice(getWriter().insertAccountEntryInvoice(entry, invoice));
		String concept = getHeader().getConcept();
		if (StringUtils.isEmpty(concept)) {
			concept = getWriter().obtainConcept(invoice, invoiceTotal);
		} else {
			concept = getWriter().obtainConcept(invoice, invoiceTotal) + " [" + concept;
			concept = StringUtils.abbreviate(concept, 31);
			concept += "]";
		}
		getHeader().setConcept(concept);
		getWriter().insertEntryDetails(entry, account, getHeader().getConcept(),
				invoice.getDocumentNumber(),invoiceTotal,
				obtainRetentionQuotasPerAccount(invoice), obtainTaxQuotasPerAccount(invoice),
				obtainBasesPerAccount(details),true);
		getHeader().setAccountEntryId(entry.getId());
	}

	private Account fillAccountEntry(AccountEntry entry) throws ManagerBeanException {
		Account account = null;
		entry.setAccountPeriod(getHeader().getPeriod());
		if (getHeader().getDate() == null) {
			throw new ManagerBeanException("La fecha de la factura no puede estar vacia.");
		}
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
		return CommonUtil.round(total);
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
		return CommonUtil.round(total);
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
		boolean ignoreTaxFree = !invoice.isSales() && (invoice.isIntracommunity() || invoice.isOtherISP());
		Account account;
		Account balancingAccount = null;
		if (invoice.getType().equals(InvoiceType.SALES)) {
			account = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC);
		} else {
			account = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PAID_VAT_ACC);
			if (ignoreTaxFree) {
				balancingAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_CHARGED_VAT_ACC);		
			}
		}

		double total = 0.0;
		Iterator<?> iter = ((List<?>) details.getWrappedData()).iterator();
		while (iter.hasNext()) {
			InvoiceEntryDetail detail = (InvoiceEntryDetail) iter.next();
			total += detail.getVatQuota() + detail.getSurchargeQuota();
		}

		Map<Account, Double> taxQuotasPerAccountMap = new HashMap<Account, Double>();
		taxQuotasPerAccountMap.put(account, new Double(total));
		if (ignoreTaxFree) {
			taxQuotasPerAccountMap.put(balancingAccount, new Double(total * (-1)));	
		}
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
	private Map<Account, Double> obtainRetentionQuotasPerAccount(Invoice invoice) {
		Account account = getHeader().getRetentionAccount();
//		if (invoice.getType().equals(InvoiceType.SALES)) {
//			account = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT);
//		} else {
//			account = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.CHARGED_RETENTION_ACCOUNT);
//		}
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
				LOGGER.error(msg, e);
			}
			String msg = "No se pudo borrar la factura. " + e.getMessage();
			LOGGER.error(msg, e);
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
		Invoice invoice = isNevv() ? new Invoice() : getAccountEntryInvoice().getInvoice();
		invoice = mergeInvoice(invoice);
		if (isNevv()) {
			invoice.setDefaultTaxInfo(false);
			invoice = (Invoice) invoiceBean.insert(invoice);
		} else {
			invoice = (Invoice) HibernateUtil.getSession(sessionName).merge(invoice);
			invoice.setDefaultTaxInfo(false);
			invoice = (Invoice) invoiceBean.update(invoice);
		}
		// Al convertir el proceso en transaccional, el update
		// de invoice se realiza al momento del session.flush()
		return invoice;
	}

	private Invoice mergeInvoice(Invoice invoice) throws ManagerBeanException {
		invoice.setIssueDate(getHeader().getDate());
		invoice.setTaxDate(getHeader().getTaxDate());
		if (isNevv() && getHeader().getType().equals(InvoiceType.SALES)) {
			if ( StringUtils.isBlank(getHeader().getSeries()) ) {
				invoice.setSeries(null);
			} else {
				invoice.setSeries(getHeader().getSeries());				
			}
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
		invoice.setRegistryDocumentCountry(getHeader().getDocumentCountry());
		invoice.setRegistryDocumentType(getHeader().getDocumentType());
		invoice.setRegistryDocument(getHeader().getDocument());
		invoice.setRegistryName(getHeader().getName());
		invoice.setStatus(InvoiceStatus.SCORED);
		invoice.setType(getHeader().getType());
		invoice.setSecurityLevel(getHeader().getSecurityLevel());
		invoice.setInvestment(getHeader().isInvestment());
		invoice.setTransaction(getHeader().getTransaction());
		invoice.setWithholding(getHeader().isWithholding());
		invoice.setVatAccrualPayment(getHeader().isVatAccrualPayment());
		invoice.setSurcharge(getHeader().isSurcharge());
		return invoice;
	}

	private String obtainReferenceCode(String series, int number) {
    	String referenceCode = StringUtils.leftPad(Integer.toString(number), 6, "0");
		if (!StringUtils.isBlank(series)) {
			referenceCode = series + "/" + referenceCode;
		}
		return referenceCode;
	}

	private int calculateNextNumber(String series, InvoiceType invoiceType)
			throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES), series);
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE),
				invoiceType);
		Projection projection = Projection.max(invoiceBean
				.getFieldName(IEntityAlias.INVOICE_NUMBER));
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
			invoiceDetail.setLine(1);
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
		
		invoiceTax.setInvoiceDetail(invoiceDetail);
		invoiceTax.setPercentage(detail.getVatPercent());
		invoiceTax.setQuota(detail.getVatQuota());
		invoiceTax.setSurcharge(detail.getSurchargePercent());
		invoiceTax.setSurchargeQuota(detail.getSurchargeQuota());
		invoiceTax.setTaxType(TaxType.VAT);
		invoiceTax.setVatDeductionType(getHeader().getVatDeductionType());
		invoiceTaxBean.insert(invoiceTax);
		
		if (detail.getRetentionPercent() > 0) {
			invoiceTax = new InvoiceTax();
			invoiceTax.setInvoiceDetail(invoiceDetail);
			invoiceTax.setPercentage(detail.getRetentionPercent());
			invoiceTax.setQuota(detail.getRetentionQuota());
			invoiceTax.setSurcharge(0);
			invoiceTax.setTaxType(TaxType.RETENTION);
			invoiceTax.setWithholdingType(getHeader().getWithholdingType());
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
	private void insertFinances(Invoice invoice, String sessionName) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		Iterator<Finance> iter = ((List<Finance>) finances.getWrappedData()).iterator();
		while (iter.hasNext()) {
			Finance finance = iter.next();
			finance.setInvoice(invoice);
			finance.setRegistry(invoice.getRegistry());
			finance.setRegistryName(invoice.getRegistryName());
			finance.setRegistryDocumentCountry(invoice.getRegistryDocumentCountry());
			finance.setRegistryDocumentType(invoice.getRegistryDocumentType());
			finance.setRegistryDocument(invoice.getRegistryDocument());
			finance.setScope(invoice.getScope());
			if (finance.getId() == null) {
				finance.setFinanceStatus(FinanceStatus.PENDING);
			}
			if (finance.getPayMethod() != null && finance.getPayMethod().getId() == null) {
				finance.setPayMethod(null);
			}
			finance = (Finance) HibernateUtil.getSession(sessionName).merge(finance);
			financeBean.insertOrUpdate(finance);
		}
	}

	public void generateFinances(ActionEvent event) throws ManagerBeanException {
		List<Finance> financeList = new LinkedList<Finance>();
		Invoice invoice = isNevv() ? new Invoice() : getAccountEntryInvoice().getInvoice();
		invoice = mergeInvoice(invoice);
		financeList = getFinanceGenerator().generateFinances(invoice, getInvoiceTotal(), false);
		setFinances( new SerializableListDataModel(financeList) );
	}

	private void deleteAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean
				.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry
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
				.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
		Iterator<ITransferObject> iter = invoiceDetailBean.getList(criteria).iterator();
		while (iter.hasNext()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail) iter.next();
			Criteria taxCriteria = new Criteria();
			taxCriteria.addEqualExpression(invoiceTaxBean
					.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail
					.getId());
			Iterator<ITransferObject> taxIter = invoiceTaxBean.getList(taxCriteria).iterator();
			while (taxIter.hasNext()) {
				invoiceTaxBean.remove(taxIter.next());
			}

			Criteria accountCriteria = new Criteria();
			accountCriteria.addEqualExpression(invoiceAccountBean
					.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID),
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
		criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID),
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
					.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IEntityAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			String m = "Error loading AccountEntryController";
			AonUtil.addErrorMessage(m);
			LOGGER.error(m, e);
		}
	}

	public void registryChanged(LookupChangeEvent event) {
		try {
			if (event.getNewValue() != null && !event.getNewValue().equals("")) {
				Company company = getCompany();
				if (isSales()) {
					Customer customer = (Customer) event.getNewValue();
					getHeader().setRegistry( customer.getRegistry());
					getHeader().setWithholding(company.isWithholding() && customer.isWithholding());
					getHeader().setSurcharge(customer.isSurcharge());
					getHeader().setTransaction( customer.getTransaction() );
					getHeader().setVatAccrualPayment( company.isVatAccrualPayment() );
				} else if (isPurchase()) {
					Supplier supplier = (Supplier) event.getNewValue();
					getHeader().setRegistry( supplier.getRegistry());
					getHeader().setWithholding(supplier.isWithholding());
					getHeader().setSurcharge(company.isSurcharge());
					getHeader().setTransaction( supplier.getTransaction() );
					getHeader().setVatAccrualPayment(supplier.isVatAccrualPayment());
				} else if (isExpense()) {
					Creditor creditor = (Creditor) event.getNewValue();
					getHeader().setRegistry(creditor.getRegistry());
					getHeader().setWithholding(creditor.isWithholding());
					getHeader().setSurcharge(false);
					getHeader().setTransaction( creditor.getTransaction() );
					getHeader().setVatAccrualPayment(creditor.isVatAccrualPayment());
				}
				
				getHeader().setDocumentCountry(getHeader().getRegistry().getDocumentCountry());
				getHeader().setDocumentType(getHeader().getRegistry().getDocumentType());
				getHeader().setDocument(getHeader().getRegistry().getDocument());
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
				if (getHeader().isWithholding()) {
					if (isSales()) {
						getHeader().setRetentionAccount( AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PAID_RET_ACC) );
					} else {
						getHeader().setRetentionAccount( AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_CHARGED_RET_ACC) );
					}
				} else {
					getHeader().setRetentionAccount( null );
				}
			} else {
				getHeader().setDocumentCountry(Country.ES);
				getHeader().setDocumentType(DocumentType.NIF);
				getHeader().setDocument(null);
				getHeader().setName(null);
				getHeader().setWithholding(false);
				getHeader().setVatAccrualPayment(false);
				getHeader().setSurcharge(false);
				getHeader().setTransaction(InvoiceTransactionType.NATIONAL);
				getHeader().setAccount( null );
			}
		} catch (ManagerBeanException e) {
			String m = "Error al inicializar los datos";
			AonUtil.addErrorMessage(m);
			LOGGER.error(m, e);
		}
	}
/*
	public boolean isWithHolding() {
		return getHeader().isWithholding();
	}

	public boolean isWithSurcharge() {
		return ((isSales() || isPurchase()) && getHeader().isSurcharge());
	}
*/
	public void onPayMethodChanged(ValueChangeEvent event) {
		PayMethod oldPay = (PayMethod) event.getOldValue();
		PayMethod newPay = (PayMethod) event.getNewValue();
		if (oldPay == null || newPay == null || oldPay.getType() != newPay.getType()) {
			currentFinance.setBankAccount(new BankAccount());
			currentFinance.setBankAlias(null);
			currentFinance.setBic(null);
		}
	}

	public void onRBankChanged(ValueChangeEvent event) {
		currentFinance.setBankAccount(new BankAccount());
		currentFinance.setBankAlias(null);
		currentFinance.setBic(null);
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			RegistryBank rbank = (RegistryBank) event.getNewValue();
			currentFinance.setBankAccount(rbank.getBankAccount());
			currentFinance.setBankAlias(rbank.getBankAlias());
			currentFinance.setBic(rbank.getBic());
		}
	}

	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		if (getCurrentFinance() != null && getCurrentFinance().getPayMethod() != null) {
			PayMethod pm = getCurrentFinance().getPayMethod();
			if (useRegistryBanks(pm) ) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getAllRegistryBanks(getCurrentFinance().getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getAllCompanyBanks();
			
		}
		return new LinkedList<SelectItem>();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		if (getCurrentFinance() != null && getCurrentFinance().getPayMethod() != null) {
			PayMethod pm = getCurrentFinance().getPayMethod();
			if (useRegistryBanks(pm) ) {
				RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
				return c.getActiveRegistryBanks(getCurrentFinance().getRegistry());
			} 
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getActiveCompanyBanks();
			
		}
		return new LinkedList<SelectItem>();
	}
	public int getActiveBanksCount() throws ManagerBeanException {
		return getActiveBanks().size();
	}
	public int getAllBanksCount() throws ManagerBeanException {
		return getAllBanks().size();
	}
	
	private SaleInvoiceController getSaleInvoiceController() {
		return (SaleInvoiceController) AonUtil.getRegisteredBean(IFinanceConstants.SALE_INVOICE_CONTROLLER_NAME);
	}			
	
	public void onSeriesChanged(ValueChangeEvent event) throws ManagerBeanException {
		if ( getSaleInvoiceController().isNumberEditable() ) {
			String series = (String) event.getNewValue();
			seriesChanged(series);			
		}
	}
	
	public void onInvoiceNumberEditable(ActionEvent event) throws ManagerBeanException {
		seriesChanged(getHeader().getSeries());		
	}			
	
	private void seriesChanged( String series ) throws ManagerBeanException {
		if (getHeader() != null) {
			if ( getSaleInvoiceController().isNumberEditable() ) {
				int number = obtainMaxNumber(series);
				getHeader().setNumber(number);
			}
			SecurityLevel securityLevel = obtainSeriesSecurityLevel(series);
			getHeader().setSecurityLevel(securityLevel);
		}
	}

	public void onDateChanged(ActionEvent event) {
		getHeader().setTaxDate(getHeader().getDate());
	}

	private SecurityLevel obtainSeriesSecurityLevel(String seriesId) throws ManagerBeanException {
		IManagerBean seriesBean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(seriesBean.getFieldName(IEntityAlias.SERIES_CODE), seriesId);
		Iterator<?> iter = seriesBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			Series series = (Series) iter.next();
			if (series.getSecurityLevel() != null) {
				return series.getSecurityLevel();
			}
		}
		return SecurityLevel.OFFICIAL;
	}

	private int obtainMaxNumber(String seriesId) throws ManagerBeanException {
		IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
		Criteria criteria = new Criteria();
		if (StringUtils.isEmpty(seriesId)) {
			criteria.addNullExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES));
		} else {
			criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_SERIES),
					seriesId);
		}
		criteria.addEqualExpression(invoiceBean.getFieldName(IEntityAlias.INVOICE_TYPE),
				InvoiceType.SALES);
		Projection projection = Projection.max(invoiceBean
				.getFieldName(IEntityAlias.INVOICE_NUMBER));
		Object value = invoiceBean.getUniqueResult(projection, criteria);
		if (value != null) {
			return ((Integer) value).intValue() + 1;
		}
		return 1;
	}

	@Override
	public void loadEntry(AccountEntry entry) throws ManagerBeanException {
		onReset(null);
		setNevv(false);
		IManagerBean accountEntryInvoiceBean = BeanManager
				.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryInvoiceBean
				.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_ACCOUNT_ENTRY_ID),
				entry.getId());
		Iterator<?> iter = accountEntryInvoiceBean.getList(criteria).iterator();
		if (iter.hasNext()) {
			AccountEntryInvoice accountEntryInvoice = (AccountEntryInvoice) iter.next();
			setAccountEntryInvoice(accountEntryInvoice);
			setHeader( new InvoiceEntryHeader() );
			AccountEntryDetail detail = null;
			if (entry.getType().equals(AccountEntryType.SALES_INVOICE)) {
				getHeader().setType(InvoiceType.SALES);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "70*");
				getHeader().setConcept( StringUtils.substringBetween(((detail != null)?detail.getConcept():null),"[","]") );
				getHeader().setAccount((detail != null) ? detail.getAccount() : null);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "473*");
				getHeader().setRetentionAccount((detail != null) ? detail.getAccount() : null);
			}
			if (entry.getType().equals(AccountEntryType.PURCHASE_INVOICE)) {
				getHeader().setType(InvoiceType.PURCHASE);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "60*");
				getHeader().setConcept( StringUtils.substringBetween(((detail != null)?detail.getConcept():null),"[","]") );
				getHeader().setAccount((detail != null) ? detail.getAccount() : null);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "475*");
				getHeader().setRetentionAccount((detail != null) ? detail.getAccount() : null);
			}
			if (entry.getType().equals(AccountEntryType.EXPENSE_INVOICE)) {
				getHeader().setType(InvoiceType.EXPENSES);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "6*");
				getHeader().setConcept( StringUtils.substringBetween(((detail != null)?detail.getConcept():null),"[","]") );
				getHeader().setAccount((detail != null) ? detail.getAccount() : null);
				detail = getAccountingUtil().getEntryDetailFromAccountPattern(entry, "475*");
				getHeader().setRetentionAccount((detail != null) ? detail.getAccount() : null);
			}
			
			getHeader().setDate(accountEntryInvoice.getInvoice().getIssueDate());
			getHeader().setDocumentCountry(accountEntryInvoice.getInvoice().getRegistryDocumentCountry());
			getHeader().setDocumentType(accountEntryInvoice.getInvoice().getRegistryDocumentType());
			getHeader().setDocument(accountEntryInvoice.getInvoice().getRegistryDocument());
			getHeader().setName(accountEntryInvoice.getInvoice().getRegistryName());
			getHeader().setSeries(accountEntryInvoice.getInvoice().getSeries());
			getHeader().setNumber(accountEntryInvoice.getInvoice().getNumber());
			getHeader().setTaxDate(accountEntryInvoice.getInvoice().getTaxDate());
			getHeader().setReferenceCode(accountEntryInvoice.getInvoice().getReferenceCode());
			getHeader().setPeriod(entry.getAccountPeriod());
			getHeader().setSecurityLevel(entry.getSecurityLevel());
			getHeader().setRegistry(accountEntryInvoice.getInvoice().getRegistry());
			getHeader().setWithholding(accountEntryInvoice.getInvoice().isWithholding());
			getHeader().setVatAccrualPayment(accountEntryInvoice.getInvoice().isVatAccrualPayment());
			getHeader().setSurcharge(accountEntryInvoice.getInvoice().isSurcharge());
			getHeader().setInvestment(accountEntryInvoice.getInvoice().isInvestment());
			getHeader().setTransaction(accountEntryInvoice.getInvoice().getTransaction());
			getHeader().setAccountEntryId(entry.getId());
			setFinances(new SerializableListDataModel(
					obtainFinances(accountEntryInvoice.getInvoice())));
			setDetails(new SerializableListDataModel(
					obtainDetails(accountEntryInvoice.getInvoice())));
		}
	}

	private List<?> obtainFinances(Invoice invoice) {
		List<Finance> finances = new LinkedList<Finance>();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_INVOICE_ID), invoice.getId());
			Iterator<?> iter = financeBean.getList(criteria).iterator();
			while(iter.hasNext()){
				finances.add((Finance)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining finances for invoice with id=" + invoice.getId(), e);
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
			criteria.addEqualExpression(invoiceDetailBean.getFieldName(IEntityAlias.INVOICE_DETAIL_INVOICE_ID), invoice.getId());
			Iterator<?> iter = invoiceDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				InvoiceEntryDetail detail = new InvoiceEntryDetail();
				detail.setTransaction(getHeader().getTransaction());
				InvoiceDetail invoiceDetail = (InvoiceDetail)iter.next();
				detail.setId(invoiceDetail.getId());
				if (invoiceDetail.getSource() != InvoiceSource.ACCOUNT) {
					String msg = "Asiento generado automáticamente. No se puede modificar.";
					AonUtil.addErrorMessage(msg);
					throw new AbortProcessingException(msg);
				}
				detail.setTaxableBase(invoiceDetail.getTaxableBase());

				Criteria taxCriteria = new Criteria();
				taxCriteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), invoiceDetail.getId());
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
						getHeader().setVatDeductionType(invoiceTax.getVatDeductionType());
					} else if(invoiceTax.getTaxType().equals(TaxType.RETENTION)){
						detail.setRetentionPercent(invoiceTax.getPercentage());
						detail.setRetentionQuota(invoiceTax.getQuota());
						// el siguiente IF --> para dar soporte a las facturas grabadas antes de la versión 4.0.0 donde no habia cuotas.
						if (invoiceTax.getPercentage() > 0 && invoiceTax.getQuota() == 0) {
							detail.setVatQuota(CommonUtil.round(invoiceDetail.getTaxableBase() * invoiceTax.getPercentage() / 100, 2));
						}
						getHeader().setWithholdingType(invoiceTax.getWithholdingType());
					}
				}

				Criteria accountCriteria = new Criteria();
				accountCriteria.addEqualExpression(invoiceAccountBean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_ID), invoiceDetail.getId());
				Iterator<?> accountIter= invoiceAccountBean.getList(accountCriteria).iterator();
				if(accountIter.hasNext()){
					InvoiceDetailAccount invoiceDetailAccount = (InvoiceDetailAccount)accountIter.next();
					detail.setAccount(invoiceDetailAccount.getAccount());
				}

				details.add(detail);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error loading details for invoice with id=" + invoice.getId(), e);
		}
		return details;
	}

	@Override
	public String getNavigationKey() {
		return "invoiceEntry_form";
	}
	
	private List<SelectItem> getAccounts() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		try {
			AccountCollectionsController acc = (AccountCollectionsController) AonUtil.getRegisteredBean( IAccountingConstants.ACCOUNT_COLLECTIONS_CONTROLLER_NAME );
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
			if (getHeader().isInvestment()) {
				list = mergeLists(list, acc.getFixedAssetAccountsExtended() );
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error related accounts", e);
		} catch (ExpressionException e) {
			LOGGER.error("Error related accounts", e);
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
			Connection connection = null; 
			try {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
				DSLContext ctx = DSL.using(connection, AccountingUtil.getDefaultSettings());
				AggregateFunction<Integer> countFunc = DSL.countDistinct(ACCOUNT_ENTRY_DETAIL.ID);
				String c = "%";
				if (a.getCode().startsWith("430")) {
					c = "7%";
				} else if (a.getCode().startsWith("400")) {
					c = "6%";
				} else if (a.getCode().startsWith("410")) {
					c = "6%";
				}
				Result<Record2<Integer,Integer>> record = 
					ctx.select(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT,countFunc)
						.from(ACCOUNT_ENTRY_DETAIL)
						.innerJoin(ACCOUNT).on(ACCOUNT.ID.eq(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT))
						.where(ACCOUNT_ENTRY_DETAIL.DOMAIN.equal(DomainManager.getCurrentDomain()))
						.and(ACCOUNT_ENTRY_DETAIL.ACCOUNT.eq(a.getId()))
						.and(ACCOUNT.CODE.like(c))
						.groupBy(ACCOUNT_ENTRY_DETAIL.BALANCING_ACCOUNT)
						.orderBy(countFunc.desc())
						.limit(1)
						.fetch();
				for (Record2<Integer,Integer> step : record) {
					Integer id = step.value1();
					Account account = (Account) accountBean.get(id);
					SelectItem item = new SelectItem(account, account.getFullDescription());
					retList.add(item);
				}
				retList.add(new SelectItem(null,"---------------","---------------",true) );
			} catch (AonConnectionException e) {
				throw new ManagerBeanException(e.getMessage(), e);
			} finally {
				DatabaseUtil.closeQuietly(connection);
			}
		}
		return retList;
	}
//			
//			
//			
//			
//			
//			IManagerBean helperBean = BeanManager.getManagerBean(AccountHelper.class);
//			String accountAlias = helperBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_CODE);
//			String balAccountAlias = helperBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT_CODE);
//			String counterAlias = helperBean.getFieldName(IEntityAlias.ACCOUNT_HELPER_COUNTER);
//			Criteria criteria = new Criteria();
//			criteria.addEqualExpression(accountAlias, a.getCode());
//			String c = "%";
//			if (a.getCode().startsWith("430")) {
//				c = "7%";
//			} else if (a.getCode().startsWith("400")) {
//				c = "6%";
//			} else if (a.getCode().startsWith("410")) {
//				c = "6%";
//			}
//			Expression exp = ExpressionUtilities.getLikeExpression(balAccountAlias, c);	
//			criteria.addExpression(exp);
//			criteria.addOrder(counterAlias, false );
//			List<ITransferObject> list = helperBean.getList(criteria);
//			for (ITransferObject to : list) {
//				AccountHelper ah = (AccountHelper) to;
//				Account account = ah.getBalancingAccount();
//				SelectItem item = new SelectItem(account, account.getFullDescription());
//				retList.add(item);
//			}
//			retList.add(new SelectItem(null,"---------------","---------------",true) );
//		}
//		return retList;
//	}

	private boolean useRegistryBanks(PayMethod pm) {
		return ((isSales() && pm.getType() == PayMethodType.NEGOTIABLE_DOCUMENT) || (!isSales() && pm.getType() == PayMethodType.BANK_TRANSFER));	
	}

	@Override
	public IBankAccountContainer getBankAccountContainer() {
		return getCurrentFinance();
	}

	public BankAccountHelper getAccountHelper() {
		return accountHelper;
	}
	
	public boolean isTaxDateEquals() {
		if (getHeader() != null) {
			return ObjectUtils.equals(getHeader().getDate(), getHeader().getTaxDate());
		}
		return true;
	}
	
}

