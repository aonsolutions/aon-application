package com.esferalia.aon.salary.payment;

import java.util.List;

import net.aonsolutions.payroll.report.Payment;
import net.aonsolutions.payroll.report.Values;

public class SpecialBonuses extends Values<Payment>{

	public SpecialBonuses(List<Payment> values) {
		super(values);
	}

}
