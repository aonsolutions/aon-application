package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.ContactProperties;
import com.esferalia.aon.occam.api.model.Properties.DepartmentProperties;
import com.esferalia.aon.occam.api.model.Properties.DomainProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;

public interface Filter {
	
	public interface Property<T> {
		Filter eq(T t);
		Filter ne(T t);
		Filter le(T t);
		Filter lt(T t);
		Filter gt(T t);
		Filter ge(T t);
		Filter in(T[] t);
		Filter isNull();
		Filter isNotNull();
		Filter like(T t);
		Filter between(T min, T max);
	}
	
	
	public Filter or(Filter filter);
	public Filter and(Filter filter);
	public Filter not(Filter filter);
	
	@FunctionalInterface
	public interface WarehouseFilter{
		Filter filter(WarehouseProperties properties);
	}
	
	@FunctionalInterface
	public interface DepartmentFilter{
		Filter filter(DepartmentProperties properties);
	}
	
	@FunctionalInterface
	public interface ProductFilter{
		Filter filter(ProductProperties properties);
	}
	
	@FunctionalInterface
	public interface ItemFilter{
		Filter filter(ItemProperties properties);
	}
	
	@FunctionalInterface
	public interface DomainFilter{
		Filter filter(DomainProperties properties);
	}
	
	@FunctionalInterface
	public interface MailAccountFilter{
		Filter filter(MailAccountProperties properties);
	}
	
	@FunctionalInterface
	public interface ContactFilter{
		Filter filter(ContactProperties properties);
	}
	
	

}
