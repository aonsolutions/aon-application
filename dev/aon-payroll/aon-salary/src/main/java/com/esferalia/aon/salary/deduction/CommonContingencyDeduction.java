package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class CommonContingencyDeduction extends AbstractPercentageDeduction {
	
	
	
	public CommonContingencyDeduction(IDeductionsFactoryContext ctx) {
		super(ctx);
	}
	
	@Override
	protected double getPercentage(IDeductionsFactoryContext ctx){
		return 4.70;
	}
	
	@Override
	public DeductionType getType() {
		return DeductionType.COMMON_CONTINGENCY;
	}


}
