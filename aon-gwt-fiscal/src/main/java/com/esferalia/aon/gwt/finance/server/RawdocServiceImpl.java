package com.esferalia.aon.gwt.finance.server;

import java.util.Base64;
import java.util.LinkedList;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;

@WebServlet(name = "Rawdoc Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Rawdoc" })
public class RawdocServiceImpl extends AonStatelessRemoteServiceServlet implements RawdocService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public LinkedList<Rawdoc> getRawdocs(String domainName, int domain, String user, RawdocParams params, int offset,
			int limit) throws AonCoreException {
		return AON.getRawdocs(domainName, domain,user, params, offset, limit );
	}
	
	@Override
	public LinkedList<RawdocDomainData> getDomainData(String domainName, int domain, String user, int searchDomain) throws AonCoreException {
		return AON.getRawdocDomainData(domainName, domain,user,searchDomain);
	}
	
	@Override
	public TediResult parse(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException {
		String url = null;
		if (AON.rawdocHasData(domainName, domain,user,rawdocId)) {
//			StringBuilder baseURL = new StringBuilder();
//			baseURL.append( getThreadLocalRequest().getContextPath() );
// ---------------------
			HttpServletRequest req = getThreadLocalRequest();
			String serverName = req.getServerName();
			int serverPort = req.getServerPort();
			StringBuilder baseURL = new StringBuilder();
			if (serverPort != 80 && serverPort != 443) {
				String scheme = req.getScheme();
				baseURL
					.append(scheme).append(":")
					.append("//").append(serverName)
					.append(":").append(serverPort);
			}
			baseURL.append( getThreadLocalRequest().getContextPath() );
//----------------------
			String params = "domain="+ domain + "&id=" +  rawdocId;
			params = Base64.getEncoder().encodeToString(params.getBytes());
			url = baseURL.toString() + "/ms/download_rawdoc" 
					+ "/" + domainName 
					+ "/" + user 
					+ "/" +  params;
		}
		try {
			TediContext tctx = new TediContext()
				.setDomainName(domainName)
				.setDomain(domain)
				.setUser(user);
			TediResult result = TEDI.fromRawdoc(tctx, rawdocId );
			result.getAccountingInvoice()
				.setFromRawdoc(true)
				.setTediParsed(true)
				.getAttach().setAttachURL(url);
			return result;
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		}			
	}
	@Override
	public void delete(String domainName, int domain, String user, Integer rawdocId) {
		AON.rawdocDelete(domainName, domain,user,rawdocId);
	}
	@Override
	public void toDraft(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException {
		AON.rawdocToDraft(domainName, domain,user,rawdocId);
	}

	@Override
	public void toRejected(String domainName, int domain, String user, Integer rawdocId, String reason) throws AonCoreException {
		AON.rawdocToRejected(domainName, domain,user,rawdocId,reason);
	}

	@Override
	public void toInbox(String domainName, int domain, String user, Integer rawdocId) throws AonCoreException {
		AON.rawdocToInbox(domainName, domain,user,rawdocId);
	}
}
