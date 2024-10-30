package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum InvoiceHeaderMetadata implements Serializable {
	 ID { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitDomain();} }
	,ACTIVITY { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitActivity();} }
	,PROJECT { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitProject();} }
	,SERIES { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitSeries();} }
	,NUMBER { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitNumber();} }
	,REFERENCE_CODE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitReferenceCode();} }
	,REGISTRY { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRegistry();} }
	,RDOCUMENT { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRdocument();} }
	,RDOCUMENT_TYPE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRdocumentType();} }
	,RDOCUMENT_COUNTRY { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRdocumentCountry();} }
	,RNAME { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRname();} }
	,ISSUE_DATE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitIssueDate();} }
	,TAX_DATE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitTaxDate();} }
	,SECURITY_LEVEL { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitSecurityLevel();} }
	,STATUS { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitStatus();} }
	,TYPE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitType();} }
	,SURCHARGE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitSurcharge();} }
	,WITHHOLDING { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitWithholding();} }
	,WITHHOLDING_FARMER { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitWithholdingFarmer();} }
	,VAT_ACCRUAL_PAYMENT { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitVatAccrualPayment();} }
	,COMMENTS { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitComments();} }
	,REMARKS { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRemarks();} }
	,INVESTMENT { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitInvestment();} }
	,TRANSACTION { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitTransaction();} }
	,SIGNED { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitSigned();} }
	,SCOPE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitScope();} }
	,SERVICE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitService();} }
	,RECTIFICATION_TYPE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRectificationType();} }
	,RECTIFICATION_INVOICE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRectificationInvoice();} }
	,SELLER { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitSeller();} }
	,TAXABLE_BASE { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitTaxableBase();} }
	,VAT_QUOTA { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitVatQuota();} }
	,RETENTION_QUOTA { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitRetentionQuota();} }
	,TOTAL { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitTotal();} }
	
	,ACCOUNT { @Override public <T> T visit(InvoiceHeaderMetadataVisitor<T> v) { return v.visitAccount();} }
	
	,	;

	public abstract <T> T visit(InvoiceHeaderMetadataVisitor<T> v);
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
}
