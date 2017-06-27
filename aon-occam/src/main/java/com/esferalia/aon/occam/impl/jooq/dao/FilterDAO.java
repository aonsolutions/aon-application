package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.Arrays;
import java.util.Date;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.model.Filter;

public class FilterDAO implements Filter {
	
	public static class PropertyDAO<T> implements Property<T> {
		
		private Field<T> field;
		
		public PropertyDAO(Field<T> field) {
			this.field = field;
		}

		@Override
		public FilterDAO eq(T t) {
			return new FilterDAO(field.eq(t));
		}

		@Override
		public FilterDAO ne(T t) {
			return new FilterDAO(field.ne(t));
		}

		@Override
		public FilterDAO le(T t) {
			return new FilterDAO(field.le(t));
		}

		@Override
		public FilterDAO lt(T t) {
			return new FilterDAO(field.lt(t));
		}

		@Override
		public FilterDAO gt(T t) {
			return new FilterDAO(field.gt(t));
		}

		@Override
		public FilterDAO ge(T t) {
			return new FilterDAO(field.ge(t));
		}
		
		@Override
		public Filter in(T[] t) {
			return new FilterDAO(field.in(t));
		}

		@Override
		public Filter isNull() {
			return new FilterDAO(field.isNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterDAO(field.isNotNull());
		}

		@Override
		public Filter like(T t) {
			if (t instanceof String) {
				return new FilterDAO(field.like( (String) t));
			} else {
				throw new UnsupportedOperationException();				
			}
		}
		
		@Override
		public Filter between(T min, T max) {
			return new FilterDAO(field.between(min, max));
		}

		@Override
		public Filter notIn(T[] t) {
			return new FilterDAO(field.notIn(t));
		}
		
	}
	public static class DateBetweenPropertyDAO implements Property<Date> {

		@Override
		public Filter eq(Date t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter ne(Date t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter le(Date t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter lt(Date t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter gt(Date t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter ge(Date t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter in(Date[] t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter isNull() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter isNotNull() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter like(Date t) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter between(Date min, Date max) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Filter notIn(Date[] t) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	public static class DatePropertyDAO implements Property<Date> {

		private Field<java.sql.Date> field;
		
		public DatePropertyDAO(Field<java.sql.Date> field) {
			this.field = field;
		}

		@Override
		public FilterDAO eq(Date date) {
			return new FilterDAO(field.eq(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterDAO ne(Date date) {
			return new FilterDAO(field.ne(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterDAO le(Date date) {
			return new FilterDAO(field.le(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterDAO lt(Date date) {
			return new FilterDAO(field.lt(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterDAO gt(Date date) {
			return new FilterDAO(field.gt(new java.sql.Date(date.getTime())));
		}

		@Override
		public FilterDAO ge(Date date) {
			return new FilterDAO(field.ge(new java.sql.Date(date.getTime())));
		}

		@Override
		public Filter in(Date[] t) {
			return new FilterDAO(field.in( Arrays.asList(t)));
		}

		@Override
		public Filter isNull() {
			return new FilterDAO(field.isNotNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterDAO(field.isNotNull());
		}
		@Override
		public Filter like(Date date) {
			throw new UnsupportedOperationException();				
		}
		
		@Override
		public Filter between(Date min, Date max) {
			return new FilterDAO(field.between(new java.sql.Date(min.getTime()), new java.sql.Date(max.getTime())));
		}

		@Override
		public Filter notIn(Date[] t) {
			return new FilterDAO(field.notIn(Arrays.asList(t)));
		}
		
	}

	public static class PropertyValueDAO<V> implements Property<Boolean> {
		
		private V value;
		private Field<V> field;
		
		public PropertyValueDAO(Field<V> field, V value) {
			this.value = value;
			this.field = field;
		}

		@Override
		public FilterDAO eq(Boolean t) {
			return new FilterDAO( t ? field.eq(value) : field.ne(value));
		}

		@Override
		public FilterDAO ne(Boolean t) {
			return new FilterDAO( t ? field.ne(value) : field.ne(value));
		}

		@Override
		public FilterDAO le(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterDAO lt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterDAO gt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterDAO ge(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter in(Boolean[] t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter isNull() {
			return new FilterDAO( field.isNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterDAO( field.isNotNull());
		}

		@Override
		public Filter like(Boolean t) {
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

	public static class PropertyNullDAO implements Property<Boolean> {
		
		private Field<?> field;
		
		public PropertyNullDAO(Field<?> field) {
			this.field = field;
		}

		@Override
		public FilterDAO eq(Boolean t) {
			return new FilterDAO( t ? field.isNull() : field.isNotNull());
		}

		@Override
		public FilterDAO ne(Boolean t) {
			return new FilterDAO( t ? field.isNotNull() : field.isNull());
		}

		@Override
		public FilterDAO le(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterDAO lt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterDAO gt(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public FilterDAO ge(Boolean t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter in(Boolean[] t) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Filter isNull() {
			return new FilterDAO( field.isNull());
		}

		@Override
		public Filter isNotNull() {
			return new FilterDAO( field.isNotNull());
		}

		@Override
		public Filter like(Boolean t) {
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
	private Integer perPage;
	private Integer page;

	public FilterDAO(Condition condition) {
		this.condition = condition;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public Filter or(Filter filter) {
		return (filter == null)?this:new FilterDAO(condition.or(((FilterDAO)filter).condition));	
	}

	@Override
	public Filter and(Filter filter) {
		return (filter == null)?this:new FilterDAO(condition.and(((FilterDAO)filter).condition));
	}
	
	@Override
	public Filter not(Filter filter) {
		return (filter == null)?this:new FilterDAO(condition.not());
	}

	@Override
	public Filter page(Integer page) {
		this.page = page;
		return this;
	}

	@Override
	public Filter perPage(Integer perPage) {
		this.perPage = perPage;
		return this;
	}
	
	public Select<Record> build(SelectJoinStep<Record> select) {
		if(perPage != null && page != null) {
			return select.where(getCondition()).limit(perPage)
				.offset(perPage * (page -1));
		} else if(perPage != null ){
			return select.where(getCondition()).limit(perPage);
		}
		return select.where(getCondition());
	}
	
}
