package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;

public class D2PrevioustoD2Current {
	
	@FunctionalInterface
	private static interface IPropertyFillerHeader {
		public void fill(Map<D2DepositHeaderKey,Double> mapCurrent, Map<D2DepositHeaderKey,Double> mapPrevious);
	}
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void fill2(Map<D2DepositKey,Double> mapCurrent, Map<D2DepositKey,Double> mapPrevious);
	}
	

	private static final IPropertyFillerHeader[] BALANCE_ACTIVE_KEYS = new IPropertyFillerHeader[] {
			
		 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111000,D2DepositHeaderKey.BA1110009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111100,D2DepositHeaderKey.BA1111009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111200,D2DepositHeaderKey.BA1112009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111300,D2DepositHeaderKey.BA1113009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111400,D2DepositHeaderKey.BA1114009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111500,D2DepositHeaderKey.BA1115009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111600,D2DepositHeaderKey.BA1116009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA111700,D2DepositHeaderKey.BA1117009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112000,D2DepositHeaderKey.BA1120009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112100,D2DepositHeaderKey.BA1121009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112200,D2DepositHeaderKey.BA1122009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112300,D2DepositHeaderKey.BA1123009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112380,D2DepositHeaderKey.BA1123809)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112381,D2DepositHeaderKey.BA1123819)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112382,D2DepositHeaderKey.BA1123829)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112370,D2DepositHeaderKey.BA1123709)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112390,D2DepositHeaderKey.BA1123909)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112400,D2DepositHeaderKey.BA1124009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112500,D2DepositHeaderKey.BA1125009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112600,D2DepositHeaderKey.BA1126009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA112700,D2DepositHeaderKey.BA1127009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA110000,D2DepositHeaderKey.BA1100009)
		
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2120000,D2DepositHeaderKey.BA21200009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121000,D2DepositHeaderKey.BA21210009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121100,D2DepositHeaderKey.BA21211009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121110,D2DepositHeaderKey.BA21211109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121120,D2DepositHeaderKey.BA21211209)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121200,D2DepositHeaderKey.BA21212009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121300,D2DepositHeaderKey.BA21213009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121400,D2DepositHeaderKey.BA21214009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121500,D2DepositHeaderKey.BA21215009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121600,D2DepositHeaderKey.BA21216009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121700,D2DepositHeaderKey.BA21217009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121800,D2DepositHeaderKey.BA21218009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2121900,D2DepositHeaderKey.BA21219009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2122000,D2DepositHeaderKey.BA21220009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2123000,D2DepositHeaderKey.BA21230009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131000,D2DepositHeaderKey.BA21310009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131100,D2DepositHeaderKey.BA21311009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131200,D2DepositHeaderKey.BA21312009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131220,D2DepositHeaderKey.BA21312209)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131230,D2DepositHeaderKey.BA21312309)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131290,D2DepositHeaderKey.BA21312909)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131300,D2DepositHeaderKey.BA21313009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131400,D2DepositHeaderKey.BA21314009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131500,D2DepositHeaderKey.BA21315009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131600,D2DepositHeaderKey.BA21316009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2131700,D2DepositHeaderKey.BA21317009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232000,D2DepositHeaderKey.BA22320009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232100,D2DepositHeaderKey.BA22321009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232200,D2DepositHeaderKey.BA22322009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232300,D2DepositHeaderKey.BA22323009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232320,D2DepositHeaderKey.BA22323209)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232330,D2DepositHeaderKey.BA22323309)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232390,D2DepositHeaderKey.BA22323909)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232400,D2DepositHeaderKey.BA22324009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232500,D2DepositHeaderKey.BA22325009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232580,D2DepositHeaderKey.BA22325809)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232581,D2DepositHeaderKey.BA22325819)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232582,D2DepositHeaderKey.BA22325829)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232590,D2DepositHeaderKey.BA22325909)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232600,D2DepositHeaderKey.BA22326009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2232700,D2DepositHeaderKey.BA22327009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.BA2230000,D2DepositHeaderKey.BA22300009)
	};
		
	private static final IPropertyFillerHeader[] PYG_ACTIVE_KEYS = new IPropertyFillerHeader[] {
		 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40100,D2DepositHeaderKey.PA401009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40200,D2DepositHeaderKey.PA402009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40300,D2DepositHeaderKey.PA403009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40400,D2DepositHeaderKey.PA404009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40500,D2DepositHeaderKey.PA405009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40600,D2DepositHeaderKey.PA406009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40700,D2DepositHeaderKey.PA407009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40800,D2DepositHeaderKey.PA408009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA40900,D2DepositHeaderKey.PA409009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41000,D2DepositHeaderKey.PA410009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41100,D2DepositHeaderKey.PA411009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41200,D2DepositHeaderKey.PA412009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41300,D2DepositHeaderKey.PA413009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA49100,D2DepositHeaderKey.PA491009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41400,D2DepositHeaderKey.PA414009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41430,D2DepositHeaderKey.PA414309)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41490,D2DepositHeaderKey.PA414909)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41500,D2DepositHeaderKey.PA415009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41600,D2DepositHeaderKey.PA416009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41700,D2DepositHeaderKey.PA417009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41800,D2DepositHeaderKey.PA418009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA49200,D2DepositHeaderKey.PA492009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA49300,D2DepositHeaderKey.PA493009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA41900,D2DepositHeaderKey.PA419009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA49500,D2DepositHeaderKey.PA495009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA42100,D2DepositHeaderKey.PA421009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA42110,D2DepositHeaderKey.PA421109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA42120,D2DepositHeaderKey.PA421209)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PA42130,D2DepositHeaderKey.PA421309)
	};
		
	private static final IPropertyFillerHeader[] ECPN_ACTIVE_KEYS = new IPropertyFillerHeader[] {
		 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA159100,D2DepositHeaderKey.PNA1591009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150010,D2DepositHeaderKey.PNA1500109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150020,D2DepositHeaderKey.PNA1500209)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150030,D2DepositHeaderKey.PNA1500309)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150040,D2DepositHeaderKey.PNA1500409)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150050,D2DepositHeaderKey.PNA1500509)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150060,D2DepositHeaderKey.PNA1500609)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150070,D2DepositHeaderKey.PNA1500709)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA159200,D2DepositHeaderKey.PNA1592009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150080,D2DepositHeaderKey.PNA1500809)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150090,D2DepositHeaderKey.PNA1500909)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150100,D2DepositHeaderKey.PNA1501009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150110,D2DepositHeaderKey.PNA1501109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150120,D2DepositHeaderKey.PNA1501209)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA150130,D2DepositHeaderKey.PNA1501309)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA159300,D2DepositHeaderKey.PNA1593009)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA159400,D2DepositHeaderKey.PNA1594009)
	};
	private static final IPropertyFillerHeader[] ECPN2_ACTIVE_KEYS = new IPropertyFillerHeader[] {
		 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251101,D2DepositHeaderKey.PNA2511019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251102,D2DepositHeaderKey.PNA2511029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251103,D2DepositHeaderKey.PNA2511039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251104,D2DepositHeaderKey.PNA2511049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251105,D2DepositHeaderKey.PNA2511059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251106,D2DepositHeaderKey.PNA2511069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251107,D2DepositHeaderKey.PNA2511079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251108,D2DepositHeaderKey.PNA2511089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251109,D2DepositHeaderKey.PNA2511099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251110,D2DepositHeaderKey.PNA2511109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251111,D2DepositHeaderKey.PNA2511119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251112,D2DepositHeaderKey.PNA2511129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251113,D2DepositHeaderKey.PNA2511139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251201,D2DepositHeaderKey.PNA2512019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251202,D2DepositHeaderKey.PNA2512029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251203,D2DepositHeaderKey.PNA2512039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251204,D2DepositHeaderKey.PNA2512049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251205,D2DepositHeaderKey.PNA2512059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251206,D2DepositHeaderKey.PNA2512069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251207,D2DepositHeaderKey.PNA2512079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251208,D2DepositHeaderKey.PNA2512089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251209,D2DepositHeaderKey.PNA2512099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251210,D2DepositHeaderKey.PNA2512109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251211,D2DepositHeaderKey.PNA2512119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251212,D2DepositHeaderKey.PNA2512129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251213,D2DepositHeaderKey.PNA2512139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251301,D2DepositHeaderKey.PNA2513019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251302,D2DepositHeaderKey.PNA2513029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251303,D2DepositHeaderKey.PNA2513039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251304,D2DepositHeaderKey.PNA2513049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251305,D2DepositHeaderKey.PNA2513059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251306,D2DepositHeaderKey.PNA2513069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251307,D2DepositHeaderKey.PNA2513079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251308,D2DepositHeaderKey.PNA2513089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251309,D2DepositHeaderKey.PNA2513099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251310,D2DepositHeaderKey.PNA2513109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251311,D2DepositHeaderKey.PNA2513119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251312,D2DepositHeaderKey.PNA2513129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251313,D2DepositHeaderKey.PNA2513139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251401,D2DepositHeaderKey.PNA2514019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251402,D2DepositHeaderKey.PNA2514029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251403,D2DepositHeaderKey.PNA2514039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251404,D2DepositHeaderKey.PNA2514049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251405,D2DepositHeaderKey.PNA2514059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251406,D2DepositHeaderKey.PNA2514069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251407,D2DepositHeaderKey.PNA2514079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251408,D2DepositHeaderKey.PNA2514089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251409,D2DepositHeaderKey.PNA2514099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251410,D2DepositHeaderKey.PNA2514109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251411,D2DepositHeaderKey.PNA2514119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251412,D2DepositHeaderKey.PNA2514129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251413,D2DepositHeaderKey.PNA2514139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152801,D2DepositHeaderKey.PNP1528019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152802,D2DepositHeaderKey.PNP1528029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152803,D2DepositHeaderKey.PNP1528039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152804,D2DepositHeaderKey.PNP1528049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152805,D2DepositHeaderKey.PNP1528059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152806,D2DepositHeaderKey.PNP1528069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152807,D2DepositHeaderKey.PNP1528079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152808,D2DepositHeaderKey.PNP1528089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152809,D2DepositHeaderKey.PNP1528099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152811,D2DepositHeaderKey.PNP1528119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152812,D2DepositHeaderKey.PNP1528129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152813,D2DepositHeaderKey.PNP1528139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153001,D2DepositHeaderKey.PNP1530019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153002,D2DepositHeaderKey.PNP1530029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153003,D2DepositHeaderKey.PNP1530039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153004,D2DepositHeaderKey.PNP1530049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153005,D2DepositHeaderKey.PNP1530059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153006,D2DepositHeaderKey.PNP1530069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153007,D2DepositHeaderKey.PNP1530079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153008,D2DepositHeaderKey.PNP1530089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153009,D2DepositHeaderKey.PNP1530099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153011,D2DepositHeaderKey.PNP1530119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153012,D2DepositHeaderKey.PNP1530129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP153013,D2DepositHeaderKey.PNP1530139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152701,D2DepositHeaderKey.PNP1527019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152702,D2DepositHeaderKey.PNP1527029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152703,D2DepositHeaderKey.PNP1527039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152704,D2DepositHeaderKey.PNP1527049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152705,D2DepositHeaderKey.PNP1527059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152706,D2DepositHeaderKey.PNP1527069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152707,D2DepositHeaderKey.PNP1527079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152708,D2DepositHeaderKey.PNP1527089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152709,D2DepositHeaderKey.PNP1527099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152711,D2DepositHeaderKey.PNP1527119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152712,D2DepositHeaderKey.PNP1527129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152713,D2DepositHeaderKey.PNP1527139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152901,D2DepositHeaderKey.PNP1529019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152902,D2DepositHeaderKey.PNP1529029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152903,D2DepositHeaderKey.PNP1529039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152904,D2DepositHeaderKey.PNP1529049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152905,D2DepositHeaderKey.PNP1529059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152906,D2DepositHeaderKey.PNP1529069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152907,D2DepositHeaderKey.PNP1529079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152908,D2DepositHeaderKey.PNP1529089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152909,D2DepositHeaderKey.PNP1529099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152911,D2DepositHeaderKey.PNP1529119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152912,D2DepositHeaderKey.PNP1529129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNP152913,D2DepositHeaderKey.PNP1529139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251501,D2DepositHeaderKey.PNA2515019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251502,D2DepositHeaderKey.PNA2515029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251503,D2DepositHeaderKey.PNA2515039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251504,D2DepositHeaderKey.PNA2515049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251505,D2DepositHeaderKey.PNA2515059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251506,D2DepositHeaderKey.PNA2515069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251507,D2DepositHeaderKey.PNA2515079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251508,D2DepositHeaderKey.PNA2515089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251509,D2DepositHeaderKey.PNA2515099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251510,D2DepositHeaderKey.PNA2515109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251511,D2DepositHeaderKey.PNA2515119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251512,D2DepositHeaderKey.PNA2515129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251513,D2DepositHeaderKey.PNA2515139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251601,D2DepositHeaderKey.PNA2516019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251602,D2DepositHeaderKey.PNA2516029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251603,D2DepositHeaderKey.PNA2516039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251604,D2DepositHeaderKey.PNA2516049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251605,D2DepositHeaderKey.PNA2516059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251606,D2DepositHeaderKey.PNA2516069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251607,D2DepositHeaderKey.PNA2516079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251608,D2DepositHeaderKey.PNA2516089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251609,D2DepositHeaderKey.PNA2516099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251610,D2DepositHeaderKey.PNA2516109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251611,D2DepositHeaderKey.PNA2516119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251612,D2DepositHeaderKey.PNA2516129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251613,D2DepositHeaderKey.PNA2516139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251701,D2DepositHeaderKey.PNA2517019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251702,D2DepositHeaderKey.PNA2517029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251703,D2DepositHeaderKey.PNA2517039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251704,D2DepositHeaderKey.PNA2517049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251705,D2DepositHeaderKey.PNA2517059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251706,D2DepositHeaderKey.PNA2517069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251707,D2DepositHeaderKey.PNA2517079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251708,D2DepositHeaderKey.PNA2517089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251709,D2DepositHeaderKey.PNA2517099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251710,D2DepositHeaderKey.PNA2517109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251711,D2DepositHeaderKey.PNA2517119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251712,D2DepositHeaderKey.PNA2517129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251713,D2DepositHeaderKey.PNA2517139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251801,D2DepositHeaderKey.PNA2518019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251802,D2DepositHeaderKey.PNA2518029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251803,D2DepositHeaderKey.PNA2518039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251804,D2DepositHeaderKey.PNA2518049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251805,D2DepositHeaderKey.PNA2518059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251806,D2DepositHeaderKey.PNA2518069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251807,D2DepositHeaderKey.PNA2518079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251808,D2DepositHeaderKey.PNA2518089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251809,D2DepositHeaderKey.PNA2518099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251810,D2DepositHeaderKey.PNA2518109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251811,D2DepositHeaderKey.PNA2518119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251812,D2DepositHeaderKey.PNA2518129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA251813,D2DepositHeaderKey.PNA2518139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252601,D2DepositHeaderKey.PNA2526019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252602,D2DepositHeaderKey.PNA2526029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252603,D2DepositHeaderKey.PNA2526039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252604,D2DepositHeaderKey.PNA2526049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252605,D2DepositHeaderKey.PNA2526059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252606,D2DepositHeaderKey.PNA2526069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252607,D2DepositHeaderKey.PNA2526079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252608,D2DepositHeaderKey.PNA2526089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252609,D2DepositHeaderKey.PNA2526099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252610,D2DepositHeaderKey.PNA2526109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252611,D2DepositHeaderKey.PNA2526119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252612,D2DepositHeaderKey.PNA2526129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252613,D2DepositHeaderKey.PNA2526139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252401,D2DepositHeaderKey.PNA2524019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252402,D2DepositHeaderKey.PNA2524029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252403,D2DepositHeaderKey.PNA2524039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252404,D2DepositHeaderKey.PNA2524049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252405,D2DepositHeaderKey.PNA2524059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252406,D2DepositHeaderKey.PNA2524069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252407,D2DepositHeaderKey.PNA2524079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252408,D2DepositHeaderKey.PNA2524089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252409,D2DepositHeaderKey.PNA2524099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252410,D2DepositHeaderKey.PNA2524109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252411,D2DepositHeaderKey.PNA2524119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252412,D2DepositHeaderKey.PNA2524129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA252413,D2DepositHeaderKey.PNA2524139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253101,D2DepositHeaderKey.PNA2531019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253102,D2DepositHeaderKey.PNA2531029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253103,D2DepositHeaderKey.PNA2531039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253104,D2DepositHeaderKey.PNA2531049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253105,D2DepositHeaderKey.PNA2531059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253106,D2DepositHeaderKey.PNA2531069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253107,D2DepositHeaderKey.PNA2531079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253108,D2DepositHeaderKey.PNA2531089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253109,D2DepositHeaderKey.PNA2531099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253110,D2DepositHeaderKey.PNA2531109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253111,D2DepositHeaderKey.PNA2531119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253112,D2DepositHeaderKey.PNA2531129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253113,D2DepositHeaderKey.PNA2531139)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253201,D2DepositHeaderKey.PNA2532019)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253202,D2DepositHeaderKey.PNA2532029)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253203,D2DepositHeaderKey.PNA2532039)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253204,D2DepositHeaderKey.PNA2532049)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253205,D2DepositHeaderKey.PNA2532059)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253206,D2DepositHeaderKey.PNA2532069)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253207,D2DepositHeaderKey.PNA2532079)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253208,D2DepositHeaderKey.PNA2532089)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253209,D2DepositHeaderKey.PNA2532099)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253210,D2DepositHeaderKey.PNA2532109)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253211,D2DepositHeaderKey.PNA2532119)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253212,D2DepositHeaderKey.PNA2532129)
		,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositHeaderKey.PNA253213,D2DepositHeaderKey.PNA2532139)
		
	};
	
	private static final IPropertyFiller[] MEM_AP3 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391000,D2DepositKey.MA3910009)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391001,D2DepositKey.MA3910019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391002,D2DepositKey.MA3910029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391003,D2DepositKey.MA3910039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391004,D2DepositKey.MA3910049)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391005,D2DepositKey.MA3910059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391006,D2DepositKey.MA3910069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391007,D2DepositKey.MA3910079)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391008,D2DepositKey.MA3910089)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391009,D2DepositKey.MA3910099)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391010,D2DepositKey.MA3910109)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391011,D2DepositKey.MA3910119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA391012,D2DepositKey.MA3910129)
			 
	};

	private static final IPropertyFiller[] MEM_AP5 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592001,D2DepositKey.MA5920019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592002,D2DepositKey.MA5920029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592003,D2DepositKey.MA5920039)
	
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592011,D2DepositKey.MA5920119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592012,D2DepositKey.MA5920129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592013,D2DepositKey.MA5920139)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592141,D2DepositKey.MA5921419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592142,D2DepositKey.MA5921429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592143,D2DepositKey.MA5921439)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592021,D2DepositKey.MA5920219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592022,D2DepositKey.MA5920229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592023,D2DepositKey.MA5920239)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592031,D2DepositKey.MA5920319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592032,D2DepositKey.MA5920329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592033,D2DepositKey.MA5920339)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592041,D2DepositKey.MA5920419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592042,D2DepositKey.MA5920429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592043,D2DepositKey.MA5920439)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592051,D2DepositKey.MA5920519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592052,D2DepositKey.MA5920529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592053,D2DepositKey.MA5920539)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592151,D2DepositKey.MA5921519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592152,D2DepositKey.MA5921529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592153,D2DepositKey.MA5921539)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592061,D2DepositKey.MA5920619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592062,D2DepositKey.MA5920629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592063,D2DepositKey.MA5920639)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592071,D2DepositKey.MA5920719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592072,D2DepositKey.MA5920729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592073,D2DepositKey.MA5920739)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592081,D2DepositKey.MA5920819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592082,D2DepositKey.MA5920829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592083,D2DepositKey.MA5920839)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592091,D2DepositKey.MA5920919)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592092,D2DepositKey.MA5920929)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592093,D2DepositKey.MA5920939)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592101,D2DepositKey.MA5921019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592102,D2DepositKey.MA5921029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592103,D2DepositKey.MA5921039)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592111,D2DepositKey.MA5921119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592112,D2DepositKey.MA5921129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592113,D2DepositKey.MA5921139)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592121,D2DepositKey.MA5921219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592122,D2DepositKey.MA5921229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592123,D2DepositKey.MA5921239)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592131,D2DepositKey.MA5921319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592132,D2DepositKey.MA5921329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA592133,D2DepositKey.MA5921339)
			 		 
	};
	
	private static final IPropertyFiller[] MEM_AP6_1 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193001,D2DepositKey.MA61930019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193002,D2DepositKey.MA61930029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193003,D2DepositKey.MA61930039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193004,D2DepositKey.MA61930049)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193011,D2DepositKey.MA61930119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193012,D2DepositKey.MA61930129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193013,D2DepositKey.MA61930139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193014,D2DepositKey.MA61930149)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193021,D2DepositKey.MA61930219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193022,D2DepositKey.MA61930229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193023,D2DepositKey.MA61930239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193024,D2DepositKey.MA61930249)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193031,D2DepositKey.MA61930319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193032,D2DepositKey.MA61930329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193033,D2DepositKey.MA61930339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193034,D2DepositKey.MA61930349)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193041,D2DepositKey.MA61930419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193042,D2DepositKey.MA61930429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193043,D2DepositKey.MA61930439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193044,D2DepositKey.MA61930449)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193051,D2DepositKey.MA61930519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193052,D2DepositKey.MA61930529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193053,D2DepositKey.MA61930539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193054,D2DepositKey.MA61930549)
			 
			// ********* PYMES ************
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193061,D2DepositKey.MP61930619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193062,D2DepositKey.MP61930629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193063,D2DepositKey.MP61930639)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193064,D2DepositKey.MP61930649)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193071,D2DepositKey.MP61930719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193072,D2DepositKey.MP61930729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193073,D2DepositKey.MP61930739)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193074,D2DepositKey.MP61930749)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193081,D2DepositKey.MP61930819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193082,D2DepositKey.MP61930829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193083,D2DepositKey.MP61930839)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193084,D2DepositKey.MP61930849)

			 // ****************************
			 
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193101,D2DepositKey.MA61931019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193102,D2DepositKey.MA61931029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193103,D2DepositKey.MA61931039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193104,D2DepositKey.MA61931049)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193111,D2DepositKey.MA61931119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193112,D2DepositKey.MA61931129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193113,D2DepositKey.MA61931139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193114,D2DepositKey.MA61931149)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193121,D2DepositKey.MA61931219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193122,D2DepositKey.MA61931229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193123,D2DepositKey.MA61931239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193124,D2DepositKey.MA61931249)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193131,D2DepositKey.MA61931319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193132,D2DepositKey.MA61931329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193133,D2DepositKey.MA61931339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193134,D2DepositKey.MA61931349)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193141,D2DepositKey.MA61931419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193142,D2DepositKey.MA61931429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193143,D2DepositKey.MA61931439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193144,D2DepositKey.MA61931449)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193151,D2DepositKey.MA61931519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193152,D2DepositKey.MA61931529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193153,D2DepositKey.MA61931539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193154,D2DepositKey.MA61931549)
			
			 // ********* PYMES ************
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193161,D2DepositKey.MP61931619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193162,D2DepositKey.MP61931629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193163,D2DepositKey.MP61931639)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193164,D2DepositKey.MP61931649)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193171,D2DepositKey.MP61931719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193172,D2DepositKey.MP61931729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193173,D2DepositKey.MP61931739)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193174,D2DepositKey.MP61931749)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193181,D2DepositKey.MP61931819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193182,D2DepositKey.MP61931829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193183,D2DepositKey.MP61931839)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP6193184,D2DepositKey.MP61931849)
	
			 // ****************************
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193201,D2DepositKey.MA61932019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193202,D2DepositKey.MA61932029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193203,D2DepositKey.MA61932039)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193211,D2DepositKey.MA61932119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193212,D2DepositKey.MA61932129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193213,D2DepositKey.MA61932139)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193221,D2DepositKey.MA61932219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193222,D2DepositKey.MA61932229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193223,D2DepositKey.MA61932239)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193231,D2DepositKey.MA61932319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193232,D2DepositKey.MA61932329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193233,D2DepositKey.MA61932339)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193241,D2DepositKey.MA61932419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193242,D2DepositKey.MA61932429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6193243,D2DepositKey.MA61932439)
			 
	};
	
	private static final IPropertyFiller[] MEM_AP6_2 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293301,D2DepositKey.MA62933019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293302,D2DepositKey.MA62933029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293303,D2DepositKey.MA62933039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293304,D2DepositKey.MA62933049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293305,D2DepositKey.MA62933059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293306,D2DepositKey.MA62933069)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293311,D2DepositKey.MA62933119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293312,D2DepositKey.MA62933129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293313,D2DepositKey.MA62933139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293314,D2DepositKey.MA62933149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293315,D2DepositKey.MA62933159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293316,D2DepositKey.MA62933169)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293321,D2DepositKey.MA62933219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293322,D2DepositKey.MA62933229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293323,D2DepositKey.MA62933239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293324,D2DepositKey.MA62933249)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293325,D2DepositKey.MA62933259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293326,D2DepositKey.MA62933269)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293331,D2DepositKey.MA62933319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293332,D2DepositKey.MA62933329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293333,D2DepositKey.MA62933339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293334,D2DepositKey.MA62933349)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293335,D2DepositKey.MA62933359)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293336,D2DepositKey.MA62933369)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293341,D2DepositKey.MA62933419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293342,D2DepositKey.MA62933429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293343,D2DepositKey.MA62933439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293344,D2DepositKey.MA62933449)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293345,D2DepositKey.MA62933459)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293346,D2DepositKey.MA62933469)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293351,D2DepositKey.MA62933519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293352,D2DepositKey.MA62933529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293353,D2DepositKey.MA62933539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293354,D2DepositKey.MA62933549)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293355,D2DepositKey.MA62933559)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293356,D2DepositKey.MA62933569)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293351,D2DepositKey.MA62933519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293352,D2DepositKey.MA62933529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293353,D2DepositKey.MA62933539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293354,D2DepositKey.MA62933549)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293355,D2DepositKey.MA62933559)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293356,D2DepositKey.MA62933569)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293401,D2DepositKey.MA62934019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293402,D2DepositKey.MA62934029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293403,D2DepositKey.MA62934039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293404,D2DepositKey.MA62934049)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293411,D2DepositKey.MA62934119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293412,D2DepositKey.MA62934129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293413,D2DepositKey.MA62934139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293414,D2DepositKey.MA62934149)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293421,D2DepositKey.MA62934219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293422,D2DepositKey.MA62934229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293423,D2DepositKey.MA62934239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293424,D2DepositKey.MA62934249)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293431,D2DepositKey.MA62934319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293432,D2DepositKey.MA62934329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293433,D2DepositKey.MA62934339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293434,D2DepositKey.MA62934349)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293505,D2DepositKey.MA62935059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293515,D2DepositKey.MA62935159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293525,D2DepositKey.MA62935259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA6293535,D2DepositKey.MA62935359)
			 
	};
	
	private static final IPropertyFiller[] MEM_AP7 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794001,D2DepositKey.MA7940019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794002,D2DepositKey.MA7940029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794003,D2DepositKey.MA7940039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794004,D2DepositKey.MA7940049)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794011,D2DepositKey.MA7940119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794012,D2DepositKey.MA7940129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794013,D2DepositKey.MA7940139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794014,D2DepositKey.MA7940149)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794021,D2DepositKey.MA7940219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794022,D2DepositKey.MA7940229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794023,D2DepositKey.MA7940239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794024,D2DepositKey.MA7940249)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794031,D2DepositKey.MA7940319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794032,D2DepositKey.MA7940329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794033,D2DepositKey.MA7940339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794034,D2DepositKey.MA7940349)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794101,D2DepositKey.MA7941019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794102,D2DepositKey.MA7941029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794103,D2DepositKey.MA7941039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794104,D2DepositKey.MA7941049)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794111,D2DepositKey.MA7941119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794112,D2DepositKey.MA7941129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794113,D2DepositKey.MA7941139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794114,D2DepositKey.MA7941149)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794121,D2DepositKey.MA7941219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794122,D2DepositKey.MA7941229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794123,D2DepositKey.MA7941239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794124,D2DepositKey.MA7941249)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794131,D2DepositKey.MA7941319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794132,D2DepositKey.MA7941329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794133,D2DepositKey.MA7941339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA794134,D2DepositKey.MA7941349)
			 
			//************ PYMES ************
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794041,D2DepositKey.MP7940419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794042,D2DepositKey.MP7940429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794043,D2DepositKey.MP7940439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794044,D2DepositKey.MP7940449)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794051,D2DepositKey.MP7940519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794052,D2DepositKey.MP7940529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794053,D2DepositKey.MP7940539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794054,D2DepositKey.MP7940549)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794141,D2DepositKey.MP7941419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794142,D2DepositKey.MP7941429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794143,D2DepositKey.MP7941439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794144,D2DepositKey.MP7941449)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794151,D2DepositKey.MP7941519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794152,D2DepositKey.MP7941529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794153,D2DepositKey.MP7941539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP794154,D2DepositKey.MP7941549)
			 
			//*******************************
			 
	};
	
	private static final IPropertyFiller[] MEM_AP10 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095000,D2DepositKey.MA10950009)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095001,D2DepositKey.MA10950019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095002,D2DepositKey.MA10950029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095003,D2DepositKey.MA10950039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095004,D2DepositKey.MA10950049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095005,D2DepositKey.MA10950059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095006,D2DepositKey.MA10950069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095007,D2DepositKey.MA10950079)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095008,D2DepositKey.MA10950089)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095009,D2DepositKey.MA10950099)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095010,D2DepositKey.MA10950109)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095011,D2DepositKey.MA10950119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095012,D2DepositKey.MA10950129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095013,D2DepositKey.MA10950139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095014,D2DepositKey.MA10950149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095015,D2DepositKey.MA10950159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095016,D2DepositKey.MA10950169)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095017,D2DepositKey.MA10950179)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095018,D2DepositKey.MA10950189)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095019,D2DepositKey.MA10950199)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1095020,D2DepositKey.MA10950209)
			 
	};
	
	private static final IPropertyFiller[] MEM_AP11 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196000,D2DepositKey.MA11960009)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196001,D2DepositKey.MA11960019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196002,D2DepositKey.MA11960029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196010,D2DepositKey.MA11960109)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196011,D2DepositKey.MA11960119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196012,D2DepositKey.MA11960129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196013,D2DepositKey.MA11960139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196014,D2DepositKey.MA11960149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196015,D2DepositKey.MA11960159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1196016,D2DepositKey.MA11960169)
			 
			//*********** PYMES ****************
			
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP1196017,D2DepositKey.MP11960179)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP1196018,D2DepositKey.MP11960189)
			 
			//**********************************

	};
	
	private static final IPropertyFiller[] MEM_AP12_12 = new IPropertyFiller[] { // 1&2
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97001,D2DepositKey.MA12A970019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97002,D2DepositKey.MA12A970029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97003,D2DepositKey.MA12A970039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97004,D2DepositKey.MA12A970049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97005,D2DepositKey.MA12A970059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97006,D2DepositKey.MA12A970069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97007,D2DepositKey.MA12A970079)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97011,D2DepositKey.MA12A970119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97012,D2DepositKey.MA12A970129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97013,D2DepositKey.MA12A970139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97014,D2DepositKey.MA12A970149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97015,D2DepositKey.MA12A970159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97016,D2DepositKey.MA12A970169)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97017,D2DepositKey.MA12A970179)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97021,D2DepositKey.MA12A970219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97022,D2DepositKey.MA12A970229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97023,D2DepositKey.MA12A970239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97024,D2DepositKey.MA12A970249)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97025,D2DepositKey.MA12A970259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97026,D2DepositKey.MA12A970269)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97027,D2DepositKey.MA12A970279)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97031,D2DepositKey.MA12A970319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97032,D2DepositKey.MA12A970329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97033,D2DepositKey.MA12A970339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97034,D2DepositKey.MA12A970349)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97035,D2DepositKey.MA12A970359)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97036,D2DepositKey.MA12A970369)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97037,D2DepositKey.MA12A970379)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97041,D2DepositKey.MA12A970419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97042,D2DepositKey.MA12A970429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97043,D2DepositKey.MA12A970439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97044,D2DepositKey.MA12A970449)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97045,D2DepositKey.MA12A970459)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97046,D2DepositKey.MA12A970469)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97047,D2DepositKey.MA12A970479)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97051,D2DepositKey.MA12A970519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97052,D2DepositKey.MA12A970529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97053,D2DepositKey.MA12A970539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97054,D2DepositKey.MA12A970549)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97055,D2DepositKey.MA12A970559)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97056,D2DepositKey.MA12A970569)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97057,D2DepositKey.MA12A970579)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97061,D2DepositKey.MA12A970619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97062,D2DepositKey.MA12A970629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97063,D2DepositKey.MA12A970639)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97064,D2DepositKey.MA12A970649)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97065,D2DepositKey.MA12A970659)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97066,D2DepositKey.MA12A970669)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97067,D2DepositKey.MA12A970679)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97071,D2DepositKey.MA12A970719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97072,D2DepositKey.MA12A970729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97073,D2DepositKey.MA12A970739)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97074,D2DepositKey.MA12A970749)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97075,D2DepositKey.MA12A970759)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97076,D2DepositKey.MA12A970769)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97077,D2DepositKey.MA12A970779)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97081,D2DepositKey.MA12A970819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97082,D2DepositKey.MA12A970829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97083,D2DepositKey.MA12A970839)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97084,D2DepositKey.MA12A970849)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97085,D2DepositKey.MA12A970859)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97086,D2DepositKey.MA12A970869)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97087,D2DepositKey.MA12A970879)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97091,D2DepositKey.MA12A970919)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97092,D2DepositKey.MA12A970929)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97093,D2DepositKey.MA12A970939)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97094,D2DepositKey.MA12A970949)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97095,D2DepositKey.MA12A970959)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97096,D2DepositKey.MA12A970969)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97097,D2DepositKey.MA12A970979)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97101,D2DepositKey.MA12A971019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97102,D2DepositKey.MA12A971029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97103,D2DepositKey.MA12A971039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97104,D2DepositKey.MA12A971049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97105,D2DepositKey.MA12A971059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97106,D2DepositKey.MA12A971069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97107,D2DepositKey.MA12A971079)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97111,D2DepositKey.MA12A971119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97112,D2DepositKey.MA12A971129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97113,D2DepositKey.MA12A971139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97114,D2DepositKey.MA12A971149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97115,D2DepositKey.MA12A971159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97116,D2DepositKey.MA12A971169)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97117,D2DepositKey.MA12A971179)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97121,D2DepositKey.MA12A971219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97122,D2DepositKey.MA12A971229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97123,D2DepositKey.MA12A971239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97124,D2DepositKey.MA12A971249)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97125,D2DepositKey.MA12A971259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97126,D2DepositKey.MA12A971269)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97127,D2DepositKey.MA12A971279)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97131,D2DepositKey.MA12A971319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97132,D2DepositKey.MA12A971329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97133,D2DepositKey.MA12A971339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97134,D2DepositKey.MA12A971349)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97135,D2DepositKey.MA12A971359)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97136,D2DepositKey.MA12A971369)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97137,D2DepositKey.MA12A971379)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97141,D2DepositKey.MA12A971419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97142,D2DepositKey.MA12A971429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97143,D2DepositKey.MA12A971439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97144,D2DepositKey.MA12A971449)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97145,D2DepositKey.MA12A971459)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97146,D2DepositKey.MA12A971469)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97147,D2DepositKey.MA12A971479)
	
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97151,D2DepositKey.MA12A971519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97152,D2DepositKey.MA12A971529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97153,D2DepositKey.MA12A971539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97154,D2DepositKey.MA12A971549)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97155,D2DepositKey.MA12A971559)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97156,D2DepositKey.MA12A971569)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97157,D2DepositKey.MA12A971579)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97161,D2DepositKey.MA12A971619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97162,D2DepositKey.MA12A971629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97163,D2DepositKey.MA12A971639)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97164,D2DepositKey.MA12A971649)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97165,D2DepositKey.MA12A971659)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97166,D2DepositKey.MA12A971669)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97167,D2DepositKey.MA12A971679)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97171,D2DepositKey.MA12A971719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97172,D2DepositKey.MA12A971729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97173,D2DepositKey.MA12A971739)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97174,D2DepositKey.MA12A971749)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97175,D2DepositKey.MA12A971759)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97176,D2DepositKey.MA12A971769)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97177,D2DepositKey.MA12A971779)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97181,D2DepositKey.MA12A971819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97182,D2DepositKey.MA12A971829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97183,D2DepositKey.MA12A971839)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97184,D2DepositKey.MA12A971849)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97185,D2DepositKey.MA12A971859)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97186,D2DepositKey.MA12A971869)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97187,D2DepositKey.MA12A971879)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97191,D2DepositKey.MA12A971919)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97192,D2DepositKey.MA12A971929)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97193,D2DepositKey.MA12A971939)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97194,D2DepositKey.MA12A971949)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97195,D2DepositKey.MA12A971959)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97196,D2DepositKey.MA12A971969)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97197,D2DepositKey.MA12A971979)

			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97201,D2DepositKey.MA12A972019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97202,D2DepositKey.MA12A972029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97203,D2DepositKey.MA12A972039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97204,D2DepositKey.MA12A972049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97205,D2DepositKey.MA12A972059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97206,D2DepositKey.MA12A972069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12A97207,D2DepositKey.MA12A972079)
		
	};
	
	private static final IPropertyFiller[] MEM_AP12_34 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97301,D2DepositKey.MA12B973019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97302,D2DepositKey.MA12B973029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97303,D2DepositKey.MA12B973039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97304,D2DepositKey.MA12B973049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97305,D2DepositKey.MA12B973059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97306,D2DepositKey.MA12B973069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97307,D2DepositKey.MA12B973079)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97311,D2DepositKey.MA12B973119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97312,D2DepositKey.MA12B973129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97313,D2DepositKey.MA12B973139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97314,D2DepositKey.MA12B973149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97315,D2DepositKey.MA12B973159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97316,D2DepositKey.MA12B973169)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97317,D2DepositKey.MA12B973179)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97321,D2DepositKey.MA12B973219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97322,D2DepositKey.MA12B973229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97323,D2DepositKey.MA12B973239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97324,D2DepositKey.MA12B973249)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97325,D2DepositKey.MA12B973259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97326,D2DepositKey.MA12B973269)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97327,D2DepositKey.MA12B973279)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97331,D2DepositKey.MA12B973319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97332,D2DepositKey.MA12B973329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97333,D2DepositKey.MA12B973339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97334,D2DepositKey.MA12B973349)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97335,D2DepositKey.MA12B973359)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97336,D2DepositKey.MA12B973369)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97337,D2DepositKey.MA12B973379)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97341,D2DepositKey.MA12B973419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97342,D2DepositKey.MA12B973429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97343,D2DepositKey.MA12B973439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97344,D2DepositKey.MA12B973449)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97345,D2DepositKey.MA12B973459)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97346,D2DepositKey.MA12B973469)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97347,D2DepositKey.MA12B973479)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97351,D2DepositKey.MA12B973519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97352,D2DepositKey.MA12B973529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97353,D2DepositKey.MA12B973539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97354,D2DepositKey.MA12B973549)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97355,D2DepositKey.MA12B973559)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97356,D2DepositKey.MA12B973569)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97357,D2DepositKey.MA12B973579)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97361,D2DepositKey.MA12B973619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97362,D2DepositKey.MA12B973629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97363,D2DepositKey.MA12B973639)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97364,D2DepositKey.MA12B973649)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97365,D2DepositKey.MA12B973659)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97366,D2DepositKey.MA12B973669)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97367,D2DepositKey.MA12B973679)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97371,D2DepositKey.MA12B973719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97372,D2DepositKey.MA12B973729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97373,D2DepositKey.MA12B973739)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97374,D2DepositKey.MA12B973749)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97375,D2DepositKey.MA12B973759)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97376,D2DepositKey.MA12B973769)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97377,D2DepositKey.MA12B973779)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97381,D2DepositKey.MA12B973819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97382,D2DepositKey.MA12B973829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97383,D2DepositKey.MA12B973839)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97384,D2DepositKey.MA12B973849)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97385,D2DepositKey.MA12B973859)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97386,D2DepositKey.MA12B973869)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97387,D2DepositKey.MA12B973879)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97391,D2DepositKey.MA12B973919)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97392,D2DepositKey.MA12B973929)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97393,D2DepositKey.MA12B973939)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97394,D2DepositKey.MA12B973949)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97395,D2DepositKey.MA12B973959)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97396,D2DepositKey.MA12B973969)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97397,D2DepositKey.MA12B973979)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97401,D2DepositKey.MA12B974019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97402,D2DepositKey.MA12B974029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97403,D2DepositKey.MA12B974039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97404,D2DepositKey.MA12B974049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97405,D2DepositKey.MA12B974059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97406,D2DepositKey.MA12B974069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97407,D2DepositKey.MA12B974079)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97411,D2DepositKey.MA12B974119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97412,D2DepositKey.MA12B974129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97413,D2DepositKey.MA12B974139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97414,D2DepositKey.MA12B974149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97415,D2DepositKey.MA12B974159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97416,D2DepositKey.MA12B974169)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97417,D2DepositKey.MA12B974179)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97421,D2DepositKey.MA12B974219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97422,D2DepositKey.MA12B974229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97423,D2DepositKey.MA12B974239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97424,D2DepositKey.MA12B974249)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97425,D2DepositKey.MA12B974259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97426,D2DepositKey.MA12B974269)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97427,D2DepositKey.MA12B974279)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97431,D2DepositKey.MA12B974319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97432,D2DepositKey.MA12B974329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97433,D2DepositKey.MA12B974339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97434,D2DepositKey.MA12B974349)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97435,D2DepositKey.MA12B974359)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97436,D2DepositKey.MA12B974369)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97437,D2DepositKey.MA12B974379)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97441,D2DepositKey.MA12B974419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97442,D2DepositKey.MA12B974429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97443,D2DepositKey.MA12B974439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97444,D2DepositKey.MA12B974449)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97445,D2DepositKey.MA12B974459)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97446,D2DepositKey.MA12B974469)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97447,D2DepositKey.MA12B974479)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97451,D2DepositKey.MA12B974519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97452,D2DepositKey.MA12B974529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97453,D2DepositKey.MA12B974539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97454,D2DepositKey.MA12B974549)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97455,D2DepositKey.MA12B974559)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97456,D2DepositKey.MA12B974569)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97457,D2DepositKey.MA12B974579)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97461,D2DepositKey.MA12B974619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97462,D2DepositKey.MA12B974629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97463,D2DepositKey.MA12B974639)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97464,D2DepositKey.MA12B974649)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97465,D2DepositKey.MA12B974659)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97466,D2DepositKey.MA12B974669)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97467,D2DepositKey.MA12B974679)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97471,D2DepositKey.MA12B974719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97472,D2DepositKey.MA12B974729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97473,D2DepositKey.MA12B974739)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97474,D2DepositKey.MA12B974749)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97475,D2DepositKey.MA12B974759)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97476,D2DepositKey.MA12B974769)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97477,D2DepositKey.MA12B974779)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97481,D2DepositKey.MA12B974819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97482,D2DepositKey.MA12B974829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97483,D2DepositKey.MA12B974839)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97484,D2DepositKey.MA12B974849)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97485,D2DepositKey.MA12B974859)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97486,D2DepositKey.MA12B974869)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97487,D2DepositKey.MA12B974879)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97491,D2DepositKey.MA12B974919)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97492,D2DepositKey.MA12B974929)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97493,D2DepositKey.MA12B974939)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97494,D2DepositKey.MA12B974949)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97495,D2DepositKey.MA12B974959)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97496,D2DepositKey.MA12B974969)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97497,D2DepositKey.MA12B974979)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97501,D2DepositKey.MA12B975019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97502,D2DepositKey.MA12B975029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97503,D2DepositKey.MA12B975039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97504,D2DepositKey.MA12B975049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97505,D2DepositKey.MA12B975059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97506,D2DepositKey.MA12B975069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97507,D2DepositKey.MA12B975079)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97511,D2DepositKey.MA12B975119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97512,D2DepositKey.MA12B975129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97513,D2DepositKey.MA12B975139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97514,D2DepositKey.MA12B975149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97515,D2DepositKey.MA12B975159)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97516,D2DepositKey.MA12B975169)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97517,D2DepositKey.MA12B975179)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97521,D2DepositKey.MA12B975219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97522,D2DepositKey.MA12B975229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97523,D2DepositKey.MA12B975239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97524,D2DepositKey.MA12B975249)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97525,D2DepositKey.MA12B975259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97526,D2DepositKey.MA12B975269)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97527,D2DepositKey.MA12B975279)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97531,D2DepositKey.MA12B975319)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97532,D2DepositKey.MA12B975329)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97533,D2DepositKey.MA12B975339)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97534,D2DepositKey.MA12B975349)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97535,D2DepositKey.MA12B975359)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97536,D2DepositKey.MA12B975369)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97537,D2DepositKey.MA12B975379)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97541,D2DepositKey.MA12B975419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97542,D2DepositKey.MA12B975429)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97543,D2DepositKey.MA12B975439)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97544,D2DepositKey.MA12B975449)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97545,D2DepositKey.MA12B975459)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97546,D2DepositKey.MA12B975469)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97547,D2DepositKey.MA12B975479)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97551,D2DepositKey.MA12B975519)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97552,D2DepositKey.MA12B975529)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97553,D2DepositKey.MA12B975539)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97554,D2DepositKey.MA12B975549)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97555,D2DepositKey.MA12B975559)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97556,D2DepositKey.MA12B975569)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97557,D2DepositKey.MA12B975579)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97561,D2DepositKey.MA12B975619)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97562,D2DepositKey.MA12B975629)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97563,D2DepositKey.MA12B975639)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97564,D2DepositKey.MA12B975649)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97565,D2DepositKey.MA12B975659)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97566,D2DepositKey.MA12B975669)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97567,D2DepositKey.MA12B975679)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97571,D2DepositKey.MA12B975719)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97572,D2DepositKey.MA12B975729)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97573,D2DepositKey.MA12B975739)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97574,D2DepositKey.MA12B975749)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97575,D2DepositKey.MA12B975759)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97576,D2DepositKey.MA12B975769)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97577,D2DepositKey.MA12B975779)
	
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97581,D2DepositKey.MA12B975819)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97582,D2DepositKey.MA12B975829)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97583,D2DepositKey.MA12B975839)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97584,D2DepositKey.MA12B975849)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97585,D2DepositKey.MA12B975859)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97586,D2DepositKey.MA12B975869)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12B97587,D2DepositKey.MA12B975879)
			 
			 //********** PYMES ************
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97591,D2DepositKey.MP12B975919)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97592,D2DepositKey.MP12B975929)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97593,D2DepositKey.MP12B975939)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97594,D2DepositKey.MP12B975949)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97595,D2DepositKey.MP12B975959)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97596,D2DepositKey.MP12B975969)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97597,D2DepositKey.MP12B975979)
	
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97601,D2DepositKey.MP12B976019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97602,D2DepositKey.MP12B976029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97603,D2DepositKey.MP12B976039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97604,D2DepositKey.MP12B976049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97605,D2DepositKey.MP12B976059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97606,D2DepositKey.MP12B976069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MP12B97607,D2DepositKey.MP12B976079)

			 //*****************************
			
	};
	
	private static final IPropertyFiller[] MEM_AP12_5 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97700,D2DepositKey.MA12C977009)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97701,D2DepositKey.MA12C977019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97702,D2DepositKey.MA12C977029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97703,D2DepositKey.MA12C977039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97704,D2DepositKey.MA12C977049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97705,D2DepositKey.MA12C977059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97706,D2DepositKey.MA12C977069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97707,D2DepositKey.MA12C977079)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97708,D2DepositKey.MA12C977089)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97709,D2DepositKey.MA12C977099)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97710,D2DepositKey.MA12C977109)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97711,D2DepositKey.MA12C977119)
			 
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97720,D2DepositKey.MA12C977209)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97721,D2DepositKey.MA12C977219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97722,D2DepositKey.MA12C977229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97723,D2DepositKey.MA12C977239)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97724,D2DepositKey.MA12C977249)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97725,D2DepositKey.MA12C977259)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97726,D2DepositKey.MA12C977269)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97727,D2DepositKey.MA12C977279)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97728,D2DepositKey.MA12C977289)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97729,D2DepositKey.MA12C977299)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97730,D2DepositKey.MA12C977309)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA12C97731,D2DepositKey.MA12C977319)
	};
	
	private static final IPropertyFiller[] MEM_AP13 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398000,D2DepositKey.MA13980009)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398001,D2DepositKey.MA13980019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398002,D2DepositKey.MA13980029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398003,D2DepositKey.MA13980039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398004,D2DepositKey.MA13980049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398005,D2DepositKey.MA13980059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398006,D2DepositKey.MA13980069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA1398007,D2DepositKey.MA13980079)
	};
	
	private static final IPropertyFiller[] MEM_AP14 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199000,D2DepositKey.MA141990009)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199001,D2DepositKey.MA141990019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199002,D2DepositKey.MA141990029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199003,D2DepositKey.MA141990039)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199004,D2DepositKey.MA141990049)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199005,D2DepositKey.MA141990059)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199006,D2DepositKey.MA141990069)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199007,D2DepositKey.MA141990079)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199008,D2DepositKey.MA141990089)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199009,D2DepositKey.MA141990099)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199010,D2DepositKey.MA141990109)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199011,D2DepositKey.MA141990119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199012,D2DepositKey.MA141990129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199013,D2DepositKey.MA141990139)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199014,D2DepositKey.MA141990149)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA14199015,D2DepositKey.MA141990159)
	};

	private static final IPropertyFiller[] MEM_AP15 = new IPropertyFiller[] {
			 (ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947001,D2DepositKey.MA159470019)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947002,D2DepositKey.MA159470029)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947011,D2DepositKey.MA159470119)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947012,D2DepositKey.MA159470129)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947021,D2DepositKey.MA159470219)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947022,D2DepositKey.MA159470229)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947041,D2DepositKey.MA159470419)
			 ,(ctx,mapPrevious) ->  set(ctx,mapPrevious,D2DepositKey.MA15947042,D2DepositKey.MA159470429)

	};
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositHeaderKey, Double> mapPrevious, D2DepositHeaderKey previousKey, D2DepositHeaderKey D2Key) {
		Double dv = mapPrevious.get(previousKey);
		ctx.put(D2Key, dv==null?0.0:dv);
	}
	
	private static void set(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious, D2DepositKey previousKey, D2DepositKey D2Key) {
		Double dv = mapPrevious.get(previousKey);
		ctx.put(D2Key, dv==null?0.0:dv);
	}

	public static void fill(Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositHeaderKey, Double> mapPrevious) {
		fillBalance(ctx, mapPrevious);
		fillPyg(ctx, mapPrevious);
		fillEcpn(ctx, mapPrevious);
		fillEcpn2(ctx, mapPrevious);
	}
	
	public static void fillBalance(Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositHeaderKey, Double> mapPrevious) {
		for (IPropertyFillerHeader filler : BALANCE_ACTIVE_KEYS ) {
			filler.fill(ctx, mapPrevious);
		}
	}
	public static void fillPyg(Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositHeaderKey, Double> mapPrevious) {
		for (IPropertyFillerHeader filler : PYG_ACTIVE_KEYS ) {
			filler.fill(ctx, mapPrevious);
		}
	}
	public static void fillEcpn(Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositHeaderKey, Double> mapPrevious) {
		for (IPropertyFillerHeader filler : ECPN_ACTIVE_KEYS ) {
			filler.fill(ctx, mapPrevious);
		}
	}
	public static void fillEcpn2(Map<D2DepositHeaderKey, Double> ctx, Map<D2DepositHeaderKey, Double> mapPrevious) {
		for (IPropertyFillerHeader filler : ECPN2_ACTIVE_KEYS ) {
			filler.fill(ctx, mapPrevious);
		}
	}
	
	public static void fill2(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		fillMemAP3(ctx, mapPrevious);
		fillMemAP5(ctx, mapPrevious);
		fillMemAP6_1(ctx, mapPrevious);
		fillMemAP6_2(ctx, mapPrevious);
		fillMemAP7(ctx, mapPrevious);
		fillMemAP10(ctx, mapPrevious);
		fillMemAP11(ctx, mapPrevious);
		fillMemAP12_12(ctx, mapPrevious);
		fillMemAP12_34(ctx, mapPrevious);
		fillMemAP12_5(ctx, mapPrevious);
		fillMemAP13(ctx, mapPrevious);
		fillMemAP14(ctx, mapPrevious);
		fillMemAP15(ctx, mapPrevious);
	}
	
	private static void fillMemAP3(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP3) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP5(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP5) {
			filler.fill2(ctx, mapPrevious);
		}
	}

	private static void fillMemAP6_1(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP6_1) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP6_2(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP6_2) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP7(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP7) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP10(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP10) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP11(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP11) {
			filler.fill2(ctx, mapPrevious);
		}
	}

	private static void fillMemAP12_12(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP12_12) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP12_34(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP12_34) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP12_5(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP12_5) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP13(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP13) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP14(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP14) {
			filler.fill2(ctx, mapPrevious);
		}
	}
	
	private static void fillMemAP15(Map<D2DepositKey, Double> ctx, Map<D2DepositKey, Double> mapPrevious) {
		for (IPropertyFiller filler : MEM_AP15) {
			filler.fill2(ctx, mapPrevious);
		}
	}
}
