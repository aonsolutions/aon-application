package com.esferalia.aon.payroll.core.empresa;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.CausaSuspension;

public interface IRemesaCertificadoEmpresaDetalle extends ITransferObject {

	Integer getId();
	void setId(Integer  id);

	IRemesaCertificadoEmpresa getRemesaCertificado();
	void setRemesaCertificado(IRemesaCertificadoEmpresa remesaCertificado);
	
	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);
	
	Date getFechaBaja();
	void setFechaBaja(Date fechaBaja);
	
	CausaSuspension getCausaSuspension();
	void setCausaSuspension(CausaSuspension causaSuspension);
	
}
