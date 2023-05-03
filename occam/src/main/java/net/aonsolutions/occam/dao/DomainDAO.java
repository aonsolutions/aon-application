package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWithTiesAfterOffsetStep;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilder;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilderFactory;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilter;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilters;

public class DomainDAO {
	
	private DomainDAO() {
		
	}
	
	private static final DomainFilterDAO DOMAIN_FILTER = new DomainFilterDAO();
	private static class DomainFilterDAO implements DomainFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(DOMAIN.ID);}
		@Override public Property<String> withName() {return new PropertyDAO<>(DOMAIN.NAME);}
		@Override public Property<String> withDescription() {return new PropertyDAO<>(DOMAIN.DESCRIPTION);}
		@Override public Property<Integer> withParent() {return new PropertyDAO<>(DOMAIN.PARENT);}
		@Override public Property<Byte> withType() {return new PropertyDAO<>(DOMAIN.TYPE);}
		@Override public Property<Integer> withScope() {return new PropertyDAO<>(DOMAIN.SCOPE);}
		@Override public Property<String> withSubDomainSuffix() {return new PropertyDAO<>(DOMAIN.SUBDOMAINSUFFIX);}
		@Override public Property<Byte> withEnableHeredity() {return new PropertyDAO<>(DOMAIN.ENABLEHEREDITY);}
		@Override public Property<Byte> withDomainManagement() {return new PropertyDAO<>(DOMAIN.DOMAINMANAGEMENT);}
		@Override public Property<Byte> withDisableDomainManagement() {return new PropertyDAO<>(DOMAIN.DISABLEDOMAINMANAGEMENT);}
		@Override public Property<Integer> withMaxDocumentSize() {return new PropertyDAO<>(DOMAIN.MAXDOCUMENTSIZE);}
		@Override public Property<Integer> withMaxTotalDocumentSize() {return new PropertyDAO<>(DOMAIN.MAXTOTALDOCUMENTSIZE);}
		@Override public Property<Integer> withMaxDefinedUsers() {return new PropertyDAO<>(DOMAIN.MAXDEFINEDUSERS);}
		@Override public Property<Byte> withActive() {return new PropertyDAO<>(DOMAIN.ACTIVE);}
		@Override public Property<String> withOwner() {return new PropertyDAO<>(DOMAIN.OWNER);}
		@Override public Property<String> withCreationUser() {return new PropertyDAO<>(DOMAIN.CREATION_USER);}
		@Override public Property<Timestamp> withCreationDate() {return new PropertyDAO<>(DOMAIN.CREATION_DATE);}
		@Override public Property<String> withModificationUser() {return new PropertyDAO<>(DOMAIN.MODIFICATION_USER);}
		@Override public Property<Timestamp> withModificationDate() {return new PropertyDAO<>(DOMAIN.MODIFICATION_DATE);}
		@Override public Property<Date> withExpirationDate() {return new PropertyDAO<>(DOMAIN.EXPIRATIONDATE);}
		@Override public Property<String> withLastAccessUser() {return new PropertyDAO<>(DOMAIN.LASTACCESS_USER);}
		@Override public Property<Timestamp> withLastAccessDate() {return new PropertyDAO<>(DOMAIN.LASTACCESS_DATE);}
		@Override public Property<Integer> withAonCustomer() {return new PropertyDAO<>(DOMAIN.AONCUSTOMER);}
		@Override public Property<Byte> withAonStatus() {return new PropertyDAO<>(DOMAIN.AONSTATUS);}
	}
	
	private static class DomainSelectBuilderDAO implements DomainBuilder<Stream<Domain>> {
		
		private static final Field<?>[] DOMAIN_BASIC_FIELDS = new Field[]{
			DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION
		};
		private static final Field<?>[] DOMAIN_OTHER_FIELDS = new Field[]{
			 DOMAIN.PARENT
			,DOMAIN.TYPE
			,DOMAIN.SCOPE
			,DOMAIN.SUBDOMAINSUFFIX
			,DOMAIN.ENABLEHEREDITY
			,DOMAIN.DOMAINMANAGEMENT
			,DOMAIN.DISABLEDOMAINMANAGEMENT
			,DOMAIN.MAXDOCUMENTSIZE
			,DOMAIN.MAXTOTALDOCUMENTSIZE
			,DOMAIN.MAXDEFINEDUSERS
			,DOMAIN.ACTIVE
			,DOMAIN.OWNER
			,DOMAIN.CREATION_USER
			,DOMAIN.CREATION_DATE
			,DOMAIN.MODIFICATION_USER
			,DOMAIN.MODIFICATION_DATE
			,DOMAIN.EXPIRATIONDATE
			,DOMAIN.LASTACCESS_USER
			,DOMAIN.LASTACCESS_DATE
			,DOMAIN.AONCUSTOMER
			,DOMAIN.AONSTATUS
		};

		protected SelectSelectStep<Record> select;
		protected SelectJoinStep<Record> from;
		protected SelectConditionStep<Record> where;
		protected SelectWithTiesAfterOffsetStep<Record> limit;
		
		public DomainSelectBuilderDAO( AONContext ctx, DomainFilter filter ) {
			this.select = ctx.getDslContext().select(DOMAIN_BASIC_FIELDS);
			this.from = select.from(DOMAIN);
			this.where(filter);
		}
		
		@Override
		public Stream<Domain> build( ) {
			return ((limit == null)?this.where:this.limit)
				.fetch()
				.stream()
				.map(new DomainFiller());
		}
		
		private DomainSelectBuilderDAO where(DomainFilter filter) {
			if ( filter.filter(DOMAIN_FILTER) instanceof FilterDAO filterDAO) {
				this.where = this.from.where(  filterDAO.getCondition() );
				return this;
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}

		@Override
		public DomainSelectBuilderDAO limit(int offest, int rows) {
			this.limit = this.where.limit(offest, rows);
			return this;
		}

		@Override
		public DomainSelectBuilderDAO full() {
			this.select = select.select(DOMAIN_OTHER_FIELDS);
			return this;
		}

	}

	private static class DomainFiller extends Filler<Domain> implements Function<Record,Domain> {
		
		@Override
		public Domain apply(Record r) {
			return map(r, Domain::new);
		}
		
		@Override
		Domain map(Record r, Supplier<Domain> supplier) {
			return supplier.get()
				.setId(getValue(r,DOMAIN.ID))
				.setName(getValue(r,DOMAIN.NAME))
				.setDescription(getValue(r,DOMAIN.DESCRIPTION))
				.setParent(getValue(r,DOMAIN.PARENT))
				.setType( DomainType.safeValueOf( getValue(r,DOMAIN.TYPE)).orElse(null))
				.setScope(getValue(r,DOMAIN.SCOPE))
				.setSubDomainSuffix(getValue(r,DOMAIN.SUBDOMAINSUFFIX))
				.setEnableHeredity(getBoolean(r, DOMAIN.ENABLEHEREDITY))
				.setDomainManagement(getBoolean(r, DOMAIN.DOMAINMANAGEMENT))
				.setDisableDomainManagement(getBoolean(r, DOMAIN.DISABLEDOMAINMANAGEMENT))
				.setMaxDocumentSize(getValue(r, DOMAIN.MAXDOCUMENTSIZE))
				.setMaxTotalDocumentSize(getValue(r, DOMAIN.MAXTOTALDOCUMENTSIZE))
				.setMaxDefinedUsers(getValue(r,DOMAIN.MAXDEFINEDUSERS))
				.setActive(getBoolean(r, DOMAIN.ACTIVE))
				.setOwner(getValue(r, DOMAIN.OWNER))
				.setCreationUser(getValue(r, DOMAIN.CREATION_USER))
				.setCreationDate(getValue(r, DOMAIN.CREATION_DATE))
				.setModificationUser(getValue(r, DOMAIN.MODIFICATION_USER))
				.setModificationDate(getValue(r, DOMAIN.MODIFICATION_DATE))
				.setExpirationDate(getValue(r, DOMAIN.EXPIRATIONDATE))
				.setLastAccessUser(getValue(r, DOMAIN.LASTACCESS_USER))
				.setLastAccessDate(getValue(r, DOMAIN.LASTACCESS_DATE))
				.setAonCustomer(getValue(r,DOMAIN.AONCUSTOMER))
				.setAonStatus( AonStatus.safeValueOf( getValue(r,DOMAIN.AONSTATUS)).orElse(null))
				.setDirty(false)
				;	
		}
	}
	
	private static DomainSelectBuilderDAO getBuilder( AONContext ctx, DomainFilter filter ) {
		return new DomainSelectBuilderDAO(ctx,filter);
	}
	
	public static Optional<Domain> get(AONContext ctx, DomainFilter filter){
		return get(ctx, filter, b -> b); 
	}
	public static Optional<Domain> get(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst();
	}

	public static Stream<Domain> getStream(AONContext ctx, DomainFilter filter){
		return getStream(ctx, filter, b -> b); 
	}
	
	public static Stream<Domain> getStream(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create( getBuilder(ctx,filter)).build();
	}
	
}
