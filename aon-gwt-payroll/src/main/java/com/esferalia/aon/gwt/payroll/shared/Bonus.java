package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.salary.enumeration.BonusType;
import com.esferalia.aon.salary.enumeration.BonusTypeVisitor;

public class Bonus extends Item<Bonus.Type> {
	
	
	public static enum Type {
		SOCIAL_SECURITY,
		EMPLOYMENT_PROMOTION,
		CEUTA_MELILLA,
		HANDICAP,
		LAW_19_94,
		DISTANCE_FORMATION,
		CLASSROOM_FORMATION,
		ERE,
		ENCOURAGED_INDUSTRIAL_SECTOR,
		GT_60;

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
			}
		};
	}

}