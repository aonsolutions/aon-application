package com.esferalia.aon.payroll.core.it;

import java.io.Serializable;
import java.util.Date;

public interface IConfirmacionParteIT extends Serializable{

	IParteIT getParteIT();
	void setParteIT();
	
	Integer getNumero();
	void setNumero();
	
	Date getFecha();
	void getFecha(Date fecha);
	
	String getNumeroColegiado();
	void setNumeroColegiado(String numeroColegiado);
	
	String getCias();
	void setCias(String cias);
	
	boolean isProcesado();
	void setProcesado(boolean procesado);
	
}
