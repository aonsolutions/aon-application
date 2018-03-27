package com.esferalia.aon.occam.api.model.stat.invoice;

import java.io.Serializable;

public enum DirectSalesChartType implements Serializable {

	 DELIVERY_TYPE_BY_YEAR_COMBO_CHART("Ventas directas por a\u00F1os", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitDirectSalesTypeByYearComboChart();
		}
	 })
	 
	,DELIVERY_TYPE_BY_MONTHS_COMBO_CHART("Ventas directas por meses", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitDirectSalesTypeByMonthsComboChart();
		}
	})
	
	,DELIVERY_TYPE_BY_WEEKS_COMBO_CHART("Ventas directas por semanas", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitDirectSalesTypeByWeeksComboChart();
		}
	})

	,DELIVERY_TYPE_BY_DAYS_COMBO_CHART("Ventas directas por d\u00EDas", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitDirectSalesTypeByDaysComboChart();
		}
	})
	,ABC_DELIVERY_TITULAR("ABC Titular venta directa", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcDirectSalesTitular();
		}
	})
	,ABC_DELIVERY_TITULAR_ADDRESS("ABC Direcci\u00F3n del titular venta directa", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcDirectSalesTitularAddress();
		}
	})
	,ABC_DELIVERY_PRODUCT("ABC Productos", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcDirectSalesProduct();
		}
	})
	,ABC_DELIVERY_CATEGORY("ABC Categor\u00EDa", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcDirectSalesCategory();
		}
	})
	,ABC_PRODUCT_BRAND("ABC Marca producto", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcProductBrand();
		}
	})
	,ABC_DELIVERY_WORKPLACE("ABC Centro de trabajo", new ITypeVisitor() {
		@Override
		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcDirectSalesWorkplace();
		}
	})
//	,ABC_DELIVERY_SELLER("ABC Agente comercial", new ITypeVisitor() {
//		@Override
//		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
//			chartVisitor. visitAbcDirectSalesSeller();
//		}
//	})
//	,DELIVERY_GEO_PROVINCE("Zona geogr\u00E1fica", new ITypeVisitor() {
//		@Override
//		public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
//			chartVisitor. visitGeoProvince();
//		}
//	})
	;
	
	
	public static interface ITypeVisitor {
		void visit(IDirectSalesChartTypeVisitor chartVisitor);
	}
	
	private String description;
	private ITypeVisitor typeVisitor;
	
	private DirectSalesChartType(String description,ITypeVisitor typeVisitor ) {
		this.description = description;
		this.typeVisitor = typeVisitor;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void visit(IDirectSalesChartTypeVisitor chartVisitor) {
		typeVisitor.visit(chartVisitor);
	}
	
	public Byte value() {
		return (byte) this.ordinal();
	}

}
