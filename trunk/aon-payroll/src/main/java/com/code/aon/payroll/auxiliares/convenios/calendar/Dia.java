package com.code.aon.payroll.auxiliares.convenios.calendar;

import java.util.Date;

import com.code.aon.payroll.enumeration.Tipdia;

public class Dia {

	private Integer cdg;
    private Date fecha;
   // private Boolean festivo;
	private Tipdia tipdia;
	
	
	
	public Integer getCdg() {
		return cdg;
	}
	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Tipdia getTipdia() {
		return tipdia;
	}
	public void setTipdia(Tipdia tipdia) {
		this.tipdia = tipdia;
	}
	/*public Boolean getFestivo() {
		return festivo;
	}
	public void setFestivo(Boolean festivo) {
		this.festivo = festivo;
	}*/

	public Boolean isFestivo() {
		if (this.tipdia.getValue().equals("F")){
		return true;
	}
		else	
			return false;
		
	
	}
	
	public Boolean isLaboral() {
		if (this.tipdia.getValue().equals("L")){
		return true;
	}
		else
			return false;
		
	
	}


	
	
	
}
