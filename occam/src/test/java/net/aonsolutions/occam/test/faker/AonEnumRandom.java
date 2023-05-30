package net.aonsolutions.occam.test.faker;

import java.util.Optional;

import net.aonsolutions.occam.api.constants.AccountingPeriodStatus;
import net.aonsolutions.occam.api.constants.Administration;
import net.aonsolutions.occam.api.constants.AonApp;
import net.aonsolutions.occam.api.constants.AonLanguage;
import net.aonsolutions.occam.api.constants.AonModule;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.AppParam;
import net.aonsolutions.occam.api.constants.Country;
import net.aonsolutions.occam.api.constants.DocumentType;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.constants.InvoiceSource;
import net.aonsolutions.occam.api.constants.InvoiceType;
import net.aonsolutions.occam.api.constants.RectificationType;
import net.aonsolutions.occam.api.constants.SecurityLevel;
import net.aonsolutions.occam.api.constants.StreetType;
import net.aonsolutions.occam.api.constants.TaxType;
import net.aonsolutions.occam.api.constants.TransactionType;
import net.aonsolutions.occam.api.constants.VatDeductionType;
import net.aonsolutions.occam.api.constants.WithholdingType;
import net.aonsolutions.occam.api.constants.WithholdingTypeGroup;

public class AonEnumRandom {
	private static final int REQUIRED = -1;
	
	public static Administration getAdministration() {
		return getAdministration(REQUIRED).get();
	}
	public static Optional<Administration> getAdministration(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(Administration.values()[AonRandom.number(Administration.values().length)])
			:Optional.empty();
	}

	public static DomainType getDomainType() {
		return getDomainType(REQUIRED).get();
	}
	public static Optional<DomainType> getDomainType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(DomainType.values()[AonRandom.number(DomainType.values().length)])
			:Optional.empty();
	}
	
	public static AonStatus getAonStatus() {
		return getAonStatus(REQUIRED).get();
	}
	public static Optional<AonStatus> getAonStatus(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(AonStatus.values()[AonRandom.number(AonStatus.values().length)])
			:Optional.empty();
	}
	
	public static Country getCountry() {
		return getCountry(REQUIRED).get();
	}
	public static Optional<Country> getCountry(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(Country.values()[AonRandom.number(Country.values().length)])
			:Optional.empty();
	}

	public static DocumentType getDocumentType() {
		return getDocumentType(REQUIRED).get();
	}
	public static Optional<DocumentType> getDocumentType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(DocumentType.values()[AonRandom.number(DocumentType.values().length)])
			:Optional.empty();
	}

	public static InvoiceType getInvoiceType() {
		return getInvoiceType(REQUIRED).get();
	}
	public static Optional<InvoiceType> getInvoiceType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(InvoiceType.values()[AonRandom.number(InvoiceType.values().length)])
			:Optional.empty();
	}

	public static SecurityLevel getSecurityLevel() {
		return getSecurityLevel(REQUIRED).get();
	}
	public static Optional<SecurityLevel> getSecurityLevel(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(SecurityLevel.values()[AonRandom.number(SecurityLevel.values().length)])
			:Optional.empty();
	}
	
	public static StreetType getStreetType() {
		return getStreetType(REQUIRED).get();
	}
	public static Optional<StreetType> getStreetType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(StreetType.values()[AonRandom.number(StreetType.values().length)])
			:Optional.empty();
	}

	public static AonApp getAonApp() {
		return getAonApp(REQUIRED).get();
	}
	public static Optional<AonApp> getAonApp(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(AonApp.values()[AonRandom.number(AonApp.values().length)])
			:Optional.empty();
	}

	public static AonModule getAonModule() {
		return getAonModule(REQUIRED).get();
	}
	public static Optional<AonModule> getAonModule(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(AonModule.values()[AonRandom.number(AonModule.values().length)])
			:Optional.empty();
	}

	public static AonLanguage getAonLanguage() {
		return getAonLanguage(REQUIRED).get();
	}
	public static Optional<AonLanguage> getAonLanguage(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(AonLanguage.values()[AonRandom.number(AonLanguage.values().length)])
			:Optional.empty();
	}

	public static AppParam getAppParam() {
		return getAppParam(REQUIRED).get();
	}
	public static Optional<AppParam> getAppParam(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(AppParam.values()[AonRandom.number(AppParam.values().length)])
			:Optional.empty();
	}
	
	public static AccountingPeriodStatus getAccountingPeriodStatus() {
		return getAccountingPeriodStatus(REQUIRED).get();
	}
	public static Optional<AccountingPeriodStatus> getAccountingPeriodStatus(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(AccountingPeriodStatus.values()[AonRandom.number(AccountingPeriodStatus.values().length)])
			:Optional.empty();
	}
	
	public static RectificationType getRectificationType() {
		return getRectificationType(REQUIRED).get();
	}
	public static Optional<RectificationType> getRectificationType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(RectificationType.values()[AonRandom.number(RectificationType.values().length)])
			:Optional.empty();
	}

	public static TransactionType getTransactionType() {
		return getTransactionType(REQUIRED).get();
	}
	public static Optional<TransactionType> getTransactionType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(TransactionType.values()[AonRandom.number(TransactionType.values().length)])
			:Optional.empty();
	}

	public static TaxType getTaxType() {
		return getTaxType(REQUIRED).get();
	}
	public static Optional<TaxType> getTaxType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(TaxType.values()[AonRandom.number(TaxType.values().length)])
			:Optional.empty();
	}

	public static WithholdingTypeGroup getWithholdingTypeGroup() {
		return getWithholdingTypeGroup(REQUIRED).get();
	}
	public static Optional<WithholdingTypeGroup> getWithholdingTypeGroup(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(WithholdingTypeGroup.values()[AonRandom.number(WithholdingTypeGroup.values().length)])
			:Optional.empty();
	}

	public static WithholdingType getWithholdingType() {
		return getWithholdingType(REQUIRED).get();
	}
	public static Optional<WithholdingType> getWithholdingType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(WithholdingType.values()[AonRandom.number(WithholdingType.values().length)])
			:Optional.empty();
	}

	public static VatDeductionType getVatDeductionType() {
		return getVatDeductionType(REQUIRED).get();
	}
	public static Optional<VatDeductionType> getVatDeductionType(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(VatDeductionType.values()[AonRandom.number(VatDeductionType.values().length)])
			:Optional.empty();
	}

	public static InvoiceSource getInvoiceSource() {
		return getInvoiceSource(REQUIRED).get();
	}
	public static Optional<InvoiceSource> getInvoiceSource(int nullThreshold) {
    	return AonRandom.gt(nullThreshold)
			?Optional.of(InvoiceSource.values()[AonRandom.number(InvoiceSource.values().length)])
			:Optional.empty();
	}

}


