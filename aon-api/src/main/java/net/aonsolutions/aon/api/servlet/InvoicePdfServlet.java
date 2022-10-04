package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.finance.TbaiConfiguration;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.tbai.TbaiData;


@SuppressWarnings("serial")
@WebServlet(name = "DownloadInvoicePdf", urlPatterns = {"/ms/api/download_invoice_pdf/*",
														"/aon_gwt_aio/download_invoice_pdf/*"
														})
public class InvoicePdfServlet extends AonApiHttpServlet {
	
	private static final Logger LOGGER  = Logger.getLogger(InvoicePdfServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD INVOICE PDF");
		try {
			
			JSONObject json = new JSONObject();
			String idStr = req.getParameter("id");
			if(idStr != null) {
				json.put(IJsonNames.ID, Integer.parseInt(idStr));
				json.put(IJsonNames.SOURCE, req.getParameter("source"));
				String dn = req.getServerName();
				Domain domain = AON.getDomain(dn, 0, "", f-> f.getNameProperty().eq(dn));
				json.put("domain_name", domain.getName());
				json.put("domain_id", domain.getId());
				json.put("login", "");
			} else {
				String param = req.getParameter("json");
				param = new String(Base64.getDecoder().decode(param));
				json = new JSONObject(param); 
			}

			String domainName = json.optString("domain_name");
			Integer domainId = json.optInt("domain_id");
			String login = json.optString("login");
			String source = json.optString("source");
			Integer id = json.optInt(IJsonNames.ID);
			PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domainName, domainId, login, true);
			Invoice invoice = new Invoice();
			if(json.opt(IConstants.ID) != null && json.opt("source") != null && "rawdoc".equalsIgnoreCase(source)) {
				Rawdoc r = AON.getRawdocStream(domainName, domainId, login, 
						f -> f.getDomainProperty().eq(domainId)
						.and(f.getIdProperty().eq(id))).findFirst().orElse(new Rawdoc());
				if(r.getId() != null) json = new JSONObject(r.getJson());
				invoice = InvoiceJSON.fromJSON(json);
				invoice = setPaymethod(domainName, domainId, login, invoice);
			} else if(json.opt(IConstants.ID) != null){
				invoice = AON_SOLUTIONS.getInvoice(domainName, domainId, login, id);
			}
			
			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			Attach logo = new Attach();
			
			if(config.isLogo()) {
				Integer logoId = company.getRegistry().getId();
				logo = AON.getAttach(domainName, domainId, login, f-> f.getAttachModuleProperty().eq(logoId)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			}

			String qrUrl = domainName + "/dip?source=" + source + "&id=" + id;  
			TbaiConfiguration tbai = AON.getTbaiConfiguration(domainName, domainId, login);
			String tbaiId = "";
			if(tbai.isActive()) {
				TbaiData tbaiData = TbaiData.getInstance(tbai);
				String tbaiUrl = tbaiData.getTbaiUrl(domainName, domainId, login, invoice.getId());
				qrUrl = AonStringUtils.isBlank(tbaiUrl) ? qrUrl : tbaiUrl;
				tbaiId = tbaiData.getTbaiId(domainName, domainId, login, invoice.getId());
			}
			PdfMaker.printInvoice(resp.getOutputStream(), company, invoice, config, qrUrl, logo.getData(), tbaiId);
		
			responseFile(resp, "factura", MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	private Invoice setPaymethod(String domainName, Integer domainId, String login, Invoice invoice) {
		for(Integer i = 0; i < invoice.getFinances().size(); i++) {
			Integer paymethod = invoice.getFinances().get(i).getPayMethod();
			if(paymethod != null) {
				PayMethod pm = AON.getPayMethod(domainName, domainId, login, f -> f.getIdProperty().eq(paymethod));
				invoice.getFinances().get(i).setPayMethodName(pm.getName());
			}
		}
		return invoice;
	}
	
}
