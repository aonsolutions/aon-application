package net.aonsolutions.occam.dao;

import static com.esferalia.aon.jooq.tables.User.USER;

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
import net.aonsolutions.occam.api.config.User;
import net.aonsolutions.occam.api.filter.UserFacade.UserBuilder;
import net.aonsolutions.occam.api.filter.UserFacade.UserBuilderFactory;
import net.aonsolutions.occam.api.filter.UserFacade.UserFilter;
import net.aonsolutions.occam.api.filter.UserFacade.UserFilters;

public class UserDAO {
	
	private UserDAO() {
		
	}
	
	private static final UserFilterDAO USER_FILTERS = new UserFilterDAO();
	private static class UserFilterDAO implements UserFilters {
		@Override public Property<Integer> withId() {return new PropertyDAO<>(USER.ID);}
		@Override public Property<Integer> withDomain() {return new PropertyDAO<>(USER.DOMAIN);}
		@Override public Property<String> withName() {return new PropertyDAO<>(USER.NAME);}
		@Override public Property<String> withLogin() {return new PropertyDAO<>(USER.LOGIN);}
		@Override public Property<Byte> withActive() {return new PropertyDAO<>(USER.ACTIVE);}
	}
	
	public static Optional<User> get(AONContext ctx, UserFilter filter){
		return get(ctx, filter, b -> b); 
	}
	
	public static Optional<User> get(AONContext ctx, UserFilter filter, UserBuilderFactory factory){
		return getStream(ctx, filter, factory).findFirst(); 
	}

	public static Stream<User> getStream(AONContext ctx, UserFilter filter){
		return getStream(ctx, filter, b -> b); 
	}

	public static Stream<User> getStream(AONContext ctx, UserFilter filter, UserBuilderFactory factory){
		ctx.checkRead();
		DAOUtils.checkNullFactory(factory);
		DAOUtils.checkNullFilter(filter);
		return factory.create(new UserSelectBuilderDAO(ctx,filter)).build();
	}

	private static class UserSelectBuilderDAO implements UserBuilder<Stream<User>> {
		
		private static final Field<?>[] USER_BASIC_FIELDS = new Field[]{
			USER.ID,USER.DOMAIN,USER.NAME,USER.LOGIN,USER.ACTIVE
		};
		
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

	private static class UserFiller extends Filler<User> implements Function<Record,User> {
		
		public User apply(Record r) {
			return map(r, User::new);
		}
		
		User map(Record r, Supplier<User> supplier) {
			return supplier.get()
				.setId(getValue(r,USER.ID))
				.setDomain(getValue(r,USER.DOMAIN))
				.setName(getValue(r,USER.NAME))
				.setLogin(getValue(r,USER.LOGIN))
				.setActive(getBoolean(r, USER.ACTIVE))
				.setDirty(false)
				;	
		}
	}
	
	
	
}
