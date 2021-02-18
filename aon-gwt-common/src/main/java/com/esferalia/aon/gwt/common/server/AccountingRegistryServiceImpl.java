package com.esferalia.aon.gwt.common.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.client.AccountingRegistryService;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.AccountingRegistryProperties;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistryParams;
import com.esferalia.aon.occam.api.model.registry.InvoiceRegistry;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Aon Accounting Registry Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/AccountingRegistry", "/aon_gwt_aio/ms/AccountingRegistry"})
public class AccountingRegistryServiceImpl extends AonStatelessRemoteServiceServlet implements AccountingRegistryService {

	private static final long serialVersionUID = 7126010462321534330L;
	
	public static final Integer ZERO = 0;
	
	@Override
	public AccountingRegistry initialize(String domainName, int domain, String user,
			AccountingRegistry ar) throws AonCoreException {
		return ACCOUNTING.initialize(domainName, domain,user,ar);		
	}

//	@Override
//	public AccountingRegistry getAccountingRegistry(String domainName, int domain, String user,
//			AccountingRegistry ar) throws AonCoreException {
//
//		com.esferalia.aon.occam.api.model.registry.Registry reg = AON.getRegistry(domainName, domain, user, f -> 
//			f.getDomainProperty().eq(domain)
//			.and(f.getDocumentProperty().eq(ar.getDocument()))
//		);
//		
//		if(reg.getId() == null ) {
//			reg = AON.getRegistry(domainName, domain, user, f -> 
//				f.getDomainProperty().eq(0)
//			.and(f.getDocumentProperty().eq(ar.getDocument())));
//		}
//		RAddress address = new RAddress();
//		String email = null;
//		String phone = null;
//		String cellular = null;
//		String fax = null;
//		String web = null;
//		if(reg.getId() != null) {
//			Integer regId = reg.getId();
//			address = AON.getRAddress(domainName, domain, user, f -> 
//				f.getRegistryProperty().eq(regId)
//				.and(f.getTypeProperty().eq((byte) 0)));
//			
//			email = AON.getRMedia(domainName, domain, user, f -> 
//				f.getRegistryProperty().eq(regId)
//				.and(f.getMediaProperty().eq(MediaType.EMAIL.value())))
//				.getValue();
//			
//			phone = AON.getRMedia(domainName, domain, user, f -> 
//				f.getRegistryProperty().eq(regId)
//				.and(f.getMediaProperty().eq(MediaType.FIXED_PHONE.value())))
//				.getValue();
//			
//			cellular = AON.getRMedia(domainName, domain, user, f -> 
//				f.getRegistryProperty().eq(regId)
//				.and(f.getMediaProperty().eq(MediaType.CELLULAR.value())))
//				.getValue();
//			
//			fax = AON.getRMedia(domainName, domain, user, f -> 
//				f.getRegistryProperty().eq(regId)
//				.and(f.getMediaProperty().eq(MediaType.FAX.value())))
//				.getValue();
//			
//			web = AON.getRMedia(domainName, domain, user, f -> 
//				f.getRegistryProperty().eq(regId)
//				.and(f.getMediaProperty().eq(MediaType.WEB.value())))
//				.getValue();
//		}		
//	
//		Account acc = ACCOUNTING.getAccounts(domainName, domain, user, f -> 
//			f.getAliasProperty().eq(ar.getDocument()))
//			.findFirst().orElse(new Account());
//		
//		return ar.setId(!ZERO.equals(reg.getDomain()) ? reg.getId() : null)
//				.setAccountId(acc.getId())
//				.setAccountCode(acc.getCode())
//				.setAccountDescription(acc.getDescription())
//				.setAlias(reg.getAlias())
//				.setDocumentType(reg.getDocumentType() != null ? reg.getDocumentType() : ar.getDocumentType())
//				.setDocumentCountry(reg.getDocumentCountry() != null ? reg.getDocumentCountry() : ar.getDocumentCountry())
//				.setName(reg.getName())
//				.setAddress(address.getAddress())
//				.setAddressId(!ZERO.equals(address.getDomain()) ? address.getId() : null)
//				.setAddressNumber(address.getNumber())
//				.setAddressStreetType(StreetType.safeValueOf(address.getStreet_type()))
//				.setAddressTown(address.getCity())
//				.setAddressZIP(address.getZip())
//				.setGeozone(address.getGeozone())	
//				.setPhone(phone)
//				.setCellular(cellular)
//				.setFax(fax)
//				.setEmail(email)
//				.setWeb(web);
//	}
	
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
				p ->     (p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q))
					 .or(p.getAccountCodeProperty().like(q))
					 .or(p.getAccountDescriptionProperty().like(q)))
				.and(p.getStatusProperty().ne( RegistryStatus.INACTIVE.value()))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	
	@Override
	public LinkedList<AccountingRegistry> getAccountingRegistries(String domainName, int domain, String user,
			AccountingRegistryParams params) throws AonCoreException {
		LinkedList<AccountingRegistry> list = ACCOUNTING.getAccountingRegistries(domainName, domain,user,
				p -> getFilter(p, params))
				.collect(Collectors.toCollection(LinkedList::new));
		 return list;
	}
	
	private Filter getFilter(AccountingRegistryProperties p, AccountingRegistryParams params) {
		Filter f = p.getStatusProperty().ne( RegistryStatus.INACTIVE.value());
		if (AonStringUtils.isNotBlank(params.getDocument())) {
			f = f.and(p.getDocumentProperty().eq(params.getDocument()));
		}
		if (params.getDocumentType() != null) {
			f = f.and(p.getDocumentTypeProperty().eq(params.getDocumentType().value()));
		}
		if (params.getDocumentCountry() != null) {
			f = f.and(p.getDocumentCountryProperty().eq(params.getDocumentCountry().getIso2()));
		}
		return f;
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
