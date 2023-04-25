package net.aonsolutions.occam.api.filter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;

public interface InvoiceFacade extends Serializable{
	
	@FunctionalInterface
	public interface InvoiceFilter extends Serializable {
		Filter filter(InvoiceProperties properties);
	}
	
	public interface InvoiceProperties {
		
		Property<Integer> getIdProperty();
		Property<Integer> getDomainProperty();
		Property<String> getSeriesProperty();
		Property<Integer> getNumberProperty();	
		Property<String> getReferenceCodeProperty();
		Property<Integer> getRegistryProperty();
		Property<String> getRegistryDocumentProperty();
		Property<String> getRegistryNameProperty();
		Property<Byte> getTypeProperty();
		Property<Date> getIssueDateProperty();
		Property<Date> getTaxDateProperty();
		Property<Byte> getConfidentialProperty();
		Property<String> getCreationUserProperty();
		Property<Timestamp> getCreationDateProperty();
		Property<String> getModificationUserProperty();
		Property<Timestamp> getModificationDateProperty();
	}
	
}
