package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.DoubleVariable2018;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Key;
public class Mod2002018toD2 {
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void fill(Map<D2DepositHeaderKey,Double> map,Mod2002018 mod200, Boolean act);
	}
	
	private static final IPropertyFiller[] BALANCE_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111000 : D2DepositHeaderKey.BA1110009, Mod2002018Key.BA101)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111100 : D2DepositHeaderKey.BA1111009, Mod2002018Key.BA102)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111200 : D2DepositHeaderKey.BA1112009, Mod2002018Key.BA111)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111300 : D2DepositHeaderKey.BA1113009, Mod2002018Key.BA115)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111400 : D2DepositHeaderKey.BA1114009, Mod2002018Key.BA118)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111500 : D2DepositHeaderKey.BA1115009, Mod2002018Key.BA126)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111600 : D2DepositHeaderKey.BA1116009, Mod2002018Key.BA134)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA111700 : D2DepositHeaderKey.BA1117009, Mod2002018Key.BA135)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112000 : D2DepositHeaderKey.BA1120009, Mod2002018Key.BA136)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112100 : D2DepositHeaderKey.BA1121009, Mod2002018Key.BA137)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112200 : D2DepositHeaderKey.BA1122009, Mod2002018Key.BA138)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112300 : D2DepositHeaderKey.BA1123009, Mod2002018Key.BA149)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112380 : D2DepositHeaderKey.BA1123809, Mod2002018Key.BA150)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112381 : D2DepositHeaderKey.BA1123819, Mod2002018Key.BA151)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112382 : D2DepositHeaderKey.BA1123829, Mod2002018Key.BA152)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112370 : D2DepositHeaderKey.BA1123709, Mod2002018Key.BA158)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112390 : D2DepositHeaderKey.BA1123909, Mod2002018Key.BA159)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112400 : D2DepositHeaderKey.BA1124009, Mod2002018Key.BA160)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112500 : D2DepositHeaderKey.BA1125009, Mod2002018Key.BA168)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112600 : D2DepositHeaderKey.BA1126009, Mod2002018Key.BA176)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA112700 : D2DepositHeaderKey.BA1127009, Mod2002018Key.BA177)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA110000 : D2DepositHeaderKey.BA1100009, Mod2002018Key.BA180)
		 
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2120000 : D2DepositHeaderKey.BA21200009, Mod2002018Key.BP185)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121000 : D2DepositHeaderKey.BA21210009, Mod2002018Key.BP186)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121100 : D2DepositHeaderKey.BA21211009, Mod2002018Key.BP187)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121110 : D2DepositHeaderKey.BA21211109, Mod2002018Key.BP188)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121120 : D2DepositHeaderKey.BA21211209, Mod2002018Key.BP189)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121200 : D2DepositHeaderKey.BA21212009, Mod2002018Key.BP190)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121300 : D2DepositHeaderKey.BA21213009, Mod2002018Key.BP191)
		 
		// TODO 
		//********************************************************************************/
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121350 : D2DepositHeaderKey.BA21213509, Mod2002018Key.BP1001)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121360 : D2DepositHeaderKey.BA21213609, new Mod2002018Key[]
				{Mod2002018Key.BP1002, Mod2002018Key.BP193, Mod2002018Key.BP192, Mod2002018Key.BP702})
		//********************************************************************************/
		 
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121400 : D2DepositHeaderKey.BA21214009, Mod2002018Key.BP194)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121500 : D2DepositHeaderKey.BA21215009, Mod2002018Key.BP195)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121600 : D2DepositHeaderKey.BA21216009, Mod2002018Key.BP198)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121700 : D2DepositHeaderKey.BA21217009, Mod2002018Key.BP199)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121800 : D2DepositHeaderKey.BA21218009, Mod2002018Key.BP200)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2121900 : D2DepositHeaderKey.BA21219009, Mod2002018Key.BP201)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2122000 : D2DepositHeaderKey.BA21220009, Mod2002018Key.BP202)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2123000 : D2DepositHeaderKey.BA21230009, Mod2002018Key.BP209)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131000 : D2DepositHeaderKey.BA21310009, Mod2002018Key.BP210)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131100 : D2DepositHeaderKey.BA21311009, Mod2002018Key.BP211)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131200 : D2DepositHeaderKey.BA21312009, Mod2002018Key.BP216)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131220 : D2DepositHeaderKey.BA21312209, Mod2002018Key.BP218)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131230 : D2DepositHeaderKey.BA21312309, Mod2002018Key.BP219)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131290 : D2DepositHeaderKey.BA21312909, Mod2002018Key.BP222)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131300 : D2DepositHeaderKey.BA21313009, Mod2002018Key.BP223)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131400 : D2DepositHeaderKey.BA21314009, Mod2002018Key.BP224)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131500 : D2DepositHeaderKey.BA21315009, Mod2002018Key.BP225)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131600 : D2DepositHeaderKey.BA21316009, Mod2002018Key.BP226)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2131700 : D2DepositHeaderKey.BA21317009, Mod2002018Key.BP227)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232000 : D2DepositHeaderKey.BA22320009, Mod2002018Key.BP228)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232100 : D2DepositHeaderKey.BA22321009, Mod2002018Key.BP229)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232200 : D2DepositHeaderKey.BA22322009, Mod2002018Key.BP230)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232300 : D2DepositHeaderKey.BA22323009, Mod2002018Key.BP231)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232320 : D2DepositHeaderKey.BA22323209, Mod2002018Key.BP233)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232330 : D2DepositHeaderKey.BA22323309, Mod2002018Key.BP234)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232390 : D2DepositHeaderKey.BA22323909, Mod2002018Key.BP237)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232400 : D2DepositHeaderKey.BA22324009, Mod2002018Key.BP238)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232500 : D2DepositHeaderKey.BA22325009, Mod2002018Key.BP239)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232580 : D2DepositHeaderKey.BA22325809, Mod2002018Key.BP240)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232581 : D2DepositHeaderKey.BA22325819, Mod2002018Key.BP241)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232582 : D2DepositHeaderKey.BA22325829, Mod2002018Key.BP242)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232590 : D2DepositHeaderKey.BA22325909, Mod2002018Key.BP249)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232600 : D2DepositHeaderKey.BA22326009, Mod2002018Key.BP250)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2232700 : D2DepositHeaderKey.BA22327009, Mod2002018Key.BP251)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.BA2230000 : D2DepositHeaderKey.BA22300009, Mod2002018Key.BP252)
	};	

	private static final IPropertyFiller[] PYG_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40100 : D2DepositHeaderKey.PA401009, Mod2002018Key.PG255)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40200 : D2DepositHeaderKey.PA402009, Mod2002018Key.PG258)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40300 : D2DepositHeaderKey.PA403009, Mod2002018Key.PG259)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40400 : D2DepositHeaderKey.PA404009, Mod2002018Key.PG260)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40500 : D2DepositHeaderKey.PA404009, Mod2002018Key.PG265)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40600 : D2DepositHeaderKey.PA406009, Mod2002018Key.PG270)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40700 : D2DepositHeaderKey.PA407009, Mod2002018Key.PG279)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40800 : D2DepositHeaderKey.PA408009, Mod2002018Key.PG284)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA40900 : D2DepositHeaderKey.PA409009, Mod2002018Key.PG285)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41000 : D2DepositHeaderKey.PA410009, Mod2002018Key.PG286)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41100 : D2DepositHeaderKey.PA411009, Mod2002018Key.PG287)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41200 : D2DepositHeaderKey.PA412009, Mod2002018Key.PG294)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41300 : D2DepositHeaderKey.PA413009, Mod2002018Key.PG295)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA49100 : D2DepositHeaderKey.PA491009, Mod2002018Key.PG296)
		  
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41400 : D2DepositHeaderKey.PA414009, Mod2002018Key.PG297)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41430 : D2DepositHeaderKey.PA414309, Mod2002018Key.PG304)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41490 : D2DepositHeaderKey.PA414909, new Mod2002018Key[]
				{Mod2002018Key.PG298, Mod2002018Key.PG301})
		 
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41500 : D2DepositHeaderKey.PA415009, Mod2002018Key.PG305)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41600 : D2DepositHeaderKey.PA416009, Mod2002018Key.PG309)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41700 : D2DepositHeaderKey.PA417009, Mod2002018Key.PG312)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41800 : D2DepositHeaderKey.PA418009, Mod2002018Key.PG313)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA49200 : D2DepositHeaderKey.PA492009, Mod2002018Key.PG324)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA49300 : D2DepositHeaderKey.PA493009, Mod2002018Key.PG325)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA41900 : D2DepositHeaderKey.PA419009, Mod2002018Key.PG326)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA49500 : D2DepositHeaderKey.PA495009, Mod2002018Key.PG327)
		  
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA42100 : D2DepositHeaderKey.PA421009, Mod2002018Key.PG329)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA42110 : D2DepositHeaderKey.PA421109, Mod2002018Key.PG330)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA42120 : D2DepositHeaderKey.PA421209, Mod2002018Key.PG331)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PA42130 : D2DepositHeaderKey.PA421309, Mod2002018Key.PG332)
	};
	
	private static final IPropertyFiller[] ECPN_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA159100 : D2DepositHeaderKey.PNA1591009, Mod2002018Key.T0500)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150010 : D2DepositHeaderKey.PNA1500109, Mod2002018Key.T0336)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150020 : D2DepositHeaderKey.PNA1500209, Mod2002018Key.T0339)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150030 : D2DepositHeaderKey.PNA1500309, Mod2002018Key.T0340)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150040 : D2DepositHeaderKey.PNA1500409, Mod2002018Key.T0341)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150050 : D2DepositHeaderKey.PNA1500509, Mod2002018Key.T0342)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150060 : D2DepositHeaderKey.PNA1500609, Mod2002018Key.T0343)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150070 : D2DepositHeaderKey.PNA1500709, Mod2002018Key.T0344)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA159200 : D2DepositHeaderKey.PNA1592009, Mod2002018Key.T0345)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150080 : D2DepositHeaderKey.PNA1500809, Mod2002018Key.T0346)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150090 : D2DepositHeaderKey.PNA1500909, Mod2002018Key.T0349)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150100 : D2DepositHeaderKey.PNA1501009, Mod2002018Key.T0350)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150110 : D2DepositHeaderKey.PNA1501109, Mod2002018Key.T0351)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150120 : D2DepositHeaderKey.PNA1501209, Mod2002018Key.T0352)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA150130 : D2DepositHeaderKey.PNA1501309, Mod2002018Key.T0353)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA159300 : D2DepositHeaderKey.PNA1593009, Mod2002018Key.T0354)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA159400 : D2DepositHeaderKey.PNA1594009, Mod2002018Key.T0355)
	};

	private static final IPropertyFiller[] ECPN2_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251101 : D2DepositHeaderKey.PNA2511019, Mod2002018Key.TC380)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251102 : D2DepositHeaderKey.PNA2511029, Mod2002018Key.TC381)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251103 : D2DepositHeaderKey.PNA2511039, Mod2002018Key.TC382)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251104 : D2DepositHeaderKey.PNA2511049, Mod2002018Key.TC383)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251105 : D2DepositHeaderKey.PNA2511059, Mod2002018Key.TC384)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251106 : D2DepositHeaderKey.PNA2511069, Mod2002018Key.TC385)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251107 : D2DepositHeaderKey.PNA2511079, Mod2002018Key.TC386)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251108 : D2DepositHeaderKey.PNA2511089, Mod2002018Key.TC387)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251109 : D2DepositHeaderKey.PNA2511099, Mod2002018Key.TC388)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251110 : D2DepositHeaderKey.PNA2511109, Mod2002018Key.TC389)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251111 : D2DepositHeaderKey.PNA2511119, Mod2002018Key.TC390)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251112 : D2DepositHeaderKey.PNA2511129, Mod2002018Key.TC392)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251113 : D2DepositHeaderKey.PNA2511139, Mod2002018Key.TC393)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251201 : D2DepositHeaderKey.PNA2512019, Mod2002018Key.TC394)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251202 : D2DepositHeaderKey.PNA2512029, Mod2002018Key.TC395)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251203 : D2DepositHeaderKey.PNA2512039, Mod2002018Key.TC396)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251204 : D2DepositHeaderKey.PNA2512049, Mod2002018Key.TC397)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251205 : D2DepositHeaderKey.PNA2512059, Mod2002018Key.TC398)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251206 : D2DepositHeaderKey.PNA2512069, Mod2002018Key.TC399)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251207 : D2DepositHeaderKey.PNA2512079, Mod2002018Key.TC400)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251208 : D2DepositHeaderKey.PNA2512089, Mod2002018Key.TC401)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251209 : D2DepositHeaderKey.PNA2512099, Mod2002018Key.TC402)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251210 : D2DepositHeaderKey.PNA2512109, Mod2002018Key.TC403)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251211 : D2DepositHeaderKey.PNA2512119, Mod2002018Key.TC404)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251212 : D2DepositHeaderKey.PNA2512129, Mod2002018Key.TC406)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251213 : D2DepositHeaderKey.PNA2512139, Mod2002018Key.TC407)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251301 : D2DepositHeaderKey.PNA2513019, Mod2002018Key.TC408)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251302 : D2DepositHeaderKey.PNA2513029, Mod2002018Key.TC409)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251303 : D2DepositHeaderKey.PNA2513039, Mod2002018Key.TC410)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251304 : D2DepositHeaderKey.PNA2513049, Mod2002018Key.TC411)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251305 : D2DepositHeaderKey.PNA2513059, Mod2002018Key.TC412)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251306 : D2DepositHeaderKey.PNA2513069, Mod2002018Key.TC413)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251307 : D2DepositHeaderKey.PNA2513079, Mod2002018Key.TC414)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251308 : D2DepositHeaderKey.PNA2513089, Mod2002018Key.TC415)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251309 : D2DepositHeaderKey.PNA2513099, Mod2002018Key.TC416)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251310 : D2DepositHeaderKey.PNA2513109, Mod2002018Key.TC417)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251311 : D2DepositHeaderKey.PNA2513119, Mod2002018Key.TC418)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251312 : D2DepositHeaderKey.PNA2513129, Mod2002018Key.TC420)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251313 : D2DepositHeaderKey.PNA2513139, Mod2002018Key.TC421)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251401 : D2DepositHeaderKey.PNA2514019, Mod2002018Key.TC422)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251402 : D2DepositHeaderKey.PNA2514029, Mod2002018Key.TC423)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251403 : D2DepositHeaderKey.PNA2514039, Mod2002018Key.TC424)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251404 : D2DepositHeaderKey.PNA2514049, Mod2002018Key.TC425)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251405 : D2DepositHeaderKey.PNA2514059, Mod2002018Key.TC426)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251406 : D2DepositHeaderKey.PNA2514069, Mod2002018Key.TC427)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251407 : D2DepositHeaderKey.PNA2514079, Mod2002018Key.TC428)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251408 : D2DepositHeaderKey.PNA2514089, Mod2002018Key.TC429)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251409 : D2DepositHeaderKey.PNA2514099, Mod2002018Key.TC430)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251410 : D2DepositHeaderKey.PNA2514109, Mod2002018Key.TC431)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251411 : D2DepositHeaderKey.PNA2514119, Mod2002018Key.TC432)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251412 : D2DepositHeaderKey.PNA2514129, Mod2002018Key.TC434)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251413 : D2DepositHeaderKey.PNA2514139, Mod2002018Key.TC435)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152801 : D2DepositHeaderKey.PNP1528019, Mod2002018Key.TC450)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152802 : D2DepositHeaderKey.PNP1528029, Mod2002018Key.TC451)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152803 : D2DepositHeaderKey.PNP1528039, Mod2002018Key.TC452)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152804 : D2DepositHeaderKey.PNP1528049, Mod2002018Key.TC453)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152805 : D2DepositHeaderKey.PNP1528059, Mod2002018Key.TC454)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152806 : D2DepositHeaderKey.PNP1528069, Mod2002018Key.TC455)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152807 : D2DepositHeaderKey.PNP1528079, Mod2002018Key.TC456)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152808 : D2DepositHeaderKey.PNP1528089, Mod2002018Key.TC457)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152809 : D2DepositHeaderKey.PNP1528099, Mod2002018Key.TC458)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152811 : D2DepositHeaderKey.PNP1528119, Mod2002018Key.TC461)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152812 : D2DepositHeaderKey.PNP1528129, Mod2002018Key.TC462)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152813 : D2DepositHeaderKey.PNP1528139, Mod2002018Key.TC463)
  
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153001 : D2DepositHeaderKey.PNP1530019, Mod2002018Key.TC464)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153002 : D2DepositHeaderKey.PNP1530029, Mod2002018Key.TC465)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153003 : D2DepositHeaderKey.PNP1530039, Mod2002018Key.TC466)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153004 : D2DepositHeaderKey.PNP1530049, Mod2002018Key.TC467)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153005 : D2DepositHeaderKey.PNP1530059, Mod2002018Key.TC468)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153006 : D2DepositHeaderKey.PNP1530069, Mod2002018Key.TC469)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153007 : D2DepositHeaderKey.PNP1530079, Mod2002018Key.TC470)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153008 : D2DepositHeaderKey.PNP1530089, Mod2002018Key.TC471)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153009 : D2DepositHeaderKey.PNP1530099, Mod2002018Key.TC472)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153011 : D2DepositHeaderKey.PNP1530119, Mod2002018Key.TC475)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153012 : D2DepositHeaderKey.PNP1530129, Mod2002018Key.TC476)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP153013 : D2DepositHeaderKey.PNP1530139, Mod2002018Key.TC477)  

		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152701 : D2DepositHeaderKey.PNP1527019, Mod2002018Key.TC478)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152702 : D2DepositHeaderKey.PNP1527029, Mod2002018Key.TC479)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152703 : D2DepositHeaderKey.PNP1527039, Mod2002018Key.TC480)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152704 : D2DepositHeaderKey.PNP1527049, Mod2002018Key.TC481)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152705 : D2DepositHeaderKey.PNP1527059, Mod2002018Key.TC482)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152706 : D2DepositHeaderKey.PNP1527069, Mod2002018Key.TC483)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152707 : D2DepositHeaderKey.PNP1527079, Mod2002018Key.TC484)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152708 : D2DepositHeaderKey.PNP1527089, Mod2002018Key.TC485)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152709 : D2DepositHeaderKey.PNP1527099, Mod2002018Key.TC486)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152711 : D2DepositHeaderKey.PNP1527119, Mod2002018Key.TC489)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152712 : D2DepositHeaderKey.PNP1527129, Mod2002018Key.TC490)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152713 : D2DepositHeaderKey.PNP1527139, Mod2002018Key.TC491)

		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152901 : D2DepositHeaderKey.PNP1529019, Mod2002018Key.TC492)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152902 : D2DepositHeaderKey.PNP1529029, Mod2002018Key.TC493)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152903 : D2DepositHeaderKey.PNP1529039, Mod2002018Key.TC494)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152904 : D2DepositHeaderKey.PNP1529049, Mod2002018Key.TC495)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152905 : D2DepositHeaderKey.PNP1529059, Mod2002018Key.TC496)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152906 : D2DepositHeaderKey.PNP1529069, Mod2002018Key.TC497)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152907 : D2DepositHeaderKey.PNP1529079, Mod2002018Key.TC498)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152908 : D2DepositHeaderKey.PNP1529089, Mod2002018Key.TC499)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152909 : D2DepositHeaderKey.PNP1529099, Mod2002018Key.TC502)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152911 : D2DepositHeaderKey.PNP1529119, Mod2002018Key.TC503)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152912 : D2DepositHeaderKey.PNP1529129, Mod2002018Key.TC504)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNP152913 : D2DepositHeaderKey.PNP1529139, Mod2002018Key.TC505)
   
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251501 : D2DepositHeaderKey.PNA2515019, Mod2002018Key.TC436)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251502 : D2DepositHeaderKey.PNA2515029, Mod2002018Key.TC437)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251503 : D2DepositHeaderKey.PNA2515039, Mod2002018Key.TC438)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251504 : D2DepositHeaderKey.PNA2515049, Mod2002018Key.TC439)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251505 : D2DepositHeaderKey.PNA2515059, Mod2002018Key.TC440)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251506 : D2DepositHeaderKey.PNA2515069, Mod2002018Key.TC441)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251507 : D2DepositHeaderKey.PNA2515079, Mod2002018Key.TC442)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251508 : D2DepositHeaderKey.PNA2515089, Mod2002018Key.TC443)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251509 : D2DepositHeaderKey.PNA2515099, Mod2002018Key.TC444)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251510 : D2DepositHeaderKey.PNA2515109, Mod2002018Key.TC445)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251511 : D2DepositHeaderKey.PNA2515119, Mod2002018Key.TC446)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251512 : D2DepositHeaderKey.PNA2515129, Mod2002018Key.TC448)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251513 : D2DepositHeaderKey.PNA2515139, Mod2002018Key.TC449)
		  
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251601 : D2DepositHeaderKey.PNA2516019, Mod2002018Key.TC506)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251602 : D2DepositHeaderKey.PNA2516029, Mod2002018Key.TC507)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251603 : D2DepositHeaderKey.PNA2516039, Mod2002018Key.TC508)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251604 : D2DepositHeaderKey.PNA2516049, Mod2002018Key.TC509)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251605 : D2DepositHeaderKey.PNA2516059, Mod2002018Key.TC510)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251606 : D2DepositHeaderKey.PNA2516069, Mod2002018Key.TC511)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251607 : D2DepositHeaderKey.PNA2516079, Mod2002018Key.TC512)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251608 : D2DepositHeaderKey.PNA2516089, Mod2002018Key.TC513)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251609 : D2DepositHeaderKey.PNA2516099, Mod2002018Key.TC514)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251610 : D2DepositHeaderKey.PNA2516109, Mod2002018Key.TC515)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251611 : D2DepositHeaderKey.PNA2516119, Mod2002018Key.TC516)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251612 : D2DepositHeaderKey.PNA2516129, Mod2002018Key.TC518)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251613 : D2DepositHeaderKey.PNA2516139, Mod2002018Key.TC519)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251701 : D2DepositHeaderKey.PNA2517019, Mod2002018Key.TC520)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251702 : D2DepositHeaderKey.PNA2517029, Mod2002018Key.TC521)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251703 : D2DepositHeaderKey.PNA2517039, Mod2002018Key.TC522)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251704 : D2DepositHeaderKey.PNA2517049, Mod2002018Key.TC523)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251705 : D2DepositHeaderKey.PNA2517059, Mod2002018Key.TC524)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251706 : D2DepositHeaderKey.PNA2517069, Mod2002018Key.TC525)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251707 : D2DepositHeaderKey.PNA2517079, Mod2002018Key.TC526)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251708 : D2DepositHeaderKey.PNA2517089, Mod2002018Key.TC527)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251709 : D2DepositHeaderKey.PNA2517099, Mod2002018Key.TC528)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251710 : D2DepositHeaderKey.PNA2517109, Mod2002018Key.TC529)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251711 : D2DepositHeaderKey.PNA2517119, Mod2002018Key.TC530)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251712 : D2DepositHeaderKey.PNA2517129, Mod2002018Key.TC532)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251713 : D2DepositHeaderKey.PNA2517139, Mod2002018Key.TC533)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251801 : D2DepositHeaderKey.PNA2518019, Mod2002018Key.TC534)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251802 : D2DepositHeaderKey.PNA2518029, Mod2002018Key.TC535)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251803 : D2DepositHeaderKey.PNA2518039, Mod2002018Key.TC536)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251804 : D2DepositHeaderKey.PNA2518049, Mod2002018Key.TC537)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251805 : D2DepositHeaderKey.PNA2518059, Mod2002018Key.TC538)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251806 : D2DepositHeaderKey.PNA2518069, Mod2002018Key.TC539)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251807 : D2DepositHeaderKey.PNA2518079, Mod2002018Key.TC540)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251808 : D2DepositHeaderKey.PNA2518089, Mod2002018Key.TC541)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251809 : D2DepositHeaderKey.PNA2518099, Mod2002018Key.TC542)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251810 : D2DepositHeaderKey.PNA2518109, Mod2002018Key.TC543)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251811 : D2DepositHeaderKey.PNA2518119, Mod2002018Key.TC544)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251812 : D2DepositHeaderKey.PNA2518129, Mod2002018Key.TC546)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA251813 : D2DepositHeaderKey.PNA2518139, Mod2002018Key.TC547)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252601 : D2DepositHeaderKey.PNA2526019, Mod2002018Key.TC604)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252602 : D2DepositHeaderKey.PNA2526029, Mod2002018Key.TC605)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252603 : D2DepositHeaderKey.PNA2526039, Mod2002018Key.TC606)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252604 : D2DepositHeaderKey.PNA2526049, Mod2002018Key.TC607)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252605 : D2DepositHeaderKey.PNA2526059, Mod2002018Key.TC608)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252606 : D2DepositHeaderKey.PNA2526069, Mod2002018Key.TC609)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252607 : D2DepositHeaderKey.PNA2526079, Mod2002018Key.TC610)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252608 : D2DepositHeaderKey.PNA2526089, Mod2002018Key.TC611)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252609 : D2DepositHeaderKey.PNA2526099, Mod2002018Key.TC612)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252610 : D2DepositHeaderKey.PNA2526109, Mod2002018Key.TC613)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252611 : D2DepositHeaderKey.PNA2526119, Mod2002018Key.TC614)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252612 : D2DepositHeaderKey.PNA2526129, Mod2002018Key.TC616)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252613 : D2DepositHeaderKey.PNA2526139, Mod2002018Key.TC617) 
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252401 : D2DepositHeaderKey.PNA2524019, Mod2002018Key.TC618)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252402 : D2DepositHeaderKey.PNA2524029, Mod2002018Key.TC619)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252403 : D2DepositHeaderKey.PNA2524039, Mod2002018Key.TC620)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252404 : D2DepositHeaderKey.PNA2524049, Mod2002018Key.TC621)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252405 : D2DepositHeaderKey.PNA2524059, Mod2002018Key.TC622)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252406 : D2DepositHeaderKey.PNA2524069, Mod2002018Key.TC623)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252407 : D2DepositHeaderKey.PNA2524079, Mod2002018Key.TC624)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252408 : D2DepositHeaderKey.PNA2524089, Mod2002018Key.TC625)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252409 : D2DepositHeaderKey.PNA2524099, Mod2002018Key.TC626)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252410 : D2DepositHeaderKey.PNA2524109, Mod2002018Key.TC627)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252411 : D2DepositHeaderKey.PNA2524119, Mod2002018Key.TC628)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252412 : D2DepositHeaderKey.PNA2524129, Mod2002018Key.TC630)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252413 : D2DepositHeaderKey.PNA2524139, Mod2002018Key.TC631)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253101 : D2DepositHeaderKey.PNA2531019, Mod2002018Key.TC715)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253102 : D2DepositHeaderKey.PNA2531029, Mod2002018Key.TC716)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253103 : D2DepositHeaderKey.PNA2531039, Mod2002018Key.TC717)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253104 : D2DepositHeaderKey.PNA2531049, Mod2002018Key.TC718)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253105 : D2DepositHeaderKey.PNA2531059, Mod2002018Key.TC719)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253106 : D2DepositHeaderKey.PNA2531069, Mod2002018Key.TC720)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253107 : D2DepositHeaderKey.PNA2531079, Mod2002018Key.TC721)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253108 : D2DepositHeaderKey.PNA2531089, Mod2002018Key.TC722)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253109 : D2DepositHeaderKey.PNA2531099, Mod2002018Key.TC723)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253110 : D2DepositHeaderKey.PNA2531109, Mod2002018Key.TC724)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253111 : D2DepositHeaderKey.PNA2531119, Mod2002018Key.TC725)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253112 : D2DepositHeaderKey.PNA2531129, Mod2002018Key.TC727)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253113 : D2DepositHeaderKey.PNA2531139, Mod2002018Key.TC728)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253201 : D2DepositHeaderKey.PNA2532019, Mod2002018Key.TC729)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253202 : D2DepositHeaderKey.PNA2532029, Mod2002018Key.TC730)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253203 : D2DepositHeaderKey.PNA2532039, Mod2002018Key.TC731)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253204 : D2DepositHeaderKey.PNA2532049, Mod2002018Key.TC732)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253205 : D2DepositHeaderKey.PNA2532059, Mod2002018Key.TC733)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253206 : D2DepositHeaderKey.PNA2532069, Mod2002018Key.TC734)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253207 : D2DepositHeaderKey.PNA2532079, Mod2002018Key.TC735)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253208 : D2DepositHeaderKey.PNA2532089, Mod2002018Key.TC736)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253209 : D2DepositHeaderKey.PNA2532099, Mod2002018Key.TC737)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253210 : D2DepositHeaderKey.PNA2532109, Mod2002018Key.TC738)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253211 : D2DepositHeaderKey.PNA2532119, Mod2002018Key.TC739)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253212 : D2DepositHeaderKey.PNA2532129, Mod2002018Key.TC741)
		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA253213 : D2DepositHeaderKey.PNA2532139, Mod2002018Key.TC742)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252501 : D2DepositHeaderKey.PNA252501, Mod2002018Key.TC632)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252502 : D2DepositHeaderKey.PNA252502, Mod2002018Key.TC633)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252503 : D2DepositHeaderKey.PNA252503, Mod2002018Key.TC634)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252504 : D2DepositHeaderKey.PNA252504, Mod2002018Key.TC635)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252505 : D2DepositHeaderKey.PNA252505, Mod2002018Key.TC636)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252506 : D2DepositHeaderKey.PNA252506, Mod2002018Key.TC637)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252507 : D2DepositHeaderKey.PNA252507, Mod2002018Key.TC638)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252508 : D2DepositHeaderKey.PNA252508, Mod2002018Key.TC639)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252509 : D2DepositHeaderKey.PNA252509, Mod2002018Key.TC640)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252510 : D2DepositHeaderKey.PNA252510, Mod2002018Key.TC641)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252511 : D2DepositHeaderKey.PNA252511, Mod2002018Key.TC642)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252512 : D2DepositHeaderKey.PNA252512, Mod2002018Key.TC644)
