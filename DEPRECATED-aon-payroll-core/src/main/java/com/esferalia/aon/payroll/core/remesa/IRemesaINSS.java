package com.esferalia.aon.payroll.core.remesa;

import java.io.Serializable;
import java.util.Date;

public interface IRemesaINSS extends Serializable{
	
	Integer getId();
	void setId(Integer id);
	
	Date getFecha();
	void setFecha(Date fecha);
	
	Date getHora();
	void setHora(Date hora);

}
