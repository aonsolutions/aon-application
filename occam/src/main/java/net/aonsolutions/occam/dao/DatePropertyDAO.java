package net.aonsolutions.occam.dao;

import java.sql.Date;
import java.util.Arrays;

import org.jooq.Field;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;

class DatePropertyDAO implements Property<Date> {

	private Field<Date> field;
	
	DatePropertyDAO(Field<Date> field) {
		this.field = field;
	}

	@Override
	public FilterDAO eq(Date date) {
		return date==null
			?new FilterDAO(field.isNull())
			:new FilterDAO(field.eq(new Date(date.getTime())));
	}

	@Override
	public FilterDAO ne(Date date) {
		return date==null
			?new FilterDAO(field.isNotNull())
			:new FilterDAO(field.ne(new Date(date.getTime())));
	}

	@Override
	public FilterDAO le(Date date) {
		return new FilterDAO(field.le(new Date(date.getTime())));
	}

	@Override
	public FilterDAO lt(Date date) {
		return new FilterDAO(field.lt(new Date(date.getTime())));
	}

	@Override
	public FilterDAO gt(Date date) {
		return new FilterDAO(field.gt(new Date(date.getTime())));
	}

	@Override
	public FilterDAO ge(Date date) {
		return new FilterDAO(field.ge(new Date(date.getTime())));
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
		return new FilterDAO(field.between(new Date(min.getTime()), new Date(max.getTime())));
	}

	@Override
	public Filter notIn(Date[] t) {
		return new FilterDAO(field.notIn(Arrays.asList(t)));
	}
	
}
