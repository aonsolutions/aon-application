package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.tedi.TediService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
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
	public LinkedList<TediResult> getVerifiedInvoices(String domainName, String user, int domain) throws AonCoreException {
		try {
			return TEDI.getVerifiedInvoices(domainName, domain, user);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}
	
	@Override
	public TediResult getInvoice(String domainName, String user, int domain, String uuid) throws AonCoreException {
		try {
			return TEDI.getInvoice(domainName, domain, user, uuid);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public TediResult putInvoice(String domainName, String user, int domain, TediInvoice invoice) throws AonCoreException {
		try {
			return TEDI.putInvoice(domainName, domain, user, invoice);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}

	@Override
	public LinkedList<TediResult> putInvoices(String domainName, String user, int domain, LinkedList<TediResult> results) throws AonCoreException {
		try {
			return TEDI.putInvoices(domainName, domain, user, results);
		} catch ( TediException t) {
			throw new AonCoreException(t);
		}
	}
}
