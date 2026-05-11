package com.esferalia.aon.gwt.finance.server;

import java.text.MessageFormat;
import java.util.Base64;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.RawdocService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.RawdocParams;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.tedi.TediResult;
import com.esferalia.aon.occam.impl.jooq.RawdocImpl;
import com.esferalia.aon.occam.impl.jooq.dao.AccountingInvoiceDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;
import com.esferalia.aon.occam.server.rawdoc.RawdocUtils;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import net.aonsolutions.aon.api.servlet.SendMailServlet;
import net.aonsolutions.aon.tedi.TEDI;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediException;
import net.aonsolutions.aon.tedi.TediParser;
import solutions.aon.aws.s3.S3;

@WebServlet(name = "Rawdoc Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Rawdoc" })
public class RawdocServiceImpl extends AonStatelessRemoteServiceServlet implements RawdocService {

	private static final long serialVersionUID = 1249978088517559976L;

	@Override
	public LinkedList<Rawdoc> getRawdocs(Occam occam, RawdocParams params, int offset, int limit) throws AonCoreException {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			AonConfiguration config = ConfigurationDAO.getConfiguration(ctx);
			return new RawdocImpl().getRawdocStream(ctx, p -> RawdocUtils.getFilter(p, params),offset,limit)
				.map( r -> {
					if (r.isRecordable() ) {
						TediResult tr = TediParser.toAccountingInvoice(ctx, config, r );
						r.setInvoice( tr.getAccountingInvoice().getInvoice() );
						// InvoiceRecorderDAO.fillMessages( ctx, occam.getDomain(), r.getInvoice() );
					}
					return r;
				}) 
				.collect(Collectors.toCollection(LinkedList::new))
				;
		}
	}

	@Override
	public void delete(Occam occam, Integer rawdocId) {
		AON.rawdocDelete(occam,rawdocId);
	}
	@Override
	public Rawdoc toDraft(Occam occam, Integer rawdocId) throws AonCoreException {
		return AON.rawdocToDraft(occam,rawdocId);
	}

	@Override
	public Rawdoc toRejected(Occam occam, Integer rawdocId, String reason, String email) throws AonCoreException {
		// SEND EMAIL 
		if(email != null && AonStringUtils.isNotBlank(email.trim())) {
			SendMailServlet sms = new SendMailServlet();
			sms.sendEmailInvoiceReject(occam, email);
		}	
		return AON.rawdocToRejected(occam,rawdocId,reason);
	}

	@Override
	public Rawdoc toInbox(Occam occam, Integer rawdocId) throws AonCoreException {
		return AON.rawdocToInbox(occam,rawdocId);
	}
	
	@Override
	public TediResult parse(Occam occam, Integer rawdocId) throws AonCoreException {
		try {
			TediContext tctx = new TediContext()
				.setDomainName(occam.getDomainName())
				.setDomain(occam.getDomain())
				.setUser(occam.getUser());
			TediResult result = TEDI.fromRawdoc(tctx, rawdocId );
			result.getAccountingInvoice()
				.setFromRawdoc(true)
				.setTediParsed(true)
			;
			Attach attach = result.getAccountingInvoice().getAttach();
			if (attach != null && AonStringUtils.equals("RAWDOC_URL",attach.getAttachURL())) {
				String url = getRawdocDataAttachURL(occam,rawdocId);
				result.getAccountingInvoice().getAttach()
					.setAttachURL(url)
					.setData( null );
			}
			return result;
		} catch ( TediException t) {
			t.printStackTrace();
			throw new AonCoreException(t);
		}			
	}

	@Override
	public LinkedList<String> saveToAccounting(Occam occam, LinkedHashSet<Integer> rawdocIds) throws AonCoreException {
		LinkedList<String> ret = new LinkedList<>();
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)){
			AonConfiguration config = ConfigurationDAO.getConfiguration( ctx );
			AonCollectionUtils.stream(rawdocIds)
			.forEach( rawdocId -> {
				try {
					TediResult result = parse(occam, rawdocId);
					AccountingInvoiceDAO.save(ctx, config , result.getAccountingInvoice());					
				} catch ( Exception e) {
					e.printStackTrace();
					ret.add( e.getMessage() );
				}			
			});
		}
		return ret;
	}

	@Override
	public String getS3Url(Rawdoc rawdoc) {
		return S3.getInstance().getURL(rawdoc.getS3Bucket(), rawdoc.getS3Key()).toExternalForm();
	}

	private String getRawdocDataAttachURL(Occam occam, Integer rawdocId) {
		String url = null;
		Rawdoc rawdoc = AON.getRawdocFull(occam, rawdocId)
			.orElseThrow( () -> 
				new AonCoreException(MessageFormat.format("No se ha encontrado el documento {0} en el dominio ( {1} - {2})"
					,rawdocId,occam.getDomain(),occam.getDomainName())));
		
		if (!AonStringUtils.isBlank(rawdoc.getS3Key())) {
			url = S3.getInstance().getURL(rawdoc.getS3Bucket(), rawdoc.getS3Key()).toExternalForm();
		} else if (rawdoc.getData() != null) {
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
			
			String params = "domain="+ occam.getDomain() + "&id=" +  rawdocId;
			params = Base64.getEncoder().encodeToString(params.getBytes());
			url = baseURL.toString() + "/ms/download_rawdoc" 
					+ "/" + occam.getDomainName() 
					+ "/" + occam.getUser() 
					+ "/" +  params;
		}
		System.out.println( "getRawdocDataAttachURL...: " + url);
		return url;
	}
	
	@Override
	public String getUserEmail(Occam occam, String userLogin) {
		Domain domain = AON.getDomain(occam, occam.getDomain());
		Integer[] domains = domain.getParentId() != null
				? new Integer[] {domain.getId(), domain.getParentId()}
				: new Integer[] {domain.getId()};
		
		User user = AON.getUser(occam, f -> 
			f.getDomainProperty().in(domains).and(
			f.getLoginProperty().eq(userLogin)));	
		if(user != null && user.getAuth() != null && AonStringUtils.isNotBlank(user.getAuth().getEmail())) {
			return user.getAuth().getEmail();			
		} else if(user != null && user.getId() != null) {
			MailAccount mailAccount = AON.getMailAccount(occam,
				f -> f.getUserIdProperty().eq(user.getId())
				.and(f.getDomainProperty().eq(user.getDomain().getId())));
			return mailAccount.getEmail();
		}
		return "";
	}
	
	//	************************************************* OLD
	
