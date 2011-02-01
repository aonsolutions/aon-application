package com.esferalia.aon.payroll.core.empresa;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.enumeration.FileStatus;

public interface IRemesaCertificadoEmpresa extends ITransferObject {
	
	Integer getId();
	void setId(Integer  id);
	
	IEmpresa getEmpresa();
	void setEmpresa(IEmpresa empresa);
	
	Date getFecha();
	void setFecha(Date fecha);
	
	FileStatus getEstado();
	void setEstado(FileStatus generado);
	
	String getHuella();
	void setHuella(String huella);
	
}
