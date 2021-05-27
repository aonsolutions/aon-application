package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.Provinces;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.esferalia.aon.occam.api.model.type.Province;




public class ProvincetoProvinces {
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void fill(Map<Province,String> map);
	}
	

	private static final IPropertyFiller[] PROVINCES = new IPropertyFiller[] {
		 (ctx) ->  set(ctx,Province.A_CORUNA,Provinces.ACORUNA)
		 ,(ctx) ->  set(ctx,Province.ALBACETE,Provinces.ALBACETE)
		 ,(ctx) ->  set(ctx,Province.ALICANTE,Provinces.ALICANTE)
		 ,(ctx) ->  set(ctx,Province.ALMERIA,Provinces.ALMERIA)
		 ,(ctx) ->  set(ctx,Province.ARABA,Provinces.ARABA)
		 ,(ctx) ->  set(ctx,Province.ASTURIAS,Provinces.ASTURIAS)
		 ,(ctx) ->  set(ctx,Province.AVILA,Provinces.AVILA)
		 ,(ctx) ->  set(ctx,Province.BADAJOZ,Provinces.BADAJOZ)
		 ,(ctx) ->  set(ctx,Province.ILLES_BALEARS,Provinces.BALEARES)
		 ,(ctx) ->  set(ctx,Province.BARCELONA,Provinces.BARCELONA)
		 ,(ctx) ->  set(ctx,Province.BIZKAIA,Provinces.BIZKAIA)
		 ,(ctx) ->  set(ctx,Province.BURGOS,Provinces.BURGOS)
		 ,(ctx) ->  set(ctx,Province.CACERES,Provinces.CACERES)
		 ,(ctx) ->  set(ctx,Province.CADIZ,Provinces.CADIZ)
		 ,(ctx) ->  set(ctx,Province.CANTABRIA,Provinces.CANTABRIA)
		 ,(ctx) ->  set(ctx,Province.CASTELLON,Provinces.CASTELLON)
		 ,(ctx) ->  set(ctx,Province.CEUTA,Provinces.CEUTA)
		 ,(ctx) ->  set(ctx,Province.CIUDAD_REAL,Provinces.CIUDAD_REAL)
		 ,(ctx) ->  set(ctx,Province.CORDOBA,Provinces.CORDOBA)
		 ,(ctx) ->  set(ctx,Province.CUENCA,Provinces.CUENCA)
		 ,(ctx) ->  set(ctx,Province.GIPUZKOA,Provinces.GIPUZKOA)
		 ,(ctx) ->  set(ctx,Province.GIRONA,Provinces.GIRONA)
		 ,(ctx) ->  set(ctx,Province.GRANADA,Provinces.GRANADA)
		 ,(ctx) ->  set(ctx,Province.GUADALAJARA,Provinces.GUADALAJARA)
		 ,(ctx) ->  set(ctx,Province.HUELVA,Provinces.HUELVA)
		 ,(ctx) ->  set(ctx,Province.HUESCA,Provinces.HUESCA)
		 ,(ctx) ->  set(ctx,Province.JAEN,Provinces.JAEN)
		 ,(ctx) ->  set(ctx,Province.LA_RIOJA,Provinces.LARIOJA)
		 ,(ctx) ->  set(ctx,Province.LEON,Provinces.LEON)
		 ,(ctx) ->  set(ctx,Province.LLEIDA,Provinces.LLEIDA)
		 ,(ctx) ->  set(ctx,Province.LUGO,Provinces.LUGO)
		 ,(ctx) ->  set(ctx,Province.MADRID,Provinces.MADRID)
		 ,(ctx) ->  set(ctx,Province.MALAGA,Provinces.MALAGA)
		 ,(ctx) ->  set(ctx,Province.MELILLA,Provinces.MELILLA)
		 ,(ctx) ->  set(ctx,Province.MURCIA,Provinces.MURCIA)
		 ,(ctx) ->  set(ctx,Province.NAVARRA,Provinces.NAVARRA)
		 ,(ctx) ->  set(ctx,Province.OURENSE,Provinces.ORENSE)
		 ,(ctx) ->  set(ctx,Province.PALENCIA,Provinces.PALENCIA)
		 ,(ctx) ->  set(ctx,Province.LAS_PALMAS,Provinces.PALMAS)
		 ,(ctx) ->  set(ctx,Province.PONTEVEDRA,Provinces.PONTEVEDRA)
		 ,(ctx) ->  set(ctx,Province.SALAMANCA,Provinces.SALAMANCA)
		 ,(ctx) ->  set(ctx,Province.SEGOVIA,Provinces.SEGOVIA)
		 ,(ctx) ->  set(ctx,Province.SEVILLA,Provinces.SEVILLA)
		 ,(ctx) ->  set(ctx,Province.SORIA,Provinces.SORIA)
		 ,(ctx) ->  set(ctx,Province.TARRAGONA,Provinces.TARRAGONA)
		 ,(ctx) ->  set(ctx,Province.TENERIFE,Provinces.TENERIFE)
		 ,(ctx) ->  set(ctx,Province.TERUEL,Provinces.TERUEL)
		 ,(ctx) ->  set(ctx,Province.TOLEDO,Provinces.TOLEDO)
		 ,(ctx) ->  set(ctx,Province.VALENCIA,Provinces.VALENCIA)
		 ,(ctx) ->  set(ctx,Province.VALLADOLID,Provinces.VALLADOLID)
		 ,(ctx) ->  set(ctx,Province.ZAMORA,Provinces.ZAMORA)
		 ,(ctx) ->  set(ctx,Province.ZARAGOZA,Provinces.ZARAGOZA)
	};
	
	private static void set(Map<Province, String> ctx, Province p, Provinces ps) {
		String value = ps.getId();
		ctx.put(p, value);
	} 

	public static void fill(Map<Province, String> ctx) {
		for (IPropertyFiller filler : PROVINCES ) {
			filler.fill(ctx);
		}
	}
	
}
