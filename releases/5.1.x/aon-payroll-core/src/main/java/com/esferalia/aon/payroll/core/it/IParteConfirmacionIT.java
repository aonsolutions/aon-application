package com.esferalia.aon.payroll.core.it;

import java.io.Serializable;
import java.util.Date;

public interface IParteConfirmacionIT extends Serializable{

	Serializable getId();
	
	IParteIT getParteIT();
	void setParteIT(IParteIT parteIT);
	
	Integer getNumero();
	void setNumero(Integer numero);
	
	Date getFecha();
	void setFecha(Date fecha);
	
	String getNumeroColegiado();
	void setNumeroColegiado(String numeroColegiado);
	
	String getCias();
	void setCias(String cias);
	
	boolean isProcesado();
	void setProcesado(boolean procesado);
	
}
