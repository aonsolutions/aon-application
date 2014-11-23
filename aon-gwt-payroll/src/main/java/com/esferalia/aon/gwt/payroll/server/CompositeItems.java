package com.esferalia.aon.gwt.payroll.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Item;
import com.esferalia.aon.payroll.calculator.CompositeCollection;

public class CompositeItems<T extends Item<?>> extends CompositeCollection<T> {

	private class MyPredicate implements Predicate {

		Set<Integer> ids = new HashSet<Integer>();
		Set<Integer> concepts = new HashSet<Integer>();
		Map<Integer, Integer> parent = new HashMap<Integer, Integer>();

		@SuppressWarnings("unchecked")
		@Override
		public boolean evaluate(Object obj) {
			T t = (T) obj;
			Integer conceptId = t.getConceptId();

			if (StringUtils.equals("PARENT()", t.getExpression())) {
				if (conceptId != null){
					parent.put(conceptId, t.getDomain());
				}
				return false;
			}

			if (conceptId != null && parent.containsKey(conceptId)
					&& parent.get(conceptId).equals(t.getDomain())){
				return false;
			}

			if (conceptId != null && !concepts.add(conceptId))
				return false;

			if (!ids.add(t.getId()))
				return false;

			return true;
		}
	}

	public CompositeItems(Collection<T>... collections) {
		super(collections);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator<T> iterator() {
		return new FilterIterator(super.iterator(), new MyPredicate());
	}

}
