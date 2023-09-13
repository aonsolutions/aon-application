package net.aonsolutions.aon.api.servlet.invoice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.code.aon.facturae.nuevo.FacturaeWriter2;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Workplace;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.sign.FacturaeSigner;
import net.aonsolutions.aon.sign.exception.AonSignerException;

@SuppressWarnings("serial")
@WebServlet(name = "FaceServlet", urlPatterns = {"/ms/api/face/*",
												"/aon_gwt_aio/face/*"})
public class FaceServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(FaceServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		AonApiData api = initialize(req, false);
		try {
			String idStr = req.getParameter("id");
			Integer id = Integer.parseInt(idStr);
			String domainName = req.getParameter(IJsonNames.DOMAIN_NAME);
			String domainIdStr = req.getParameter(IJsonNames.DOMAIN_ID);
			Integer domainId = Integer.parseInt(domainIdStr);
			String login = "";

			Domain domain = new Domain()
				.setName(domainName)
				.setId(domainId);
			
			User user = new User()
					.setLogin(login);
		
			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			Invoice invoice = AON_SOLUTIONS.getInvoice(domainName, domainId, login, id);
			Workplace workplace = new Workplace();
			if(!invoice.getDetails().isEmpty() && invoice.getDetails().get(0).getWorkplace() != null &&
					invoice.getDetails().get(0).getWorkplace().getId() != null) {
				Integer wId = invoice.getDetails().get(0).getWorkplace().getId();
				workplace = AON.getWorkplace(domainName, domainId, login, f -> f.getIdProperty().eq(wId));				
			} else {
				workplace = AON.getWorkplace(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId)
						.and(f.getActiveProperty().eq((byte)1)));
			}

			FacturaeWriter2 facturae = new FacturaeWriter2(domain, user, company, workplace, invoice);
			byte[] data = facturae.generate();
			
			try {
				Certificate certificate = checkCertificate(api);
				byte[] signedData = FacturaeSigner.getInstance().sign(certificate, data);
				responseFile(resp, "FACTURAE", signedData, MimeType.XML);
			} catch (AonSignerException e) {
				e.printStackTrace();
				responseFile(resp, "FACTURAE", data, MimeType.XML);
			}
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}

	
	public void responseFile(HttpServletResponse resp, Attach attach) throws IOException {
		ByteArrayInputStream is =  new ByteArrayInputStream(attach.getData());
		responseFile(resp, attach.getDescription(), is, attach.getMimeType());
	}
	
	public void responseFile(HttpServletResponse resp, File file, MimeType mimetype ) throws IOException {
		FileInputStream is =  new FileInputStream(file);
		responseFile(resp, file.getName(), is, mimetype);
	}
	
	public void responseFile(HttpServletResponse resp, String filename, byte[] file, MimeType mimetype ) throws IOException {
		ByteArrayInputStream is =  new ByteArrayInputStream(file);
		responseFile(resp, filename, is, mimetype);
	}
	
	public void responseFile(HttpServletResponse resp, String filename, byte[] file, MimeType mimetype, String contentDisposition) throws IOException {
		ByteArrayInputStream is =  new ByteArrayInputStream(file);
		responseFile(resp, filename, is, mimetype, contentDisposition);
	}

	public void responseFile(HttpServletResponse resp, String filename, InputStream is, MimeType mimetype) throws IOException {
		responseFile(resp, filename, is, mimetype, "inline");
	}
	
	public void responseFile(HttpServletResponse resp, String filename, InputStream is, MimeType mimetype, String contentDisposition) throws IOException {
		addCorsHeader(resp);
        resp.setContentType(mimetype.getName());
		resp.setHeader("Content-disposition", contentDisposition + "; filename=\"" + filename + "." + mimetype.getExtension() +"\";");
		AonIOUtils.copy(is, resp.getOutputStream());
		resp.flushBuffer();
		is.close();
	}
	
	public void responseFile(HttpServletResponse resp, String filename, MimeType mimetype) throws IOException {
		addCorsHeader(resp);
        resp.setContentType(mimetype.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + filename + "." + mimetype.getExtension() +"\";");
		resp.flushBuffer();
	}
	
    protected void addCorsHeader(HttpServletResponse response) {
    	response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "*");
        response.addHeader("Access-Control-Max-Age", "1728000");
    }
    
	public void error(HttpServletRequest req, HttpServletResponse resp, Exception e) {
		e.printStackTrace();
		resp.setStatus(400);
		JSONObject json = new JSONObject();
		String className =  e.getClass().getSimpleName();
		String message = e.getMessage()!= null ? e.getMessage() : className;
		json.put("message", message);
		json.put("type", "error");
		json.put("class_name", className);
		addCorsHeader(resp);
		giveBack(req, resp, json, new JSONObject());
	}
	
	protected void giveBack(HttpServletRequest req, HttpServletResponse resp,
			Object object, JSONObject meta) {
		try {
			String js = req.getParameter("callback");
			if(js != null){
				resp.setContentType("application/javascript; charset=utf-8");     
				PrintWriter out = resp.getWriter();
				out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
				out.flush();
			} else {
				resp.setContentType("application/json");     
				PrintWriter out = resp.getWriter();
				out.print(object);
				out.flush();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
}
