package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class UnemployementDeduction extends AbstractPercentageDeduction {

	
	
	public UnemployementDeduction(IDeductionsFactoryContext ctx) {
		super(ctx);
	}

	@Override
	public DeductionType getType() {
		return DeductionType.UNEMPLOYMENT;
	}

	@Override
	protected double getPercentage(IDeductionsFactoryContext ctx) {
		return 1.55;
	}

}
