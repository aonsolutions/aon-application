package com.code.aon.webservice.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.CommercialTracking;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Elaboration;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.management.Sales;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductCategory;
import com.esferalia.aon.occam.api.model.project.ProjectCommercial;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryMedia;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.ElaborationSource;
import com.esferalia.aon.occam.api.model.type.ElaborationStatus;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SalesDetailStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonFileUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

public class ToJSON {
	
	public static JSONObject attachToJSON(Attach attach){	
		String icon = "aon-documental:file";
		if(attach.getMimeType().getName().contains("image")) {
			icon = "aon-documental:image";
		} else if(MimeType.MS_WORD.equals(attach.getMimeType()) || MimeType.MS_WORD_2007.equals(attach.getMimeType())) {
			icon = "aon-documental:word";
		} else if(MimeType.MS_EXCEL.equals(attach.getMimeType()) || MimeType.MS_EXCEL_2007.equals(attach.getMimeType())) {
			icon = "aon-documental:excel";
		} else if(MimeType.MS_POWER_POINT.equals(attach.getMimeType()) || MimeType.MS_POWER_POINT_2007.equals(attach.getMimeType())) {
			icon = "aon-documental:powerpoint";
		} else if(MimeType.PDF.equals(attach.getMimeType())) {
			icon = "aon-documental:pdf";
		} else if(attach.getMimeType().getName().contains("audio")) {
			icon = "aon-documental:audio";
		} else if(attach.getMimeType().getName().contains("video")) {
			icon = "aon-documental:video";
		} else if(MimeType.ZIP.equals(attach.getMimeType())) {
			icon = "view-list";
		}

		JSONArray tagArray = new JSONArray();
		if(attach.getTagList() != null)
			attach.getTagList().stream().forEach(r -> {
				tagArray.put(ToJSON.tagToJSON(r));
			});
		
		JSONObject f = new JSONObject();
	    String str = "domain="+ attach.getDomain().getId() + "&id=" + attach.getId() + "&attach_type=registry";
	    String result = Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
	    String url =  "ms/download_attachment/"  + attach.getDomain().getName() + "/" + attach.getCreationUser() + "/" +  result;
	    f.put("url", url);
	    f.put("type", attach.getMimeType().getName());
			
		return new JSONObject()
			.put("id", attach.getId())
			.put("domain", attach.getDomain().getId())
			.put("category", attach.getFullCategory() != null ? ToJSON.categoryToJSON(attach.getFullCategory()):new JSONObject())
			.put("scope", attach.getFullCategory() != null ? ToJSON.scopeToJSON(attach.getFullScope()): new JSONObject())
			.put("date", AonDateUtils.simpleFormat(attach.getDate()))
			.put("confidential", attach.getConfidential())
			.put("size", AonFileUtils.byteCountToDisplaySize( attach.getDparentId() != null ? Long.parseLong( attach.getDparentId()): 0))
			.put("title", attach.getDescription())
			.put("icon", icon)
			.put("tags", tagArray)
			.put("file", f);
	}
	
	public static JSONObject categoryToJSON(Category category){	
		return new JSONObject()
			.put("id",category.getId())
			.put("domain", category.getDomain())
			.put("name", category.getName())
			.put("type", category.getType())
			.put("scope", category.getScope())
			.put("url", category.getUrl())
			.put("description", category.getDescription())
			.put("rattach", category.getRattach());
	}
	
	public static JSONObject tagToJSON(Tag tag){	
		return new JSONObject()
			.put("id",tag.getId())
			.put("domain", tag.getDomain())
			.put("name", tag.getName())
			.put("type", tag.getType())
			.put("color", tag.getColor());
	}
	
	public static JSONObject scopeToJSON(Scope scope){	
		return new JSONObject()
			.put("id",scope.getId())
			.put("domain", scope.getDomain())
			.put("name", scope.getDescription());
	}
	
