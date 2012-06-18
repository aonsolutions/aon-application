package com.code.aon.ui.accounting.entry;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.Scope;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Creditor;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.supplier.Supplier;
import com.esferalia.aon.entity.IEntityAlias;


//"Fecha";"Asiento";"";"Cuenta";"Descripción";"Concepto del asiento";"Sección";"Documento";"Factura";"Debe";"Haber";
/**
 * @author ecastellano
 *
 */
public class AccountEntryLoaderManager {

	private SecurityLevel securityLevel;
	private Period period;
	private Scope scope;
	
	public AccountEntryLoaderManager(Period period,Scope scope,SecurityLevel securityLevel, PrintWriter log) {
		this.securityLevel = securityLevel;
		this.period = period;
		this.scope = scope;
	}
	
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public Period getPeriod() {
		return period;
	}
	public Scope getScope() {
		return scope;
	}

	public void validateFormat(ByteArrayInputStream input) throws AonException {
    	InputStreamReader r = new InputStreamReader(input);
    	LineNumberReader reader = new LineNumberReader(r);
    	int i = 0;
		try {
	    	while (reader.ready()) {
	    		String lineInput = reader.readLine();
	    		i++;
	    		if (StringUtils.isNotBlank( StringUtils.trim(lineInput))) {
	    			CSVParser csvParser = new CSVParser();
	    			String[] fields = csvParser.parse(lineInput);
	    			EntryLoaded loaded = new EntryLoaded( );
	    			loaded.populate(fields);
	    		}
	    	}
		} catch (Throwable e) {
			throw new AonException("Línea "+ i +". "+e.getMessage());
		}
	}

	public void load(ByteArrayInputStream input) throws AonException {
		IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
		IManagerBean detailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		
    	InputStreamReader r = new InputStreamReader(input);
    	LineNumberReader reader = new LineNumberReader(r);
    	int i = 0;
		try {
			Date date = null;
			Integer id = null;
			AccountEntry entry = null;
			int line = 0;
	    	while (reader.ready()) {
	    		String lineInput = reader.readLine();
	    		i++;
	    		if (StringUtils.isNotBlank( StringUtils.trim(lineInput))) {
	    			CSVParser csvParser = new CSVParser();
	    			String[] fields = csvParser.parse(lineInput);
	    			EntryLoaded loaded = new EntryLoaded( );
	    			loaded.populate(fields);
	    			if (date == null || (loaded.getDate() != null && !date.equals(loaded.getDate())) ) {
	    				date = loaded.getDate();
	    			}
	    			if (id == null || (loaded.getId() != null && !id.equals(loaded.getId()))) {
	    				id = loaded.getId();
	    				entry = insertEntry(entryBean, loaded);
	    				line = 0;
	    			}
	    			insertEntryDetail(detailBean, entry, loaded, line);
	    		}
	    	}
		} catch (Throwable e) {
			throw new AonException("Línea "+ i +". "+e.getMessage());
		}
	}

	private AccountEntry insertEntry(IManagerBean entryBean, EntryLoaded loaded) throws ManagerBeanException {
		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod( getPeriod() );
		entry.setEntryDate( loaded.getDate() );
		entry.setSecurityLevel(getSecurityLevel());
		entry.setType(AccountEntryType.MANUAL);
		return (AccountEntry) entryBean.insert(entry);
	}

	private void insertEntryDetail(IManagerBean detailBean, AccountEntry entry, EntryLoaded loaded, int line) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setConcept(StringUtils.abbreviate( loaded.getConcept(), 32));
		detail.setDocumentNumber(StringUtils.join( new String[] {loaded.getDocument(), loaded.getInvoice()}));
		detail.setBalancingAccount(null);
		if (loaded.getDebit() != 0.0) {
			detail.setDebit(loaded.getDebit());	
		}
		if (loaded.getCredit() != 0.0) {
			detail.setCredit(loaded.getCredit());
		}
		detail.setLine(++line);
		Account account = getAccount( loaded.getAccount(), loaded.getDescription() );
		detail.setAccount(account);
		detailBean.insert(detail);		
	}

	private Account getAccount(String code,String description) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), code);
		List<ITransferObject> list = bean.getList(c);
		Account account = null;
		if (list == null || list.isEmpty()) {
			account = new Account();
			account.setCode(code);
			account.setDescription(description);
			account = (Account) bean.insert(account);
			if (StringUtils.startsWith(account.getCode(),"400")) {
				recordSupplier(account);
			} else if (StringUtils.startsWith(account.getCode(),"410")) {
				recordCreditor(account);
			} else if (StringUtils.startsWith(account.getCode(),"430")) {
				recordCustomer(account);
			}
		} else {
			account = (Account) list.get(0);
		}
		return account;
	}

	private void recordCustomer(Account account) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		Customer customer = new Customer();
		Registry registry = new Registry();
		registry.setName(account.getDescription());
		customer.setRegistry(registry);
		customer.setScope(getScope());
		customer = (Customer) bean.insert(customer);
		IManagerBean cabean = BeanManager.getManagerBean(CustomerAccount.class);
		CustomerAccount customerAccount = new CustomerAccount();
		customerAccount.setAccount(account);
		customerAccount.setCustomer(customer);
		cabean.insert(customerAccount);
	}

	private void recordCreditor(Account account) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Creditor creditor = new Creditor();
		Registry registry = new Registry();
		registry.setName(account.getDescription());
		creditor.setRegistry(registry);
		creditor.setScope(getScope());
		bean.insert(creditor);
		IManagerBean cabean = BeanManager.getManagerBean(CreditorAccount.class);
		CreditorAccount creditorAccount = new CreditorAccount();
		creditorAccount.setAccount(account);
		creditorAccount.setCreditor(creditor);
		cabean.insert(creditorAccount);
	}

	private void recordSupplier(Account account) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Supplier supplier = new Supplier();
		Registry registry = new Registry();
		registry.setName(account.getDescription());
		supplier.setRegistry(registry);
		supplier.setScope(getScope());
		bean.insert(supplier);
		IManagerBean cabean = BeanManager.getManagerBean(SupplierAccount.class);
		SupplierAccount supplierAccount = new SupplierAccount();
		supplierAccount.setAccount(account);
		supplierAccount.setSupplier(supplier);
		cabean.insert(supplierAccount);
	}
}
