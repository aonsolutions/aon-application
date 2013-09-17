package com.code.aon.common.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;


public enum Province implements IResourceable {

	DESCONOCIDO,
	ARABA,
	ALBACETE,
	ALICANTE,
	ALMERIA,
	AVILA,
	BADAJOZ,
	ILLES_BALEARS,
	BARCELONA,
	BURGOS,
	CACERES,
	CADIZ,
	CASTELLON,
	CIUDAD_REAL,
	CORDOBA,
	A_CORUNA,
	CUENCA,
	GIRONA,
	GRANADA,
	GUADALAJARA,
	GIPUZKOA,
	HUELVA,
	HUESCA,
	JAEN,
	LEON,
	LLEIDA,
	LA_RIOJA,
	LUGO,
	MADRID,
	MALAGA,
	MURCIA,
	NAVARRA,
	OURENSE,
	ASTURIAS,
	PALENCIA,
	LAS_PALMAS,
	PONTEVEDRA,
	SALAMANCA,
	TENERIFE,
	CANTABRIA,
	SEGOVIA,
	SEVILLA,
	SORIA,
	TARRAGONA,
	TERUEL,
	TOLEDO,
	VALENCIA,
	VALLADOLID,
	BIZKAIA,
	ZAMORA,
	ZARAGOZA,
	CEUTA,
	MELILLA,
	NO_RESIDENTE;

    private static final String MSG_KEY_PREFIX = "aon_enum_province_";
    
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
    
}