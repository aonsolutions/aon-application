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
import com.esferalia.aon.occam.api.model.Properties.AccountingRegistryProperties;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.CreditorStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Aon Common Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Common", "/aon_gwt_aio/ms/Common"})
public class CommonServiceImpl extends AonStatelessRemoteServiceServlet implements CommonService {

	private static final long serialVersionUID = -6555645829679341214L;

	// --------------------------------------------------------- CONFIGURATION
	@Override
	public AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain,String user) {
		return AON.getConfiguration(currentDomainName, currentDomain,user, null);
	}
	@Override
	public AonConfiguration getAonConfiguration(String currentDomainName, int currentDomain,String user, Date atDate) {
		return AON.getConfiguration(currentDomainName, currentDomain,user, atDate);
	}

	// -------------------------------------------------------------- SECURITY
	@Override
	public User getCurrentUser(String domainName, int domain,String user) throws AonCoreException {
		return AON.getUser(domainName,domain,user); 		
	}
	// -------------------------------------------------------------- ENTERPRISE
	@Override
	public LinkedList<Enterprise> getParentEnterprises(String domainName, int domain,String user,String query) throws AonCoreException {
		return AON.getParentEnterprises(domainName, domain,user, query);		
	}

	@Override
	public Enterprise getEnterprise(String domainName, int domain,String user, int id) throws AonCoreException {
		return AON.getEnterprise(domainName, domain,user, id);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain,String user, int enterprise) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,user, enterprise);
	}

	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName,int domain,String user) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain,user);
	}

	@Override
	public LinkedList<Account> getAccounts(String domainName, int domain,String user,String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
		 	?((AonStringUtils.isNumeric(query)? AonStringUtils.EMPTY:AonStringUtils.PERCENT) 
		 			+ query 
		 			+ AonStringUtils.PERCENT)
			:(query);
		return ACCOUNTING.getAccounts(domainName, domain,user,
				p ->  p.getActiveProperty().eq((byte) 1)
					.and(p.getCodeProperty().like(q)
					 .or(p.getDescriptionProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public Account getAccount(String domainName, int domain,String user,Integer id) throws AonCoreException {
		return ACCOUNTING.getAccount(domainName, domain,user, id);
	}
	@Override
	public Account getAccount(String domainName, int domain,String user,String code) throws AonCoreException {
		return ACCOUNTING.getAccount(domainName, domain,user, code);
	}
	
	@Override
	public Account save(String domainName, int domain,String user, Account account) throws AonCoreException {
		return ACCOUNTING.save(domainName, domain,user, account);
	}
	@Override
	public String getAccountNextCode(String domainName, int domain,String user, String prefix) {
		return ACCOUNTING.getAccountNextCode(domainName, domain,user, prefix);
	}

	// -------------------------------------------------------------- CREDITOR
	@Override
	public LinkedList<Creditor> getBasicCreditors(String domainName, int domain,String user, String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getBasicCreditors(domainName, domain,user,
				p ->  p.getActiveProperty().eq( CreditorStatus.ACTIVE.value())
					.and(p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}

	
	@Override
	public AccountingRegistry getAccountingRegistry(String domainName, int domain, String user,
			AccountingRegistry ar) throws AonCoreException {

		com.esferalia.aon.occam.api.model.registry.Registry reg = AON.getRegistry(domainName, domain, user, f -> 
			f.getDomainProperty().eq(domain)
			.and(f.getDocumentProperty().eq(ar.getDocument()))
		);
		// [EUKE] REG puede se NULL. Evita el NullPointer.

		/* [EUKE]
		 * 
		 	Ese filtro de Alias NO MOLA. ¿¿Tiene pinta de de ñapita??
			NO puedes fiarte del contenido de la columna alias.
		*/
		Account acc = ACCOUNTING.getAccounts(domainName, domain, user, f -> 
			f.getAliasProperty().eq(ar.getDocument()))
			.findFirst().orElse(new Account());

		return ar.setId(reg.getId())
				.setAccountId(acc.getId())
				.setAccountCode(acc.getCode())
				.setAccountDescription(acc.getDescription())
				.setAlias(reg.getAlias())
				.setDocumentType(reg.getDocumentType() != null ? reg.getDocumentType() : ar.getDocumentType())
				.setDocumentCountry(reg.getDocumentCountry() != null ? reg.getDocumentCountry() : ar.getDocumentCountry())
				.setName(reg.getName());
	}
	
	@Override
	public LinkedList<AccountingRegistry> getAccountingRegistries(String domainName, int domain,String user, Integer id) throws AonCoreException {
		return ACCOUNTING.getAccountingRegistries(domainName, domain,user,
				p -> p.getIdProperty().eq(id))
			.collect(Collectors.toCollection(LinkedList::new));
	}
	@Override
	public LinkedList<AccountingRegistry> getAccountingRegistries(String domainName, int domain,String user, String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return ACCOUNTING.getAccountingRegistries(domainName, domain,user,
				p ->     p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q))
					 .or(p.getAccountCodeProperty().like(q))
					 .or(p.getAccountDescriptionProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public LinkedList<AccountingRegistry> getAccountingRegistries(String domainName, int domain, String user,
			AccountingRegistryParams params) throws AonCoreException {
		return ACCOUNTING.getAccountingRegistries(domainName, domain,user,
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
	public AccountingRegistry insert(String domainName, int domain, String user, AccountingRegistry reg) throws AonCoreException {
		return ACCOUNTING.insert(domainName, domain,user, reg);
	}
	
	@Override
	public AccountingRegistry update(String domainName, int domain, String user, AccountingRegistry reg) throws AonCoreException {
		return ACCOUNTING.update(domainName, domain,user, reg);
	}
	
	@Override
	public LinkedList<InvoiceRegistry> getInvoiceRegistries(String domainName, int domain, String user, String query)
			throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getInvoiceRegistries(domainName, domain,user,
				p -> p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public LinkedList<Product> getInvoiceProducts(String domainName, int domain, String user, String query)
			throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getInvoiceProducts(domainName, domain,user
					,p -> p.getNameProperty().like(q)
						.or(p.getCodeProperty().like(q))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	
}
