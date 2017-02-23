package com.code.aon.webservice.warehouse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "packinglistProjection", urlPatterns = {"/aon_gwt_aio/download_packing_list/*"})
public class PackingListDownload extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	       
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		String carrierPackingIdStr = parameters.get("id");
		Integer carrierPackingId = Integer.parseInt(carrierPackingIdStr);
		
		// GENERATE JSON //
		
		CarrierPacking carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, carrierPackingId);
		JSONObject json = new JSONObject();
		json.put("carrier_packing", ToJSON.carrierPackingToJSON(carrierPacking));
		Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), login);
		json.put("address", ToJSON.raddressToJSON(
			AON.getRAddres(domain.getName(), domain.getId(), login, company.getId())
		));
		JSONArray array = new JSONArray();
		if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())){
			AON.getPurchaseStream(domain.getName(), domain.getId(), login, 
					f -> f.getCarrierPackingProperty().eq(carrierPackingId)
					.and(f.getDomainProperty().eq(domain.getId())))
			.forEach(purchase ->{
				JSONObject purchaseJSON = ToJSON.purchaseToJSON(purchase);
				purchaseJSON.put("address", ToJSON.raddressToJSON(
					AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(purchase.getAddress()))
				));
				
				JSONArray details = new JSONArray();
				AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getPurchaseProperty().eq(purchase.getId())
						.and(f.getCarrierPackingProperty().eq(carrierPackingId)))
				.forEach(detail -> {
					JSONObject detailJSON = ToJSON.purchaseDetailToJSON(detail);
					Item item = AON.getItem(domain.getName(), domain.getId(), login, detail.getItem());
					detailJSON.put("format_tag", item.getPackFormatTag()!= null ? item.getPackFormatTag().getName() : "");
					detailJSON.put("measurements", item.getPackMeasurement() != null ? item.getPackMeasurement() : "");
					detailJSON.put("units_tag", item.getPackUnitsTag()!= null ? item.getPackUnitsTag().getName() : "");
					detailJSON.put("units", item.getPackUnits() != null ? item.getPackUnits() : "");
					details.put(detailJSON);	
				});
				
				purchaseJSON.put("details", details);
				array.put(purchaseJSON);
			});
		} else  if(CarrierPackingType.WAYBILL.equals(carrierPacking.getType())){
			AON.getDeliveryStream(domain.getName(), domain.getId(), login, f-> f.getCarrierPackingProperty().eq(carrierPackingId))
			.forEach(delivery -> {
				JSONObject deliveryJSON = ToJSON.deliveryToJSON(delivery);
				deliveryJSON.put("address", ToJSON.raddressToJSON(
					AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(delivery.getAddress()))
				));
			
				JSONArray details = new JSONArray();
				AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> f.getDelivery().eq(delivery.getId()))
				.forEach(detail -> {
					JSONObject detailJSON = ToJSON.deliveryDetailToJSON(detail);
					Item item = AON.getItem(domain.getName(), domain.getId(), login, detail.getItem().getId());
					detailJSON.put("format_tag", item.getPackFormatTag()!= null ? item.getPackFormatTag().getName() : "");
					detailJSON.put("measurements", item.getPackMeasurement() != null ? item.getPackMeasurement() : "");
					detailJSON.put("units_tag", item.getPackUnitsTag()!= null ? item.getPackUnitsTag().getName() : "");
					detailJSON.put("units", item.getPackUnits() != null ? item.getPackUnits() : "");
					details.put(detailJSON);	
				});
				
				deliveryJSON.put("details", details);
				array.put(deliveryJSON);
			});
		}
		json.put("orders", array);
		// --------------- //
		
		Attach attach = AON.getAttach(domain.getName(), domain.getId(), login, 
					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
					.and(f.getDomainProperty().eq(domain.getId())),
				AttachType.REGISTRY);
		
		File file = PackingList.createPdf(json, attach.getData());
		
        Utils.addCorsHeader(resp);
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
	}
	

}
