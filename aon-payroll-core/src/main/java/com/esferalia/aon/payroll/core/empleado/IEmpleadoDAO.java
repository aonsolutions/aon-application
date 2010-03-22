package com.esferalia.aon.payroll.core.empleado;

import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IPersona;

public interface IEmpleadoDAO {

	List<IEmpleado> getEmpleados(EmpleadoParams params) throws PayrollException;
	List<IEmpleado> getEmpleados(EmpleadoParams params, int start, int count) throws PayrollException;
	List<IPersona> getDistinctEmpleados(EmpleadoParams params, int start, int count) throws PayrollException;
	int getCount(EmpleadoParams params) throws PayrollException;
	Criteria getCriteria(EmpleadoParams params) throws PayrollException;
}
