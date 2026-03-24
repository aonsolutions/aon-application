package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.js.payroll.client.Reports;

@SuppressWarnings("serial")
public class Deduction extends Item<Deduction.Type> implements Reports.Deduction {

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
		EMBARGO, // TODO: ???
		BONUS,
		MEI,
		SOLIDARITY,
		SEA
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
				put(NON_STRUCTURAL_OVERTIME, "Horas Extraordinarias Fuerza Mayor");
				put(STRUCTURAL_OVERTIME, "Resto Horas Extraordinarias");
				put(IRPF, "IRPF");
				put(ADVANCE_PAYMENT, "Anticipo");
				put(IN_KIND, "Valor de productos en especie");
				put(OTHER, "Otras deducciones");
				put(FOGASA, "FOGASA");
				put(EMBARGO, "Embargo");
				put(BONUS, "Bonif.y Subvenc.con cargo al INEM");
				put(MEI, "Mecanismo Equidad Intergeneracional (MEI)");
				put(SOLIDARITY, "Solidaridad");
			}
		};
	}

	@Override
	public String getTypeName() {
		return this.type.getDescription();
	}

	@Override
	public Double getPercent() {
		return 0.00;
	}

}