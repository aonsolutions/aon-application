package net.aonsolutions.occam.api.model.metadata;

import java.io.Serializable;

public enum InvoiceDetailMetadata implements Serializable {
	 ID { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitId();} }
	,DOMAIN { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitDomain();} }
	,INVOICE { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitInvoice();} }
	,INVEST_ASSET { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitInvestAsset();} }
	,PROJECT { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitProject();} }
	,LINE { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitLine();} }
	,ITEM { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitItem();} }
	,DESCRIPTION { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitDescription();} }
	,QUANTITY { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitQuantity();} }
	,PRICE { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitPrice();} }
	,DISCOUNT_EXPR { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitDiscountExpr();} }
	,SOURCE { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitSource();} }
	,SOURCE_ID { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitSourceId();} }
	,TAXABLE_BASE { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitTaxableBase();} }
	,TAXES { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitTaxes();} }
	,PREPAYMENT { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitPrepayment();} }
	,SELLER { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitSeller();} }
	,WORKPLACE { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitWorkplace();} }
	,WAREHOUSE { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitWarehouse();} }
	,INVOICE_TAXES { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitInvoiceTaxes();} }
	,EXP_ACCOUNT { @Override public <T> T visit(InvoiceDetailMetadataVisitor<T> v) { return v.visitExpAccount();} }
	,	;

	public abstract <T> T visit(InvoiceDetailMetadataVisitor<T> v);
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
	}
}
