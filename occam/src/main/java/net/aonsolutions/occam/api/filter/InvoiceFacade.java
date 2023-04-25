package net.aonsolutions.occam.api.filter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.filter.AonFacade.AonBuilder;

public interface InvoiceFacade extends Serializable{
	
	@FunctionalInterface
	public interface InvoiceFilter extends Serializable {
		Filter filter(InvoiceFilters properties);
	}
	
	public interface InvoiceFilters {
		Property<Integer> withId();
		Property<Integer> withDomain();
		Property<Byte> withType();
		Property<String> withSeries();
		Property<Integer> withNumber();	
		Property<String> withReferenceCode();
		Property<Date> withIssueDate();
		Property<Date> withTaxDate();
		Property<Byte> withConfidential();
		Property<Integer> withRegistry();
		Property<String> withRegistryDocument();
		Property<Byte> withRegistryDocumentType();
		Property<String> withRegistryDocumentCountry();
		Property<String> withRegistryName();
		Property<Integer> withActivity();
		Property<String> withActivityDescription();
		Property<String> withActivityEpigraph();
		Property<String> withCreationUser();
		Property<Timestamp> withCreationDate();
		Property<String> withModificationUser();
		Property<Timestamp> withModificationDate();
	}
	
	public interface InvoiceBuilder<T> extends AonBuilder<T> {
		public InvoiceBuilder<T> limit(int offest, int rows);
		public InvoiceBuilder<T> full();
	}
	
	
	@FunctionalInterface
	public interface InvoiceBuilderFactory<T> {
		public InvoiceBuilder<T> create( InvoiceBuilder<T> builder );
	}
}
