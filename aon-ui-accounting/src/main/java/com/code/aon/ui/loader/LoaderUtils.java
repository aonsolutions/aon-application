package com.code.aon.ui.loader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.AccountEntryInvoice;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.InvoiceDetailAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.DefaultAccounts;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.geozone.GeoZone;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.code.aon.ui.loader.pojo.LoadedCreditor;
import com.code.aon.ui.loader.pojo.LoadedCustomer;
import com.code.aon.ui.loader.pojo.LoadedFinance;
import com.code.aon.ui.loader.pojo.LoadedInvoice;
import com.code.aon.ui.loader.pojo.LoadedInvoiceDetail;
import com.code.aon.ui.loader.pojo.LoadedRegistry;
import com.code.aon.ui.loader.pojo.LoadedSupplier;
import com.esferalia.aon.entity.IEntityAlias;

public class LoaderUtils {
	
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm:ss");
	
	private Account outputVatAccount;
	private Account inputVatAccount;
	private Account retentionAccount;
	
	private LoaderParams parameters;
	private AccountingUtil accountingUtil;
	
	public LoaderParams getParams() {
		return parameters;
	}
	public AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil(); 
		}
		return accountingUtil;
	}
	public LoaderUtils(LoaderParams params) {
		this.parameters = params;
	}
	
	public void insertRegistryMedia(Registry registry, MediaType type, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		RegistryMedia rmedia = new RegistryMedia();
		rmedia.setRegistry(registry);
		rmedia.setMediaType(type);
		rmedia.setValue(value);
		bean.insert(rmedia);		
	}
	
	public RegistryBank insertRegistryBank(Registry registry,LoadedRegistry loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		RegistryBank rbank = new RegistryBank();
		rbank.setRegistry(registry);
		BankAccount bankAccount = new BankAccount();
		String[] ccc = StringUtils.split(loaded.getCuentaBanco(),".");
		if (ccc.length == 4) {
			throw new ManagerBeanException("El CCC del cliente "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
		}
		bankAccount.setEntity(ccc[0]);
		bankAccount.setOffice(ccc[1]);
		bankAccount.setControl(ccc[2]);
		bankAccount.setAccount(ccc[3]);
		if (!bankAccount.isValid()) {
//			throw new ManagerBeanException("El CCC del cliente "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
		} else {
			Bank bank = ensureBank(ccc[0], loaded.getBanco() );
			rbank.setBank(bank);
			rbank.setBankAccount(bankAccount);
			return (RegistryBank) bean.insert(rbank);		
		}
		return null;
	}
	
	public Bank ensureBank(String bankCode, String banco) throws ManagerBeanException {
		IManagerBean bankBean = BeanManager.getManagerBean(Bank.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bankBean.getFieldName( IEntityAlias.BANK_CODE) , bankCode);
		List<ITransferObject> list = bankBean.getList(c);
		if (list != null && list.size() > 0) {
			return (Bank) list.get(0);	
		} 
		if (StringUtils.isNotBlank(banco)) {
			Bank bank = new Bank();
			bank.setCode(bankCode);
			bank.setName(banco);
			return (Bank) bankBean.insert(bank);
		}
		return null;
	}

	public void insertRegistryPayMethod(Registry registry,RegistryBank rbank,LoadedRegistry loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryPayMethod.class);
		RegistryPayMethod rPayMethod = new RegistryPayMethod();
		rPayMethod.setRegistry(registry);
		rPayMethod.setRegistryBank(rbank);
		rPayMethod.setNumberOfPayments(loaded.getNumeroVtos());
		rPayMethod.setDaysBetweenPayments(loaded.getDiasEntreVtos());
		rPayMethod.setDaysToFirstPayment(loaded.getDiasAlPrimerVto());
		if (StringUtils.isNotBlank(loaded.getDiasPago())) {
			rPayMethod.setPaymentDays(loaded.getDiasPago());	
		}
		if (StringUtils.isNotBlank(loaded.getFormaPago())) {
			PayMethod payMethod = ensurePayMethod(loaded.getFormaPago());
			rPayMethod.setPayment(payMethod);
		}
		bean.insert(rPayMethod);
	}
	
	public PayMethod ensurePayMethod(String formaPago) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.PAY_METHOD_NAME) , formaPago);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (PayMethod) list.get(0);	
		} 
		PayMethod payMethod = new PayMethod();
		payMethod.setName(formaPago);
		payMethod.setType(PayMethodType.OTHER);
		return (PayMethod) bean.insert(payMethod);
	}

	public void insertRegistryCustomerAccount(Customer customer, LoadedCustomer loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CustomerAccount.class);
		CustomerAccount ca = new CustomerAccount();
		Account account = ensureAccount(loaded.getCuenta(), customer.getRegistry().getName());
		ca.setCustomer(customer);
		ca.setAccount(account);
		bean.insert(ca);
	}
	
	private void insertRegistrySupplierAccount(Supplier supplier, LoadedSupplier loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(SupplierAccount.class);
		SupplierAccount ca = new SupplierAccount();
		Account account = ensureAccount(loaded.getCuenta(), supplier.getRegistry().getName());
		ca.setSupplier(supplier);
		ca.setAccount(account);
		bean.insert(ca);
	}

	private void insertRegistryCreditorAccount(Creditor creditor, LoadedCreditor loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CreditorAccount.class);
		CreditorAccount ca = new CreditorAccount();
		Account account = ensureAccount(loaded.getCuenta(), creditor.getRegistry().getName());
		ca.setCreditor(creditor);
		ca.setAccount(account);
		bean.insert(ca);
	}

	public Account ensureAccount(String accountCode, String description) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.ACCOUNT_CODE) , accountCode);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (Account) list.get(0);	
		} 
		if (StringUtils.isNotBlank(description)) {
			Account account = new Account();
			account.setCode(accountCode);
			account.setDescription(description);
			return (Account) bean.insert(account);
		}
		return null;
	}

	public void insertRegistryAddress(Registry registry, LoadedRegistry loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		RegistryAddress address = new RegistryAddress();
		address.setRegistry(registry);
		address.setAddress(loaded.getDireccion());
		address.setNumber(loaded.getNumero());
		address.setAddress2(loaded.getDireccion2());
		address.setAddress3(loaded.getDireccion3());
		address.setZip(loaded.getCp());
		address.setCity(loaded.getCiudad());
		GeoZone geozone = ensureGeoZone(loaded.getPais(),loaded.getProvincia(),loaded.getNombreProvincia() );
		address.setGeozone(geozone);
		bean.insert(address);		
	}

	public GeoZone ensureGeoZone(String pais, String provincia, String nombreProvincia) throws ManagerBeanException {
		IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
		Criteria c = new Criteria();
		c.addEqualExpression(geozoneBean.getFieldName( IEntityAlias.GEO_ZONE_CODE) , provincia);
		List<ITransferObject> list = geozoneBean.getList(c);
		if (list != null && list.size() > 0) {
			return (GeoZone) list.get(0);	
		}
		return null;
	}
	
	public Registry populateRegistry(LoadedRegistry loaded) {
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getNationality());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getParams().getSecurityLevel());
		return registry;
	}
	
	public Integer insertCustomer(LoadedCustomer loaded) throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		Customer customer  = new Customer ();
		
		customer.setRegistry(populateRegistry(loaded));
		customer.setScope(getParams().getScope());
		customer.setTransaction(loaded.getInvoiceTransactionType());
		customer.setSurcharge(loaded.isSurcharge());
		customer.setWithholding(loaded.isWithholding());
		customer.setDeliveryGrouped(loaded.isDeliveryGrouped());
		customer.setStatus(CustomerStatus.ACTIVE);
		customer = (Customer) bean.insert(customer);
		
		
		if (StringUtils.isNotBlank(loaded.getTipoVia())
			|| StringUtils.isNotBlank(loaded.getDireccion())
			|| StringUtils.isNotBlank(loaded.getNumero())
			|| StringUtils.isNotBlank(loaded.getDireccion2())
			|| StringUtils.isNotBlank(loaded.getDireccion3())
			|| StringUtils.isNotBlank(loaded.getCp())
			|| StringUtils.isNotBlank(loaded.getCiudad())
			|| StringUtils.isNotBlank(loaded.getProvincia())
			|| StringUtils.isNotBlank(loaded.getNombreProvincia())
			|| StringUtils.isNotBlank(loaded.getPais())) {
			insertRegistryAddress(customer.getRegistry(),loaded);	
		}
		
		if (StringUtils.isNotBlank(loaded.getTelefono1())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		}
		if (StringUtils.isNotBlank(loaded.getTelefono2())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
		}
		if (StringUtils.isNotBlank(loaded.getFax())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.FAX,loaded.getFax());
		}
		if (StringUtils.isNotBlank(loaded.getEmail())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.EMAIL,loaded.getEmail());
		}
		if (StringUtils.isNotBlank(loaded.getWeb())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.WEB,loaded.getWeb());
		}
		RegistryBank rbank = null;
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			rbank = insertRegistryBank(customer.getRegistry(),loaded);	
		}
		if (rbank != null || StringUtils.isNotBlank(loaded.getFormaPago())) {
			insertRegistryPayMethod(customer.getRegistry(),rbank,loaded);
		}
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			insertRegistryCustomerAccount( customer, loaded);
		}
		return customer.getId();
	}

	public Integer insertCreditor(LoadedCreditor loaded) throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Creditor creditor  = new Creditor();
		
		creditor.setRegistry(populateRegistry(loaded));
		creditor.setScope(getParams().getScope());
		creditor.setTransaction(loaded.getInvoiceTransactionType());
		creditor.setWithholding(loaded.isWithholding());
		creditor.setStatus(CreditorStatus.ACTIVE);
		creditor = (Creditor) bean.insert(creditor);
		
		
		if (StringUtils.isNotBlank(loaded.getTipoVia())
			|| StringUtils.isNotBlank(loaded.getDireccion())
			|| StringUtils.isNotBlank(loaded.getNumero())
			|| StringUtils.isNotBlank(loaded.getDireccion2())
			|| StringUtils.isNotBlank(loaded.getDireccion3())
			|| StringUtils.isNotBlank(loaded.getCp())
			|| StringUtils.isNotBlank(loaded.getCiudad())
			|| StringUtils.isNotBlank(loaded.getProvincia())
			|| StringUtils.isNotBlank(loaded.getNombreProvincia())
			|| StringUtils.isNotBlank(loaded.getPais())) {
			insertRegistryAddress(creditor.getRegistry(),loaded);	
		}
		
		if (StringUtils.isNotBlank(loaded.getTelefono1())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		}
		if (StringUtils.isNotBlank(loaded.getTelefono2())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
		}
		if (StringUtils.isNotBlank(loaded.getFax())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.FAX,loaded.getFax());
		}
		if (StringUtils.isNotBlank(loaded.getEmail())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.EMAIL,loaded.getEmail());
		}
		if (StringUtils.isNotBlank(loaded.getWeb())) {
			insertRegistryMedia(creditor.getRegistry(),MediaType.WEB,loaded.getWeb());
		}
		RegistryBank rbank = null;
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			rbank = insertRegistryBank(creditor.getRegistry(),loaded);	
		}
		if (rbank != null || StringUtils.isNotBlank(loaded.getFormaPago())) {
			insertRegistryPayMethod(creditor.getRegistry(),rbank,loaded);
		}
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			insertRegistryCreditorAccount( creditor, loaded);
		}
		return creditor.getId();
	}

	public Integer insertSupplier(LoadedSupplier loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Supplier supplier  = new Supplier ();
		
		supplier.setRegistry(populateRegistry(loaded));
		supplier.setScope(getParams().getScope());
		supplier.setTransaction(loaded.getInvoiceTransactionType());
		supplier.setWithholding(loaded.isWithholding());
		supplier.setStatus(SupplierStatus.ACTIVE);
		supplier = (Supplier) bean.insert(supplier);
		
		if (StringUtils.isNotBlank(loaded.getTipoVia())
			|| StringUtils.isNotBlank(loaded.getDireccion())
			|| StringUtils.isNotBlank(loaded.getNumero())
			|| StringUtils.isNotBlank(loaded.getDireccion2())
			|| StringUtils.isNotBlank(loaded.getDireccion3())
			|| StringUtils.isNotBlank(loaded.getCp())
			|| StringUtils.isNotBlank(loaded.getCiudad())
			|| StringUtils.isNotBlank(loaded.getProvincia())
			|| StringUtils.isNotBlank(loaded.getNombreProvincia())
			|| StringUtils.isNotBlank(loaded.getPais())) {
			insertRegistryAddress(supplier.getRegistry(),loaded);	
		}
		
		if (StringUtils.isNotBlank(loaded.getTelefono1())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		}
		if (StringUtils.isNotBlank(loaded.getTelefono2())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
		}
		if (StringUtils.isNotBlank(loaded.getFax())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.FAX,loaded.getFax());
		}
		if (StringUtils.isNotBlank(loaded.getEmail())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.EMAIL,loaded.getEmail());
		}
		if (StringUtils.isNotBlank(loaded.getWeb())) {
			insertRegistryMedia(supplier.getRegistry(),MediaType.WEB,loaded.getWeb());
		}
		RegistryBank rbank = null;
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			rbank = insertRegistryBank(supplier.getRegistry(),loaded);	
		}
		if (rbank != null && StringUtils.isNotBlank(loaded.getFormaPago())) {
			insertRegistryPayMethod(supplier.getRegistry(),rbank,loaded);
		}
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			insertRegistrySupplierAccount( supplier, loaded);
		}
		return supplier.getId();
	}
	
	private Registry getRegistry(Class<? extends ITransferObject> clazz, String alias, String cuenta) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(clazz);
		Criteria c = new Criteria();
		c.addEqualExpression(alias, cuenta);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			IAccount account = (IAccount) list.get(0);
			IRegistry ir = (IRegistry) account.getLinkedTo();
			return ir.getRegistry();
		}
		return null;
	}

	public Integer insertFinance(ILoaderIdCache cache,LoadedFinance loaded) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		Finance finance  = new Finance();
		Registry registry = null;
		if (loaded.getFactura() != null) {
			Invoice invoice = (Invoice) cache.getAonEntity("FRA", loaded.getFactura().toString());
			if (invoice  == null) {
				throw new AonException("La factura con identiicador " + loaded.getFactura() + " no existe.");
			}
			finance.setInvoice(invoice);
			registry = invoice.getRegistry();
			if (StringUtils.isBlank(loaded.getConcepto())) {
				loaded.setConcepto(invoice.getDocumentNumber());
			}
		} else {
			String cuenta = loaded.getCuenta();
			if (StringUtils.startsWith(cuenta, "400")) {
				registry = getRegistry(SupplierAccount.class,"SupplierAccount.account.code",loaded.getCuenta());
			}
			if (StringUtils.startsWith(cuenta, "410")) {
				registry = getRegistry(CreditorAccount.class,"CreditorAccount.account.code",loaded.getCuenta());
			}
			if (StringUtils.startsWith(cuenta, "430")) {
				registry = getRegistry(CustomerAccount.class,"CustomerAccount.account.code",loaded.getCuenta());
			}
			if (registry == null) {
				throw new ManagerBeanException("Registry es nulo!");
			}
		}
		finance.setRegistry(registry);
		finance.setScope(getParams().getScope());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		if (loaded.getTipoDocumento() == null) {
			finance.setRegistryDocumentType(finance.getRegistry().getDocumentType());
		} else {
			finance.setRegistryDocumentType(loaded.getDocumentType());
		}
		if (StringUtils.isBlank(loaded.getPaisDocumento())) {
			finance.setRegistryDocumentCountry(finance.getRegistry().getDocumentCountry());
		} else {
			finance.setRegistryDocumentCountry(loaded.getDocumentCountry());
		}
		if (StringUtils.isBlank(loaded.getDocumento())) {
			finance.setRegistryDocument(finance.getRegistry().getDocument());
		} else {
			finance.setRegistryDocument(loaded.getDocumento());
		}
		if (StringUtils.isBlank(loaded.getDocumento())) {
			finance.setRegistryName(finance.getRegistry().getName());
		} else {
			finance.setRegistryName(loaded.getRazonSocial());
		}
		finance.setAmount(loaded.getImporte());
		finance.setConcept(loaded.getConcepto());
		if (StringUtils.isNotBlank(loaded.getFormaPago())) {
			finance.setPayMethod(ensurePayMethod(loaded.getFormaPago()));	
		}
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			BankAccount bankAccount = new BankAccount();
			String[] ccc = StringUtils.split(loaded.getCuentaBanco(),".");
			if (ccc.length != 4) {
				throw new ManagerBeanException("El CCC del cliente "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
			}
			bankAccount.setEntity(ccc[0]);
			bankAccount.setOffice(ccc[1]);
			bankAccount.setControl(ccc[2]);
			bankAccount.setAccount(ccc[3]);
			if (!bankAccount.isValid()) {
//				throw new ManagerBeanException("El CCC del vto "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
			} else {
				finance.setBankAccount(bankAccount);
				Bank bank = ensureBank(ccc[0],null );
				finance.setBank(bank);
			}
		}
		finance.setPayment(loaded.getTipo()==1);
		finance = (Finance) bean.insert(finance);
		return finance.getId();
	}
	
	public Integer insertInvoice(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = new Invoice();
		InvoiceType type = InvoiceType.values()[loaded.getTipo()];
		if (type == InvoiceType.SALES ) {
			Series series = ensureInvoiceSeries( loaded.getSerie() ); 
			invoice.setSeries( series.getCode() );
			invoice.setNumber( loaded.getNumero() );
		} else {
			invoice.setReferenceCode(StringUtils.abbreviate(loaded.getReferencia(),16));
		}
		invoice.setType(type);
		Registry registry = obtainRegistry( type , loaded);
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(loaded.getDocumento());
		invoice.setRegistryDocumentType(DocumentType.values()[loaded.getTipoDocumento()]);
		invoice.setRegistryDocumentCountry(Country.valueOf( loaded.getPaisDocumento()));
		invoice.setRegistryName(loaded.getRazonSocial());
		invoice.setIssueDate(loaded.getFechaFactura());
		invoice.setTaxDate(loaded.getFechaIva());
		invoice.setInvestment( loaded.isInvestment() );
		invoice.setTransaction(loaded.getInvoiceTransactionType());
		invoice.setComments(loaded.getComentario());
		Date now = new Date();
		invoice.setRemarks("Importada de fichero " + FORMATTER.format(now) + " - " + TIME_FORMATTER.format(now));
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice.setTaxableBase(loaded.getBaseImponible());
		invoice.setVatQuota(loaded.getTotalCuotaIVA());
		invoice.setRetentionQuota(loaded.getTotalCuotaIRPF());
		invoice.setTotal(loaded.getTotalFactura());
		invoice.setUpdateEnabled(false);
		invoice = (Invoice) bean.insert(invoice);
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			invoice.setStatus(InvoiceStatus.SCORED);
		}
		invoice = (Invoice) bean.insert(invoice);
		if (invoice.getStatus() == InvoiceStatus.SCORED) {
			insertInvoiceAccountEntry(invoice, loaded);
		} 
		return invoice.getId();
	}

	private void insertInvoiceAccountEntry(Invoice invoice,LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AccountEntry.class);
		IManagerBean invoiceBean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		IManagerBean detailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod( getParams().getAccountPeriod() );
		entry.setEntryDate(loaded.getFechaFactura());
		InvoiceType type = InvoiceType.values()[loaded.getTipo()];
		if (type == InvoiceType.SALES) {
			entry.setType(AccountEntryType.SALES_INVOICE);	
		} else if (type == InvoiceType.PURCHASE) {
			entry.setType(AccountEntryType.PURCHASE_INVOICE);
		} else if (type == InvoiceType.EXPENSES) {
			entry.setType(AccountEntryType.EXPENSE_INVOICE);
		} else if (type == InvoiceType.UNDEDUCTIBLE) {
			entry.setType(AccountEntryType.EXPENSE_INVOICE);
		} else {
			entry.setType(AccountEntryType.MANUAL);
		}
		entry.setSecurityLevel(getParams().getSecurityLevel());
		entry = (AccountEntry) bean.insert(entry);
		
		AccountEntryInvoice aei = new AccountEntryInvoice();
		aei.setAccountEntry(entry);
		aei.setInvoice(invoice);
		invoiceBean.insert(aei);

		// Primer Apunte (Cliente, Proveedor o Acreedor)
		AccountEntryDetail aed = new AccountEntryDetail();
		aed .setAccountEntry(entry);
		Account account = ensureAccount(loaded.getCuenta(), loaded.getRazonSocial());
		aed.setAccount(account);
		aed.setDocumentNumber(invoice.getDocumentNumber());
		String prefix = (invoice.getType() == InvoiceType.SALES) ? "N/Fra" : "S/Fra";
		if (loaded.getTotalFactura() < 0) {
			prefix += " " + "ABONO";
		}
		prefix += ": ";
		aed.setConcept( StringUtils.abbreviate(prefix + invoice.getReferenceCode(), 32) );
		if (entry.getType() == AccountEntryType.SALES_INVOICE) {
			aed.setDebit(loaded.getTotalFactura());
		} else {
			aed.setCredit(loaded.getTotalFactura());
		}
		detailBean.insert(aed);
		
	}

	private Series ensureInvoiceSeries(String serie) throws ManagerBeanException {
		if (StringUtils.isNotEmpty(serie)) {
			IManagerBean bean = BeanManager.getManagerBean(Series.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_CODE), serie);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_SCOPE_ID), getParams().getScope().getId());
			List<ITransferObject> list = bean.getList(criteria);
			if ( list.size() > 0 ) {
				Series series = (Series) list.get(0);
				return series;
			}
			Series series = new Series();
			series.setCode(serie);
			series.setDescription(serie);
			series.setInvoice(true);
			series.setActive(true);
			series.setScope( getParams().getScope() );
			series.setSecurityLevel( getParams().getSecurityLevel() );
			series = (Series) bean.insert(series);
			return series;
		}
		return null;
	}
	
	private Registry obtainRegistry(InvoiceType type, LoadedInvoice loaded) throws ManagerBeanException {
		IRegistry r = null;
		if ( type == InvoiceType.SALES ) {
			if (StringUtils.isNotBlank(loaded.getCuenta())) {
				Registry registry = getRegistry(CustomerAccount.class,"CustomerAccount.account.code",loaded.getCuenta());
				if (registry != null) return registry;
			}
			r = obtainCustomer( loaded );		
		} else if ( type == InvoiceType.PURCHASE ) {
			if (StringUtils.isNotBlank(loaded.getCuenta())) {
				Registry registry = getRegistry(SupplierAccount.class,"SupplierAccount.account.code",loaded.getCuenta());	
				if (registry != null) return registry;
			}
			r = obtainSupplier( loaded );
		} else if ( type == InvoiceType.EXPENSES || type == InvoiceType.UNDEDUCTIBLE) {
			if (StringUtils.isNotBlank(loaded.getCuenta())) {
				Registry registry = getRegistry(CreditorAccount.class,"CreditorAccount.account.code",loaded.getCuenta());	
				if (registry != null) return registry;
			}
			r = obtainCreditor( loaded );
		}
		return r.getRegistry();
	}
	
	private Customer obtainCustomer(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID), getParams().getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un cliente activo con el número de documento " + loaded.getDocumento());
			}
			Customer customer = (Customer) list.get(0);  
			if (!StringUtils.isBlank(loaded.getCuenta())) {
				IManagerBean caBean = BeanManager.getManagerBean(CustomerAccount.class);
				String alias = caBean.getFieldName( IEntityAlias.CUSTOMER_ACCOUNT_CUSTOMER_ID );
				Integer id = customer.getId();
				String msg = "cliente";
				CustomerAccount emptyIAccount = new CustomerAccount();
				checkIAccount(loaded,caBean,alias,customer,id,msg,emptyIAccount);
			}
			return customer; 
		}
		Customer customer = new Customer();
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getDocumentCountry());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getParams().getSecurityLevel());
		customer.setRegistry(registry);
		customer.setScope(getParams().getScope());
		customer.setTransaction(loaded.getInvoiceTransactionType());
		customer = (Customer) bean.insert(customer);
		if (!StringUtils.isBlank(loaded.getCuenta())) {
			IManagerBean caBean = BeanManager.getManagerBean(CustomerAccount.class);
			Account account = ensureAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			CustomerAccount ca = new CustomerAccount();
			ca.setAccount(account);
			ca.setCustomer(customer);
			caBean.insert(ca);					
		}
		return customer; 
	}
	
	private Creditor obtainCreditor(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_STATUS), CreditorStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_SCOPE_ID), getParams().getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un acreedor activo con el número de documento " + loaded.getDocumento());
			}
			Creditor creditor = (Creditor) list.get(0); 
			if (!StringUtils.isBlank(loaded.getCuenta())) {
				IManagerBean caBean = BeanManager.getManagerBean(CreditorAccount.class);
				String alias = caBean.getFieldName( IEntityAlias.CREDITOR_ACCOUNT_CREDITOR_ID );
				Integer id = creditor.getId();
				String msg = "acreedor";
				CreditorAccount emptyIAccount = new CreditorAccount();
				checkIAccount(loaded,caBean,alias,creditor,id,msg,emptyIAccount);
			}
			return creditor;
		}
		Creditor creditor = new Creditor();
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getDocumentCountry());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getParams().getSecurityLevel());
		creditor.setRegistry(registry);
		creditor.setScope(getParams().getScope());
		creditor.setTransaction(loaded.getInvoiceTransactionType());
		bean.insert(creditor);
		if (!StringUtils.isBlank(loaded.getCuenta())) {
			IManagerBean caBean = BeanManager.getManagerBean(CreditorAccount.class);
			Account account = ensureAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			CreditorAccount ca = new CreditorAccount();
			ca.setAccount(account);
			ca.setCreditor(creditor);
			caBean.insert(ca);					
		}
		return creditor;
	}

	private Supplier obtainSupplier(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_STATUS), SupplierStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_SCOPE_ID), getParams().getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un proveedor activo con el número de documento " + loaded.getDocumento());
			}
			Supplier supplier = (Supplier) list.get(0); 
			if (!StringUtils.isBlank(loaded.getCuenta())) {
				IManagerBean caBean = BeanManager.getManagerBean(SupplierAccount.class);
				String alias = caBean.getFieldName( IEntityAlias.SUPPLIER_ACCOUNT_SUPPLIER_ID );
				Integer id = supplier.getId();
				String msg = "proveedor";
				SupplierAccount emptyIAccount = new SupplierAccount();
				checkIAccount(loaded,caBean,alias,supplier,id,msg,emptyIAccount);
			}
			return supplier;
		}
		Supplier supplier = new Supplier();
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getDocumentCountry());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getParams().getSecurityLevel());
		supplier.setRegistry(registry);
		supplier.setScope(getParams().getScope());
		supplier.setTransaction(loaded.getInvoiceTransactionType());
		supplier = (Supplier) bean.insert(supplier);
		if (!StringUtils.isBlank(loaded.getCuenta())) {
			IManagerBean caBean = BeanManager.getManagerBean(SupplierAccount.class);
			Account account = ensureAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			SupplierAccount ca = new SupplierAccount();
			ca.setAccount(account);
			ca.setSupplier(supplier);
			caBean.insert(ca);					
		}
		return supplier;
	}

	private void checkIAccount(LoadedInvoice loaded,IManagerBean bean,String alias,ITransferObject iRegistry,Integer id,String msg,IAccount emptyIAccount) throws ManagerBeanException {
		Criteria c = new Criteria();
		c.addEqualExpression(alias, id );
		List<ITransferObject> l = bean.getList(c);		
		if ( l.size() > 0 ) {
			IAccount iAccount = (IAccount) l.get(0);
			if (!StringUtils.equals(iAccount.getAccount().getCode(),loaded.getCuenta())) {
				throw new ManagerBeanException("Existe una cuenta cuenta contable vinculada al " + msg +" y no coincide con la indicada." +
												" Grabada '" + iAccount.getAccount().getCode() + "', en el fichero '"+ loaded.getCuenta()+"'");						
			}
		} else {
			Account account = ensureAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			emptyIAccount.setAccount(account);
			emptyIAccount.setLinkedTo(iRegistry);
			bean.insert((ITransferObject) emptyIAccount);					
		}
	}

	public Integer insertInvoiceDetail(ILoaderIdCache cache,LoadedInvoiceDetail loaded) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(InvoiceDetail.class);
		InvoiceDetail detail = new InvoiceDetail();
		
		Invoice invoice = (Invoice) cache.getAonEntity("FRA", loaded.getFactura().toString());
		if (invoice  == null) {
			throw new AonException("La factura con identiicador " + loaded.getFactura() + " no existe.");
		}
		detail.setInvoice(invoice);
		detail.setLine(loaded.getLinea());
		if (StringUtils.isBlank(loaded.getArticulo())) {
			detail.setSource(InvoiceSource.ACCOUNT);	

			StringBuilder sb = new StringBuilder();
			sb.append("Fra. Nº: ");
			sb.append(invoice.getReferenceCode());
			sb.append(" del ");
			sb.append(FORMATTER.format(invoice.getIssueDate()));
			detail.setDescription(sb.toString());
		} else {
			Item item = obtainItem( loaded );
			detail.setItem(item);
			detail.setDescription(StringUtils.join(new String[]{item.getProduct().getName(),item.getDescription()}," "));
			detail.setSource(InvoiceSource.DIRECT_INVOICE);
		}
		detail.setPrice(loaded.getPrecio());
		detail.setQuantity(loaded.getCantidad());
		detail.setTaxableBase(loaded.getBaseImponible());
		detail.setWorkPlace(getParams().getWorkPlace());
		// No actualiza la linea.
		detail.setUpdateEnabled(false);
		// No actualiza los totales. 
		detail.getInvoice().setUpdateEnabled(false);
		//
		detail = (InvoiceDetail) bean.insert(detail);

		if (detail.getSource() == InvoiceSource.ACCOUNT) {
			String prefix = (invoice.getType() == InvoiceType.SALES) ? "N/Fra" : "S/Fra";
			if (invoice.getTotal() < 0) {
				prefix += " " + "ABONO";
			}
			prefix += ": ";
			String concept = StringUtils.abbreviate(prefix + invoice.getReferenceCode(), 32); 

			IManagerBean aedBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			AccountEntryDetail aed = new AccountEntryDetail();
			AccountEntry entry = ensureAccountEntry( invoice ); 
			aed.setAccountEntry( entry );
			Account account =  ensureAccount( loaded.getCuenta(), loaded.getConcepto() );
			aed.setAccount( account );
			aed.setDocumentNumber(invoice.getDocumentNumber());
			aed.setConcept( concept );
			if (entry.getType() == AccountEntryType.SALES_INVOICE) {
				aed.setCredit(loaded.getBaseImponible());
			} else {
				aed.setDebit(loaded.getBaseImponible());
			}
			aedBean.insert(aed);
			
			IManagerBean idaBean = BeanManager.getManagerBean(InvoiceDetailAccount.class);
			InvoiceDetailAccount ida = new InvoiceDetailAccount();
			ida.setInvoiceDetail(detail);
			ida.setAccount(account);
			idaBean.insert(ida);
			
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			InvoiceTax invoiceTax = new InvoiceTax();
			invoiceTax.setInvoiceDetail(detail);
			invoiceTax.setPercentage(loaded.getPorcentajeIva());
			invoiceTax.setQuota(loaded.getCuotaIva());
			if (!invoice.isSurcharge() && loaded.getRe() > 0) {
				invoice.setDefaultTaxInfo(false);
				invoice.setSurcharge(true);
			}
			invoiceTax.setSurcharge(loaded.getRe());
			invoiceTax.setSurchargeQuota(loaded.getCuotaRe());
			invoiceTax.setTaxType(TaxType.VAT);
			invoiceTax.setVatDeductionType(loaded.getVatDeductionType());
			invoiceTaxBean.insert(invoiceTax);

			aed = new AccountEntryDetail();
			aed.setAccountEntry( entry );
			aed.setDocumentNumber(invoice.getDocumentNumber());
			aed.setConcept( concept );
			
			Account vatAccount;
			if (StringUtils.isNotEmpty( loaded.getCuentaIva()) ){
				if (entry.getType() == AccountEntryType.SALES_INVOICE) {
					vatAccount = ensureAccount( loaded.getCuentaIva(), "Hacienda Pública, IVA repercutido." );		
				} else {
					vatAccount = ensureAccount( loaded.getCuentaIva(), "Hacienda Pública, IVA soportado." );
				}
			} else {
				vatAccount = (entry.getType() == AccountEntryType.SALES_INVOICE)?getOutputVatAccount():getInputVatAccount();
			}
			double cuota = CommonUtil.round(loaded.getCuotaIva() + loaded.getCuotaRe(),2);
			if (entry.getType() == AccountEntryType.SALES_INVOICE) {
				aed.setCredit(cuota);
			} else {
				aed.setDebit(cuota);
			}
			aed.setAccount(vatAccount);
			aedBean.insert(aed);
			
			
			if (loaded.getPorcentajeIrpf() > 0) {
				if (!invoice.isWithholding()) {
					invoice.setDefaultTaxInfo(false);
					invoice.setWithholding(true);
				}
				invoiceTax = new InvoiceTax();
				invoiceTax.setInvoiceDetail(detail);
				invoiceTax.setPercentage(loaded.getPorcentajeIrpf());
				invoiceTax.setQuota(loaded.getCuotaIrpf());
				invoiceTax.setSurcharge(0);
				invoiceTax.setTaxType(TaxType.RETENTION);
				invoiceTax.setWithholdingType(loaded.getWithholdingType());
				invoiceTaxBean.insert(invoiceTax);

				aed = new AccountEntryDetail();
				aed.setAccountEntry( entry );
				aed.setDocumentNumber(invoice.getDocumentNumber());
				aed.setConcept( concept );
				if (StringUtils.isNotEmpty( loaded.getCuentaIva()) ){
					account = ensureAccount( loaded.getCuentaIva(), "Hacienda Pública, retenciones y pagos a cuenta." );
				} else {
					account = getRetentionAccount();
				}
				aed.setAccount(account);
				if (entry.getType() == AccountEntryType.SALES_INVOICE) {
					aed.setDebit(loaded.getCuotaIrpf());
				} else {
					aed.setCredit(loaded.getCuotaIrpf());
				}
				aedBean.insert(aed);
			}
		}
		return detail.getId();
	}
	
	private AccountEntry ensureAccountEntry(Invoice invoice) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AccountEntryInvoice.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.ACCOUNT_ENTRY_INVOICE_INVOICE_ID) , invoice.getId());
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return ((AccountEntryInvoice) list.get(0)).getAccountEntry();	
		} 
		return null;
	}

	private Item obtainItem(LoadedInvoiceDetail loaded) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE), loaded.getArticulo());
		List<ITransferObject> list = itemBean.getList(criteria);
		Item item = null;
		if (list.isEmpty()) {
			Product product = new Product();
			product.setCode(loaded.getArticulo());
			product.setName(loaded.getConcepto());
			product.setVat( obtainTax( TaxType.VAT, loaded ) );
			if (loaded.getPorcentajeIrpf() != 0) {
				product.setRetention(obtainTax( TaxType.RETENTION, loaded ) );
			}
			product.setCategory( getParams().getCategory() );
			product.setStatus(ProductStatus.ACTIVE);
			product.setType(ProductType.COMMERCIAL_PRODUCT);
			item = new Item();
			item.setProduct(product);
			item.setPrice( loaded.getPrecio() );
			item.setStatus(ProductStatus.ACTIVE);
			item = (Item) itemBean.insert(item);
		} else {
			item = (Item) list.get(0);	
		}
		return item;
	}

	private Tax obtainTax(TaxType type, LoadedInvoiceDetail loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_TYPE), type);
		if (type == TaxType.VAT) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_PERCENTAGE), loaded.getPorcentajeIva());
			if ( loaded.getRe() != 0) {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_SURCHARGE), loaded.getRe());	
			}
		} else {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_PERCENTAGE), loaded.getPorcentajeIrpf());
		}
		List<ITransferObject> list = bean.getList(criteria);
		Tax tax = null;
		if (list.isEmpty()) {
			tax = new Tax();
			tax.setType(type);
			try {
				tax.setStartDate(FORMATTER.parse("01/01/2000"));
			} catch (ParseException e) {
				/// nothing
			}
			tax.setPercentage((type == TaxType.VAT)?loaded.getPorcentajeIva():loaded.getPorcentajeIrpf() );
			tax.setName((type == TaxType.VAT)?"IVA " + loaded.getPorcentajeIva():"IRPF " + loaded.getPorcentajeIrpf() );
			tax.setSurcharge( (type == TaxType.VAT)?loaded.getRe(): 0.0 );
			tax.setVatDeductionType((type == TaxType.VAT)?loaded.getVatDeductionType(): null );
			tax.setWithholdingType((type == TaxType.VAT)?null:loaded.getWithholdingType());
			tax = (Tax) bean.insert(tax);
		} else {
			tax = (Tax) list.get(0);
		}
		return tax;
	}
	
	public Account getOutputVatAccount() throws ManagerBeanException {
		if (outputVatAccount == null) {
			outputVatAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.CHARGE_VAT_ACCOUNT);
		}
		return outputVatAccount;
	}

	public Account getInputVatAccount() throws ManagerBeanException {
		if (inputVatAccount == null) {
			inputVatAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_VAT_ACCOUNT);  
		}
		return inputVatAccount;
	}

	public Account getRetentionAccount() throws ManagerBeanException {
		if (retentionAccount == null) {
			retentionAccount = getAccountingUtil().obtainDefaultAccount(DefaultAccounts.PAID_RETENTION_ACCOUNT);
		}
		return retentionAccount;
	}

}
