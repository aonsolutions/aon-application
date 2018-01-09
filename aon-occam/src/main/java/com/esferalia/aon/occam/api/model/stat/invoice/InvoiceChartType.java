package com.esferalia.aon.occam.api.model.stat.invoice;

import java.io.Serializable;

public enum InvoiceChartType implements Serializable {

	 INVOICE_TYPE_BY_YEAR_COMBO_CHART("Facturaci\u00F3n por a\u00F1os", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByYearComboChart();
		}
	 })
	 
	,INVOICE_TYPE_BY_MONTHS_COMBO_CHART("Facturaci\u00F3n por meses", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByMonthsComboChart();
		}
	})
	
	,INVOICE_TYPE_BY_WEEKS_COMBO_CHART("Facturaci\u00F3n por semanas", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByWeeksComboChart();
		}
	})

	,INVOICE_TYPE_BY_DAYS_COMBO_CHART("Facturaci\u00F3n por d\u00EDas", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByDaysComboChart();
		}
	})
	,ABC_INVOICE_TITULAR("ABC Titular factura", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceTitular();
		}
	})
	,ABC_INVOICE_TITULAR_ADDRESS("ABC Titular factura / Direcci\u00F3n facturaci\u00F3n ", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceTitularAddress();
		}
	})
	,ABC_INVOICE_PRODUCT("ABC Productos", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceProduct();
		}
	})
	,ABC_INVOICE_CATEGORY("ABC Categor\u00EDa", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceCategory();
		}
	})
	,ABC_PRODUCT_BRAND("ABC Marca producto", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcProductBrand();
		}
	})
	,ABC_INVOICE_WORKPLACE("ABC Centro de trabajo", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceWorkplace();
		}
	})
	,ABC_INVOICE_SELLER("ABC Agente comercial", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceSeller();
		}
	})
	,INVOICE_GEO_PROVINCE("Zona geogr\u00E1fica", new ITypeVisitor() {
		@Override
		public void visit(IInvoiceChartTypeVisitor chartVisitor) {
			chartVisitor. visitGeoProvince();
		}
	})
	;
	
	
	public static interface ITypeVisitor {
		void visit(IInvoiceChartTypeVisitor chartVisitor);
	}
	
	private String description;
	private ITypeVisitor typeVisitor;
	
	private InvoiceChartType(String description,ITypeVisitor typeVisitor ) {
		this.description = description;
		this.typeVisitor = typeVisitor;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void visit(IInvoiceChartTypeVisitor chartVisitor) {
		typeVisitor.visit(chartVisitor);
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}

}
