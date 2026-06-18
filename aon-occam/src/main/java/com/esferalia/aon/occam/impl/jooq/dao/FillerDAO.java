package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AgreementLevel.AGREEMENT_LEVEL;
import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Commission.COMMISSION;
import static com.esferalia.aon.jooq.tables.CommissionCategory.COMMISSION_CATEGORY;
import static com.esferalia.aon.jooq.tables.CommissionItem.COMMISSION_ITEM;
import static com.esferalia.aon.jooq.tables.CommissionType.COMMISSION_TYPE;
import static com.esferalia.aon.jooq.tables.CommissionTypeCommission.COMMISSION_TYPE_COMMISSION;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.EnterpriseCcc.ENTERPRISE_CCC;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailCommission.INVOICE_DETAIL_COMMISSION;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ItemAddinfo.ITEM_ADDINFO;
import static com.esferalia.aon.jooq.tables.MkTemplate.MK_TEMPLATE;
import static com.esferalia.aon.jooq.tables.OfferDetail.OFFER_DETAIL;
import static com.esferalia.aon.jooq.tables.OfferDetailCommission.OFFER_DETAIL_COMMISSION;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.Workplace.WORKPLACE;

import java.util.Arrays;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.model.AonCompany;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.ContractExtendedData;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseActivity;
import com.esferalia.aon.occam.api.model.MailTemplate;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.commission.Commission;
import com.esferalia.aon.occam.api.model.commission.CommissionCategory;
import com.esferalia.aon.occam.api.model.commission.CommissionItem;
import com.esferalia.aon.occam.api.model.commission.CommissionType;
import com.esferalia.aon.occam.api.model.commission.CommissionTypeCommission;
import com.esferalia.aon.occam.api.model.commission.InvoiceDetailCommission;
import com.esferalia.aon.occam.api.model.commission.InvoiceDetailCommissionStatus;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommission;
import com.esferalia.aon.occam.api.model.commission.OfferDetailCommissionStatus;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.fiscal.IrpfData;
import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.Purchase;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevel;
import com.esferalia.aon.occam.api.model.payroll.AgreementLevelCategory;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemAddInfo;
import com.esferalia.aon.occam.api.model.product.OldItem;
import com.esferalia.aon.occam.api.model.product.OldProduct;
import com.esferalia.aon.occam.api.model.registry.Project;
import com.esferalia.aon.occam.api.model.registry.RecordData;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.registry.Supplier;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.security.UserType;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.api.model.type.AonStatus;
import com.esferalia.aon.occam.api.model.type.CCCType;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.DomainType;
import com.esferalia.aon.occam.api.model.type.Gender;
import com.esferalia.aon.occam.api.model.type.IncomeStatus;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MaritalStatus;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.api.model.type.PurchaseDetailStatus;
import com.esferalia.aon.occam.api.model.type.PurchaseSourceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.occam.api.model.warehouse.PaturpatQuality;
import com.esferalia.aon.occam.api.model.warehouse.UdapaQuality;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO.OfferDetailFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FillerDAO {
	
	private FillerDAO() {
	
	}
	
	public static class DomainFiller extends Filler implements Function<Record, Domain> {
		@Override
		public Domain apply(Record r) {
			Domain d = build(r);
			return d;
		}
		
		public static Domain build(Record r) {
			return buildDomain(r, Domain::new, DOMAIN, DomainDAO.PARENT, DomainDAO.PAYER);
		}
		
		public static Domain buildDomain(Record r, java.util.function.Supplier<Domain> supplier, com.esferalia.aon.jooq.tables.Domain domainTable, com.esferalia.aon.jooq.tables.Domain parentTable, com.esferalia.aon.jooq.tables.AppParam payerTable) {
			Domain domain = fillDomain(r, supplier.get(), domainTable);
			Domain parent = fillDomain(r, supplier.get(), parentTable);
			if ( parent.getId() != null ) {
				domain.setParent(parent);
			}
			fillDomain(r, domain, payerTable);
			return domain;
		}

//		public static Domain buildDomain(Record r, com.esferalia.aon.jooq.tables.Domain domainTable) {
//			return fillDomain(r, new Domain(), domainTable);
//		}
		
		public static Domain fillDomain(Record r, Domain domain, com.esferalia.aon.jooq.tables.AppParam payerTable) {
			try {
				domain.setPayer(AonNumberUtils.toInteger(getValue(r, payerTable.VALUE)));
			} catch (Exception e) {

			}
			return domain;
		}

		public static Domain fillDomain(Record r, Domain domain, com.esferalia.aon.jooq.tables.Domain domainTable) {
			return domain
				.setId(getValue(r, domainTable.ID))
				.setName(getValue(r, domainTable.NAME))
				.setDescription(getValue(r, domainTable.DESCRIPTION))
				.setParentId(getValue(r, domainTable.PARENT))
				.setDomainType(DomainType.safeValueOf(getValue(r, domainTable.TYPE)))
				.setScope(getValue(r, domainTable.SCOPE))
				.setEnableHeredity(getBoolean(r, domainTable.ENABLEHEREDITY))
				.setDomainManagement(getBoolean(r, domainTable.DOMAINMANAGEMENT))
				.setDisableDomainManagement(getBoolean(r, domainTable.DISABLEDOMAINMANAGEMENT))
				.setMaxDocumentSize(getValue(r, domainTable.MAXDOCUMENTSIZE))
				.setMaxTotalDocumentSize(getValue(r, domainTable.MAXTOTALDOCUMENTSIZE))
				.setMaxDefinedUsers(getValue(r, domainTable.MAXDEFINEDUSERS))
				.setActive(getBoolean(r, domainTable.ACTIVE))
				.setOwner(getValue(r, domainTable.OWNER))
				.setCreationUser(getValue(r, domainTable.CREATION_USER))
				.setCreationDate(getValue(r, domainTable.CREATION_DATE))
				.setModificationUser(getValue(r, domainTable.MODIFICATION_USER))
				.setModificationDate(getValue(r, domainTable.MODIFICATION_DATE))
				.setLastAccessUser(getValue(r, domainTable.LASTACCESS_USER))
				.setLastAccessDate(getValue(r, domainTable.LASTACCESS_DATE))
				.setExpirationDate(getValue(r, domainTable.EXPIRATIONDATE))
				.setAonCustomer(getValue(r, domainTable.AONCUSTOMER))
				.setAonStatus(AonStatus.safeValueOf(getValue(r, domainTable.AONSTATUS)))
				.setSubDomainSuffix(getValue(r, domainTable.SUBDOMAINSUFFIX))
				;	
		}

	}

	public static class ApplicationParameterFiller extends Filler implements Function<Record, ApplicationParameter> {
		@Override
		public ApplicationParameter apply(Record r) {
			return new ApplicationParameter()
					.setId(getValue(r, APP_PARAM.ID))
					.setDomain(getValue(r, APP_PARAM.DOMAIN))
					.setName(getValue(r, APP_PARAM.NAME))
					.setValue(getValue(r, APP_PARAM.VALUE));
		}
	}
	
	

	public static class RegistryFiller extends Filler implements Function<Record, Registry> {
		@Override
		public Registry apply(Record r) {
			return new Registry()
					.setAlias(getValue(r, REGISTRY.ALIAS))
					.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(getValue(r, REGISTRY.SECURITY_LEVEL)))
					.setDocument(getValue(r, REGISTRY.DOCUMENT))
					.setDocumentCountry(null) // TODO
					.setDocumentType(DocumentType.safeValueOf(getValue(r, REGISTRY.DOCUMENT_TYPE)))
					.setDomain(new Domain().setId(getValue(r, REGISTRY.DOMAIN)))
					.setId(getValue(r, REGISTRY.ID))
					.setName(getValue(r, REGISTRY.NAME))
					.setNationality(null) // TODO
					.setSecurityLevel(SecurityLevel.safeValueOf(getValue(r, REGISTRY.SECURITY_LEVEL)))
					.setLegalPerson(AonEnumUtils.getBoolean( getValue(r, REGISTRY.TYPE)));			
		}
	}
	
	public static class PersonFiller extends Filler implements Function<Record, Person> {
		@Override
		public Person apply(Record r) {
			Person person = new Person();
			person.setAlias(getValue(r, REGISTRY.ALIAS));
			person.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(getValue(r, REGISTRY.SECURITY_LEVEL)));
			person.setDocument(getValue(r, REGISTRY.DOCUMENT));
			person.setDocumentCountry(null); // TODO
			person.setDocumentType(DocumentType.safeValueOf(getValue(r, REGISTRY.DOCUMENT_TYPE)));
			person.setId(getValue(r, REGISTRY.ID));
			person.setName(getValue(r, REGISTRY.NAME));
			person.setNationality(null); // TODO
			person.setSecurityLevel(SecurityLevel.safeValueOf(getValue(r, REGISTRY.SECURITY_LEVEL)));
			person.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, REGISTRY.TYPE)));
			person.setDomain(new Domain().setId(getValue(r, PERSON.DOMAIN)));
			return person.setBirthDate(getValue(r, PERSON.BIRTH_DATE))
					.setFirstName(getValue(r, PERSON.NAME))
					.setFirstSurname(getValue(r, PERSON.FIRST_SURNAME))
					.setSecondSurname(getValue(r, PERSON.SECOND_SURNAME))
					.setGender(Gender.safeValueOf(getValue(r, PERSON.GENDER)))
					.setMaritalStatus(MaritalStatus.safeValueOf(getValue(r, PERSON.MARITAL_STATUS)))
					.setSocialSecurityNum(getValue(r, PERSON.SOCIAL_SECURITY_NUM));				
		}
	}
	
	public static class RItemFiller  extends Filler implements Function<Record,RegistryItem> {

		@Override
		public RegistryItem apply(Record r) {
			return new RegistryItem()
					.setId(getValue(r, RITEM.ID))
					.setDomain(getValue(r, RITEM.DOMAIN))
					.setRegistry(getValue(r, RITEM.REGISTRY))
					.setItem(ItemFiller.build(r))
					.setType(RegistryMode.safeValueOf(getValue(r, RITEM.TYPE)))
					.setCode(getValue(r, RITEM.CODE))
					.setEdiSalesCode(r.get(RITEM.EDI_SALES_CODE))
					.setCustomerFee(r.get(RITEM.CUSTOMER_FEE))
//					.setSeller(SellerFiller.build(r))
					.setPrice(getValue(r, RITEM.PRICE))
					.setDiscountExpression(getValue(r, RITEM.DISCOUNT_EXPR))
					.setWorkplace(getValue(r, RITEM.WORKPLACE))
					.setPriority(Priority.safeValueOf(getValue(r, RITEM.PRIORITY)))
					.setStatus(RegistryItemStatus.safeValueOf(getValue(r, RITEM.STATUS)))
					.setQuantity(getValue(r, RITEM.QUANTITY))
					.setStartDate(getValue(r, RITEM.START_DATE))
					.setEndDate(getValue(r, RITEM.END_DATE))
					.setCreationDate(getValue(r, RITEM.CREATION_DATE))
					.setCreationUser(getValue(r, RITEM.CREATION_USER))
					.setModificationDate(getValue(r, RITEM.MODIFICATION_DATE))
					.setModificationUser(getValue(r, RITEM.MODIFICATION_USER));
		}
	}
	
	public static class PurchaseDetailFiller extends Filler implements Function<Record, PurchaseDetail> {
		
		@Override
		public PurchaseDetail apply(Record r) {
			PurchaseDetail detail = new PurchaseDetail();
			detail.setId(getValue(r, PURCHASE_DETAIL.ID));
			detail.setDomain(getValue(r, PURCHASE_DETAIL.DOMAIN));
			detail.setPurchaseId(getValue(r, PURCHASE_DETAIL.PURCHASE));
			detail.setItem(getValue(r, PURCHASE_DETAIL.ITEM));
			detail.setLine(getValue(r, PURCHASE_DETAIL.LINE).intValue());
			detail.setDescription(getValue(r, PURCHASE_DETAIL.DESCRIPTION));
			detail.setQuantity(getValue(r, PURCHASE_DETAIL.QUANTITY));
			detail.setPrice(getValue(r, PURCHASE_DETAIL.PRICE));
			detail.setDiscountExpression(getValue(r, PURCHASE_DETAIL.DISCOUNT_EXPR));
			detail.setTaxes(getValue(r, PURCHASE_DETAIL.TAXES));
			detail.setStatus(PurchaseDetailStatus.safeValueOf(getValue(r, PURCHASE_DETAIL.STATUS)));
			detail.setProposalDetail(getValue(r, PURCHASE_DETAIL.PROPOSAL_DETAIL));
			if(getValue(r, PURCHASE_DETAIL.SOURCE) != null)
				detail.setSource(PurchaseSourceType.safeValueOf(getValue(r, PURCHASE_DETAIL.SOURCE)));
			detail.setSourceId(getValue(r, PURCHASE_DETAIL.SOURCE_ID));
			detail.setDelivered(getValue(r, PURCHASE_DETAIL.DELIVERED));
			detail.setCarrier(getValue(r, PURCHASE_DETAIL.CARRIER));
			detail.setCarrierPacking(getValue(r, PURCHASE_DETAIL.CARRIER_PACKING));
			

			return detail;
		}
	}
	
	public static class PurchaseDetailItemFiller extends Filler implements Function<Record, PurchaseDetail> {
		
		@Override
		public PurchaseDetail apply(Record r) {
			PurchaseDetail detail = new PurchaseDetail();
			detail.setId(getValue(r, PURCHASE_DETAIL.ID));
			detail.setDomain(getValue(r, PURCHASE_DETAIL.DOMAIN));
			detail.setPurchaseId(getValue(r, PURCHASE_DETAIL.PURCHASE));
			detail.setPurchase(new Purchase()
					.setId(getValue(r, PURCHASE.ID))
					.setPurchaseReference(getValue(r, PURCHASE.PURCHASE_REFERENCE))
					.setSupplier(getValue(r, REGISTRY.ID))
					.setSupplierName(getValue(r, REGISTRY.NAME))
					.setAddress(getValue(r, PURCHASE.ADDRESS))
					);
			detail.setItem(getValue(r, PURCHASE_DETAIL.ITEM));
			detail.setLine(getValue(r, PURCHASE_DETAIL.LINE).intValue());
			detail.setDescription(getValue(r, PURCHASE_DETAIL.DESCRIPTION));
			detail.setQuantity(getValue(r, PURCHASE_DETAIL.QUANTITY));
			detail.setPrice(getValue(r, PURCHASE_DETAIL.PRICE));
			detail.setDiscountExpression(getValue(r, PURCHASE_DETAIL.DISCOUNT_EXPR));
			detail.setTaxes(getValue(r, PURCHASE_DETAIL.TAXES));
			detail.setStatus(PurchaseDetailStatus.safeValueOf(getValue(r, PURCHASE_DETAIL.STATUS)));
			detail.setProposalDetail(getValue(r, PURCHASE_DETAIL.PROPOSAL_DETAIL));
			if(getValue(r, PURCHASE_DETAIL.SOURCE) != null)
				detail.setSource(PurchaseSourceType.safeValueOf(getValue(r, PURCHASE_DETAIL.SOURCE)));
			detail.setSourceId(getValue(r, PURCHASE_DETAIL.SOURCE_ID));
			detail.setDelivered(getValue(r, PURCHASE_DETAIL.DELIVERED));
			detail.setCarrier(getValue(r, PURCHASE_DETAIL.CARRIER));
			detail.setCarrierPacking(getValue(r, PURCHASE_DETAIL.CARRIER_PACKING));
			detail.setProductId(getValue(r, PRODUCT.ID));
			detail.setProductCode(getValue(r, PRODUCT.CODE));
			detail.setProductName(getValue(r, PRODUCT.NAME));
			detail.setItem2(new OldItem().setId(getValue(r, ITEM.ID))
				.setBarcode(getValue(r, ITEM.BARCODE))
				.setCreationDate(getValue(r, ITEM.CREATION_DATE))
				.setCreationUser(getValue(r, ITEM.CREATION_USER))
				.setDescription(getValue(r, ITEM.DESCRIPTION))
				.setDetail(getValue(r, ITEM.DETAIL))
				.setDetail2(getValue(r, ITEM.DETAIL2))
				.setDetail3(getValue(r, ITEM.DETAIL3))
				.setDomain(getValue(r, ITEM.DOMAIN))
				.setExpensesFixed(getValue(r, ITEM.EXPENSES_FIXED))
				.setExpensesPercent(getValue(r, ITEM.EXPENSES_PERCENT))
				.setInternet(getValue(r, ITEM.INTERNET) == 1)
				.setModificationDate(getValue(r, ITEM.MODIFICATION_DATE))
				.setModificationUser(getValue(r, ITEM.MODIFICATION_USER))
				.setPackMeasurement(getValue(r, ITEM.PACK_MEASUREMENT))
				.setPackUnits(getValue(r, ITEM.PACK_UNITS).doubleValue())
				.setPrice(getValue(r, ITEM.PRICE))
				.setProductId(getValue(r, ITEM.PRODUCT))
				.setProfitPercent(getValue(r, ITEM.PROFIT_PERCENT))
				.setPurchasePrice(getValue(r, ITEM.PURCHASE_PRICE))
				.setSerialNumber(getValue(r, ITEM.SERIAL_NUMBER))
				.setSerialDate(getValue(r, ITEM.SERIAL_DATE))
				.setStatus(getValue(r, ITEM.STATUS))
				.setProduct(new OldProduct().setId(getValue(r, PRODUCT.ID))
					.setName(getValue(r, PRODUCT.NAME))
					.setDomain(getValue(r, PRODUCT.DOMAIN))
					.setCode(getValue(r, PRODUCT.CODE))
					.setComposition(getValue(r, PRODUCT.COMPOSITION) == 1)
					.setCompositionPrice(getValue(r, PRODUCT.COMPOSITION_PRICE) == 1)
					.setCreationDate(getValue(r, PRODUCT.CREATION_DATE))
					.setCreationUser(getValue(r, PRODUCT.CREATION_USER))
					.setInventoriable(getValue(r, PRODUCT.INVENTORIABLE) == 1)
					.setKind(getValue(r, PRODUCT.KIND))
					.setLotable(getValue(r, PRODUCT.LOTABLE) == 1)
					.setManufactured(getValue(r, PRODUCT.MANUFACTURED))
					.setModificationDate(getValue(r, PRODUCT.MODIFICATION_DATE))
					.setModificationUser(getValue(r, PRODUCT.MODIFICATION_USER))
					.setPackaged(getValue(r, PRODUCT.PACKAGED) == 1)
					.setPurchaseAccount(getValue(r, PRODUCT.PURCHASE_ACCOUNT))
					.setRetention(getValue(r, PRODUCT.RETENTION)) 
					.setSalesAccount(getValue(r, PRODUCT.SALES_ACCOUNT))
					.setSerializable(getValue(r, PRODUCT.SERIALIZABLE) == 1)
					.setStatus(getValue(r, PRODUCT.STATUS))
					.setType(getValue(r, PRODUCT.TYPE))
					.setVat(getValue(r, PRODUCT.VAT))));
			return detail;
		}
	}
	
	public static class RecordDataFiller extends Filler implements Function<Record, RecordData> {
		@Override
		public RecordData apply(Record r) {
			return new RecordData()
					.setAttach(getValue(r, RECORD_DATA.ATTACH))
					.setCreationDate(getValue(r, RECORD_DATA.CREATION_DATE))
					.setDescription(getValue(r, RECORD_DATA.DESCRIPTION))
					.setDomain(getValue(r, RECORD_DATA.DOMAIN))
					.setId(getValue(r, RECORD_DATA.ID))
					.setNotary(getValue(r, RECORD_DATA.NOTARY))
					.setNumber(getValue(r, RECORD_DATA.NUMBER))
					.setPage(getValue(r, RECORD_DATA.PAGE))
					.setRecordDate(getValue(r, RECORD_DATA.RECORD_DATE))
					.setRegistration(getValue(r, RECORD_DATA.REGISTRATION))
					.setRegistry(getValue(r, RECORD_DATA.REGISTRY))
					.setSection(getValue(r, RECORD_DATA.SECTION))
					.setSheet(getValue(r, RECORD_DATA.SHEET))
					.setVolume(getValue(r, RECORD_DATA.VOLUME));
		}
	}
	
	public static class AonCompanyFiller extends Filler implements Function<Record, AonCompany> {
		public static final Field<String> EMAILS = DSL.field("emails", String.class);
		public static final Field<String> PHONES = DSL.field("phones", String.class);
		
		@Override
		public AonCompany apply(Record r) {
			com.esferalia.aon.jooq.tables.Domain domain = DOMAIN.as("d");
			com.esferalia.aon.jooq.tables.Domain parent = DOMAIN.as("p");
			com.esferalia.aon.jooq.tables.AppParam payer = APP_PARAM.as("payer");

			Domain d = new Domain()
			.setId(getValue(r, domain.ID))
			.setName(getValue(r, domain.NAME))
			.setParentId(getValue(r, domain.PARENT))
			.setActive(AonEnumUtils.getBoolean(getValue(r, domain.ACTIVE)))
			.setDescription(getValue(r, domain.DESCRIPTION))
			.setDomainType(DomainType.values()[getValue(r, domain.TYPE)])
			.setScope(getValue(r, domain.SCOPE))
			.setExpirationDate(getValue(r, domain.EXPIRATIONDATE))
			.setEnableHeredity(AonEnumUtils.getBoolean(getValue(r, domain.ENABLEHEREDITY)))
			.setDomainManagement(AonEnumUtils.getBoolean(getValue(r, domain.DOMAINMANAGEMENT)))
			.setPayer(AonNumberUtils.toInteger(getValue(r, payer.VALUE)))
			.setParent(new Domain()
				.setId(getValue(r, parent.ID))
				.setName(getValue(r, parent.NAME))
				.setExpirationDate(getValue(r, parent.EXPIRATIONDATE))
				.setActive(AonEnumUtils.getBoolean(getValue(r, parent.ACTIVE)))
				);

			Company company = new Company();
			company.setDomain(d);
			company.setAlias(getValue(r, REGISTRY.ALIAS));
			company.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(getValue(r, REGISTRY.SECURITY_LEVEL)));
			company.setDocumentCountry(Country.valueOf(getValue(r, REGISTRY.DOCUMENT_COUNTRY)));
			company.setDocumentType(DocumentType.safeValueOf(getValue(r, REGISTRY.DOCUMENT_TYPE)));
			company.setNationality(getValue(r, REGISTRY.NATIONALITY) != null ? Country.valueOf(getValue(r, REGISTRY.NATIONALITY)): null);
			company.setSecurityLevel(SecurityLevel.safeValueOf(getValue(r, REGISTRY.SECURITY_LEVEL)));
			company.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, REGISTRY.TYPE)));	
			company
				.setDocument(getValue(r, REGISTRY.DOCUMENT))
				.setId(getValue(r, REGISTRY.ID))
				.setName(getValue(r, REGISTRY.NAME));
			company
				.setActive(getValue(r, COMPANY.ACTIVE) == 1)
				.seteInvoice(getValue(r, COMPANY.E_INVOICE) == 1)
				.setSurcharge(getValue(r, COMPANY.SURCHARGE) == 1)
				.setVatAccrualPayment(getValue(r, COMPANY.VAT_ACCRUAL_PAYMENT) == 1)
				.setWithholding(getValue(r, COMPANY.WITHHOLDING) == 1);
			
			String[] emails = Arrays.stream(AonStringUtils.split(AonStringUtils.defaultIfBlank(r.getValue(EMAILS)), ','))
					.filter(AonStringUtils::isNotBlank).toArray(String[]::new);
			String[] phones = Arrays.stream(AonStringUtils.split(AonStringUtils.defaultIfBlank(r.getValue(PHONES)), ','))
					.filter(AonStringUtils::isNotBlank).map( phone -> phone.replaceAll("\\D","")).toArray(String[]::new);

			return new AonCompany()
				.setDomain(d)
				.setShared(AonEnumUtils.enumValue(UserType.class, getValue(r, USER.TYPE)) == UserType.SHARED)
				.setCompany(company)
				.setAdministration(Administration.safeValueOf(AonNumberUtils.toInteger(getValue(r, APP_PARAM.VALUE))))
				.setLogin(getValue(r, USER.LOGIN))
				.setEmails(emails)
				.setPhones(phones)
				;
		}
	}
	
	public static class IncomeFiller extends Filler implements Function<Record, Income> {
		@Override
		public Income apply(Record r) {
			Income income = new Income();
			income.setCreationDate(getValue(r, INCOME.CREATION_DATE));
			income.setCreationUser(getValue(r, INCOME.CREATION_USER));
			income.setModificationDate(getValue(r, INCOME.MODIFICATION_DATE));
			income.setModificationUser(getValue(r, INCOME.MODIFICATION_USER));
			return income
					.setAddress(getValue(r, INCOME.ADDRESS))
					.setBankAccount(getValue(r, INCOME.BANK_ACCOUNT))
					.setBankAlias(getValue(r, INCOME.BANK_ALIAS))
					.setBic(getValue(r, INCOME.BIC))
					.setCarrierPacking(getValue(r, INCOME.CARRIER_PACKING))
					.setComments(getValue(r, INCOME.COMMENTS))
					.setDaysBetweenPymnt(getValue(r, INCOME.DAYS_BETWEEN_PYMNTS) != null ? getValue(r, INCOME.DAYS_BETWEEN_PYMNTS).intValue() : null)
					.setDaysToFirstPymnt(getValue(r, INCOME.DAYS_TO_FIRST_PYMNT) != null ? getValue(r, INCOME.DAYS_TO_FIRST_PYMNT).intValue() : null)
					.setDomain(getValue(r, INCOME.DOMAIN))
					.setId(getValue(r, INCOME.ID))
					.setIssueDate(getValue(r, INCOME.ISSUE_TIME))
					.setNumberOfPymnts(getValue(r, INCOME.NUMBER_OF_PYMNTS) != null ? getValue(r, INCOME.NUMBER_OF_PYMNTS).intValue() : null)
					.setPayMethod(getValue(r, INCOME.PAY_METHOD))
					.setProject(new Project().setId(getValue(r, INCOME.PROJECT)))
					.setPymntDays(getValue(r, INCOME.PYMNT_DAYS))
					.setReferenceCode(getValue(r, INCOME.REFERENCE_CODE))
					.setRemarks(getValue(r, INCOME.REMARKS))
					.setScope(getValue(r, INCOME.SCOPE))
					.setSecurityLevel(getValue(r, INCOME.SECURITY_LEVEL) != null ? getValue(r, INCOME.SECURITY_LEVEL).intValue() : null)
					.setStatus(IncomeStatus.safeValueOf(getValue(r, INCOME.STATUS)))
					.setSupplier(getValue(r, INCOME.SUPPLIER))
					.setWorkplace(getValue(r, INCOME.WORKPLACE));
		}
	}
	
	public static class IncomeRegistryFiller extends Filler implements Function<Record, Income> {
		@Override
		public Income apply(Record r) {
			Income income = new Income();
			income.setCreationDate(getValue(r, INCOME.CREATION_DATE));
			income.setCreationUser(getValue(r, INCOME.CREATION_USER));
			income.setModificationDate(getValue(r, INCOME.MODIFICATION_DATE));
			income.setModificationUser(getValue(r, INCOME.MODIFICATION_USER));
			return income
					.setAddress(getValue(r, INCOME.ADDRESS))
					.setBankAccount(getValue(r, INCOME.BANK_ACCOUNT))
					.setBankAlias(getValue(r, INCOME.BANK_ALIAS))
					.setBic(getValue(r, INCOME.BIC))
					.setCarrierPacking(getValue(r, INCOME.CARRIER_PACKING))
					.setComments(getValue(r, INCOME.COMMENTS))
					.setDaysBetweenPymnt(getValue(r, INCOME.DAYS_BETWEEN_PYMNTS) != null ? getValue(r, INCOME.DAYS_BETWEEN_PYMNTS).intValue() : null)
					.setDaysToFirstPymnt(getValue(r, INCOME.DAYS_TO_FIRST_PYMNT) != null ? getValue(r, INCOME.DAYS_TO_FIRST_PYMNT).intValue() : null)
					.setDomain(getValue(r, INCOME.DOMAIN))
					.setId(getValue(r, INCOME.ID))
					.setIssueDate(getValue(r, INCOME.ISSUE_TIME))
					.setNumberOfPymnts(getValue(r, INCOME.NUMBER_OF_PYMNTS) != null ? getValue(r, INCOME.NUMBER_OF_PYMNTS).intValue() : null)
					.setPayMethod(getValue(r, INCOME.PAY_METHOD))
					.setProject(new Project().setId(getValue(r, INCOME.PROJECT)))
					.setPymntDays(getValue(r, INCOME.PYMNT_DAYS))
					.setReferenceCode(getValue(r, INCOME.REFERENCE_CODE))
					.setRemarks(getValue(r, INCOME.REMARKS))
					.setScope(getValue(r, INCOME.SCOPE))
					.setSecurityLevel(getValue(r, INCOME.SECURITY_LEVEL) != null ? getValue(r, INCOME.SECURITY_LEVEL).intValue() : null)
					.setStatus(IncomeStatus.safeValueOf(getValue(r, INCOME.STATUS)))
					.setSupplier(getValue(r, INCOME.SUPPLIER))
					.setWorkplace(getValue(r, INCOME.WORKPLACE))
					.setSupplierName(getValue(r, REGISTRY.NAME));
		}
	}
	
	public static class IncomeDetailFiller extends Filler implements Function<Record, IncomeDetail> {
		
		@Override
		public IncomeDetail apply(Record r) {
			IncomeDetail incomeDetail = new IncomeDetail();
			incomeDetail.setCreationDate(getValue(r, INCOME_DETAIL.CREATION_DATE));
			incomeDetail.setCreationUser(getValue(r, INCOME_DETAIL.CREATION_USER));
			incomeDetail.setModificationDate(getValue(r, INCOME_DETAIL.MODIFICATION_DATE));
			incomeDetail.setModificationUser(getValue(r, INCOME_DETAIL.MODIFICATION_USER));
			return incomeDetail
					.setDescription(getValue(r, INCOME_DETAIL.DESCRIPTION))
					.setDiscountExpression(getValue(r, INCOME_DETAIL.DISCOUNT_EXPR))
					.setDomain(getValue(r, INCOME_DETAIL.DOMAIN))
					.setId(getValue(r, INCOME_DETAIL.ID))
					.setIncome(new Income()
							.setId(getValue(r, INCOME_DETAIL.INCOME)))
					.setItem(new OldItem()
							.setId(getValue(r, INCOME_DETAIL.ITEM)))
					.setLine(getValue(r, INCOME_DETAIL.LINE))
					.setPrice(getValue(r, INCOME_DETAIL.PRICE))
					.setProject(new Project()
							.setId(getValue(r, INCOME_DETAIL.PROJECT)))
					.setPurchaseDetail(getValue(r, INCOME_DETAIL.PURCHASE_DETAIL))
					.setQuantity(getValue(r, INCOME_DETAIL.QUANTITY))
					.setWarehouse(getValue(r, INCOME_DETAIL.WAREHOUSE));
		}
	}
	
	public static class DataResponseFiller extends Filler implements Function<Record, DataResponse> {
		
		@Override
		public DataResponse apply(Record r) {
			return new DataResponse().setDomain(getValue(r, DATA_RESPONSE.DOMAIN))
					.setId(getValue(r, DATA_RESPONSE.ID))
					.setResponseDate(getValue(r, DATA_RESPONSE.RESPONSE_DATE))
					.setCode(getValue(r, DATA_RESPONSE.CODE))
					.setSource(DataResponseSource.safeValueOf(getValue(r, DATA_RESPONSE.SOURCE)))
					.setSourceId(getValue(r, DATA_RESPONSE.SOURCE_ID))
					.setDataRequest(getValue(r, DATA_RESPONSE.DATA_REQUEST))
					.setCreationDate(getValue(r, DATA_RESPONSE.CREATION_DATE))
					.setCreationUser(getValue(r, DATA_RESPONSE.CREATION_USER))
					.setModificationDate(getValue(r, DATA_RESPONSE.MODIFICATION_DATE))
					.setModificationUser(getValue(r, DATA_RESPONSE.MODIFICATION_USER));
		}
	}
	
	public static class DataResponseDetailFiller extends Filler implements Function<Record, DataResponseDetail> {
		
		@Override
		public DataResponseDetail apply(Record r) {
			DataResponseDetail dataResponseDetail = new DataResponseDetail();
			dataResponseDetail.setCreationDate(getValue(r, DATA_RESPONSE_DETAIL.CREATION_DATE));
			dataResponseDetail.setCreationUser(getValue(r, DATA_RESPONSE_DETAIL.CREATION_USER));
			dataResponseDetail.setModificationDate(getValue(r, DATA_RESPONSE_DETAIL.MODIFICATION_DATE));
			dataResponseDetail.setModificationUser(getValue(r, DATA_RESPONSE_DETAIL.MODIFICATION_USER));
			return dataResponseDetail.setDomain(getValue(r, DATA_RESPONSE_DETAIL.DOMAIN))
					.setId(getValue(r, DATA_RESPONSE_DETAIL.ID))
					.setDataResponse(getValue(r, DATA_RESPONSE_DETAIL.DATA_RESPONSE))
					.setDataVariable(getValue(r, DATA_RESPONSE_DETAIL.DATA_VARIABLE))
					.setDataValue(getValue(r, DATA_RESPONSE_DETAIL.DATA_VALUE));
		}
	}
	
	public static class ContractFiller extends Filler implements Function<Record, Contract> {
		
		@Override
		public Contract apply(Record r) {
			return new Contract()
					.setId(getValue(r, CONTRACT.ID))
					.setDomain(getValue(r, CONTRACT.DOMAIN))
					.setPerson(getValue(r, CONTRACT.PERSON))
					.setWorkplace(getValue(r, CONTRACT.WORKPLACE))
					.setStartDate(getValue(r, CONTRACT.START_DATE))
					.setEndDate(getValue(r, CONTRACT.END_DATE))
					.setCalendar(getValue(r, CONTRACT.CALENDAR))
					.setDescription(getValue(r, CONTRACT.DESCRIPTION))
				//TODO	.setSepeStatus(ContractStatus.values()[getValue(r, CONTRACT.SEPE_STATUS)])
					.setRegistration(getValue(r, CONTRACT.REGISTRATION))
					.setSeniorityDate(getValue(r, CONTRACT.SENIORITY_DATE))
					.setEnterpriseActivity(getValue(r, CONTRACT.ENTERPRISE_ACTIVITY))
					.setEnterpriseCCCRegime(CCCType.getSsRegimeType(getValue(r, ENTERPRISE_CCC.TYPE)))
					.setAgreementLevel(getValue(r, CONTRACT.AGREEMENT_LEVEL))
					
					.setEnterpriseCCC(getValue(r, ENTERPRISE_CCC.CCC))
					.setPersonDocument(getValue(r, REGISTRY.DOCUMENT))
					.setPersonSsNumber(getValue(r, PERSON.SOCIAL_SECURITY_NUM))
					.setPersonName(getValue(r, REGISTRY.NAME))

				//TODO	.setModel(ContractModel.values()[getValue(r, CONTRACT.MODEL)])
					.setCategoryDescription(getValue(r, CONTRACT.CATEGORY_DESCRIPTION));
				//TODO	.setSsStatus(ContractStatus.values()[getValue(r, CONTRACT.SS_STATUS)]);
		}
	}
	
	public static class ContractExtendedDataFiller extends Filler implements Function<Record, ContractExtendedData> {
		@Override
		public ContractExtendedData apply(Record r) {
			return new ContractExtendedData()
					.setGrossSalaryLastMonth(getValue(r, ContractDAO.SALARY_CGC_BASE))
					.setTotalMarksLastMonth(getValue(r, ContractDAO.MARK_TOTAL_TIME))
					.setId(getValue(r, CONTRACT.ID))
					.setDomain(getValue(r, CONTRACT.DOMAIN))
					.setPerson(getValue(r, CONTRACT.PERSON))
					.setWorkplace(getValue(r, CONTRACT.WORKPLACE))
					.setWorkplaceName(getValue(r, WORKPLACE.DESCRIPTION))
					.setStartDate(getValue(r, CONTRACT.START_DATE))
					.setEndDate(getValue(r, CONTRACT.END_DATE))
					.setContractType(getValue(r, ContractDAO.CONTRACT_TYPE))
					.setPersonName(getValue(r, ContractDAO.PERSON_FULL_NAME))
					.setDomainName(getValue(r, DOMAIN.NAME))
					.setPersonDocument(getValue(r, REGISTRY.DOCUMENT))
					.setPersonSsNumber(getValue(r, PERSON.SOCIAL_SECURITY_NUM))
					;
		}
	}
	
	public static class ContractDataFiller extends Filler implements Function<Record, ContractData> {
		
		@Override
		public ContractData apply(Record r) {
			return new ContractData()
					.setId(getValue(r, CONTRACT_DATA.ID))
					.setDomain(getValue(r, CONTRACT_DATA.DOMAIN))
					.setName(getValue(r, CONTRACT_DATA.NAME))
					.setContract(getValue(r, CONTRACT_DATA.CONTRACT))
					.setExpression(getValue(r, CONTRACT_DATA.EXPRESSION))
					.setStartDate(getValue(r, CONTRACT_DATA.START_DATE))
					.setEndDate(getValue(r, CONTRACT_DATA.END_DATE));
		}
	}
	
	public static class IrpfDataFiller extends Filler implements Function<Record, IrpfData> {
		
		@Override
		public IrpfData apply(Record r) {
			return new IrpfData()
					.setId(getValue(r, IRPF_DATA.ID))
					.setDomain(getValue(r, IRPF_DATA.DOMAIN))
					.setDisability(getValue(r, IRPF_DATA.DISABILITY_LEVEL));
			// TODO AÑADIR LOS PARÁMETROS QUE FALTAN.
		}
	}

	public static class AgreementLevelCategoryFiller extends Filler implements Function<Record, AgreementLevelCategory> {
		
		@Override
		public AgreementLevelCategory apply(Record r) {
			return new AgreementLevelCategory()
					.setId(getValue(r, AGREEMENT_LEVEL_CATEGORY.ID))
					.setDomain(getValue(r, AGREEMENT_LEVEL_CATEGORY.DOMAIN))
					.setAgreementLevel(getValue(r, AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
					.setDescription(getValue(r, AGREEMENT_LEVEL_CATEGORY.DESCRIPTION))
					.setLevel(null == getValue(r, AGREEMENT_LEVEL.ID) 
						? null 
						: new AgreementLevel()
							.setId(getValue(r, AGREEMENT_LEVEL.ID))
							.setDomain(getValue(r, AGREEMENT_LEVEL.DOMAIN))
							.setAgreement(null == getValue(r, AGREEMENT_LEVEL.AGREEMENT) ? null : getValue(r, AGREEMENT_LEVEL.AGREEMENT))
							.setDescription(null == getValue(r, AGREEMENT_LEVEL.DESCRIPTION) ? null : getValue(r, AGREEMENT_LEVEL.DESCRIPTION))
					);
		}
	}

	// ---------- COMMISSION

	public static class OfferDetailCommissionFiller extends Filler implements Function<Record, OfferDetailCommission> {
		
		@Override
		public OfferDetailCommission apply(Record r) {
			return new OfferDetailCommission()
					.setId(getValue(r, OFFER_DETAIL_COMMISSION.ID))
					.setDomain(getValue(r, OFFER_DETAIL_COMMISSION.DOMAIN))
					.setOfferDetail(checkField(r, OFFER_DETAIL.ID)
							? OfferDetailFiller.build(r)
							: new OfferDetail().setId(getValue(r, OFFER_DETAIL_COMMISSION.OFFER_DETAIL)))
					.setStatus(OfferDetailCommissionStatus.safeValueOf(getValue(r, OFFER_DETAIL_COMMISSION.STATUS)))
					.setPayDate(getValue(r, OFFER_DETAIL_COMMISSION.PAY_DATE))
					.setCommission(getValue(r, OFFER_DETAIL_COMMISSION.COMMISSION))
					.setAmount(getValue(r, OFFER_DETAIL_COMMISSION.AMOUNT));
			
		}
	}
	
	public static class InvoiceDetailCommissionFiller extends Filler implements Function<Record, InvoiceDetailCommission> {
		
		@Override
		public InvoiceDetailCommission apply(Record r) {
			Invoice i = new Invoice()
				.setId(getValue(r, INVOICE.ID))
				.setDomain(getValue(r, INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class,getValue(r, INVOICE.TYPE)))
				.setSeries(getValue(r, INVOICE.SERIES))
				.setNumber(getValue(r, INVOICE.NUMBER))
				.setReferenceCode(getValue(r, INVOICE.REFERENCE_CODE))
				.setIssueDate(getValue(r, INVOICE.ISSUE_DATE))
				.setTaxDate(getValue(r, INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,getValue(r, INVOICE.SECURITY_LEVEL)))
				.setRegistry(getValue(r, INVOICE.REGISTRY))
				.setRegistryDocument(getValue(r, INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,getValue(r, INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(getValue(r, INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(getValue(r, INVOICE.RNAME))
				.setScope(new Scope().setId(getValue(r, INVOICE.SCOPE)))
				.setActivity(new EnterpriseActivity().setId(getValue(r, INVOICE.ACTIVITY)))	
				.setInvestAsset(getValue(r, INVOICE.INVEST_ASSET))
				.setProject(getValue(r, INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class,getValue(r, INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(getValue(r, INVOICE.RECTIFICATION_INVOICE))	
				.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class,getValue(r, INVOICE.TRANSACTION)))
				.setRecorded(getValue(r, INVOICE.STATUS) == 1 )	
				.setSurcharge(getValue(r, INVOICE.SURCHARGE) == 1 )	
				.setWithholding(getValue(r, INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(getValue(r, INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(getValue(r, INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(getValue(r, INVOICE.INVESTMENT) == 1 )	
				.setService(getValue(r, INVOICE.SERVICE) == 1 )	
				.setAdvance(getValue(r, INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(getValue(r, INVOICE.TAXABLE_BASE))	
				.setVatQuota(getValue(r, INVOICE.VAT_QUOTA))	
				.setRetentionQuota(getValue(r, INVOICE.RETENTION_QUOTA))	
				.setTotal(getValue(r, INVOICE.TOTAL))	
				.setComments(getValue(r, INVOICE.COMMENTS))
				.setSeller(getValue(r, REGISTRY.ID))
				.setSellerName(getValue(r, REGISTRY.NAME));
			
			
			InvoiceDetail id = new InvoiceDetail()
					.setDescription(getValue(r, INVOICE_DETAIL.DESCRIPTION))
					.setDiscountExpression(getValue(r, INVOICE_DETAIL.DISCOUNT_EXPR))
					.setDomain(getValue(r, INVOICE_DETAIL.DOMAIN))
					.setId(getValue(r, INVOICE_DETAIL.ID))
					.setItem(new Item().setId(getValue(r, INVOICE_DETAIL.ITEM)))
					.setInvoice(i)
					.setPrice(getValue(r, INVOICE_DETAIL.PRICE))
					.setQuantity(getValue(r, INVOICE_DETAIL.QUANTITY));
			
			return new InvoiceDetailCommission()
					.setId(getValue(r, INVOICE_DETAIL_COMMISSION.ID))
					.setDomain(getValue(r, INVOICE_DETAIL_COMMISSION.DOMAIN))
					.setInvoiceDetail(id)
					.setStatus(InvoiceDetailCommissionStatus.safeValueOf(getValue(r, INVOICE_DETAIL_COMMISSION.STATUS)))
					.setPayDate(getValue(r, INVOICE_DETAIL_COMMISSION.PAY_DATE))
					.setCommission(getValue(r, INVOICE_DETAIL_COMMISSION.COMMISSION))
					.setAmount(getValue(r, INVOICE_DETAIL_COMMISSION.AMOUNT));
		}
	}
	
	public static class CommissionItemFiller extends Filler implements Function<Record, CommissionItem> {
		
		@Override
		public CommissionItem apply(Record r) {
			return new CommissionItem()
					.setId(getValue(r, COMMISSION_ITEM.ID))
					.setDomain(getValue(r, COMMISSION_ITEM.DOMAIN))
					.setCommission(getValue(r, COMMISSION_ITEM.COMMISSION))
					.setItem(getValue(r, COMMISSION_ITEM.ITEM))
					.setAmount(getValue(r, COMMISSION_ITEM.AMOUNT))
					.setQuantity(getValue(r, COMMISSION_ITEM.QUANTITY))
					.setRate(getValue(r, COMMISSION_ITEM.RATE));
		}
	}
	
	public static class CommissionCategoryFiller extends Filler implements Function<Record, CommissionCategory> {
		
		@Override
		public CommissionCategory apply(Record r) {
			return new CommissionCategory()
					.setId(getValue(r, COMMISSION_CATEGORY.ID))
					.setDomain(getValue(r, COMMISSION_CATEGORY.DOMAIN))
					.setCommission(getValue(r, COMMISSION_CATEGORY.COMMISSION))
					.setCategory(getValue(r, COMMISSION_CATEGORY.CATEGORY))
					.setQuantity(r .getValue(COMMISSION_CATEGORY.QUANTITY))
					.setRate(getValue(r, COMMISSION_CATEGORY.RATE));
		}
	}
	
	public static class CommissionTypeCommissionFiller extends Filler implements Function<Record, CommissionTypeCommission> {
		
		@Override
		public CommissionTypeCommission apply(Record r) {
			return new CommissionTypeCommission()
					.setId(getValue(r, COMMISSION_TYPE_COMMISSION.ID))
					.setDomain(getValue(r, COMMISSION_TYPE_COMMISSION.DOMAIN))
					.setCommission(getValue(r, COMMISSION_TYPE_COMMISSION.COMMISSION))
					.setCommissionType(getValue(r, COMMISSION_TYPE_COMMISSION.COMMISSION_TYPE));
		}
	}

	public static class CommissionFiller extends Filler implements Function<Record, Commission> {
		
		@Override
		public Commission apply(Record r) {
			return new Commission()
					.setId(getValue(r, COMMISSION.ID))
					.setDomain(getValue(r, COMMISSION.DOMAIN))
					.setName(getValue(r, COMMISSION.NAME))
					.setStartDate(getValue(r, COMMISSION.START_DATE))
					.setEndDate(getValue(r, COMMISSION.END_DATE));
		}
	}
	
	public static class CommissionTypeFiller extends Filler implements Function<Record, CommissionType> {
		
		@Override
		public CommissionType apply(Record r) {
			return new CommissionType()
					.setId(getValue(r, COMMISSION_TYPE.ID))
					.setDomain(getValue(r, COMMISSION_TYPE.DOMAIN))
					.setName(getValue(r, COMMISSION_TYPE.NAME))
					.setRate(getValue(r, COMMISSION_TYPE.RATE));
		}
	}
	
	public static class MailTemplateFiller extends Filler implements Function<Record, MailTemplate> {
		
		@Override
		public MailTemplate apply(Record r) {
			return new MailTemplate()
					.setId(getValue(r, MK_TEMPLATE.ID))
					.setDomain(getValue(r, MK_TEMPLATE.DOMAIN))
					.setName(getValue(r, MK_TEMPLATE.NAME))
					.setActive(getValue(r, MK_TEMPLATE.ACTIVE) == 1)
					.setBackgroundColor(getValue(r, MK_TEMPLATE.BACKGROUND_COLOR))
					.setCreationDate(getValue(r, MK_TEMPLATE.CREATIONDATE))
					.setFooterTemplate(getValue(r, MK_TEMPLATE.FOOTER_TEMPLATE))
					.setHeaderTemplate(getValue(r, MK_TEMPLATE.HEADER_TEMPLATE))
					.setScope(getValue(r, MK_TEMPLATE.SCOPE))
					.setSubject(getValue(r, MK_TEMPLATE.SUBJECT))
					.setTitleColor(getValue(r, MK_TEMPLATE.TITLE_COLOR))
					.setWidth(getValue(r, MK_TEMPLATE.WIDTH));
		}
	}
	
	public static class UdapaQualityFiller extends Filler implements Function<Record, UdapaQuality> {
		
		@Override
		public UdapaQuality apply(Record r) {
			Supplier supplier = new Supplier();
			supplier.setAlias(getValue(r, REGISTRY.ALIAS));
			supplier.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(getValue(r, REGISTRY.SECURITY_LEVEL)));
			supplier.setDocument(getValue(r, REGISTRY.DOCUMENT));
			supplier.setDocumentType(DocumentType.safeValueOf(getValue(r, REGISTRY.DOCUMENT_TYPE)));
			supplier.setDomain(new Domain().setId(getValue(r, REGISTRY.DOMAIN)));
			supplier.setId(getValue(r, REGISTRY.ID));
			supplier.setName(getValue(r, REGISTRY.NAME));
			supplier.setSecurityLevel(SecurityLevel.safeValueOf(getValue(r, REGISTRY.SECURITY_LEVEL)));
			supplier.setLegalPerson(AonEnumUtils.getBoolean(getValue(r, REGISTRY.TYPE)));
			return new UdapaQuality()
					.setDataResponse(new DataResponse()
							.setDomain(getValue(r, DATA_RESPONSE.DOMAIN))
							.setId(getValue(r, DATA_RESPONSE.ID))
							.setResponseDate(getValue(r, DATA_RESPONSE.RESPONSE_DATE))
							.setCode(getValue(r, DATA_RESPONSE.CODE))
							.setSource(DataResponseSource.safeValueOf(getValue(r, DATA_RESPONSE.SOURCE)))
							.setSourceId(getValue(r, DATA_RESPONSE.SOURCE_ID))
							.setCreationDate(getValue(r, DATA_RESPONSE.CREATION_DATE))
							.setCreationUser(getValue(r, DATA_RESPONSE.CREATION_USER))
							.setModificationDate(getValue(r, DATA_RESPONSE.MODIFICATION_DATE))
							.setModificationUser(getValue(r, DATA_RESPONSE.MODIFICATION_USER)))
					.setSupplier(supplier)
					.setProduct(getValue(r, INCOME_DETAIL.DESCRIPTION));
			
		}
	}

	public static class PaturpatQualityFiller extends Filler implements Function<Record, PaturpatQuality> {
		
		@Override
		public PaturpatQuality apply(Record r) {

			return new PaturpatQuality()
				.setDataResponse(new DataResponse()
							.setDomain(getValue(r, DATA_RESPONSE.DOMAIN))
							.setId(getValue(r, DATA_RESPONSE.ID))
							.setResponseDate(getValue(r, DATA_RESPONSE.RESPONSE_DATE))
							.setCode(getValue(r, DATA_RESPONSE.CODE))
							.setSource(DataResponseSource.safeValueOf(getValue(r, DATA_RESPONSE.SOURCE)))
							.setSourceId(getValue(r, DATA_RESPONSE.SOURCE_ID))
							.setCreationDate(getValue(r, DATA_RESPONSE.CREATION_DATE))
							.setCreationUser(getValue(r, DATA_RESPONSE.CREATION_USER))
							.setModificationDate(getValue(r, DATA_RESPONSE.MODIFICATION_DATE))
							.setModificationUser(getValue(r, DATA_RESPONSE.MODIFICATION_USER)))
				.setProduct(getValue(r, PRODUCT.NAME) + " #" + getValue(r, ITEM.SERIAL_NUMBER));
			
		}
	}
	
	public static class ItemAddInfoFiller extends Filler implements Function<Record, ItemAddInfo> {
		
		@Override
		public ItemAddInfo apply(Record r) {

			return new ItemAddInfo()
				.setDomain(getValue(r, ITEM_ADDINFO.DOMAIN))
				.setId(getValue(r, ITEM_ADDINFO.ID))
				.setProduct(getValue(r, ITEM_ADDINFO.PRODUCT))
				.setItem(getValue(r, ITEM_ADDINFO.ITEM))
				.setAttribute(getValue(r, ITEM_ADDINFO.ATTRIBUTE))
				.setValue(getValue(r, ITEM_ADDINFO.VALUE))
				.setDate(getValue(r, ITEM_ADDINFO.VALUE_DATE));
		}
	}

	
}
