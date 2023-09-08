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
	PROFESSIONAL(PROFESIONAL,"Profesional (G/01)")
	,RENTING(CAPITAL_INMOBILIARIO,"Arrendamiento")
	,MOVABLE_CAPITAL(CAPITAL_MOBILIARIO,"Cap. Mobiliario (A)")
	,FARMER(OTRAS,"Agr\u00EDcola (H/01)")
	,TRANSPORT_OPERATOR(OTRAS,"Est. Obj. (H/04)")
	,M190_G_02	(PROFESIONAL,"Profesional (G/02)")
	,M190_G_03	(PROFESIONAL,"Profesional (G/03)")
	,M190_H_02 	(OTRAS,"Agr\u00EDcola (H/02)")
	,M190_H_03	(OTRAS,"Forestales (H/03)")
	,M190_I_01	(OTRAS,"Der.Imagen (I/01)")
	,M190_I_02	(OTRAS,"Otras (I/02)")
	,M190_J		(DERECHOS_IMAGEN,"Cesi\u00F3n Der. Imagen(J)")
	,M190_K_01	(GANANCIAS_PATRIMONIALES,"Premios (K/01)")
	,M190_K_03	(GANANCIAS_PATRIMONIALES,"Premios (K/03)")
	,M190_K_02	(GANANCIAS_PATRIMONIALES,"Gan. forestal (K/02)")
	,M193_C1(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/1)")
	,M193_C2(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/2)")
	,M193_C3(CAPITAL_MOBILIARIO,"Cap. Mobiliario (C/3)")
	,M190_F_01(TRABAJO,"Trabajo (F/01)")
	,M190_F_02_1(TRABAJO,"Cursos (F/02)")
	,M190_F_02_2(TRABAJO,"Elab. obras (F/02)")
	;
	private WithholdingTypeGroup group;
	private String abbrev;
	
	private WithholdingType(WithholdingTypeGroup group, String abbrev){
		this.group = group;
		this.abbrev = abbrev;
	}
	
	public WithholdingTypeGroup getGroup() {
		return group;
	}
	public String getAbbreviatedDescription() {
		return abbrev;
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