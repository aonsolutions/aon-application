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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


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

			Integer containerId = json.optInt(IJsonNames.CONTAINER);
			Double quantity = json.optDouble(IJsonNames.QUANTITY);
			String barcode = json.optString(IJsonNames.BARCODE);
	
			Integer itemId;
			if(containerId != null) {
				ItemComposition ic = AON.getItemCompositionStream(domain, login, f -> f.getItemProperty().eq(containerId))
						.findFirst().orElse(new ItemComposition());
				itemId = ic.getCompositionItemId();
				quantity = ic.getQuantity();
			} else itemId = json.optInt(IJsonNames.ITEM);
			
			Item item = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(itemId)));
			Item container = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(containerId)));
			
			if(AonStringUtils.isBlank(barcode)) {
				Item base = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId)
						.and(f.getProductProperty().eq(item.getProduct().getId()))
						.and(f.getBarcodeProperty().isNotNull()));
				barcode = base.getBarcode();
			}

			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			
			Integer logoId = company.getRegistry().getId();
			Attach logo = AON.getAttach(domainName, domainId, login, f-> f.getAttachModuleProperty().eq(logoId)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);

			Double boxQuantity = quantity;
			if(item.getStockUnitTag().getId().equals(item.getPackMeasurementTag().getId())) {
				boxQuantity = quantity / item.getPackMeasurement();
				boxQuantity = boxQuantity / item.getPackUnits().doubleValue();	
			} else if(item.getStockUnitTag().getId().equals(item.getPackUnitsTag().getId())) {
				boxQuantity = quantity / item.getPackUnits().doubleValue();	
			}    
			boxQuantity = AonMathUtils.round(boxQuantity);
//			String separator = "\u001d";
//			char separator = 29; 
			String separator = "\312";

//			String ean128 = "(01)" + barcode + "(15)" + AonDateUtils.format(item.getSerialDate(), "yyMMdd") + "(10)" + item.getSerialNumber();

			String boxQ = toParChar(Integer.toString(boxQuantity.intValue()));
			String serialNumber = toParChar(item.getSerialNumber());
			String ean128 = "(02)" + barcode + "(37)" + boxQ + separator + "(15)" + AonDateUtils.format(item.getSerialDate(), "yyMMdd") + "(10)" + serialNumber ;
			String sscc = container.getSerialNumber();
				
			PdfMaker.printPackaging(resp.getOutputStream(), company, item, logo.getData(), barcode, boxQuantity, ean128, sscc);

			responseFile(resp, "packaging", MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	private String toParChar(String text) {
	    if(AonNumberUtils.isPar(text.length())) return text;
	    else return "0" + text;
    }
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	private static final Logger LOGGER  = Logger.getLogger(PackagingPdfServlet.class.getName());
}
