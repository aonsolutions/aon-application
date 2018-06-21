package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.salary.enumeration.DeductionType;

public class Deduction extends Item<Deduction.Type> {

	public static enum Type {
		COMMON_CONTINGENCY, 
		PROFESSIONAL_CONTINGENCY, 
		UNEMPLOYMENT, 
		JOB_TRAINING, 
		STRUCTURAL_OVERTIME, 
		NON_STRUCTURAL_OVERTIME, 
		IRPF, 
		ADVANCE_PAYMENT, 
		IN_KIND, 
		OTHER, 
		FOGASA, // TODO:
		EMBARGO // TODO: ???
		;
		public String getDescription() {
			return DESCRIPTIONS.get(this);
		}
		
		static Map<Type, String> DESCRIPTIONS = new HashMap<Type, String>() {
			{
				put(COMMON_CONTINGENCY, "Contingencias Comunes");
				put(PROFESSIONAL_CONTINGENCY, "Contingencias Profesionales");
				put(UNEMPLOYMENT, "Desempleo");
				put(JOB_TRAINING, "Formaci\u00f3n Profesional");
				put(STRUCTURAL_OVERTIME, "Horas Extraordinarias Fuerza Mayor");
				put(NON_STRUCTURAL_OVERTIME, "Resto Horas Extraordinarias");
				put(IRPF, "I.R.P.F");
				put(ADVANCE_PAYMENT, "Anticipo");
				put(IN_KIND, "valor de productos en especie");
				put(OTHER, "Otras deducciones");
				put(FOGASA, "FOGASA");
				put(EMBARGO, "Embargo");
			}
		};


	}



}