package com.esferalia.aon.payroll.core.empleado;

import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;

public interface IEmpleadoDAO {

	List<?> getEmpleados(EmpleadoParams params) throws PayrollException;
	Criteria getCriteria(EmpleadoParams params) throws PayrollException;
}
