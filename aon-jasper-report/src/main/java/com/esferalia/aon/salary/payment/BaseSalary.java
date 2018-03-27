package com.esferalia.aon.salary.payment;

import java.util.List;

import net.aonsolutions.payroll.report.Payment;
import net.aonsolutions.payroll.report.Values;

public class BaseSalary extends Values<Payment>{

	public BaseSalary(List<Payment> values) {
		super(values);
	}

}
