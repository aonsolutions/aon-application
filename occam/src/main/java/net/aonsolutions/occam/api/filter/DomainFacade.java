package net.aonsolutions.occam.api.filter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.filter.AonFacade.AonBuilder;
public interface DomainFacade extends Serializable{

	@FunctionalInterface
	public interface DomainFilter {
		Filter filter(DomainFilters properties);
	}

	public interface DomainFilters {
		Property<Integer> withId();
		Property<String> withName();
		Property<String> withDescription();
		Property<Integer> withParent();
		Property<Byte> withType();
		Property<Integer> withScope();
		Property<String> withSubDomainSuffix();
		Property<Byte> withEnableHeredity();
		Property<Byte> withDomainManagement();
		Property<Byte> withDisableDomainManagement();
		Property<Integer> withMaxDocumentSize();
		Property<Integer> withMaxTotalDocumentSize();
		Property<Integer> withMaxDefinedUsers();
		Property<Byte> withActive();
		Property<String> withOwner();
		Property<Date> withExpirationDate();
		Property<Timestamp> withLastAccessDate();
		Property<String> withLastAccessUser();
		Property<Integer> withAonCustomer();
		Property<Byte> withAonStatus();
		Property<String> withCreationUser();
		Property<Timestamp> withCreationDate();
		Property<String> withModificationUser();
		Property<Timestamp> withModificationDate();
	}
	
	public interface DomainBuilder<T> extends AonBuilder<T> {
		public DomainBuilder<T> limit(int offest, int rows);
		public DomainBuilder<T> withParent();
		public DomainBuilder<T> withAudit();
		public DomainBuilder<T> withAllRow();
		public DomainBuilder<T> full();
		public DomainBuilder<T> withUsers();
	}
	
	
	@FunctionalInterface
	public interface DomainBuilderFactory {
		public DomainBuilder<Stream<Domain>> create( DomainBuilder<Stream<Domain>> builder );
	}
	
}
