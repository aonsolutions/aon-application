package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

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
	public static class Peculiarity implements Serializable{
		private String name;
		private String value;
		private Boolean checked;
		private Integer type;
		
		public Peculiarity() {
			super();
		}

		public Peculiarity(String name, String value, Boolean checked, Integer type) {
			super();
			this.name = name;
			this.value = value;
			this.checked = checked;
			this.type = type;
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

		public Integer getType() {
			return type;
		}

		public void setType(Integer type) {
			this.type = type;
		}
		
	}
	
	//BEGIN PECULIARITIES CLASS
	private Map<Date, ArrayList<Peculiarity>> peculiarties;
	
	public Peculiarities() {
		super();
		this.peculiarties = new TreeMap<Date, ArrayList<Peculiarity>>();
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
	
	public void addPeculiarity(Date date) {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "Sistema", false, 0);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "Sistema", false, 0);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "Sistema", false, 0);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "Sistema", false, 0);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "Sistema", false, 0);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "Sistema", false, 0);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "Sistema", false, 0);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "Sistema", false, 0);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "Sistema", false, 0);
		
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
		
		DateUtils.resetTime(date);
		
		this.peculiarties.put(date, peculiaritiesList);
	}
	
	public void updatePecularity(Date date, String value, String name) {
		for(Peculiarity peculiarity : this.peculiarties.get(date)) {
			if(peculiarity.getName().equals(name)) {
				if(value.equals("Sistema")) {
					peculiarity.setValue(value);
					peculiarity.setChecked(false);
				}else {
					peculiarity.setValue(value);
					peculiarity.setChecked(true);
				}
				peculiarity.setType(0);
			}else {
				peculiarity.setType(0);
			}
		}
	}
	
	
	private void initPeculiarities() {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "3", true, 0);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "3", true, 0);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "3", true, 0);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "4", true, 0);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "4", true, 0);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "4", true, 0);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "4", true, 0);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "4", true, 0);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "4", true, 0);
		
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


	public void addPeculiarityJubAct(Date date) {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "2.25", true, 1);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "0", true, 1);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "0", true, 1);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "7.25", true, 1);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "Sistema", false, 1);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "Sistema", false, 1);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "0", true, 1);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "0", true, 1);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "0", true, 1);
		
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
		
		DateUtils.resetTime(date);
		
		this.peculiarties.put(date, peculiaritiesList);
	}
	
	public void addPeculiarityCoop(Date date) {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "Sistema", false, 2);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "0", true, 2);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "Sistema", false, 2);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "Sistema", false, 2);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "Sistema", false, 2);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "Sistema", false, 2);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "0", true, 2);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "Sistema", false, 2);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "0", true, 2);
		
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
		
		DateUtils.resetTime(date);
		
		this.peculiarties.put(date, peculiaritiesList);
	}
	
	public void addPeculiarityBec(Date date) {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "0", true, 3);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "0", true, 3);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "0", true, 3);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "0", true, 3);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "0", true, 3);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "0", true, 3);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "0", true, 3);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "0", true, 3);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "0", true, 3);
		
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
		
		DateUtils.resetTime(date);
		
		this.peculiarties.put(date, peculiaritiesList);
	}

	public void addPeculiarityRegGen(Date date) {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "Sistema", false, 4);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "0", true, 4);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "Sistema", false, 4);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "Sistema", false, 4);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "Sistema", false, 4);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "Sistema", false, 4);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "0", true, 4);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "Sistema", false, 4);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "0", true, 4);
		
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
		
		DateUtils.resetTime(date);
		
		this.peculiarties.put(date, peculiaritiesList);
	}

	public void addPeculiarity65Old(Date date) {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "0.25", true, 5);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "0", true, 5);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "0", true, 5);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "1.25", true, 5);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "Sistema", false, 5);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "Sistema", false, 5);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "0", true, 5);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "0", true, 5);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "0", true, 5);
		
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
		
		DateUtils.resetTime(date);
		
		this.peculiarties.put(date, peculiaritiesList);
	}

	public void addPeculiarityMinCult(Date date) {
		Peculiarity p1 = new Peculiarity("PORCENTAJE_CGC", "Sistema", false, 6);
		Peculiarity p2 = new Peculiarity("PORCENTAJE_DESMPL", "0", true, 6);
		Peculiarity p3 = new Peculiarity("PORCENTAJE_FP", "0", true, 6);
		
		Peculiarity p1_e = new Peculiarity("PORCENTAJE_CGC_E", "Sistema", false, 6);
		Peculiarity p2_e = new Peculiarity("PORCENTAJE_IT", "Sistema", false, 6);
		Peculiarity p3_e = new Peculiarity("PORCENTAJE_IMS", "Sistema", false, 6);
		Peculiarity p4_e = new Peculiarity("PORCENTAJE_FOGASA", "0", true, 6);
		Peculiarity p5_e = new Peculiarity("PORCENTAJE_FP_E", "0", true, 6);
		Peculiarity p6_e = new Peculiarity("PORCENTAJE_DESMPL_E", "0", true, 6);
		
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
		
		DateUtils.resetTime(date);
		
		this.peculiarties.put(date, peculiaritiesList);
	}

		
}
