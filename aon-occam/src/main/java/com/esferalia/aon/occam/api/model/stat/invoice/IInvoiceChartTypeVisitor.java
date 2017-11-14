package com.esferalia.aon.occam.api.model.stat.invoice;

public interface IInvoiceChartTypeVisitor {

	void visitInvoiceTypeByYearComboChart();

	void visitInvoiceTypeByMonthsComboChart();

	void visitInvoiceTypeByWeeksComboChart();

	void visitInvoiceTypeByDaysComboChart();

	void visitAbcInvoiceTitular();

	void visitAbcInvoiceTitularAddress();

	void visitAbcInvoiceCategory();

	void visitAbcInvoiceProduct();

	void visitAbcInvoiceWorkplace();

	void visitAbcInvoiceSeller();

	void visitGeoProvince();
	
}
