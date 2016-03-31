package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;

public enum StatChartType implements Serializable {

	 INVOICE_TYPE_BY_YEAR_COMBO_CHART("Facturaci\u00F3n por a\u00F1os", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByYearComboChart();
		}
	 })
	 
	,INVOICE_TYPE_BY_MONTHS_COMBO_CHART("Facturaci\u00F3n por meses", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByMonthsComboChart();
		}
	})
	
	,INVOICE_TYPE_BY_DAYS_COMBO_CHART("Facturaci\u00F3n por d\u00EDas", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByDaysComboChart();
		}
	})
	,ABC_INVOICE_TITULAR("ABC Titular Factura", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceTitular();
		}
	})
	;
	
	public static interface ITypeVisitor {
		void visit(IStatChartTypeVisitor chartVisitor);
	}
	
	private String description;
	private ITypeVisitor typeVisitor;
	
	private StatChartType(String description,ITypeVisitor typeVisitor ) {
		this.description = description;
		this.typeVisitor = typeVisitor;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void visit(IStatChartTypeVisitor chartVisitor) {
		typeVisitor.visit(chartVisitor);
	}

}
