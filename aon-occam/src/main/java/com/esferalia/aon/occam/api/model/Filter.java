package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Properties.*;
import com.esferalia.aon.occam.api.model.finance.Properties.IRPFProperties;
import com.esferalia.aon.occam.api.model.finance.Properties.Mod145Properties;

public interface Filter extends Serializable{
	
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
		Filter match(T t);
		Filter between(T min, T max);
	}
	
	
	public Filter or(Filter filter);
	public Filter and(Filter filter);
	public Filter not(Filter filter);
	
	@Deprecated(forRemoval = true)
	public Filter page(Integer page);
	@Deprecated(forRemoval = true)
	public Filter perPage(Integer perPage);
	
	public Filter limit(int offset, int rows);
	
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
	public interface ItemCompositionFilter{
		Filter filter(ItemCompositionProperties properties);
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
	public interface RDirStaffFilter {
		Filter filter(RDirStaffProperties properties);
	}
	
	@FunctionalInterface
	public interface DomainFilter{
		Filter filter(DomainProperties properties);
	}
	
	@FunctionalInterface
	public interface DomainAppFilter{
		Filter filter(DomainAppProperties properties);
	}
	
	@FunctionalInterface
	public interface ScopeFilter{
		Filter filter(ScopeProperties properties);
	}
	
	@FunctionalInterface
	public interface UserAppRoleFilter{
		Filter filter(UserAppRoleProperties properties);
	}
	
	@FunctionalInterface
	public interface UserScopeFilter{
		Filter filter(UserScopeProperties properties);
	}
	
	@FunctionalInterface
	public interface UserWorkgroupFilter{
		Filter filter(UserWorkgroupProperties properties);
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
	public interface DeliveryInfoFilter{
		Filter filter(DeliveryInfoProperties properties);
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
	public interface PayrollWorkplaceFilter{
		Filter filter(PayrollWorkplaceProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryAddressFilter{
		Filter filter(RegistryAddressProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistrySegmentFilter{
		Filter filter(RegistrySegmentProperties properties);
	}
	
	@FunctionalInterface
	public interface RRelationshipFilter{
		Filter filter(RRelationshipProperties properties);
	}
	
	@FunctionalInterface
	public interface RelationshipFilter{
		Filter filter(RelationshipProperties properties);
	}
	
	@FunctionalInterface
	public interface SegmentFilter{
		Filter filter(SegmentProperties properties);
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
	public interface DailyTrackingFilter{
		Filter filter(DailyTrackingProperties properties);
	}
		
	@FunctionalInterface
	public interface JobTypeFilter{
		Filter filter(JobTypeProperties properties);
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
	public interface TaskWorkflowFilter{
		Filter filter(TaskWorkflowProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskAttachFilter{
		Filter filter(TaskAttachProperties properties);
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
	public interface CnoFilter{
		Filter filter(CnoProperties properties);
	}
	
	@FunctionalInterface
	public interface IAEFilter{
		Filter filter(IAEProperties properties);
	}

	@FunctionalInterface
	public interface Cnae2009Filter{
		Filter filter(Cnae2009Properties properties);
	}

	@FunctionalInterface
	public interface ProjectHolderFilter{
		Filter filter(ProjectHolderProperties properties);
	}
	
	@FunctionalInterface
	public interface ProjectActivityFilter{
		Filter filter(ProjectActivityProperties properties);
	}
	
	@FunctionalInterface
	public interface ProjectTypeFilter{
		Filter filter(ProjectTypeProperties properties);
	}
	
	@FunctionalInterface
	public interface ActivityTypeFilter{
		Filter filter(ActivityTypeProperties properties);
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
	public interface CreditorFilter{
		Filter filter(CreditorProperties properties);
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
	public interface DeliveryPackagingFilter{
		Filter filter(DeliveryPackagingProperties properties);
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
	public interface DataRequestFilter{
		Filter filter(DataRequestProperties properties);
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
	public interface SalesInfoFilter{
		Filter filter(SalesInfoProperties properties);
	}
	
	@FunctionalInterface
	public interface CategoryFilter{
		Filter filter(CategoryProperties properties);
	}
	
	@FunctionalInterface
	public interface NewsFilter{
		Filter filter(NewsProperties properties);
	}
	
	@FunctionalInterface
	public interface NewsletterFilter{
		Filter filter(NewsletterProperties properties);
	}
	
	@FunctionalInterface
	public interface SurveyFilter{
		Filter filter(SurveyProperties properties);
	}
	
	@FunctionalInterface
	public interface EmployeeFilter{
		Filter filter(EmployeeProperties properties);
	}

	@FunctionalInterface
	public interface ContractFilter{
		Filter filter(ContractProperties properties);
	}
	
	@FunctionalInterface
	public interface ContractExtendedDataFilter{
		Filter filter(ContractExtendedDataProperties properties);
	}
	
	@FunctionalInterface
	public interface SalaryNewPortalFilter{
		Filter filter(SalaryNewPortalProperties properties);
	}
	
	@FunctionalInterface
	public interface ContractDocFilter{
		Filter filter(ContractDocProperties properties);
	}
	
	@FunctionalInterface
	public interface InvoiceDocFilter{
		Filter filter(InvoiceDocProperties properties);
	}

	@FunctionalInterface
	public interface ContractDataFilter{
		Filter filter(ContractDataProperties properties);
	}
	
	@FunctionalInterface
	public interface EnterpriseDataFilter{
		Filter filter(EnterpriseDataProperties properties);
	}
	
	@FunctionalInterface
	public interface ContractLeaveFilter{
		Filter filter(ContractLeaveProperties properties);
	}
	
	
	@FunctionalInterface
	public interface ContractAttachFilter{
		Filter filter(ContractAttachProperties properties);
	}
	
	
	@FunctionalInterface
	public interface IrpfDataFilter{
		Filter filter(IrpfDataProperties properties);
	}
	
	@FunctionalInterface
	public interface EnterpriseFilter{
		Filter filter(com.esferalia.aon.occam.api.model.Properties.EnterpriseProperties properties);
	}
	
	@FunctionalInterface
	public interface EnterpriseActivityFilter{
		Filter filter(EnterpriseActivityProperties properties);
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
	public interface OfferDetailFilter{
		Filter filter(OfferDetailProperties properties);
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
	
	@FunctionalInterface
	public interface UserFilter{
		Filter filter(UserProperties properties);
	}
	
	@FunctionalInterface
	public interface CertificateFilter{
		Filter filter(CertificateProperties properties);
	}
	
	@FunctionalInterface
	public interface TimeControlFilter{
		Filter filter(TimeControlProperties properties);
	}
	
	@FunctionalInterface
	public interface LocationFilter{
		Filter filter(LocationProperties properties);
	}
	
	@FunctionalInterface
	public interface EnterpriseCCCFilter{
		Filter filter(EnterpriseCCCProperties properties);
	}
	
	@FunctionalInterface
	public interface NoteFilter{
		Filter filter(NoteProperties properties);
	}
	
	
	@FunctionalInterface
	public interface NotificationFilter{
		Filter filter(NotificationProperties properties);
	}
	
	@FunctionalInterface
	public interface AuthFilter {
		Filter filter(AuthProperties properties);
	}
	
	@FunctionalInterface
	public interface AuthAttachFilter{
		Filter filter(AuthAttachProperties properties);
	}
	
	@FunctionalInterface
	public interface AuthDeviceFilter{
		Filter filter(AuthDeviceProperties properties);
	}
	
	@FunctionalInterface
	public interface RawdocFilter{
		Filter filter(RawdocProperties properties);
	}
	
	@FunctionalInterface
	public interface TariffFilter{
		Filter filter(TariffProperties properties);
	}
	
	@FunctionalInterface
	public interface RattachTagFilter{
		Filter filter(RattachTagProperties properties);
	}
	
	@FunctionalInterface
	public interface PayMethodFilter{
		Filter filter(PayMethodProperties properties);
	}

	@FunctionalInterface
	public interface FiscalModelFilter{
		Filter filter(FiscalModelProperties properties);
	}
	
	@FunctionalInterface
	public interface WorkgroupFilter{
		Filter filter(WorkgroupProperties properties);
	}
	
	@FunctionalInterface
	public interface InvoiceDetailFilter{
		Filter filter(InvoiceDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface InvoiceTaxFilter{
		Filter filter(InvoiceTaxProperties properties);
	}
	
	@FunctionalInterface
	public interface InvoiceInfoFilter{
		Filter filter(InvoiceInfoProperties properties);
	}

	@FunctionalInterface
	public interface InvoiceCommunicationTrackingFilter{
		Filter filter(InvoiceCommunicationTrackingProperties properties);
	}
	
	@FunctionalInterface
	public interface InvoiceBatchFilter{
		Filter filter(InvoiceBatchProperties properties);
	}
	
	@FunctionalInterface
	public interface InvoiceBatchDetailFilter{
		Filter filter(InvoiceBatchDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface InvestAssetFilter{
		Filter filter(InvestAssetProperties properties);
	}
	
	@FunctionalInterface
	public static interface IRPFFilter{
		Filter filter(IRPFProperties properties);
	}
	
	@FunctionalInterface
	public interface Mod145Filter{
		Filter filter(Mod145Properties properties);
	}
	
	@FunctionalInterface
	public interface QuestionFilter{
		Filter filter(QuestionProperties properties);
	}
	
	@FunctionalInterface
	public interface MarketingCampaignFilter{
		Filter filter(MarketingCampaignProperties properties);
	}
	
}
