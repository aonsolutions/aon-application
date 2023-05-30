package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Company.COMPANY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;

import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWithTiesAfterOffsetStep;
import org.jooq.impl.DSL;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.filter.AonFacade.AonFillerBuilder;
import net.aonsolutions.occam.api.filter.ConfigurationFacade.ConfigurationBuilderFactory;
import net.aonsolutions.occam.api.filter.DomainFacade.CompositeDomainBuilder;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilder;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilderFactory;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilter;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilters;
import net.aonsolutions.occam.dao.FillerDAO.RegistryFiller;
import net.aonsolutions.watson.client.util.AonStringUtils;
import net.aonsolutions.watson.server.AonEnumUtils;
import net.aonsolutions.watson.server.AonObjectUtils;

public class DomainDAO {
	
	private DomainDAO() {
		
	}
	private static final com.esferalia.aon.jooq.tables.Domain PARENT_DOMAIN = DOMAIN.as("parent_domain");
	
	private static final DomainFilterDAO DOMAIN_FILTER = new DomainFilterDAO();
	private static class DomainFilterDAO implements DomainFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(DOMAIN.ID);}
		@Override public Property<String> withName() {return new PropertyDAO<>(DOMAIN.NAME);}
		@Override public Property<String> withDescription() {return new PropertyDAO<>(DOMAIN.DESCRIPTION);}
		@Override public Property<Integer> withParent() {return new PropertyDAO<>(DOMAIN.PARENT);}
		@Override public Property<Byte> withType() {return new PropertyDAO<>(DOMAIN.TYPE);}
		@Override public Property<Integer> withScope() {return new PropertyDAO<>(DOMAIN.SCOPE);}
		@Override public Property<Byte> withEnableHeredity() {return new PropertyDAO<>(DOMAIN.ENABLEHEREDITY);}
		@Override public Property<Byte> withDomainManagement() {return new PropertyDAO<>(DOMAIN.DOMAINMANAGEMENT);}
		@Override public Property<Byte> withDisableDomainManagement() {return new PropertyDAO<>(DOMAIN.DISABLEDOMAINMANAGEMENT);}
		@Override public Property<Integer> withMaxDefinedUsers() {return new PropertyDAO<>(DOMAIN.MAXDEFINEDUSERS);}
		@Override public Property<Byte> withActive() {return new PropertyDAO<>(DOMAIN.ACTIVE);}
		@Override public Property<String> withOwner() {return new PropertyDAO<>(DOMAIN.OWNER);}
		@Override public Property<String> withCreationUser() {return new PropertyDAO<>(DOMAIN.CREATION_USER);}
		@Override public Property<Timestamp> withCreationDate() {return new PropertyDAO<>(DOMAIN.CREATION_DATE);}
		@Override public Property<String> withModificationUser() {return new PropertyDAO<>(DOMAIN.MODIFICATION_USER);}
		@Override public Property<Timestamp> withModificationDate() {return new PropertyDAO<>(DOMAIN.MODIFICATION_DATE);}
		@Override public Property<java.sql.Date> withExpirationDate() {return new PropertyDAO<>(DOMAIN.EXPIRATIONDATE);}
		@Override public Property<String> withLastAccessUser() {return new PropertyDAO<>(DOMAIN.LASTACCESS_USER);}
		@Override public Property<Timestamp> withLastAccessDate() {return new PropertyDAO<>(DOMAIN.LASTACCESS_DATE);}
		@Override public Property<Integer> withAonCustomer() {return new PropertyDAO<>(DOMAIN.AONCUSTOMER);}
		@Override public Property<Byte> withAonStatus() {return new PropertyDAO<>(DOMAIN.AONSTATUS);}
	}
	
	private static class DomainSelectBuilderDAO extends  CompositeDomainBuilder<Stream<Domain>> {
		private final ResultQuery<Record> query;
		private final FillerBuilder fillerBuilder;
		
		public DomainSelectBuilderDAO( final AONContext ctx, DomainFilter filter ) {
			SelectBuilder selectBuilder = new SelectBuilder( ctx );
			FromBuilder fromBuilder = new  FromBuilder( selectBuilder.build() );
			SelectJoinStep<Record> from = fromBuilder.build();
			WhereBuilder whereBuilder = new WhereBuilder(ctx, from,filter);
			LimitBuilder limitBuilder = new  LimitBuilder( whereBuilder.build() );
			fillerBuilder = new  FillerBuilder(ctx);
			query =  limitBuilder.build();
		
			addBuilder(selectBuilder);
			addBuilder(fromBuilder);
			addBuilder(whereBuilder);
			addBuilder(limitBuilder);
			addBuilder(fillerBuilder);
			 
		}
		
		@Override
		public Stream<Domain> build() {
			return this.query
				.fetch()
				.stream()
				.map( fillerBuilder::build );
		}
		
	}
	
	private static class SelectBuilder implements DomainBuilder<SelectSelectStep<Record>> {
		
		private static final Field<?>[] DOMAIN_BASIC_FIELDS = new Field<?>[]{
			DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.TYPE
			,DOMAIN.SCOPE,DOMAIN.ENABLEHEREDITY,DOMAIN.ACTIVE
		};
			
		private static final Field<?>[] DOMAIN_PARENT_BASIC_FIELDS = new Field<?>[]{
			PARENT_DOMAIN.ID,PARENT_DOMAIN.NAME,PARENT_DOMAIN.DESCRIPTION
			,PARENT_DOMAIN.TYPE,PARENT_DOMAIN.ACTIVE
		};

		private static final Field<?>[] DOMAIN_AUDIT_FIELDS = new Field<?>[]{
			DOMAIN.LASTACCESS_USER, DOMAIN.LASTACCESS_DATE,DOMAIN.CREATION_USER
			,DOMAIN.CREATION_DATE,DOMAIN.MODIFICATION_USER,DOMAIN.MODIFICATION_DATE
			
		};

		private static final Field<?>[] DOMAIN_BOOKING_FIELDS = new Field<?>[]{
			DOMAIN.OWNER,DOMAIN.EXPIRATIONDATE,DOMAIN.DOMAINMANAGEMENT,
			DOMAIN.DISABLEDOMAINMANAGEMENT,DOMAIN.MAXDEFINEDUSERS,
			DOMAIN.AONCUSTOMER,DOMAIN.AONSTATUS
		};

		private SelectSelectStep<Record> select;

		public SelectBuilder(AONContext ctx) {
			this.select = ctx.getDslContext()
					.select( DOMAIN_BASIC_FIELDS )
					.select( SCOPE.fields() );
		}

		@Override
		public SelectBuilder limit(int offest, int rows) {return this;}

		@Override
		public DomainBuilder<SelectSelectStep<Record>> withCompany() {
			this.select = select.select( REGISTRY.fields() );
			return this;
		}
		@Override
		public SelectBuilder withParentDomain() {
			this.select = select.select( DOMAIN_PARENT_BASIC_FIELDS );
			return this;
		}

		@Override
		public SelectBuilder withAudit() {
			this.select = select.select( DOMAIN_AUDIT_FIELDS );
			return this;
		}

		@Override
		public SelectBuilder withBooking() {
			this.select = select.select(DOMAIN_BOOKING_FIELDS);
			return this;
		}

		@Override
		public SelectSelectStep<Record> build() {
			return select;
		}
		
		@Override public SelectBuilder withUsers() { return this;}
		@Override public SelectBuilder withConfiguration(ConfigurationBuilderFactory factory) { return this; }
	}
	
	private static class FromBuilder implements DomainBuilder<SelectJoinStep<Record>> {
		private SelectJoinStep<Record> from;
		
		public FromBuilder(SelectSelectStep<Record> select ) {
			from = select
				.from(DOMAIN)
				.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(DOMAIN.SCOPE));
		}
		@Override
		public DomainBuilder<SelectJoinStep<Record>> withCompany() {
			from = from
				.innerJoin(COMPANY).on(COMPANY.DOMAIN.eq(DOMAIN.ID))
				.innerJoin(REGISTRY).on(REGISTRY.ID.eq(COMPANY.REGISTRY));
			return this;
		}
		@Override
		public FromBuilder withParentDomain() {
			from = from.leftOuterJoin(PARENT_DOMAIN).on(PARENT_DOMAIN.ID.eq(DOMAIN.PARENT));
			return this;
		}

		
		@Override public FromBuilder limit(int offest, int rows) { return this; }
		@Override public FromBuilder withUsers() { return this;}
		@Override public FromBuilder withAudit() {return this; }
		@Override public FromBuilder withBooking() {return this; }
		@Override public FromBuilder withConfiguration(ConfigurationBuilderFactory factory) { return this; }

		
		@Override
		public SelectJoinStep<Record> build() {
			return from;
		}
	}
	
	private static class WhereBuilder implements DomainBuilder<SelectLimitStep<Record>> {
		private SelectLimitStep<Record> where;
		
		public WhereBuilder(AONContext ctx, SelectJoinStep<Record> from, DomainFilter filter) {
			where = from.where( SecurityDAO.getUserScopesCondition(ctx, DOMAIN.SCOPE) )
						.and( getWhere(filter) );
		}
		
		@Override public WhereBuilder limit(int offset, int rows) {return this;}
		@Override public WhereBuilder withAudit() {return this; }
		@Override public WhereBuilder withBooking() {return this; }
		@Override public WhereBuilder withConfiguration(ConfigurationBuilderFactory factory) { return this; }
		@Override public WhereBuilder withCompany() {return this;}
		@Override public WhereBuilder withParentDomain() {return this;}
		@Override public WhereBuilder withUsers() {return this;}
		
		@Override
		public SelectLimitStep<Record> build() {
			return where;
		}
		
		private Condition getWhere(DomainFilter filter) {
			if ( filter == null) return DSL.trueCondition();
			if ( filter.filter(DOMAIN_FILTER) instanceof FilterDAO filterDAO) {
				return filterDAO.getCondition();
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}
	}

	private static class LimitBuilder implements DomainBuilder<ResultQuery<Record>> {
		private SelectLimitStep<Record> where;
		private SelectWithTiesAfterOffsetStep<Record> limit;
		
		public LimitBuilder(SelectLimitStep<Record> where) {
			this.where = where;
		}
		
		@Override 
		public LimitBuilder limit(int offset, int rows) {
			limit = where.limit(offset,rows);
			return this;
		}

		@Override public LimitBuilder withAudit() {return this; }
		@Override public LimitBuilder withBooking() {return this; }
		@Override public LimitBuilder withCompany() {return this;}
		@Override public LimitBuilder withConfiguration(ConfigurationBuilderFactory factory) { return this; }
		@Override public LimitBuilder withParentDomain() {return this;}
		@Override public LimitBuilder withUsers() {return this;}
		
		@Override
		public ResultQuery<Record> build() {
			return limit==null?where:limit;
		}
	}

	private static class FillerBuilder implements DomainBuilder<Function<Record, RecordMapper<Domain>>>,AonFillerBuilder<Domain> {
		private final AONContext ctx;
		private UnaryOperator<RecordMapper<Domain>> withCompany = t -> t;
		private UnaryOperator<RecordMapper<Domain>> withAudit = t -> t;
		private UnaryOperator<RecordMapper<Domain>> withParent = t -> t;
		private UnaryOperator<RecordMapper<Domain>> withBooking = t -> t;
		private UnaryOperator<RecordMapper<Domain>> withConfiguration = t -> t;
		private UnaryOperator<RecordMapper<Domain>> withUsers = t -> t;
		
		public FillerBuilder(AONContext ctx) {
			this.ctx = ctx;
		}
		
		@Override
		public Function<Record, RecordMapper<Domain>> build() {
			return null;
		}
		
		@Override
		public Domain build(Record rec) {
			Function<Record, RecordMapper<Domain>> f = a -> new RecordMapper<Domain>(
				rec
				,() -> new FillerDAO.DomainFiller().apply(rec, DOMAIN) 
			);
			return f
				.andThen( withCompany )
				.andThen( withBooking )
				.andThen( withAudit )
				.andThen( withParent )
				.andThen( withConfiguration )
				.andThen( withUsers )
				.apply(rec)
				.get()
			;
		}
		
		@Override
		public FillerBuilder withAudit() {
			withAudit = mapper -> {
				mapper.get().setAudit(new DomainAudit()
					.setLastAccessUser(FillerUtils.getValue(mapper.getRecord(), DOMAIN.LASTACCESS_USER))
					.setLastAccessDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.LASTACCESS_DATE))
					.setCreationUser(FillerUtils.getValue(mapper.getRecord(), DOMAIN.CREATION_USER))
					.setCreationDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.CREATION_DATE))
					.setModificationUser(FillerUtils.getValue(mapper.getRecord(), DOMAIN.MODIFICATION_USER))
					.setModificationDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.MODIFICATION_DATE))
				);
				return mapper;
			};
			return this;
		}
		
		@Override
		public FillerBuilder withCompany() {
			withCompany = mapper -> {
				mapper.get().setCompany( new RegistryFiller( ).apply(mapper.getRecord(), REGISTRY ) );
				return mapper;
			};
			return this;
		}
		
		@Override
		public FillerBuilder withParentDomain() {
			withParent = mapper -> {
				if ( FillerUtils.getValue(mapper.getRecord(), DOMAIN.PARENT) != null) {
					mapper.get().setParent(new FillerDAO.DomainFiller().apply(mapper.getRecord(), PARENT_DOMAIN));
				}
				return mapper;
			};
			return this;
		}
		
		@Override
		public FillerBuilder withBooking() {
			withBooking = mapper -> {
				mapper.get().setBooking(new Booking()
					.setOwner(FillerUtils.getValue(mapper.getRecord(), DOMAIN.OWNER))
					.setExpirationDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.EXPIRATIONDATE))
					.setDomainManagement(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.DOMAINMANAGEMENT))
					.setDisableDomainManagement(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.DISABLEDOMAINMANAGEMENT))
					.setMaxDefinedUsers(FillerUtils.getValue(mapper.getRecord(),DOMAIN.MAXDEFINEDUSERS))
					.setAonCustomer(FillerUtils.getValue(mapper.getRecord(),DOMAIN.AONCUSTOMER))
					.setAonStatus( AonStatus.safeValueOf( FillerUtils.getValue(mapper.getRecord(),DOMAIN.AONSTATUS)).orElse(null)))
					.setDirty(false)
				;
				return mapper;
			};
			return this;
		}

		@Override 
		public FillerBuilder withUsers() {
			withUsers = mapper -> {
				mapper.get().setUsers(
					SecurityDAO.getUserStream(ctx, f -> f.withDomain().eq(mapper.get().getId()) )
						.collect(Collectors.toCollection(LinkedList::new)));
				return mapper;
			};
			return this;
		}
		
		
		@Override 
		public FillerBuilder withConfiguration(ConfigurationBuilderFactory factory) {
			withConfiguration = mapper -> {
				mapper.get().setConfiguration(ConfigurationDAO.getConfiguration(ctx, mapper.get(), factory ));
				return mapper;
			};
			return this; 
		}
		
		@Override public FillerBuilder limit(int offset, int rows) { return this; }

	}
	
	private static class DomainAutoComplete {
		
		public static final BiConsumer<AONContext,Domain> LOWCASE_NAME = 
			(ctx,domain) -> domain.setName( AonStringUtils.lowerCase(domain.getName()));
			
		public static final BiConsumer<AONContext,Domain> COMPLETE_TYPE = (ctx,domain) -> {
			if (domain.getType() == null) {
				ctx.log().debug("\t Saving domain: Type to ENTERPRISE");
				domain.setType(DomainType.ENTERPRISE);
			}
		};
 
		public static final BiConsumer<AONContext,Domain> COMPLETE_ENABLE_HEREDITY = (ctx,domain) -> {
			if (domain.isEnableHeredity() && !domain.getParent().isPresent()) {
				ctx.log().debug("\t Saving domain: EnableHeredity to false (no parent)");
				domain.setEnableHeredity(false);
			}
		};

		public static void autoComplete(AONContext ctx, Domain domain) throws AonCoreException {
			LOWCASE_NAME
				.andThen(COMPLETE_TYPE)
				.andThen(COMPLETE_ENABLE_HEREDITY)
				.accept(ctx, domain);
		}

	}
	
	private static class DomainValidation {
		
		public static final BiConsumer<AONContext,Domain> VALIDATE_NAME = (ctx,domain) -> {
			if (AonStringUtils.isBlank(domain.getName())) 
				throw new AonCoreException(AonError.EMPTY_NAME.getMessage());
			if (AonStringUtils.length(domain.getName()) > DOMAIN.NAME.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "Nombre", DOMAIN.NAME.getDataType().length() ));
		};
		
		public static final BiConsumer<AONContext,Domain> VALIDATE_DESCRIPTION = (ctx,domain) -> {
			if (AonStringUtils.isBlank(domain.getDescription())) 
				throw new AonCoreException(AonError.EMPTY_DESCRIPTION.getMessage());
			if (AonStringUtils.length(domain.getDescription()) > DOMAIN.DESCRIPTION.getDataType().length() )
				throw new AonCoreException(AonError.INVALID_LENGTH.format( "Descripci\u00F3n", DOMAIN.DESCRIPTION.getDataType().length() ));
		};
		
		public static final BiConsumer<AONContext,Domain> VALIDATE_BOOKING_EMPTY = (ctx,domain) -> {
			if (!domain.getBooking().isPresent()) {
				throw new AonCoreException(AonError.DOMAIN_NO_BOOKING_INFO.getMessage());
			}
		};
		public static final BiConsumer<AONContext,Domain> VALIDATE_BOOKING_OWNER_EMPTY = (ctx,domain) -> {
			Booking booking = domain.getBooking().get();
			if (AonStringUtils.isBlank(booking.getOwner())) {
				throw new AonCoreException(AonError.DOMAIN_NO_OWNER.getMessage());	
			} else {
				if (AonStringUtils.length(booking.getOwner()) > DOMAIN.OWNER.getDataType().length() )
					throw new AonCoreException(AonError.INVALID_LENGTH.format( "Creador", DOMAIN.OWNER.getDataType().length() ));
			}
		};

		
		public static void validateUpdate(AONContext ctx, Domain domain) throws AonCoreException{
			VALIDATE_NAME
			.andThen(VALIDATE_DESCRIPTION)
			.accept(ctx, domain);
		}
		
		public static void validateInsert(AONContext ctx, Domain domain) throws AonCoreException{
			validateUpdate(ctx, domain);
			VALIDATE_BOOKING_EMPTY
				.andThen(VALIDATE_BOOKING_OWNER_EMPTY)
				.accept(ctx, domain);
		}
	}

	private static Stream<Domain> getStream(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		return factory.create( new DomainSelectBuilderDAO(ctx, filter) ).build();
	}

	// ************************************* [PUBLIC METHODS]
	public static Optional<Domain> get(AONContext ctx, String domainName){
		return getStream(ctx, f -> f.withName().eq(domainName), b -> b).findFirst();
	}
	public static Optional<Domain> get(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst();
	}

	public static List<Domain> getList(AONContext ctx, String domainName){
		return getList(ctx, f -> f.withName().eq(domainName), b -> b);
	}
	public static List<Domain> getList(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		return getStream(ctx, filter, factory).toList();
	}
	
	public static Domain save(AONContext ctx, Domain domain) {
		ctx.checkWrite();
		if (domain == null) throw new AonCoreException(AonError.SAVE_EMPTY.getMessage());
		if (domain.isDirty()) { 
			DomainAutoComplete.autoComplete(ctx, domain);
			return (domain.getId() == null)
				?insert(ctx,domain)
				:update(ctx,domain);
		} else {
			ctx.log().debug(AonError.NOT_DIRTY.format("Domain",domain.getId()));
		}
		return domain;  
	}
	
	private static Domain insert(AONContext ctx, Domain domain) {
		DomainValidation.validateInsert(ctx, domain);
		DomainAudit audit = domain.getAudit().orElse(new DomainAudit());
		audit.setCreationUser(ctx.getUser());
		audit.setCreationDate(new Timestamp(new Date().getTime()));
		domain.setAudit(audit);
		Integer id = ctx.getDslContext().insertInto(DOMAIN)
			.set(DOMAIN.NAME, domain.getName())
			.set(DOMAIN.DESCRIPTION, domain.getDescription())
			.set(DOMAIN.TYPE, AonEnumUtils.getByte(domain.getType() ))
			.set(DOMAIN.ACTIVE, AonEnumUtils.getByte(domain.isActive() ))
			.set(DOMAIN.SCOPE, AonObjectUtils.ifOptionalPresent(domain.getScope(), Scope::getId))
			.set(DOMAIN.PARENT, AonObjectUtils.ifOptionalPresent(domain.getParent(), Domain::getId))
			.set(DOMAIN.ACTIVE, AonEnumUtils.getByte(domain.isEnableHeredity() ))
			.set(DOMAIN.OWNER, AonObjectUtils.ifOptionalPresent(domain.getBooking(), Booking::getOwner))
			.set(DOMAIN.DOMAINMANAGEMENT
				, AonEnumUtils.getByte( AonObjectUtils.ifOptionalPresent(domain.getBooking(), Booking::isDomainManagement)))
			.set(DOMAIN.DISABLEDOMAINMANAGEMENT
				, AonEnumUtils.getByte( AonObjectUtils.ifOptionalPresent(domain.getBooking(), Booking::isDisableDomainManagement)))
			.set(DOMAIN.MAXDEFINEDUSERS
				,AonObjectUtils.<Booking,Integer>ifOptionalPresent(domain.getBooking(), b -> b.getMaxDefinedUsers().orElse(null)))
			.<Integer>set(DOMAIN.AONCUSTOMER
				,AonObjectUtils.<Booking,Integer>ifOptionalPresent(domain.getBooking(), a -> a.getAonCustomer().orElse(null)))
			.set(DOMAIN.AONSTATUS
				,AonEnumUtils.getByte(AonObjectUtils.ifOptionalPresent(domain.getBooking(), Booking::getAonStatus)))
			.set(DOMAIN.CREATION_USER, audit.getCreationUser().orElse(ctx.getUser()) )
			.set(DOMAIN.CREATION_DATE, new Timestamp(audit.getCreationDate().orElse(new Date()).getTime())) 
			.returning(DOMAIN.ID)
			.fetchOne().getValue(DOMAIN.ID);
		domain.setId(id);
		ctx.log().info("INSERT DOMAIN id: {0} - {1}", domain.getId(), domain.getName());	
		return domain;
	}
	
	private static Domain update(AONContext ctx, Domain domain) {
		DomainValidation.validateUpdate(ctx, domain);
		DomainAudit audit = domain.getAudit().orElse(new DomainAudit());
		audit.setModificationUser(ctx.getUser());
		audit.setModificationDate(new Timestamp(new Date().getTime()));
		domain.setAudit(audit);
		int count = ctx.getDslContext().update(DOMAIN)
			.set(DOMAIN.NAME, domain.getName())
			.set(DOMAIN.DESCRIPTION, domain.getDescription())
			.set(DOMAIN.TYPE, AonEnumUtils.getByte(domain.getType() ))
			.set(DOMAIN.ACTIVE, AonEnumUtils.getByte(domain.isActive() ))
			.set(DOMAIN.SCOPE, AonObjectUtils.ifOptionalPresent(domain.getScope(), Scope::getId))
			.set(DOMAIN.PARENT, AonObjectUtils.ifOptionalPresent(domain.getParent(), Domain::getId))
			.set(DOMAIN.ACTIVE, AonEnumUtils.getByte(domain.isEnableHeredity() ))
			.set(DOMAIN.MODIFICATION_USER, audit.getModificationUser().orElse(ctx.getUser()) )
			.set(DOMAIN.MODIFICATION_DATE, new Timestamp(audit.getModificationDate().orElse(new Date()).getTime())) 
			.where(DOMAIN.ID.eq(domain.getId()))
			.execute();
		ctx.log().debug("UPDATE DOMAIN id: {0}. ({1} rows)", domain.getId(),count);
		return domain;
	}
	
}
