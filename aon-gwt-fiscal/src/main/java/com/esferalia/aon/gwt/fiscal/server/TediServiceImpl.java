package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.watson.error.AonCoreException;

import es.translogia.tedi.baloo.TediException;
import es.translogia.tedi.ewok.TediInvoice;
import net.aonsolutions.aon.tedi.TEDI;

@WebServlet(name = "TEDI Servlet", urlPatterns = { "/aon_gwt_fiscal/roms/Tedi" })
public class TediServiceImpl extends AonStatelessRemoteServiceServlet implements TediService {

	private static final long serialVersionUID = -2121272613749054639L;

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
	public TediResult putInvoice(String domainName, String user, int domain, boolean snapshot, TediInvoice invoice) throws AonCoreException {
		try {
			return TEDI.putInvoice(domainName, domain, snapshot, user, invoice);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public LinkedList<TediResult> putInvoices(String domainName, String user, int domain, boolean snapshot, LinkedList<TediResult> results) throws AonCoreException {
		try {
			return TEDI.putInvoices(domainName, domain, snapshot, user, results);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public TediResult validateInvoice(String domainName, String user, int domain, boolean snapshot, TediResult result ) throws AonCoreException {
		return TEDI.validateInvoice(domainName, domain, user, result);
	}
	
	@Override
	public String getInvoiceAttachURL(String domainName, String user, int domain, boolean snapshot, String uuid) throws AonCoreException {
		try {
			return TEDI.getInvoiceAttach(domainName, domain, snapshot, user, uuid);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}
	
	@Override
	public LinkedList<Company> getCompanies(String domainName, String user, int domain, boolean snapshot ) throws AonCoreException {
		User u =  AON.getUser(domainName, domain, user);
		Integer[] scopes = AON.getUserScopes(domainName, domain, user, u.getId());
		return AON.getUserCompanyStream(domainName, domain, user, scopes)
				.filter(cp -> isTediCenter(domainName,  cp, snapshot))
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	private Boolean isTediCenter(String domainName, Company company, boolean snapshot) {
		ApplicationParameter apActive = AON.getApplicationParameter(domainName, company.getDomain(), "", snapshot ? AppParam.TEDI_SNAPSHOT_ACTIVE.getValue() : AppParam.TEDI_ACTIVE.getValue());
		return "1".equals(apActive.getValue());
	}

	@Override
	public LinkedList<Company> tediSync(String domainName, String user, int domain, boolean snapshot) {
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
					ApplicationParameter apActive = AON.getApplicationParameter(domainName, company.getDomain(), "", snapshot ? AppParam.TEDI_SNAPSHOT_ACTIVE.getValue() : AppParam.TEDI_ACTIVE.getValue());
					if(apActive.getId() != null) {
						apActive.setValue("1");
						AON.updateApplicationParameter(domainName, domain, user, apActive, f -> f.getIdProperty().eq(apActive.getId()));
					} else {
						AON.insertApplicationParameter(domainName, company.getDomain(), user, new ApplicationParameter()
								.setDomain(company.getDomain())
								.setName(snapshot ? AppParam.TEDI_SNAPSHOT_ACTIVE.getValue() : AppParam.TEDI_ACTIVE.getValue())
								.setValue("1"));
					}

				});	
			});
			return getCompanies(domainName, user, domain, snapshot);
		} catch (TediException e) {
			throw new AonCoreException(e);
		}
	}
}