//	@Override
//	public AccountingInvoice getAccountingInvoice(String domainName, int domainId, String login, String invoiceStr) {
//		JSONObject json = new JSONObject(invoiceStr);
//		return Rawdoc2AccountingInvoice.getAccountingInvoice(domainName, domainId, login, json);
//	}
//	
//	@Override
//	public Boolean processInvoiceFile(String domainName, int domainId, String login, String invoiceStr, Invoice invoice) {
//		JSONObject json = new JSONObject(invoiceStr);
//		JSONObject fileJSON = JsonUtils.getJSONObject(json, IJsonNames.FILE);
//		if(!fileJSON.isEmpty()) {
//			String s3Key = JsonUtils.getString(fileJSON, IJsonNames.S3_KEY);
//			String contentType = JsonUtils.getString(fileJSON, "content_type");
//			try {
//				byte[] data = S3.getInstance().download("aon-upload-post", s3Key);
//				if(data != null) {
//					MimeType mimetype = MimeType.safeValueFromContenType(contentType);
//					Attach attach = new Attach()
//							.setDate(new Date())
//							.setDomain(new Domain().setId(invoice.getDomain()))
//							.setAttachModule(invoice.getId())
//							.setMimeType(mimetype != null ? mimetype : MimeType.PDF)
//							.setAttachType(AttachType.INVOICE)
//							.setType(InvoiceAttachmentType.INVOICE.value())
//							.setData(data);
//
//					AON.insertAttach(domainName, domainId, login, attach);
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		return true;
//	}
//

}
