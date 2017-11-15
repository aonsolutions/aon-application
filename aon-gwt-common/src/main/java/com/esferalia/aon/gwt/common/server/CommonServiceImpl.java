package com.esferalia.aon.gwt.common.server;

import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryProperties;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.CreditorStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Aon Common Servlet", urlPatterns = { "/aon_gwt_fiscal/Common", "/aon_gwt_aio/Common"})
public class CommonServiceImpl extends AonRemoteServiceServlet implements CommonService {

	// --------------------------------------------------------- CONFIGURATION
	@Override
	public AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain) {
		return AON.getConfiguration(currentDomainName, currentDomain,AonServletUtils.getLoggedUser(), null);
	}
	@Override
	public AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain, Date atDate) {
		return AON.getConfiguration(currentDomainName, currentDomain,AonServletUtils.getLoggedUser(), atDate);
	}

	// -------------------------------------------------------------- SECURITY
	@Override
	public User getCurrentUser(String domainName, int domain)
			throws AonCoreException {
		return AON.getUser(domainName,domain,AonServletUtils.getLoggedUser()); 		
	}
	// -------------------------------------------------------------- ENTERPRISE
	@Override
	public LinkedList<Enterprise> getParentEnterprises(String domainName, int domain,
			String query) throws AonCoreException {
		return AON.getParentEnterprises(domainName, domain,AonServletUtils.getLoggedUser(), query);		
	}

	@Override
	public Enterprise getEnterprise(String domainName, int domain, int id)
			throws AonCoreException {
		return AON.getEnterprise(domainName, domain,AonServletUtils.getLoggedUser(), id);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,
			int domain, int enterprise) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,AonServletUtils.getLoggedUser(), enterprise);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,
			int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,AonServletUtils.getLoggedUser());
	}

	@Override
	public LinkedList<Account> getAccounts(String domainName, int domain,
			String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
		 	?((AonStringUtils.isNumeric(query)? AonStringUtils.EMPTY:AonStringUtils.PERCENT) 
		 			+ query 
		 			+ AonStringUtils.PERCENT)
			:(query);
		return ACCOUNTING.getAccounts(domainName, domain,AonServletUtils.getLoggedUser(),
				p ->  p.getActiveProperty().eq((byte) 1)
					.and(p.getCodeProperty().like(q)
					 .or(p.getDescriptionProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Account getAccount(String domainName, int domain,Integer id) throws AonCoreException {
		return ACCOUNTING.getAccount(domainName, domain,AonServletUtils.getLoggedUser(), id);
	}
	@Override
	public Account getAccount(String domainName, int domain,String code) throws AonCoreException {
		return ACCOUNTING.getAccount(domainName, domain,AonServletUtils.getLoggedUser(), code);
	}
	
	@Override
	public Account save(String domainName, int domain, Account account) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain,AonServletUtils.getLoggedUser(), account);
	}
	@Override
	public String getAccountNextCode(String domainName, int domain, String prefix) {
		return ACCOUNTING.getAccountNextCode(domainName, domain,AonServletUtils.getLoggedUser(), prefix);
	}

	// -------------------------------------------------------------- CREDITOR
	@Override
	public LinkedList<Creditor> getBasicCreditors(String domainName, int domain, String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getBasicCreditors(domainName, domain,AonServletUtils.getLoggedUser(),
				p ->  p.getActiveProperty().eq( CreditorStatus.ACTIVE.value())
					.and(p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public LinkedList<AccountingRegistry> getAccountingRegistries(String domainName, int domain,
			Integer id) throws AonCoreException {
		return ACCOUNTING.getAccountingRegistries(domainName, domain,AonServletUtils.getLoggedUser(),
				p -> p.getIdProperty().eq(id))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public LinkedList<AccountingRegistry> getAccountingRegistries(String domainName, int domain, String query)
			throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return ACCOUNTING.getAccountingRegistries(domainName, domain,AonServletUtils.getLoggedUser(),
				p ->     p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q))
					 .or(p.getAccountCodeProperty().like(q))
					 .or(p.getAccountDescriptionProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public LinkedList<AccountingRegistry> getAccountingRegistries(String domainName, int domain,
			AccountingRegistryParams params) throws AonCoreException {
		return ACCOUNTING.getAccountingRegistries(domainName, domain,AonServletUtils.getLoggedUser(),
				p -> getFilter(p, params))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private Filter getFilter(AccountingRegistryProperties p, AccountingRegistryParams params) {
		return p.getDocumentTypeProperty().eq(params.getDocumentType().value())
			.and(p.getDocumentCountryProperty().eq(params.getDocumentCountry().getIso2()))
			.and(p.getDocumentProperty().eq(params.getDocument()))
		;
	}
	@Override
	public AccountingRegistry insert(String domainName, int domain, AccountingRegistry reg) throws AonCoreException {
		return ACCOUNTING.insert(domainName, domain,AonServletUtils.getLoggedUser(), reg);
	}
	
	@Override
	public LinkedList<InvoiceRegistry> getInvoiceRegistries(String domainName, int domain, String query)
			throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getInvoiceRegistries(domainName, domain,AonServletUtils.getLoggedUser(),
				p -> p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public LinkedList<Product> getInvoiceProducts(String domainName, int domain, String query)
			throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getInvoiceProducts(domainName, domain,AonServletUtils.getLoggedUser()
					,p -> p.getNameProperty().like(q)
						.or(p.getCodeProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	
}
