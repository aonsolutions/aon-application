package com.code.aon.payroll.auxiliares.convenios.calendar;

import java.util.ArrayList;
import java.util.List;


public class Mes {
	
	 List <Calendario> listaDias;
	 private Integer cdg;
	 private String desc;
	 
	
	public Integer getCdg() {
		return cdg;
	}
	public void setCdg(Integer cdg) {
		this.cdg = cdg;
	}
	public List<Calendario> getlistaDias() {
		return listaDias;
	}
	public void setlistaDias(List<Calendario> lista) {
		this.listaDias = lista;
	}
	
	public void addDia(Calendario d)
		
	{
		this.listaDias.add(d);
	}
	
	public Mes() {
		
		listaDias = new ArrayList <Calendario>();

	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

	 
	 







	
	
}
