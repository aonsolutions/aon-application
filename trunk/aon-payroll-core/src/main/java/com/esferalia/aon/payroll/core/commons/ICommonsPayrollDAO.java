package com.esferalia.aon.payroll.core.commons;

import java.util.List;

import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;


public interface ICommonsPayrollDAO {

	List<ITipoBonificacion> getTiposBonificacion() throws PayrollException;
}
