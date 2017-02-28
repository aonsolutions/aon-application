package com.code.aon.webservice.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class ToJSON {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	public static JSONObject applicationParameterToJSON(ApplicationParameter appParam){	
		return new JSONObject()
			.put("id",appParam.getId())
			.put("domain", appParam.getDomain())
			.put("name", appParam.getName())
			.put("value", appParam.getValue());
	}
	
	public static JSONObject raddressToJSON(RAddress address){	
		String streetType = address.getStreet_type();
		if("XX".equals(streetType) || "ZZ".equals(streetType)){
			streetType = "";
		}
		return new JSONObject()
			.put("name",address.getRegistryName())
			.put("address", streetType + " " + address.getAddress() + " " 
					+ address.getNumber() +" " + address.getAddress2() +  " " + address.getAddress3())
			.put("zip", address.getZip())
			.put("city", address.getCity())
			.put("province", "")
			.put("country", address.getGeozoneName());
	}
	
	public static JSONObject generalToJSON(String direction, String commercial, String segmentation, String observation, JSONArray rmedia, String status){
		JSONObject json = new JSONObject();
		json.put("direction", direction);
		json.put("commercial", commercial);
		json.put("segmentation", segmentation);
		json.put("observation", observation);
		json.put("rmedia", rmedia);
		json.put("status", status);
		return json;
	}
	
	public static JSONObject rmediaToJSON(RegistryMedia rmedia) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, rmedia.getId());
		json.put(MSG.DOMAIN, rmedia.getDomain());
		json.put(MSG.REGISTRY, rmedia.getRegistry().getId());
		json.put("media", rmedia.getMedia());
		json.put("value", rmedia.getValue());
		json.put(MSG.COMMENT, rmedia.getComment());
		json.put("administrative", rmedia.getAdministrative() == 1);
		json.put("commercial", rmedia.getCommercial() == 1);
		json.put("technical", rmedia.getTechnical() == 1);
		json.put("raddress", rmedia.getRaddress());
		json.put("icon", Icon.rmediaIcon(rmedia.getMedia()));
		return json;
	}
	
	public static JSONObject rnoteToJSON(RegistryNote rnote) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, rnote.getId());
		json.put(MSG.DOMAIN, rnote.getDomain());
		json.put(MSG.REGISTRY, rnote.getRegistry());
		json.put(MSG.DESCRIPTION, rnote.getDescription());
		json.put("note_date", dateFormat.format(rnote.getNoteDate()));
		json.put("comments", rnote.getComments());
		json.put("note_type", rnote.getNoteType());
		json.put("confidential", rnote.getSecurityLevel() == 1);
		return json;
	}
	
	public static JSONObject invoiceToJSON(Invoice invoice) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, invoice.getId());
		json.put(MSG.DOMAIN, invoice.getDomain());
		json.put(MSG.REGISTRY, invoice.getRegistry());
		json.put("reference_code", invoice.getReferenceCode());
		return json;
	}
	
	public static JSONObject feeToJSON(Fee fee) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, fee.getId());
		json.put(MSG.DOMAIN, fee.getDomain());
		json.put("customer", fee.getCustomer());
		json.put(MSG.DESCRIPTION, fee.getDescription());
		json.put("start_date", fee.getStartDate() != null ?
				dateFormat.format(fee.getStartDate()) : "");
		json.put("end_date", fee.getEndDate() != null ?
				dateFormat.format(fee.getEndDate()) : "");
		json.put("billing_month", getMonth(fee.getBillingDate()));
		json.put("billing_year", AonDateUtils.getYear(fee.getBillingDate()));
		json.put("period", getPeriod(BillingPeriod.values()[fee.getPeriod()]));
		return json;
	}

	public static JSONObject boughtProductToJSON(InvoiceDetail id) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, id.getId());
		json.put(MSG.DOMAIN, id.getDomain());
		json.put(MSG.DESCRIPTION, id.getDescription());
		json.put(MSG.NAME, id.getItem().getName());
		json.put("quantity", id.getQuantity());
		json.put("price", id.getPrice());
		json.put("discount", Double.parseDouble(id.getDiscountExpression()));
		json.put("date", dateFormat.format(id.getInvoice().getIssueDate()));
		json.put("code", id.getItem().getCode());
		json.put("total", AonMathUtils.round(id.getQuantity()*id.getPrice() * ((Double.parseDouble(id.getDiscountExpression())/100) + 1)));
		json.put("reference_code", id.getInvoice().getReferenceCode());
		return json;
	}

	public static JSONObject projectCommercialToJSON(ProjectCommercial project, Registry registry, Registry seller) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, project.getId());
		json.put(MSG.DOMAIN, project.getDomain());
		json.put(MSG.NAME, project.getName());
		json.put("alias", project.getAlias());
		json.put(MSG.REGISTRY, registryToJSON(registry));
		json.put("seller", registryToJSON(seller));
		json.put("date", dateFormat.format(project.getDate()));
		json.put("project_type", project.getProjectTypeId());
		json.put(MSG.COMMENT, project.getComments());
		return json;
	}
	
	public static JSONObject commercialTrackingToJSON(CommercialTracking ct, Registry seller) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, ct.getId());
		json.put(MSG.DOMAIN, ct.getDomain());
		json.put("date", dateFormat.format(ct.getDate()));
		json.put("seller", registryToJSON(seller));
		json.put(MSG.COMMENT, ct.getComments());
		return json;
	}

	public static JSONObject registryToJSON(Registry registry) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, registry.getId());
		json.put(MSG.NAME, registry.getName());
		return json;
	}
	
	public static JSONObject productCategoryToJSON(ProductCategory pc) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, pc.getId());
		json.put(MSG.DOMAIN, pc.getDomain());
		json.put(MSG.NAME, pc.getName());
		return json;
	}
	
	public static JSONObject carrierPackingToJSON(CarrierPacking carrierPacking) {
		return new JSONObject()
			.put(MSG.ID, carrierPacking.getId())
			.put(MSG.DOMAIN, carrierPacking.getDomain())
			.put("series", carrierPacking.getSeries())
			.put("number", carrierPacking.getNumber())
			.put("type", new JSONObject()
				.put(MSG.ID, carrierPacking.getType() != null ? carrierPacking.getType().value() : "")
				.put(MSG.NAME, carrierPacking.getType() != null ? carrierPacking.getType().getName() : ""))
			.put("status", new JSONObject()
				.put(MSG.ID, carrierPacking.getStatus() != null ? carrierPacking.getStatus().value() : "")
				.put(MSG.NAME, carrierPacking.getStatus() != null ? carrierPacking.getStatus().getName(): "")) 
			.put(MSG.ISSUE_DATE, carrierPacking.getIssueDate() != null ? dateFormat.format(carrierPacking.getIssueDate()) : "")
			.put("carrier", new JSONObject()
				.put(MSG.ID, carrierPacking.getCarrier())
				.put(MSG.NAME, carrierPacking.getCarrierName()))
			.put("delivery_date", carrierPacking.getDeliveryDate() != null ? dateFormat.format(carrierPacking.getDeliveryDate()) : "")
			.put("carrier_reference", carrierPacking.getCarrierReference())
			.put("number_plate", carrierPacking.getNumberPlate())
			.put("driver_name", carrierPacking.getDriverName())
			.put("driver_document", carrierPacking.getDriverDocument())
			.put("comments", carrierPacking.getComments() != null ? carrierPacking.getComments() : " ")
			.put("observation", carrierPacking.getObservation())
			.put("params", carrierPacking.getParams())
			.put("creation_date", carrierPacking.getCreationDate() != null ? dateFormat.format(carrierPacking.getCreationDate()): "")
			.put("creation_user", carrierPacking.getCreationUser())
			.put("modification_date", carrierPacking.getModificationDate() != null ? dateFormat.format(carrierPacking.getModificationDate()) : "")
			.put("modification_user", carrierPacking.getModificationUser());
	}
	
	public static JSONObject purchaseToJSON(Purchase purchase) {
		return new JSONObject()
			.put(MSG.ID, purchase.getId())
			.put(MSG.DOMAIN, purchase.getDomain())
			.put(MSG.SERIES, purchase.getSeries())
			.put(MSG.NUMBER, purchase.getNumber())
			.put(MSG.REGISTRY, new JSONObject()
				.put(MSG.ID, purchase.getSupplier())
				.put(MSG.NAME, purchase.getSupplierName())) 
			.put(MSG.ISSUE_DATE, purchase.getIssueDate() != null ? dateFormat.format(purchase.getIssueDate()) : "")
			.put("order_type", "purchase")
			.put("reference", purchase.getPurchaseReference());
	}
	
	public static JSONObject deliveryToJSON(Delivery delivery) {
		return new JSONObject()
			.put(MSG.ID, delivery.getId())
			.put(MSG.DOMAIN, delivery.getDomain())
			.put(MSG.SERIES, delivery.getSeries())
			.put(MSG.NUMBER, delivery.getNumber())
			.put(MSG.REGISTRY, new JSONObject()
				.put(MSG.ID, delivery.getCustomer())
				.put(MSG.NAME, delivery.getCustomerName())) 
			.put(MSG.ISSUE_DATE, delivery.getIssueTime() != null ? dateFormat.format(delivery.getIssueTime()) : "")
			.put("order_type", "delivery")
			.put("reference", delivery.getTrackingNumber())
			.put("total_packages", delivery.getTotalPackages())
			.put("total_weight", delivery.getTotalWeight());
			
			
	}
	
	public static JSONObject purchaseDetailToJSON(PurchaseDetail purchaseDetail) {
		return new JSONObject()
			.put(MSG.ID, purchaseDetail.getId())
			.put(MSG.DOMAIN, purchaseDetail.getDomain())
			.put("price", purchaseDetail.getPrice())
			.put("quantity", purchaseDetail.getQuantity())
			.put("discount_expr", purchaseDetail.getDiscountExpression())
			.put("line", purchaseDetail.getLine())
			.put("item", purchaseDetail.getItem())
			.put("delivered", purchaseDetail.getDelivered())
			.put("description", purchaseDetail.getDescription())
			.put("product_code", purchaseDetail.getProductCode())
			.put("product_name", purchaseDetail.getProductName())
			
			.put("carrier_packing", purchaseDetail.getCarrierPacking())
			;
	}
	
	public static JSONObject deliveryDetailToJSON(DeliveryDetail deliveryDetail) {
		return new JSONObject()
			.put(MSG.ID, deliveryDetail.getId())
			.put(MSG.DOMAIN, deliveryDetail.getDomain())
			.put("price", deliveryDetail.getPrice())
			.put("quantity", deliveryDetail.getQuantity())
			.put("discount_expr", deliveryDetail.getDiscountExpression())
			.put("line", deliveryDetail.getLine())
			.put("item", deliveryDetail.getItem())
			.put("description", deliveryDetail.getDescription())
			.put("product_code", deliveryDetail.getProductCode())
			.put("product_name", deliveryDetail.getProductName())
			;
	}
	
	public static JSONObject objectToJSON(Integer id, String name) {
		return new JSONObject()
			.put(MSG.ID, id)
			.put(MSG.NAME, name);
	}
	
	public static String getPeriod(BillingPeriod period){
		if(period.equals(BillingPeriod.YEARLY))
			return "Anual";
		else if(period.equals(BillingPeriod.SIX_MONTHLY))
			return "Semestral";
		else if(period.equals(BillingPeriod.FOUR_MONTHLY))
			return "Cuatrimestral";
		else if(period.equals(BillingPeriod.THREE_MONTHLY))
			return "Trimestral";
		else if(period.equals(BillingPeriod.BI_MONTHLY))
			return "Bi-mensual";
		else if(period.equals(BillingPeriod.MONTHLY))
			return "Mensual";
		else return "Sin Periodo";
	}
	
	public static String getMonth(Date date){
		switch (AonDateUtils.getMonth(date)){
		case 0 : return "Enero";
		case 1 : return "Febrero";
		case 2 : return "Marzo";
		case 3 : return "Abril";
		case 4 : return "Mayo";
		case 5 : return "Junio";
		case 6 : return "Julio";
		case 7 : return "Agosto";
		case 8 : return "Septiembre";
		case 9 : return "Octubre";
		case 10 : return "Noviembre";
		default : return "Diciembre";
		}
	}
}
