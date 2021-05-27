package com.code.aon.ui.fiscal.controller;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.enumeration.Period;

public interface IFiscalModelController {

	public String editModel(Administration administration, int year,
			Period period) throws ManagerBeanException;

	public String newModel(Administration administration, int year,
			Period period) throws ManagerBeanException;

	public void printModel(Administration administration, int year,
			Period period) throws ManagerBeanException;
}
