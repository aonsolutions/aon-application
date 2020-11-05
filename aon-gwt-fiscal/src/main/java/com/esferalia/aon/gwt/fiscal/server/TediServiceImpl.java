package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediService;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.io.DataUrl;
import com.esferalia.aon.watson.server.io.DataUrlSerializer;
import com.esferalia.aon.watson.server.io.IDataUrlSerializer;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@WebServlet(name = "TEDI Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/Tedi" })
public class TediServiceImpl extends AonStatelessRemoteServiceServlet implements TediService {

	private static final long serialVersionUID = -2121272613749054639L;

	@Override
	public TediResult parseInvoice(String domainName, String user, int domain, String fileName, String content) throws AonCoreException {
		try {
			IDataUrlSerializer serializer = new DataUrlSerializer();
			DataUrl unserialized = serializer.unserialize(content);
			ByteArrayInputStream input = new ByteArrayInputStream(unserialized.getData());
			TediResult result = TEDI.parse(new TediContext().setDomainName(domainName).setDomain(domain).setUser(user), input); 
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
	
/*
	@Override
	public AonConfiguration getAonConfiguration(String domainName, String user, int domain) {
		return AON.getConfiguration(domainName, domain,user, null);
	}

	@Override
	public LinkedList<TediResult> getVerifiedInvoices(String domainName, String user, int domain, boolean snapshot, Company company) throws AonCoreException {
		try {
			return TEDI.getVerifiedInvoices(domainName, company.getDomain(), snapshot, user, company);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}
	
	@Override
	public Integer getCountInboxInvoices(String domainName, String user, int domain, boolean snapshot, Company company) throws AonCoreException {
		try {
			return TEDI.getCountInvoices(domainName, company.getDomain(), snapshot, user, company, TediInvoiceStatus.inbox);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}
	
	@Override
	public TediResult getInvoice(String domainName, String user, int domain, boolean snapshot, String uuid, String tediStatus) throws AonCoreException {
		try {
			return TEDI.getInvoice(domainName, domain, snapshot, user, uuid, tediStatus);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public TediResult rejectInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice) throws AonCoreException {
		try {
			return TEDI.rejectInvoice(domainName, domain, snapshot, user, invoice);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public LinkedList<TediResult> rejectInvoices(String domainName, String user, int domain, boolean snapshot, LinkedList<TediResult> results) throws AonCoreException {
		try {
			return TEDI.rejectInvoices(domainName, domain, snapshot, user, results);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}
	
	@Override
	public TediResult acceptInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice) throws AonCoreException {
		try {
			return TEDI.acceptInvoice(domainName, domain, snapshot, user, invoice);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public LinkedList<TediResult> acceptInvoices(String domainName, String user, int domain, boolean snapshot, LinkedList<TediResult> results) throws AonCoreException {
		try {
			return TEDI.acceptInvoices(domainName, domain, snapshot, user, results);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	
	@Override
	public String getInvoiceAttachURL(String domainName, String user, int domain, boolean snapshot, String uuid) throws AonCoreException {
		try {
			return TEDI.getInvoiceAttach(domainName, domain, snapshot, user, uuid);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}
	
	public LinkedList<TediCompanyResult> getCompanies(String domainName, String user, int domain, boolean snapshot ) throws AonCoreException {
		User u =  AON.getUser(domainName, domain, user);
		Integer[] scopes = AON.getUserScopes(domainName, domain, user, u.getId());
		return AON.getUserCompanyStream(domainName, domain, user, scopes)
				.filter(cp -> isTediCenter(domainName,  cp, snapshot))
				.map(cp -> new TediCompanyResult(cp))
				.collect(Collectors.toCollection(LinkedList::new));
	}

	@Override
	public LinkedList<TediCompanyResult> getCompanies(String domainName, String user, int domain, boolean snapshot, boolean showNotTedi) throws AonCoreException {
		if(showNotTedi) {
			User u =  AON.getUser(domainName, domain, user);
			Integer[] scopes = AON.getUserScopes(domainName, domain, user, u.getId());
			return AON.getUserCompanyStream(domainName, domain, user, scopes)
					.map(cp -> new TediCompanyResult(cp)
						.setTedi(isTediCenter(domainName, cp, snapshot)))
					.collect(Collectors.toCollection(LinkedList::new));
		} else {
			return getCompanies(domainName, user, domain, snapshot);
		}
	}
	
	private Boolean isTediCenter(String domainName, Company company, boolean snapshot) {
		ApplicationParameter apActive = AON.getApplicationParameter(domainName, company.getDomain(), "", snapshot ? AppParam.TEDI_SNAPSHOT_ACTIVE.getValue() : AppParam.TEDI_ACTIVE.getValue());
		return "1".equals(apActive.getValue());
	}

	@Override
	public LinkedList<TediCompanyResult> tediSync(String domainName, String user, int domain, boolean snapshot, boolean showNotTedi) {
		try {
			TEDI.getCompanies(domainName, domain, snapshot, user).forEach(cp -> {
				System.out.println("------------ " + cp.getDocument() + " --------------------");
				System.out.println("");
				User u =  AON.getUser(domainName, domain, user);
				Integer[] scopes = AON.getUserScopes(domainName, domain, user, u.getId());
				AON.getUserCompanyStream(domainName, domain, user, scopes, f -> f.getDocumentProperty().eq(cp.getDocument()))
				.forEach(company -> {
					System.out.println("***************************************");
					System.out.println("");
					System.out.println(company.getDocument() + " - " + company.getName());
					System.out.println(company.getDomain());
					System.out.println("");
					activeAonCompany(domainName, domain, user, snapshot, company);
				});	
			});
			return getCompanies(domainName, user, domain, snapshot, showNotTedi);
		} catch (TediException e) {
			throw new AonCoreException(e);
		}
	}

	@Override
	public void addTediCompany(String domainName, String user, int domain, boolean snapshot, TediCompanyResult company) {
		try { 
			TediCompany tc = TEDI.getRegistry(domainName, domain, user, snapshot, company.getCompany().getDocument());
			
			tc.setActive(true)
				.setDocument(tc.getDocument() != null ? tc.getDocument() : company.getCompany().getDocument())
				.setName(tc.getName() != null ? tc.getName() : company.getCompany().getName());
			if(tc.getAddress() == null) {
				RAddress ra = AON.getRAddres(domainName, domain, user, company.getCompany().getId());
				TediAddress ta = new TediAddress()
						.setAddress(ra.getFullAddress())
						.setCity(ra.getCity())
						.setPostalCode(ra.getZip());
								
				tc.setAddress(ta);
			}
			
			TEDI.createCompany(domainName, domain, snapshot, user, tc);
			activeAonCompany(domainName, domain, user, snapshot, company.getCompany());
		} catch (TediException e) {
			throw new AonCoreException(e);
		}

	}
	
	private void activeAonCompany(String domainName, Integer domainId, String user, boolean snapshot, Company company) {
		ApplicationParameter apActive = AON.getApplicationParameter(domainName, company.getDomain(), "", snapshot ? AppParam.TEDI_SNAPSHOT_ACTIVE.getValue() : AppParam.TEDI_ACTIVE.getValue());
		if(apActive.getId() != null) {
			apActive.setValue("1");
			AON.updateApplicationParameter(domainName, domainId, user, apActive, f -> f.getIdProperty().eq(apActive.getId()));
		} else {
			AON.insertApplicationParameter(domainName, company.getDomain(), user, new ApplicationParameter()
					.setDomain(company.getDomain())
					.setName(snapshot ? AppParam.TEDI_SNAPSHOT_ACTIVE.getValue() : AppParam.TEDI_ACTIVE.getValue())
					.setValue("1"));
		}
	}
	
	
	@Override
	public TediResult fillAttach(String domainName, String user, int domain, boolean snapshot, TediResult result) throws AonCoreException {
		try {
			return TEDI.fillAttach(domainName, domain, snapshot, user, result);
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		}			
	}
*/
}
