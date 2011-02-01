package com.esferalia.aon.salary.deduction;

import com.esferalia.aon.salary.ISalary;

public abstract class AbstractPercentageDeduction extends AbstractDeduction {

	public AbstractPercentageDeduction(IDeductionsFactoryContext ctx) {
		init(ctx);
	}

	private void init(IDeductionsFactoryContext ctx) {
		double percentage = getPercentage(ctx);
		ISalary salary = ctx.getCurrentSalary();
		double commonBase =  salary.getCommonBase();
		setAmount(commonBase * ( percentage / 100 )); ;
		setFunction(String.format("%.2f%%", percentage ) );
	}

	protected abstract double getPercentage(IDeductionsFactoryContext ctx);


}