package net.aonsolutions.occam.dao;

import org.jooq.Field;

import com.esferalia.aon.watson.util.AonNumberUtils;

import net.aonsolutions.occam.api.Filter;
import net.aonsolutions.occam.api.Filter.Property;

public class PropertyDAO<T> implements Property<T> {

	private Field<T> field;

	public PropertyDAO(Field<T> field) {
		this.field = field;
	}

	@Override
	public FilterDAO eq(T t) {
		return t==null
			?new FilterDAO(field.isNull())
			:new FilterDAO(field.eq(t));
	}

	@Override
	public FilterDAO ne(T t) {
		return t==null
			?new FilterDAO(field.isNotNull())
			:new FilterDAO(field.ne(t));
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
		if (t instanceof String tstring) {
			return new FilterDAO(field.like(tstring));
		} else if (t instanceof Integer tint) {
			return new FilterDAO(field.like("%" + AonNumberUtils.toString(tint) + "%"));
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
