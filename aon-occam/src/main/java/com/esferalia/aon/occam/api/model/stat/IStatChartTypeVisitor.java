package com.esferalia.aon.occam.api.model.stat;

public interface IStatChartTypeVisitor {

	void visitInvoiceTypeByYearComboChart();

	void visitInvoiceTypeByMonthsComboChart();

	void visitInvoiceTypeByDaysComboChart();

	void visitAbcInvoiceTitular();

	void visitAbcInvoiceCategory();

	void visitAbcInvoiceProduct();

	void visitAbcInvoiceWorkplace();

	void visitAbcInvoiceSeller();

	void visitGeoProvince();
	
	// -------------------- TASK STAT

	void visitTaskByStatus();
	
	void visitTaskByType();

	void visitTaskBySchedule();
	
	void visitTaskByDayOfWeek();
	
	void visitTaskByMonth();
	
	void visitTaskByDay();
}
