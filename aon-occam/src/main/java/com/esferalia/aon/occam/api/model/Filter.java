package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.BrandProperties;
import com.esferalia.aon.occam.api.model.Properties.ContactProperties;
import com.esferalia.aon.occam.api.model.Properties.DepartmentProperties;
import com.esferalia.aon.occam.api.model.Properties.DomainProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.Properties.SeriesProperties;
import com.esferalia.aon.occam.api.model.Properties.SignatureProperties;
import com.esferalia.aon.occam.api.model.Properties.StockProperties;
import com.esferalia.aon.occam.api.model.Properties.TagProperties;
import com.esferalia.aon.occam.api.model.Properties.TaxProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseTransferDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.WarehouseTransferProperties;

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
	
	@FunctionalInterface
	public interface TagFilter{
		Filter filter(TagProperties properties);
	}
	
	@FunctionalInterface
	public interface BrandFilter{
		Filter filter(BrandProperties properties);
	}
	
	@FunctionalInterface
	public interface TaxFilter{
		Filter filter(TaxProperties properties);
	}
	
	@FunctionalInterface
	public interface ProductCategoryFilter{
		Filter filter(ProductCategoryProperties properties);
	}
	
	@FunctionalInterface
	public interface SignatureFilter{
		Filter filter(SignatureProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryMediaFilter{
		Filter filter(RegistryMediaProperties properties);
	}
	
	@FunctionalInterface
	public interface StockFilter{
		Filter filter(StockProperties properties);
	}
	
	@FunctionalInterface
	public interface WarehouseTransferFilter{
		Filter filter(WarehouseTransferProperties properties);
	}
	
	@FunctionalInterface
	public interface WarehouseTransferDetailFilter{
		Filter filter(WarehouseTransferDetailProperties properties);
	}
	
	@FunctionalInterface
	public interface SeriesFilter{
		Filter filter(SeriesProperties properties);
	}
}