	public static JSONObject applicationParameterToJSON(ApplicationParameter appParam){	
		return new JSONObject()
			.put("id",appParam.getId())
			.put("domain", appParam.getDomain())
			.put("name", appParam.getName())
			.put("value", appParam.getValue());
	}
	
	public static JSONObject raddressToJSON(RAddress address){	
		return new JSONObject()
			.put("name",address.getRegistryName() != null ? address.getRegistryName() : "")
			.put("address",address.getFullAddress())
			.put("zip", address.getZip() != null ? address.getZip() : " ")
			.put("city", address.getCity() != null ? address.getCity() : " ")
			.put("province", " ")
			.put("country", address.getGeozoneName() != null ? address.getGeozoneName() : " ");
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
		json.put(MSG.REGISTRY, rmedia.getRegistry());
		json.put("media", rmedia.getMedia().value());
		json.put("value", rmedia.getValue());
		json.put(MSG.COMMENT, rmedia.getComment());
		json.put("administrative", rmedia.isAdministrative());
		json.put("commercial", rmedia.isCommercial());
		json.put("technical", rmedia.isTechnical());
		json.put("raddress", rmedia.getRaddress());
		json.put("icon", Icon.rmediaIcon(rmedia.getMedia().value()));
		return json;
	}
	
	public static JSONObject rnoteToJSON(RegistryNote rnote) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, rnote.getId());
		json.put(MSG.DOMAIN, rnote.getDomain());
		json.put(MSG.REGISTRY, rnote.getRegistry());
		json.put(MSG.DESCRIPTION, rnote.getDescription());
		json.put("note_date", AonDateUtils.simpleFormat(rnote.getNoteDate()));
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
		json.put("registry_name", invoice.getRegistryName());
		json.put("registry_document", invoice.getRegistryDocument());
		json.put("registry_document_country", invoice.getRegistryDocumentCountry());
		json.put("registry_document_type", invoice.getRegistryDocumentType());
		json.put("reference_code", invoice.getReferenceCode());
		json.put("vat_accrual_payment", invoice.isVatAccrualPayment());
		json.put("type", invoice.getType().getDescription());
		json.put("issue_date", AonDateUtils.format(invoice.getIssueDate(), "dd-MM-yyyy"));
		json.put("tax_date", AonDateUtils.format(invoice.getTaxDate(), "dd-MM-yyyy"));
		json.put("creation_date", AonDateUtils.format(invoice.getCreationDate(), "dd-MM-yyyy"));
		json.put("creation_user", invoice.getCreationUser());
		json.put("modification_date", invoice.getCreationDate());
		json.put("modification_user", invoice.getCreationDate());
		json.put("sii_status", invoice.getSiiStatus() != null ? invoice.getSiiStatus() : "Pendiente" );
		
		return json;
	}
	
	public static JSONObject invoiceDetailFullToJSON(InvoiceDetail invoiceDetail){
		JSONObject json = new JSONObject();
		if(invoiceDetail != null){
			json.put(MSG.ID, invoiceDetail.getId());
			json.put(MSG.DOMAIN, invoiceDetail.getDomain());
			json.put(MSG.PROJECT, invoiceDetail.getProject());
			json.put(MSG.INVOICE,
					new JSONObject()
					.put(MSG.ID, invoiceDetail.getInvoice().getId())
					.put(MSG.REGISTRY, new JSONObject()
										.put(MSG.ID, invoiceDetail.getInvoice().getRegistry())
										.put(MSG.NAME, invoiceDetail.getInvoice().getRegistryName()))
					.put("registry_name", invoiceDetail.getInvoice().getRegistryName())
					.put(MSG.REFERENCE_CODE, invoiceDetail.getInvoice().getReferenceCode())
					.put(MSG.SERIES, invoiceDetail.getInvoice().getSeries())
					.put(MSG.NUMBER, invoiceDetail.getInvoice().getNumber())
					.put(MSG.ISSUE_DATE, AonDateUtils.dateTimeFormat(invoiceDetail.getInvoice().getIssueDate())));
			json.put(MSG.LINE, invoiceDetail.getLine());
			json.put(MSG.ITEM, invoiceDetail.getItem().getId());
			json.put(MSG.DESCRIPTION, invoiceDetail.getDescription());
			json.put(MSG.QUANTITY, invoiceDetail.getQuantity());
			json.put(MSG.PRICE, invoiceDetail.getPrice());
			json.put(MSG.DISCOUNT_EXPR, invoiceDetail.getDiscountExpression());
		}
		return json;
	}
	
	public static JSONObject feeToJSON(Fee fee) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, fee.getId());
		json.put(MSG.DOMAIN, fee.getDomain().getId());
		json.put("customer", fee.getCustomer().getId());
		json.put(MSG.DESCRIPTION, fee.getDescription());
		json.put("start_date", fee.getStartDate() != null ?
				AonDateUtils.simpleFormat(fee.getStartDate()) : "");
		json.put("end_date", fee.getEndDate() != null ?
				AonDateUtils.simpleFormat(fee.getEndDate()) : "");
		json.put("billing_month", getMonth(fee.getBillingDate()));
		json.put("billing_year", AonDateUtils.getYear(fee.getBillingDate()));
		json.put("period", getPeriod(fee.getPeriod()));
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
		json.put("date", AonDateUtils.simpleFormat(id.getInvoice().getIssueDate()));
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
		json.put("date", AonDateUtils.simpleFormat(project.getDate()));
		json.put("project_type", project.getProjectTypeId());
		json.put(MSG.COMMENT, project.getComments());
		return json;
	}
	
	public static JSONObject commercialTrackingToJSON(CommercialTracking ct, Registry seller) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, ct.getId());
		json.put(MSG.DOMAIN, ct.getDomain());
		json.put("date", AonDateUtils.simpleFormat(ct.getDate()));
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
	
	public static JSONObject productToJSON(Product product) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, product.getId());
		json.put(MSG.DOMAIN, product.getDomain());
		json.put("code", product.getCode());
		json.put(MSG.NAME, product.getName());
		return json;
	}

	public static JSONObject itemToJSON(Item item) {
		JSONObject json = new JSONObject();
		json.put(MSG.ID, item.getId());
		json.put(MSG.DOMAIN, item.getDomain());
		json.put("code", item.getProduct() != null ? item.getProduct().getCode(): "");
		json.put(MSG.NAME, item.getProduct() != null ? item.getProduct().getName(): "");
		json.put("product_id", item.getProductId());
		json.put("serial_number", item.getSerialNumber());
		json.put("detail", item.getDetail());
		json.put("detail2", item.getDetail2());
		json.put("detail3", item.getDetail3());
		return json;
	}
	
	public static JSONObject carrierPackingToJSON(CarrierPacking carrierPacking) {
		String seriesNumber = (carrierPacking.getSeries() != null ? carrierPacking.getSeries() + "/" : "") + carrierPacking.getNumber();
		return new JSONObject()
			.put(MSG.ID, carrierPacking.getId())
			.put(MSG.DOMAIN, carrierPacking.getDomain())
			.put(MSG.SERIES_NUMBER, seriesNumber)
			.put(MSG.SERIES, carrierPacking.getSeries())
			.put(MSG.NUMBER, carrierPacking.getNumber())
			.put(MSG.TYPE, new JSONObject()
				.put(MSG.ID, carrierPacking.getType() != null ? carrierPacking.getType().value() : "")
				.put(MSG.NAME, carrierPacking.getType() != null ? carrierPacking.getType().getName() : ""))
			.put(MSG.STATUS, new JSONObject()
				.put(MSG.ID, carrierPacking.getStatus() != null ? carrierPacking.getStatus().value() : "")
				.put(MSG.NAME, carrierPacking.getStatus() != null ? carrierPacking.getStatus().getName(): "")) 
			.put(MSG.ISSUE_DATE, carrierPacking.getIssueDate() != null ? AonDateUtils.simpleFormat(carrierPacking.getIssueDate()) : "")
			.put(MSG.CARRIER, new JSONObject()
				.put(MSG.ID, carrierPacking.getCarrier())
				.put(MSG.NAME, carrierPacking.getCarrierName()))
			.put(MSG.DELIVERY_DATE, carrierPacking.getDeliveryDate() != null ? AonDateUtils.simpleFormat(carrierPacking.getDeliveryDate()) : "")
			.put(MSG.CARRIER_REFERENCE, carrierPacking.getCarrierReference() != null ? carrierPacking.getCarrierReference() : "")
			.put(MSG.NUMBER_PLATE, carrierPacking.getNumberPlate())
			.put(MSG.DRIVER_NAME, carrierPacking.getDriverName())
			.put(MSG.DRIVER_DOCUMENT, carrierPacking.getDriverDocument())
			.put(MSG.COMMENTS, carrierPacking.getComments() != null ? carrierPacking.getComments() : " ")
			.put(MSG.OBSERVATION, carrierPacking.getObservation())
			.put(MSG.PARAMS, carrierPacking.getParams())
			
			.put(MSG.GROSS, carrierPacking.getGross())
			.put(MSG.TARE, carrierPacking.getTare())
			.put(MSG.ADDITIONAL_TARE, carrierPacking.getAdditionalTare())
			.put(MSG.NET, carrierPacking.getNet())
			.put(MSG.RECEPTION_START_DATE, carrierPacking.getReceptionStartDate() != null ? AonDateUtils.dateTimeFormat(carrierPacking.getReceptionStartDate()) : null)
			.put(MSG.RECEPTION_END_DATE, carrierPacking.getReceptionEndDate() != null ? AonDateUtils.dateTimeFormat(carrierPacking.getReceptionEndDate()) : null)
			
			
			.put(MSG.CREATION_DATE, carrierPacking.getCreationDate() != null ? AonDateUtils.simpleFormat(carrierPacking.getCreationDate()): "")
			.put(MSG.CREATION_USER, carrierPacking.getCreationUser())
			.put(MSG.MODIFICATION_DATE, carrierPacking.getModificationDate() != null ? AonDateUtils.simpleFormat(carrierPacking.getModificationDate()) : "")
			.put(MSG.MODIFICATION_USER, carrierPacking.getModificationUser())
			.put("supplier", "-")
			.put("customer", "-")
			.put("supplier_array", new JSONArray())
			.put("customer_array", new JSONArray());
		
	}
	
	public static JSONObject elaborationToJSON(Elaboration elaboration) {
		String seriesNumber = (elaboration.getSeries() != null ? elaboration.getSeries() + "/" : "") + elaboration.getNumber();
		return new JSONObject()
			.put(MSG.ID, elaboration.getId())
			.put(MSG.DOMAIN, elaboration.getDomain())
			.put(MSG.SERIES_NUMBER, seriesNumber)
			.put(MSG.SERIES, elaboration.getSeries())
			.put(MSG.NUMBER, elaboration.getNumber())
			.put(MSG.STATUS, new JSONObject()
				.put(MSG.ID, elaboration.getStatus() != null ? elaboration.getStatus().intValue() : "")
				.put(MSG.NAME, elaboration.getStatus() != null ? ElaborationStatus.values()[elaboration.getStatus()].getName(): ""))
			.put(MSG.DATE, elaboration.getDate() != null ? AonDateUtils.format(elaboration.getDate(), "yyyy/MM/dd") : "")
			.put(MSG.ITEM, itemToJSON(elaboration.getItem()))
			.put(MSG.DESCRIPTION, elaboration.getDescription())
			.put(MSG.QUANTITY, elaboration.getQuantity())
			.put(MSG.WAREHOUSE, new JSONObject()
				.put(MSG.ID, elaboration.getWarehouse() != null ? elaboration.getWarehouse().getId() : "")
				.put(MSG.NAME, elaboration.getWarehouse() != null ? elaboration.getWarehouse().getName(): ""))
			.put(MSG.COMMENTS, elaboration.getComments() != null ? elaboration.getComments() : " ")
			.put(MSG.REMARKS, elaboration.getRemarks() != null ? elaboration.getRemarks() : " ")
			.put("source", new JSONObject()
					.put(MSG.ID, elaboration.getSource() != null ? elaboration.getSource().intValue() : "")
					.put(MSG.NAME, elaboration.getSource() != null ? ElaborationSource.values()[elaboration.getSource()].getName(): ""))
			.put("source_id", elaboration.getSourceId())
			.put(MSG.CREATION_DATE, elaboration.getCreationDate() != null ? AonDateUtils.simpleFormat(elaboration.getCreationDate()): "")
			.put(MSG.CREATION_USER, elaboration.getCreationUser())
			.put(MSG.MODIFICATION_DATE, elaboration.getModificationDate() != null ? AonDateUtils.simpleFormat(elaboration.getModificationDate()) : "")
			.put(MSG.MODIFICATION_USER, elaboration.getModificationUser());
	}
	
	public static JSONObject elaborationDetailToJSON(ElaborationDetail detail) {
		return new JSONObject()
				.put(MSG.ID, detail.getId())
				.put(MSG.DOMAIN, detail.getDomain())
				.put(MSG.DATE, detail.getDate() != null ? AonDateUtils.format(detail.getDate(), "yyyy/MM/dd") : "")
				.put(MSG.ITEM, itemToJSON(detail.getItem()))
				.put(MSG.QUANTITY, detail.getQuantity())
				.put(MSG.WAREHOUSE, new JSONObject()
						.put(MSG.ID, detail.getWarehouse() != null ? detail.getWarehouse().getId() : "")
						.put(MSG.NAME, detail.getWarehouse() != null ? detail.getWarehouse().getName(): ""))
				.put(MSG.ADD_INFO, detail.getAddInfo() != null ? detail.getAddInfo() : " ")
				.put(MSG.CREATION_DATE, detail.getCreationDate() != null ? AonDateUtils.simpleFormat(detail.getCreationDate()): "")
				.put(MSG.CREATION_USER, detail.getCreationUser())
				.put(MSG.MODIFICATION_DATE, detail.getModificationDate() != null ? AonDateUtils.simpleFormat(detail.getModificationDate()) : "")
				.put(MSG.MODIFICATION_USER, detail.getModificationUser());
	}
	
	public static JSONObject elaborationDetailCompositionToJSON(ElaborationDetailComposition detailComposition) {
		return new JSONObject()
				.put(MSG.ID, detailComposition.getId())
				.put(MSG.DOMAIN, detailComposition.getDomain())
				.put(MSG.ITEM, itemToJSON(detailComposition.getItem()))
				.put(MSG.QUANTITY, detailComposition.getQuantity())
				.put(MSG.WAREHOUSE, new JSONObject()
						.put(MSG.ID, detailComposition.getWarehouse() != null ? detailComposition.getWarehouse().getId() : "")
						.put(MSG.NAME, detailComposition.getWarehouse() != null ? detailComposition.getWarehouse().getName(): ""))
				.put(MSG.CREATION_DATE, detailComposition.getCreationDate() != null ? AonDateUtils.simpleFormat(detailComposition.getCreationDate()): "")
				.put(MSG.CREATION_USER, detailComposition.getCreationUser())
				.put(MSG.MODIFICATION_DATE, detailComposition.getModificationDate() != null ? AonDateUtils.simpleFormat(detailComposition.getModificationDate()) : "")
				.put(MSG.MODIFICATION_USER, detailComposition.getModificationUser());
	}
	
	public static JSONObject purchaseToJSON(Purchase purchase) {
		String seriesNumber = (purchase.getSeries() != null ? purchase.getSeries() + "/" : "") + purchase.getNumber();
		return new JSONObject()
			.put(MSG.ID, purchase.getId())
			.put(MSG.DOMAIN, purchase.getDomain())
			.put("series_number", seriesNumber)
			.put(MSG.SERIES, purchase.getSeries())
			.put(MSG.NUMBER, purchase.getNumber())
			.put(MSG.REGISTRY, new JSONObject()
				.put(MSG.ID, purchase.getSupplier())
				.put(MSG.NAME, purchase.getSupplierName())) 
			.put(MSG.ISSUE_DATE, purchase.getIssueDate() != null ? AonDateUtils.dateTimeFormat(purchase.getIssueDate()) : "")
			.put("order_type", "purchase")
			.put("address", purchase.getAddress())
			.put("reference", purchase.getPurchaseReference() != null ? purchase.getPurchaseReference() : " ")
			.put("purchase_reference", purchase.getPurchaseReference() != null ? purchase.getPurchaseReference() : " ");
	}
	
	public static JSONObject deliveryToJSON(Delivery delivery) {		
		String seriesNumber = (delivery.getSeries() != null ? delivery.getSeries() + "/" : "") + delivery.getNumber();
		return new JSONObject()
			.put(MSG.ID, delivery.getId())
			.put(MSG.DOMAIN, delivery.getDomain())
			.put("series_number", seriesNumber)
			.put(MSG.SERIES, delivery.getSeries())
			.put(MSG.NUMBER, delivery.getNumber())
			.put(MSG.REGISTRY, new JSONObject()
				.put(MSG.ID, delivery.getCustomer())
				.put(MSG.NAME, delivery.getCustomerName())) 
			.put(MSG.ISSUE_DATE, delivery.getIssueTime() != null ? AonDateUtils.dateTimeFormat(delivery.getIssueTime()) : "")
			.put("order_type", "delivery")
			.put("reference", delivery.getTrackingNumber() != null ? delivery.getTrackingNumber() : " ")
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
			.put("purchase", purchaseDetail.getPurchaseId())
			.put("carrier_packing", purchaseDetail.getCarrierPacking())
			.put("lotable", purchaseDetail.getItem2() != null ?
					purchaseDetail.getItem2().getProduct().isLotable() : "")
			.put("serializable", purchaseDetail.getItem2() != null ?
					purchaseDetail.getItem2().getProduct().isSerializable() : "")
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
	
	public static JSONObject salesToJSON(Sales sales) {
		return new JSONObject()
		.put(MSG.ID, sales.getId())
		.put(MSG.DOMAIN, sales.getDomain())
		.put(MSG.SERIES, sales.getSeries())
		.put(MSG.NUMBER, sales.getNumber())
		.put(MSG.CUSTOMER, sales.getCustomer()) 
		.put(MSG.CUSTOMER, new JSONObject()
			.put(MSG.ID, sales.getCustomer() != null ? sales.getCustomer().getId() : "")
			.put(MSG.NAME, sales.getCustomer() != null ? sales.getCustomer().getName(): ""))
		.put(MSG.ISSUE_DATE, sales.getIssueDate() != null ? AonDateUtils.dateTimeFormat(sales.getIssueDate()) : "")
		.put(MSG.DELIVERY_DATE, sales.getDeliveryDate() != null ? AonDateUtils.dateTimeFormat(sales.getDeliveryDate()) : "")
		.put("purchase_reference", sales.getPurchaseReference() != null ? sales.getPurchaseReference() : " ");
	}
	
	public static JSONObject salesDetailToJSON(SalesDetail detail) {
		return new JSONObject()
		.put(MSG.ID, detail.getId())
		.put(MSG.DOMAIN, detail.getDomain())
		.put(MSG.SALES, salesToJSON(detail.getSales()))
		.put(MSG.LINE, detail.getLine())
		.put(MSG.ITEM, itemToJSON(detail.getItem()))
		.put(MSG.DESCRIPTION, detail.getDescription())
		.put(MSG.QUANTITY, detail.getQuantity())
		.put(MSG.PRICE, detail.getPrice())
		.put(MSG.DISCOUNT_EXPR, detail.getDiscountExpression())
		.put("taxes", detail.getTaxes())
		.put(MSG.STATUS, new JSONObject()
				.put(MSG.ID, detail.getStatus() != null ? detail.getStatus().ordinal() : "")
				.put(MSG.NAME, detail.getStatus() != null ? SalesDetailStatus.values()[detail.getStatus().ordinal()].getName(): ""))
		.put("delivered", detail.getDelivered())
		;
	}
	
	public static JSONObject dataResponseToJSON(DataResponse dr) {
		return new JSONObject()
				.put(MSG.ID, dr.getId())
				.put(MSG.DOMAIN, dr.getDomain())
				.put("code", dr.getCode())
				.put(MSG.NUMBER, dr.getCode())
				.put(MSG.ISSUE_DATE, dr.getResponseDate() != null ? AonDateUtils.dateTimeFormat(dr.getResponseDate()) : "")
				.put(MSG.DATE, dr.getResponseDate() != null ? AonDateUtils.dateTimeFormat(dr.getResponseDate()) : "")
				.put(MSG.CREATION_USER, dr.getCreationUser())
				.put(MSG.CREATION_DATE, dr.getCreationDate() != null ? AonDateUtils.dateTimeFormat(dr.getCreationDate()) : "")
				.put(MSG.MODIFICATION_USER, dr.getModificationUser())
				.put(MSG.MODIFICATION_DATE, dr.getModificationDate() != null ? AonDateUtils.dateTimeFormat(dr.getModificationDate()) : "")
		;
	}
	
	public static JSONObject dataResponseDetailToJSON(DataResponseDetail drd) {
		return new JSONObject()
				.put(MSG.ID, drd.getId())
				.put(MSG.DOMAIN, drd.getDomain())
				.put(MSG.DATA_RESPONSE, drd.getDataResponse())
				.put(MSG.DATA_VARIABLE, drd.getDataVariable())
				.put("variable", drd.getDataVariable())
				.put(MSG.VALUE, drd.getDataValue())
				.put(MSG.CREATION_USER, drd.getCreationUser())
				.put(MSG.ISSUE_DATE, AonDateUtils.dateTimeFormat(drd.getCreationDate()))
				.put(MSG.MODIFICATION_USER, drd.getModificationUser())
				.put(MSG.MODIFICATION_DATE, AonDateUtils.dateTimeFormat(drd.getModificationDate()))
		;
	}
	
	public static JSONObject objectToJSON(Integer id, String name) {
		return new JSONObject()
			.put(MSG.ID, id)
			.put(MSG.NAME, name);
	}
	
	public static JSONObject objectToJSON(Integer id, String name, Date date) {
		return new JSONObject()
			.put(MSG.ID, id)
			.put(MSG.NAME, name)
			.put(MSG.DATE, AonDateUtils.dateTimeFormat(date));
		
	}
	
	public static JSONObject userToJSON(User user) {
		return new JSONObject()
			.put(MSG.ID, user.getId())
			.put(MSG.NAME, user.getName())
			.put(MSG.DOMAIN, user.getDomain())
			.put(MSG.LOGIN, user.getLogin());		
	}
	
	public static JSONObject companyToJSON(Company company) {
		return new JSONObject()
			.put(MSG.ID, company.getId())
			.put(MSG.NAME, company.getName())
			.put(MSG.DOCUMENT, company.getDocument())
			.put(MSG.DOMAIN, company.getDomain())
			.put(MSG.SCOPE, objectToJSON(company.getScope().getId(), company.getScope().getDescription()));		
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
