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
import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.salary.expression.Period;

public class CompositePayments<T extends IContractPayment> extends
		CompositeCollection<T> {

	public CompositePayments(Collection<T>... payments) {
		super(payments);
	}

	@Override
	public Iterator<T> iterator() {
		return new PaymentsIterator(super.iterator(), this);
	}

	private static class PaymentsIterator<T extends IContractPayment>
			implements Iterator<IContractPayment> {
		private Iterator<IContractPayment> next;

		private Iterator<T> iterator;
		private CompositePayments<T> payments;
		private Map<String, Map<Integer, List<Period>>> processed;

		public PaymentsIterator(Iterator<T> iterator, CompositePayments<T> payments) {
			this.iterator = iterator;
			this.payments = payments;
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
				T payment = iterator.next();
				Iterator<IContractPayment> next = visit(payment);
				if (next.hasNext()) {
					return next;
				}
			}
			return EmptyIterator.EMPTY_ITERATOR;
		}

		private Iterator<IContractPayment> visit(T payment) {
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

		private static <T extends IContractPayment> Iterator<T> iterator(
				T payment) {
			return Collections.nCopies(1, payment).iterator();
		}

	}
	
	
	
	protected static class PeriodsContractPaymentIterator extends
			DelegateContractPayment implements Iterator<IContractPayment> {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

	protected int getLevel(T payment) {
		switch (payment.getScope()) {
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
	
	protected Iterator<IContractPayment> getIterator4(T payment,
			Iterator<Period> periodsIt) {
		return new PeriodsContractPaymentIterator(payment, periodsIt);
	}

}
