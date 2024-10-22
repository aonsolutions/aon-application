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

class FilterImpl  implements Filter {
	
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
		public FilterImpl  eq(T t) {
			return new FilterImpl (field.eq(t));
		}

		@Override
		public FilterImpl  ne(T t) {
			return new FilterImpl (field.ne(t));
		}

		@Override
		public FilterImpl  le(T t) {
			return new FilterImpl (field.le(t));
		}

		@Override
		public FilterImpl  lt(T t) {
			return new FilterImpl (field.lt(t));
		}

		@Override
		public FilterImpl  gt(T t) {
			return new FilterImpl (field.gt(t));
		}

		@Override
		public FilterImpl  ge(T t) {
			return new FilterImpl (field.ge(t));
		}
		
		@Override
		public Filter in(T[] t) {
			return new FilterImpl (field.in(t));
		}

		@Override
		public FilterImpl  isNull() {
			return new FilterImpl (field.isNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterImpl (field.isNotNull());
		}

		@Override
		public Filter like(T t) {
			if (t instanceof String) {
				return new FilterImpl (field.like( (String) t));
			} else if ( t instanceof byte[]) {
				return new FilterImpl (field.like(new String((byte[])t)));
			} else if ( t instanceof Integer) {
				return new FilterImpl (field.like("%"+ AonNumberUtils.toString((Integer) t) +"%"));
			} else if ( t instanceof Double) {
				return new FilterImpl (field.like("%"+ AonNumberUtils.toString((Double) t) +"%"));
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
		    return new FilterImpl ( DSL.condition("match({0}) against({1} IN "+ mode +" MODE)", name, val));
		}

		@Override
		public Filter between(T min, T max) {
			return new FilterImpl (field.between(min, max));
		}

		@Override
		public Filter notIn(T[] t) {
			return new FilterImpl (field.notIn(t));
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
		public FilterImpl  eq(Date date) {
			return new FilterImpl (field.eq(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterImpl  ne(Date date) {
			return new FilterImpl (field.ne(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterImpl  le(Date date) {
			return new FilterImpl (field.le(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterImpl  lt(Date date) {
			return new FilterImpl (field.lt(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterImpl  gt(Date date) {
			return new FilterImpl (field.gt(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterImpl  ge(Date date) {
			return new FilterImpl (field.ge(new java.sql.Date(date.getTime())));
		}

		@Override
		public Filter in(Date[] t) {
			return new FilterImpl (field.in( Arrays.asList(t)));
		}

		@Override
		public FilterImpl  isNull() {
			return new FilterImpl (field.isNotNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterImpl (field.isNotNull());
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
			return new FilterImpl (field.between(new java.sql.Date(min.getTime()), new java.sql.Date(max.getTime())));
		}

		@Override
		public Filter notIn(Date[] t) {
			return new FilterImpl (field.notIn(Arrays.asList(t)));
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
		public FilterImpl  eq(Timestamp date) {
			return new FilterImpl (field.eq(date));
		}

		@Override
		public FilterImpl  ne(Timestamp date) {
			return new FilterImpl (field.ne(date));
		}

		@Override
		public FilterImpl  le(Timestamp date) {
			return new FilterImpl (field.le(date));
		}

		@Override
		public FilterImpl  lt(Timestamp date) {
			return new FilterImpl (field.lt(date));
		}

		@Override
		public FilterImpl  gt(Timestamp date) {
			return new FilterImpl (field.gt(date));
		}

		@Override
		public FilterImpl  ge(Timestamp date) {
			return new FilterImpl (field.ge(date));
		}

		@Override
		public Filter in(Timestamp[] t) {
			return new FilterImpl (field.in( Arrays.asList(t)));
		}

		@Override
		public FilterImpl  isNull() {
			return new FilterImpl (field.isNotNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterImpl (field.isNotNull());
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
			return new FilterImpl (field.between(min, max));
		}

		@Override
		public Filter notIn(Timestamp[] t) {
			return new FilterImpl (field.notIn(Arrays.asList(t)));
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
		public FilterImpl  eq(Boolean t) {
			return new FilterImpl ( t ? field.eq(value) : field.ne(value));
		}

		@Override
		public FilterImpl  ne(Boolean t) {
			return new FilterImpl ( t ? field.ne(value) : field.ne(value));
		}

		@Override
		public FilterImpl  le(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  lt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  gt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  ge(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter in(Boolean[] t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  isNull() {
			return new FilterImpl ( field.isNull());
		}

		@Override
		public FilterImpl  isNotNull() {
			return new FilterImpl ( field.isNotNull());
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
		public FilterImpl  eq(Optional<Boolean> t) {
			return t.map( v -> eq(v)).orElse(isNull());
		}
		
		@Override
		public FilterImpl  eq(Boolean t) {
			return new FilterImpl ( t ? field.isNull() : field.isNotNull());
		}

		@Override
		public FilterImpl  ne(Boolean t) {
			return new FilterImpl ( t ? field.isNotNull() : field.isNull());
		}

		@Override
		public FilterImpl  le(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  lt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  gt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  ge(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter in(Boolean[] t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterImpl  isNull() {
			return new FilterImpl ( field.isNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterImpl ( field.isNotNull());
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
	
	FilterImpl (Condition condition) {
		this.condition = condition;
	}

	protected Condition getCondition() {
		return condition;
	}

	@Override
	public Filter or(Filter filter) {
		return (filter == null)?this:new FilterImpl (condition.or(((FilterImpl )filter).condition));	
	}

	@Override
	public Filter and(Filter filter) {
		return (filter == null)?this:new FilterImpl (condition.and(((FilterImpl )filter).condition));
	}
	
	@Override
	public Filter not(Filter filter) {
		return (filter == null)?this:new FilterImpl (condition.not());
	}
	
	Select<Record> build(SelectJoinStep<Record> select) {
		return select.where(getCondition());
	}
	
	
}
