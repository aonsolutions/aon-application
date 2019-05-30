package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.DateUtils;

@SuppressWarnings("serial")
public class Peculiarities implements Serializable {

	// CLASS PECULIARITY
	/*
	 *	Contingencia comunes (Empleado) =>  PORCENTAJE_CGC
	 *	Desemepleo (Empleado) => PORCENTAJE_DESMPL
	 *	Formacion profesional (Empleado) => PORCENTAJE_FP
	 *	Contingencias comunes (Empresa) => PORCENTAJE_CGC_E
	 *	AT/EP - IT (Empresa) => PORCENTAJE_IT
	 *	AT/EP - IMS (Empresa) => PORCENTAJE_IMS
	 *	FOGASA (Empresa) => PORCENTAJE_FOGASA
	 *	Formacion profesional (Empresa) => PORCENTAJE_FP_E
	 *	Desempleo (Empresa) => PORCENTAJE_DESMPL_E
	 *
	 *	En el valor se guarda el valor del % introducido
	 */
	public class Peculiarity implements Serializable{
		private String name;
		private String value;
		private Boolean checked;
		
		public Peculiarity() {
			super();
		}

		public Peculiarity(String name, String value, Boolean checked) {
			super();
			this.name = name;
			this.value = value;
			this.checked = checked;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Boolean isChecked() {
			return checked;
		}

		public void setChecked(Boolean checked) {
			this.checked = checked;
		}
		
	}
	
	//BEGIN PECULIARITIES CLASS
	private Map<Date, ArrayList<Peculiarity>> peculiarties;
	
	public Peculiarities() {
		super();
		this.peculiarties = new HashMap<Date, ArrayList<Peculiarity>>();
		//DELETE METHOD
		initPeculiarities();
		initPeculiarities2();
		initPeculiarities3();
	}
	
	public Map<Date, ArrayList<Peculiarity>> getPeculiarities() {
		return this.peculiarties;
	}
	
	public ArrayList<Peculiarity> getPeculiaritiesByDate(Date date){
		return this.peculiarties.get(date);
	}
	
	public void deletePeculiaritiesByDate(Date date){
		this.peculiarties.remove(date);
	}
	
	private void initPeculiarities() {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "3", true);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "3", true);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "3", true);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "4", true);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "4", true);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "4", true);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "4", true);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "4", true);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "4", true);
		
		ArrayList<Peculiarity> peculiaritiesList = new ArrayList<>();
		peculiaritiesList.add(p1);
		peculiaritiesList.add(p2);
		peculiaritiesList.add(p3);
		peculiaritiesList.add(p1_e);
		peculiaritiesList.add(p2_e);
		peculiaritiesList.add(p3_e);
		peculiaritiesList.add(p4_e);
		peculiaritiesList.add(p5_e);
		peculiaritiesList.add(p6_e);
		
		Date currentDate = new Date();
		DateUtils.resetTime(currentDate);
		
		this.peculiarties.put(currentDate, peculiaritiesList);
	}
	
	private void initPeculiarities2() {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "Sistema", false);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "1", true);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "Sistema", false);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "Sistema", false);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "2", true);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "Sistema", false);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "Sistema", false);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "2", true);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "2", true);
		
		ArrayList<Peculiarity> peculiaritiesList = new ArrayList<>();
		peculiaritiesList.add(p1);
		peculiaritiesList.add(p2);
		peculiaritiesList.add(p3);
		peculiaritiesList.add(p1_e);
		peculiaritiesList.add(p2_e);
		peculiaritiesList.add(p3_e);
		peculiaritiesList.add(p4_e);
		peculiaritiesList.add(p5_e);
		peculiaritiesList.add(p6_e);
		
		Date currentDate = new Date(119,9,22);
		DateUtils.resetTime(currentDate);
		
		this.peculiarties.put(currentDate, peculiaritiesList);
	}
	
	private void initPeculiarities3() {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "6", true);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "6", true);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "6", true);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "7", true);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "7", true);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "7", true);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "7", true);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "7", true);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "7", true);
		
		ArrayList<Peculiarity> peculiaritiesList = new ArrayList<>();
		peculiaritiesList.add(p1);
		peculiaritiesList.add(p2);
		peculiaritiesList.add(p3);
		peculiaritiesList.add(p1_e);
		peculiaritiesList.add(p2_e);
		peculiaritiesList.add(p3_e);
		peculiaritiesList.add(p4_e);
		peculiaritiesList.add(p5_e);
		peculiaritiesList.add(p6_e);
		
		Date currentDate = new Date(119,11,25);
		DateUtils.resetTime(currentDate);
		
		this.peculiarties.put(currentDate, peculiaritiesList);
	}
}
