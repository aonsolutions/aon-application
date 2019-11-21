package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.AccountEntryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.AccountEntryProperties;
import com.esferalia.aon.occam.api.model.Properties.AccountingRegistryProperties;
import com.esferalia.aon.occam.api.model.Properties.AgreementLevelCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.ApplicationParameterProperties;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.Properties.BrandProperties;
import com.esferalia.aon.occam.api.model.Properties.CarrierPackingProperties;
import com.esferalia.aon.occam.api.model.Properties.CarrierProperties;
import com.esferalia.aon.occam.api.model.Properties.CategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionItemProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionTypeCommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.CommissionTypeProperties;
import com.esferalia.aon.occam.api.model.Properties.CompanyProperties;
import com.esferalia.aon.occam.api.model.Properties.ContactProperties;
import com.esferalia.aon.occam.api.model.Properties.ContractDataProperties;
import com.esferalia.aon.occam.api.model.Properties.ContractProperties;
import com.esferalia.aon.occam.api.model.Properties.CustomerProperties;
import com.esferalia.aon.occam.api.model.Properties.DataResponseDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.DataResponseProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.DepartmentProperties;
import com.esferalia.aon.occam.api.model.Properties.DomainProperties;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.Properties.GeoZoneProperties;
import com.esferalia.aon.occam.api.model.Properties.IncomeDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.IncomeProperties;
import com.esferalia.aon.occam.api.model.Properties.InventoryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.InventoryProperties;
import com.esferalia.aon.occam.api.model.Properties.InvoiceDetailCommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.IrpfDataProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemAddInfoProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;
import com.esferalia.aon.occam.api.model.Properties.MailTemplateProperties;
import com.esferalia.aon.occam.api.model.Properties.OfferDetailCommissionProperties;
import com.esferalia.aon.occam.api.model.Properties.PersonProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductTagProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectCommercialProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectReservationProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.PurchaseProperties;
import com.esferalia.aon.occam.api.model.Properties.RecordDataProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddInfoProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryBankProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryItemProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryNoteProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryPayMethodProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistrySellerProperties;
import com.esferalia.aon.occam.api.model.Properties.SalesDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.Properties.ScopeProperties;
import com.esferalia.aon.occam.api.model.Properties.SellerProperties;
import com.esferalia.aon.occam.api.model.Properties.SeriesProperties;
import com.esferalia.aon.occam.api.model.Properties.SignatureProperties;
import com.esferalia.aon.occam.api.model.Properties.StockProperties;
import com.esferalia.aon.occam.api.model.Properties.SupplierProperties;
import com.esferalia.aon.occam.api.model.Properties.TagProperties;
import com.esferalia.aon.occam.api.model.Properties.TargetProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskCommentProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskEventProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderWorkgroupProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskTagProperties;
import com.esferalia.aon.occam.api.model.Properties.TaxProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseTransferDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseTransferProperties;

public interface Filter {
	
	public interface Property<T> {
		Filter eq(T t);
		Filter ne(T t);
		Filter le(T t);
		Filter lt(T t);
		Filter gt(T t);
		Filter ge(T t);
		Filter in(T[] t);
		Filter notIn(T[] t);
		Filter isNull();
		Filter isNotNull();
		Filter like(T t);
		Filter between(T min, T max);
	}
	
	
	public Filter or(Filter filter);
	public Filter and(Filter filter);
	public Filter not(Filter filter);
	
	public Filter page(Integer page);
	public Filter perPage(Integer perPage);

	@FunctionalInterface
	public interface ApplicationParameterFilter{
		Filter filter(ApplicationParameterProperties properties);
	}
	
	@FunctionalInterface
	public interface AttachFilter{
		Filter filter(AttachProperties properties);
	}
	
	@FunctionalInterface
	public interface WarehouseFilter{
		Filter filter(WarehouseProperties properties);
	}
	
	@FunctionalInterface
	public interface ElaborationFilter{
		Filter filter(ElaborationProperties properties);
	}
	
