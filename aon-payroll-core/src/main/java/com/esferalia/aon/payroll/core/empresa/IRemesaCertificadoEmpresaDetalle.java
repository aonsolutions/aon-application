package com.esferalia.aon.payroll.core.empresa;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpleado;

public interface IRemesaCertificadoEmpresaDetalle extends ITransferObject {

	Integer getId();
	void setId(Integer  id);

	IRemesaCertificadoEmpresa getRemesaCertificado();
	void setRemesaCertificado(IRemesaCertificadoEmpresa remesaCertificado);
	
	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);
	
	Date getFecha_baja();
	void setFecha_baja(Date fechaBaja);
	
}
