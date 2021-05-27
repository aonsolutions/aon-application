package com.esferalia.aon.occam.api.model.stat.invoice;

public interface IDirectSalesChartTypeVisitor {

	void visitDirectSalesTypeByYearComboChart();

	void visitDirectSalesTypeByMonthsComboChart();

	void visitDirectSalesTypeByWeeksComboChart();

	void visitDirectSalesTypeByDaysComboChart();

	void visitAbcDirectSalesTitular();

	void visitAbcDirectSalesTitularAddress();

	void visitAbcDirectSalesCategory();

	void visitAbcDirectSalesProduct();

	void visitAbcDirectSalesWorkplace();

//	void visitAbcDirectSalesSeller();

//	void visitGeoProvince();

	void visitAbcProductBrand();
	
}
