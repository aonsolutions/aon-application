package net.aonsolutions.occam.api.filter;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.stream.Stream;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.ConfigurationBuilderFactory;

public class DomainFacade {
	
	private DomainFacade() {
		
	}

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
		Property<Byte> withEnableHeredity();
		Property<Byte> withDomainManagement();
		Property<Byte> withDisableDomainManagement();
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
	
	public interface DomainBuilder<T> {
		DomainBuilder<T> limit(int offset, int rows);
		DomainBuilder<T> withAudit();
		DomainBuilder<T> withBooking();
		DomainBuilder<T> withCompany();
		default DomainBuilder<T> withConfiguration() {
			return withConfiguration( f -> f.withAccountingConfiguration()); 	
		}
		DomainBuilder<T> withConfiguration(ConfigurationBuilderFactory factory);
		DomainBuilder<T> withParentDomain();
		DomainBuilder<T> withUsers();
		DomainBuilder<T> full();
		T build();
	}
	
	
	public abstract static class CompositeDomainBuilder<T> implements DomainBuilder<T> {
		private LinkedList<DomainBuilder<?>> builders = new LinkedList<>();
		
		public CompositeDomainBuilder<T> addBuilder(DomainBuilder<?> builder) {
			builders.add(builder);
			return this;
		}
		@Override
		public DomainBuilder<T> withCompany() {
			builders.stream().forEach( b -> b.withCompany());
			return this;
		}
		@Override
		public DomainBuilder<T> withParentDomain() {
			builders.stream().forEach( b -> b.withParentDomain());
			return this;
		}
		@Override
		public DomainBuilder<T> withAudit() {
			builders.stream().forEach( b -> b.withAudit());
			return this;
		}
		@Override
		public DomainBuilder<T> withBooking(){
			builders.stream().forEach( b -> b.withBooking());
			return this;
		}
		@Override
		public DomainBuilder<T> withUsers(){
			builders.stream().forEach( b -> b.withUsers());
			return this;
		}
		@Override
		public DomainBuilder<T> withConfiguration(ConfigurationBuilderFactory factory){
			builders.stream().forEach( b -> b.withConfiguration(factory));
			return this;
		}
		@Override
		public DomainBuilder<T> full() {
			builders.stream().forEach( b -> b.full());
			return this;
		}
		@Override
		public DomainBuilder<T> limit(int offset, int rows){
			builders.stream().forEach( b -> b.limit(offset,rows));
			return this;
		}
	}

	@FunctionalInterface
	public interface DomainBuilderFactory {
		public DomainBuilder<Stream<Domain>> create( DomainBuilder<Stream<Domain>> builder );
	}
	
}
