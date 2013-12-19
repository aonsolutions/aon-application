package com.code.aon.ui.finance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.AccountEntryInvoice;
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
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.ITaxInfo;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryDocument;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class BasicExporter {
	
	public static final String NEW_LINE = "\r\n";

	private static final String[] SKIP_ACCOUNTS = new String[] { "477", "472", "473", "4751" };
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

	private ByteArrayOutputStream out;
	
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
	
	private IPriceStrategy priceStrategy;
	
	private double total;
	
	public BasicExporter( InvoiceExportConfiguration configuration ) {
		this.out = new ByteArrayOutputStream();
		this.configuration = configuration;
	}

	public void init( Invoice invoice ) throws ManagerBeanException, IOException {
		initBasic(invoice);
		this.accountEntries = obtainAccountEntries(invoice);
		this.taxBreakDowns = obtainTaxBreakDowns();		
	}
	
	public void init( Finance finance ) throws ManagerBeanException, IOException {
		initBasic(finance);
		this.accountEntries = obtainAccountEntries(finance);
		this.taxBreakDowns = Collections.emptyList();		
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
	
	public double getTotal() {
		return total;
	}
	
	public Finance getFinance() {
		return finance;
	}

	public Set<Finance> getFinances() {
		if ( finance == null ) {
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
	
	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
		}
		return priceStrategy;
	}	
	
	private List<TaxBreakDown> obtainTaxBreakDowns() {
		List<TaxBreakDown> list = getPriceStrategy().getTaxBreakDowns(invoice, invoice);
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
		for( InvoiceDetail id : getInvoice().getLines() ) {
			if ( id.getWorkPlace()!=null && id.getWorkPlace().getId()!=null ) {
				return id.getWorkPlace().getEnterprise();
			}
		}
		return null;
	}
	
	private boolean isRelated( AccountEntryDetail aed, TaxBreakDown tbd ) {
		double amount = (aed.getCredit() != 0) ? aed.getCredit() : aed.getDebit();
		return amount == tbd.getBase();
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
		return detail;
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
	
	protected OutputStream getOutputStream() {
		return this.out;
	}
	
	protected void writeLine() throws IOException {
		out.write(this.line);
	}

	protected void writeNewLine() throws IOException {
		writeNewLine(this.out);
	}
	
	
	protected void writeNewLine( OutputStream out ) throws IOException {
		out.write(NEW_LINE.getBytes());
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
		return out.toByteArray();
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
	
	public void write() throws IOException, ManagerBeanException {
		for( AccountEntry accountEntry : getAccountEntries() ) {
			this.details = obtainDetails(accountEntry);
			this.registryDetail = obtainRegistryDetail(entity);
			write(accountEntry);
		}			
	}
	
	public abstract void write( AccountEntry accountEntry ) throws IOException, ManagerBeanException;
	
	public abstract Map<String,byte[]> getDataMap();
	
	public abstract InvoiceExportType getType();
	
}
