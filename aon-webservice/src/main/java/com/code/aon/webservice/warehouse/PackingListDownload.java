package com.code.aon.webservice.warehouse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

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
import com.code.aon.webservice.warehouse.jooq.DBIncome;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
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
		JSONObject cpJSON = ToJSON.carrierPackingToJSON(carrierPacking);
		RAddress addr = AON.getRAddres(domain.getName(), domain.getId(), login, carrierPacking.getCarrier());
		cpJSON.put("address", ToJSON.raddressToJSON(addr));
		cpJSON.put("document", AON.getCarrier(domain.getName(), domain.getId(), login, carrierPacking.getCarrier()).getDocument() != null ?
				AON.getCarrier(domain.getName(), domain.getId(), login, carrierPacking.getCarrier()).getDocument() : "");
		json.put("carrier_packing", cpJSON);
		Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), login);
		JSONObject addressJSON = ToJSON.raddressToJSON(
			AON.getRAddres(domain.getName(), domain.getId(), login, company.getId()));
		addressJSON.put("document", company.getDocument() != null ? 
				company.getDocument() : "");
		json.put("address", addressJSON);
		JSONArray array = new JSONArray();
		if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())
			&& !CarrierPackingStatus.FINISHED.equals(carrierPacking.getStatus())){
			json.put("type", carrierPacking.getType());
			AON.getPurchaseStream(domain.getName(), domain.getId(), login, 
					f -> f.getCarrierPackingProperty().eq(carrierPackingId)
					.and(f.getDomainProperty().eq(domain.getId())))
			.forEach(purchase ->{
				JSONObject purchaseJSON = ToJSON.purchaseToJSON(purchase);
				RAddress ra = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(purchase.getAddress()));
				JSONObject addressJSON2 = ToJSON.raddressToJSON(ra);
				addressJSON2.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ? 
						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
				purchaseJSON.put("address",addressJSON2);
				JSONArray details = new JSONArray();
				AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getPurchaseProperty().eq(purchase.getId())
						.and(f.getCarrierPackingProperty().eq(carrierPackingId)))
				.forEach(detail -> {
					JSONObject detailJSON = ToJSON.purchaseDetailToJSON(detail);		
					Optional<Item> item = getItem(domain, login, detail.getItem(), detail.getProductId());

					String code = AON.getRItem(domain.getName(), domain.getId(), login, f2 -> 
						f2.getRegistryProperty().eq(purchase.getSupplier())
						.and(f2.getItemProperty().eq(detail.getItem()))).getCode();
					detailJSON.put("code", code);
					
					detailJSON.put("format_tag", item.isPresent() ? item.get().getPackFormatTag().getName() : "");
					detailJSON.put("measurements", item.isPresent() ? item.get().getPackMeasurement() : 0.0);
					detailJSON.put("measurements_tag", item.isPresent() ? item.get().getPackMeasurementTag().getName() : "");
					detailJSON.put("units", item.isPresent() ? item.get().getPackUnits() : 0.0);
					details.put(detailJSON);	
				});
				
				purchaseJSON.put("details", details);
				array.put(purchaseJSON);
			});
		} else  if(CarrierPackingType.WAYBILL.equals(carrierPacking.getType())){
			json.put("type", carrierPacking.getType());
			AON.getDeliveryStream(domain.getName(), domain.getId(), login, f-> f.getCarrierPackingProperty().eq(carrierPackingId))
			.forEach(delivery -> {
				JSONObject deliveryJSON = ToJSON.deliveryToJSON(delivery);
				RAddress ra = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(delivery.getAddress()));
				JSONObject addressJSON3= ToJSON.raddressToJSON(ra);
				addressJSON3.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ?
						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
				deliveryJSON.put("address", addressJSON3);
			
				JSONArray details = new JSONArray();
				AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> f.getDelivery().eq(delivery.getId()))
				.forEach(detail -> {
					JSONObject detailJSON = ToJSON.deliveryDetailToJSON(detail);
					Optional<Item> item = getItem(domain, login, detail.getItem().getId(), detail.getProductId());
					
					String code = AON.getRItem(domain.getName(), domain.getId(), login, f2 -> 
						f2.getRegistryProperty().eq(delivery.getCustomer())
						.and(f2.getItemProperty().eq(detail.getItem().getId()))).getCode();
					detailJSON.put("code", code);
					
					detailJSON.put("format_tag", item.isPresent() ? item.get().getPackFormatTag().getName() : "");
					detailJSON.put("measurements", item.isPresent() ? item.get().getPackMeasurement() : 0.0);
					detailJSON.put("measurements_tag", item.isPresent() ? item.get().getPackMeasurementTag().getName() : "");
					detailJSON.put("units", item.isPresent() ? item.get().getPackUnits() : 0.0);
					details.put(detailJSON);	
				});
				
				deliveryJSON.put("details", details);
				array.put(deliveryJSON);
			});
		} else if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())
				&& CarrierPackingStatus.FINISHED.equals(carrierPacking.getStatus())){ // TODO RECEPTION
			json.put("type", "reception");
			AON.getIncomeStream(domain.getName(), domain.getId(), login, f -> f.getCarrierPackingProperty().eq(carrierPackingId))
			.forEach(income -> {
				JSONObject incomeJSON = DBIncome.incomeToJSON(income);
				RAddress ra = AON.getRAddress(domain.getName(), domain.getId(), login,f -> f.getIdProperty().eq(income.getAddress()));
				JSONObject addressJSON3 = ToJSON.raddressToJSON(ra);
				addressJSON3.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ?
						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
				incomeJSON.put("address", addressJSON3);

				JSONArray details = new JSONArray();
				AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> f.getIncomeProperty().eq(income.getId()))
				.forEach(detail -> {
					JSONObject detailJSON = DBIncome.incomeDetailToJSON(detail);
					Optional<Item> item = getItem(domain, login, detail.getItem().getId(), detail.getItem().getProductId());
					
					String code = AON.getRItem(domain.getName(), domain.getId(), login, f2 -> 
						f2.getRegistryProperty().eq(income.getSupplier())
						.and(f2.getItemProperty().eq(detail.getItem().getId()))).getCode();
					detailJSON.put("code", code);
					
					detailJSON.put("format_tag", item.isPresent() ? item.get().getPackFormatTag().getName() : "");
					detailJSON.put("measurements", item.isPresent() ? item.get().getPackMeasurement() : 0.0);
					detailJSON.put("measurements_tag", item.isPresent() ? item.get().getPackMeasurementTag().getName() : "");
					detailJSON.put("units", item.isPresent() ? item.get().getPackUnits() : 0.0);
					details.put(detailJSON);	
				});
				
				incomeJSON.put("details", details);
				array.put(incomeJSON);
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
	
	
	private Optional<Item> getItem(Domain domain, String login, Integer itemId, Integer productId) {
		Optional<Item> optional = AON.getItemOptional(domain.getName(), domain.getId(), login, f -> 
			f.getIdProperty().eq(itemId)
			.and(f.getPackFormatTagProperty().isNotNull())
			.and(f.getPackMeasurementTagProperty().isNotNull())
			.and(f.getPackMeasurementProperty().isNotNull())
			.and(f.getPackUnitsProperty().isNotNull()));
		return optional.isPresent() ? optional :  AON.getItemOptional(domain.getName(), domain.getId(), login, f -> 
			f.getProductProperty().eq(productId)
			.and(f.getPackFormatTagProperty().isNotNull())
			.and(f.getPackMeasurementTagProperty().isNotNull())
			.and(f.getPackMeasurementProperty().isNotNull())
			.and(f.getPackUnitsProperty().isNotNull()));
	}

}
