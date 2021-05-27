package com.esferalia.aon.salary.payment;

import java.util.List;

import net.aonsolutions.payroll.report.Payment;
import net.aonsolutions.payroll.report.Values;

public class CompensationOrPrepaidExpenses extends Values<Payment>{

	public CompensationOrPrepaidExpenses(List<Payment> values) {
		super(values);
	}

}
