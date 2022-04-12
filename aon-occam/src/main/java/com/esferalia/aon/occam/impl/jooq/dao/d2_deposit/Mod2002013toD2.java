package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.DoubleVariable2013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;




public class Mod2002013toD2 {
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void fill(Map<D2DepositHeaderKey,Double> map,Mod2002013 mod200);
	}
	

	private static final IPropertyFiller[] BALANCE_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1110009,Mod2002013Key.BA101)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1111009,Mod2002013Key.BA102)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1112009,Mod2002013Key.BA111)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1113009,Mod2002013Key.BA115)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1114009,Mod2002013Key.BA118)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1115009,Mod2002013Key.BA126)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1116009,Mod2002013Key.BA134)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1117009,Mod2002013Key.BA135)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1120009,Mod2002013Key.BA136)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1121009,Mod2002013Key.BA137)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1122009,Mod2002013Key.BA138)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1123009,Mod2002013Key.BA149)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1123809,Mod2002013Key.BA150)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1123819,Mod2002013Key.BA151)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1123829,Mod2002013Key.BA152)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1123709,Mod2002013Key.BA158)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1123909,Mod2002013Key.BA159)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1124009,Mod2002013Key.BA160)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1125009,Mod2002013Key.BA168)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1126009,Mod2002013Key.BA176)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1127009,Mod2002013Key.BA177)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA1100009,Mod2002013Key.BA180)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21200009,Mod2002013Key.BP185)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21210009,Mod2002013Key.BP186)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21211009,Mod2002013Key.BP187)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21211109,Mod2002013Key.BP188)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21211209,Mod2002013Key.BP189)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21212009,Mod2002013Key.BP190)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21213009,Mod2002013Key.BP191)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21214009,Mod2002013Key.BP194)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21215009,Mod2002013Key.BP195)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21216009,Mod2002013Key.BP198)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21217009,Mod2002013Key.BP199)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21218009,Mod2002013Key.BP200)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21219009,Mod2002013Key.BP201)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21220009,Mod2002013Key.BP202)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21230009,Mod2002013Key.BP209)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21310009,Mod2002013Key.BP210)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21311009,Mod2002013Key.BP211)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21312009,Mod2002013Key.BP216)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21312209,Mod2002013Key.BP218)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21312309,Mod2002013Key.BP219)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21312909,Mod2002013Key.BP222)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21313009,Mod2002013Key.BP223)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21314009,Mod2002013Key.BP224)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21315009,Mod2002013Key.BP225)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21316009,Mod2002013Key.BP226)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA21317009,Mod2002013Key.BP227)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22320009,Mod2002013Key.BP228)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22321009,Mod2002013Key.BP229)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22322009,Mod2002013Key.BP230)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22323009,Mod2002013Key.BP231)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22323209,Mod2002013Key.BP233)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22323309,Mod2002013Key.BP234)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22323909,Mod2002013Key.BP237)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22324009,Mod2002013Key.BP238)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22325009,Mod2002013Key.BP239)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22325809,Mod2002013Key.BP240)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22325819,Mod2002013Key.BP241)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22325829,Mod2002013Key.BP242)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22325909,Mod2002013Key.BP249)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22326009,Mod2002013Key.BP250)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22327009,Mod2002013Key.BP251)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA22300009,Mod2002013Key.BP252)
	};
		
	private static final IPropertyFiller[] PYG_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA401009,Mod2002013Key.PG255)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA402009,Mod2002013Key.PG258)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA403009,Mod2002013Key.PG259)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA404009,Mod2002013Key.PG260)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA405009,Mod2002013Key.PG265)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA406009,Mod2002013Key.PG270)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA407009,Mod2002013Key.PG279)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA408009,Mod2002013Key.PG284)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA409009,Mod2002013Key.PG285)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA410009,Mod2002013Key.PG286)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA411009,Mod2002013Key.PG287)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA412009,Mod2002013Key.PG294)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA413009,Mod2002013Key.PG295)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA491009,Mod2002013Key.PG296)
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA414009,Mod2002013Key.PG297)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA414309,Mod2002013Key.PG304)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA414909,new  Mod2002013Key[]
				{Mod2002013Key.PG298,Mod2002013Key.PG301})
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA415009,Mod2002013Key.PG305)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA416009,Mod2002013Key.PG309)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA417009,Mod2002013Key.PG312)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA418009,Mod2002013Key.PG313)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA492009,Mod2002013Key.PG324)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA493009,Mod2002013Key.PG325)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA419009,Mod2002013Key.PG326)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA495009,Mod2002013Key.PG327)
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA421009,Mod2002013Key.PG329)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA421109,Mod2002013Key.PG330)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA421209,Mod2002013Key.PG331)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA421309,Mod2002013Key.PG332)
	};
		
	private static final IPropertyFiller[] ECPN_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1591009,Mod2002013Key.T0500)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500109,Mod2002013Key.T0336)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500209,Mod2002013Key.T0339)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500309,Mod2002013Key.T0340)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500409,Mod2002013Key.T0341)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500509,Mod2002013Key.T0342)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500609,Mod2002013Key.T0343)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500709,Mod2002013Key.T0344)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1592009,Mod2002013Key.T0345)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500809,Mod2002013Key.T0346)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1500909,Mod2002013Key.T0349)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1501009,Mod2002013Key.T0350)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1501109,Mod2002013Key.T0351)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1501209,Mod2002013Key.T0352)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1501309,Mod2002013Key.T0353)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1593009,Mod2002013Key.T0354)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA1594009,Mod2002013Key.T0355)
	};
	private static final IPropertyFiller[] ECPN2_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511019,Mod2002013Key.TC380)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511029,Mod2002013Key.TC381)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511039,Mod2002013Key.TC382)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511049,Mod2002013Key.TC383)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511059,Mod2002013Key.TC384)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511069,Mod2002013Key.TC385)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511079,Mod2002013Key.TC386)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511089,Mod2002013Key.TC387)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511099,Mod2002013Key.TC388)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511109,Mod2002013Key.TC389)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511119,Mod2002013Key.TC390)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511129,Mod2002013Key.TC392)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2511139,Mod2002013Key.TC393)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512019,Mod2002013Key.TC394)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512029,Mod2002013Key.TC395)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512039,Mod2002013Key.TC396)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512049,Mod2002013Key.TC397)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512059,Mod2002013Key.TC398)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512069,Mod2002013Key.TC399)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512079,Mod2002013Key.TC400)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512089,Mod2002013Key.TC401)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512099,Mod2002013Key.TC402)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512109,Mod2002013Key.TC403)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512119,Mod2002013Key.TC404)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512129,Mod2002013Key.TC406)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2512139,Mod2002013Key.TC407)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513019,Mod2002013Key.TC408)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513029,Mod2002013Key.TC409)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513039,Mod2002013Key.TC410)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513049,Mod2002013Key.TC411)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513059,Mod2002013Key.TC412)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513069,Mod2002013Key.TC413)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513079,Mod2002013Key.TC414)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513089,Mod2002013Key.TC415)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513099,Mod2002013Key.TC416)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513109,Mod2002013Key.TC417)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513119,Mod2002013Key.TC418)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513129,Mod2002013Key.TC420)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2513139,Mod2002013Key.TC421)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514019,Mod2002013Key.TC422)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514029,Mod2002013Key.TC423)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514039,Mod2002013Key.TC424)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514049,Mod2002013Key.TC425)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514059,Mod2002013Key.TC426)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514069,Mod2002013Key.TC427)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514079,Mod2002013Key.TC428)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514089,Mod2002013Key.TC429)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514099,Mod2002013Key.TC430)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514109,Mod2002013Key.TC431)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514119,Mod2002013Key.TC432)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514129,Mod2002013Key.TC434)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2514139,Mod2002013Key.TC435)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528019,Mod2002013Key.TC450)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528029,Mod2002013Key.TC451)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528039,Mod2002013Key.TC452)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528049,Mod2002013Key.TC453)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528059,Mod2002013Key.TC454)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528069,Mod2002013Key.TC455)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528079,Mod2002013Key.TC456)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528089,Mod2002013Key.TC457)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528099,Mod2002013Key.TC458)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528119,Mod2002013Key.TC461)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528129,Mod2002013Key.TC462)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1528139,Mod2002013Key.TC463)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530019,Mod2002013Key.TC464)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530029,Mod2002013Key.TC465)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530039,Mod2002013Key.TC466)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530049,Mod2002013Key.TC467)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530059,Mod2002013Key.TC468)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530069,Mod2002013Key.TC469)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530079,Mod2002013Key.TC470)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530089,Mod2002013Key.TC471)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530099,Mod2002013Key.TC472)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530119,Mod2002013Key.TC475)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530129,Mod2002013Key.TC476)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1530139,Mod2002013Key.TC477)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527019,Mod2002013Key.TC478)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527029,Mod2002013Key.TC479)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527039,Mod2002013Key.TC480)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527049,Mod2002013Key.TC481)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527059,Mod2002013Key.TC482)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527069,Mod2002013Key.TC483)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527079,Mod2002013Key.TC484)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527089,Mod2002013Key.TC485)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527099,Mod2002013Key.TC486)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527119,Mod2002013Key.TC489)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527129,Mod2002013Key.TC490)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1527139,Mod2002013Key.TC491)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529019,Mod2002013Key.TC492)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529029,Mod2002013Key.TC493)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529039,Mod2002013Key.TC494)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529049,Mod2002013Key.TC495)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529059,Mod2002013Key.TC496)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529069,Mod2002013Key.TC497)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529079,Mod2002013Key.TC498)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529089,Mod2002013Key.TC499)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529099,Mod2002013Key.TC502)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529119,Mod2002013Key.TC503)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529129,Mod2002013Key.TC504)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP1529139,Mod2002013Key.TC505)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515019,Mod2002013Key.TC436)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515029,Mod2002013Key.TC437)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515039,Mod2002013Key.TC438)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515049,Mod2002013Key.TC439)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515059,Mod2002013Key.TC440)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515069,Mod2002013Key.TC441)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515079,Mod2002013Key.TC442)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515089,Mod2002013Key.TC443)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515099,Mod2002013Key.TC444)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515109,Mod2002013Key.TC445)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515119,Mod2002013Key.TC446)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515129,Mod2002013Key.TC448)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2515139,Mod2002013Key.TC449)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516019,Mod2002013Key.TC506)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516029,Mod2002013Key.TC507)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516039,Mod2002013Key.TC508)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516049,Mod2002013Key.TC509)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516059,Mod2002013Key.TC510)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516069,Mod2002013Key.TC511)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516079,Mod2002013Key.TC512)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516089,Mod2002013Key.TC513)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516099,Mod2002013Key.TC514)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516109,Mod2002013Key.TC515)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516119,Mod2002013Key.TC516)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516129,Mod2002013Key.TC518)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2516139,Mod2002013Key.TC519)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517019,Mod2002013Key.TC520)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517029,Mod2002013Key.TC521)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517039,Mod2002013Key.TC522)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517049,Mod2002013Key.TC523)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517059,Mod2002013Key.TC524)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517069,Mod2002013Key.TC525)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517079,Mod2002013Key.TC526)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517089,Mod2002013Key.TC527)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517099,Mod2002013Key.TC528)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517109,Mod2002013Key.TC529)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517119,Mod2002013Key.TC530)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517129,Mod2002013Key.TC532)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2517139,Mod2002013Key.TC533)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518019,Mod2002013Key.TC534)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518029,Mod2002013Key.TC535)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518039,Mod2002013Key.TC536)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518049,Mod2002013Key.TC537)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518059,Mod2002013Key.TC538)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518069,Mod2002013Key.TC539)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518079,Mod2002013Key.TC540)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518089,Mod2002013Key.TC541)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518099,Mod2002013Key.TC542)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518109,Mod2002013Key.TC543)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518119,Mod2002013Key.TC544)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518129,Mod2002013Key.TC546)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2518139,Mod2002013Key.TC547)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526019,Mod2002013Key.TC604)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526029,Mod2002013Key.TC605)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526039,Mod2002013Key.TC606)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526049,Mod2002013Key.TC607)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526059,Mod2002013Key.TC608)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526069,Mod2002013Key.TC609)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526079,Mod2002013Key.TC610)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526089,Mod2002013Key.TC611)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526099,Mod2002013Key.TC612)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526109,Mod2002013Key.TC613)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526119,Mod2002013Key.TC614)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526129,Mod2002013Key.TC616)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2526139,Mod2002013Key.TC617)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524019,Mod2002013Key.TC618)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524029,Mod2002013Key.TC619)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524039,Mod2002013Key.TC620)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524049,Mod2002013Key.TC621)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524059,Mod2002013Key.TC622)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524069,Mod2002013Key.TC623)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524079,Mod2002013Key.TC624)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524089,Mod2002013Key.TC625)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524099,Mod2002013Key.TC626)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524109,Mod2002013Key.TC627)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524119,Mod2002013Key.TC628)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524129,Mod2002013Key.TC630)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2524139,Mod2002013Key.TC631)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531019,Mod2002013Key.TC715)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531029,Mod2002013Key.TC716)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531039,Mod2002013Key.TC717)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531049,Mod2002013Key.TC718)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531059,Mod2002013Key.TC719)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531069,Mod2002013Key.TC720)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531079,Mod2002013Key.TC721)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531089,Mod2002013Key.TC722)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531099,Mod2002013Key.TC723)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531109,Mod2002013Key.TC724)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531119,Mod2002013Key.TC725)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531129,Mod2002013Key.TC727)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2531139,Mod2002013Key.TC728)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532019,Mod2002013Key.TC729)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532029,Mod2002013Key.TC730)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532039,Mod2002013Key.TC731)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532049,Mod2002013Key.TC732)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532059,Mod2002013Key.TC733)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532069,Mod2002013Key.TC734)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532079,Mod2002013Key.TC735)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532089,Mod2002013Key.TC736)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532099,Mod2002013Key.TC737)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532109,Mod2002013Key.TC738)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532119,Mod2002013Key.TC739)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532129,Mod2002013Key.TC741)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA2532139,Mod2002013Key.TC742)
		
	};
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002013 mod200, D2DepositHeaderKey D2Key, Mod2002013Key mod200Key) {
		DoubleVariable2013 dv = mod200.getVariable(mod200Key);
		ctx.put(D2Key, dv==null?0.0:dv.getValue());
	} 
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002013 mod200, D2DepositHeaderKey D2Key, Mod2002013Key[] mod200Keys) {
		Double d = 0.0;
		for(Integer i = 0; i< mod200Keys.length; i++){
			DoubleVariable2013 dv = mod200.getVariable(mod200Keys[i]);
			d = d + (dv == null ? 0.0 : dv.getValue());
		}
		ctx.put(D2Key, d);
	} 

	public static void fill(Map<D2DepositHeaderKey, Double> ctx, Mod2002013 mod200) {
		fillBalance(ctx, mod200);
		fillPyg(ctx, mod200);
		fillEcpn(ctx, mod200);
		fillEcpn2(ctx, mod200);
	}
	public static void fillBalance(Map<D2DepositHeaderKey, Double> ctx, Mod2002013 mod200) {
		for (IPropertyFiller filler : BALANCE_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillPyg(Map<D2DepositHeaderKey, Double> ctx, Mod2002013 mod200) {
		for (IPropertyFiller filler : PYG_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillEcpn(Map<D2DepositHeaderKey, Double> ctx, Mod2002013 mod200) {
		for (IPropertyFiller filler : ECPN_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillEcpn2(Map<D2DepositHeaderKey, Double> ctx, Mod2002013 mod200) {
		for (IPropertyFiller filler : ECPN2_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}

}
