package com.esferalia.aon.payroll.core.empresa;

import java.util.List;

import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IActividad;
import com.esferalia.aon.payroll.core.IActividadCCC;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;

public interface IEmpresaDAO {
	
	void configure();

	IActividadCCC getActividadCCC(IActividad actividad, CuentaCotizacion ccc) throws PayrollException;

	List<IRemesaCertificadoEmpresa> getRemesaCertificados(
			RemesaCertificadoEmpresaParams params) throws PayrollException;

	List<IRemesaCertificadoEmpresaDetalle> getDetalleRemesaCertificados(
			IRemesaCertificadoEmpresa remesa) throws PayrollException;

	List<IEmpleado> getEmpleados(RemesaCertificadoEmpresaParams params)
			throws PayrollException;

	IRemesaCertificadoEmpresa getNewRemesa(IEmpleado empleado) throws PayrollException;
	IRemesaCertificadoEmpresaDetalle getNewRemesaDetalle(IEmpleado empleado);

	IRemesaCertificadoEmpresa accept(IRemesaCertificadoEmpresa remesa) throws PayrollException;

	void accept(IRemesaCertificadoEmpresaDetalle detalle)
			throws PayrollException;

	String getEmpresaCccEmpleado(IEmpleado empleado) throws PayrollException;

	
}
