package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.AgreementLevelCategory.AGREEMENT_LEVEL_CATEGORY;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Carrier.CARRIER;
import static com.esferalia.aon.jooq.tables.CarrierPacking.CARRIER_PACKING;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Commission.COMMISSION;
import static com.esferalia.aon.jooq.tables.CommissionCategory.COMMISSION_CATEGORY;
import static com.esferalia.aon.jooq.tables.CommissionItem.COMMISSION_ITEM;
import static com.esferalia.aon.jooq.tables.CommissionType.COMMISSION_TYPE;
import static com.esferalia.aon.jooq.tables.CommissionTypeCommission.COMMISSION_TYPE_COMMISSION;
import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;
import static com.esferalia.aon.jooq.tables.ContractData.CONTRACT_DATA;
import static com.esferalia.aon.jooq.tables.Creditor.CREDITOR;
import static com.esferalia.aon.jooq.tables.Customer.CUSTOMER;
import static com.esferalia.aon.jooq.tables.DataResponse.DATA_RESPONSE;
import static com.esferalia.aon.jooq.tables.DataResponseDetail.DATA_RESPONSE_DETAIL;
import static com.esferalia.aon.jooq.tables.Delivery.DELIVERY;
import static com.esferalia.aon.jooq.tables.DeliveryDetail.DELIVERY_DETAIL;
import static com.esferalia.aon.jooq.tables.Income.INCOME;
import static com.esferalia.aon.jooq.tables.IncomeDetail.INCOME_DETAIL;
import static com.esferalia.aon.jooq.tables.Inventory.INVENTORY;
import static com.esferalia.aon.jooq.tables.InventoryDetail.INVENTORY_DETAIL;
import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceDetailCommission.INVOICE_DETAIL_COMMISSION;
import static com.esferalia.aon.jooq.tables.IrpfData.IRPF_DATA;
import static com.esferalia.aon.jooq.tables.Item.ITEM;
import static com.esferalia.aon.jooq.tables.ItemAddinfo.ITEM_ADDINFO;
import static com.esferalia.aon.jooq.tables.MkTemplate.MK_TEMPLATE;
import static com.esferalia.aon.jooq.tables.Offer.OFFER;
import static com.esferalia.aon.jooq.tables.OfferDetailCommission.OFFER_DETAIL_COMMISSION;
import static com.esferalia.aon.jooq.tables.Pcategory.PCATEGORY;
import static com.esferalia.aon.jooq.tables.Person.PERSON;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Purchase.PURCHASE;
import static com.esferalia.aon.jooq.tables.PurchaseDetail.PURCHASE_DETAIL;
import static com.esferalia.aon.jooq.tables.Rbank.RBANK;
import static com.esferalia.aon.jooq.tables.RecordData.RECORD_DATA;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Ritem.RITEM;
import static com.esferalia.aon.jooq.tables.Rnote.RNOTE;
import static com.esferalia.aon.jooq.tables.Rpaymethod.RPAYMETHOD;
import static com.esferalia.aon.jooq.tables.Rseller.RSELLER;
import static com.esferalia.aon.jooq.tables.RdirStaff.RDIR_STAFF;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.Seller.SELLER;
import static com.esferalia.aon.jooq.tables.Supplier.SUPPLIER;
import static com.esferalia.aon.jooq.tables.Target.TARGET;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Date;
import java.sql.Timestamp;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.jooq.tables.Raddinfo;
import com.esferalia.aon.occam.api.model.Filter.AgreementLevelCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.ApplicationParameterFilter;
import com.esferalia.aon.occam.api.model.Filter.CarrierFilter;
import com.esferalia.aon.occam.api.model.Filter.CarrierPackingFilter;
import com.esferalia.aon.occam.api.model.Filter.CategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionCategoryFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionItemFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionTypeCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.CommissionTypeFilter;
import com.esferalia.aon.occam.api.model.Filter.CompanyFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ContractFilter;
import com.esferalia.aon.occam.api.model.Filter.CustomerFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DataResponseFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.DeliveryFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.IncomeFilter;
import com.esferalia.aon.occam.api.model.Filter.InventoryDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.InventoryFilter;
import com.esferalia.aon.occam.api.model.Filter.InvoiceDetailCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.IrpfDataFilter;
import com.esferalia.aon.occam.api.model.Filter.ItemAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.MailTemplateFilter;
import com.esferalia.aon.occam.api.model.Filter.OfferDetailCommissionFilter;
import com.esferalia.aon.occam.api.model.Filter.PersonFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.PurchaseDetailFilter;
import com.esferalia.aon.occam.api.model.Filter.PurchaseFilter;
import com.esferalia.aon.occam.api.model.Filter.RDirStaffFilter;
import com.esferalia.aon.occam.api.model.Filter.RecordDataFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryAddInfoFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryBankFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryItemFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryNoteFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistryPayMethodFilter;
import com.esferalia.aon.occam.api.model.Filter.RegistrySellerFilter;
import com.esferalia.aon.occam.api.model.Filter.ScopeFilter;
import com.esferalia.aon.occam.api.model.Filter.SellerFilter;
import com.esferalia.aon.occam.api.model.Filter.SupplierFilter;
import com.esferalia.aon.occam.api.model.Filter.TargetFilter;
import com.esferalia.aon.occam.api.model.Filter.UserFilter;
import com.esferalia.aon.occam.api.model.Properties.AgreementLevelCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.ApplicationParameterProperties;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.Properties.CarrierProperties;
import com.esferalia.aon.occam.api.model.Properties.CategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionItemProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionTypeCommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionTypeProperties;
import com.esferalia.aon.occam.api.model.Properties.CompanyProperties;
import com.esferalia.aon.occam.api.model.Properties.ContractDataProperties;
import com.esferalia.aon.occam.api.model.Properties.ContractProperties;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.DataResponseDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.DataResponseProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.IncomeDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.IncomeProperties;
import com.esferalia.aon.occam.api.model.Properties.InventoryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.InventoryProperties;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDetailCommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.IrpfDataProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemAddInfoProperties;
import com.esferalia.aon.occam.api.model.Properties.MailTemplateProperties;
import com.esferalia.aon.occam.api.model.Properties.OfferDetailCommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.PersonProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseProperties;
import com.esferalia.aon.occam.api.model.Properties.RDirStaffProperties;
import com.esferalia.aon.occam.api.model.Properties.RecordDataProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddInfoProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryBankProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryItemProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryNoteProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryPayMethodProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistrySellerProperties;
import com.esferalia.aon.occam.api.model.Properties.ScopeProperties;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.Properties.SupplierProperties;
import com.esferalia.aon.occam.api.model.Properties.TargetProperties;
import com.esferalia.aon.occam.api.model.Properties.UserProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceFilter;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.registry.CreditorFilter;
import com.esferalia.aon.occam.api.model.registry.CreditorProperties;

