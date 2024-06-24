package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Rawdoc.RAWDOC;
import static com.esferalia.aon.jooq.tables.Product.PRODUCT;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.util.Collection;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import com.esferalia.aon.occam.api.model.Order.CustomerOrder;
import com.esferalia.aon.occam.api.model.Order.InvoiceOrder;
import com.esferalia.aon.occam.api.model.Order.ProductOrder;
import com.esferalia.aon.occam.api.model.Order.PropertyOrder;
import com.esferalia.aon.occam.api.model.Order.RawdocOrder;
import com.esferalia.aon.occam.api.model.Order.SupplierCreditorOrder;
import com.esferalia.aon.occam.api.model.PropertyOrders.CustomerPropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.InvoicePropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.ProductPropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.RawdocPropertyOrders;
import com.esferalia.aon.occam.api.model.PropertyOrders.SupplierCreditorPropertyOrders;

public class PropertyOrdersDAO {

	public static class InvoicePropertyOrdersDAO implements InvoicePropertyOrders {
		public Collection<SortField<?>> getOrders(InvoiceOrder order){
			OrderDAO orderDAO = (OrderDAO) order.order(this);
			return orderDAO.getOrder();
		}
		@Override public PropertyOrder getStartIssueDatePropertyName() {return new OrderDAO.PropertyOrderDAO<>(INVOICE.ISSUE_DATE);}
		@Override public PropertyOrder getReferenceCodePropertyName() {return new OrderDAO.PropertyOrderDAO<>(INVOICE.REFERENCE_CODE);}
		@Override public PropertyOrder getRegistryNamePropertyName() {return new OrderDAO.PropertyOrderDAO<>(INVOICE.RNAME);}
		@Override public PropertyOrder getTotalPropertyName() {return new OrderDAO.PropertyOrderDAO<>(INVOICE.TOTAL);}
	}
	
	public static class RawdocPropertyOrdersDAO implements RawdocPropertyOrders {
		public Collection<SortField<?>> getOrders(RawdocOrder order){
			OrderDAO orderDAO = (OrderDAO) order.order(this);
			return orderDAO.getOrder();
		}
		@Override public PropertyOrder getStartIssueDatePropertyName() {return new OrderDAO.PropertyOrderDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.date"));}
		@Override public PropertyOrder getReferenceCodePropertyName() {return new OrderDAO.PropertyOrderDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.reference"));}
		@Override public PropertyOrder getRegistryNamePropertyName() {return new OrderDAO.PropertyOrderDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.name"));}
		@Override public PropertyOrder getTotalPropertyName() {return new OrderDAO.PropertyOrderDAO<>(DSL.jsonValue(RAWDOC.JSON.cast(SQLDataType.JSON), "$.total"));}
	}
	
	public static class ProductPropertyOrdersDAO implements ProductPropertyOrders {
		public Collection<SortField<?>> getOrders(ProductOrder order){
			OrderDAO orderDAO = (OrderDAO) order.order(this);
			return orderDAO.getOrder();
		}
		@Override public PropertyOrder getNamePropertyName() {return new OrderDAO.PropertyOrderDAO<>(PRODUCT.NAME);}
		@Override public PropertyOrder getCodePropertyName() {return new OrderDAO.PropertyOrderDAO<>(PRODUCT.CODE);}
		@Override public PropertyOrder getCategoryPropertyName() {return new OrderDAO.PropertyOrderDAO<>(CATEGORY.NAME);}
	}
	
	public static class CustomerPropertyOrdersDAO implements CustomerPropertyOrders {
		public Collection<SortField<?>> getOrders(CustomerOrder order){
			OrderDAO orderDAO = (OrderDAO) order.order(this);
			return orderDAO.getOrder();
		}
		@Override public PropertyOrder getRegistryNamePropertyName() {return new OrderDAO.PropertyOrderDAO<>(REGISTRY.NAME);}
		@Override public PropertyOrder getRegistryDocumentPropertyName() {return new OrderDAO.PropertyOrderDAO<>(REGISTRY.DOCUMENT);}
		@Override public PropertyOrder getRegistryNationalityPropertyName() {return new OrderDAO.PropertyOrderDAO<>(REGISTRY.NATIONALITY);}
	}
	
	public static class SupplierCreditorPropertyOrdersDAO implements SupplierCreditorPropertyOrders {
		public Collection<SortField<?>> getOrders(SupplierCreditorOrder order){
			OrderDAO orderDAO = (OrderDAO) order.order(this);
			return orderDAO.getOrder();
		}
		@Override public PropertyOrder getRegistryNamePropertyName() {return new OrderDAO.PropertyOrderDAO<>(REGISTRY.NAME);}
		@Override public PropertyOrder getRegistryDocumentPropertyName() {return new OrderDAO.PropertyOrderDAO<>(REGISTRY.DOCUMENT);}
		@Override public PropertyOrder getRegistryNationalityPropertyName() {return new OrderDAO.PropertyOrderDAO<>(REGISTRY.NATIONALITY);}
	}
	
}
