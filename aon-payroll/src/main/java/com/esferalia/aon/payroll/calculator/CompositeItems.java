package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.IResourceable;
import com.esferalia.aon.salary.ISalaryItem;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.IExpression;
import com.esferalia.aon.salary.expression.Period;

public abstract class CompositeItems<T extends ISalaryItem<?> & IHashStartAndEndDate & IExpression>
		extends CompositeCollection<T> {

	public CompositeItems(Collection<T>... items) {
		super(items);
	}

	@Override
	public Iterator<T> iterator() {
		return new ItemsIterator(super.iterator(), this);
	}

	private static class ItemsIterator<T extends ISalaryItem<?> & IHashStartAndEndDate & IExpression>
			implements Iterator<T> {
		private Iterator<T> next;

		private Iterator<T> iterator;
		private CompositeItems<T> payments;
		private Map<String, Map<Integer, List<Period>>> processed;

		public ItemsIterator(Iterator<T> iterator, CompositeItems<T> payments) {
			this.iterator = iterator;
			this.payments = payments;
			this.next = new EmptyIterator<T>();
			this.processed = new HashMap<String, Map<Integer, List<Period>>>();
		}

		@Override
		public boolean hasNext() {
			if (!next.hasNext()) {
				next = nextImpl();
			}
			return next.hasNext();
		}

		@Override
		public T next() {
			return next.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private Iterator<T> nextImpl() {
			while (iterator.hasNext()) {
				T payment = iterator.next();
				Iterator<T> next = visit(payment);
				if (next.hasNext()) {
					return next;
				}
			}
			return new EmptyIterator<T>();
		}

		private Iterator<T> visit(T payment) {
			String name = payment.getName();
			if (name == null) {
				return iterator(payment);
			}

			Period period = new Period(payment.getStartDate(),
					payment.getEndDate());

			int level = payments.getLevel(payment);
			List<Period> periods = getProcessed(name, level);

			if (periods == null) {
				periods = new LinkedList<Period>();
				periods.add(period);
				addProcessed(name, level, periods);

				return iterator(payment);
			}

			List<Period> diffs = Period.sub(period, periods);
			addProcessed(name, level, diffs);

			return payments.getIterator4(payment, diffs.iterator());

		}

		private List<Period> getProcessed(String name, int level) {

			Map<Integer, List<Period>> levelPeriods = processed.get(name);
			if (levelPeriods == null)
				return null;

			List<Period> processed = new LinkedList<Period>();
			for (Entry<Integer, List<Period>> entry : levelPeriods.entrySet()) {
				if (entry.getKey() != level) {
					processed.addAll(entry.getValue());
				}// end-if : Payments at same level can't hide themselves.
			}

			return processed;
		}

		private void addProcessed(String name, int level, List<Period> periods) {
			Map<Integer, List<Period>> levelPeriods = processed.get(name);
			if (levelPeriods == null) {
				levelPeriods = new HashMap<Integer, List<Period>>();
				List<Period> list = new LinkedList<Period>(periods);
				levelPeriods.put(level, list);
				processed.put(name, levelPeriods);

			} else {
				List<Period> list = levelPeriods.get(level);
				if (list == null)
					levelPeriods.put(level, list = new LinkedList<Period>(
							periods));
				else
					list.addAll(periods);
			}
		}

		private static class EmptyIterator<T extends ISalaryItem<?> & IHashStartAndEndDate & IExpression>
				implements Iterator<T> {

			public boolean hasNext() {
				return false;
			}

			public T next() {
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new IllegalStateException();
			}
		}

		private static <T extends ISalaryItem<?> & IHashStartAndEndDate & IExpression> Iterator<T> iterator(
				T payment) {
			return Collections.nCopies(1, payment).iterator();
		}

	}

	protected abstract static class PeriodsItemsIterator<E extends Enum<E> & IResourceable, T extends ISalaryItem<E> & IHashStartAndEndDate & IExpression> 
			implements Iterator<T> , IHashStartAndEndDate, IExpression, ISalaryItem<E> {

		private T item;
		private Period nextPeriod;
		private Iterator<Period> periodsIt;

		public PeriodsItemsIterator(T item,
				Iterator<Period> periodsIt) {
			this.item = item;
			this.periodsIt = periodsIt;
		}

		@Override
		public boolean hasNext() {
			return periodsIt.hasNext();
		}

		@Override
		public T next() {
			nextPeriod = periodsIt.next();
			return nextImpl();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		

		// ----------------------------------------------- IHashStartAndEndDate
		
		@Override
		public Date getEndDate() {
			return nextPeriod.getEnd();
		}

		@Override
		public Date getStartDate() {
			return nextPeriod.getStart();
		}
		
		// -------------------------------------------------------- IExpression
		
		@Override
		public String getName() {
			return item.getName();
		}

		@Override
		public String getExpression() {
			return item.getExpression();
		}
		
		@Override
		public boolean isReadOnly() {
			return item.isReadOnly();
		}

		@Override
		public ExpressionScope getScope() {
			return item.getScope();
		}
		// ----------------------------------------------------- ISalaryItem<E> 
		
		@Override
		public E getType() {
			return item.getType();
		}
		
		@Override
		public double getAmount() {
			return item.getAmount();
		}

		@Override
		public String getDescription() {
			return item.getDescription();
		}
		
		// --------------------------------------------------------------------
		
		protected T getItem(){
			return item;
		}
		

		protected abstract T nextImpl();
		
		
	}

	protected int getLevel(T item) {
		if ( item.getScope() == null )
			return -1;
		
		switch (item.getScope()) {
		case SYSTEM:
			return 0;
		case APPLICATION:
			return 1;
		case AGREEMENT:
			return 2;
		case CONTRACT:
		case SALARY:
			return 3;
		default:
			return -1;

		}
	}

	protected abstract Iterator<T> getIterator4(T item,
			Iterator<Period> periodsIt) ;

}
