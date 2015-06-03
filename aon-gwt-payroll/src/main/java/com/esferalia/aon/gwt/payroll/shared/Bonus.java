package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.HasDescription;
import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.BonusTypeVisitor;

public class Bonus extends Item<Bonus.Type> {
	
	
	public static enum Type implements HasDescription {
		SOCIAL_SECURITY,
		EMPLOYMENT_PROMOTION,
		CEUTA_MELILLA,
		HANDICAP,
		LAW_19_94,
		DISTANCE_FORMATION,
		CLASSROOM_FORMATION,
		ERE,
		ENCOURAGED_INDUSTRIAL_SECTOR,
		GT_60,
		EXEMPTION_GT30_CHILD,
		REDUCTION_RIGHT_CONTRACT,
		REDUCTION_COMMON_CONTINGENCY_EXCEPT_IT,
		REDUCTION_FARMER_COMMON_CONTINGENCY,
		REDUCTION_FARMER_UNEMPLOYMENT,
		REDUCTION_FLAT_RATE_RDL03_2014,
		CONTINUOUS_FORMATION,
		YOUTH_WARRANTY_RDL08_2014
		;

		public String getDescription() {
			return DESCRIPTIONS.get(this);
		}

		static Map<Type, String> DESCRIPTIONS = new HashMap<Type, String>() {
			{
				put(SOCIAL_SECURITY, "Seguridad Social");
				put(EMPLOYMENT_PROMOTION, "Promoci\u00f3n del Empleo");
				put(CEUTA_MELILLA, "Ceuta y Melilla");
				put(HANDICAP, "Minusval\u00cdas");
				put(LAW_19_94, "Ley 19/94");
				put(DISTANCE_FORMATION, "Formaci\u00f3n a Distancia");
				put(CLASSROOM_FORMATION, "Formaci\u00f3n Presencial");
				put(ERE, "Expediente de Regulaci\u00f3n de Empleo");
				put(ENCOURAGED_INDUSTRIAL_SECTOR, "Incentivo del Sector Industrial");
				put(GT_60, "Mayor o Igual de 60 a\u00f1os");
				put(EXEMPTION_GT30_CHILD, "Exenci\u00f3n de desempleo hijos<30au00f1s Autonomos");
				put(REDUCTION_RIGHT_CONTRACT, "Reducciones - Contratos con derecho a reducci\u00f3n (casilla 209 de TC1) ");
				put(REDUCTION_COMMON_CONTINGENCY_EXCEPT_IT, "Reducci\u00f3n contingencias comunes excepto I.T. (R.D.L. 16/2001) ");
				put(REDUCTION_FARMER_COMMON_CONTINGENCY, "Reducciones SEA Contingencias comunes - (Sistema Especial Agrario)");
				put(REDUCTION_FARMER_UNEMPLOYMENT, "Reducciones SEA Desempleo - (Sistema Especial Agrario)");
				put(REDUCTION_FLAT_RATE_RDL03_2014, "Reducciones Tarifa Plana Contingencias comunes RDL-3/2014");
				put(CONTINUOUS_FORMATION, "Bonificaci\u00f3n INEM formaci\u00f3n continua");
				put(YOUTH_WARRANTY_RDL08_2014, "Bonificaci\u00f3n Garantia Juvenil (RDL-8/2014)");
			}
		};
	}

}