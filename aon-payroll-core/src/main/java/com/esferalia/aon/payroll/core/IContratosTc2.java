package com.esferalia.aon.payroll.core;

import java.io.Serializable;

public interface IContratosTc2 extends Serializable{

	String getCdg();
	void setCdg(String cdg);
	
	String getDescripcion();
	void setDescripcion(String descripcion);
	
	String getDescripcionAbreviada();
	void setDescripcionAbreviada(String descripcionAbreviada);
	
	String getCdgAntiguo();
	void setCdgAntiguo(String cdgAntiguo);
	
}
