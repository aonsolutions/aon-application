package com.esferalia.aon.gwt.fiscal.shared;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public interface FiscalEnum {

	public enum Administration implements Serializable, IsSerializable{
		ALAVA, BIZKAIA, GIPUZKOA, NAVARRA, COMMON_TERRITORY, UNKNOWN;
	}

	public enum Province implements Serializable, IsSerializable{
		DESCONOCIDO, ARABA, ALBACETE, ALICANTE, ALMERIA, AVILA, BADAJOZ, 
		ILLES_BALEARS, BARCELONA, BURGOS, CACERES, CADIZ, CASTELLON, 
		CIUDAD_REAL, CORDOBA, A_CORUNA, CUENCA, GIRONA, GRANADA, GUADALAJARA, 
		GIPUZKOA, HUELVA, HUESCA, JAEN, LEON, LLEIDA, LA_RIOJA, LUGO, MADRID, 
		MALAGA, MURCIA, NAVARRA, OURENSE, ASTURIAS, PALENCIA, LAS_PALMAS, 
		PONTEVEDRA, SALAMANCA, TENERIFE, CANTABRIA, SEGOVIA, SEVILLA, SORIA, 
		TARRAGONA, TERUEL, TOLEDO, VALENCIA, VALLADOLID, BIZKAIA, ZAMORA, 
		ZARAGOZA, CEUTA, MELILLA, NO_RESIDENTE;
	}

	public enum ActivityGroup implements Serializable, IsSerializable{
		GROUP1("1"), GROUP2("2"), GROUP3("2"), GROUP4("3"), GROUP5("4"), GROUP6(
				"5"), GROUP7("6");

		String key;

		private ActivityGroup(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
	}
	
}