public class PropertiesDAO {
	
	private PropertiesDAO() {
		throw new IllegalAccessError("DAO class");
	}
	
	public static final InvoicePropertiesDAO INVOICE_PROPERTIES = new InvoicePropertiesDAO();
	public static class InvoicePropertiesDAO implements InvoiceProperties {
		
		protected Select<Record> build(SelectJoinStep<Record> select, InvoiceFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(InvoiceFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null)
				return new Condition[0];

			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.ID);}
		@Override public Property<Integer> getDomainProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty(){return new FilterDAO.PropertyDAO<>(INVOICE.REGISTRY);}
		@Override public Property<java.util.Date> getStartIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<java.util.Date> getEndIssueDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.ISSUE_DATE);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TYPE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SCOPE);}
		@Override public Property<Byte> getConfidentialProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SECURITY_LEVEL);}
		@Override public Property<Byte> getRectificationTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_TYPE);}
		@Override public Property<Integer> getRectificationInvoiceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.RECTIFICATION_INVOICE);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.WORKPLACE);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.SELLER);}
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<>(ITEM.PRODUCT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL.ITEM);}
		@Override public Property<Integer> getProductCategoryProperty() {return new FilterDAO.PropertyDAO<>(PCATEGORY.ID);}
		@Override public Property<Integer> getProductBrandProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.BRAND);}
		@Override public Property<String> getProductCodeProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.CODE);}	
		@Override public Property<Byte> getProductTypeProperty() {return new FilterDAO.PropertyDAO<>(PRODUCT.TYPE);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TRANSACTION);}
		@Override public Property<Byte> getInvestmentProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.INVESTMENT);}
		@Override public Property<java.util.Date> getTaxDateProperty() {return new FilterDAO.DatePropertyDAO(INVOICE.TAX_DATE);}
		@Override public Property<Integer> getPosShiftroperty() {return new FilterDAO.PropertyDAO<>(INVOICE.POS_SHIFT);}
		@Override public Property<Byte> getVatAccrualPayment() {return new FilterDAO.PropertyDAO<>(INVOICE.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SERIES);}
 		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.NUMBER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.MODIFICATION_USER);}
	}
	
	public static class ApplicationParameterPropertiesDAO implements ApplicationParameterProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, ApplicationParameterFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ApplicationParameterFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(APP_PARAM.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(APP_PARAM.ID);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(APP_PARAM.NAME);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<>(APP_PARAM.VALUE);}
	}
	
	public static class CustomerPropertiesDAO extends RegistryPropertiesDAO implements CustomerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CustomerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CustomerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DOMAIN);}
		@Override public Property<Integer> getTariffProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.TARIFF);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.SURCHARGE);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.WITHHOLDING);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.TRANSACTION);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.SCOPE);}
		@Override public Property<Byte> getEInvoiceProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.E_INVOICE);}
		@Override public Property<Integer> getInvoicingGroupProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.INVOICING_GROUP);}
		@Override public Property<Byte> getProjectGroupedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.PROJECT_GROUPED);}
		@Override public Property<Byte> getDeliveryGroupedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DELIVERY_GROUPED);}
		@Override public Property<Byte> getDeliveryValuatedProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.DELIVERY_VALUATED);}
		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.ACCOUNT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(CUSTOMER.MODIFICATION_DATE);}	
	}
	
	public static class SellerPropertiesDAO implements SellerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(SellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(SELLER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SELLER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(SELLER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.SCOPE);}
		@Override public Property<Integer> getCommissionTypeProperty() {return new FilterDAO.PropertyDAO<>(SELLER.COMMISSION_TYPE);}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ID);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
	}
	
	public static class RegistrySellerPropertiesDAO implements RegistrySellerProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RegistrySellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistrySellerFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.STATUS);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.ID);}
 		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.SELLER);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(RSELLER.END_DATE);}
	}
	
	
	public static class CreditorPropertiesDAO implements CreditorProperties {
		
		protected Select<Record> build(SelectJoinStep<Record> select, CreditorFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CreditorFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(CREDITOR.REGISTRY);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CREDITOR.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.DOCUMENT);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<String>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<Byte>(CREDITOR.STATUS);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(REGISTRY.SECURITY_LEVEL);}
	}
	
	public static class SupplierPropertiesDAO implements SupplierProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, SupplierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(SupplierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.SCOPE);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.REGISTRY);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.MODIFICATION_DATE);}
		@Override public Property<Integer> getTariffProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.TARIFF);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.WITHHOLDING);}
		@Override public Property<Byte> getWithholdingFarmerProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.WITHHOLDING_FARMER);}
		@Override public Property<Byte> getVatAccrualPaymentProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.TRANSACTION);}
		@Override public Property<Byte> getPurchaseValuatedProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.PURCHASE_VALUATED);}
		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<>(SUPPLIER.ACCOUNT);}
	}
	
	public static class TargetPropertiesDAO implements TargetProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, TargetFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(TargetFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(TARGET.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(TARGET.STATUS);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(TARGET.SCOPE);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(TARGET.REGISTRY);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(TARGET.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(TARGET.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(TARGET.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(TARGET.MODIFICATION_DATE);}
		@Override public Property<Integer> getTariffProperty() {return new FilterDAO.PropertyDAO<>(TARGET.TARIFF);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(TARGET.WITHHOLDING);}
		@Override public Property<Byte> getTransactionProperty() {return new FilterDAO.PropertyDAO<>(TARGET.TRANSACTION);}
		@Override public Property<Byte> getAdvertisingProperty() {return new FilterDAO.PropertyDAO<>(TARGET.ADVERTISING);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(TARGET.SURCHARGE);}
	}
	
	public static class PersonPropertiesDAO implements PersonProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, PersonFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(PersonFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PERSON.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PERSON.REGISTRY);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
		@Override public Property<Date> getBirthDateProperty() {return new FilterDAO.PropertyDAO<>(PERSON.BIRTH_DATE);}
		@Override public Property<Byte> getGenderProperty() {return new FilterDAO.PropertyDAO<>(PERSON.GENDER);}
		@Override public Property<Byte> getMaritalStatusProperty() {return new FilterDAO.PropertyDAO<>(PERSON.MARITAL_STATUS);}
		@Override public Property<String> getSocialSecurityNumProperty() {return new FilterDAO.PropertyDAO<>(PERSON.SOCIAL_SECURITY_NUM);}
		@Override public Property<String> getFirstNameProperty() {return new FilterDAO.PropertyDAO<>(PERSON.NAME);}
		@Override public Property<String> getFirstSurnameProperty() {return new FilterDAO.PropertyDAO<>(PERSON.FIRST_SURNAME);}
		@Override public Property<String> getSecondSurnameProperty() {return new FilterDAO.PropertyDAO<>(PERSON.SECOND_SURNAME);}
	}
	
	public static class CategoryPropertiesDAO implements CategoryProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] {filterDAO.getCondition()};
		}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.ID);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.NAME);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.TYPE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.SCOPE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.DESCRIPTION);}
		@Override public Property<String> getUrlProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.URL);}
		@Override public Property<Integer> getRattachProperty() {return new FilterDAO.PropertyDAO<>(CATEGORY.RATTACH);}
	}
	
	public static class CarrierPackingPropertiesDAO implements CarrierPackingProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CarrierPackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CarrierPackingFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DOMAIN);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.STATUS);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NUMBER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.TYPE);}
		@Override public Property<Timestamp> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.ISSUE_DATE);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CARRIER);}
		@Override public Property<Timestamp> getDeliveryDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DELIVERY_DATE);}
		@Override public Property<String> getCarrierReferenceProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CARRIER_REFERENCE);}
		@Override public Property<String> getNumberPlateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NUMBER_PLATE);}
		@Override public Property<String> getDriverNameProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DRIVER_NAME);}
		@Override public Property<String> getDriverDocumentProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.DRIVER_DOCUMENT);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.MODIFICATION_DATE);}
		@Override public Property<Double> getGrossWeightProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.GROSS);}
		@Override public Property<Double> getTareProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.TARE);}
		@Override public Property<Double> getNetProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.NET);}
		@Override public Property<Timestamp> getReceptionStartDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.RECEPTION_START_DATE);}
		@Override public Property<Timestamp> getReceptionEndDateProperty() {return new FilterDAO.PropertyDAO<>(CARRIER_PACKING.RECEPTION_END_DATE);}
	}
	
	public static class RegistryPropertiesDAO implements RegistryProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOMAIN);}
		@Override public Property<String> getDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_TYPE);}
		@Override public Property<String> getDocumentCountryProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT_COUNTRY);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.ALIAS);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.TYPE);}
		@Override public Property<String> getNationalityProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NATIONALITY);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.SECURITY_LEVEL);}
	}
	
	public static class CarrierPropertiesDAO extends RegistryPropertiesDAO implements CarrierProperties  {
		protected Select<Record> build(SelectJoinStep<Record> select, CarrierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CarrierFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CARRIER.SCOPE);}
	}
	
	public static class RNotePropertiesDAO implements RegistryNoteProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RegistryNoteFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryNoteFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RNOTE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RNOTE.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<Integer>(RNOTE.REGISTRY);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(RNOTE.DESCRIPTION);}
		@Override public Property<Date> getNoteDateProperty() {return new FilterDAO.PropertyDAO<Date>(RNOTE.NOTE_DATE);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<String>(RNOTE.COMMENTS);}
		@Override public Property<Byte> getNoteTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(RNOTE.NOTE_TYPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(RNOTE.SECURITY_LEVEL);}
	}
	
	public static class RItemPropertiesDAO implements RegistryItemProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, RegistryItemFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryItemFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RITEM.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RITEM.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RITEM.REGISTRY);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(RITEM.ITEM);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(RITEM.TYPE);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(RITEM.CODE);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(RITEM.PRICE);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(RITEM.DISCOUNT_EXPR);}
		@Override public Property<Byte> getPriorityProperty() {return new FilterDAO.PropertyDAO<>(RITEM.PRIORITY);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(RITEM.WORKPLACE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(RITEM.STATUS);}

	}
	
	protected static class PurchaseDetailPropertiesDAO implements PurchaseDetailProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, PurchaseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(PurchaseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DOMAIN);}
		@Override public Property<Integer> getPurchaseProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PURCHASE);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PROJECT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.ITEM);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.LINE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DESCRIPTION);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Double> getTaxesProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.TAXES);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.STATUS);}
		@Override public Property<Integer> getProposalDetailProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.PROPOSAL_DETAIL);}
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.SOURCE_ID);}
		@Override public Property<Double> getDeliveredProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.DELIVERED);}

		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.CARRIER_PACKING);}

		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SUPPLIER);}

		
		// TODO
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getConfidentialProperty() {return null;}


	}
	
	protected static class PurchasePropertiesDAO implements PurchaseProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,PurchaseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(PurchaseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PROJECT);}
		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SUPPLIER);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.NUMBER);}
		@Override public Property<String> getPurchaseReferenceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PURCHASE_REFERENCE);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.ADDRESS);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DISCOUNT_EXPR);}
		@Override public Property<Date> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.ISSUE_DATE);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PAY_METHOD);}
		@Override public Property<Byte> getDocumentTypeProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DOCUMENT_TYPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.WORKPLACE);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.WAREHOUSE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntsProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.BIC);}
		@Override public Property<Byte> getEmailCommunicationProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.EMAIL_COMMUNICATION);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CARRIER);}
		@Override public Property<String> getShippingAlternativeAddressProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS);}
		@Override public Property<String> getShippingAlternativeAddress2Property() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_ADDRESS2);}
		@Override public Property<String> getShippingAlternativeZipProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_ZIP);}
		@Override public Property<String> getShippingAlternativeCityProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_CITY);}
		@Override public Property<String> getShippingAlternativePhoneProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_PHONE);}
		@Override public Property<String> getShippingAlternativeRecipientProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_ALTERNATIVE_RECIPIENT);}
		@Override public Property<String> getShippingContactProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_CONTACT);}
		@Override public Property<Byte> getShippingPeriodProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.SHIPPING_PERIOD);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_DATE);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE_DETAIL.CARRIER_PACKING);}
	
		// REGISTRY
		@Override public Property<String> getRegistryNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getRegistryDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}

		@Override public Property<Date> getStartIssueDateProperty() {return null;}
		@Override public Property<Date> getEndIssueDateProperty() {return null;}
		@Override public Property<Byte> getConfidentialProperty() {return null;}
	}
	
	protected static class DeliveryPropertiesDAO implements DeliveryProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,DeliveryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DeliveryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PROJECT);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.NUMBER);}
		@Override public Property<Integer> getCustomerProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CUSTOMER);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ADDRESS);}
		@Override public Property<Timestamp> getIssueTimeProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.ISSUE_TIME);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PAY_METHOD);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.WORKPLACE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.BIC);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.MODIFICATION_USER);}
		@Override public Property<Integer> getCarrierProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CARRIER);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY.CARRIER_PACKING);}
		
		@Override public Property<String> getRegistryNameProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.NAME);}
		@Override public Property<String> getRegistryDocumentProperty() {return new FilterDAO.PropertyDAO<>(REGISTRY.DOCUMENT);}

		@Override
		public Property<Byte> getConfidentialProperty() {return null;}
	}
	
	protected static class DeliveryDetailPropertiesDAO implements DeliveryDetailProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,DeliveryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DeliveryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DOMAIN);}
		@Override public Property<Integer> getDelivery() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DELIVERY);}
		@Override public Property<Short> getLine() {return new FilterDAO.PropertyDAO<Short>(DELIVERY_DETAIL.LINE);}
		@Override public Property<Integer> getItem() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.ITEM);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DESCRIPTION);}
		@Override public Property<Integer> getWarehouse() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.WAREHOUSE);}
		@Override public Property<Double> getQuantity() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.QUANTITY);}
		@Override public Property<Double> getPrice() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExpressionProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Integer> getSalesDetail() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.SALES_DETAIL);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DELIVERY_DETAIL.MODIFICATION_USER);}
	}
	
	protected static class IncomePropertiesDAO implements IncomeProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, IncomeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(IncomeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INCOME.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INCOME.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(INCOME.PROJECT);}
		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<>(INCOME.SUPPLIER);}
		@Override public Property<Integer> getAddressProperty() {return new FilterDAO.PropertyDAO<>(INCOME.ADDRESS);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(INCOME.PAY_METHOD);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(INCOME.SECURITY_LEVEL);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INCOME.STATUS);}
		@Override public Property<String> getCommentsProperty() {return new FilterDAO.PropertyDAO<>(INCOME.COMMENTS);}
		@Override public Property<String> getRemarksProperty() {return new FilterDAO.PropertyDAO<>(INCOME.REMARKS);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(INCOME.WORKPLACE);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(INCOME.SCOPE);}
		@Override public Property<Short> getNumberOfPymntsProperty() {return new FilterDAO.PropertyDAO<>(INCOME.NUMBER_OF_PYMNTS);}
		@Override public Property<Short> getDaysToFirstPymntProperty() {return new FilterDAO.PropertyDAO<>(INCOME.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Short> getDaysBetweenPymntsProperty() {return new FilterDAO.PropertyDAO<>(INCOME.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(INCOME.PYMNT_DAYS);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(INCOME.BANK_ACCOUNT);}
		@Override public Property<String> getBankAliasProperty() {return new FilterDAO.PropertyDAO<>(INCOME.BANK_ALIAS);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(INCOME.BIC);}
		@Override public Property<Integer> getCarrierPackingProperty() {return new FilterDAO.PropertyDAO<>(INCOME.CARRIER_PACKING);}
		@Override public Property<String> getReferenceCodeProperty() {return new FilterDAO.PropertyDAO<>(INCOME.REFERENCE_CODE);}
		@Override public Property<Date> getIssueTimeProperty() {return new FilterDAO.PropertyDAO<>(INCOME.ISSUE_TIME);}
		
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_DATE);}

		@Override public Property<Byte> getConfidentialProperty() {return null;}
	}
	
	protected static class IncomeDetailPropertiesDAO implements IncomeDetailProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, IncomeDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(IncomeDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) {
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.DOMAIN);}
		@Override public Property<Integer> getProjectProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.PROJECT);}
		@Override public Property<Integer> getIncomeProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.INCOME);}
		@Override public Property<Short> getLineProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.LINE);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.ITEM);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.DESCRIPTION);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.WAREHOUSE);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.QUANTITY);}
		@Override public Property<Double> getPriceProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.PRICE);}
		@Override public Property<String> getDiscountExprProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.DISCOUNT_EXPR);}
		@Override public Property<Integer> getPurchaseDetailProperty() {return new FilterDAO.PropertyDAO<>(INCOME_DETAIL.PURCHASE_DETAIL);}
		
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(PURCHASE.MODIFICATION_DATE);}
	}
	
	protected static class RecordDataPropertiesDAO implements RecordDataProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,RecordDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RecordDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.REGISTRY);}
		@Override public Property<Date> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.CREATION_DATE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.DESCRIPTION);}
		@Override public Property<String> getNotaryProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.NOTARY);}
		@Override public Property<String> getNumberProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.NUMBER);}
		@Override public Property<Date> getRecordDateProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.RECORD_DATE);}
		@Override public Property<String> getVolumeProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.VOLUME);}
		@Override public Property<String> getSectionProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.SECTION);}
 		@Override public Property<String> getPageProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.PAGE);}
		@Override public Property<String> getSheetProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.SHEET);}
		@Override public Property<String> getRegistrationProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.REGISTRATION);}
		@Override public Property<Integer> getAttachProperty() {return new FilterDAO.PropertyDAO<>(RECORD_DATA.ATTACH);}
	}
	
	protected static class RBankPropertiesDAO implements RegistryBankProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,RegistryBankFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryBankFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RBANK.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RBANK.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RBANK.REGISTRY);}
		@Override public Property<String> getBankAccountProperty() {return new FilterDAO.PropertyDAO<>(RBANK.BANK_ACCOUNT);}
		@Override public Property<String> getBicProperty() {return new FilterDAO.PropertyDAO<>(RBANK.BIC);}
		@Override public Property<String> getSufixProperty() {return new FilterDAO.PropertyDAO<>(RBANK.SUFIX);}
		@Override public Property<String> getAliasProperty() {return new FilterDAO.PropertyDAO<>(RBANK.ALIAS);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(RBANK.ACTIVE);}
		@Override public Property<Integer> getAccountProperty() {return new FilterDAO.PropertyDAO<>(RBANK.REGISTRY);}
	}
	
	protected static class RPayMethodPropertiesDAO implements RegistryPayMethodProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,RegistryPayMethodFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryPayMethodFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.REGISTRY);}
		@Override public Property<Integer> getPayMethodProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.PAY_METHOD);}
		@Override public Property<Integer> getRBankProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.RBANK);}
		@Override public Property<Integer> getNumberOfPymntsProperty() {return null;} // new FilterDAO.PropertyDAO<>(RPAYMETHOD.NUMBER_OF_PYMNTS);}
		@Override public Property<Integer> getDaysToFirstPymntProperty() {return null;} // new FilterDAO.PropertyDAO<>(RPAYMETHOD.DAYS_TO_FIRST_PYMNT);}
		@Override public Property<Integer> getDaysBetweenPymntsProperty() {return null;} // new FilterDAO.PropertyDAO<>(RPAYMETHOD.DAYS_BETWEEN_PYMNTS);}
		@Override public Property<String> getPymntDaysProperty() {return new FilterDAO.PropertyDAO<>(RPAYMETHOD.PYMNT_DAYS);}
	}
	
	protected static class RDirStaffPropertiesDAO implements RDirStaffProperties {
		protected Select<Record> build(SelectJoinStep<Record> select,RDirStaffFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RDirStaffFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.ID);} 
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.DOMAIN);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.REGISTRY);}
		@Override public Property<Byte> getShareHolderProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.SHAREHOLDER);}
		@Override public Property<Byte> getRepresentativeProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.REPRESENTATIVE);}
		@Override public Property<Byte> getRepresentativeLaborProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.REPRESENTATIVE_LABOR);}
		@Override public Property<Byte> getDirectorProperty() {return new FilterDAO.PropertyDAO<>(RDIR_STAFF.DIRECTOR);}
	
	}
	
	
	protected static class RegistryAddInfoPropertiesDAO implements RegistryAddInfoProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RegistryAddInfoFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RegistryAddInfoFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(Raddinfo.RADDINFO.DOMAIN);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(Raddinfo.RADDINFO.ID);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(Raddinfo.RADDINFO.REGISTRY);}
		@Override public Property<String> getAttributeProperty() {return new FilterDAO.PropertyDAO<>(Raddinfo.RADDINFO.ATTRIBUTE);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<>(Raddinfo.RADDINFO.VALUE);}
		@Override public Property<Date> getValueDate() {return new FilterDAO.PropertyDAO<>(Raddinfo.RADDINFO.VALUE_DATE);}

	}
	
	
	protected static class CompanyPropertiesDAO extends RegistryPropertiesDAO implements CompanyProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, CompanyFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CompanyFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.DOMAIN);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.ACTIVE);}
		@Override public Property<Byte> getSurchargeProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.SURCHARGE);}
		@Override public Property<Byte> getWithholdingProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.WITHHOLDING);}
		@Override public Property<Byte> getVatAccrualPaymentProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.VAT_ACCRUAL_PAYMENT);}
		@Override public Property<Byte> getEInvoiceProperty() {return new FilterDAO.PropertyDAO<>(COMPANY.E_INVOICE);}
	}
	
	protected static class DataResponsePropertiesDAO implements DataResponseProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, DataResponseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DataResponseFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.MODIFICATION_DATE);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.DOMAIN);}
		@Override public Property<String> getNumberProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.CODE);}
		@Override public Property<Date> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.RESPONSE_DATE);}
		@Override public Property<String> getCodeProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.CODE);}
		
		@Override public Property<Byte> getSourceProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.SOURCE);}
		@Override public Property<Integer> getSourceIdProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE.SOURCE_ID);}

		@Override public Property<String> getDetailVariableProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.DATA_VARIABLE);}
		@Override public Property<String> getDetailValueProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.DATA_VALUE);}
	}
	
	protected static class DataResponseDetailPropertiesDAO implements DataResponseDetailProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, DataResponseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(DataResponseDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.CREATION_USER);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.CREATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.MODIFICATION_USER);}
		@Override public Property<Timestamp> getModificationDateProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.MODIFICATION_DATE);}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.DOMAIN);}
		@Override public Property<Integer> getDataResponseProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.DATA_RESPONSE);}
		@Override public Property<String> getDataVariableProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.DATA_VARIABLE);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<>(DATA_RESPONSE_DETAIL.DATA_VALUE);}
	}
	
	protected static class ContractPropertiesDAO implements ContractProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, ContractFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ContractFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.DOMAIN);}
		@Override public Property<Integer> getPersonProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.PERSON);}
		@Override public Property<Integer> getWorkplaceProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.WORKPLACE);}
		@Override public Property<Integer> getEnterpriseCCCProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.ENTERPRISE_CCC);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.END_DATE);}
		@Override public Property<Integer> getCalendarProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.CALENDAR);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.DESCRIPTION);}
		@Override public Property<Byte> getSepeStatusProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.SEPE_STATUS);}
		@Override public Property<Integer> getRegistrationProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.REGISTRATION);}
		@Override public Property<Date> getSeniorityDateProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.SENIORITY_DATE);}
		@Override public Property<Integer> getEnterpriseActivityProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.ENTERPRISE_ACTIVITY);}
		@Override public Property<Byte> getSSRegimeProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.SS_REGIME);}
		@Override public Property<Integer> getAgreementLevelProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.AGREEMENT_LEVEL);}
		@Override public Property<Byte> getModelProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.MODEL);}
		@Override public Property<String> getCategoryDescriptionProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.CATEGORY_DESCRIPTION);}
		@Override public Property<Byte> getSSStatusProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT.SS_STATUS);}

	}
	
	protected static class ContractDataPropertiesDAO implements ContractDataProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, ContractDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ContractDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.NAME);}
		@Override public Property<Integer> getContractProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.CONTRACT);}
		@Override public Property<String> getExpressionProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.EXPRESSION);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_DATA.END_DATE);}
	}
	
	protected static class IrpfDataPropertiesDAO implements IrpfDataProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, IrpfDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(IrpfDataFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.DOMAIN);}
		@Override public Property<Integer> getContractProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.CONTRACT);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.END_DATE);}
		@Override public Property<Byte> getFamilySituationProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.FAMILY_SITUATION);}
		@Override public Property<String> getSpouseDocumentProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.SPOUSE_DOCUMENT);}
		@Override public Property<Byte> getDisabiltyLevelProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.DISABILITY_LEVEL);}
		@Override public Property<Byte> getDependenceProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.DEPENDENCE);}
		@Override public Property<Date> getMovingDateProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.MOVING_DATE);}
		@Override public Property<Byte> getLabourProlongationProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.LABOUR_PROLONGATION);}
		@Override public Property<Byte> getDescendientCountProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.DESCENDIENT_COUNT);}
		@Override public Property<Byte> getFiscalExclusionProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.FISCAL_EXCLUSION);}
		@Override public Property<Date> getIssueDateProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.ISSUE_DATE);}
		@Override public Property<Double> getAnnualRemunerationProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.ANNUAL_REMUNERATION);}
		@Override public Property<Double> getIrregular182ReductionProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.IRREGULAR_18_2_REDUCTION);}
		@Override public Property<Double> getIrregular183ReductionProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.IRREGULAR_18_3_REDUCTION);}
		@Override public Property<Double> getDeducciblesExpensesProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.DEDUCCIBLES_EXPENSES);}
		@Override public Property<Double> getSpousalSupportProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.SPOUSAL_SUPPORT);}
		@Override public Property<Double> getFoodAnnuityProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.FOOD_ANNUITY);}
		@Override public Property<Byte> getDeductHomeLoanProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.DEDUCT_HOME_LOAN);}
		@Override public Property<Double> getRequestIrpfProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.REQUEST_IRPF);}
		@Override public Property<Byte> getContractTypeProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.CONTRACT_TYPE);}
		@Override public Property<Byte> getCeutaMelillaProperty() {return new FilterDAO.PropertyDAO<>(IRPF_DATA.CEUTA_MELILLA);}
	}
	
	protected static class AgreementLevelCategoryPropertiesDAO implements AgreementLevelCategoryProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, AgreementLevelCategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AgreementLevelCategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(AGREEMENT_LEVEL_CATEGORY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(AGREEMENT_LEVEL_CATEGORY.DOMAIN);}
		@Override public Property<Integer> getAgreementLevelProperty() {return new FilterDAO.PropertyDAO<>(AGREEMENT_LEVEL_CATEGORY.AGREEMENT_LEVEL);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(AGREEMENT_LEVEL_CATEGORY.DESCRIPTION);}
		
	}
	
	protected static class InventoryPropertiesDAO implements InventoryProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, InventoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(InventoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY.DESCRIPTION);}
		@Override public Property<Date> getInventoryDateProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY.INVENTORY_DATE);}
		@Override public Property<Integer> getWarehouseProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY.WAREHOUSE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY.STATUS);}
	}
	
	protected static class InventoryDetailPropertiesDAO implements InventoryDetailProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, InventoryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(InventoryDetailFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY_DETAIL.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY_DETAIL.DOMAIN);}
		@Override public Property<Integer> getInventoryProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY_DETAIL.INVENTORY);}
 		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY_DETAIL.ITEM);}
		@Override public Property<Double> getActualQuantityProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY_DETAIL.ACTUAL_QUANTITY);}
		@Override public Property<Double> getRealQuantityProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY_DETAIL.REAL_QUANTITY);}
		@Override public Property<Double> getCostProperty() {return new FilterDAO.PropertyDAO<>(INVENTORY_DETAIL.COST);}
	}
	
	// ---------- COMMISSION

	protected static class CommissionPropertiesDAO implements CommissionProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, CommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.NAME);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.END_DATE);}
	}
	
	protected static class CommissionTypePropertiesDAO implements CommissionTypeProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, CommissionTypeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CommissionTypeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE.NAME);}
		@Override public Property<Double> getRateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE.RATE);}

	}
	
	protected static class OfferDetailCommissionPropertiesDAO implements OfferDetailCommissionProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, OfferDetailCommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(OfferDetailCommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL_COMMISSION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL_COMMISSION.DOMAIN);}
		@Override public Property<Double> getCommissionProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL_COMMISSION.COMMISSION);}
		@Override public Property<Integer> getOfferDetailProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL_COMMISSION.OFFER_DETAIL);}
		@Override public Property<Double> getAmountProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL_COMMISSION.AMOUNT);}
		@Override public Property<Date> getPayDateProperty() {return new FilterDAO.PropertyDAO<>(OFFER_DETAIL_COMMISSION.PAY_DATE);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(OFFER.ISSUE_DATE);}
		@Override public Property<String> getSerieProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(OFFER.NUMBER);}
		@Override public Property<Integer> getTargetProperty() {return new FilterDAO.PropertyDAO<>(OFFER.TARGET);}
		@Override public Property<Integer> getSupplierProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SUPPLIER);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(OFFER.SELLER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(OFFER.TYPE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(OFFER.STATUS);}
		
	}
	
	protected static class InvoiceDetailCommissionPropertiesDAO implements InvoiceDetailCommissionProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, InvoiceDetailCommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(InvoiceDetailCommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL_COMMISSION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL_COMMISSION.DOMAIN);}
		@Override public Property<Double> getCommissionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL_COMMISSION.COMMISSION);}
		@Override public Property<Integer> getInvoiceDetailProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL_COMMISSION.INVOICE_DETAIL);}
		@Override public Property<Double> getAmountProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL_COMMISSION.AMOUNT);}
		@Override public Property<Date> getPayDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_DETAIL_COMMISSION.PAY_DATE);}
		@Override public Property<Date> getDateProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.ISSUE_DATE);}
		@Override public Property<String> getSeriesProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SERIES);}
		@Override public Property<Integer> getNumberProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.NUMBER);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.REGISTRY);}
		@Override public Property<Integer> getSellerProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.SELLER);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.TYPE);}
		@Override public Property<Byte> getStatusProperty() {return new FilterDAO.PropertyDAO<>(INVOICE.STATUS);}
		
	}
	
	protected static class CommissionTypeCommissionPropertiesDAO implements CommissionTypeCommissionProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, CommissionTypeCommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CommissionTypeCommissionFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE_COMMISSION.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE_COMMISSION.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.NAME);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.END_DATE);}
		@Override public Property<Integer> getCommissionProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE_COMMISSION.COMMISSION);}
		@Override public Property<Integer> getCommissionTypeProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_TYPE_COMMISSION.COMMISSION_TYPE);}
	}
	
	protected static class CommissionItemPropertiesDAO implements CommissionItemProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, CommissionItemFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CommissionItemFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_ITEM.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_ITEM.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.NAME);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.END_DATE);}
		@Override public Property<Integer> getCommissionProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_ITEM.COMMISSION);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_ITEM.ITEM);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_ITEM.QUANTITY);}
		@Override public Property<Double> getAmountProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_ITEM.AMOUNT);}
		@Override public Property<Double> getRateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_ITEM.RATE);}
	}

	protected static class CommissionCategoryPropertiesDAO implements CommissionCategoryProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, CommissionCategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(CommissionCategoryFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_CATEGORY.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_CATEGORY.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.NAME);}
		@Override public Property<Date> getStartDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.START_DATE);}
		@Override public Property<Date> getEndDateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION.END_DATE);}
		@Override public Property<Integer> getCommissionProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_CATEGORY.COMMISSION);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_CATEGORY.CATEGORY);}
		@Override public Property<Double> getQuantityProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_CATEGORY.QUANTITY);}
		@Override public Property<Double> getRateProperty() {return new FilterDAO.PropertyDAO<>(COMMISSION_CATEGORY.RATE);}
	}
	
	protected static class ScopePropertiesDAO implements ScopeProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, ScopeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ScopeFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SCOPE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SCOPE.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(SCOPE.DESCRIPTION);}
	}
	
	protected static class MailTemplatePropertiesDAO implements MailTemplateProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, MailTemplateFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(MailTemplateFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.DOMAIN);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.SCOPE);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.NAME);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.ACTIVE);}
		@Override public Property<Timestamp> getCreationDateProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.CREATIONDATE);}
 		@Override public Property<String> getSubjectProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.SUBJECT);} 
		@Override public Property<String> getWidthProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.WIDTH);}
		@Override public Property<String> getTitleColorProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.TITLE_COLOR);}
		@Override public Property<String> getBackgroundColorProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.BACKGROUND_COLOR);}
		@Override public Property<Integer> getHeaderTemplateProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.HEADER_TEMPLATE);}
		@Override public Property<Integer> getFooterTemplateProperty() {return new FilterDAO.PropertyDAO<>(MK_TEMPLATE.FOOTER_TEMPLATE);}
	}

	protected static class ItemAddInfoPropertiesDAO implements ItemAddInfoProperties{
		protected Select<Record> build(SelectJoinStep<Record> select, ItemAddInfoFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(ItemAddInfoFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(ITEM_ADDINFO.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(ITEM_ADDINFO.DOMAIN);}
		@Override public Property<Integer> getProductProperty() {return new FilterDAO.PropertyDAO<>(ITEM_ADDINFO.PRODUCT);}
		@Override public Property<Integer> getItemProperty() {return new FilterDAO.PropertyDAO<>(ITEM_ADDINFO.ITEM);} 
		@Override public Property<String> getAttributeProperty() {return new FilterDAO.PropertyDAO<>(ITEM_ADDINFO.ATTRIBUTE);}
		@Override public Property<String> getValueProperty() {return new FilterDAO.PropertyDAO<>(ITEM_ADDINFO.VALUE);}
		@Override public Property<Date> getValueDate() {return new FilterDAO.PropertyDAO<>(ITEM_ADDINFO.VALUE_DATE);}
	}
	
	protected static class UserPropertiesDAO implements UserProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, UserFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(UserFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null){
				return new Condition[0];
			}
			return new Condition[] { filterDAO.getCondition() };
		}

		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(USER.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(USER.DOMAIN);}
		@Override public Property<String> getNameProperty() {return new FilterDAO.PropertyDAO<>(USER.NAME);}
		@Override public Property<String> getLoginProperty() {return new FilterDAO.PropertyDAO<>(USER.LOGIN);}
		@Override public Property<Byte> getActiveProperty() {return new FilterDAO.PropertyDAO<>(USER.ACTIVE);}
		@Override public Property<Integer> getEnterpriseProperty() {return new FilterDAO.PropertyDAO<>(USER.ENTERPRISE);}
		@Override public Property<Integer> getRegistryProperty() {return new FilterDAO.PropertyDAO<>(USER.REGISTRY);}

	}
	
}