	@FunctionalInterface
	public interface ElaborationDetailFilter{
		Filter filter(ElaborationDetailProperties properties);
	}

	@FunctionalInterface
	public interface ElaborationDetailCompositionFilter{
		Filter filter(ElaborationDetailCompositionProperties properties);
	}
	
	@FunctionalInterface
	public interface FeeFilter{
		Filter filter(FeeProperties properties);
	}
	
	@FunctionalInterface
	public interface DepartmentFilter{
		Filter filter(DepartmentProperties properties);
	}
	
	@FunctionalInterface
	public interface ProductFilter{
		Filter filter(ProductProperties properties);
	}
	
	@FunctionalInterface
	public interface ItemFilter{
		Filter filter(ItemProperties properties);
	}
	
	@FunctionalInterface
	public interface ItemAddInfoFilter {
		Filter filter(ItemAddInfoProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryAddInfoFilter {
		Filter filter(RegistryAddInfoProperties properties);
	}
	
	@FunctionalInterface
	public interface DomainFilter{
		Filter filter(DomainProperties properties);
	}
	
	@FunctionalInterface
	public interface ScopeFilter{
		Filter filter(ScopeProperties properties);
	}
	
	@FunctionalInterface
	public interface MailAccountFilter{
		Filter filter(MailAccountProperties properties);
	}
	
	@FunctionalInterface
	public interface ContactFilter{
		Filter filter(ContactProperties properties);
	}
	
	@FunctionalInterface
	public interface DeliveryFilter{
		Filter filter(DeliveryProperties properties);
	}
	
	@FunctionalInterface
	public interface DeliveryDetailFilter{
		Filter filter(DeliveryDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface TagFilter{
		Filter filter(TagProperties properties);
	}
	
	@FunctionalInterface
	public interface BrandFilter{
		Filter filter(BrandProperties properties);
	}
	
	@FunctionalInterface
	public interface TaxFilter{
		Filter filter(TaxProperties properties);
	}
	
	@FunctionalInterface
	public interface ProductCategoryFilter{
		Filter filter(ProductCategoryProperties properties);
	}
	
	@FunctionalInterface
	public interface SignatureFilter{
		Filter filter(SignatureProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryAddressFilter{
		Filter filter(RegistryAddressProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistrySellerFilter{
		Filter filter(RegistrySellerProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryMediaFilter{
		Filter filter(RegistryMediaProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryNoteFilter{
		Filter filter(RegistryNoteProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryItemFilter{
		Filter filter(RegistryItemProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryBankFilter{
		Filter filter(RegistryBankProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryPayMethodFilter{
		Filter filter(RegistryPayMethodProperties properties);
	}
	
	@FunctionalInterface
	public interface StockFilter{
		Filter filter(StockProperties properties);
	}
	
	@FunctionalInterface
	public interface WarehouseTransferFilter{
		Filter filter(WarehouseTransferProperties properties);
	}
	
	@FunctionalInterface
	public interface WarehouseTransferDetailFilter{
		Filter filter(WarehouseTransferDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface SeriesFilter{
		Filter filter(SeriesProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskFilter{
		Filter filter(TaskProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskTagFilter{
		Filter filter(TaskTagProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskCommentFilter{
		Filter filter(TaskCommentProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskEventFilter{
		Filter filter(TaskEventProperties properties);
	}

	@FunctionalInterface
	public interface TaskHolderFilter{
		Filter filter(TaskHolderProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskHolderWorkgroupFilter{
		Filter filter(TaskHolderWorkgroupProperties properties);
	}

	@FunctionalInterface
	public interface GeoZoneFilter{
		Filter filter(GeoZoneProperties properties);
	}
	
	@FunctionalInterface
	public interface ProjectReservationFilter{
		Filter filter(ProjectReservationProperties properties);
	}
	
	@FunctionalInterface
	public interface ProjectCommercialFilter{
		Filter filter(ProjectCommercialProperties properties);
	}
	
	@FunctionalInterface
	public interface CustomerFilter{
		Filter filter(CustomerProperties properties);
	}
	
	@FunctionalInterface
	public interface SellerFilter{
		Filter filter(SellerProperties properties);
	}
	
	@FunctionalInterface
	public interface CarrierPackingFilter{
		Filter filter(CarrierPackingProperties properties);
	}
	
	@FunctionalInterface
	public interface CarrierFilter{
		Filter filter(CarrierProperties properties);
	}
	
	@FunctionalInterface
	public interface SupplierFilter{
		Filter filter(SupplierProperties properties);
	}
	
	@FunctionalInterface
	public interface TargetFilter{
		Filter filter(TargetProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryFilter{
		Filter filter(RegistryProperties properties);
	}
	
	@FunctionalInterface
	public interface PurchaseFilter{
		Filter filter(PurchaseProperties properties);
	}
	
	@FunctionalInterface
	public interface PurchaseDetailFilter{
		Filter filter(PurchaseDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface RecordDataFilter{
		Filter filter(RecordDataProperties properties);
	}
	
	@FunctionalInterface
	public interface CompanyFilter{
		Filter filter(CompanyProperties properties);
	}
	
	@FunctionalInterface
	public interface PersonFilter{
		Filter filter(PersonProperties properties);
	}
	
	@FunctionalInterface
	public interface ProductTagFilter{
		Filter filter(ProductTagProperties properties);
	}
	
	@FunctionalInterface
	public interface IncomeFilter{
		Filter filter(IncomeProperties properties);
	}
	
	@FunctionalInterface
	public interface IncomeDetailFilter{
		Filter filter(IncomeDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface DataResponseFilter{
		Filter filter(DataResponseProperties properties);
	}
	
	@FunctionalInterface
	public interface DataResponseDetailFilter{
		Filter filter(DataResponseDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface SalesFilter{
		Filter filter(SalesProperties properties);
	}
	
	@FunctionalInterface
	public interface SalesDetailFilter{
		Filter filter(SalesDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface CategoryFilter{
		Filter filter(CategoryProperties properties);
	}
	
	@FunctionalInterface
	public interface ContractFilter{
		Filter filter(ContractProperties properties);
	}
	
	@FunctionalInterface
	public interface ContractDataFilter{
		Filter filter(ContractDataProperties properties);
	}
	
	@FunctionalInterface
	public interface IrpfDataFilter{
		Filter filter(IrpfDataProperties properties);
	}
	
	@FunctionalInterface
	public interface AgreementLevelCategoryFilter{
		Filter filter(AgreementLevelCategoryProperties properties);
	}
	
	@FunctionalInterface
	public interface InventoryFilter{
		Filter filter(InventoryProperties properties);
	}
	
	@FunctionalInterface
	public interface InventoryDetailFilter{
		Filter filter(InventoryDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface CommissionFilter{
		Filter filter(CommissionProperties properties);
	}
	
	@FunctionalInterface
	public interface CommissionTypeFilter{
		Filter filter(CommissionTypeProperties properties);
	}
	
	
	@FunctionalInterface
	public interface CommissionTypeCommissionFilter{
		Filter filter(CommissionTypeCommissionProperties properties);
	}
	
	@FunctionalInterface
	public interface CommissionItemFilter{
		Filter filter(CommissionItemProperties properties);
	}
	
	@FunctionalInterface
	public interface CommissionCategoryFilter{
		Filter filter(CommissionCategoryProperties properties);
	}
	
	@FunctionalInterface
	public interface OfferDetailCommissionFilter{
		Filter filter(OfferDetailCommissionProperties properties);
	}
	
	@FunctionalInterface
	public interface InvoiceDetailCommissionFilter{
		Filter filter(InvoiceDetailCommissionProperties properties);
	}
	
	@FunctionalInterface
	public interface AccountEntryFilter{
		Filter filter(AccountEntryProperties properties);
	}

	@FunctionalInterface
	public interface AccountEntryDetailFilter{
		Filter filter(AccountEntryDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface AccountingRegistryFilter{
		Filter filter(AccountingRegistryProperties properties);
	}
	
	@FunctionalInterface
	public interface MailTemplateFilter{
		Filter filter(MailTemplateProperties properties);
	}
}
