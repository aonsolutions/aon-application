package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Iterator;

import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;

public class CompositeDeductions extends CompositeItems<IContractDeduction> {

	@SuppressWarnings("serial")
	private static class PeriodDeductionsIterator extends PeriodsItemsIterator<DeductionType, IContractDeduction> implements
			IContractDeduction {

		public PeriodDeductionsIterator(IContractDeduction item,
				Iterator<Period> periodsIt) {
			super(item, periodsIt);
		}
		
		// --------------------------------------------------------------------

		@Override
		protected IContractDeduction nextImpl() {
			return this;
		}

		@Override
		public Integer getId() {
			return getItem().getId();
		}

	}

	public CompositeDeductions(Collection<IContractDeduction>... items) {
		super(items);
	}

	// --------------------------------------------------------------------

	@Override
	protected Iterator<IContractDeduction> getIterator4(IContractDeduction item,
			Iterator<Period> periodsIt) {
		return new PeriodDeductionsIterator(item, periodsIt);
	}

}
