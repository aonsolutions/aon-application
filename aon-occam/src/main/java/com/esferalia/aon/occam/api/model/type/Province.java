package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum Province implements Serializable {
	  DESCONOCIDO	, ARABA			, ALBACETE			, ALICANTE
	, ALMERIA		, AVILA			, BADAJOZ			, ILLES_BALEARS
	, BARCELONA		, BURGOS		, CACERES			, CADIZ	
	, CASTELLON		, CIUDAD_REAL	, CORDOBA			, A_CORUNA
	, CUENCA		, GIRONA		, GRANADA			, GUADALAJARA
	, GIPUZKOA		, HUELVA		, HUESCA			, JAEN
	, LEON			, LLEIDA		, LA_RIOJA			, LUGO
	, MADRID		, MALAGA		, MURCIA			, NAVARRA
	, OURENSE		, ASTURIAS		, PALENCIA			, LAS_PALMAS
	, PONTEVEDRA	, SALAMANCA		, TENERIFE			, CANTABRIA
	, SEGOVIA		, SEVILLA		, SORIA				, TARRAGONA
	, TERUEL		, TOLEDO		, VALENCIA			, VALLADOLID
	, BIZKAIA		, ZAMORA		, ZARAGOZA			, CEUTA
	, MELILLA		, NO_RESIDENTE;

	public static Province safeValueOf( String code ) {
		if (AonStringUtils.isBlank(code)) {
			return null;
		}
		code = AonStringUtils.trim( code);
		if (!AonStringUtils.isNumeric(code)) {
			return null;
		}
		int i = Integer.parseInt(code);
		if (i < 0 || i > Province.values().length) {
			return null;
		}
		return Province.values()[i];
	}
}
