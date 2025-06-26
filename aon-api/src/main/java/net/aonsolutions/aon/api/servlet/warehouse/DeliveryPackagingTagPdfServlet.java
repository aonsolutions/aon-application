package net.aonsolutions.aon.api.servlet.warehouse;

import java.io.IOException;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import net.aonsolutions.aon.in.pdf.maker.PdfMaker;
import net.aonsolutions.aon.in.pdf.maker.warehouse.PackagingTag;
import net.aonsolutions.aon.in.pdf.maker.warehouse.PackagingTagDetail;

import org.json.JSONObject;


import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.JsonUtils;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.CompanyFull;
import com.esferalia.aon.occam.api.model.registry.CustomerFull;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


@SuppressWarnings("serial")
@WebServlet(name = "DeliveryPackagingTagPdf", urlPatterns = {"/ms/api/deliveryPackagingTag/*"})
public class DeliveryPackagingTagPdfServlet extends AonApiHttpServlet {
	
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

			Integer deliveryId = JsonUtils.getInteger(json, IJsonNames.DELIVERY);
			Delivery delivery = AON.getDelivery(domain, login, f -> f.getIdProperty().eq(deliveryId));
			
			Integer containerId = JsonUtils.getInteger(json, IJsonNames.CONTAINER);
			Double quantity = JsonUtils.getDouble(json, IJsonNames.QUANTITY);
			String barcode = JsonUtils.getString(json, IJsonNames.BARCODE);
			Integer itemId = JsonUtils.getInteger(json, IJsonNames.ITEM);	
			
			List<ItemComposition> compositionList = new LinkedList<>();
			if(containerId != null) {
				compositionList = AON.getItemCompositionStream(domain, login, f -> f.getItemProperty().eq(containerId)).toList();
				ItemComposition ic = compositionList.stream().findFirst().orElse(new ItemComposition());
				itemId = ic.getCompositionItemId();
				quantity = ic.getQuantity();
			} 

			CompanyFull company = AON.getCompanyFull(domainName, domainId, login);
			Integer logoId = company.getRegistry().getId();
			Attach logo = AON.getAttach(domainName, domainId, login, f-> f.getAttachModuleProperty().eq(logoId)
					.and(f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())), AttachType.REGISTRY);
			
			Item container = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(containerId)));	
			String sscc = container.getSerialNumber();

			boolean onlyOneItem = compositionList.stream().map(m -> m.getCompositionItemId()).distinct().count() == 1;
			if(isMercadona(delivery.getCustomer()) && onlyOneItem) {
				Integer itemIdAux = itemId;
				Item item = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId).and(f.getIdProperty().eq(itemIdAux)));				
				if(AonStringUtils.isBlank(barcode)) {
					Item base = AON.getItem(domain, login, f -> f.getDomainProperty().eq(domainId)
							.and(f.getProductProperty().eq(item.getProduct().getId()))
							.and(f.getBarcodeProperty().isNotNull()));
					barcode = base.getBarcode();
				}
				
				Double boxQuantity = quantity;
				if(item.getStockUnitTag().getId().equals(item.getPackMeasurementTag().getId())) {
					boxQuantity = quantity / item.getPackMeasurement();
					boxQuantity = boxQuantity / item.getPackUnits().doubleValue();	
				} else if(item.getStockUnitTag().getId().equals(item.getPackUnitsTag().getId())) {
					boxQuantity = quantity / item.getPackUnits().doubleValue();	
				}    
				boxQuantity = AonMathUtils.round(boxQuantity);
				
//				String separator = "\u001d";
//				char separator = 29; 
				String separator = "\312";

//				String ean128 = "(01)" + barcode + "(15)" + AonDateUtils.format(item.getSerialDate(), "yyMMdd") + "(10)" + item.getSerialNumber();

				String boxQ = toParChar(Integer.toString(boxQuantity.intValue()));
				String serialNumber = toParChar(item.getSerialNumber());
				String ean128 = "(02)" + barcode + "(37)" + boxQ + separator + "(15)" + AonDateUtils.format(item.getSerialDate(), "yyMMdd") + "(10)" + serialNumber ;

				PackagingTagDetail ptd = new PackagingTagDetail();
				ptd.setBarcode(barcode)
					.setEan128(ean128)
					.setSscc(sscc)
					.setItem(item)
					.setQuantity(boxQuantity)
					.setCustomer(null)
					.setCarrier(null);
				PackagingTag packagingTag = new PackagingTag()
						.setCompany(company)
						.setLogo(logo.getData())
						.addDetail(ptd);
//				PdfMaker.printPackaging(resp.getOutputStream(), company, item, logo.getData(), barcode, boxQuantity, ean128, sscc);
				PdfMaker.printMercadonaPackagingTag(resp.getOutputStream(), packagingTag);
			} else {
				CustomerFull customer = AON.getCustomerFull(domain.getName(), domain.getId(), login, delivery.getCustomer().getId());
				Carrier carrier = AON.getCarrier(domain, login, delivery.getCarrier());
				PackagingTagDetail ptd = new PackagingTagDetail();
				ptd.setSscc(sscc)
					.setCustomer(customer)
					.setCarrier(carrier);
				PackagingTag packagingTag = new PackagingTag()
						.setCompany(company)
						.setLogo(logo.getData())
						.addDetail(ptd);
				PdfMaker.printGenericPackagingTag(resp.getOutputStream(), packagingTag);
			}
			responseFile(resp, "packaging", MimeType.PDF);
		} catch (IOException e) {
			error(req, resp, e);
		}
	}

	private String toParChar(String text) {
	    if(AonNumberUtils.isPar(text.length())) return text;
	    else return "0" + text;
    }
	
	private boolean isMercadona(Customer customer) {
		return "A46103834".equalsIgnoreCase(customer.getDocument());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	private static final Logger LOGGER  = Logger.getLogger(DeliveryPackagingTagPdfServlet.class.getName());
}
