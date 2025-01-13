package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;

public enum InvoiceErrorKey implements Serializable {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	 ACCOUNT_ENTRY("Apunte contable")
	 	{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitAccountEntry();}}
	,ADDRESS("Direcci\u00F3n del titular")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitAddress();}}
	,AMBIGUOUS_REGISTRY("Titular de la factura")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitAmbiguousRegistry();}}
	,ATTACH ("Documento adjunto")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) { return visitor.visitAttach();}}
	,BASES_QUOTAS("Bases y cuotas")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitBasesQuotas();}}
	,DETAIL_DESCRIPTION("Descripci\u00F3n de la l\u00EDnea")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitDetailDescription();}}
	,DETAILS("Detalles de la factura")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitDetails();}}
	,DOMAIN("Dominio")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitDomain();}}
	,DUPLICATED_REFERENCE_CODE("N\u00FAmero")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitDuplicatedReferenceCode();}}
	,DUPLICATED_SERIES_NUMBER("Serie/N\u00FAmero")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitDuplicatedSeriesNumber();}}
	,EXPENSE_ACCOUNT("Cuenta contable de gasto")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitExpenseAccount();}}
	,FINANCE_AMOUNT_ZERO("Importe del vencimiento no puede ser cero")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) { return visitor.visitFinanceAmountZero();}}
	,FINANCE_TOTAL_AMOUNT("Importe del vencimiento no conincide con total factura")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitFinanceTotalAmount();}}
	,FINANCE_WRONG_ACCOUNT_BANK("Cuenta bancaria incorrecta.")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitFinanceAccountBank();}}
	,FINANCE_WRONG_DUE_DATE("fecha del vencimiento incorrecta")
		{ @Override  public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitFinanceAmountZero();}}
	,GENERIC("Gen\u00E9rico")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitGeneric();}}
	,INVESTMENT("Inversi\u00F3n")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitInvestment();}}
	,IRPF_RATE("Tipo (%) retenci\u00F3n")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitIrpfRate();}}
	,IRPF_QUOTA("Retenci\u00F3n")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitIrpfQuota();}}
	,ISSUE_DATE("Fecha de factura")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitIssueDate();}}
	,NUMBER("N\u00FAmero")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitNumber();}}
	,PAY_METHOD("Forma de pago")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitPayMethod();}}
	,RDOCUMENT("N\u00BA documento del titular")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitRdocument();}}
	,RDOCUMENT_COUNTRY("Pa\u00CDs del documento del titular")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitRdocumentCountry();}}
	,REFERENCE_CODE("N\u00BA factura")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitReferenceCode();}}
	,REGISTRY("Titular de la factura")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitRegistry();}}
	,RNAME("Raz\u00F3n social del titular")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitRname();}}
	,SCOPE("\u00C1mbito")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitScope();}}
	,SERIES("Serie")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitSeries();}}
	,SURCHARGE("Recargo de equivalencia")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitSurcharge();}}
	,TAX_BASE("Base Imponible")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitTaxBase();}}
	,TAX_DATE("Fecha de IVA")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitTaxDate();}}
	,TAX_QUOTA("Cuota")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitTaxQuota();}}
	,TAX_RATE("Tipo (%) del Impuesto")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitTaxRate();}}
	,TOTAL("Total")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitTotal();}}
	,TRANSACTION("Tipo de transacci\u00F3n")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitTransaction();}}
	,TYPE("Tipo de factura")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitType();}}
	,WITHHOLDING("Retenci\u00F3n")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitWithholding();}}
	,WORKPLACE("Centro de trabajo")
		{ @Override public <T> T visit(InvoiceErrorKeyVisitor<T> visitor) {return visitor.visitWorkplace();}}
		
	;

	private String description;

	private InvoiceErrorKey() {
	}

	private InvoiceErrorKey(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public abstract <T> T visit(InvoiceErrorKeyVisitor<T> visitor);
	public interface InvoiceErrorKeyVisitor<T> {
		T visitInvestment();
		T visitGeneric();
		T visitDomain();
		T visitType();
		T visitSeries();
		T visitNumber();
		T visitReferenceCode();
		T visitTransaction();
		T visitIssueDate();
		T visitTaxDate();
		T visitTaxRate();
		T visitTaxBase();
		T visitTaxQuota();
		T visitIrpfRate();
		T visitIrpfQuota();
		T visitScope();
		T visitRegistry();
		T visitAmbiguousRegistry();
		T visitRdocument();
		T visitRdocumentCountry();
		T visitRname();
		T visitAddress();
		T visitDetailDescription();
		T visitDetails();
		T visitAccountEntry();
		T visitDuplicatedSeriesNumber();
		T visitDuplicatedReferenceCode();
		T visitFinanceAmountZero();
		T visitFinanceTotalAmount();
		T visitFinanceWrongDate();
		T visitFinanceAccountBank();
		T visitWorkplace();
		T visitBasesQuotas();
		T visitPayMethod();
		T visitTotal();
		T visitAttach();
		T visitSurcharge();
		T visitWithholding();
		T visitExpenseAccount();
	}
	
}