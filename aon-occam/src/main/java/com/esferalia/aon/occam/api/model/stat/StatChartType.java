package com.esferalia.aon.occam.api.model.stat;

import java.io.Serializable;

public enum StatChartType implements Serializable {

	 INVOICE_TYPE_BY_YEAR_COMBO_CHART("Facturaci\u00F3n por a\u00F1os", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByYearComboChart();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	 })
	 
	,INVOICE_TYPE_BY_MONTHS_COMBO_CHART("Facturaci\u00F3n por meses", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByMonthsComboChart();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	
	,INVOICE_TYPE_BY_DAYS_COMBO_CHART("Facturaci\u00F3n por d\u00EDas", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitInvoiceTypeByDaysComboChart();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	,ABC_INVOICE_TITULAR("ABC Titular Factura", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceTitular();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	,ABC_INVOICE_CATEGORY("ABC Categor\u00EDa", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceCategory();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	,ABC_INVOICE_PRODUCT("ABC Productos", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceProduct();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	,ABC_INVOICE_WORKPLACE("ABC Centro de trabajo", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceWorkplace();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	,ABC_INVOICE_SELLER("ABC Agente Comercial", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitAbcInvoiceSeller();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	,INVOICE_GEO_PROVINCE("Zona geogr\u00E1fica", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitGeoProvince();
		}

		@Override
		public String type() {
			return INVOICE;
		}
	})
	,TASK_BY_STATUS("Tareas por estado", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByStatus();
		}

		@Override
		public String type() {
			return TASK;
		}
	})
	,TASK_BY_TYPE("Tareas por tipo", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByType();
		}

		@Override
		public String type() {
			return TASK;
		}
	})
	,TASK_BY_SCHEDULE("Tareas por franja horaria", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskBySchedule();
		}

		@Override
		public String type() {
			return TASK;
		}
	})
	,TASK_BY_DAY_OF_WEEK("Tareas por dia de la semana", new ITypeVisitor() {
		@Override
		public void visit(IStatChartTypeVisitor chartVisitor) {
			chartVisitor. visitTaskByDayOfWeek();
		}

		@Override
		public String type() {
			return TASK;
		}
	})
	;
	
	public static final String TASK = "task";
	public static final String INVOICE = "invoice";
	
	public static interface ITypeVisitor {
		String type();
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
	
	public String getType() {
		return typeVisitor.type();
	}

}
