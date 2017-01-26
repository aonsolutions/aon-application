package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.CarrierPacking.CARRIER_PACKING;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.function.Function;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.Product;
import com.esferalia.aon.jooq.tables.records.WarehouseRecord;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseSourceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Warehouse;

public class FillerDAO {

	public static class FullWarehouseFiller implements Function<WarehouseRecord, Warehouse> {
		@Override
		public Warehouse apply(WarehouseRecord r) {
			return new Warehouse()
					.setActive(r.getActive())
					.setDomain(r.getDomain())
					.setId(r.getId())
					.setDepartment(r.getDepartment())
					.setName(r.getName())
					.setWorkplace(r.getWorkplace());
		}
	}
	
	public static class CarrierPackingFiller implements Function<Record, CarrierPacking> {
		@Override
		public CarrierPacking apply(Record r) {
			return new CarrierPacking()
					.setCarrier(r.getValue(CARRIER_PACKING.CARRIER))
					.setCarrierReference(r.getValue(CARRIER_PACKING.CARRIER_REFERENCE))
					.setCreationDate(r.getValue(CARRIER_PACKING.CREATION_DATE))
					.setCreationUser(r.getValue(CARRIER_PACKING.CREATION_USER))
					.setDeliveryDate(r.getValue(CARRIER_PACKING.DELIVERY_DATE))
					.setDomain(r.getValue(CARRIER_PACKING.DOMAIN))
					.setDriverDocument(r.getValue(CARRIER_PACKING.DRIVER_DOCUMENT))
					.setDriverName(r.getValue(CARRIER_PACKING.DRIVER_NAME))
					.setId(r.getValue(CARRIER_PACKING.ID))
					.setIssueDate(r.getValue(CARRIER_PACKING.ISSUE_DATE))
					.setModificationDate(r.getValue(CARRIER_PACKING.MODIFICATION_DATE))
					.setModificationUser(r.getValue(CARRIER_PACKING.MODIFICATION_USER))
					.setNumber(r.getValue(CARRIER_PACKING.NUMBER))
					.setNumberPlate(r.getValue(CARRIER_PACKING.NUMBER_PLATE))
					.setSeries(r.getValue(CARRIER_PACKING.SERIES))
					.setStatus(CarrierPackingStatus.values()[r.getValue(CARRIER_PACKING.STATUS)])
					.setType(CarrierPackingType.values()[r.getValue(CARRIER_PACKING.TYPE)])
					
					.setCarrierName(r.getValue(REGISTRY.NAME))
					;
		}
	}
	
	public static class RegistryFiller implements Function<Record, Registry> {
		@Override
		public Registry apply(Record r) {
			return new Registry()
					.setAlias(r.getValue(REGISTRY.ALIAS))
					.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(r.getValue(REGISTRY.SECURITY_LEVEL)))
					.setDocument(r.getValue(REGISTRY.DOCUMENT))
					.setDocumentCountry(null) // TODO
					.setDocumentType(DocumentType.values()[r.getValue(REGISTRY.DOCUMENT_TYPE)])
					.setDomain(r.getValue(REGISTRY.DOMAIN))
					.setId(r.getValue(REGISTRY.ID))
					.setName(r.getValue(REGISTRY.NAME))
					.setNationality(null) // TODO
					.setSecurityLevel(SecurityLevel.values()[r.getValue(REGISTRY.SECURITY_LEVEL)])
					.setType(r.getValue(REGISTRY.TYPE));			
		}
	}
	
	public static class CarrierFiller implements Function<Record, Carrier> {
		@Override
		public Carrier apply(Record r) {
			Carrier carrier = new Carrier();
			carrier.setAlias(r.getValue(REGISTRY.ALIAS));
			carrier.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(r.getValue(REGISTRY.SECURITY_LEVEL)));
			carrier.setDocument(r.getValue(REGISTRY.DOCUMENT));
			carrier.setDocumentCountry(null); // TODO
			carrier.setDocumentType(DocumentType.values()[r.getValue(REGISTRY.DOCUMENT_TYPE)]);
			carrier.setDomain(r.getValue(REGISTRY.DOMAIN));
			carrier.setId(r.getValue(REGISTRY.ID));
			carrier.setName(r.getValue(REGISTRY.NAME));
			carrier.setNationality(null); // TODO
			carrier.setSecurityLevel(SecurityLevel.values()[r.getValue(REGISTRY.SECURITY_LEVEL)]);
			carrier.setType(r.getValue(REGISTRY.TYPE));
			return carrier.setScope(r.getValue(CARRIER.SCOPE));				
		}
	}
	
	public static class PurchaseDetailFiller implements Function<Record, PurchaseDetail> {
		
		@Override
		public PurchaseDetail apply(Record r) {
			PurchaseDetail detail = new PurchaseDetail();
			detail.setId(r.getValue(PURCHASE_DETAIL.ID));
			detail.setDomain(r.getValue(PURCHASE_DETAIL.DOMAIN));
			detail.setPurchaseId(r.getValue(PURCHASE_DETAIL.PURCHASE));
			detail.setItem(r.getValue(PURCHASE_DETAIL.ITEM));
			detail.setLine(r.getValue(PURCHASE_DETAIL.LINE).intValue());
			detail.setDescription(r.getValue(PURCHASE_DETAIL.DESCRIPTION));
			detail.setQuantity(r.getValue(PURCHASE_DETAIL.QUANTITY));
			detail.setPrice(r.getValue(PURCHASE_DETAIL.PRICE));
			detail.setDiscountExpression(r.getValue(PURCHASE_DETAIL.DISCOUNT_EXPR));
			detail.setTaxes(r.getValue(PURCHASE_DETAIL.TAXES));
			detail.setStatus(PurchaseDetailStatus.values()[r.getValue(PURCHASE_DETAIL.STATUS)]);
			detail.setProposalDetail(r.getValue(PURCHASE_DETAIL.PROPOSAL_DETAIL));
			detail.setSource(PurchaseSourceType.values()[r.getValue(PURCHASE_DETAIL.SOURCE)]);
			detail.setSourceId(r.getValue(PURCHASE_DETAIL.SOURCE_ID));
			detail.setDelivered(r.getValue(PURCHASE_DETAIL.DELIVERED));
			
			detail.setProductCode(r.getValue(Product.PRODUCT.CODE));
			detail.setProductName(r.getValue(Product.PRODUCT.NAME));
			return detail;
		}
	}
}
