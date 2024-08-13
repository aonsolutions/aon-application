package com.esferalia.aon.gwt.finance.server;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;

import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocDomainData;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.InvoiceAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AttachmentDAO;
import com.esferalia.aon.occam.server.accounting.Rawdoc2AccountingInvoice;
import com.esferalia.aon.watson.error.AonCoreException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;
import solutions.aon.aws.s3.S3;

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
				.getInvoice().setRawdocId(rawdocId);
			result.getAccountingInvoice()
				.setTediParsed(true)
				.getAttach()
				.setAttachURL(url);
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

	@Override
	public AccountingInvoice getAccountingInvoice(String domainName, int domainId, String login, String invoiceStr) {
		JSONObject json = new JSONObject(invoiceStr);
		return Rawdoc2AccountingInvoice.getAccountingInvoice(domainName, domainId, login, json);
	}
	
	@Override
	public Boolean processInvoiceFile(String domainName, int domainId, String login, String invoiceStr, Invoice invoice) {
		JSONObject json = new JSONObject(invoiceStr);
		JSONObject fileJSON = JsonUtils.getJSONObject(json, IJsonNames.FILE);
		if(!fileJSON.isEmpty()) {
			String s3Key = JsonUtils.getString(fileJSON, IJsonNames.S3_KEY);
			String contentType = JsonUtils.getString(fileJSON, "content_type");
			try {
				byte[] data = S3.download("aon-upload-post", s3Key);
				if(data != null) {
					MimeType mimetype = MimeType.safeValueFromContenType(contentType);
					Attach attach = new Attach()
							.setDate(new Date())
							.setDomain(new Domain().setId(invoice.getDomain()))
							.setAttachModule(invoice.getId())
							.setMimeType(mimetype != null ? mimetype : MimeType.PDF)
							.setAttachType(AttachType.INVOICE)
							.setType(InvoiceAttachmentType.INVOICE.value())
							.setData(data);

					AON.insertAttach(domainName, domainId, login, attach);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
}
