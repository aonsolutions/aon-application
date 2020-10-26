package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.DataAttachSource;
import com.esferalia.aon.occam.api.model.attachment.DataAttachType;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceStatus;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.api.json.AonInvoiceJSON;
import net.aonsolutions.aon.google.apis.drive.AonDrive;

@SuppressWarnings("serial")
@WebServlet(name = "AonInvoiceServlet", urlPatterns = {"/ms/api/invoice/*"})
public class InvoiceServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(InvoiceServlet.class.getName());
	private static final String INBOX = "inbox";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");
		Domain domain = AON.getDomain(domainName, domainId, "");
		
		String status = req.getParameter(IConstants.STATUS);
		String[] types = req.getParameter(IConstants.TYPE) != null ? req.getParameter(IConstants.TYPE).split(","): null;
		
		System.out.println(status);
		System.out.println(types);
		
		JSONArray jsArray = getInvoices(domain, "api", status, types);

		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, jsArray, new JSONObject());	
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");	
		Domain domain = AON.getDomain(domainName, domainId, "");
		User user = AON_SOLUTIONS.getUser(domain, token);
		JSONObject json = Utils.getRequestJSON(req);
//		Company company = AON.getCompany(domainName, domainId, user.getLogin(), f -> f.getDomainProperty().eq(domainId));
		
		json = setInvoice(domain, user.getLogin(), json);
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, json, new JSONObject());	
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - DELETE METHOD");
		String token = req.getHeader("session_id");
		Integer domainId = AonNumberUtils.toInteger(req.getHeader("domain_id"));
		String domainName = req.getHeader("domain_name");	
		Domain domain = AON.getDomain(domainName, domainId, "");
		JSONObject json = Utils.getRequestJSON(req);
		LinkedList<Integer> invoiceIds = toList(json.optJSONArray(IConstants.ID));
		if(invoiceIds != null) {
			deleteInvoices(domain, "api", invoiceIds);
		}
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, new JSONObject(), new JSONObject());	
	}
	
	public static LinkedList<Integer> toList(JSONArray array) {
	    if(array==null || array.isEmpty())
	        return new LinkedList<>();
	    LinkedList<Integer> list = new LinkedList<>();
	    for(int i=0; i<array.length(); i++) {
	    	list.add(array.optInt(i));
	    }
	    return list;
	}
	
	public static Filter invoiceFilter(InvoiceProperties f, Integer domainId, String status, String[] types) {
    	Filter filter =  f.getDomainProperty().eq(domainId);
    	
    	InvoiceStatus st = getInvoiceStatus(status);
    	if(st != null) {
    		filter = filter.and(f.getStatusProperty().eq(st.value())); 
    	}
    	
    	if(types != null && types.length > 0) {
    		Filter filter2 = f.getTypeProperty().eq(InvoiceType.safeValueOf(types[0]).value());
    		for(Integer i = 1; i < types.length; i++) {
    			filter2 = filter2.or(f.getTypeProperty().eq(InvoiceType.safeValueOf(types[i]).value()));
    		}
    		filter = filter.and(filter2); 
    	}
    	
		return filter;
    }

	private static JSONArray getInvoices(Domain domain, String login, String status, String[] types) {
		JSONArray jsArray = new JSONArray();
		if(isContabilizada(status)) {
			Company company = AON.getCompany(domain.getName(), domain.getId(), "api", f -> f.getDomainProperty().eq(domain.getId()));
			AON_SOLUTIONS.getInvoices(domain.getName(), domain.getId(), "api", f -> invoiceFilter(f, domain.getId(), status, types))
				.forEach(invoice -> jsArray.put(AonInvoiceJSON.toJSON(invoice, company)));
		} else {
			AON_SOLUTIONS.getDataResponseInvoices(domain.getName(), domain.getId(), login,
				f -> f.getSourceProperty().eq(DataResponseSource.INVOICE.value())
				.and(f.getCodeProperty().eq(status))
				.and(f.getDetailVariableProperty().eq("json")))
				.forEach(json -> jsArray.put(json));
		}
		return jsArray;
	}
	
	private void deleteInvoices(Domain domain, String login, LinkedList<Integer> ids) {
		Integer[] idsArray = ids.toArray(new Integer[ids.size()]);
		AON.deleteDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().in(idsArray));
		AON.deleteDataResponse(domain.getName(), domain.getId(), login, f -> f.getIdProperty().in(idsArray));
	}
	
	private static JSONObject setInvoice(Domain domain, String login, JSONObject json) {
		JSONObject file = null;
		if(json.opt("file")!= null) { 
			file = json.opt("invoice") != null ? json.optJSONObject("file") : null;
			json = json.opt("invoice") != null ? json.optJSONObject("invoice"): json;
		}
		Integer id = json.opt("id") !=null ? json.optInt("id") : null;
		String status = json.opt("status") != null ? json.optString("status") : INBOX;

		DataResponse dr = new DataResponse()
				.setDomain(domain.getId())
				.setCode(status)
				.setSource(DataResponseSource.INVOICE)
				.setResponseDate(new Date());
		
		if(id != null) {
			dr = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.INVOICE, f -> f.getIdProperty().eq(id));
			dr.setCode(status);
			AON.updateDataResponse(domain.getName(), domain.getId(), login, dr, f -> f.getIdProperty().eq(id));
		} else {
			dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, dr);
			json.put("id", dr.getId());
		}

		Integer drId = dr.getId();
		if(file != null) {
			String base64 = file.optString("content");
			String type = file.optString("contentType");
			
			Attach attach = new Attach(AttachType.DATA)
				.setDomain(domain)
				.setSource(DataAttachSource.INVOICE.value()) 
				.setSourceId(drId)
				.setType(DataAttachType.REQUEST.value())
				.setDescription("invoice")
				.setMimeType(MimeType.get(type))
				.setData(Base64.getDecoder().decode(base64));
			Integer attachId = AON.insertAttach(domain.getName(), domain.getId(), login, attach);
			attach.setId(attachId);
			DomainGserviceaccount d = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), login);
		    Drive drive = AonDrive.getInstace().serviceInitialize(d);
		    AonDrive.getInstace().sync(drive, new User().setLogin(login), attach, false);
		    String driveId = AON.getAttach(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(attachId), AttachType.DATA).getDriveId();
		    AonDrive.getInstace().setPermission(drive, attach.getDriveId());
		    JSONObject f = new JSONObject();
		    f.put("url", getFile(drive, driveId).getWebContentLink());
		    f.put("type", type);
		    json.put("file", f);
		}
				
		Optional<DataResponseDetail> drdOpt = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(drId).and(f.getDataVariableProperty().eq("json")));
		if(drdOpt.isPresent()) {
			DataResponseDetail drd = drdOpt.get();
			drd.setDataValue(json.toString());
			AON.updateDataResponseDetail(domain.getName(), domain.getId(), login, drd, f -> f.getIdProperty().eq(drd.getId()));
		} else {
			System.out.println("ZZZZ");
			DataResponseDetail drd = new DataResponseDetail()
					.setDomain(domain.getId())
					.setDataResponse(dr.getId())
					.setDataVariable("json")
					.setDataValue(json.toString());
			drd = AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
		}
		
		
		return json;
	}
	
	public static File getFile(Drive drive, String id){
		File file  = new File();
		try {
			file = drive.files().get(id).setFields("webContentLink, webViewLink").execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}
	
	private static InvoiceStatus getInvoiceStatus(String status) {
		InvoiceStatus st = InvoiceStatus.safeValueOf(status);
		if(st == null) {
			if("inbox".equals(status) || "pending".equals(status)
					|| "verified".equals(status)) {
				st = InvoiceStatus.PENDING;
			} else if("accepted".equals(status)) {
				st = InvoiceStatus.SCORED;
			} else if("refused".equals(status)) {
				st = InvoiceStatus.REFUSED; 
			} else if("trash".equals(status)) {
				st = InvoiceStatus.TRASH;
			}
		}
		return st;
	}
	
	private static Boolean isContabilizada(String status) {
		return "accounting".equalsIgnoreCase(status);
	}

}
