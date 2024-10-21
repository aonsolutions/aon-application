package net.aonsolutions.occam.impl.handler;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;

import com.esferalia.aon.watson.util.AonNumberUtils;

import net.aonsolutions.occam.api.model.Filter;

class FilterHandler  implements Filter {
	
	private static final long serialVersionUID = 6914118541297474140L;

	static class PropertyDAO<T> implements Property<T> {
		
		private Field<T> field;
		
		public PropertyDAO(Field<T> field) {
			this.field = field;
		}
		
		@Override
		public Filter eq(Optional<T> t) {
			return t.map( v -> eq(v)).orElse(isNull());
		}
		
		@Override
		public FilterHandler  eq(T t) {
			return new FilterHandler (field.eq(t));
		}

		@Override
		public FilterHandler  ne(T t) {
			return new FilterHandler (field.ne(t));
		}

		@Override
		public FilterHandler  le(T t) {
			return new FilterHandler (field.le(t));
		}

		@Override
		public FilterHandler  lt(T t) {
			return new FilterHandler (field.lt(t));
		}

		@Override
		public FilterHandler  gt(T t) {
			return new FilterHandler (field.gt(t));
		}

		@Override
		public FilterHandler  ge(T t) {
			return new FilterHandler (field.ge(t));
		}
		
		@Override
		public Filter in(T[] t) {
			return new FilterHandler (field.in(t));
		}

		@Override
		public FilterHandler  isNull() {
			return new FilterHandler (field.isNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterHandler (field.isNotNull());
		}

		@Override
		public Filter like(T t) {
			if (t instanceof String) {
				return new FilterHandler (field.like( (String) t));
			} else if ( t instanceof byte[]) {
				return new FilterHandler (field.like(new String((byte[])t)));
			} else if ( t instanceof Integer) {
				return new FilterHandler (field.like("%"+ AonNumberUtils.toString((Integer) t) +"%"));
			} else if ( t instanceof Double) {
				return new FilterHandler (field.like("%"+ AonNumberUtils.toString((Double) t) +"%"));
			} else {
				throw new UnsupportedOperationException();				
			}
		}
		
		@Override
		public Filter match(T t) {
		    Param<T> val = DSL.val(t);
		    String str = t.toString();
		    String mode = str.contains("*") ?  "BOOLEAN" : "NATURAL LANGUAGE";
		    Name name = field.getQualifiedName();
		    return new FilterHandler ( DSL.condition("match({0}) against({1} IN "+ mode +" MODE)", name, val));
		}

		@Override
		public Filter between(T min, T max) {
			return new FilterHandler (field.between(min, max));
		}

		@Override
		public Filter notIn(T[] t) {
			return new FilterHandler (field.notIn(t));
		}
	}

	static class DatePropertyDAO implements Property<Date> {

		private Field<java.sql.Date> field;
		
		public DatePropertyDAO(Field<java.sql.Date> field) {
			this.field = field;
		}

		@Override
		public Filter eq(Optional<Date> date) {
			return date.map( v -> eq(v)).orElse(isNull());
		}
		
