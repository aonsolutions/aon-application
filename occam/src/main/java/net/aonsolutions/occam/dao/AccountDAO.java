package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Account.ACCOUNT;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.SelectJoinStep;
import org.jooq.SelectLimitStep;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWithTiesAfterOffsetStep;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.accounting.Account;
import net.aonsolutions.occam.api.filter.AccountFacade.AccountBuilder;
import net.aonsolutions.occam.api.filter.AccountFacade.AccountBuilderFactory;
import net.aonsolutions.occam.api.filter.AccountFacade.AccountFilter;
import net.aonsolutions.occam.api.filter.AccountFacade.AccountFilters;
import net.aonsolutions.occam.api.filter.AccountFacade.CompositeAccountBuilder;
import net.aonsolutions.occam.api.filter.AonFacade.AonFillerBuilder;

public class AccountDAO {

	
	private AccountDAO() {

	}
	
	private static final AccountFilterDAO ACCOUNT_FILTER = new AccountFilterDAO();
	private static class AccountFilterDAO implements AccountFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(ACCOUNT.ID);}
		@Override public Property<Integer> withDomain() {return new PropertyDAO<>(ACCOUNT.DOMAIN);}
		@Override public Property<String> withCode() {return new PropertyDAO<>(ACCOUNT.CODE);}
		@Override public Property<String> withDescription() {return new PropertyDAO<>(ACCOUNT.DESCRIPTION);}
		@Override public Property<String> withAlias() {return new PropertyDAO<>(ACCOUNT.ALIAS);}
		@Override public Property<Byte> withActive() {return new PropertyDAO<>(ACCOUNT.ACTIVE);}
	}
	
	private static class AccountSelectBuilderDAO extends  CompositeAccountBuilder<Stream<Account>> {
		
		private final ResultQuery<Record> query;
		private final FillerBuilder fillerBuilder; 
		
		public AccountSelectBuilderDAO( AONContext ctx, AccountFilter filter ) {
			
			SelectBuilder selectBuilder = new SelectBuilder( ctx );
			FromBuilder fromBuilder = new  FromBuilder( selectBuilder.build() );
			SelectJoinStep<Record> from = fromBuilder.build();
			WhereBuilder whereBuilder = new WhereBuilder(from,filter);
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
		public Stream<Account> build() {
			return this.query
				.fetch()
				.stream()
				.map(fillerBuilder::build );
		}
		
	}
	
	private static class SelectBuilder implements AccountBuilder<SelectSelectStep<Record>> {
		
		private SelectSelectStep<Record> select;

		public SelectBuilder(AONContext ctx) {
			this.select = ctx.getDslContext()
					.select( ACCOUNT.fields() );
		}

		@Override
		public SelectSelectStep<Record> build() {
			return select;
		}
		
		@Override public SelectBuilder limit(int offest, int rows) {return this;}
	}

	private static class FromBuilder implements AccountBuilder<SelectJoinStep<Record>> {
		private SelectJoinStep<Record> from;
		
		public FromBuilder(SelectSelectStep<Record> select ) {
			from = select.from(ACCOUNT);
		}
		
		@Override
		public SelectJoinStep<Record> build() {
			return from;
		}
		
		@Override public FromBuilder limit(int offest, int rows) { return this; }
	}

	private static class WhereBuilder implements AccountBuilder<SelectLimitStep<Record>> {
		private SelectLimitStep<Record> where;
		
		public WhereBuilder(SelectJoinStep<Record> from, AccountFilter filter) {
			where = from.where( getWhere(filter) );
		}
		
		@Override
		public SelectLimitStep<Record> build() {
			return where;
		}
		
		private Condition getWhere(AccountFilter filter) {
			if ( filter.filter(ACCOUNT_FILTER) instanceof FilterDAO filterDAO) {
				return filterDAO.getCondition();
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}
		
		@Override public WhereBuilder limit(int offset, int rows) {return this;}
	}

	private static class LimitBuilder implements AccountBuilder<ResultQuery<Record>> {
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
		
		@Override
		public ResultQuery<Record> build() {
			return limit==null?where:limit;
		}
	}

	private static class FillerBuilder implements AccountBuilder<Function<Record, RecordMapper<Account>>>,AonFillerBuilder<Account> {
		
		@Override
		public Function<Record, RecordMapper<Account>> build() {
			return null;
		}
		
		@Override
		public Account build(Record rec) {
			Function<Record, RecordMapper<Account>> f = a -> new RecordMapper<Account>(rec, Account::new );
			return f.andThen( mapper -> {
					mapper.get()
					.setId(FillerUtils.getValue(mapper.getRecord(), ACCOUNT.ID))
					.setDomain(FillerUtils.getValue(mapper.getRecord(), ACCOUNT.DOMAIN))
					.setCode(FillerUtils.getValue(mapper.getRecord(), ACCOUNT.CODE))
					.setDescription(FillerUtils.getValue(mapper.getRecord(), ACCOUNT.DESCRIPTION))
					.setAlias(FillerUtils.getValue(mapper.getRecord(), ACCOUNT.ALIAS))
					.setActive(FillerUtils.getBoolean(mapper.getRecord(), ACCOUNT.ACTIVE));
					return mapper;
				})
				.apply(rec)
				.get()
				.setDirty(false)
			;
		}
		
		@Override public AccountBuilder<Function<Record, RecordMapper<Account>>> limit(int offset, int rows) { return null; }

	}

	private static AccountSelectBuilderDAO getBuilder( AONContext ctx, AccountFilter filter ) {
		return new AccountSelectBuilderDAO(ctx,filter);
	}

	public static Optional<Account> get(AONContext ctx, AccountFilter filter, AccountBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst();
	}

	public static Stream<Account> getStream(AONContext ctx, AccountFilter filter, AccountBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create( getBuilder(ctx,filter)).build();
	}
}




