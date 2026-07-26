package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.json.invoice.InvoiceJSON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.InvoiceInfo;
import com.esferalia.aon.occam.api.model.finance.PayMethod;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationConfiguration;
import com.esferalia.aon.occam.api.model.invoice.InvoiceCommunicationType;
import com.esferalia.aon.occam.api.model.project.ProjectTas;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.IConstants;
import net.aonsolutions.aon.in.pdf.maker.PdfMaker;
import net.aonsolutions.aon.tbai.TbaiData;


@SuppressWarnings("serial")
@WebServlet(name = "DownloadInvoicePdf", urlPatterns = {"/ms/api/download_invoice_pdf/*",
														"/aon_gwt_aio/download_invoice_pdf/*",
														"/ms/api/download_invoice_pdf_ak/*",
														"/aon_gwt_aio/download_invoice_pdf_ak/*"
														})
public class InvoicePdfServlet extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(InvoicePdfServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		if(req.getRequestURI() != null && req.getRequestURI().contains("download_invoice_pdf_ak")) {
			printSampleInvoice(req, resp);
		} else {
			printInvoice(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}

	private void printInvoice(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD INVOICE PDF");
		try {
			addCorsHeader(resp);
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
				if(r != null && r.getId() != null) json = new JSONObject(r.getJson());
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
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(login);
			String qrUrl = "https://" + domainName + "/dip?d=" + company.getRegistry().getDocument()
						+ "&f=" + AonDateUtils.simpleFormat(invoice.getIssueDate())
						+ "&s=" + invoice.getSeries()
						+ "&n=" + invoice.getNumber()
						+ "&t=" + invoice.getTotal();
			InvoiceCommunicationConfiguration icc = AON.getInvoiceCommunicationConfiguration(occam);
			String tbaiId = "";
			Date expDate = invoice.getExpDate() != null ? invoice.getExpDate() : invoice.getIssueDate();
			if(icc.isTbai(expDate) || icc.isLroe(expDate)) {
				try (CloseableAONContext ctx = AONContext.getAONContext(domainName, domainId, login)) {
					TbaiData tbaiData = TbaiData.getInstance(ctx, icc);
					String tbaiUrl = tbaiData.getTbaiUrl(domainId, invoice.getId());
					qrUrl = AonStringUtils.isBlank(tbaiUrl) ? qrUrl : tbaiUrl;
					tbaiId = tbaiData.getTbaiId(domainId, invoice.getId());
				}
			} else if(icc.isVerifactu(expDate) || icc.isNoVerifactu(expDate)) {
				if (invoice.getCommunicationInfo() != null) {
					InvoiceInfo info = invoice.getCommunicationInfo().get(
						icc.isVerifactu(expDate)
							? InvoiceCommunicationType.VERIFACTU
							: InvoiceCommunicationType.NO_VERIFACTU);
					if (info != null) {
						qrUrl = info.getCheckUrl();
					}
				}
			}
			if(company.getRegistry().getDomain().isGarage()) {
				invoice.detailStream().forEach(d -> {
					ProjectTas pt = AON.getProjectTas(occam, f -> f.getDomainProperty().eq(domainId)
							.and(f.getIdProperty().eq(d.getProject()))).orElse(null);
					if(pt != null)	d.setProjectName(d.getProjectName() + " - KMS. " + pt.getCounter());
				});
			}
			PdfMaker.printInvoice(resp.getOutputStream(), company, icc, invoice, config, qrUrl, logo.getData(), tbaiId);

			responseFile(resp, "Factura " + invoice.getReferenceCode(), MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	private void printSampleInvoice(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD INVOICE PDF AK");
		try {

			String param = req.getParameter("json");
			param = new String(Base64.getDecoder().decode(param));
			JSONObject json = new JSONObject(param);

			String domainName = json.optString("domain_name");
			Integer domainId = json.optInt("domain_id");
			String login = json.optString("login");

			PrintInvoiceConfiguration config = AON_SOLUTIONS.getPrintInvoiceConfiguration(domainName, domainId, login, true);
			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			Attach logo = new Attach();

			if(config.isLogo()) {
				Integer id = company.getRegistry().getId();
				logo = AON.getAttach(domainName, domainId, login, f-> f.getAttachModuleProperty().eq(id)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			}

			Invoice invoice = new Invoice();

			invoice.setSeries("TEST");
			invoice.setNumber(1);

			invoice.getBreakdown().add(new InvoiceBreakdown()
				.setBase(0.0)
				.setPercentage(0.0)
				.setQuota(0.0)
				.setSurcharge(0.0)
				.setSurchargeQuota(0.0)
				.setTaxType(TaxType.VAT));

			invoice.getDetails().add(new InvoiceDetail()
				.setDescription("Test")
				.setDiscountExpression("0.0")
				.setPrice(0.0)
				.setQuantity(1.0)
				.setPrepayment(false));

			InvoiceCommunicationConfiguration icc = new InvoiceCommunicationConfiguration();
			icc.setAdministration(Administration.COMMON_TERRITORY);
			PdfMaker.printInvoice(resp.getOutputStream(), company, icc, invoice, config, "www.aonsolutions.es", logo.getData(), "");

			responseFile(resp, "factura", MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
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
