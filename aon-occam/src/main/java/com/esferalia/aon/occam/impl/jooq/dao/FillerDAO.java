package com.esferalia.aon.occam.impl.jooq.dao;

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
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
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

import java.util.function.Function;

import org.jooq.Record;

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
import com.esferalia.aon.occam.api.model.warehouse.Inventory;
import com.esferalia.aon.occam.api.model.warehouse.InventoryDetail;
import com.esferalia.aon.occam.api.model.warehouse.PaturpatQuality;
import com.esferalia.aon.occam.api.model.warehouse.UdapaQuality;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO.ItemFiller;
import com.esferalia.aon.occam.impl.jooq.dao.OfferDAO.OfferDetailFiller;
import com.esferalia.aon.occam.impl.jooq.dao.SellerDAO.SellerFiller;
import com.esferalia.aon.watson.util.AonEnumUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

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
			return buildDomain(r, DOMAIN);
		}
		
		public static Domain buildDomain(Record r, com.esferalia.aon.jooq.tables.Domain domainTable) {
			return fillDomain(r, new Domain(), domainTable);
		}
		
		public static Domain fillDomain(Record r, Domain domain, com.esferalia.aon.jooq.tables.Domain domainTable) {
			return domain
				.setId(r.getValue(domainTable.ID))
				.setName(r.getValue(domainTable.NAME))
				.setDescription(r.getValue(domainTable.DESCRIPTION))
				.setParentId(r.getValue(domainTable.PARENT))
				.setDomainType(DomainType.safeValueOf(r.getValue(domainTable.TYPE)))
				.setScope(r.getValue(domainTable.SCOPE))
				.setEnableHeredity(getBoolean(r, domainTable.ENABLEHEREDITY))
				.setDomainManagement(getBoolean(r, domainTable.DOMAINMANAGEMENT))
				.setDisableDomainManagement(getBoolean(r, domainTable.DISABLEDOMAINMANAGEMENT))
				.setMaxDocumentSize(getValue(r, domainTable.MAXDOCUMENTSIZE))
				.setMaxTotalDocumentSize(getValue(r, domainTable.MAXTOTALDOCUMENTSIZE))
				.setMaxDefinedUsers(r.getValue(domainTable.MAXDEFINEDUSERS))
				.setActive(getBoolean(r, domainTable.ACTIVE))
				.setOwner(getValue(r, domainTable.OWNER))
				.setCreationUser(getValue(r, domainTable.CREATION_USER))
				.setCreationDate(getValue(r, domainTable.CREATION_DATE))
				.setModificationUser(getValue(r, domainTable.MODIFICATION_USER))
				.setModificationDate(getValue(r, domainTable.MODIFICATION_DATE))
				.setLastAccessUser(getValue(r, domainTable.LASTACCESS_USER))
				.setLastAccessDate(getValue(r, domainTable.LASTACCESS_DATE))
				.setExpirationDate(getValue(r, domainTable.EXPIRATIONDATE))
				.setAonCustomer(r.getValue(domainTable.AONCUSTOMER))
				.setAonStatus(AonStatus.safeValueOf(r.getValue(domainTable.AONSTATUS)))
				;	
		}
	}

	public static class ApplicationParameterFiller implements Function<Record, ApplicationParameter> {
		@Override
		public ApplicationParameter apply(Record r) {
			return new ApplicationParameter()
					.setId(r.getValue(APP_PARAM.ID))
					.setDomain(r.getValue(APP_PARAM.DOMAIN))
					.setName(r.getValue(APP_PARAM.NAME))
					.setValue(r.getValue(APP_PARAM.VALUE));
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
					.setDocumentType(DocumentType.safeValueOf(r.getValue(REGISTRY.DOCUMENT_TYPE)))
					.setDomain(new Domain().setId(r.getValue(REGISTRY.DOMAIN)))
					.setId(r.getValue(REGISTRY.ID))
					.setName(r.getValue(REGISTRY.NAME))
					.setNationality(null) // TODO
					.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(REGISTRY.SECURITY_LEVEL)))
					.setLegalPerson(AonEnumUtils.getBoolean( r.getValue(REGISTRY.TYPE)));			
		}
	}
	
	public static class PersonFiller implements Function<Record, Person> {
		@Override
		public Person apply(Record r) {
			Person person = new Person();
			person.setAlias(r.getValue(REGISTRY.ALIAS));
			person.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(r.getValue(REGISTRY.SECURITY_LEVEL)));
			person.setDocument(r.getValue(REGISTRY.DOCUMENT));
			person.setDocumentCountry(null); // TODO
			person.setDocumentType(DocumentType.safeValueOf(r.getValue(REGISTRY.DOCUMENT_TYPE)));
			person.setId(r.getValue(REGISTRY.ID));
			person.setName(r.getValue(REGISTRY.NAME));
			person.setNationality(null); // TODO
			person.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(REGISTRY.SECURITY_LEVEL)));
			person.setLegalPerson(AonEnumUtils.getBoolean(r.getValue(REGISTRY.TYPE)));
			person.setDomain(new Domain().setId(r.getValue(PERSON.DOMAIN)));
			return person.setBirthDate(r.getValue(PERSON.BIRTH_DATE))
					.setFirstName(r.getValue(PERSON.NAME))
					.setFirstSurname(r.getValue(PERSON.FIRST_SURNAME))
					.setSecondSurname(r.getValue(PERSON.SECOND_SURNAME))
					.setGender(Gender.safeValueOf(r.getValue(PERSON.GENDER)))
					.setMaritalStatus(MaritalStatus.safeValueOf(r.getValue(PERSON.MARITAL_STATUS)))
					.setSocialSecurityNum(r.getValue(PERSON.SOCIAL_SECURITY_NUM));				
		}
	}
	
	public static class RItemFiller  implements Function<Record,RegistryItem> {

		@Override
		public RegistryItem apply(Record r) {
			return new RegistryItem()
					.setId(r.getValue(RITEM.ID))
					.setDomain(r.getValue(RITEM.DOMAIN))
					.setRegistry(r.getValue(RITEM.REGISTRY))
					.setItem(ItemFiller.build(r))
					.setType(RegistryMode.safeValueOf(r.getValue(RITEM.TYPE)))
					.setCode(r.getValue(RITEM.CODE))
					.setEdiSalesCode(r.get(RITEM.EDI_SALES_CODE))
					.setCustomerFee(r.get(RITEM.CUSTOMER_FEE))
//					.setSeller(SellerFiller.build(r))
					.setPrice(r.getValue(RITEM.PRICE))
					.setDiscountExpr(r.getValue(RITEM.DISCOUNT_EXPR))
					.setWorkplace(r.getValue(RITEM.WORKPLACE))
					.setPriority(Priority.safeValueOf(r.getValue(RITEM.PRIORITY)))
					.setStatus(RegistryItemStatus.safeValueOf(r.getValue(RITEM.STATUS)))
					.setQuantity(r.getValue(RITEM.QUANTITY))
					.setStartDate(r.getValue(RITEM.START_DATE))
					.setEndDate(r.getValue(RITEM.END_DATE))
					.setCreationDate(r.getValue(RITEM.CREATION_DATE))
					.setCreationUser(r.getValue(RITEM.CREATION_USER))
					.setModificationDate(r.getValue(RITEM.MODIFICATION_DATE))
					.setModificationUser(r.getValue(RITEM.MODIFICATION_USER));
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
			detail.setStatus(PurchaseDetailStatus.safeValueOf(r.getValue(PURCHASE_DETAIL.STATUS)));
			detail.setProposalDetail(r.getValue(PURCHASE_DETAIL.PROPOSAL_DETAIL));
			if(r.getValue(PURCHASE_DETAIL.SOURCE) != null)
				detail.setSource(PurchaseSourceType.safeValueOf(r.getValue(PURCHASE_DETAIL.SOURCE)));
			detail.setSourceId(r.getValue(PURCHASE_DETAIL.SOURCE_ID));
			detail.setDelivered(r.getValue(PURCHASE_DETAIL.DELIVERED));
			detail.setCarrier(r.getValue(PURCHASE_DETAIL.CARRIER));
			detail.setCarrierPacking(r.getValue(PURCHASE_DETAIL.CARRIER_PACKING));
			

			return detail;
		}
	}
	
	public static class PurchaseDetailItemFiller implements Function<Record, PurchaseDetail> {
		
		@Override
		public PurchaseDetail apply(Record r) {
			PurchaseDetail detail = new PurchaseDetail();
			detail.setId(r.getValue(PURCHASE_DETAIL.ID));
			detail.setDomain(r.getValue(PURCHASE_DETAIL.DOMAIN));
			detail.setPurchaseId(r.getValue(PURCHASE_DETAIL.PURCHASE));
			detail.setPurchase(new Purchase()
					.setId(r.getValue(PURCHASE.ID))
					.setPurchaseReference(r.getValue(PURCHASE.PURCHASE_REFERENCE))
					.setSupplier(r.getValue(REGISTRY.ID))
					.setSupplierName(r.getValue(REGISTRY.NAME))
					.setAddress(r.getValue(PURCHASE.ADDRESS))
					);
			detail.setItem(r.getValue(PURCHASE_DETAIL.ITEM));
			detail.setLine(r.getValue(PURCHASE_DETAIL.LINE).intValue());
			detail.setDescription(r.getValue(PURCHASE_DETAIL.DESCRIPTION));
			detail.setQuantity(r.getValue(PURCHASE_DETAIL.QUANTITY));
			detail.setPrice(r.getValue(PURCHASE_DETAIL.PRICE));
			detail.setDiscountExpression(r.getValue(PURCHASE_DETAIL.DISCOUNT_EXPR));
			detail.setTaxes(r.getValue(PURCHASE_DETAIL.TAXES));
			detail.setStatus(PurchaseDetailStatus.safeValueOf(r.getValue(PURCHASE_DETAIL.STATUS)));
			detail.setProposalDetail(r.getValue(PURCHASE_DETAIL.PROPOSAL_DETAIL));
			if(r.getValue(PURCHASE_DETAIL.SOURCE) != null)
				detail.setSource(PurchaseSourceType.safeValueOf(r.getValue(PURCHASE_DETAIL.SOURCE)));
			detail.setSourceId(r.getValue(PURCHASE_DETAIL.SOURCE_ID));
			detail.setDelivered(r.getValue(PURCHASE_DETAIL.DELIVERED));
			detail.setCarrier(r.getValue(PURCHASE_DETAIL.CARRIER));
			detail.setCarrierPacking(r.getValue(PURCHASE_DETAIL.CARRIER_PACKING));
			detail.setProductId(r.getValue(PRODUCT.ID));
			detail.setProductCode(r.getValue(PRODUCT.CODE));
			detail.setProductName(r.getValue(PRODUCT.NAME));
			detail.setItem2(new OldItem().setId(r.getValue(ITEM.ID))
				.setBarcode(r.getValue(ITEM.BARCODE))
				.setCreationDate(r.getValue(ITEM.CREATION_DATE))
				.setCreationUser(r.getValue(ITEM.CREATION_USER))
				.setDescription(r.getValue(ITEM.DESCRIPTION))
				.setDetail(r.getValue(ITEM.DETAIL))
				.setDetail2(r.getValue(ITEM.DETAIL2))
				.setDetail3(r.getValue(ITEM.DETAIL3))
				.setDomain(r.getValue(ITEM.DOMAIN))
				.setExpensesFixed(r.getValue(ITEM.EXPENSES_FIXED))
				.setExpensesPercent(r.getValue(ITEM.EXPENSES_PERCENT))
				.setInternet(r.getValue(ITEM.INTERNET) == 1)
				.setModificationDate(r.getValue(ITEM.MODIFICATION_DATE))
				.setModificationUser(r.getValue(ITEM.MODIFICATION_USER))
				.setPackMeasurement(r.getValue(ITEM.PACK_MEASUREMENT))
				.setPackUnits(r.getValue(ITEM.PACK_UNITS).doubleValue())
				.setPrice(r.getValue(ITEM.PRICE))
				.setProductId(r.getValue(ITEM.PRODUCT))
				.setProfitPercent(r.getValue(ITEM.PROFIT_PERCENT))
				.setPurchasePrice(r.getValue(ITEM.PURCHASE_PRICE))
				.setSerialNumber(r.getValue(ITEM.SERIAL_NUMBER))
				.setSerialDate(r.getValue(ITEM.SERIAL_DATE))
				.setStatus(r.getValue(ITEM.STATUS))
				.setProduct(new OldProduct().setId(r.getValue(PRODUCT.ID))
					.setName(r.getValue(PRODUCT.NAME))
					.setDomain(r.getValue(PRODUCT.DOMAIN))
					.setCode(r.getValue(PRODUCT.CODE))
					.setComposition(r.getValue(PRODUCT.COMPOSITION) == 1)
					.setCompositionPrice(r.getValue(PRODUCT.COMPOSITION_PRICE) == 1)
					.setCreationDate(r.getValue(PRODUCT.CREATION_DATE))
					.setCreationUser(r.getValue(PRODUCT.CREATION_USER))
					.setInventoriable(r.getValue(PRODUCT.INVENTORIABLE) == 1)
					.setKind(r.getValue(PRODUCT.KIND))
					.setLotable(r.getValue(PRODUCT.LOTABLE) == 1)
					.setManufactured(r.getValue(PRODUCT.MANUFACTURED))
					.setModificationDate(r.getValue(PRODUCT.MODIFICATION_DATE))
					.setModificationUser(r.getValue(PRODUCT.MODIFICATION_USER))
					.setPackaged(r.getValue(PRODUCT.PACKAGED) == 1)
					.setPurchaseAccount(r.getValue(PRODUCT.PURCHASE_ACCOUNT))
					.setRetention(r.getValue(PRODUCT.RETENTION)) 
					.setSalesAccount(r.getValue(PRODUCT.SALES_ACCOUNT))
					.setSerializable(r.getValue(PRODUCT.SERIALIZABLE) == 1)
					.setStatus(r.getValue(PRODUCT.STATUS))
					.setType(r.getValue(PRODUCT.TYPE))
					.setVat(r.getValue(PRODUCT.VAT))));
			return detail;
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
	
	public static class AonCompanyFiller implements Function<Record, AonCompany> {
		@Override
		public AonCompany apply(Record r) {
			com.esferalia.aon.jooq.tables.Domain domain = DOMAIN.as("d");
			com.esferalia.aon.jooq.tables.Domain parent = DOMAIN.as("p");

			Domain d = new Domain()
					.setId(r.getValue(domain.ID))
					.setName(r.getValue(domain.NAME))
					.setActive(r.getValue(domain.ACTIVE) == 1)
					.setParentId(r.getValue(domain.PARENT))
					.setMaxDefinedUsers(r.getValue(domain.MAXDEFINEDUSERS));
			Company company = new Company();
			company.setDomain(d);
			company.setAlias(r.getValue(REGISTRY.ALIAS));
			company.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(r.getValue(REGISTRY.SECURITY_LEVEL)));
			company.setDocumentCountry(Country.valueOf(r.getValue(REGISTRY.DOCUMENT_COUNTRY)));
			company.setDocumentType(DocumentType.safeValueOf(r.getValue(REGISTRY.DOCUMENT_TYPE)));
			company.setNationality(r.getValue(REGISTRY.NATIONALITY) != null ? Country.valueOf(r.getValue(REGISTRY.NATIONALITY)): null);
			company.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(REGISTRY.SECURITY_LEVEL)));
			company.setLegalPerson(AonEnumUtils.getBoolean(r.getValue(REGISTRY.TYPE)));	
			company
				.setDocument(r.getValue(REGISTRY.DOCUMENT))
				.setId(r.getValue(REGISTRY.ID))
				.setName(r.getValue(REGISTRY.NAME));
			company
				.setActive(r.getValue(COMPANY.ACTIVE) == 1)
				.seteInvoice(r.getValue(COMPANY.E_INVOICE) == 1)
				.setSurcharge(r.getValue(COMPANY.SURCHARGE) == 1)
				.setVatAccrualPayment(r.getValue(COMPANY.VAT_ACCRUAL_PAYMENT) == 1)
				.setWithholding(r.getValue(COMPANY.WITHHOLDING) == 1);

			return new AonCompany()
				.setDomain(new com.esferalia.aon.occam.api.model.Domain()
					.setId(r.getValue(domain.ID))
					.setName(r.getValue(domain.NAME))
					.setParentId(r.getValue(domain.PARENT))
					.setActive(AonEnumUtils.getBoolean(r.getValue(domain.ACTIVE)))
					.setDescription(r.getValue(domain.DESCRIPTION))
					.setDomainType(DomainType.values()[r.getValue(domain.TYPE)])
					.setScope(r.getValue(domain.SCOPE))
					.setEnableHeredity(AonEnumUtils.getBoolean(r.getValue(domain.ENABLEHEREDITY)))
					.setDomainManagement(AonEnumUtils.getBoolean(r.getValue(domain.DOMAINMANAGEMENT))))
			
				.setParentDomain(new com.esferalia.aon.occam.api.model.Domain()
					.setId(r.getValue(parent.ID))
					.setName(r.getValue(parent.NAME)))
				.setShared(r.getValue(USER.SHARED) == 1)
				.setCompany(company)
				.setAdministration(Administration.safeValueOf(AonNumberUtils.toInteger(r.getValue(APP_PARAM.VALUE))));
		}
	}
	
	public static class IncomeFiller implements Function<Record, Income> {
		@Override
		public Income apply(Record r) {
			Income income = new Income();
			income.setCreationDate(r.getValue(INCOME.CREATION_DATE));
			income.setCreationUser(r.getValue(INCOME.CREATION_USER));
			income.setModificationDate(r.getValue(INCOME.MODIFICATION_DATE));
			income.setModificationUser(r.getValue(INCOME.MODIFICATION_USER));
			return income
					.setAddress(r.getValue(INCOME.ADDRESS))
					.setBankAccount(r.getValue(INCOME.BANK_ACCOUNT))
					.setBankAlias(r.getValue(INCOME.BANK_ALIAS))
					.setBic(r.getValue(INCOME.BIC))
					.setCarrierPacking(r.getValue(INCOME.CARRIER_PACKING))
					.setComments(r.getValue(INCOME.COMMENTS))
					.setDaysBetweenPymnt(r.getValue(INCOME.DAYS_BETWEEN_PYMNTS) != null ? r.getValue(INCOME.DAYS_BETWEEN_PYMNTS).intValue() : null)
					.setDaysToFirstPymnt(r.getValue(INCOME.DAYS_TO_FIRST_PYMNT) != null ? r.getValue(INCOME.DAYS_TO_FIRST_PYMNT).intValue() : null)
					.setDomain(r.getValue(INCOME.DOMAIN))
					.setId(r.getValue(INCOME.ID))
					.setIssueDate(r.getValue(INCOME.ISSUE_TIME))
					.setNumberOfPymnts(r.getValue(INCOME.NUMBER_OF_PYMNTS) != null ? r.getValue(INCOME.NUMBER_OF_PYMNTS).intValue() : null)
					.setPayMethod(r.getValue(INCOME.PAY_METHOD))
					.setProject(new Project().setId(r.getValue(INCOME.PROJECT)))
					.setPymntDays(r.getValue(INCOME.PYMNT_DAYS))
					.setReferenceCode(r.getValue(INCOME.REFERENCE_CODE))
					.setRemarks(r.getValue(INCOME.REMARKS))
					.setScope(r.getValue(INCOME.SCOPE))
					.setSecurityLevel(r.getValue(INCOME.SECURITY_LEVEL) != null ? r.getValue(INCOME.SECURITY_LEVEL).intValue() : null)
					.setStatus(IncomeStatus.safeValueOf(r.getValue(INCOME.STATUS)))
					.setSupplier(r.getValue(INCOME.SUPPLIER))
					.setWorkplace(r.getValue(INCOME.WORKPLACE));
		}
	}
	
	public static class IncomeRegistryFiller implements Function<Record, Income> {
		@Override
		public Income apply(Record r) {
			Income income = new Income();
			income.setCreationDate(r.getValue(INCOME.CREATION_DATE));
			income.setCreationUser(r.getValue(INCOME.CREATION_USER));
			income.setModificationDate(r.getValue(INCOME.MODIFICATION_DATE));
			income.setModificationUser(r.getValue(INCOME.MODIFICATION_USER));
			return income
					.setAddress(r.getValue(INCOME.ADDRESS))
					.setBankAccount(r.getValue(INCOME.BANK_ACCOUNT))
					.setBankAlias(r.getValue(INCOME.BANK_ALIAS))
					.setBic(r.getValue(INCOME.BIC))
					.setCarrierPacking(r.getValue(INCOME.CARRIER_PACKING))
					.setComments(r.getValue(INCOME.COMMENTS))
					.setDaysBetweenPymnt(r.getValue(INCOME.DAYS_BETWEEN_PYMNTS) != null ? r.getValue(INCOME.DAYS_BETWEEN_PYMNTS).intValue() : null)
					.setDaysToFirstPymnt(r.getValue(INCOME.DAYS_TO_FIRST_PYMNT) != null ? r.getValue(INCOME.DAYS_TO_FIRST_PYMNT).intValue() : null)
					.setDomain(r.getValue(INCOME.DOMAIN))
					.setId(r.getValue(INCOME.ID))
					.setIssueDate(r.getValue(INCOME.ISSUE_TIME))
					.setNumberOfPymnts(r.getValue(INCOME.NUMBER_OF_PYMNTS) != null ? r.getValue(INCOME.NUMBER_OF_PYMNTS).intValue() : null)
					.setPayMethod(r.getValue(INCOME.PAY_METHOD))
					.setProject(new Project().setId(r.getValue(INCOME.PROJECT)))
					.setPymntDays(r.getValue(INCOME.PYMNT_DAYS))
					.setReferenceCode(r.getValue(INCOME.REFERENCE_CODE))
					.setRemarks(r.getValue(INCOME.REMARKS))
					.setScope(r.getValue(INCOME.SCOPE))
					.setSecurityLevel(r.getValue(INCOME.SECURITY_LEVEL) != null ? r.getValue(INCOME.SECURITY_LEVEL).intValue() : null)
					.setStatus(IncomeStatus.safeValueOf(r.getValue(INCOME.STATUS)))
					.setSupplier(r.getValue(INCOME.SUPPLIER))
					.setWorkplace(r.getValue(INCOME.WORKPLACE))
					.setSupplierName(r.getValue(REGISTRY.NAME));
		}
	}
	
	public static class IncomeDetailFiller implements Function<Record, IncomeDetail> {
		
		@Override
		public IncomeDetail apply(Record r) {
			IncomeDetail incomeDetail = new IncomeDetail();
			incomeDetail.setCreationDate(r.getValue(INCOME_DETAIL.CREATION_DATE));
			incomeDetail.setCreationUser(r.getValue(INCOME_DETAIL.CREATION_USER));
			incomeDetail.setModificationDate(r.getValue(INCOME_DETAIL.MODIFICATION_DATE));
			incomeDetail.setModificationUser(r.getValue(INCOME_DETAIL.MODIFICATION_USER));
			return incomeDetail
					.setDescription(r.getValue(INCOME_DETAIL.DESCRIPTION))
					.setDiscountExpression(r.getValue(INCOME_DETAIL.DISCOUNT_EXPR))
					.setDomain(r.getValue(INCOME_DETAIL.DOMAIN))
					.setId(r.getValue(INCOME_DETAIL.ID))
					.setIncome(new Income()
							.setId(r.getValue(INCOME_DETAIL.INCOME)))
					.setItem(new OldItem()
							.setId(r.getValue(INCOME_DETAIL.ITEM)))
					.setLine(r.getValue(INCOME_DETAIL.LINE))
					.setPrice(r.getValue(INCOME_DETAIL.PRICE))
					.setProject(new Project()
							.setId(r.getValue(INCOME_DETAIL.PROJECT)))
					.setPurchaseDetail(r.getValue(INCOME_DETAIL.PURCHASE_DETAIL))
					.setQuantity(r.getValue(INCOME_DETAIL.QUANTITY))
					.setWarehouse(r.getValue(INCOME_DETAIL.WAREHOUSE));
		}
	}
	
	public static class DataResponseFiller implements Function<Record, DataResponse> {
		
		@Override
		public DataResponse apply(Record r) {
			return new DataResponse().setDomain(r.getValue(DATA_RESPONSE.DOMAIN))
					.setId(r.getValue(DATA_RESPONSE.ID))
					.setResponseDate(r.getValue(DATA_RESPONSE.RESPONSE_DATE))
					.setCode(r.getValue(DATA_RESPONSE.CODE))
					.setSource(DataResponseSource.safeValueOf(r.getValue(DATA_RESPONSE.SOURCE)))
					.setSourceId(r.getValue(DATA_RESPONSE.SOURCE_ID))
					.setDataRequest(r.getValue(DATA_RESPONSE.DATA_REQUEST))
					.setCreationDate(r.getValue(DATA_RESPONSE.CREATION_DATE))
					.setCreationUser(r.getValue(DATA_RESPONSE.CREATION_USER))
					.setModificationDate(r.getValue(DATA_RESPONSE.MODIFICATION_DATE))
					.setModificationUser(r.getValue(DATA_RESPONSE.MODIFICATION_USER));
		}
	}
	
	public static class DataResponseDetailFiller implements Function<Record, DataResponseDetail> {
		
		@Override
		public DataResponseDetail apply(Record r) {
			DataResponseDetail dataResponseDetail = new DataResponseDetail();
			dataResponseDetail.setCreationDate(r.getValue(DATA_RESPONSE_DETAIL.CREATION_DATE));
			dataResponseDetail.setCreationUser(r.getValue(DATA_RESPONSE_DETAIL.CREATION_USER));
			dataResponseDetail.setModificationDate(r.getValue(DATA_RESPONSE_DETAIL.MODIFICATION_DATE));
			dataResponseDetail.setModificationUser(r.getValue(DATA_RESPONSE_DETAIL.MODIFICATION_USER));
			return dataResponseDetail.setDomain(r.getValue(DATA_RESPONSE_DETAIL.DOMAIN))
					.setId(r.getValue(DATA_RESPONSE_DETAIL.ID))
					.setDataResponse(r.getValue(DATA_RESPONSE_DETAIL.DATA_RESPONSE))
					.setDataVariable(r.getValue(DATA_RESPONSE_DETAIL.DATA_VARIABLE))
					.setDataValue(r.getValue(DATA_RESPONSE_DETAIL.DATA_VALUE));
		}
	}
	
	public static class ContractFiller implements Function<Record, Contract> {
		
		@Override
		public Contract apply(Record r) {
			return new Contract()
					.setId(r.getValue(CONTRACT.ID))
					.setDomain(r.getValue(CONTRACT.DOMAIN))
					.setPerson(r.getValue(CONTRACT.PERSON))
					.setWorkplace(r.getValue(CONTRACT.WORKPLACE))
					.setStartDate(r.getValue(CONTRACT.START_DATE))
					.setEndDate(r.getValue(CONTRACT.END_DATE))
					.setCalendar(r.getValue(CONTRACT.CALENDAR))
					.setDescription(r.getValue(CONTRACT.DESCRIPTION))
				//TODO	.setSepeStatus(ContractStatus.values()[r.getValue(CONTRACT.SEPE_STATUS)])
					.setRegistration(r.getValue(CONTRACT.REGISTRATION))
					.setSeniorityDate(r.getValue(CONTRACT.SENIORITY_DATE))
					.setEnterpriseActivity(r.getValue(CONTRACT.ENTERPRISE_ACTIVITY))
					.setEnterpriseCCCRegime(CCCType.getSsRegimeType(r.getValue(ENTERPRISE_CCC.TYPE)))
					.setAgreementLevel(r.getValue(CONTRACT.AGREEMENT_LEVEL))
					
					.setEnterpriseCCC(r.getValue(ENTERPRISE_CCC.CCC))
					.setPersonDocument(r.getValue(REGISTRY.DOCUMENT))
					.setPersonSsNumber(r.getValue(PERSON.SOCIAL_SECURITY_NUM))

				//TODO	.setModel(ContractModel.values()[r.getValue(CONTRACT.MODEL)])
					.setCategoryDescription(r.getValue(CONTRACT.CATEGORY_DESCRIPTION));
				//TODO	.setSsStatus(ContractStatus.values()[r.getValue(CONTRACT.SS_STATUS)]);
		}
	}
	
	public static class ContractExtendedDataFiller implements Function<Record, ContractExtendedData> {
		@Override
		public ContractExtendedData apply(Record r) {
			return new ContractExtendedData()
					.setGrossSalaryLastMonth(r.getValue(ContractDAO.SALARY_CGC_BASE))
					.setTotalMarksLastMonth(r.getValue(ContractDAO.MARK_TOTAL_TIME))
					.setId(r.getValue(CONTRACT.ID))
					.setDomain(r.getValue(CONTRACT.DOMAIN))
					.setPerson(r.getValue(CONTRACT.PERSON))
					.setWorkplace(r.getValue(CONTRACT.WORKPLACE))
					.setWorkplaceName(r.getValue(WORKPLACE.DESCRIPTION))
					.setStartDate(r.getValue(CONTRACT.START_DATE))
					.setEndDate(r.getValue(CONTRACT.END_DATE))
					.setContractType(r.getValue(ContractDAO.CONTRACT_TYPE))
					.setPersonName(r.getValue(ContractDAO.PERSON_FULL_NAME));
		}
	}
	
	public static class ContractDataFiller implements Function<Record, ContractData> {
		
		@Override
		public ContractData apply(Record r) {
			return new ContractData()
					.setId(r.getValue(CONTRACT_DATA.ID))
					.setDomain(r.getValue(CONTRACT_DATA.DOMAIN))
					.setName(r.getValue(CONTRACT_DATA.NAME))
					.setContract(r.getValue(CONTRACT_DATA.CONTRACT))
					.setExpression(r.getValue(CONTRACT_DATA.EXPRESSION))
					.setStartDate(r.getValue(CONTRACT_DATA.START_DATE))
					.setEndDate(r.getValue(CONTRACT_DATA.END_DATE));
		}
	}
	
	public static class IrpfDataFiller implements Function<Record, IrpfData> {
		
		@Override
		public IrpfData apply(Record r) {
			return new IrpfData()
					.setId(r.getValue(IRPF_DATA.ID))
					.setDomain(r.getValue(IRPF_DATA.DOMAIN))
					.setDisability(r.getValue(IRPF_DATA.DISABILITY_LEVEL));
			// TODO AÑADIR LOS PARÁMETROS QUE FALTAN.
		}
	}

	public static class AgreementLevelCategoryFiller implements Function<Record, AgreementLevelCategory> {
		
		@Override
		public AgreementLevelCategory apply(Record r) {
			return new AgreementLevelCategory()
					.setId(r.getValue(AGREEMENT_LEVEL_CATEGORY.ID))
					.setDomain(r.getValue(AGREEMENT_LEVEL_CATEGORY.DOMAIN))
					.setAgreementLevel(r.getValue(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL))
					.setDescription(r.getValue(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION));
		}
	}
	
	public static class InventoryDetailFiller implements Function<Record, InventoryDetail> {
		
		@Override
		public InventoryDetail apply(Record r) {
			return new InventoryDetail()
					.setId(r.getValue(INVENTORY_DETAIL.ID))
					.setInventory(new Inventory().setId(r.getValue(INVENTORY_DETAIL.INVENTORY)).setDomain(r.getValue(INVENTORY_DETAIL.DOMAIN)))
					.setCost(r.getValue(INVENTORY_DETAIL.COST))
					.setActualQuantity(r.getValue(INVENTORY_DETAIL.ACTUAL_QUANTITY))
					.setCreationDate(r.getValue(INVENTORY_DETAIL.CREATION_DATE))
					.setCreationUser(r.getValue(INVENTORY_DETAIL.CREATION_USER))
					.setDomain(r.getValue(INVENTORY_DETAIL.DOMAIN))
					.setItem(
						new OldItem().setId(r.getValue(ITEM.ID))
							.setBarcode(r.getValue(ITEM.BARCODE))
							.setCreationDate(r.getValue(ITEM.CREATION_DATE))
							.setCreationUser(r.getValue(ITEM.CREATION_USER))
							.setDescription(r.getValue(ITEM.DESCRIPTION))
							.setDetail(r.getValue(ITEM.DETAIL))
							.setDetail2(r.getValue(ITEM.DETAIL2))
							.setDetail3(r.getValue(ITEM.DETAIL3))
							.setDomain(r.getValue(ITEM.DOMAIN))
							.setExpensesFixed(r.getValue(ITEM.EXPENSES_FIXED))
							.setExpensesPercent(r.getValue(ITEM.EXPENSES_PERCENT))
							.setInternet(r.getValue(ITEM.INTERNET) == 1)
							.setModificationDate(r.getValue(ITEM.MODIFICATION_DATE))
							.setModificationUser(r.getValue(ITEM.MODIFICATION_USER))
							.setPackMeasurement(r.getValue(ITEM.PACK_MEASUREMENT))
							.setPackUnits(r.getValue(ITEM.PACK_UNITS).doubleValue())
							.setPrice(r.getValue(ITEM.PRICE))
							.setProduct( 
									new OldProduct().setId(r.getValue(PRODUCT.ID))
									.setName(r.getValue(PRODUCT.NAME))
									.setDomain(r.getValue(PRODUCT.DOMAIN))
									.setCode(r.getValue(PRODUCT.CODE))
									.setComposition(r.getValue(PRODUCT.COMPOSITION) == 1)
									.setComposition(r.getValue(PRODUCT.COMPOSITION))
									.setCompositionPrice(r.getValue(PRODUCT.COMPOSITION_PRICE) == 1)
									.setCompositionPrice(r.getValue(PRODUCT.COMPOSITION_PRICE))
									.setInventoriable(r.getValue(PRODUCT.INVENTORIABLE) == 1)
									.setInventoriable(r.getValue(PRODUCT.INVENTORIABLE) )
									.setKind(r.getValue(PRODUCT.KIND) )
									.setLotable(r.getValue(PRODUCT.LOTABLE)  == 1)
									.setLotable(r.getValue(PRODUCT.LOTABLE) )
									.setManufactured(r.getValue(PRODUCT.MANUFACTURED) )
									.setPackaged(r.getValue(PRODUCT.PACKAGED)  == 1)
									.setPurchaseAccount(r.getValue(PRODUCT.PURCHASE_ACCOUNT) )
									.setRetention(r.getValue(PRODUCT.RETENTION) )
									.setSalesAccount(r.getValue(PRODUCT.SALES_ACCOUNT) )
									.setSerializable(r.getValue(PRODUCT.SERIALIZABLE) == 1)
									.setSerializable(r.getValue(PRODUCT.SERIALIZABLE))
									.setStatus(r.getValue(PRODUCT.STATUS))
									.setType(r.getValue(PRODUCT.TYPE))
									.setVat(r.getValue(PRODUCT.VAT))
							)
							.setProductId(r.getValue(ITEM.PRODUCT))
							.setProfitPercent(r.getValue(ITEM.PROFIT_PERCENT))
							.setPurchasePrice(r.getValue(ITEM.PURCHASE_PRICE))
							.setSerialNumber(r.getValue(ITEM.SERIAL_NUMBER))
							.setSerialDate(r.getValue(ITEM.SERIAL_DATE))
							.setStatus(r.getValue(ITEM.STATUS))
					)
					.setModificationDate(r.getValue(INVENTORY_DETAIL.MODIFICATION_DATE))
					.setModificationUser(r.getValue(INVENTORY_DETAIL.MODIFICATION_USER))
					.setRealQuantity(r.getValue(INVENTORY_DETAIL.REAL_QUANTITY));
		}

	}

	// ---------- COMMISSION

	public static class OfferDetailCommissionFiller extends Filler implements Function<Record, OfferDetailCommission> {
		
		@Override
		public OfferDetailCommission apply(Record r) {
			return new OfferDetailCommission()
					.setId(r.getValue(OFFER_DETAIL_COMMISSION.ID))
					.setDomain(r.getValue(OFFER_DETAIL_COMMISSION.DOMAIN))
					.setOfferDetail(checkField(r, OFFER_DETAIL.ID)
							? OfferDetailFiller.build(r)
							: new OfferDetail().setId(r.getValue(OFFER_DETAIL_COMMISSION.OFFER_DETAIL)))
					.setStatus(OfferDetailCommissionStatus.safeValueOf(r.getValue(OFFER_DETAIL_COMMISSION.STATUS)))
					.setPayDate(r.getValue(OFFER_DETAIL_COMMISSION.PAY_DATE))
					.setCommission(r.getValue(OFFER_DETAIL_COMMISSION.COMMISSION))
					.setAmount(r.getValue(OFFER_DETAIL_COMMISSION.AMOUNT));
			
		}
	}
	
	public static class InvoiceDetailCommissionFiller implements Function<Record, InvoiceDetailCommission> {
		
		@Override
		public InvoiceDetailCommission apply(Record r) {
			Invoice i = new Invoice()
				.setId(r.getValue(INVOICE.ID))
				.setDomain(r.getValue(INVOICE.DOMAIN))
				.setType(AonEnumUtils.enumValue(InvoiceType.class,r.getValue(INVOICE.TYPE)))
				.setSeries(r.getValue(INVOICE.SERIES))
				.setNumber(r.getValue(INVOICE.NUMBER))
				.setReferenceCode(r.getValue(INVOICE.REFERENCE_CODE))
				.setIssueDate(r.getValue(INVOICE.ISSUE_DATE))
				.setTaxDate(r.getValue(INVOICE.TAX_DATE))
				.setSecurityLevel(AonEnumUtils.enumValue(SecurityLevel.class,r.getValue(INVOICE.SECURITY_LEVEL)))
				.setRegistry(r.getValue(INVOICE.REGISTRY))
				.setRegistryDocument(r.getValue(INVOICE.RDOCUMENT))
				.setRegistryDocumentType(AonEnumUtils.enumValue(DocumentType.class,r.getValue(INVOICE.RDOCUMENT_TYPE)))
				.setRegistryDocumentCountry(Country.safeValueOf(r.getValue(INVOICE.RDOCUMENT_COUNTRY)))
				.setRegistryName(r.getValue(INVOICE.RNAME))
				.setScope(new Scope().setId(r.getValue(INVOICE.SCOPE)))
				.setActivity(new EnterpriseActivity().setId(r.getValue(INVOICE.ACTIVITY)))	
				.setInvestAsset(r.getValue(INVOICE.INVEST_ASSET))
				.setProject(r.getValue(INVOICE.PROJECT))
				.setRectificationType(AonEnumUtils.enumValue(RectificationType.class,r.getValue(INVOICE.RECTIFICATION_TYPE)))	
				.setRectificationInvoice(r.getValue(INVOICE.RECTIFICATION_INVOICE))	
				.setTransaction(AonEnumUtils.enumValue(InvoiceTransactionType.class,r.getValue(INVOICE.TRANSACTION)))
				.setRecorded(r.getValue(INVOICE.STATUS) == 1 )	
				.setSurcharge(r.getValue(INVOICE.SURCHARGE) == 1 )	
				.setWithholding(r.getValue(INVOICE.WITHHOLDING) == 1 )	
				.setWithholdingFarmer(r.getValue(INVOICE.WITHHOLDING_FARMER) == 1 )	
				.setVatAccrualPayment(r.getValue(INVOICE.VAT_ACCRUAL_PAYMENT) == 1 )	
				.setInvestment(r.getValue(INVOICE.INVESTMENT) == 1 )	
				.setService(r.getValue(INVOICE.SERVICE) == 1 )	
				.setAdvance(r.getValue(INVOICE.ADVANCE) == 1 )	
				.setTaxableBase(r.getValue(INVOICE.TAXABLE_BASE))	
				.setVatQuota(r.getValue(INVOICE.VAT_QUOTA))	
				.setRetentionQuota(r.getValue(INVOICE.RETENTION_QUOTA))	
				.setTotal(r.getValue(INVOICE.TOTAL))	
				.setComments(r.getValue(INVOICE.COMMENTS))
				.setSeller(r.getValue(REGISTRY.ID))
				.setSellerName(r.getValue(REGISTRY.NAME));
			
			
			InvoiceDetail id = new InvoiceDetail()
					.setDescription(r.getValue(INVOICE_DETAIL.DESCRIPTION))
					.setDiscountExpression(r.getValue(INVOICE_DETAIL.DISCOUNT_EXPR))
					.setDomain(r.getValue(INVOICE_DETAIL.DOMAIN))
					.setId(r.getValue(INVOICE_DETAIL.ID))
					.setItem(new Item().setId(r.getValue(INVOICE_DETAIL.ITEM)))
					.setInvoice(i)
					.setPrice(r.getValue(INVOICE_DETAIL.PRICE))
					.setQuantity(r.getValue(INVOICE_DETAIL.QUANTITY));
			
			return new InvoiceDetailCommission()
					.setId(r.getValue(INVOICE_DETAIL_COMMISSION.ID))
					.setDomain(r.getValue(INVOICE_DETAIL_COMMISSION.DOMAIN))
					.setInvoiceDetail(id)
					.setStatus(InvoiceDetailCommissionStatus.safeValueOf(r.getValue(INVOICE_DETAIL_COMMISSION.STATUS)))
					.setPayDate(r.getValue(INVOICE_DETAIL_COMMISSION.PAY_DATE))
					.setCommission(r.getValue(INVOICE_DETAIL_COMMISSION.COMMISSION))
					.setAmount(r.getValue(INVOICE_DETAIL_COMMISSION.AMOUNT));
		}
	}
	
	public static class CommissionItemFiller implements Function<Record, CommissionItem> {
		
		@Override
		public CommissionItem apply(Record r) {
			return new CommissionItem()
					.setId(r.getValue(COMMISSION_ITEM.ID))
					.setDomain(r.getValue(COMMISSION_ITEM.DOMAIN))
					.setCommission(r.getValue(COMMISSION_ITEM.COMMISSION))
					.setItem(r.getValue(COMMISSION_ITEM.ITEM))
					.setAmount(r.getValue(COMMISSION_ITEM.AMOUNT))
					.setQuantity(r.getValue(COMMISSION_ITEM.QUANTITY))
					.setRate(r.getValue(COMMISSION_ITEM.RATE));
		}
	}
	
	public static class CommissionCategoryFiller implements Function<Record, CommissionCategory> {
		
		@Override
		public CommissionCategory apply(Record r) {
			return new CommissionCategory()
					.setId(r.getValue(COMMISSION_CATEGORY.ID))
					.setDomain(r.getValue(COMMISSION_CATEGORY.DOMAIN))
					.setCommission(r.getValue(COMMISSION_CATEGORY.COMMISSION))
					.setCategory(r.getValue(COMMISSION_CATEGORY.CATEGORY))
					.setQuantity(r .getValue(COMMISSION_CATEGORY.QUANTITY))
					.setRate(r.getValue(COMMISSION_CATEGORY.RATE));
		}
	}
	
	public static class CommissionTypeCommissionFiller implements Function<Record, CommissionTypeCommission> {
		
		@Override
		public CommissionTypeCommission apply(Record r) {
			return new CommissionTypeCommission()
					.setId(r.getValue(COMMISSION_TYPE_COMMISSION.ID))
					.setDomain(r.getValue(COMMISSION_TYPE_COMMISSION.DOMAIN))
					.setCommission(r.getValue(COMMISSION_TYPE_COMMISSION.COMMISSION))
					.setCommissionType(r.getValue(COMMISSION_TYPE_COMMISSION.COMMISSION_TYPE));
		}
	}

	public static class CommissionFiller implements Function<Record, Commission> {
		
		@Override
		public Commission apply(Record r) {
			return new Commission()
					.setId(r.getValue(COMMISSION.ID))
					.setDomain(r.getValue(COMMISSION.DOMAIN))
					.setName(r.getValue(COMMISSION.NAME))
					.setStartDate(r.getValue(COMMISSION.START_DATE))
					.setEndDate(r.getValue(COMMISSION.END_DATE));
		}
	}
	
	public static class CommissionTypeFiller implements Function<Record, CommissionType> {
		
		@Override
		public CommissionType apply(Record r) {
			return new CommissionType()
					.setId(r.getValue(COMMISSION_TYPE.ID))
					.setDomain(r.getValue(COMMISSION_TYPE.DOMAIN))
					.setName(r.getValue(COMMISSION_TYPE.NAME))
					.setRate(r.getValue(COMMISSION_TYPE.RATE));
		}
	}
	
	public static class MailTemplateFiller implements Function<Record, MailTemplate> {
		
		@Override
		public MailTemplate apply(Record r) {
			return new MailTemplate()
					.setId(r.getValue(MK_TEMPLATE.ID))
					.setDomain(r.getValue(MK_TEMPLATE.DOMAIN))
					.setName(r.getValue(MK_TEMPLATE.NAME))
					.setActive(r.getValue(MK_TEMPLATE.ACTIVE) == 1)
					.setBackgroundColor(r.getValue(MK_TEMPLATE.BACKGROUND_COLOR))
					.setCreationDate(r.getValue(MK_TEMPLATE.CREATIONDATE))
					.setFooterTemplate(r.getValue(MK_TEMPLATE.FOOTER_TEMPLATE))
					.setHeaderTemplate(r.getValue(MK_TEMPLATE.HEADER_TEMPLATE))
					.setScope(r.getValue(MK_TEMPLATE.SCOPE))
					.setSubject(r.getValue(MK_TEMPLATE.SUBJECT))
					.setTitleColor(r.getValue(MK_TEMPLATE.TITLE_COLOR))
					.setWidth(r.getValue(MK_TEMPLATE.WIDTH));
		}
	}
	
	public static class UdapaQualityFiller implements Function<Record, UdapaQuality> {
		
		@Override
		public UdapaQuality apply(Record r) {
			Supplier supplier = new Supplier();
			supplier.setAlias(r.getValue(REGISTRY.ALIAS));
			supplier.setConfidential(SecurityLevel.CONFIDENTIAL.value().equals(r.getValue(REGISTRY.SECURITY_LEVEL)));
			supplier.setDocument(r.getValue(REGISTRY.DOCUMENT));
			supplier.setDocumentType(DocumentType.safeValueOf(r.getValue(REGISTRY.DOCUMENT_TYPE)));
			supplier.setDomain(new Domain().setId(r.getValue(REGISTRY.DOMAIN)));
			supplier.setId(r.getValue(REGISTRY.ID));
			supplier.setName(r.getValue(REGISTRY.NAME));
			supplier.setSecurityLevel(SecurityLevel.safeValueOf(r.getValue(REGISTRY.SECURITY_LEVEL)));
			supplier.setLegalPerson(AonEnumUtils.getBoolean(r.getValue(REGISTRY.TYPE)));
			return new UdapaQuality()
					.setDataResponse(new DataResponse()
							.setDomain(r.getValue(DATA_RESPONSE.DOMAIN))
							.setId(r.getValue(DATA_RESPONSE.ID))
							.setResponseDate(r.getValue(DATA_RESPONSE.RESPONSE_DATE))
							.setCode(r.getValue(DATA_RESPONSE.CODE))
							.setSource(DataResponseSource.safeValueOf(r.getValue(DATA_RESPONSE.SOURCE)))
							.setSourceId(r.getValue(DATA_RESPONSE.SOURCE_ID))
							.setCreationDate(r.getValue(DATA_RESPONSE.CREATION_DATE))
							.setCreationUser(r.getValue(DATA_RESPONSE.CREATION_USER))
							.setModificationDate(r.getValue(DATA_RESPONSE.MODIFICATION_DATE))
							.setModificationUser(r.getValue(DATA_RESPONSE.MODIFICATION_USER)))
					.setSupplier(supplier)
					.setProduct(r.getValue(INCOME_DETAIL.DESCRIPTION));
			
		}
	}

	public static class PaturpatQualityFiller implements Function<Record, PaturpatQuality> {
		
		@Override
		public PaturpatQuality apply(Record r) {

			return new PaturpatQuality()
				.setDataResponse(new DataResponse()
							.setDomain(r.getValue(DATA_RESPONSE.DOMAIN))
							.setId(r.getValue(DATA_RESPONSE.ID))
							.setResponseDate(r.getValue(DATA_RESPONSE.RESPONSE_DATE))
							.setCode(r.getValue(DATA_RESPONSE.CODE))
							.setSource(DataResponseSource.safeValueOf(r.getValue(DATA_RESPONSE.SOURCE)))
							.setSourceId(r.getValue(DATA_RESPONSE.SOURCE_ID))
							.setCreationDate(r.getValue(DATA_RESPONSE.CREATION_DATE))
							.setCreationUser(r.getValue(DATA_RESPONSE.CREATION_USER))
							.setModificationDate(r.getValue(DATA_RESPONSE.MODIFICATION_DATE))
							.setModificationUser(r.getValue(DATA_RESPONSE.MODIFICATION_USER)))
				.setProduct(r.getValue(PRODUCT.NAME) + " #" + r.getValue(ITEM.SERIAL_NUMBER));
			
		}
	}
	
	public static class ItemAddInfoFiller implements Function<Record, ItemAddInfo> {
		
		@Override
		public ItemAddInfo apply(Record r) {

			return new ItemAddInfo()
				.setDomain(r.getValue(ITEM_ADDINFO.DOMAIN))
				.setId(r.getValue(ITEM_ADDINFO.ID))
				.setProduct(r.getValue(ITEM_ADDINFO.PRODUCT))
				.setItem(r.getValue(ITEM_ADDINFO.ITEM))
				.setAttribute(r.getValue(ITEM_ADDINFO.ATTRIBUTE))
				.setValue(r.getValue(ITEM_ADDINFO.VALUE))
				.setDate(r.getValue(ITEM_ADDINFO.VALUE_DATE));
		}
	}

	
}
