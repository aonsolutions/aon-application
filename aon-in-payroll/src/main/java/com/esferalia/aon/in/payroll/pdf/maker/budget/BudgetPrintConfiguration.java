package com.esferalia.aon.in.payroll.pdf.maker.budget;

import java.util.Locale;
import java.util.Optional;

import com.esferalia.aon.in.payroll.pdf.api.bean.PrintConfiguration;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.Budget;
import com.esferalia.aon.in.payroll.pdf.maker.budget.bean.Budget.BudgetBuilder;

public class BudgetPrintConfiguration extends PrintConfiguration {

	private Optional<Budget> budget;

	public BudgetPrintConfiguration(Locale language, Budget budget) {
		super(language);
		this.budget = Optional.ofNullable(budget);
	}

	public Budget getBudget() {
		return budget.orElse(new BudgetBuilder().build());
	}

	public void setBudget(Budget budget) {
		this.budget = Optional.ofNullable(budget);
	}
}
