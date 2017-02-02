package com.code.aon.ui.finance;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.writer.pricing.AccountInvoicePriceStrategy;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.util.FinanceUtil;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class BasicExporter implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(BasicExporter.class);
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String OUTPUT_ENCODING = "ISO-8859-1";
	
	public static final String TXT_SUFFIX = ".txt";
	
	public static final String NEW_LINE = "\r\n";
	
	private static final String RETENTION_PREFFIX_1 = "473";
	
	private static final String RETENTION_PREFFIX_2 = "4751";

	private static final String[] SKIP_ACCOUNTS = new String[] { "477", "472", RETENTION_PREFFIX_1, RETENTION_PREFFIX_2 };
	
	private static final String[] RETENTION_ACCOUNTS = new String[] { RETENTION_PREFFIX_1, RETENTION_PREFFIX_2 };
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private byte[] data;
	
	private InvoiceExportConfiguration configuration;
	
	private byte[] line;
	
	private Invoice invoice;
	
	private Finance finance;
	
	private List<AccountEntry> accountEntries;
	
	private List<TaxBreakDown> taxBreakDowns;
	
	private AccountEntryDetail registryDetail;
	
	private List<AccountEntryDetail> details;
	
	private String referenceCode;
	
	private String documentNumber;
	
	private Date date;
	
	private Date dueDate;
	
	private Date taxDate;
	
	private Integer mainId;
	
	private Registry registry;
	
	private RegistryAddress registryAddress;
	
	private IAccount entity;
	
	private RegistryDocument registryDocument;
	
	private String registryName;
	
	private InvoiceType invoiceType;
	
	private InvoiceTransactionType transaction;
	
	private RectificationType rectificationType; 
	
	private boolean investment;
	
	private AccountInvoicePriceStrategy priceStrategy;
	
	private double total;
	
	private boolean withholding;
	
	private boolean withholdingFarmer;	
	
	private boolean invoiceExport;
	
	private boolean ticket;
	
	private boolean renting;
	
	private String invoiceSeries;
	
	private Integer invoiceNumber;
	
	public BasicExporter( InvoiceExportConfiguration configuration ) {
		this.configuration = configuration;
	}

	public void init( Invoice invoice ) throws ManagerBeanException, IOException {
		initBasic(invoice);
		this.accountEntries = obtainAccountEntries(invoice);
		this.taxBreakDowns = obtainTaxBreakDowns(invoice, invoice);		
		this.invoiceExport = true;
	}
	
	public void init( Finance finance ) throws ManagerBeanException, IOException {
		initBasic(finance);
		this.accountEntries = obtainAccountEntries(finance);
		this.taxBreakDowns = Collections.emptyList();
		this.invoiceExport = false;
	}	

	public void init( FinanceBatch fBatch ) throws ManagerBeanException, IOException {
		initBasic(fBatch);
		this.accountEntries = obtainAccountEntries(fBatch);
		this.taxBreakDowns = Collections.emptyList();
		this.invoiceExport = false;
	}	
	
	private Date getDueDate( Invoice invoice ) {
		Date date = invoice.getDate();
		for( Finance finance : invoice.getFinances() ) {
			if ( finance.getDueDate() != null && finance.getDueDate().compareTo(date) > 0 ) {
				date = finance.getDueDate();
			}
		}
		return date;
	}
		
	private RegistryDocument getRegistryDocument( Invoice invoice ) {
		RegistryDocument rd = null;
		if (! StringUtils.isBlank(invoice.getRegistryDocument()) ) {
			rd = new RegistryDocument();
			rd.setDocument(getInvoice().getRegistryDocument());
			rd.setType(getInvoice().getRegistryDocumentType());
			rd.setCountry(getInvoice().getRegistryDocumentCountry());
		} else if (! StringUtils.isBlank(getInvoice().getRegistry().getDocument()) ) {
			rd = getInvoice().getRegistry().getRegistryDocument(); 
		}
		return rd; 
	}	
	
	private boolean isSupplier( Registry registry ) throws ManagerBeanException {
		IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
		return supplierBean.get(registry.getId()) != null;		
	}
	
	private double getInvoiceTotalPrice(Invoice invoice) {
		if (InvoiceType.UNDEDUCTIBLE == invoice.getType()) {
			return getPriceStrategy().getTaxableBase(invoice);
		}
		return getPriceStrategy().getTotalPrice(invoice, invoice);
	}	
	
	private void initBasic( Invoice invoice ) throws ManagerBeanException {
		this.invoice = invoice;
		this.mainId = invoice.getId();
		this.referenceCode = invoice.getReferenceCode();
		this.documentNumber = invoice.getDocumentNumber();
		this.date = invoice.getDate();
		this.dueDate = getDueDate(invoice);
		this.taxDate = invoice.getTaxDate();
		this.registry = invoice.getRegistry();
		this.registryAddress = invoice.getRegistryAddress();
		if ( this.registryAddress == null ) {
			this.registryAddress = this.registry.getDefaultAddress();
		}
		this.invoiceType = invoice.getType();
		this.entity = getEntity(registry);
		this.registryDocument = getRegistryDocument(invoice);
		this.registryName = invoice.getRegistryName();
		this.transaction = invoice.getTransaction();
		this.investment = invoice.isInvestment();
		this.rectificationType = invoice.getRectificationType();
		this.total = getInvoiceTotalPrice(invoice);
		this.withholding = invoice.isWithholding();
		this.withholdingFarmer = invoice.isWithholdingFarmer();
		this.ticket = (invoice.getPosShift() != null) && (invoice.getPosShift().getId() != null);
		this.renting = calculateRenting();
		this.invoiceSeries = invoice.getSeries();
		this.invoiceNumber = invoice.getNumber();
	}
	
	private void initBasic( Finance finance ) throws ManagerBeanException {
		this.finance = finance;
		if ( finance.getInvoice() != null ) {
			initBasic( finance.getInvoice() );
		} else {
			this.mainId = finance.getId();
		}
		if (! StringUtils.isBlank(this.referenceCode) ) {
			this.referenceCode = finance.getDocumentNumber();
		}
		if ( this.date == null ) {
			this.date = finance.getDueDate();	
		}
		if ( finance.getDueDate() != null ) {
			this.dueDate = finance.getDueDate();
		}
		if ( this.taxDate == null ) {
			this.taxDate = this.date;
		}
		if (! StringUtils.isBlank(finance.getRegistryDocument()) ) {
			this.registryDocument = finance.getRegistryFullDocument();
		}
		if (! StringUtils.isBlank(finance.getRegistryName()) ) {
			this.registryName = finance.getRegistryName();
		}
		if ( finance.getRegistry() != null ) {
			this.registry = finance.getRegistry();
		}
		if ( finance.isPayment() ) {
			this.invoiceType = isSupplier(this.registry) ? InvoiceType.PURCHASE : InvoiceType.EXPENSES;
		} else {
			this.invoiceType = InvoiceType.SALES;
		}
		if ( this.entity == null ) {
			this.entity = getEntity(this.registry);
		}
		if ( transaction == null ) { 
			this.transaction = ((ITaxInfo) this.entity).getTransaction();
		}
		if ( this.rectificationType == null ) {
			this.rectificationType = RectificationType.NONE;
		}
		this.documentNumber = finance.getConcept();
		this.total = finance.getTotalAmount();
		this.investment = false;
	}

	private void initBasic( FinanceBatch fBatch ) throws ManagerBeanException {
		this.mainId = fBatch.getId();
		this.referenceCode = String.valueOf(this.mainId);
		this.date = fBatch.getIssueDate();	
		this.dueDate = this.date;
		this.taxDate = this.date;
		if ( fBatch.isPayment() ) {
			this.invoiceType = InvoiceType.PURCHASE;
		} else {
			this.invoiceType = InvoiceType.SALES;
		}
		this.transaction = InvoiceTransactionType.NATIONAL;
		this.investment = false;
	}
	
	public Integer getMainId() {
		return mainId;
	}

	public String getReferenceCode() {
		return referenceCode;
	}
	
	public String getDocumentNumber() {
		return documentNumber;
	}

	public Date getDate() {
		return date;
	}
	
	public Date getDueDate() {
		return dueDate;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}

	public Registry getRegistry() {
		return registry;
	}

	public RegistryAddress getRegistryAddress() {
		return registryAddress;
	}

	public RegistryDocument getRegistryDocument() {
		return registryDocument;
	}

	public String getRegistryName() {
		return registryName;
	}
	
	public boolean isSales() {
		return this.invoiceType == InvoiceType.SALES;
	}
	
	public boolean isWithholding() {
		return withholding;
	}

	public boolean isWithholdingFarmer() {
		return withholdingFarmer;
	}

	public InvoiceType getInvoiceType() {
		return this.invoiceType;
	}
	
	public InvoiceTransactionType getTransaction() {
		return this.transaction;
	}
	
	public RectificationType getRectificationType() {
		return rectificationType;
	}

	public boolean isInvestment() {
		return this.investment;
	}	
	
	public void setTotal(double total) {
		this.total = total;
	}

	public double getTotal() {
		return total;
	}
	
	public Finance getFinance() {
		return finance;
	}
	
	public boolean isRectifier() {
		return (getRectificationType() == RectificationType.NORMAL_RECTIFIER) ||
				(getRectificationType() == RectificationType.SPECIAL_RECTIFIER);
	}
	
	public boolean isTicket() {
		return ticket;
	}
	
	public boolean isRenting() {
		return renting;
	}

	public String getInvoiceSeries() {
		return invoiceSeries;
	}

	public Integer getInvoiceNumber() {
		return invoiceNumber;
	}

	private boolean calculateRenting() throws ManagerBeanException {
		boolean renting = false;
		IManagerBean bean = BeanManager.getManagerBean(InvoiceTax.class);
		for( InvoiceDetail id : getInvoice().getLines() ) {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), id.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_TAX_WITHHOLDING_TYPE), WithholdingType.RENTING);
			if ( bean.getCount(criteria) > 0 ) {
				renting = true;
				break;
			}
		}
		return renting;
	}

	public Set<Finance> getFinances() {
		if ( isInvoiceExport() ) {
			return invoice.getFinances();
		}
		return Collections.emptySet();
	}

	private List<AccountEntry> obtainAccountEntries( Invoice invoice ) throws ManagerBeanException {
		List<AccountEntry> entries = new LinkedList<AccountEntry>();
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID), invoice.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			for( ITransferObject to : list ) {
				AccountEntryInvoice aei = (AccountEntryInvoice) to;
				entries.add(aei.getAccountEntry());
			}
		}
		return entries;
	}

	private List<AccountEntry> obtainAccountEntries( Finance finance ) throws ManagerBeanException {
		List<AccountEntry> entries = new LinkedList<AccountEntry>();
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID);
		criteria.addEqualExpression(alias, finance.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			for( ITransferObject to : list ) {
				AccountEntryFinanceTracking aeft = (AccountEntryFinanceTracking) to;
				entries.add(aeft.getAccountEntry());
			}
		}
		return entries;
	}

	private List<AccountEntry> obtainAccountEntries( FinanceBatch fBatch ) throws ManagerBeanException {
		List<AccountEntry> entries = new LinkedList<AccountEntry>();
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_BATCH_FINANCE_BATCH_ID);
		criteria.addEqualExpression(alias, fBatch.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			for( ITransferObject to : list ) {
				AccountEntryFinanceBatch aeft = (AccountEntryFinanceBatch) to;
				entries.add(aeft.getAccountEntry());
			}
		}
		return entries;
	}
	
	protected VatDeductionType getVatDeductionType() {
		for( TaxBreakDown tbd : taxBreakDowns ) {
			if ( tbd.getVatDeductionType() != VatDeductionType.WITH_RIGHT ) {
				return tbd.getVatDeductionType();
			}
		}
		return VatDeductionType.WITH_RIGHT;		
	}
	
	protected boolean isSkipAccount( Account account ) {
		for( String preffix : SKIP_ACCOUNTS ) {
			if ( StringUtils.startsWith(account.getCode(), preffix) ) {
				return true;
			}
		}
		return false;
	}	
	
	protected boolean isRetention( Account account ) {
		for( String preffix : RETENTION_ACCOUNTS ) {
			if ( StringUtils.startsWith(account.getCode(), preffix) ) {
				return true;
			}
		}
		return false;		
	}
	
	private List<AccountEntryDetail> obtainDetails( AccountEntry accountEntry ) {
		List<AccountEntryDetail> list = new LinkedList<AccountEntryDetail>();
		for( AccountEntryDetail aed : accountEntry.getDetail() ) {
			if (! isSkipAccount(aed.getAccount()) ) {
				list.add(aed);
			}
		}
		Comparator<AccountEntryDetail> comparator = new Comparator<AccountEntryDetail>() {

			@Override
			public int compare(AccountEntryDetail o1, AccountEntryDetail o2) {
				return o1.getAccount().getCode().compareTo(o2.getAccount().getCode());
			}
			
		};
		Collections.sort( list, comparator );
		return list;
	}
	
	public AccountInvoicePriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new AccountInvoicePriceStrategy();
		}
		return priceStrategy;
	}	
	
	private List<TaxBreakDown> obtainTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		List<TaxBreakDown> list = new LinkedList<TaxBreakDown>();
		for( TaxBreakDown tbd : getPriceStrategy().getTaxBreakDowns(icc, iti) ) {
			if ( tbd.getBase() != 0 ) {
				list.add(tbd);
			}
		}		
		Comparator<TaxBreakDown> comparator = new Comparator<TaxBreakDown>() {

			@Override
			public int compare(TaxBreakDown o1, TaxBreakDown o2) {
				Integer i1 = o1.getTaxType().ordinal();
				return i1.compareTo(o2.getTaxType().ordinal());
			}
			
		};
		Collections.sort( list, comparator );
		return list;
	}
	
	protected Enterprise getEnterprise() {
		if (! getInvoice().getLines().isEmpty() ) {
			InvoiceDetail id = getInvoice().getLines().iterator().next();
			return FinanceUtil.getEnterprise(id);
		}
		return null;
	}
	
	private boolean isRelated( AccountEntryDetail aed, TaxBreakDown tbd ) {
		double amount = (aed.getCredit() != 0) ? aed.getCredit() : aed.getDebit();
		return amount == tbd.getBase();
	}
	
	private List<InvoiceDetail> getInvoiceDetail( AccountEntryDetail aed ) throws ManagerBeanException {
		List<InvoiceDetail> details = new LinkedList<>();
		IManagerBean bean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
		Criteria criteria = new Criteria();
		String accountId = bean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_ACCOUNT_ID);
		criteria.addEqualExpression(accountId, aed.getAccount().getId());
		String invoiceId = bean.getFieldName(IEntityAlias.INVOICE_DETAIL_ACCOUNT_INVOICE_DETAIL_INVOICE_ID); 
		criteria.addEqualExpression(invoiceId, getInvoice().getId()); 
		List<ITransferObject> list = bean.getList(criteria);
		if ( list != null ) {
			for( ITransferObject to : list ) {
				InvoiceDetailAccount ida = (InvoiceDetailAccount) to;
				if (! details.contains(ida.getInvoiceDetail()) ) {
					details.add( ida.getInvoiceDetail() );	
				}
			}
		}
		return details;
	}
	
	protected List<TaxBreakDown> getInvoiceTotalTaxes() throws ManagerBeanException {
		return obtainTaxBreakDowns(invoice, invoice);
	}
	
	protected List<TaxBreakDown> getInvoiceTaxes( AccountEntryDetail aed ) throws ManagerBeanException {
		List<InvoiceDetail> details = getInvoiceDetail(aed);
		if (! details.isEmpty() ) {
			InvoiceCalculableContainer icc = new InvoiceCalculableContainer(invoice, details);
			InvoiceTaxInfo iti = new InvoiceTaxInfo(invoice);
			return obtainTaxBreakDowns(icc, iti);
		}
		return Collections.emptyList();
	}
	
	protected List<TaxBreakDown> getTaxes( AccountEntryDetail aed ) {
		List<TaxBreakDown> list = new LinkedList<TaxBreakDown>();
		for( TaxBreakDown tbd : getTaxBreakDowns() ) {
			if ( isRelated(aed, tbd) ) {
				list.add(tbd);
			}
		}
		getTaxBreakDowns().removeAll(list);
		return list;
	}
	
	protected TaxBreakDown getRelatedTax( List<TaxBreakDown> taxList, TaxBreakDown tax ) {
		TaxBreakDown relatedTax = null;
		if (! taxList.isEmpty() ) {
			for( TaxBreakDown tbd : taxList ) {
				if ( tbd.getBase()==tax.getBase() &&
						tbd.getTaxType()!=tax.getTaxType() ) {
					relatedTax = tbd;
					break;
				}
			}
		}
		if ( relatedTax != null ) {
			taxList.remove(relatedTax);
		}		
		return relatedTax;
	}	
	
	protected TaxBreakDown getNextTax( List<TaxBreakDown> taxList ) {
		TaxBreakDown tax = null;
		for( TaxBreakDown tbd : taxList ) {
			if ( tbd.getTaxType() == TaxType.VAT ) {
				tax = tbd;
			}
		}
		if ( tax == null ) {
			tax = taxList.get(0);
		}
		taxList.remove(tax);
		return tax;
	}		
		
	private IAccount getEntity( Registry registry ) throws ManagerBeanException {
		IAccount entity = null;
		switch ( getInvoiceType() ) {
			case SALES:
				IManagerBean customerBean = BeanManager.getManagerBean(Customer.class);
				entity = (Customer) customerBean.get(registry.getId());
				break;
			case PURCHASE:
				IManagerBean supplierBean = BeanManager.getManagerBean(Supplier.class);
				entity = (Supplier) supplierBean.get(registry.getId());
				break;
			default:
				IManagerBean creditorBean = BeanManager.getManagerBean(Creditor.class);
				entity = (Creditor) creditorBean.get(registry.getId());
		}
		return entity;
	}
	
	protected AccountEntryDetail obtainRegistryDetail(IAccount entity) throws ManagerBeanException {
		AccountEntryDetail detail = null;
		if ( entity != null ) {
			Account registryAccount = entity.getAccount();
			if ( registryAccount != null ) {
				for( AccountEntryDetail aed : getDetails() ) {
					if ( aed.getAccount() == registryAccount ) {
						detail = aed;
						break;
					}
				}
			}
			if ( detail == null ) {
				detail = getDetails().get(0);
			}
			getDetails().remove(detail);			
		}
		return detail;
	}
	
	protected RegistryBank getRegistryBank() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_BANK_REGISTRY_ID), getRegistry().getId());
		List<ITransferObject> list = bean.getList(criteria);
		if ( !list.isEmpty() ) {
			return (RegistryBank) list.get(0); 
		}
		return null;		
	}	
	
	protected void setString( String value, int offset, int maxLength ) {
		if (! StringUtils.isEmpty(value) ) {
			String _value = StringUtils.substring(value, 0, maxLength);
			for( int i = 0; i < _value.length(); i++ ) {
				this.line[offset+i] = (byte) _value.charAt(i);
			}			
		}
	}

	protected void setStringLeftPad( String value, int offset, int maxLength ) {
		String _value = StringUtils.leftPad(value, maxLength);
		setString(_value, offset, maxLength);
	}

	protected void setStringRightPad( String value, int offset, int maxLength ) {
		String _value = StringUtils.rightPad(value, maxLength);
		setString(_value, offset, maxLength);
	}	
	
	protected void setDate( Date date, int offset ) {
		setString( DATE_FORMAT.format(date), offset, 8);
	}	

	public byte[] getLine() {
		return line;
	}

	protected void setLine(byte[] line) {
		this.line = line;
	}
	
	protected void writeLine() throws IOException {
		data = ArrayUtils.addAll(data, this.line);
	}

	protected void writeNewLine() throws IOException {
		data = ArrayUtils.addAll(data, NEW_LINE.getBytes());
	}
	
	public InvoiceExportConfiguration getConfiguration() {
		return configuration;
	}

	protected Invoice getInvoice() {
		return invoice;
	}

	private List<AccountEntry> getAccountEntries() {
		return accountEntries;
	}

	protected List<TaxBreakDown> getTaxBreakDowns() {
		return taxBreakDowns;
	}

	protected List<AccountEntryDetail> getDetails() {
		return details;
	}

	protected AccountEntryDetail getRegistryDetail() {
		return registryDetail;
	}

	protected AccountEntryDetail getNextDetail() {
		return getDetails().remove(0);
	}
	
	protected byte[] getData() {
		return data;
	}
	
	public String getFileName() {
		return null;
	}
	
	protected Integer getJournal( AccountEntry accountEntry ) {
		if ( accountEntry.getJournal() != null ) {
			return accountEntry.getJournal();
		}
		return accountEntry.getId();
	}
	
	protected boolean isInvoiceExport() {
		return this.invoiceExport;
	}

	protected String getAccountCode( String code ) {
		String cuenta = StringUtils.substring(code, 0, 4);
		String subCuenta = StringUtils.substring(code, 4);
		int length = StringUtils.length(subCuenta);
		if ( length > 5 ) {
			subCuenta = StringUtils.substring(subCuenta, length-5, length);	
		}
		subCuenta = StringUtils.leftPad(subCuenta, getConfiguration().getAccountSize()-4, '0');
		return cuenta + subCuenta;
	}	
	
	public void write() throws IOException, ManagerBeanException {
		for( AccountEntry accountEntry : getAccountEntries() ) {
			this.details = obtainDetails(accountEntry);
			this.registryDetail = obtainRegistryDetail(entity);
			write(accountEntry);
		}			
	}

	public boolean hasAccountData() {
		return ! accountEntries.isEmpty();
	}
		
	protected void addData( Map<String, File> map, String prefix, String suffix, byte[] data) {
		if (! ArrayUtils.isEmpty(data) ) {
			try {
				File file = File.createTempFile(prefix, suffix);
				FileUtils.writeByteArrayToFile(file, data);
				map.put(prefix+suffix, file);			
			} catch ( IOException e ) {
				LOGGER.error(e.getMessage(), e);
			}			
		}
	}
	
	protected Date getLastFinanceTrackingPaidDate( Finance finance ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		String id = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID);
		criteria.addEqualExpression(id, finance.getId());
		String type = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE);
		criteria.addEqualExpression(type, FinanceTrackingType.PAID);
		String date = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_TRACKING_DATE);
		criteria.addOrder(date, false);
		List<ITransferObject> list = bean.getList(criteria, 0, 1);
		if (! list.isEmpty() ) {
			return ((FinanceTracking) list.get(0)).getTrackingDate();
		}
		return null;
	}

	public abstract void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException;
	
	public abstract Map<String,File> getDataMap();
	
	public abstract InvoiceExportType getType();
	
}
