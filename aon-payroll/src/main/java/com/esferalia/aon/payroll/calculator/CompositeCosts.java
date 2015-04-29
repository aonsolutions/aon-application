package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Iterator;

import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;
import com.esferalia.aon.salary.expression.Period;

public class CompositeCosts extends CompositeItems<IContractCost> {

	@SuppressWarnings("serial")
	private static class PeriodCostsIterator extends
			PeriodsItemsIterator<DeductionType, IContractCost> implements
			IContractCost {

		public PeriodCostsIterator(IContractCost item,
				Iterator<Period> periodsIt) {
			super(item, periodsIt);
		}
		
		// --------------------------------------------------------------------

		@Override
		protected IContractCost nextImpl() {
			return this;
		}

		@Override
		public Integer getId() {
			return getItem().getId();
		}

	}

	public CompositeCosts(Collection<IContractCost>... items) {
		super(items);
	}

	// --------------------------------------------------------------------

	@Override
	protected Iterator<IContractCost> getIterator4(IContractCost item,
			Iterator<Period> periodsIt) {
		return new PeriodCostsIterator(item, periodsIt);
	}

}
