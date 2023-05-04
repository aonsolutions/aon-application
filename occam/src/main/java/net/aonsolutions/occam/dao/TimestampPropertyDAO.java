package net.aonsolutions.occam.dao;

import java.sql.Timestamp;
import java.util.Arrays;

import org.jooq.Field;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;

public class TimestampPropertyDAO implements Property<Timestamp> {

	private Field<Timestamp> field;
	
	public TimestampPropertyDAO(Field<Timestamp> field) {
		this.field = field;
	}

	@Override
	public FilterDAO eq(Timestamp date) {
		return date==null
			?new FilterDAO(field.isNull())
			:new FilterDAO(field.eq(date));
	}

	@Override
	public FilterDAO ne(Timestamp date) {
		return date==null
			?new FilterDAO(field.isNotNull())
			:new FilterDAO(field.ne(date));
	}

	@Override
	public FilterDAO le(Timestamp date) {
		return new FilterDAO(field.le(date));
	}

	@Override
	public FilterDAO lt(Timestamp date) {
		return new FilterDAO(field.lt(date));
	}

	@Override
	public FilterDAO gt(Timestamp date) {
		return new FilterDAO(field.gt(date));
	}

	@Override
	public FilterDAO ge(Timestamp date) {
		return new FilterDAO(field.ge(date));
	}

	@Override
	public Filter in(Timestamp[] t) {
		return new FilterDAO(field.in( Arrays.asList(t)));
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
	public Filter like(Timestamp date) {
		throw new UnsupportedOperationException();				
	}
	
	@Override
	public Filter between(Timestamp min, Timestamp max) {
		return new FilterDAO(field.between(min, max));
	}

	@Override
	public Filter notIn(Timestamp[] t) {
		return new FilterDAO(field.notIn(Arrays.asList(t)));
	}
	
}
