package com.esferalia.aon.occam.api.model.stat;

public interface IStatFilterItemVisitor {
	
	 void visitInvoiceTypeCondition(StatFilterItem item);
	 void visitProductCategoryCondition(StatFilterItem item);
	 void visitWorkplaceCondition(StatFilterItem item);
	void visitSellerCondition(StatFilterItem item);

}