//		,(ctx, mod200, act) -> set(ctx, mod200, act ? D2DepositHeaderKey.PNA252513 : D2DepositHeaderKey.PNA252513, Mod2002018Key.TC645)
	};
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002018 mod200, D2DepositHeaderKey D2Key, Mod2002018Key mod200Key) {
		DoubleVariable2018 dv = mod200.getVariable(mod200Key);
		ctx.put(D2Key, dv==null?0.0:dv.getValue());
	} 
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002018 mod200, D2DepositHeaderKey D2Key, Mod2002018Key[] mod200Keys) {
		Double d = 0.0;
		for(Integer i = 0; i< mod200Keys.length ; i++){
			DoubleVariable2018 dv = mod200.getVariable(mod200Keys[i]);
			d = d + (dv==null? 0.0:dv.getValue());
		}
		ctx.put(D2Key, d);
	} 
	
	
	public static void fill(Map<D2DepositHeaderKey, Double> ctx, Mod2002018 mod200, Boolean actual) {
		fillBalance(ctx, mod200, actual);
		fillPyg(ctx, mod200, actual);
		fillEcpn(ctx, mod200, actual);
		fillEcpn2(ctx, mod200, actual);
	}
	public static void fillBalance(Map<D2DepositHeaderKey, Double> ctx, Mod2002018 mod200, Boolean actual) {
		for (IPropertyFiller filler : BALANCE_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200, actual);
		}
	}

	public static void fillPyg(Map<D2DepositHeaderKey, Double> ctx, Mod2002018 mod200, Boolean actual) {
		for (IPropertyFiller filler : PYG_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200, actual);
		}
	}
	public static void fillEcpn(Map<D2DepositHeaderKey, Double> ctx, Mod2002018 mod200, Boolean actual) {
		for (IPropertyFiller filler : ECPN_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200, actual);
		}
	}
	public static void fillEcpn2(Map<D2DepositHeaderKey, Double> ctx, Mod2002018 mod200, Boolean actual) {
		for (IPropertyFiller filler : ECPN2_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200, actual);
		}
	}
}
