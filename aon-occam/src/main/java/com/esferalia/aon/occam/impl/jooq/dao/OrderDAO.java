package com.esferalia.aon.occam.impl.jooq.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.jooq.Field;
import org.jooq.SortField;
import com.esferalia.aon.occam.api.model.Order;

public class OrderDAO implements Order {

	private static final long serialVersionUID = 1L;
	public Collection<SortField<?>> collection = new ArrayList<>();
	
	public Collection<SortField<?>> getOrder() {
		return this.collection;
	}

	@Override
	public Order and(Order order) {
		this.collection.addAll(((OrderDAO)order).collection);
		return this;
	}
	
	public OrderDAO(Collection<SortField<?>> c) {
		this.collection = c;
	}
	
	public OrderDAO(SortField<?> s) {
		this.collection.add(s);
	}
	
	public int size() {
		return this.collection.size();
	}

	public static class PropertyOrderDAO<T> implements PropertyOrder {
		
		private Field<T> table;

		public PropertyOrderDAO(Field<T> table) {
			this.table = table;
		}

		@Override
		public IOrderBy orderBy() {
			return new OrderBy<T>(table);
		}
	}
	
	public static class OrderBy<T> implements IOrderBy {
		
		private Field<T> t;
		
		OrderBy(Field<T> t){
			this.t = t;
		}

		@Override
		public Order ASC() {
			return new OrderDAO(this.t.asc());
		}

		@Override
		public Order DESC() {
			return new OrderDAO(this.t.desc());
		}
		
	}
	
}
