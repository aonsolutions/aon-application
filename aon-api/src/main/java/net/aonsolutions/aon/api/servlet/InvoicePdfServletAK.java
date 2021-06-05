package net.aonsolutions.aon.api.servlet;

import java.io.IOException;
import java.util.Base64;
import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.in.payroll.pdf.maker.PdfMaker;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.in.payroll.pdf.maker.exception.JsonParseException;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.finance.PrintInvoiceConfiguration;
import com.esferalia.aon.occam.api.model.type.MimeType;


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
			
			//InputStream is = new ByteArrayInputStream(json.toString().getBytes());
//			PdfMaker.printInvoice(resp.getOutputStream(), json, config, null);
			PdfMaker.printDemoInvoice(resp.getOutputStream(), config, null);
//			PdfMaker.printInvoice(resp.getOutputStream(), is, config, null );
			responseFile(req, resp, "factura", MimeType.PDF);
		} catch (CanNotCreatePdfException  | IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	

}
