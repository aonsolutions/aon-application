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
	,ABC_INVOICE_CATEGORY("ABC Categor\u00EDa", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceCategory();
		}
	})
	,ABC_INVOICE_PRODUCT("ABC Productos", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceProduct();
		}
	})
	,ABC_INVOICE_WORKPLACE("ABC Centro de trabajo", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceWorkplace();
		}
	})
	,ABC_INVOICE_SELLER("ABC Agente Comercial", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceSeller();
		}
	})
	,INVOICE_GEO_PROVINCE("Zona geogr\u00E1fica", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitGeoProvince();
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
