package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.Scope.SCOPE;
import static com.esferalia.aon.jooq.tables.User.USER;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSelectStep;
import org.jooq.SelectWithTiesAfterOffsetStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.AONContext;
import net.aonsolutions.occam.api.Filter.Property;
import net.aonsolutions.occam.api.config.Scope;
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.filter.UserFacade.UserBuilder;
import net.aonsolutions.occam.api.filter.UserFacade.UserBuilderFactory;
import net.aonsolutions.occam.api.filter.UserFacade.UserFilter;
import net.aonsolutions.occam.api.filter.UserFacade.UserFilters;
import net.aonsolutions.occam.dao.ScopeDAO.ScopeFiller;

public class SecurityDAO {
	
		
	@SuppressWarnings("rawtypes")
	static final Field[] USER_BASIC_FIELDS = new Field[]{
		USER.ID,USER.DOMAIN,USER.NAME,USER.LOGIN,USER.ACTIVE
	};

	private SecurityDAO() {
		
	}
	
	private static final UserFilterDAO USER_FILTERS = new UserFilterDAO();
	private static class UserFilterDAO implements UserFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(USER.ID);}
		@Override public Property<Integer> withDomain() {return new PropertyDAO<>(USER.DOMAIN);}
		@Override public Property<String> withName() {return new PropertyDAO<>(USER.NAME);}
		@Override public Property<String> withLogin() {return new PropertyDAO<>(USER.LOGIN);}
		@Override public Property<Byte> withActive() {return new PropertyDAO<>(USER.ACTIVE);}
	}
	
	public static Optional<User> getUser(AONContext ctx, UserFilter filter){
		return getUser(ctx, filter, b -> b); 
	}
	
	public static Optional<User> getUser(AONContext ctx, UserFilter filter, UserBuilderFactory factory){
		return getUserStream(ctx, filter, factory).findFirst(); 
	}

	public static Stream<User> getUserStream(AONContext ctx, UserFilter filter){
		return getUserStream(ctx, filter, b -> b); 
	}

	public static Stream<User> getUserStream(AONContext ctx, UserFilter filter, UserBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create(new UserSelectBuilderDAO(ctx,filter)).build();
	}

	private static class UserSelectBuilderDAO implements UserBuilder<Stream<User>> {
		
		protected SelectSelectStep<Record> select;
		protected SelectJoinStep<Record> from;
		protected SelectConditionStep<Record> where;
		protected SelectWithTiesAfterOffsetStep<Record> limit;
		
		public UserSelectBuilderDAO( AONContext ctx, UserFilter filter ) {
			this.select = ctx.getDslContext().select(USER_BASIC_FIELDS);
			this.from = select.from(USER);
			this.where(filter);
		}
		
		@Override
		public Stream<User> build( ) {
			return ((limit == null)?this.where:this.limit)
				.fetch()
				.stream()
				.map(new UserFiller());
		}
		
		private UserSelectBuilderDAO where(UserFilter filter) {
			if ( filter.filter(USER_FILTERS) instanceof FilterDAO filterDAO) {
				this.where = this.from.where(  filterDAO.getCondition() );
				return this;
			}
			throw new IllegalArgumentException("Filter can not be null.");
		}

		@Override
		public UserSelectBuilderDAO limit(int offest, int rows) {
			this.limit = this.where.limit(offest, rows);
			return this;
		}
		
		@Override
		public UserSelectBuilderDAO full() {
			return this;
		}

	}

	public static class UserFiller  implements Function<Record,User> {
		
		public User apply(Record r) {
			return map(r, User::new);
		}
		
		User map(Record r, Supplier<User> supplier) {
			return supplier.get()
				.setId(FillerUtils.getValue(r,USER.ID))
				.setDomain(FillerUtils.getValue(r,USER.DOMAIN))
				.setName(FillerUtils.getValue(r,USER.NAME))
				.setLogin(FillerUtils.getValue(r,USER.LOGIN))
				.setActive(FillerUtils.getBoolean(r, USER.ACTIVE))
				.setDirty(false)
				;	
		}
	}
	
	
	public static Condition getUserScopesCondition(AONContext ctx, Field<Integer> field) {
		return getUserScopesCondition(ctx, field, ctx.getUser());
	}
	public static Condition getUserScopesCondition(AONContext ctx, Field<Integer> field, String userLogin ) {
		return getUserScopesCondition(ctx, field, getUser(ctx, p -> p.withLogin().eq(userLogin))
				.orElseThrow( () -> new IllegalAccessError(AonError.USER_NOT_FOUND.getMessage())));
	}
	public static Condition getUserScopesCondition(AONContext ctx, Field<Integer> field, Integer userId) {
		return getUserScopesCondition(ctx, field, getUser(ctx, p -> p.withId().eq(userId) )
				.orElseThrow( () -> new IllegalAccessError(AonError.USER_NOT_FOUND.getMessage())));
	}
	public static Condition getUserScopesCondition (AONContext ctx, Field<Integer> field, User user) {
		Integer[] ids = getUserScopes(ctx,user)
			.stream()
			.map( Scope::getId )
			.toArray(s -> new Integer[s]);
		return AonCollectionUtils.isEmpty(ids)
				? DSL.trueCondition() 
				: field.isNull().or(field.in(ids));
	}
	public static Collection<Scope> getUserScopes (AONContext ctx, User user) {
		ctx.checkRead();
		if (user == null) throw new IllegalAccessError(AonError.USER_INVALID.getMessage());
		return ctx.getDslContext()
			.select(SCOPE.fields())
			.from(USER_SCOPE)
			.innerJoin(SCOPE).on(SCOPE.ID.eq(USER_SCOPE.SCOPE))
			.where(USER_SCOPE.USER_ID.equal(user.getId()))
			.fetch()
			.stream()
			.map( r -> new ScopeFiller().apply(r) )
			.collect(Collectors.toCollection(LinkedHashSet::new));
	} 
}
