package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import net.aonsolutions.occam.api.model.Filter.Property;


public interface Properties {
	// --------------------------------------------------------------------- [A]
	public interface AccountEntryProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getAccountPeriodProperty();
		Property<Date> getEntryDateProperty();
		Property<Byte> getEntryTypeProperty();
		Property<Integer> getJournalProperty();
		Property<Integer> getActivityProperty();
		Property<Byte> getConfidentialProperty();
		Property<String> getCommentsProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}
	
	public interface AccountEntryDetailProperties extends AccountEntryProperties {
		Property<Integer> getAccountProperty();
		Property<String> getAccountCodeProperty();
		Property<String> getAccountDescriptionProperty();
		Property<String> getConceptProperty();
		Property<Double> getDebitProperty();
		Property<Double> getCreditProperty();
		Property<String> getDocumentNumber();
		Property<Integer> getBalancingAccountProperty();
		Property<String> getBalancingAccountCodeProperty();
		Property<String> getBalancingAccountDescriptionProperty();
	}

	public interface AccountPeriodProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Date> getInitiationDateProperty();
		Property<Date> getDeadlineProperty();
		Property<Byte> getStatusProperty();
	}
	
	public interface AccountProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getCodeProperty();
		Property<String> getDescriptionProperty();
		Property<String> getAliasProperty();
		Property<Byte> getEntryEnabledProperty();
		Property<Byte> getLevelProperty();
		Property<Byte> getActiveProperty();
		Property<String> getCostCenterProperty();
	}

	public interface AuditProperties {
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
	}
	
	// --------------------------------------------------------------------- [C]
	public interface CnaeProperties {
		Property<Integer> getIdProperty();
		Property<String> getCodeProperty();
		Property<String> getTitleProperty();		
	}
	
	public interface CreditorProperties extends RegistryProperties, AuditProperties {
		Property<Byte> getWithholdingProperty();
		Property<Byte> getVatAccrualPaymentProperty();
		Property<Byte> getTransactionProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getScopeProperty();
		Property<Integer> getAccountProperty();
	}

	public interface CustomerProperties extends RegistryProperties, AuditProperties{
		Property<Integer> getTariffProperty();
		Property<Byte> getSurchargeProperty();
		Property<Byte> getWithholdingProperty();
		Property<Byte> getTransactionProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getScopeProperty();
		Property<Byte> getEInvoiceProperty();
		Property<Integer> getInvoicingGroupProperty();
		Property<Byte> getProjectGroupedProperty();
		Property<Byte> getDeliveryGroupedProperty();
		Property<Byte> getDeliveryValuatedProperty();
		Property<Integer> getAccountProperty();
	}
	// --------------------------------------------------------------------- [F]
	public interface FinanceProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getScopeProperty();
		Property<Byte> getFinanceTypeProperty();
		Property<Integer> getRegistryProperty();
		Property<String> getRegistryDocumentProperty();
		Property<Byte> getRegistryDocumentTypeProperty();
		Property<String> getRegistryDocumentCountryProperty();
		Property<String> getRegistryNameProperty();
		Property<Double> getAmountProperty();
		Property<Double> getExpensesProperty();
		Property<String> getConceptProperty();
		Property<Integer> getInvoiceProperty();
		Property<Date> getDueDateProperty();
		Property<Integer> getPayMethodProperty();
		Property<String> getBankAccountProperty();
		Property<String> getBankAliasProperty();
		Property<String> getBicProperty();
		Property<String> getChequeNumberProperty();
		Property<Byte> getStatusProperty();
		Property<Byte> getConfidentialProperty();
		Property<String> getRemarksProperty();
		Property<String> getInvoiceReferenceCodeProperty();
		Property<Date> getInvoiceDateProperty();
		Property<Byte> getPayMethodTypeProperty();
		Property<Byte> getPayrollProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
	}
	// --------------------------------------------------------------------- [G]
	public interface GeozoneProperties{
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getCodeProperty();
		Property<String> getNameProperty();
		Property<Byte> getSystemProperty();
	}
	// --------------------------------------------------------------------- [I]
	public interface IaeProperties {
		Property<Integer> getIdProperty();
		Property<String> getSectionProperty();
		Property<String> getEpigraphProperty();
		Property<String> getTitleProperty();		
	}
	
	public interface InvoiceProperties extends Serializable {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<Integer> getActivityProperty();
		Property<Integer> getInvestAssetProperty();
		Property<Integer> getProjectProperty();
		Property<String> getSeriesProperty();
		Property<Integer> getNumberProperty();	
		Property<String> getReferenceCodeProperty();
		Property<Integer> getRegistryProperty();
		Property<String> getRegistryDocumentProperty();
		Property<Byte> getRegistryDocumentTypeProperty();
		Property<String> getRegistryDocumentCountryProperty();
		Property<String> getRegistryNameProperty();
		Property<Integer> getRegistryAddressProperty();
		Property<Date> getIssueDateProperty();
		Property<Date> getTaxDateProperty();
		Property<Byte> getSecurityLevelProperty();
		Property<Byte> getStatusProperty();
		Property<Byte> getTypeProperty();
		Property<Byte> getSurchargeProperty();
		Property<Byte> getWithholdingProperty();
		Property<Byte> getWithholdingFarmerProperty();
		Property<Byte> getVatAccrualPayment(); 
		Property<String> getCommentsProperty();
		Property<String> getRemarksProperty();
		Property<Byte> getInvestmentProperty();
		Property<Byte> getTransactionProperty(); 
		Property<Byte> getSignedProperty();
		Property<Integer> getScopeProperty(); 
		Property<Byte> getServiceProperty();
		Property<Byte> getRectificationTypeProperty();
		Property<Integer> getRectificationInvoiceProperty();
		Property<Integer> getSellerProperty(); 
		Property<Double> getTaxableBaseProperty();
		Property<Double> getVatQuotaProperty();
		Property<Double> getRetentionQuotaTotalProperty();
		Property<Double> getTotalProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<Timestamp> getModificationDateProperty();
		Property<String> getModificationUserProperty();
	}	
	// --------------------------------------------------------------------- [R]
	public interface RegistryProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getDocumentProperty();
		Property<Byte> getDocumentTypeProperty();
		Property<String> getDocumentCountryProperty();
		Property<String> getNameProperty();
		Property<String> getAliasProperty();
		Property<Byte> getTypeProperty();
		Property<String> getNationalityProperty();
		Property<Byte> getSecurityLevelProperty();
	}
	
	// --------------------------------------------------------------------- [S]
	public interface SupplierProperties extends RegistryProperties, AuditProperties{
		Property<Integer> getTariffProperty();
		Property<Byte> getWithholdingProperty();
		Property<Byte> getWithholdingFarmerProperty();
		Property<Byte> getVatAccrualPaymentProperty();
		Property<Byte> getTransactionProperty();
		Property<Byte> getStatusProperty();
		Property<Integer> getScopeProperty();
		Property<Byte> getPurchaseValuatedProperty();
		Property<Integer> getAccountProperty();
	}
	
	// --------------------------------------------------------------------- [T]
	public interface TariffProperties {
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getCodeProperty();
		Property<String> getNameProperty();
		Property<Byte> getPurchaseProperty();
		Property<Double> getDiscountProperty();
		Property<Byte> getActiveProperty();
	}
	
}
