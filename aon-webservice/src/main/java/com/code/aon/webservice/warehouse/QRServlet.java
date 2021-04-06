package com.code.aon.webservice.warehouse;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.product.ProductServlet;
import com.code.aon.webservice.util.ToJSON;
import com.code.aon.webservice.warehouse.jooq.DBIncome;
import com.code.aon.webservice.warehouse.jooq.DBPurchase;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Income;

@SuppressWarnings("serial")
@WebServlet(name = "QRServlet", urlPatterns = {"/qr/*",
												"/aon_gwt_aio/qr/*",
												"/udapa/qr/*",
												"/aon_gwt_aio/udapa/qr/*"})
public class QRServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String domainName = req.getServerName();
		Integer carrierPackingId = Integer.parseInt(req.getParameter("cp"));
		Domain domain = AON.getDomain(domainName, 1, "", f -> f.getNameProperty().eq(domainName));
		String login = "";
	
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
		if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())){
			json.put("type", carrierPacking.getType().getName());
			AON.getPurchaseStream(domain.getName(), domain.getId(), login, 
					f -> f.getCarrierPackingProperty().eq(carrierPackingId)
					.and(f.getDomainProperty().eq(domain.getId())))
			.forEach(purchase ->{
				JSONObject purchaseJSON = ToJSON.purchaseToJSON(purchase);
				boolean shippingAlternativeAddressDefined = purchase
						.getShippingAlternativeAddress() != null
						|| purchase.getShippingAlternativeAddress2() != null
						|| purchase.getShippingAlternativeZip() != null
						|| purchase.getShippingAlternativeCity() != null
						|| purchase.getShippingAlternativePhone() != null
						|| purchase.getShippingAlternativeRecipient() != null;	

				JSONObject addressJSON2 = new JSONObject();
				RAddress ra = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(purchase.getAddress()));

				if(shippingAlternativeAddressDefined) {
					addressJSON2 = new JSONObject()
					.put("name", purchase.getShippingAlternativeRecipient())
					.put("address", purchase.getShippingAlternativeAddress() + " " +purchase.getShippingAlternativeAddress2())
					.put("zip", purchase.getShippingAlternativeZip())
					.put("city", purchase.getShippingAlternativeCity())
					.put("province", " ")
					.put("country", " ");
				} else addressJSON2 = ToJSON.raddressToJSON(ra);
				
				addressJSON2.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ? 
						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
				purchaseJSON.put("address",addressJSON2);
				JSONArray details = new JSONArray();
				AON.getPurchaseDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getPurchaseProperty().eq(purchase.getId())
						.and(f.getCarrierPackingProperty().eq(carrierPackingId)))
				.forEach(detail -> {
					JSONObject detailJSON = ToJSON.purchaseDetailToJSON(detail);		
					Optional<OldItem> item = getItem(domain, login, detail.getItem(), detail.getProductId());

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
			json.put("type", carrierPacking.getType().getName());
			AON.getDeliveryStream(domain.getName(), domain.getId(), login, f-> f.getCarrierPackingProperty().eq(carrierPackingId))
			.forEach(delivery -> {
				JSONObject deliveryJSON = ToJSON.deliveryToJSON(delivery);
				boolean shippingAlternativeAddressDefined = delivery
						.getShippingAlternativeAddress() != null
						|| delivery.getShippingAlternativeAddress2() != null
						|| delivery.getShippingAlternativeZip() != null
						|| delivery.getShippingAlternativeCity() != null
						|| delivery.getShippingAlternativePhone() != null
						|| delivery.getShippingAlternativeRecipient() != null;	
				
				RAddress ra = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(delivery.getAddress()));
				JSONObject addressJSON3= ToJSON.raddressToJSON(ra);

				if(shippingAlternativeAddressDefined) {
					addressJSON3 = new JSONObject()
					.put("name", delivery.getShippingAlternativeRecipient())
					.put("address", delivery.getShippingAlternativeAddress() + " " +delivery.getShippingAlternativeAddress2())
					.put("zip", delivery.getShippingAlternativeZip())
					.put("city", delivery.getShippingAlternativeCity())
					.put("province", " ")
					.put("country", " ");
				} else addressJSON3 = ToJSON.raddressToJSON(ra);
				
				addressJSON3.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ?
						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
				deliveryJSON.put("address", addressJSON3);
			
				JSONArray details = new JSONArray();
				AON.getDeliveryDetailStream(domain.getName(), domain.getId(), login, f -> f.getDelivery().eq(delivery.getId()))
				.forEach(detail -> {
					JSONObject detailJSON = ToJSON.deliveryDetailToJSON(detail);
					Optional<OldItem> item = getItem(domain, login, detail.getItem().getId(), detail.getProductId());
					
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
		} 
//		else if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())
//				&& CarrierPackingStatus.FINISHED.equals(carrierPacking.getStatus())){ // TODO RECEPTION
//			json.put("type", "reception");
//			AON.getIncomeStream(domain.getName(), domain.getId(), login, f -> f.getCarrierPackingProperty().eq(carrierPackingId))
//			.forEach(income -> {
//				JSONObject incomeJSON = DBIncome.incomeToJSON(income);
//				RAddress ra = AON.getRAddress(domain.getName(), domain.getId(), login,f -> f.getIdProperty().eq(income.getAddress()));
//				JSONObject addressJSON3 = ToJSON.raddressToJSON(ra);
//				addressJSON3.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ?
//						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
//				incomeJSON.put("address", addressJSON3);
//
//				JSONArray details = new JSONArray();
//				AON.getIncomeDetailStream(domain.getName(), domain.getId(), login, f -> f.getIncomeProperty().eq(income.getId()))
//				.forEach(detail -> {
//					JSONObject detailJSON = DBIncome.incomeDetailToJSON(detail);
//					Optional<Item> item = getItem(domain, login, detail.getItem().getId(), detail.getItem().getProductId());
//					
//					String code = AON.getRItem(domain.getName(), domain.getId(), login, f2 -> 
//						f2.getRegistryProperty().eq(income.getSupplier())
//						.and(f2.getItemProperty().eq(detail.getItem().getId()))).getCode();
//					detailJSON.put("code", code);
//					
//					detailJSON.put("format_tag", item.isPresent() ? item.get().getPackFormatTag().getName() : "");
//					detailJSON.put("measurements", item.isPresent() ? item.get().getPackMeasurement() : 0.0);
//					detailJSON.put("measurements_tag", item.isPresent() ? item.get().getPackMeasurementTag().getName() : "");
//					detailJSON.put("units", item.isPresent() ? item.get().getPackUnits() : 0.0);
//					details.put(detailJSON);	
//				});
//				
//				incomeJSON.put("details", details);
//				array.put(incomeJSON);
//			});
//		}
		json.put("orders", array);
		// --------------- //
		Utils.giveBack(req, resp, json, new JSONObject());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/*		
		{
			  carrier_packing: {
			    id: carrier_packing.id,
			    domain: carrier_packing.domain,
			    tare: 123 ,
			    additional_tare: 123,
			    net: 123,
			    gross: 123,
			  },
			  incomes:[
			    {
			      reference_code: 'XXXXXXX',
			      supplier: orders[x].registry.id,
			      address: orders[x].address,
			      carrier_packing: carrier_packing.id,
			      details: [
			        {
			          item: orders[x].details[y].item,
			          lotable: orders[x].details[y].lotable,
			          lote: 'XXXXXXX',
			          purchase_detail: orders[x].details[y].id,
			          saldar: true,
			          quantity: 123,
			          description: orders[x].details[y].description
			        }
			      ]
			    }
			  ]
			}
*/		
		JSONObject response = new JSONObject();
		try {
			JSONObject json = Utils.getRequestJSON(req);
	
			JSONObject carrierPacking = json.getJSONObject("carrier_packing");
		
			String domainName = req.getServerName();
			Domain domain = AON.getDomain(domainName, carrierPacking.getInt("domain"), "");
			String login = "";
		
			CarrierPacking cp = AON.getCarrierPacking(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(carrierPacking.getInt("id")));
			if(CarrierPackingStatus.FINISHED.equals(cp.getStatus())) {
				throw new Exception("La " + cp.getType().getName() + " está Finalizada.");
			}
			cp.setAdditionalTare(carrierPacking.getDouble("additional_tare"));
			cp.setTare(carrierPacking.getDouble("tare"));
			cp.setNet(carrierPacking.getDouble("net"));
			cp.setGross(carrierPacking.getDouble("gross"));
			cp.setStatus(CarrierPackingStatus.FINISHED);
			AON.updateCarrierPacking(domain.getName(), domain.getId(), login, cp);
			
			JSONArray incomes = json.getJSONArray("incomes");
			Integer workplaceId = AON.getWarehouseStream(domain.getName(), domain.getId(), login,f -> f.getDomainProperty().eq(domain.getId())).findFirst().get().getWorkplace();
			for (int i = 0; i < incomes.length(); i++) {
				JSONObject income = incomes.getJSONObject(i);
				income.put("workplace", workplaceId);
				Optional<Income> opt = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getReferenceCodeProperty().eq(income.getString("reference_code")));
				
				Integer incomeId = opt.isPresent() ? opt.get().getId() : DBIncome.insertIncome(domain, login, income).getInt("id");
				JSONArray details = income.getJSONArray("details");
				for (int j = 0; j < details.length(); j++) {
					JSONObject detail = details.getJSONObject(j);
					Integer itemId = detail.getInt("item");
					if(detail.getBoolean("lotable")) {
						JSONObject item = new JSONObject();
						item.put("item_id", detail.getInt("item"));
						item.put("lote", detail.getString("lote"));
						itemId = ProductServlet.getInstance().insertItem(domain, login, item).getInt("id");
					}
					detail.put("item", itemId);
					detail.put("workplace", workplaceId);
					detail.put("income", incomeId);
					DBIncome.insertIncomeDetail(domain, login, detail);
				
					JSONObject pd = new JSONObject();
					pd.put("delivered", detail.getDouble("quantity"));
					pd.put("id", detail.getInt("purchase_detail"));
					pd.put("saldar", detail.getBoolean("saldar"));
					DBPurchase.updatePurchaseDetail(domain, login, pd);		
				}	
			}		
		} catch (Exception e) {
			response.put("code", 405);
			response.put("message", e.getMessage());
		}
		
		
		resp.setContentType("application/json;charset=UTF-8");
		Utils.addCorsHeader(resp);
		PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
		os.println(response.toString());
		os.flush();
		os.close();
	}

	private Optional<OldItem> getItem(Domain domain, String login, Integer itemId, Integer productId) {
		Optional<OldItem> optional = AON.getItemOptional(domain.getName(), domain.getId(), login, f -> 
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
