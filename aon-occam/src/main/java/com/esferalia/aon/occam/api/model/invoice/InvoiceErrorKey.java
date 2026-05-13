package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceErrorKey implements Serializable {
	DOMAIN("Dominio") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitDomain(t);
		}
	},
	INVESTMENT("Inversi\u00F3n") { 
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitInvestment(t);
		}
	},
	SURCHARGE("Recargo de equivalencia") { 
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitSurcharge(t);
		}
	},
	WORKPLACE("Centro de trabajo") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitWorkplace(t);
		}
	},
	TYPE("Tipo de factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitType(t);
		}
	},
	BASES_QUOTAS("Bases y cuotas") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitBasesQuotas(t);
		}
	},
	SERIES("Serie") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitSeries(t);
		}
	},
	NUMBER("N\u00FAmero") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitNumber(t);
		}
	},
	DUPLICATED_SERIES_NUMBER("Serie/N\u00FAmero") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitDuplicatedSeriesNumber(t);
		}
	},
	REFERENCE_CODE("N\u00BA factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitReferenceCode(t);
		}
	},
	DUPLICATED_REFERENCE_CODE("N\u00FAmero") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitDuplicatedReferenceCode(t);
		}
	},
	TRANSACTION("Tipo de transacci\u00F3n") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitTransaction(t);
		}
	},
	ISSUE_DATE("Fecha de factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitIssueDate(t);
		}
	},
	TAX_DATE("Fecha de IVA") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitTaxDate(t);
		}
	},
	TAX_RATE("Tipo (%) de IVA") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitTaxRate(t);
		}
	},
	TAX_BASE("Base Imponible") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitTaxBase(t);
		}
	},
	TAX_QUOTA("Cuota") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitTaxQuota(t);
		}
	},
	SCOPE("\u00C1mbito") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitScope(t);
		}
	},
	REGISTRY("Titular de la factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitRegistry(t);
		}
	},
	REGISTRY_STATUS("Titular de la factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitRegistryStatus(t);
		}
	},
	AMBIGUOUS_REGISTRY("Titular de la factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitAmbiguousRegistry(t);
		}
	},
	RDOCUMENT("N\u00BA documento del titular") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitRdocument(t);
		}
	},
	RDOCUMENT_COUNTRY("Pa\u00CDs del documento del titular") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitRdocumentCountry(t);
		}
	},
	RNAME("Raz\u00F3n social del titular") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitRname(t);
		}
	},
	ADDRESS("Direcci\u00F3n del titular") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitAddress(t);
		}
	},
	DETAIL_DESCRIPTION("Descripci\u00F3n de la l\u00EDnea") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitDetailDescription(t);
		}
	},
	DETAILS("Detalles de la factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitDetails(t);
		}
	},
	ACCOUNT_ENTRY("Apunte contable") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitAccountEntry(t);
		}
	},
	FINANCE_AMOUNT_ZERO("importe del vencimiento no puede ser cero") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitFinanceAmountZero(t);
		}
	},
	FINANCE_TOTAL_AMOUNT("importe del vencimiento no conincide con total factura") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitFinanceTotalAmount(t);
		}
	},
	FINANCE_WRONG_DUE_DATE("fecha del vencimiento incorrecta") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitFinanceAmountZero(t);
		}
	},
	FINANCE_WRONG_ACCOUNT_BANK("Cuenta bancaria incorrecta.") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitFinanceAccountBank(t);
		}
	},
	TOTAL("Total") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitTotal(t);
		}
	},
	IRPF_RATE("Tipo (%) retención") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitIrpfRate(t);
		}
	},
	IRPF_QUOTA("Retención") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitIrpfQuota(t);
		}
	},
	PAY_METHOD("Forma de pago") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitPayMethod(t);
		}
	},
	GENERIC("Gen\u00E9rico"){
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitGeneric(t);
		}
	},
	WITHHOLDING("Retenci\u00F3n"){
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitWithholding(t);
		}
	},
	EXPENSE_ACCOUNT("Cuenta contable de gasto"){
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitExpenseAccount(t);
		}
	},
	COMMUNICATION("Comunicaci\u00F3n") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitCommunication(t);
		}
	},
	OCR("OCR") {
		@Override
		public <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t) {
			visitor.visitOCR(t);
		}
	},
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
	public static String name( InvoiceErrorKey k ) {
		return k == null ? null : k.name();
	}
	public static InvoiceErrorKey safeValueOf(String name) {
		return AonCollectionUtils.stream( InvoiceErrorKey.values() )
			.filter( k -> AonStringUtils.equalsIgnoreCase(k.name(), name))
			.findFirst()
			.orElse(null);
	}

	public abstract <T> void visit(InvoiceErrorKeyVisitor<T> visitor, T t);
}