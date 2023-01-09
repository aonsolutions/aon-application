package com.code.aon.config.enumeration;

import static com.code.aon.config.enumeration.WithholdingTypeGroup.CAPITAL_INMOBILIARIO;
import static com.code.aon.config.enumeration.WithholdingTypeGroup.CAPITAL_MOBILIARIO;
import static com.code.aon.config.enumeration.WithholdingTypeGroup.DERECHOS_IMAGEN;
import static com.code.aon.config.enumeration.WithholdingTypeGroup.GANANCIAS_PATRIMONIALES;
import static com.code.aon.config.enumeration.WithholdingTypeGroup.OTRAS;
import static com.code.aon.config.enumeration.WithholdingTypeGroup.PROFESIONAL;
import static com.code.aon.config.enumeration.WithholdingTypeGroup.TRABAJO;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum WithholdingType implements IResourceable {
	PROFESSIONAL(PROFESIONAL)
	,RENTING(CAPITAL_INMOBILIARIO)
	,MOVABLE_CAPITAL(CAPITAL_MOBILIARIO)
	,FARMER(OTRAS)
	,TRANSPORT_OPERATOR(OTRAS)
	,M190_G_02	(PROFESIONAL)
	,M190_G_03	(PROFESIONAL)
	,M190_H_02 	(OTRAS)
	,M190_H_03	(OTRAS)
	,M190_I_01	(OTRAS)
	,M190_I_02	(OTRAS)
	,M190_J		(DERECHOS_IMAGEN)
	,M190_K_01	(GANANCIAS_PATRIMONIALES)
	,M190_K_03	(GANANCIAS_PATRIMONIALES)
	,M190_K_02	(GANANCIAS_PATRIMONIALES)
	,M193_C1(CAPITAL_MOBILIARIO)
	,M193_C2(CAPITAL_MOBILIARIO)
	,M193_C3(CAPITAL_MOBILIARIO)
	,M190_F_01(TRABAJO)
	,M190_F_02_1(TRABAJO)
	,M190_F_02_2(TRABAJO)
	;
	private WithholdingTypeGroup group;
	
	private WithholdingType(WithholdingTypeGroup group){
		this.group = group;
	}
	
	public WithholdingTypeGroup getGroup() {
		return group;
	}
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_withholding_type_";

    /**
     * Returns a <code>String</code> with the transalation <code>Locale</code>
     * for the locale.
     * 
     * @param locale Required Locale.
     * 
     * @return String a <code>String</code>.
     */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
	public static WithholdingType[] ORDERED_VALUES = new WithholdingType[] { 
			 // PROFESIONAL 
		PROFESSIONAL,M190_G_02,M190_G_03
		// CAPITAL_INMOBILIARIO
		,RENTING
		//  OTRAS
		,FARMER,M190_H_02,M190_H_03,TRANSPORT_OPERATOR,M190_I_01,M190_I_02
		// TRABAJO
		,M190_F_01,M190_F_02_1,M190_F_02_2
		// CAPITAL_MOBILIARIO
		,MOVABLE_CAPITAL,M193_C1,M193_C2,M193_C3
		// DERECHOS_IMAGEN
		,M190_J
		// GANANCIAS_PATRIMONIALES
		,M190_K_01,M190_K_03,M190_K_02
	};

    
}