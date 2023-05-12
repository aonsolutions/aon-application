package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
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

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.AonCoreException;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Booking;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.api.config.DomainAudit;
import net.aonsolutions.occam.api.constants.AonStatus;
import net.aonsolutions.occam.api.constants.DomainType;
import net.aonsolutions.occam.api.filter.AonFacade.AonFillerBuilder;
import net.aonsolutions.occam.api.filter.DomainFacade.CompositeDomainBuilder;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilder;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainBuilderFactory;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilter;
import net.aonsolutions.occam.api.filter.DomainFacade.DomainFilters;
import net.aonsolutions.occam.dao.ScopeDAO.ScopeFiller;
import net.aonsolutions.occam.dao.SecurityDAO.UserFiller;
import net.aonsolutions.watson.client.Pair;
import net.aonsolutions.watson.client.util.AonStringUtils;

public class DomainDAO {
	
	private DomainDAO() {
		
	}
	private static final com.esferalia.aon.jooq.tables.Domain PARENT_DOMAIN = DOMAIN.as("parent_domain");
	
	private static class DomainMapper extends Pair<Record,Domain> {
		private static final long serialVersionUID = 2371435245238661388L;
		private DomainMapper(Record rec) {
			super(rec, new Domain());
		}
		private Domain getDomain() {
			return getRight();
		}
		private Record getRecord() {
			return getLeft();
		}
	}

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
		@Override public Property<Date> withExpirationDate() {return new PropertyDAO<>(DOMAIN.EXPIRATIONDATE);}
		@Override public Property<String> withLastAccessUser() {return new PropertyDAO<>(DOMAIN.LASTACCESS_USER);}
		@Override public Property<Timestamp> withLastAccessDate() {return new PropertyDAO<>(DOMAIN.LASTACCESS_DATE);}
		@Override public Property<Integer> withAonCustomer() {return new PropertyDAO<>(DOMAIN.AONCUSTOMER);}
		@Override public Property<Byte> withAonStatus() {return new PropertyDAO<>(DOMAIN.AONSTATUS);}
	}
	
	private static class DomainSelectBuilderDAO extends  CompositeDomainBuilder<Stream<Domain>> {
		
		private Stream<Domain> stream;
		private final ResultQuery<Record> query;
		private final FillerBuilder fillerBuilder; 
		
		public DomainSelectBuilderDAO( AONContext ctx, DomainFilter filter ) {
			
			SelectBuilder selectBuilder = new SelectBuilder( ctx );
			FromBuilder fromBuilder = new  FromBuilder( selectBuilder.build() );
			SelectJoinStep<Record> from = fromBuilder.build();
			WhereBuilder whereBuilder = new WhereBuilder(ctx, from,filter);
			LimitBuilder limitBuilder = new  LimitBuilder( whereBuilder.build() );
			fillerBuilder = new  FillerBuilder();
			query =  limitBuilder.build();
		
			addBuilder(selectBuilder);
			addBuilder(fromBuilder);
			addBuilder(whereBuilder);
			addBuilder(limitBuilder);
			addBuilder(fillerBuilder);
			 
		}
		
		@Override 
		public DomainSelectBuilderDAO withUsers() {
			super.withUsers();
			UserFiller userFiller =  new UserFiller();
			this.stream = query
				.fetchGroups( fillerBuilder::build, userFiller::apply)
				.entrySet()
				.stream()
				.map(e -> e.getKey().addUsers( 
					e.getValue()
						.stream()
						.filter(u -> u.getId() != null )
						.collect(Collectors.toCollection(LinkedList::new))));
			return this;
		}
		
		public Stream<Domain> build() {
			return this.stream != null 
				? this.stream 
				: this.query
				.fetch()
				.stream()
				.map(fillerBuilder::build );
		}
		
	}
	
	private static class SelectBuilder implements DomainBuilder<SelectSelectStep<Record>> {
		
		@SuppressWarnings("rawtypes")
		private static final Field[] DOMAIN_BASIC_FIELDS = new Field[]{
			DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION,DOMAIN.TYPE
			,DOMAIN.SCOPE,DOMAIN.ENABLEHEREDITY,DOMAIN.ACTIVE
		};
			
		@SuppressWarnings("rawtypes")
		private static final Field[] DOMAIN_PARENT_BASIC_FIELDS = new Field[]{
			PARENT_DOMAIN.ID,PARENT_DOMAIN.NAME,PARENT_DOMAIN.DESCRIPTION
			,PARENT_DOMAIN.TYPE,PARENT_DOMAIN.ACTIVE
		};

		@SuppressWarnings("rawtypes")
		private static final Field[] DOMAIN_AUDIT_FIELDS = new Field[]{
			DOMAIN.LASTACCESS_USER, DOMAIN.LASTACCESS_DATE,DOMAIN.CREATION_USER
			,DOMAIN.CREATION_DATE,DOMAIN.MODIFICATION_USER,DOMAIN.MODIFICATION_DATE
			
		};
		
		@SuppressWarnings("rawtypes")
		private static final Field[] DOMAIN_BOOKING_FIELDS = new Field[]{
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
		public SelectBuilder full() {
			withParentDomain();
			withBooking();
			withAudit();
			return this;
		}

		@Override
		public SelectBuilder withUsers() {
			this.select = select.select(SecurityDAO.USER_BASIC_FIELDS);
			return this;
		}
		
		@Override
		public SelectSelectStep<Record> build() {
			return select;
		}
	}
	
	private static class FromBuilder implements DomainBuilder<SelectJoinStep<Record>> {
		private SelectJoinStep<Record> from;
		
		public FromBuilder(SelectSelectStep<Record> select ) {
			from = select.from(DOMAIN)
				.leftOuterJoin(SCOPE).on(SCOPE.ID.eq(DOMAIN.SCOPE));
		}
		
		@Override
		public FromBuilder withParentDomain() {
			from = from.leftOuterJoin(PARENT_DOMAIN).on(PARENT_DOMAIN.ID.eq(DOMAIN.PARENT));
			return this;
		}

		@Override
		public FromBuilder withUsers() {
			from = from.leftOuterJoin(USER).on(USER.DOMAIN.eq(DOMAIN.ID));
			return this;
		}
		@Override 
		public FromBuilder full() {
			withParentDomain();
			return this;
		}

		@Override public FromBuilder limit(int offest, int rows) { return this; }
		@Override public FromBuilder withAudit() {return this; }
		@Override public FromBuilder withBooking() {return this; }
		
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
		@Override public WhereBuilder withParentDomain() {return this;}
		@Override public WhereBuilder withUsers() {return this;}
		@Override public WhereBuilder withAudit() {return this; }
		@Override public WhereBuilder withBooking() {return this; }
		@Override public WhereBuilder full() {return this; }
		
		public SelectLimitStep<Record> build() {
			return where;
		}
		
		private Condition getWhere(DomainFilter filter) {
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

		@Override public LimitBuilder withParentDomain() {return this;}
		@Override public LimitBuilder withUsers() {return this;}
		@Override public LimitBuilder withAudit() {return this; }
		@Override public LimitBuilder withBooking() {return this; }
		@Override public LimitBuilder full() {return this; }
		
		public ResultQuery<Record> build() {
			return limit==null?where:limit;
		}
	}

	private static class FillerBuilder implements DomainBuilder<Function<Record, DomainMapper>>,AonFillerBuilder<Domain> {
		private UnaryOperator<DomainMapper> withAudit = t -> t;
		private UnaryOperator<DomainMapper> withParent = t -> t;
		private UnaryOperator<DomainMapper> withBooking = t -> t;
		
		@Override
		public Function<Record, DomainMapper> build() {
			return null;
		}
		
		@Override
		public Domain build(Record rec) {
			
			Function<Record, DomainMapper> f = DomainMapper::new;
			return f.andThen( mapper -> {
					mapper.getDomain()
					.setId(FillerUtils.getValue(mapper.getRecord(),DOMAIN.ID))
					.setName(FillerUtils.getValue(mapper.getRecord(),DOMAIN.NAME))
					.setDescription(FillerUtils.getValue(mapper.getRecord(),DOMAIN.DESCRIPTION))
					.setType( DomainType.safeValueOf( FillerUtils.getValue(mapper.getRecord(),DOMAIN.TYPE)).orElse(null))
					.setScope( FillerUtils.getValue(mapper.getRecord(),SCOPE.ID) == null ? null : new ScopeFiller().apply(mapper.getRecord()))
					.setEnableHeredity(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.ENABLEHEREDITY))
					.setActive(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.ACTIVE))
					;
					return mapper;
				})
				.andThen( withBooking )
				.andThen( withAudit )
				.andThen( withParent )
				.apply(rec)
				.getDomain()
				.setDirty(false)
			;
		}
		
		@Override
		public DomainBuilder<Function<Record, DomainMapper>> withAudit() {
			withAudit = mapper -> {
				mapper.getDomain().setAudit(new DomainAudit()
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
		public DomainBuilder<Function<Record, DomainMapper>> withParentDomain() {
			withParent = mapper -> {
				mapper.getDomain().setParent(
					FillerUtils.getValue(mapper.getRecord(),DOMAIN.PARENT) == null 
					? null 
					: new Domain()
						.setId(FillerUtils.getValue(mapper.getRecord(), PARENT_DOMAIN.PARENT))
						.setName(FillerUtils.getValue(mapper.getRecord(), PARENT_DOMAIN.NAME))
						.setDescription(FillerUtils.getValue(mapper.getRecord(), PARENT_DOMAIN.DESCRIPTION))
						.setType( DomainType.safeValueOf( FillerUtils.getValue(mapper.getRecord(),PARENT_DOMAIN.TYPE)).orElse(null))
						.setActive(FillerUtils.getBoolean(mapper.getRecord(), PARENT_DOMAIN.ACTIVE))
				);
				return mapper;
			};
			return this;
		}
		
		@Override
		public DomainBuilder<Function<Record, DomainMapper>> withBooking() {
			withBooking = mapper -> {
				mapper.getDomain().setBooking(new Booking()
					.setOwner(FillerUtils.getValue(mapper.getRecord(), DOMAIN.OWNER))
					.setExpirationDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.EXPIRATIONDATE))
					.setDomainManagement(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.DOMAINMANAGEMENT))
					.setDisableDomainManagement(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.DISABLEDOMAINMANAGEMENT))
					.setMaxDefinedUsers(FillerUtils.getValue(mapper.getRecord(),DOMAIN.MAXDEFINEDUSERS))
					.setAonCustomer(FillerUtils.getValue(mapper.getRecord(),DOMAIN.AONCUSTOMER))
					.setAonStatus( AonStatus.safeValueOf( FillerUtils.getValue(mapper.getRecord(),DOMAIN.AONSTATUS)).orElse(null)))
				;
				return mapper;
			};
			return this;
		}

		@Override
		public DomainBuilder<Function<Record, DomainMapper>> full() {
			withBooking();
			withParentDomain();
			withAudit();
			return null;
		}

		@Override public DomainBuilder<Function<Record, DomainMapper>> limit(int offset, int rows) { return null; }
		@Override public DomainBuilder<Function<Record, DomainMapper>> withUsers() {return null;}

	}
	
	private static DomainSelectBuilderDAO getBuilder( AONContext ctx, DomainFilter filter ) {
		return new DomainSelectBuilderDAO(ctx,filter);
	}
	
	public static Optional<Domain> get(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst();
	}

	public static Stream<Domain> getStream(AONContext ctx, DomainFilter filter, DomainBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create( getBuilder(ctx,filter)).build();
	}
	
	public static Domain save(AONContext ctx, Domain domain) {
		ctx.checkWrite();
		if (domain == null) throw new AonCoreException(AonError.EMPTY_DOMAIN.getMessage());
		if (domain.isDirty()) { 
			DomainAutoComplete.autoComplete(ctx, domain);
			DomainValidation.validate(ctx, domain);
			return (domain.getId() == null)
				?insert(ctx,domain)
				:update(ctx,domain);
		} else {
			ctx.log().debug(AonError.NOT_DIRTY.format("Domain",domain.getId()));
		}
		return domain;  
	}
	private static Domain insert(AONContext ctx, Domain domain) {
		return domain;
	}
	private static Domain update(AONContext ctx, Domain domain) {
		return domain;
	}
	
	
	private static class DomainAutoComplete {
		
		public static final BiConsumer<AONContext,Domain> COMPLETE_TYPE = (ctx,domain) -> {
			if (domain.getType() == null) {
				ctx.log().debug("\t saving domain: autocomplete type: ENTERPRISE");
				domain.setType(DomainType.ENTERPRISE);
			}
		};
 
		public static final BiConsumer<AONContext,Domain> COMPLETE_ENABLE_HEREDITY = (ctx,domain) -> {
			if (domain.isEnableHeredity() && !domain.getParent().isPresent()) {
				ctx.log().debug("\t saving domain: EnableHeredity to false (no parent)");
				domain.setEnableHeredity(false);
			}
		};

		public static void autoComplete(AONContext ctx, Domain domain) throws AonCoreException {
			COMPLETE_TYPE
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

		
		public static void validate(AONContext ctx, Domain domain) throws AonCoreException{
			VALIDATE_NAME
			.andThen(VALIDATE_DESCRIPTION)
			.andThen(VALIDATE_BOOKING_EMPTY)
			.andThen(VALIDATE_BOOKING_OWNER_EMPTY)
			.accept(ctx, domain);
		}
		
	}
	
}

//| id                      | int(11)      | NO   | PRI | NULL    | auto_increment |
//| name                    | varchar(253) | NO   | UNI | NULL    |                |
//| description             | varchar(128) | NO   |     | NULL    |                |
//| type                    | tinyint(4)   | NO   |     | 0       |                |
//| enableHeredity          | tinyint(1)   | NO   |     | 0       |                |
		//| domainManagement        | tinyint(1)   | NO   |     | 0       |                |
		//| disableDomainManagement | tinyint(1)   | NO   |     | 0       |                |
//| active                  | tinyint(1)   | NO   |     | 1       |                |
		//| owner                   | varchar(256) | NO   |     | NULL    |                |
		//| aonStatus               | tinyint(4)   | NO   |     | 0       |                |
//
