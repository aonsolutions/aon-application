package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceBreakdown;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.TaxType;


@SuppressWarnings("serial")
@WebServlet(name = "DownloadInvoicePdfAK", urlPatterns = {"/ms/api/download_invoice_pdf_ak/*",
														"/aon_gwt_aio/download_invoice_pdf_ak/*"})
public class InvoicePdfServletAK extends AonApiHttpServlet {

	private static final Logger LOGGER  = Logger.getLogger(InvoicePdfServletAK.class.getName());
	
	public InvoicePdfServletAK() {

	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
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
			
			PdfMaker.printInvoice(resp.getOutputStream(), company, invoice, config, "www.aonsolutions.es", logo.getData(), "");
			
			responseFile(resp, "factura", MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	

}
