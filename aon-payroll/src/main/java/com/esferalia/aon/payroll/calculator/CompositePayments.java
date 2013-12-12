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

import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;

public class CompositePayments extends CompositeCollection<IContractPayment> {

	public CompositePayments(Collection<IContractPayment>... payments) {
		super(payments);
	}
	
	@Override
	public Iterator<IContractPayment> iterator() {
		return super.iterator(); //return new PaymentsIterator(super.iterator());
	}

	private static class PaymentsIterator implements Iterator<IContractPayment> {
		private Iterator<IContractPayment> next;

		private Iterator<IContractPayment> iterator;
		private Map<String, Map<Integer, List<Period>>> processed;

		public PaymentsIterator(Iterator<IContractPayment> iterator) {
			this.iterator = iterator;
			this.next = EmptyIterator.EMPTY_ITERATOR;
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
		public IContractPayment next() {
			return next.next();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		private Iterator<IContractPayment> nextImpl() {
			while (iterator.hasNext()) {
				IContractPayment payment = iterator.next();
				Iterator<IContractPayment> next = visit(payment);
				if (next.hasNext()) {
					return next;
				}
			}
			return EmptyIterator.EMPTY_ITERATOR;
		}

		private Iterator<IContractPayment> visit(IContractPayment payment) {
			String name = payment.getName();
			if (name == null) {
				return iterator(payment);
			}


			Period period = new Period(payment.getStartDate(),
					payment.getEndDate());

			ExpressionScope scope = payment.getScope();
			List<Period> periods = getProcessed(name, scope);

			if (periods == null) {
				periods = new LinkedList<Period>();
				periods.add(period);
				addProcessed(name, scope, periods);

				return iterator(payment);
			}

			List<Period> diffs = Period.sub(period, periods);
			addProcessed(name, scope, diffs);

			return new PeriodsContractPaymentIterator(payment, diffs.iterator());

		}

		private List<Period> getProcessed(String name, ExpressionScope scope) {

			Map<Integer, List<Period>> levelPeriods = processed.get(name);
			if (levelPeriods == null)
				return null;

			int level = getScopeLevel(scope);
			List<Period> processed = new LinkedList<Period>();
			for (Entry<Integer, List<Period>> entry : levelPeriods.entrySet()) {
				if (entry.getKey() != level) {
					processed.addAll(entry.getValue());
				}// end-if : Payments at same level can't hide themselves.
			}

			return processed;
		}

		private void addProcessed(String name, ExpressionScope scope,
				List<Period> periods) {
			int level = getScopeLevel(scope);
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

		private static class EmptyIterator implements
				Iterator<IContractPayment> {
			static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

			public boolean hasNext() {
				return false;
			}

			public IContractPayment next() {
				throw new NoSuchElementException();
			}

			public void remove() {
				throw new IllegalStateException();
			}
		}


		private static Iterator<IContractPayment> iterator(
				IContractPayment payment) {
			return Collections.nCopies(1, payment).iterator();
		}

		private static class PeriodsContractPaymentIterator extends
				DelegateContractPayment implements Iterator<IContractPayment> {

			private Period nextPeriod;
			private Iterator<Period> periodsIt;

			public PeriodsContractPaymentIterator(IContractPayment payment,
					Iterator<Period> periodsIt) {
				super(payment);
				this.periodsIt = periodsIt;
			}

			@Override
			public boolean hasNext() {
				return periodsIt.hasNext();
			}

			@Override
			public IContractPayment next() {
				nextPeriod = periodsIt.next();
				return this;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Date getEndDate() {
				return nextPeriod.getEnd();
			}

			@Override
			public Date getStartDate() {
				return nextPeriod.getStart();
			}
		}
	}

	private static int getScopeLevel(ExpressionScope scope) {
		switch (scope) {
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

}
