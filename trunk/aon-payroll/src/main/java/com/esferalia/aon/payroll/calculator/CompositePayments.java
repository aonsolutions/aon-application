package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.esferalia.aon.payroll.DelegateContractPayment;
import com.esferalia.aon.salary.expression.Period;

public class CompositePayments extends CompositeCollection<IContractPayment> {

	public CompositePayments(Collection<IContractPayment> ...payments) {
		super(payments);
	}
	
	@Override
	public Iterator<IContractPayment> iterator() {
		return new PaymentsIterator(super.iterator());
	}
	
	private static class PaymentsIterator 
		implements Iterator<IContractPayment>
	{
		private Iterator<IContractPayment> next;

		private Map<String, List<Period>> visited;
		private Iterator<IContractPayment> iterator;
		
		
		public PaymentsIterator(Iterator<IContractPayment> iterator) {
			this.iterator = iterator;
			this.visited = new HashMap<String, List<Period>>();
			this.next = EmptyIterator.EMPTY_ITERATOR;
		}
		
		@Override
		public boolean hasNext() {
			if ( !next.hasNext() ) {
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
			while ( iterator.hasNext() ) {
				IContractPayment payment = 
						iterator.next();
				Iterator<IContractPayment> next = 
						visit(payment); 
				if ( next.hasNext() ) {
					return next;
				}
			}
			return EmptyIterator.EMPTY_ITERATOR;
		}
		
		private Iterator<IContractPayment> visit( IContractPayment payment){
			String name = payment.getName();
			if ( name == null ) {
				return iterator(payment);
			}
			
			Period period = new Period(
					payment.getStartDate(), 
					payment.getEndDate());

			List<Period> periods = visited.get(name);
			
			if ( periods == null ) {
				periods= new LinkedList<Period>();
				periods.add(period);
				visited.put(name, periods);
				
				return iterator(payment);
			}
			
			List<Period> diffs = diff ( period, periods ) ;
			periods.addAll(diffs);
			
			return new PeriodsContractPaymentIterator(payment, diffs.iterator());
			
		}
		
	    private static class EmptyIterator implements Iterator<IContractPayment> {
	        static final EmptyIterator EMPTY_ITERATOR
	            = new EmptyIterator();

	        public boolean hasNext() { return false; }
	        public IContractPayment next() { throw new NoSuchElementException(); }
	        public void remove() { throw new IllegalStateException(); }
	    }

		private static List<Period> diff ( Period period, List<Period> periods ) {
			
			if ( periods.isEmpty() ) {
				return Collections.emptyList();
			}
			
			List<Period>  subs = 
					period.sub(periods.get(0));
			
			if ( periods.size() == 1) {
				return subs;
			}
			
			List<Period> diff = 
					new LinkedList<Period>();
			
			List<Period> remain = 
					periods.subList(1, periods.size());
			
			for (Period sub : subs) {
				diff.addAll(diff(sub, remain));
			}

			return diff;
		}

		private static Iterator<IContractPayment> iterator(IContractPayment payment){
			return Collections.nCopies(1, payment).iterator();
		}
		
		private static class PeriodsContractPaymentIterator 
			extends DelegateContractPayment implements Iterator<IContractPayment>{
			
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
	
	
	
}
