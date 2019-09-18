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

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.product.ProductServlet;
import com.code.aon.webservice.util.ToJSON;
import com.code.aon.webservice.warehouse.jooq.DBIncome;
import com.code.aon.webservice.warehouse.jooq.DBPurchase;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "QRServlet", urlPatterns = {"/udapa/qr/*",
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

		JSONArray array = new JSONArray();
		if(CarrierPackingType.SHIPMENT_REQUEST.equals(carrierPacking.getType())
			&& !CarrierPackingStatus.FINISHED.equals(carrierPacking.getStatus())){
			json.put("type", carrierPacking.getType().getName());
			AON.getPurchaseStream(domain.getName(), domain.getId(), login, 
					f -> f.getCarrierPackingProperty().eq(carrierPackingId)
					.and(f.getDomainProperty().eq(domain.getId())))
			.forEach(purchase ->{
				JSONObject purchaseJSON = purchaseToJSON(purchase);
				boolean shippingAlternativeAddressDefined = purchase
						.getShippingAlternativeAddress() != null
						|| purchase.getShippingAlternativeAddress2() != null
						|| purchase.getShippingAlternativeZip() != null
						|| purchase.getShippingAlternativeCity() != null
						|| purchase.getShippingAlternativePhone() != null
						|| purchase.getShippingAlternativeRecipient() != null;	

				JSONObject registryJSON2 = new JSONObject();
				RAddress ra = AON.getRAddress(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(purchase.getAddress()));

				registryJSON2 = new JSONObject()
						.put("id", purchase.getSupplier())
						.put("name", purchase.getSupplierName())
						.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ? 
						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
						
				if(shippingAlternativeAddressDefined) {
					JSONObject addressJSON2 = new JSONObject();
					addressJSON2.put("address", purchase.getShippingAlternativeAddress() + " " +purchase.getShippingAlternativeAddress2())
					.put("zip", purchase.getShippingAlternativeZip())
					.put("city", purchase.getShippingAlternativeCity())
					.put("province", " ")
					.put("country", " ");
					registryJSON2.put("address", addressJSON2);
				} else registryJSON2.put("address", raddressToJSON(ra));
				purchaseJSON.put("registry", registryJSON2);
	
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
				JSONObject registryJSON3 = new JSONObject();
				registryJSON3
					.put("id", delivery.getCustomer())
					.put("name", delivery.getCustomerName())
					.put("document", AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() != null ?
						AON.getRegistry(domain.getName(), domain.getId(), login,ra.getRegistry()).getDocument() : "");
				
				if(shippingAlternativeAddressDefined) {
					JSONObject addressJSON3 = new JSONObject()
					.put("address", delivery.getShippingAlternativeAddress() + " " +delivery.getShippingAlternativeAddress2())
					.put("zip", delivery.getShippingAlternativeZip())
					.put("city", delivery.getShippingAlternativeCity())
					.put("province", " ")
					.put("country", " ");
					registryJSON3.put("address", addressJSON3);
				} else registryJSON3.put("address", raddressToJSON(ra));
				
			
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
		Utils.giveBack(req, resp, json, new JSONObject());
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
/*		
{
  "carrier_packing": {
    "id": 11457, // DEL JSON RECIBIDO -> carrier_packing.id,
    "domain": 3048, // DEL JSON RECIBIDO -> carrier_packing.domain,
    "tare": 123 , // PESO DE LA TARA - A RELLENAR
    "additional_tare": 123, // PESO DE LA TARA ADICIONAL - A RELLENAR
    "net": 123, // PESO NETO - A RELLENAR
    "gross": 123, // PESO BRUTO - A RELLENAR
  },
  "incomes": // LISTADO DE ALBARANES
  [
    {
      "reference_code": "XXXXXXX", // REFERENCIA DEL ALBARÁN - A RELLENAR
      "registry": { ... }, // DEL JSON RECIBIDO -> orders[x].registry
      "carrier_packing": 11457, // DEL JSON RECIBIDO -> carrier_packing.id,
      "details": // LISTADO DE DETALLES DEL ALBARÁN
      [
        {
          "item": 2218879, // DEL JSON RECIBIDO -> orders[x].details[y].item
          "serializable": true, // DEL JSON RECIBIDO -> orders[x].details[y].serializable
          "lotable": true, // DEL JSON RECIBIDO -> orders[x].details[y].lotable
          "lote": "XXXXXXX", // NÚMERO DE LOTE SII ES LOTABLE - A RELLENAR
          "saldar": true, // SI LA CANTIDAD SE SALDA O NO - A RELLENAR
          "quantity": 123, // CANTIDAD DEL DETALLE DE ALBARÁN - A RELLENAR
          "description": // DEL JSON RECIBIDO -> orders[x].details[y].description
        }
      ]
    }
  ]
}
*/		
		
		JSONObject json = Utils.getRequestJSON(req);
	
		JSONObject carrierPacking = json.getJSONObject("carrier_packing");
		
		String domainName = req.getServerName();
		Domain domain = AON.getDomain(domainName, carrierPacking.getInt("domain"), "");
		String login = "";
		
		
		CarrierPacking cp = AON.getCarrierPacking(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(carrierPacking.getInt("id")));
		cp.setAdditionalTare(carrierPacking.getDouble("additional_tare"));
		cp.setTare(carrierPacking.getDouble("tare"));
		cp.setNet(carrierPacking.getDouble("net"));
		cp.setGross(carrierPacking.getDouble("gross"));
		
		AON.updateCarrierPacking(domain.getName(), domain.getId(), login, cp);
		
		JSONArray incomes = json.getJSONArray("incomes");
		Integer workplaceId = AON.getWarehouseStream(domain.getName(), domain.getId(), login,f -> f.getDomainProperty().eq(domain.getId())).findFirst().get().getWorkplace();
		for (int i = 0; i < incomes.length(); i++) {
			JSONObject income = incomes.getJSONObject(i);
			income.put("workplace", workplaceId);
			Optional<Income> opt = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getReferenceCodeProperty().eq(income.getString("number")));
			
			Integer incomeId = opt.isPresent() ? opt.get().getId() : DBIncome.insertIncome2(domain, login, income).getInt("id");
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
				DBPurchase.updatePurchaseDetail(domain, login, json);		
			}	
		}		
		JSONObject object = new JSONObject();
		
		resp.setContentType("application/json;charset=UTF-8");
		Utils.addCorsHeader(resp);
		PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
		os.println(object.toString());
		os.flush();
		os.close();
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
	
	public JSONObject carrierPackingToJSON(Domain domain, String login, CarrierPacking carrierPacking) {
		RAddress addr = AON.getRAddres(domain.getName(), domain.getId(), login, carrierPacking.getCarrier());
		
		String seriesNumber = (carrierPacking.getSeries() != null ? carrierPacking.getSeries() + "/" : "") + carrierPacking.getNumber();
		return new JSONObject()
			.put(MSG.ID, carrierPacking.getId())
			.put(MSG.DOMAIN, carrierPacking.getDomain())
			.put(MSG.SERIES_NUMBER, seriesNumber)
			.put(MSG.SERIES, carrierPacking.getSeries())
			.put(MSG.NUMBER, carrierPacking.getNumber())
			.put(MSG.TYPE, carrierPacking.getType() != null ? carrierPacking.getType().getName() : "")
			.put(MSG.STATUS, new JSONObject()
				.put(MSG.ID, carrierPacking.getStatus() != null ? carrierPacking.getStatus().value() : "")
				.put(MSG.NAME, carrierPacking.getStatus() != null ? carrierPacking.getStatus().getName(): "")) 
			.put(MSG.ISSUE_DATE, carrierPacking.getIssueDate() != null ? AonDateUtils.dateTimeFormat(carrierPacking.getIssueDate()) : "")
			.put(MSG.CARRIER, new JSONObject()
				.put(MSG.ID, carrierPacking.getCarrier())
				.put(MSG.NAME, carrierPacking.getCarrierName())
				.put("document", AON.getCarrier(domain.getName(), domain.getId(), login, carrierPacking.getCarrier()).getDocument() != null ?
						AON.getCarrier(domain.getName(), domain.getId(), login, carrierPacking.getCarrier()).getDocument() : "")
				.put("address", raddressToJSON(addr)))
			.put(MSG.DELIVERY_DATE, carrierPacking.getDeliveryDate() != null ? AonDateUtils.dateTimeFormat(carrierPacking.getDeliveryDate()) : "")
			.put(MSG.CARRIER_REFERENCE, carrierPacking.getCarrierReference() != null ? carrierPacking.getCarrierReference() : "")
			.put(MSG.NUMBER_PLATE, carrierPacking.getNumberPlate())
			.put(MSG.DRIVER_NAME, carrierPacking.getDriverName())
			.put(MSG.DRIVER_DOCUMENT, carrierPacking.getDriverDocument())
			.put(MSG.OBSERVATION, carrierPacking.getObservation())
			.put(MSG.GROSS, carrierPacking.getGross())
			.put(MSG.TARE, carrierPacking.getTare())
			.put(MSG.ADDITIONAL_TARE, carrierPacking.getAdditionalTare())
			.put(MSG.NET, carrierPacking.getNet())
			.put(MSG.RECEPTION_START_DATE, carrierPacking.getReceptionStartDate() != null ? AonDateUtils.dateTimeFormat(carrierPacking.getReceptionStartDate()) : null)
			.put(MSG.RECEPTION_END_DATE, carrierPacking.getReceptionEndDate() != null ? AonDateUtils.dateTimeFormat(carrierPacking.getReceptionEndDate()) : null);
	}
	
	public JSONObject raddressToJSON(RAddress address){	
		return new JSONObject()
			.put("address",address.getFullAddress())
			.put("zip", address.getZip() != null ? address.getZip() : " ")
			.put("city", address.getCity() != null ? address.getCity() : " ")
			.put("province", " ")
			.put("country", address.getGeozoneName() != null ? address.getGeozoneName() : " ");
	}
	
	public JSONObject purchaseToJSON(Purchase purchase) {
		String seriesNumber = (purchase.getSeries() != null ? purchase.getSeries() + "/" : "") + purchase.getNumber();
		return new JSONObject()
			.put("order_type", "purchase")
			.put(MSG.ID, purchase.getId())
			.put(MSG.DOMAIN, purchase.getDomain())
			.put("series_number", seriesNumber)
			.put(MSG.SERIES, purchase.getSeries())
			.put(MSG.NUMBER, purchase.getNumber())
			.put(MSG.ISSUE_DATE, purchase.getIssueDate() != null ? AonDateUtils.dateTimeFormat(purchase.getIssueDate()) : "")
			.put("reference", purchase.getPurchaseReference() != null ? purchase.getPurchaseReference() : " ");

	}
	
	public static JSONObject deliveryToJSON(Delivery delivery) {		
		String seriesNumber = (delivery.getSeries() != null ? delivery.getSeries() + "/" : "") + delivery.getNumber();
		return new JSONObject()
			.put("order_type", "delivery")
			.put(MSG.ID, delivery.getId())
			.put(MSG.DOMAIN, delivery.getDomain())
			.put("series_number", seriesNumber)
			.put(MSG.SERIES, delivery.getSeries())
			.put(MSG.NUMBER, delivery.getNumber())			
			.put(MSG.ISSUE_DATE, delivery.getIssueTime() != null ? AonDateUtils.dateTimeFormat(delivery.getIssueTime()) : "")
			.put("reference", delivery.getTrackingNumber() != null ? delivery.getTrackingNumber() : " ")
			.put("total_packages", delivery.getTotalPackages())
			.put("total_weight", delivery.getTotalWeight());
	}
}
