package com.esferalia.aon.payroll.calculator;

import java.util.Collection;
import java.util.Iterator;

import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.expression.Period;

public class CompositeBonus extends CompositeItems<IContractBonus> {

	@SuppressWarnings("serial")
	private static class PeriodBonusIterator extends
			PeriodsItemsIterator<BonusType, IContractBonus> implements
			IContractBonus{

		public PeriodBonusIterator(IContractBonus item,
				Iterator<Period> periodsIt) {
			super(item, periodsIt);
		}
		
		// --------------------------------------------------------------------

		@Override
		protected IContractBonus nextImpl() {
			return this;
		}

		@Override
		public Integer getId() {
			return getItem().getId();
		}

	}

	public CompositeBonus(Collection<IContractBonus>... items) {
		super(items);
	}

	// --------------------------------------------------------------------

	@Override
	protected Iterator<IContractBonus> getIterator4(IContractBonus item,
			Iterator<Period> periodsIt) {
		return new PeriodBonusIterator(item, periodsIt);
	}

}
