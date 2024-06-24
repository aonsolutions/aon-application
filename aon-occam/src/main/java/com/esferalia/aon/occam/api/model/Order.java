package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.PropertyOrders.CustomerPropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.InvoicePropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.ProductPropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.RawdocPropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.SupplierCreditorPropertyOrders;

public interface Order extends Serializable {
	
	Order and(Order order);
	public int size();

	public interface PropertyOrder {
		IOrderBy orderBy();
	}
	
	public interface IOrderBy {
		Order ASC();
		Order DESC();
	}
	
	@FunctionalInterface
	public interface InvoiceOrder{
		Order order(InvoicePropertyOrders propertyOrders);
	}
	
	@FunctionalInterface
	public interface RawdocOrder{
		Order order(RawdocPropertyOrders propertyOrders);
	}
	
	@FunctionalInterface
	public interface CustomerOrder{
		Order order(CustomerPropertyOrders propertyOrders);
	}
	
	@FunctionalInterface
	public interface SupplierCreditorOrder{
		Order order(SupplierCreditorPropertyOrders propertyOrders);
	}
	
	@FunctionalInterface
	public interface ProductOrder{
		Order order(ProductPropertyOrders propertyOrders);
	}
	
}
