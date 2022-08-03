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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;


@SuppressWarnings("serial")
@WebServlet(name = "DownloadPackagingPdf", urlPatterns = {"/ms/api/download_packaging_pdf/*"})
public class PackagingPdfServlet extends AonApiHttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API DOWNLOAD PACKAGING PDF");
		try {
			String param = req.getParameter("json");
			param = new String(Base64.getDecoder().decode(param));
			JSONObject json = new JSONObject(param); 
			
			String domainName = json.optString("domain_name");
			Integer domainId = json.optInt("domain_id");
			String login = json.optString("login");
			
			Domain domain = new Domain().setName(domainName).setId(domainId);
			Integer itemId = json.optInt(IJsonNames.ITEM);
			Integer containerId = json.optInt(IJsonNames.CONTAINER);
			Double quantity = json.optDouble(IJsonNames.QUANTITY);
			String barcode = json.optString(IJsonNames.BARCODE);
			
			Item item = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(itemId)));
			Item container = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(containerId)));
			
			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			
			Integer logoId = company.getRegistry().getId();
			Attach logo = AON.getAttach(domainName, domainId, login, f-> f.getAttachModuleProperty().eq(logoId)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);

			String ean128 = "(01)" + barcode + "(15)" + AonDateUtils.format(item.getSerialDate(), "yyMMdd") + "(10)" + item.getSerialNumber();
			String sscc = "084370167341234566";
			PdfMaker.printPackaging(resp.getOutputStream(), company, item, logo.getData(), barcode, quantity, ean128, sscc);
			
			responseFile(resp, "packaging", MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	private static final Logger LOGGER  = Logger.getLogger(PackagingPdfServlet.class.getName());
	
}
