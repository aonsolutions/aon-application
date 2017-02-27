package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.CarrierPacking.CARRIER_PACKING;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;

import java.util.function.Function;

import org.jooq.Record;

import com.esferalia.aon.jooq.tables.Product;
import com.esferalia.aon.jooq.tables.records.WarehouseRecord;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.registry.Carrier;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DeliveryStatus;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseSourceType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryDetail;
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
					.setComments(r.getValue(CARRIER_PACKING.COMMENTS));
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
			if(r.getValue(PURCHASE_DETAIL.SOURCE) != null)
				detail.setSource(PurchaseSourceType.values()[r.getValue(PURCHASE_DETAIL.SOURCE)]);
			detail.setSourceId(r.getValue(PURCHASE_DETAIL.SOURCE_ID));
			detail.setDelivered(r.getValue(PURCHASE_DETAIL.DELIVERED));
			detail.setCarrier(r.getValue(PURCHASE_DETAIL.CARRIER));
			detail.setCarrierPacking(r.getValue(PURCHASE_DETAIL.CARRIER_PACKING));
			
			detail.setProductCode(r.getValue(Product.PRODUCT.CODE));
			detail.setProductName(r.getValue(Product.PRODUCT.NAME));
	
			return detail;
		}
	}
	
	public static class DeliveryFiller implements Function<Record, Delivery> {
		@Override
		public Delivery apply(Record r) {
			return new Delivery().setId(r.getValue(DELIVERY.ID)).setDomain(r.getValue(DELIVERY.DOMAIN))
					.setProject(new Project()
							.setId(r.getValue(DELIVERY.PROJECT)))
					.setSeries(r.getValue(DELIVERY.SERIES)).setNumber(r.getValue(DELIVERY.NUMBER))
					.setCustomer(r.getValue(DELIVERY.CUSTOMER)).setAddress(r.getValue(DELIVERY.ADDRESS))
					.setIssueTime(r.getValue(DELIVERY.ISSUE_TIME)).setPayMethod(r.getValue(DELIVERY.PAY_METHOD))
					.setSecurityLevel(r.getValue(DELIVERY.SECURITY_LEVEL))
					.setStatus(DeliveryStatus.values()[r.getValue(DELIVERY.STATUS)])
					.setComments(r.getValue(DELIVERY.COMMENTS)).setRemarks(r.getValue(DELIVERY.REMARKS))
					.setWorkplace(r.getValue(DELIVERY.WORKPLACE)).setScope(r.getValue(DELIVERY.SCOPE))
					.setNumberOfPymnts(r.getValue(DELIVERY.NUMBER_OF_PYMNTS))
					.setDaysToFirstPymnt(r.getValue(DELIVERY.DAYS_TO_FIRST_PYMNT))
					.setDaysBetweenPymnt(r.getValue(DELIVERY.DAYS_BETWEEN_PYMNTS))
					.setPymntDays(r.getValue(DELIVERY.PYMNT_DAYS)).setBankAccount(r.getValue(DELIVERY.BANK_ACCOUNT))
					.setBankAlias(r.getValue(DELIVERY.BANK_ALIAS)).setBic(r.getValue(DELIVERY.BIC))
					.setCarrier(r.getValue(DELIVERY.CARRIER))
					.setCarrierPacking(r.getValue(DELIVERY.CARRIER_PACKING))
					.setNumberPlate(r.getValue(DELIVERY.NUMBER_PLATE))
					.setDriver(r.getValue(DELIVERY.DRIVER))
					.setDriverDocument(r.getValue(DELIVERY.DRIVER_DOCUMENT))
					.setTotalPackages(r.getValue(DELIVERY.TOTAL_PACKAGES))
					.setTotalWeight(r.getValue(DELIVERY.TOTAL_WEIGHT))
					.setShippingAlternativeAddress(r.getValue(DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS))
					.setShippingAlternativeAddress2(r.getValue(DELIVERY.SHIPPING_ALTERNATIVE_ADDRESS2))
					.setShippingAlternativeZip(r.getValue(DELIVERY.SHIPPING_ALTERNATIVE_ZIP))
					.setShippingAlternativeCity(r.getValue(DELIVERY.SHIPPING_ALTERNATIVE_CITY))
					.setShippingAlternativePhone(r.getValue(DELIVERY.SHIPPING_ALTERNATIVE_PHONE))
					.setShippingAlternativeRecipient(r.getValue(DELIVERY.SHIPPING_ALTERNATIVE_RECIPIENT))
					.setShippingContact(r.getValue(DELIVERY.SHIPPING_CONTACT))
					.setShippingPeriod(r.getValue(DELIVERY.SHIPPING_PERIOD))
					.setTrackingNumber(r.getValue(DELIVERY.TRACKING_NUMBER))
					.setShippingStatus(r.getValue(DELIVERY.SHIPPING_STATUS))
					.setStatusModificationDate(r.getValue(DELIVERY.STATUS_MODIFICATION_DATE))
					.setCreationDate(r.getValue(DELIVERY.CREATION_DATE))
					.setCreationUser(r.getValue(DELIVERY.CREATION_USER))
					.setModificationDate(r.getValue(DELIVERY.MODIFICATION_DATE))
					.setModificationUser(r.getValue(DELIVERY.MODIFICATION_USER));
		}
	}
	
	public static class RDeliveryFiller extends DeliveryFiller{
		@Override
		public Delivery apply(Record r) {
			return super.apply(r).setCustomerName(r.getValue(REGISTRY.NAME));
		}
	}
	
	public static class DeliveryDetailFiller implements Function<Record, DeliveryDetail> {
		@Override
		public DeliveryDetail apply(Record r) {
			return new DeliveryDetail().setId(r.getValue(DELIVERY_DETAIL.ID))
					.setDomain(r.getValue(DELIVERY_DETAIL.DOMAIN))
					.setDelivery(new Delivery().setId(r.getValue(DELIVERY_DETAIL.DELIVERY)))
					.setLine(r.getValue(DELIVERY_DETAIL.LINE))
					.setItem(new Item()
							.setId(r.getValue(DELIVERY_DETAIL.ITEM)))
					.setDescription(r.getValue(DELIVERY_DETAIL.DESCRIPTION))
					.setWarehouse(r.getValue(DELIVERY_DETAIL.WAREHOUSE))
					.setQuantity(r.getValue(DELIVERY_DETAIL.QUANTITY))
					.setPrice(r.getValue(DELIVERY_DETAIL.PRICE))
					.setDiscountExpression(r.getValue(DELIVERY_DETAIL.DISCOUNT_EXPR))
					.setSalesDetail(r.getValue(DELIVERY_DETAIL.SALES_DETAIL))
					.setCreationDate(r.getValue(DELIVERY_DETAIL.CREATION_DATE))
					.setCreationUser(r.getValue(DELIVERY_DETAIL.CREATION_USER))
					.setModificationDate(r.getValue(DELIVERY_DETAIL.MODIFICATION_DATE))
					.setModificationUser(r.getValue(DELIVERY_DETAIL.MODIFICATION_USER));
		}
	}
	
	public static class PDeliveryDetailFiller extends DeliveryDetailFiller {
		@Override
		public DeliveryDetail apply(Record r) {
			return super.apply(r)
					.setProductCode(r.getValue(PRODUCT.CODE))
					.setProductName(r.getValue(PRODUCT.NAME));
		}
	}
	
	public static class RecordDataFiller implements Function<Record, RecordData> {
		@Override
		public RecordData apply(Record r) {
			return new RecordData()
					.setAttach(r.getValue(RECORD_DATA.ATTACH))
					.setCreationDate(r.getValue(RECORD_DATA.CREATION_DATE))
					.setDescription(r.getValue(RECORD_DATA.DESCRIPTION))
					.setDomain(r.getValue(RECORD_DATA.DOMAIN))
					.setId(r.getValue(RECORD_DATA.ID))
					.setNotary(r.getValue(RECORD_DATA.NOTARY))
					.setNumber(r.getValue(RECORD_DATA.NUMBER))
					.setPage(r.getValue(RECORD_DATA.PAGE))
					.setRecordDate(r.getValue(RECORD_DATA.RECORD_DATE))
					.setRegistration(r.getValue(RECORD_DATA.REGISTRATION))
					.setRegistry(r.getValue(RECORD_DATA.REGISTRY))
					.setSection(r.getValue(RECORD_DATA.SECTION))
					.setSheet(r.getValue(RECORD_DATA.SHEET))
					.setVolume(r.getValue(RECORD_DATA.VOLUME));
		}
	}
	
	public static class CompanyFiller implements Function<Record, Company> {
		@Override
		public Company apply(Record r) {
			Company company = new Company();
			company.setAlias(r.getValue(REGISTRY.ALIAS));
			company.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(r.getValue(REGISTRY.SECURITY_LEVEL)));
			company.setDocumentCountry(Country.valueOf(r.getValue(REGISTRY.DOCUMENT_COUNTRY))); // TODO
			company.setDocumentType(DocumentType.values()[r.getValue(REGISTRY.DOCUMENT_TYPE)]);
			company.setNationality(Country.valueOf(r.getValue(REGISTRY.NATIONALITY))); // TODO
			company.setSecurityLevel(SecurityLevel.values()[r.getValue(REGISTRY.SECURITY_LEVEL)]);
			company.setType(r.getValue(REGISTRY.TYPE));	
			return company
				.setActive(r.getValue(COMPANY.ACTIVE) == 1)
				.seteInvoice(r.getValue(COMPANY.E_INVOICE) == 1)
				.setDomain(r.getValue(COMPANY.DOMAIN))
				.setDocument(r.getValue(REGISTRY.DOCUMENT))
				.setId(r.getValue(REGISTRY.ID))
				.setName(r.getValue(REGISTRY.NAME))
				.setSurcharge(r.getValue(COMPANY.SURCHARGE) == 1)
				.setVatAccrualPayment(r.getValue(COMPANY.VAT_ACCRUAL_PAYMENT) == 1)
				.setWithholding(r.getValue(COMPANY.WITHHOLDING) == 1);
		}
	}
}
