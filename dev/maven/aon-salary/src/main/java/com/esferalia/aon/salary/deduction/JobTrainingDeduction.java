package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class JobTrainingDeduction extends AbstractPercentageDeduction {

	public JobTrainingDeduction(IDeductionsFactoryContext ctx) {
		super(ctx);
	}

	@Override
	public DeductionType getType() {
		return DeductionType.JOB_TRAINING;
	}

	@Override
	protected double getPercentage(IDeductionsFactoryContext ctx) {
		return 0.10;
	}

}
