package com.code.aon.ui.project.controller;

import com.code.aon.customer.Customer;
import com.code.aon.ui.form.IController;

public interface ITaskController extends IController {

	public Customer getCustomer();

	public void setCustomer(Customer customer);
}