		@Override
		public FilterHandler  eq(Date date) {
			return new FilterHandler (field.eq(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterHandler  ne(Date date) {
			return new FilterHandler (field.ne(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterHandler  le(Date date) {
			return new FilterHandler (field.le(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterHandler  lt(Date date) {
			return new FilterHandler (field.lt(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterHandler  gt(Date date) {
			return new FilterHandler (field.gt(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterHandler  ge(Date date) {
			return new FilterHandler (field.ge(new java.sql.Date(date.getTime())));
		}

		@Override
		public Filter in(Date[] t) {
			return new FilterHandler (field.in( Arrays.asList(t)));
		}

		@Override
		public FilterHandler  isNull() {
			return new FilterHandler (field.isNotNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterHandler (field.isNotNull());
		}
		@Override
		public Filter like(Date date) {
			throw new UnsupportedOperationException();				
		}
		
		@Override
		public Filter match(Date date) {
			throw new UnsupportedOperationException();				
		}

		@Override
		public Filter between(Date min, Date max) {
			return new FilterHandler (field.between(new java.sql.Date(min.getTime()), new java.sql.Date(max.getTime())));
		}

		@Override
		public Filter notIn(Date[] t) {
			return new FilterHandler (field.notIn(Arrays.asList(t)));
		}
		
	}

	static class TimestampPropertyDAO implements Property<Timestamp> {

		private Field<java.sql.Timestamp> field;
		
		public TimestampPropertyDAO(Field<java.sql.Timestamp> field) {
			this.field = field;
		}

		@Override
		public Filter eq(Optional<Timestamp> date) {
			return date.map( v -> eq(v)).orElse(isNull());
		}
		
		@Override
		public FilterHandler  eq(Timestamp date) {
			return new FilterHandler (field.eq(date));
		}

		@Override
		public FilterHandler  ne(Timestamp date) {
			return new FilterHandler (field.ne(date));
		}

		@Override
		public FilterHandler  le(Timestamp date) {
			return new FilterHandler (field.le(date));
		}

		@Override
		public FilterHandler  lt(Timestamp date) {
			return new FilterHandler (field.lt(date));
		}

		@Override
		public FilterHandler  gt(Timestamp date) {
			return new FilterHandler (field.gt(date));
		}

		@Override
		public FilterHandler  ge(Timestamp date) {
			return new FilterHandler (field.ge(date));
		}

		@Override
		public Filter in(Timestamp[] t) {
			return new FilterHandler (field.in( Arrays.asList(t)));
		}

		@Override
		public FilterHandler  isNull() {
			return new FilterHandler (field.isNotNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterHandler (field.isNotNull());
		}
		@Override
		public Filter like(Timestamp date) {
			throw new UnsupportedOperationException();				
		}
		
		@Override
		public Filter match(Timestamp date) {
			throw new UnsupportedOperationException();				
		}

		@Override
		public Filter between(Timestamp min, Timestamp max) {
			return new FilterHandler (field.between(min, max));
		}

		@Override
		public Filter notIn(Timestamp[] t) {
			return new FilterHandler (field.notIn(Arrays.asList(t)));
		}
		
	}

	static class PropertyValueDAO<V> implements Property<Boolean> {
		
		private V value;
		private Field<V> field;
		
		public PropertyValueDAO(Field<V> field, V value) {
			this.value = value;
			this.field = field;
		}

		@Override
		public Filter eq(Optional<Boolean> t) {
			return t.map( v -> eq(v)).orElse(isNull());
		}

		@Override
		public FilterHandler  eq(Boolean t) {
			return new FilterHandler ( t ? field.eq(value) : field.ne(value));
		}

		@Override
		public FilterHandler  ne(Boolean t) {
			return new FilterHandler ( t ? field.ne(value) : field.ne(value));
		}

		@Override
		public FilterHandler  le(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  lt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  gt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  ge(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter in(Boolean[] t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  isNull() {
			return new FilterHandler ( field.isNull());
		}

		@Override
		public FilterHandler  isNotNull() {
			return new FilterHandler ( field.isNotNull());
		}

		@Override
		public Filter like(Boolean t) {
			throw new UnsupportedOperationException();				
		}
		
		@Override
		public Filter match(Boolean t) {
			throw new UnsupportedOperationException();				
		}

		@Override
		public Filter between(Boolean min, Boolean max) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter notIn(Boolean[] t) {
			throw new UnsupportedOperationException();
		}
		
	}

	static class PropertyNullDAO implements Property<Boolean> {
		
		private Field<?> field;
		
		public PropertyNullDAO(Field<?> field) {
			this.field = field;
		}
		
		@Override
		public FilterHandler  eq(Optional<Boolean> t) {
			return t.map( v -> eq(v)).orElse(isNull());
		}
		
		@Override
		public FilterHandler  eq(Boolean t) {
			return new FilterHandler ( t ? field.isNull() : field.isNotNull());
		}

		@Override
		public FilterHandler  ne(Boolean t) {
			return new FilterHandler ( t ? field.isNotNull() : field.isNull());
		}

		@Override
		public FilterHandler  le(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  lt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  gt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  ge(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter in(Boolean[] t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterHandler  isNull() {
			return new FilterHandler ( field.isNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterHandler ( field.isNotNull());
		}

		@Override
		public Filter like(Boolean t) {
			throw new UnsupportedOperationException();				
		}
		
		@Override
		public Filter match(Boolean t) {
			throw new UnsupportedOperationException();				
		}

		@Override
		public Filter between(Boolean min, Boolean max) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter notIn(Boolean[] t) {
			throw new UnsupportedOperationException();
		}
	}

	private Condition condition;
	
	public FilterHandler (Condition condition) {
		this.condition = condition;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public Filter or(Filter filter) {
		return (filter == null)?this:new FilterHandler (condition.or(((FilterHandler )filter).condition));	
	}

	@Override
	public Filter and(Filter filter) {
		return (filter == null)?this:new FilterHandler (condition.and(((FilterHandler )filter).condition));
	}
	
	@Override
	public Filter not(Filter filter) {
		return (filter == null)?this:new FilterHandler (condition.not());
	}
	
	Select<Record> build(SelectJoinStep<Record> select) {
		return select.where(getCondition());
	}
	
	
}
