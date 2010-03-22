package com.code.aon.ui.customer.report.test;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.customer.Customer;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.customer.controller.CustomerController;

public class CustomerDetailReportTest extends AbsReportPrintTest{

	/*
	<report id="customerDetailReport" description="Customer Detailed report ">
		<template path="/com/code/aon/ui/registry/report/masterRegistryDetailedList.jasper" />
		<bean key="com.code.aon.customer.Customer" />
		<criteria provider="#{customer}"/>
		<params>
			<param id="REPORT_RESOURCE_BUNDLE" value="com.code.aon.ui.registry.i18n.report" />
			<param id="printHeader" value="#{company.isPrintHeader}" />
			<param id="company" value="#{company.obtainCompany}" />
			<param id="title" value="#{customer.getReportTitle}" />
		</params>
	</report>
 */

	private final String TEMPLATE = "/com/code/aon/ui/registry/report/masterRegistryDetailedList.jasper";
	private CustomerController customerController;
	private CompanyController companyController;
	

	public CompanyController getCompanyController() {
		if (companyController == null) {
			try {
				companyController = new CompanyController();
				companyController.setPojo(Company.class.getName());
			} catch (ManagerBeanException e) {
				fail(e.getMessage());
			}
		}
		return companyController;
	}

	public void setCompanyController(CompanyController companyController) {
		this.companyController = companyController;
	}

	public CustomerController getCustomerController() {
		if (customerController == null) {
			customerController = new CustomerController();
			customerController.setPojo(Customer.class.getName());
		}
		return customerController;
	}

	public void setCustomerController(CustomerController customerController) {
		this.customerController = customerController;
	}

	protected Collection<?> getData() {
		try {
			List<ITransferObject> c = getCustomerController().getManagerBean().getList(null);
			return c;
		} catch (Exception e) {
			fail(e.getMessage());
		}
		return null;
	}

	protected InputStream getReportTemplate() {
		InputStream in = CustomerDetailReportTest.class.getResourceAsStream(TEMPLATE );  
		return in;
	}

	@Override
	protected Map<Object, Object> getParameters() {
		Map<Object, Object> parameters = new HashMap<Object, Object>();
		Company company = getCompanyController().obtainCompany();
		parameters.put("company", company);
		parameters.put("printHeader",getCompanyController().isPrintHeader());
		parameters.put("title", "Listado Detallado de clientes");
		Locale locale = new Locale("es_ES");
		ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.ui.registry.i18n.report", locale);		
		parameters.put("REPORT_RESOURCE_BUNDLE",bundle);
		return parameters;
	}
	
}
