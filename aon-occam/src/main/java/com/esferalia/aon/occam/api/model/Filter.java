package com.esferalia.aon.occam.api.model;

import com.esferalia.aon.occam.api.model.Properties.BrandProperties;
import com.esferalia.aon.occam.api.model.Properties.ContactProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.DeliveryProperties;
import com.esferalia.aon.occam.api.model.Properties.DepartmentProperties;
import com.esferalia.aon.occam.api.model.Properties.DomainProperties;
import com.esferalia.aon.occam.api.model.Properties.FeeProperties;
import com.esferalia.aon.occam.api.model.Properties.GeoZoneProperties;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.MailAccountProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductCategoryProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectCommercialProperties;
import com.esferalia.aon.occam.api.model.Properties.ProjectReservationProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryAddressProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryMediaProperties;
import com.esferalia.aon.occam.api.model.Properties.RegistryNoteProperties;
import com.esferalia.aon.occam.api.model.Properties.SeriesProperties;
import com.esferalia.aon.occam.api.model.Properties.SignatureProperties;
import com.esferalia.aon.occam.api.model.Properties.StockProperties;
import com.esferalia.aon.occam.api.model.Properties.TagProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskCommentProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskEventProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskHolderWorkgroupProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskProperties;
import com.esferalia.aon.occam.api.model.Properties.TaskTagProperties;
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
	public interface ElaborationFilter{
		Filter filter(ElaborationProperties properties);
	}
	
	@FunctionalInterface
	public interface ElaborationDetailFilter{
		Filter filter(ElaborationDetailProperties properties);
	}

	@FunctionalInterface
	public interface ElaborationDetailCompositionFilter{
		Filter filter(ElaborationDetailCompositionProperties properties);
	}
	
	@FunctionalInterface
	public interface FeeFilter{
		Filter filter(FeeProperties properties);
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
	public interface DeliveryFilter{
		Filter filter(DeliveryProperties properties);
	}
	
	@FunctionalInterface
	public interface DeliveryDetailFilter{
		Filter filter(DeliveryDetailProperties properties);
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
	public interface RegistryAddressFilter{
		Filter filter(RegistryAddressProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryMediaFilter{
		Filter filter(RegistryMediaProperties properties);
	}
	
	@FunctionalInterface
	public interface RegistryNoteFilter{
		Filter filter(RegistryNoteProperties properties);
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
	
	@FunctionalInterface
	public interface TaskFilter{
		Filter filter(TaskProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskTagFilter{
		Filter filter(TaskTagProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskCommentFilter{
		Filter filter(TaskCommentProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskEventFilter{
		Filter filter(TaskEventProperties properties);
	}

	@FunctionalInterface
	public interface TaskHolderFilter{
		Filter filter(TaskHolderProperties properties);
	}
	
	@FunctionalInterface
	public interface TaskHolderWorkgroupFilter{
		Filter filter(TaskHolderWorkgroupProperties properties);
	}

	@FunctionalInterface
	public interface GeoZoneFilter{
		Filter filter(GeoZoneProperties properties);
	}
	
	@FunctionalInterface
	public interface ProjectReservationFilter{
		Filter filter(ProjectReservationProperties properties);
	}
	
	@FunctionalInterface
	public interface ProjectCommercialFilter{
		Filter filter(ProjectCommercialProperties properties);
	}
}
