package com.esferalia.aon.occam.api.model.stat;

public interface IStatChartTypeVisitor {

	void visitInvoiceTypeByYearComboChart();

	void visitInvoiceTypeByMonthsComboChart();

	void visitInvoiceTypeByDaysComboChart();

	void visitAbcInvoiceTitular();
}
