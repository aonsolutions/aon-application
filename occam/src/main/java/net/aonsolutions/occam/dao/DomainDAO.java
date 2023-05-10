package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.Optional;
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

import com.esferalia.aon.watson.util.Pair;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Domain;
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
		
		private static final Field<?>[] DOMAIN_BASIC_FIELDS = new Field[]{
			DOMAIN.ID,DOMAIN.NAME,DOMAIN.DESCRIPTION
		};
			
		private static final Field<?>[] DOMAIN_PARENT_BASIC_FIELDS = new Field[]{
			PARENT_DOMAIN.ID,PARENT_DOMAIN.NAME,PARENT_DOMAIN.DESCRIPTION
		};

		private static final Field<?>[] DOMAIN_AUDIT_FIELDS = new Field[]{
			DOMAIN.CREATION_USER,DOMAIN.CREATION_DATE,DOMAIN.MODIFICATION_USER,DOMAIN.MODIFICATION_DATE
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
		public SelectBuilder withAllRow() {
			this.select = select.select(DOMAIN.fields());
			return this;
		}

		@Override
		public SelectBuilder full() {
			withAllRow();
			withParentDomain();
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
		@Override public FromBuilder withAllRow() {return this; }
		
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
		@Override public WhereBuilder withAllRow() {return this; }
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
		@Override public LimitBuilder withAllRow() {return this; }
		@Override public LimitBuilder full() {return this; }
		
		public ResultQuery<Record> build() {
			return limit==null?where:limit;
		}
	}

	private static class FillerBuilder implements DomainBuilder<Function<Record, DomainMapper>>,AonFillerBuilder<Domain> {
		private UnaryOperator<DomainMapper> withAudit = t -> t;
		private UnaryOperator<DomainMapper> withParent = t -> t;
		private UnaryOperator<DomainMapper> withAllRow = t -> t;
		
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
					.setDescription(FillerUtils.getValue(mapper.getRecord(),DOMAIN.DESCRIPTION));
					return mapper;
				})
				.andThen( withAllRow)
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
				mapper.getDomain()
					.setCreationUser(FillerUtils.getValue(mapper.getRecord(), DOMAIN.CREATION_USER))
					.setCreationDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.CREATION_DATE))
					.setModificationUser(FillerUtils.getValue(mapper.getRecord(), DOMAIN.MODIFICATION_USER))
					.setModificationDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.MODIFICATION_DATE));
				return mapper;
			};
			return this;
		}
		
		@Override
		public DomainBuilder<Function<Record, DomainMapper>> withParentDomain() {
			withParent = mapper -> {
				mapper.getDomain().setParent(new Domain()
					.setId(FillerUtils.getValue(mapper.getRecord(), PARENT_DOMAIN.PARENT))
					.setName(FillerUtils.getValue(mapper.getRecord(), PARENT_DOMAIN.NAME))
					.setDescription(FillerUtils.getValue(mapper.getRecord(), PARENT_DOMAIN.DESCRIPTION)));
				return mapper;
			};
			return this;
		}
		
		@Override
		public DomainBuilder<Function<Record, DomainMapper>> withAllRow() {
			withAllRow = mapper -> {
				mapper.getDomain()
					.setType( DomainType.safeValueOf( FillerUtils.getValue(mapper.getRecord(),DOMAIN.TYPE)).orElse(null))
					.setScope( FillerUtils.getValue(mapper.getRecord(),SCOPE.ID) == null ? null : new ScopeFiller().apply(mapper.getRecord()))
					.setSubDomainSuffix(FillerUtils.getValue(mapper.getRecord(),DOMAIN.SUBDOMAINSUFFIX))
					.setEnableHeredity(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.ENABLEHEREDITY))
					.setDomainManagement(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.DOMAINMANAGEMENT))
					.setDisableDomainManagement(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.DISABLEDOMAINMANAGEMENT))
					.setMaxDocumentSize(FillerUtils.getValue(mapper.getRecord(), DOMAIN.MAXDOCUMENTSIZE))
					.setMaxTotalDocumentSize(FillerUtils.getValue(mapper.getRecord(), DOMAIN.MAXTOTALDOCUMENTSIZE))
					.setMaxDefinedUsers(FillerUtils.getValue(mapper.getRecord(),DOMAIN.MAXDEFINEDUSERS))
					.setActive(FillerUtils.getBoolean(mapper.getRecord(), DOMAIN.ACTIVE))
					.setOwner(FillerUtils.getValue(mapper.getRecord(), DOMAIN.OWNER))
					.setExpirationDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.EXPIRATIONDATE))
					.setLastAccessUser(FillerUtils.getValue(mapper.getRecord(), DOMAIN.LASTACCESS_USER))
					.setLastAccessDate(FillerUtils.getValue(mapper.getRecord(), DOMAIN.LASTACCESS_DATE))
					.setAonCustomer(FillerUtils.getValue(mapper.getRecord(),DOMAIN.AONCUSTOMER))
					.setAonStatus( AonStatus.safeValueOf( FillerUtils.getValue(mapper.getRecord(),DOMAIN.AONSTATUS)).orElse(null))
				;
				return mapper;
			};
			return this;
		}

		@Override
		public DomainBuilder<Function<Record, DomainMapper>> full() {
			withAllRow();
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
