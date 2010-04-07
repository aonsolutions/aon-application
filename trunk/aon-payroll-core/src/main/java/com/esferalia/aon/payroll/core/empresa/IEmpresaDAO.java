package com.esferalia.aon.payroll.core.empresa;

import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IActividadCCC;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;

public interface IEmpresaDAO {
	
	void configure();

	IActividadCCC getActividadCCC(IActividad actividad, CuentaCotizacion ccc) throws PayrollException;
}
