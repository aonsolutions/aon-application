package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Order.PropertyOrder;

public interface PropertyOrders {

	public interface InvoicePropertyOrders {
		PropertyOrder getIdPropertyName();
		PropertyOrder getStartIssueDatePropertyName();
		PropertyOrder getReferenceCodePropertyName();
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getTotalPropertyName();
	}
	
	public interface RawdocPropertyOrders {
		PropertyOrder getIdPropertyName();
		PropertyOrder getStartIssueDatePropertyName();
		PropertyOrder getReferenceCodePropertyName();
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getTotalPropertyName();
	}
	
	public interface CustomerPropertyOrders {
		PropertyOrder getIdPropertyName();
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getRegistryDocumentPropertyName();
		PropertyOrder getRegistryNationalityPropertyName();
	}
	
	public interface SupplierCreditorPropertyOrders {
		PropertyOrder getIdPropertyName();
		PropertyOrder getRegistryNamePropertyName();
		PropertyOrder getRegistryDocumentPropertyName();
		PropertyOrder getRegistryNationalityPropertyName();
	}
	
	public interface ProductPropertyOrders {
		PropertyOrder getIdPropertyName();
		PropertyOrder getNamePropertyName();
		PropertyOrder getCodePropertyName();
		PropertyOrder getCategoryPropertyName();
	}
	
}
