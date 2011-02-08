package com.esferalia.aon.payroll.core.commons;

import java.util.List;

import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IUsuario;
import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;


public interface ICommonsPayrollDAO {

	void configure();
	List<ITipoBonificacion> getTiposBonificacion(String condition) throws PayrollException;
	IUsuario getUsuarioActivo(String loggedUser) throws PayrollException;
}
