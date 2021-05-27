package com.esferalia.aon.payroll.calculator;

import com.esferalia.aon.salary.payment.IPayment;

public interface IHasPayment<T extends IPayment> {

	T getPayment();
}
