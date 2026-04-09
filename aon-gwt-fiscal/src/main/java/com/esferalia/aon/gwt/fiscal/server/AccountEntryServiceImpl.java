package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.FinanceEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.SalaryEntry;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceRectificationData;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.AccountEntryUpdate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.DataUrl;
import com.esferalia.aon.watson.server.io.DataUrlSerializer;
import com.esferalia.aon.watson.server.io.IDataUrlSerializer;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@WebServlet(name = "Account Entry Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AccountEntry" })
public class AccountEntryServiceImpl extends AonStatelessRemoteServiceServlet implements AccountEntryService {

	private static final long serialVersionUID = 8791955004212947200L;

	@Override
	public AccountEntry getAccountEntry(Occam occam, int id) throws AonCoreException {
		return ACCOUNTING.getAccountEntry(occam, id);
	}
	
	@Override
	public AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry, AccountingInvoice ai, boolean preserveData) {
		return ACCOUNTING.initializeInvoice(occam, registry, ai, preserveData);
	}

	@Override
	public AccountingInvoice initializeInvoice(Occam occam, AccountingRegistry registry, Integer activity, Date issueDate) throws AonCoreException {
		return ACCOUNTING.initializeInvoice(occam, registry, activity, issueDate);
	}
	
	@Override
	public FinanceEntry save(Occam occam, FinanceEntry financeEntry) throws AonCoreException {
		return ACCOUNTING.save(occam, financeEntry);
	}

	@Override
	public FinanceEntry getFinanceEntry(Occam occam, Integer accountEntry) {
		return ACCOUNTING.getFinanceEntry(occam, accountEntry);
	}

	@Override
	public AccountingInvoice rectifyInvoice(Occam occam, Integer invoiceId, InvoiceRectificationData data) throws AonCoreException {
		return ACCOUNTING.rectifyInvoice(occam, invoiceId, data);
	}

	@Override
	public TediResult parseInvoice(String domainName, String user, int domain, String fileName, String content) throws AonCoreException {
		try {
			IDataUrlSerializer serializer = new DataUrlSerializer();
			DataUrl unserialized = serializer.unserialize(content);
			ByteArrayInputStream input = new ByteArrayInputStream(unserialized.getData());
			String extension = AonStringUtils.substringAfterLast(fileName, ".");
			TediResult result = TEDI.parse(new TediContext().setDomainName(domainName).setDomain(domain).setUser(user), input, MimeType.getByExtension(extension));
			result.getAccountingInvoice()
				.setFromRawdoc(false)
				.setTediParsed(true);
			return result;
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new AonCoreException(e);
		}
	}
	
	@Override
	public TediResult validateInvoice(String domainName, String user, int domain, TediResult result ) throws AonCoreException {
		try {
			return TEDI.validateInvoice(new TediContext().setDomainName(domainName).setDomain(domain).setUser(user), result);
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		} 	
	}
	
	// *************************************	
	// *************************************	
	// *************************************	
	// *************************************	

	@Override
	public LinkedList<AccountEntry> getAccountEntries(String domainName, int domain, String user, final AccountEntryParams params, int offset, int limit) throws AonCoreException {
		return ACCOUNTING.getAccountEntries(domainName, domain, user, params, offset, limit);
	}

	@Override
	public AccountEntry getAccountEntry(String domainName, int domain, String user, int id) throws AonCoreException {
		return ACCOUNTING.getAccountEntry(domainName, domain, user, id);
	}

	@Override
	public AccountEntry save(String domainName, int domain, String user, AccountEntry ae) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, user, ae);
	}

	@Override
	public LinkedList<SalaryEntry> getSalaryEntries(String domainName, int domain, String user, Date from, Date to) {
		return ACCOUNTING.getSalaryEntries(domainName, domain, user, from, to);
	}

	@Override
	public String getSalaryFormatted(String domainName, int domain, String user, Date from, Date to) {
		return ACCOUNTING.getSalaryFormatted(domainName, domain, user, from, to);
	}

	@Override
	public void deleteAccountEntry(String domainName, int domain, String user, Integer id) {
		ACCOUNTING.deleteAccountEntry(domainName, domain, user, id);
	}

	@Override
	public AccountingInvoice removeInvoiceAttach(String domainName, int domain, String user, Integer invoiceId) throws AonCoreException {
		return ACCOUNTING.removeInvoiceAttach(domainName, domain, user, invoiceId);
	}

	@Override
	public AccountingInvoice addInvoiceAttach(String domainName, int domain, String user, AccountingInvoice ai) throws AonCoreException {
		return ACCOUNTING.addInvoiceAttach(domainName, domain, user, ai);
	}

	@Override
	public AccountingInvoice getAccountingInvoice(String domainName, int domain, String user, Integer accountEntry) throws AonCoreException {
		return ACCOUNTING.getAccountingInvoice(domainName, domain, user, accountEntry);
	}

	@Override
	public AccountingInvoice getAccountingInvoiceFromInvoice(String domainName, int domain, String user, Integer invoiceId) throws AonCoreException {
		return ACCOUNTING.getAccountingInvoiceFromInvoice(domainName, domain, user, invoiceId);
	}

	@Override
	public LinkedList<AccountingInvoice> getPendingImportAccountingInvoices(Occam occam, String query) throws AonCoreException {
		return ACCOUNTING.getPendingImportAccountingInvoices(occam, query);
	}

	@Override
	public AccountingInvoice getRegistryLastAccountingInvoice(String domainName, int domain, String user, Integer registryId) {
		return ACCOUNTING.getRegistryLastAccountingInvoice(domainName, domain, user, registryId);
	}

	@Override
	public AccountingInvoice save(String domainName, int domain, String user, AccountingInvoice invoice) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain, user, invoice);
	}

	@Override
	public IAccountEntryWrapper updateSpecial(Occam occam, AccountEntryUpdate operation, IAccountEntryWrapper wrapper) throws AonCoreException {
		return ACCOUNTING.updateSpecial(occam, operation, wrapper);
	}

	@Override
	public LinkedList<AccountEntryUpdate> getAvailableAccountEntryUpdates(String domainName, int domain, String user, IAccountEntryWrapper wrapper) throws AonCoreException {
		return ACCOUNTING.getAvailableAccountEntryUpdates(domainName, domain, user, wrapper);
	}
	
	// AMORTIZATION TYPE

	@Override
	public List<Account> getFixedAssetAccounts(String domainName, int domain, String user) throws AonCoreException {
		List<Account> fixedAssetAccounts = ACCOUNTING.getAccounts(domainName, domain, user, 
				f -> (f.getCodeProperty().like("20%").or(f.getCodeProperty().like("21%")).or(f.getCodeProperty().like("22%")))
				.and(f.getEntryEnabledProperty().eq((byte)0))
				.and(f.getLevelProperty().eq((byte)4))
				.and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toList());
		
		fixedAssetAccounts.sort((o1, o2) -> o1.getDomain().compareTo(o2.getDomain()));
		Collections.reverse(fixedAssetAccounts);
		
		return parseRepeatAccounts(domain, fixedAssetAccounts);
	}

	@Override
	public List<Account> getAccumulatedAccounts(String domainName, int domain, String user) throws AonCoreException {
		List<Account> accumulatedAccounts = ACCOUNTING.getAccounts(domainName, domain, user,
				f -> (f.getCodeProperty().like("280%").or(f.getCodeProperty().like("281%")).or(f.getCodeProperty().like("282%")))
				.and(f.getEntryEnabledProperty().eq((byte)0))
				.and(f.getLevelProperty().eq((byte)4))
				.and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toList());
		
		accumulatedAccounts.sort((o1, o2) -> o1.getDomain().compareTo(o2.getDomain()));
		Collections.reverse(accumulatedAccounts);
		
		return parseRepeatAccounts(domain, accumulatedAccounts);
	}

	@Override
	public List<Account> getAllocationAccounts(String domainName, int domain, String user) throws AonCoreException {
		List<Account> allocationAccounts = ACCOUNTING.getAccounts(domainName, domain, user,
				f -> (f.getCodeProperty().like("680%").or(f.getCodeProperty().like("681%")).or(f.getCodeProperty().like("682%")))
				.and(f.getEntryEnabledProperty().eq((byte)0))
				.and(f.getLevelProperty().eq((byte)4))
				.and(f.getActiveProperty().eq((byte)1)))
				.collect(Collectors.toList());
		
		allocationAccounts.sort((o1, o2) -> o1.getDomain().compareTo(o2.getDomain()));
		Collections.reverse(allocationAccounts);
		
		return parseRepeatAccounts(domain, allocationAccounts);
	}
	
	private List<Account> parseRepeatAccounts(int domain, List<Account> list) {
		List<Account> result = new ArrayList<>();
		
		for(Account account : list) {
			if(account.getDomain().equals(domain)) result.add(account);
			else if(!containsCode(result, account.getCode())) result.add(account);
		}
		
		result.sort((o1, o2) -> o1.getCode().compareTo(o2.getCode()));
		
		return result;
	}

	private boolean containsCode(List<Account> list, String code) {
		Optional<Account> find = list.stream().filter(account -> AonStringUtils.equalsIgnoreCase(account.getCode(), code)).findAny();
		return find.isPresent();
	}

	@Override
	public List<AmortizationType> getAmortizationTypeList(String domainName, int domain, String user, AmortizationTypeParams params) throws AonCoreException {
		return ACCOUNTING.getAmortizationTypeList(domainName, domain, user, params);
	}

	@Override
	public void deleteAmortizationTypes(String domainName, int domain, String user, List<Integer> deleteIds) throws AonCoreException {
		ACCOUNTING.deleteAmortizationTypes(domainName, domain, user, deleteIds);
	};
	
	@Override
	public void saveAmortizationType(String domainName, int domain, String user, AmortizationType amortizationType) throws AonCoreException {
		ACCOUNTING.saveAmortizationType(domainName, domain, user, amortizationType);
	};

}
