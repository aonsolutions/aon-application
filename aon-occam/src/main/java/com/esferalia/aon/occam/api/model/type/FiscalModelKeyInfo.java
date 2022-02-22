package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;

public enum FiscalModelKeyInfo implements Serializable {
	 MODEL_INVOICE_IRPF_BREAKDOWN {
		@Override
		public String getLabel() {
			return "Ver desglose de retenciones en facturas";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitModelInvoiceIrpfBreakdown();
		}
	}
	,NONE {
		@Override
		public String getLabel() {
			return "Sin informaci\u00F3n";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitNone();
		}
	}
	, INVOICE {
		@Override
		public String getLabel() {
			return "Ver desglose en facturas";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitInvoice();
		}
	}
	, IN_ACCRUAL_INVOICE{
		@Override
		public String getLabel() {
			return "Ver desglose en facturas";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitInAccrualInvoice();
		}
	}
	, OUT_ACCRUAL_INVOICE{
		@Override
		public String getLabel() {
			return "Ver desglose en facturas";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitOutAccrualInvoice();
		}
	}
	, DIFF_INVOICE{
		@Override
		public String getLabel() {
			return "Detalle del c\u00E1lculo por diferencia. Facturas - declarado";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitDiffInvoice();
		}
	}
	, DIFF_IN_ACCRUAL_INVOICE{
		@Override
		public String getLabel() {
			return "Detalle del c\u00E1lculo por diferencia. Facturas - declarado";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitDiffInAccrualInvoice();
		}
	}
	, DIFF_OUT_ACCRUAL_INVOICE{
		@Override
		public String getLabel() {
			return "Detalle del c\u00E1lculo por diferencia. Facturas - declarado";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitDiffOutAccrualInvoice();
		}
	}
	, SALARY{
		@Override
		public String getLabel() {
			return "Ver desglose de retenciones monetarias en n\u00F3minas";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitSalary();
		}
	}
	, SALARY_IN_KIND{
		@Override
		public String getLabel() {
			return "Ver desglose de retenciones en especie en n\u00F3minas";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitSalaryInKind();
		}
	}
	, DIFF_SALARY{
		@Override
		public String getLabel() {
			return "Detalle del c\u00E1lculo por diferencia. N\u00F3minas - declarado";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitDiffSalary();
		}
	}
	, COMPUTE{
		@Override
		public String getLabel() {
			return "Ver desglose de c\u00E1lculos";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitCompute();
		}
	}
	, COMPUTE_KEY{
		@Override
		public String getLabel() {
			return "Ver desglose de c\u00E1lculos";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitComputeKey();
		}
	}
	, ACT_ACCOUNT{
		@Override
		public String getLabel() {
			return "Ver desglose de cuentas";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitActAccount();
		}
	}
	, TITLE{
		@Override
		public String getLabel() {
			return "T\u00EDtulo";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitTitle();
		}
	}
	, IRPF_ACTIVITY{
		@Override
		public String getLabel() {
			return "Detalle de la actividad";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitIrpfActivity();
		}
	}
	, CORPORATE{
		@Override
		public String getLabel() {
			return "Ver desglose de c\u00E1lculos";
		}
		@Override
		public <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor) {
			return visitor.visitCorporate();
		}
	}
	;
	
	public abstract String getLabel();
	public abstract <T> T visit(IFiscalModelKeyInfoVisitor<T> visitor);
}
