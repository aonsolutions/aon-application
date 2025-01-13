package net.aonsolutions.occam.api.model.metadata;

public interface MetadataVisitor {
	
	public interface AccountMetadataVisitor<T> {
		 T visitId();
		 T visitDomain();
		 T visitCode();
		 T visitDescription();
		 T visitAlias();
		 T visitEntryEnabled();
		 T visitLevel();
		 T visitActive();
		 T visitCostCenter();
	}

	public interface AccountEntryMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitAccountPeriod();
		T visitActivity();
		T visitEntryDate();
		T visitEntryType();
		T visitJournal();
		T visitSecurityLevel();
		T visitComments();
		T visitDetails();
	}
	
	public static interface AccountEntryDetailMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitAccountEntry();
		T visitLine();
		T visitAccount();
		T visitConcept();
		T visitBalancingAccount();
		T visitDebit();
		T visitCredit();
		T visitDocumentNumber();
	}
	
	public interface AccountPeriodMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitName();
		T visitInitiationDate();
		T visitDeadline();
		T visitStatus();
	}

	public interface ActivityMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitDescription();
		T visitMain();
		T visitIae();
		T visitCnae();
		T visitSurcharge();
		T visitVatRegime();
		T visitVatExemptionCause();
		T visitIrpfRegime();
		T visitStartDate();
		T visitEndDate();
	}
	
	public static interface FinanceMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitType();
		T visitRegistry();
		T visitRegistryDocument();
		T visitRegistryDocumentType();
		T visitRegistryDocumentCountry();
		T visitRegistryAccount();
		T visitRname();
		T visitAmount();
		T visitExpenses();
		T visitConcept();
		T visitInvoice();
		T visitDueDate();
		T visitPayMethod();
		T visitBankAccount();
		T visitBankAlias();
		T visitBic();
		T visitChequeNumber();
		T visitStatus();
		T visitSecurityLevel();
		T visitRemarks();
		T visitScope();
		T visitManual();
		T visitAdvance();
		T visitPayroll();
		T visitPrepayment();
		T visitSourceId();
		T visitFinanceGroup();
		T visitPaidDate();
	}
	
	public static interface InvoiceMetadataVisitor<T> {
		T visitHeader();
		T visitRectificationInvoice();
		T visitAddress();
		T visitDetails();
		T visitFinances();
		T visitTaxBreakdown();
		T visitFiscal();
		T visitAttach();
		T visitRawdocId();
		T visitMessages();
	}
	
	public static interface InvoiceAddressMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitInvoice();
		T visitStreetType();
		T visitAddress();
		T visitNumber();
		T visitAddress2();
		T visitZip();
		T visitCity();
		T visitProvince();
		T visitGeozone();
		T visitParent();
	}
	
	public static interface InvoiceDetailMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitInvoice();
		T visitInvestAsset();
		T visitProject();
		T visitLine();
		T visitItem();
		T visitDescription();
		T visitQuantity();
		T visitPrice();
		T visitDiscountExpr();
		T visitSource();
		T visitSourceId();
		T visitTaxableBase();
		T visitTaxes();
		T visitPrepayment();
		T visitSeller();
		T visitWorkplace();
		T visitWarehouse();
		T visitInvoiceTaxes();
		T visitExpAccount();
		T visitDirectTaxPercent();
		T visitAdjDirectTaxAccount();
	}

	public static interface InvoiceFiscalMetadataVisitor<T> {
		T visitInvoice();
		T visitDomain();
		T visitIssueDate();
		T visitTaxDate();
		T visitExpDate();
		T visitVatGeneral();
		T visitVatSimplified();
		T visitVatSurcharge();
		T visitVatAccrualPayment();
		T visitVatRebuOperation();
		T visitVatRebuProfit();
		T visitVatTravelAgency();
		T visitVatAgriculture();
		T visitVatGold();
		T visitVatUnionExternal();
		T visitVatUnion();
		T visitVatImportation();
		T visitVatExempt();
	}

	public static interface InvoiceHeaderMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitActivity();
		T visitProject();
		T visitSeries();
		T visitNumber();
		T visitReferenceCode();
		T visitRegistry();
		T visitRdocument();
		T visitRdocumentType();
		T visitRdocumentCountry();
		T visitRname();
		T visitIssueDate();
		T visitTaxDate();
		T visitSecurityLevel();
		T visitStatus();
		T visitType();
		T visitSurcharge();
		T visitWithholding();
		T visitWithholdingFarmer();
		T visitVatAccrualPayment();
		T visitComments();
		T visitRemarks();
		T visitInvestment();
		T visitTransaction();
		T visitSigned();
		T visitScope();
		T visitService();
		T visitRectificationType();
		T visitRectificationInvoice();
		T visitSeller();
		T visitTaxableBase();
		T visitVatQuota();
		T visitRetentionQuota();
		T visitTotal();
		T visitAccount();
	}

	public static interface InvoiceInfoMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitInvoice();
		T visitType();
		T visitStatus();
	}

	public static interface InvoiceTaxMetadataVisitor<T> {
		T visitId();
		T visitDomain();
		T visitInvoiceDetail();
		T visitTaxType();
		T visitBase();
		T visitPercentage();
		T visitSurcharge();
		T visitQuota();
		T visitSurchargeQuota();
		T visitVatDeductionType();
		T visitWithholdingType();
		T visitDeductiblePercent();
		T visitDeductibleQuota();
		T visitWithholdingAccount();
		T visitOutputAccount();
		T visitInputAccount();
		T visitAdjAccount();
	}
	
}

