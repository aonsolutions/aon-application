package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Order.PropertyOrder;

public interface PropertyOrders {

	public interface InvoicePropertyOrders {
		PropertyOrder getStartIssueDatePropertyName();
		PropertyOrder getReferenceCodePropertyName();
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getTotalPropertyName();
	}
	
	public interface RawdocPropertyOrders {
		PropertyOrder getStartIssueDatePropertyName();
		PropertyOrder getReferenceCodePropertyName();
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getTotalPropertyName();
	}
	
	public interface CustomerPropertyOrders {
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getRegistryDocumentPropertyName();
		PropertyOrder getRegistryNationalityPropertyName();
	}
	
	public interface SupplierCreditorPropertyOrders {
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getRegistryDocumentPropertyName();
		PropertyOrder getRegistryNationalityPropertyName();
	}
	
	public interface ProductPropertyOrders {
		PropertyOrder getNamePropertyName();
		PropertyOrder getCodePropertyName();
		PropertyOrder getCategoryPropertyName();
	}
	
}
