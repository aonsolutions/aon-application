package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.DoubleVariable2015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;

public class Mod2002015toD2 {
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void fill(Map<D2DepositHeaderKey,Double> map,Mod2002015 mod200);
	}
	
	private static final IPropertyFiller[] BALANCE_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111000,Mod2002015Key.BA101)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111100,Mod2002015Key.BA102)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111200,Mod2002015Key.BA111)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111300,Mod2002015Key.BA115)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111400,Mod2002015Key.BA118)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111500,Mod2002015Key.BA126)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111600,Mod2002015Key.BA134)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111700,Mod2002015Key.BA135)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112000,Mod2002015Key.BA136)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112100,Mod2002015Key.BA137)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112200,Mod2002015Key.BA138)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112300,Mod2002015Key.BA149)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112380,Mod2002015Key.BA150)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112381,Mod2002015Key.BA151)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112382,Mod2002015Key.BA152)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112370,Mod2002015Key.BA158)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112390,Mod2002015Key.BA159)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112400,Mod2002015Key.BA160)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112500,Mod2002015Key.BA168)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112600,Mod2002015Key.BA176)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112700,Mod2002015Key.BA177)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA110000,Mod2002015Key.BA180)
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2120000,Mod2002015Key.BP185)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121000,Mod2002015Key.BP186)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121100,Mod2002015Key.BP187)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121110,Mod2002015Key.BP188)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121120,Mod2002015Key.BP189)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121200,Mod2002015Key.BP190)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121300,Mod2002015Key.BP191)
		
		// TODO 
		//********************************************************************************/
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121350,Mod2002015Key.BP1001)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121360, new Mod2002015Key[]
				{Mod2002015Key.BP1002, Mod2002015Key.BP193, Mod2002015Key.BP192, Mod2002015Key.BP702})
		//********************************************************************************/
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121400,Mod2002015Key.BP194)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121500,Mod2002015Key.BP195)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121600,Mod2002015Key.BP198)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121700,Mod2002015Key.BP199)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121800,Mod2002015Key.BP200)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121900,Mod2002015Key.BP201)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2122000,Mod2002015Key.BP202)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2123000,Mod2002015Key.BP209)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131000,Mod2002015Key.BP210)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131100,Mod2002015Key.BP211)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131200,Mod2002015Key.BP216)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131220,Mod2002015Key.BP218)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131230,Mod2002015Key.BP219)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131290,Mod2002015Key.BP222)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131300,Mod2002015Key.BP223)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131400,Mod2002015Key.BP224)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131500,Mod2002015Key.BP225)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131600,Mod2002015Key.BP226)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131700,Mod2002015Key.BP227)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232000,Mod2002015Key.BP228)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232100,Mod2002015Key.BP229)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232200,Mod2002015Key.BP230)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232300,Mod2002015Key.BP231)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232320,Mod2002015Key.BP233)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232330,Mod2002015Key.BP234)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232390,Mod2002015Key.BP237)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232400,Mod2002015Key.BP238)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232500,Mod2002015Key.BP239)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232580,Mod2002015Key.BP240)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232581,Mod2002015Key.BP241)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232582,Mod2002015Key.BP242)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232590,Mod2002015Key.BP249)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232600,Mod2002015Key.BP250)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232700,Mod2002015Key.BP251)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2230000,Mod2002015Key.BP252)
	};
		
	private static final IPropertyFiller[] PYG_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40100,Mod2002015Key.PG255)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40200,Mod2002015Key.PG258)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40300,Mod2002015Key.PG259)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40400,Mod2002015Key.PG260)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40500,Mod2002015Key.PG265)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40600,Mod2002015Key.PG270)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40700,Mod2002015Key.PG279)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40800,Mod2002015Key.PG284)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40900,Mod2002015Key.PG285)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41000,Mod2002015Key.PG286)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41100,Mod2002015Key.PG287)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41200,Mod2002015Key.PG294)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41300,Mod2002015Key.PG295)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49100,Mod2002015Key.PG296)
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41400,Mod2002015Key.PG297)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41430,Mod2002015Key.PG304)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41490,new Mod2002015Key[]
				{Mod2002015Key.PG298, Mod2002015Key.PG301})
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41500,Mod2002015Key.PG305)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41600,Mod2002015Key.PG309)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41700,Mod2002015Key.PG312)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41800,Mod2002015Key.PG313)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49200,Mod2002015Key.PG324)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49300,Mod2002015Key.PG325)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41900,Mod2002015Key.PG326)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49500,Mod2002015Key.PG327)
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA42100,Mod2002015Key.PG329)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA42110,Mod2002015Key.PG330)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA42120,Mod2002015Key.PG331)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA42130,Mod2002015Key.PG332)

	};
		
	private static final IPropertyFiller[] ECPN_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159100,Mod2002015Key.T0500)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150010,Mod2002015Key.T0336)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150020,Mod2002015Key.T0339)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150030,Mod2002015Key.T0340)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150040,Mod2002015Key.T0341)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150050,Mod2002015Key.T0342)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150060,Mod2002015Key.T0343)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150070,Mod2002015Key.T0344)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159200,Mod2002015Key.T0345)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150080,Mod2002015Key.T0346)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150090,Mod2002015Key.T0349)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150100,Mod2002015Key.T0350)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150110,Mod2002015Key.T0351)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150120,Mod2002015Key.T0352)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150130,Mod2002015Key.T0353)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159300,Mod2002015Key.T0354)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159400,Mod2002015Key.T0355)
	};
	private static final IPropertyFiller[] ECPN2_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251101,Mod2002015Key.TC380)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251102,Mod2002015Key.TC381)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251103,Mod2002015Key.TC382)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251104,Mod2002015Key.TC383)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251105,Mod2002015Key.TC384)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251106,Mod2002015Key.TC385)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251107,Mod2002015Key.TC386)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251108,Mod2002015Key.TC387)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251109,Mod2002015Key.TC388)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251110,Mod2002015Key.TC389)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251111,Mod2002015Key.TC390)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251112,Mod2002015Key.TC392)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251113,Mod2002015Key.TC393)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251201,Mod2002015Key.TC394)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251202,Mod2002015Key.TC395)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251203,Mod2002015Key.TC396)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251204,Mod2002015Key.TC397)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251205,Mod2002015Key.TC398)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251206,Mod2002015Key.TC399)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251207,Mod2002015Key.TC400)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251208,Mod2002015Key.TC401)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251209,Mod2002015Key.TC402)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251210,Mod2002015Key.TC403)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251211,Mod2002015Key.TC404)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251212,Mod2002015Key.TC406)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251213,Mod2002015Key.TC407)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251301,Mod2002015Key.TC408)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251302,Mod2002015Key.TC409)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251303,Mod2002015Key.TC410)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251304,Mod2002015Key.TC411)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251305,Mod2002015Key.TC412)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251306,Mod2002015Key.TC413)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251307,Mod2002015Key.TC414)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251308,Mod2002015Key.TC415)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251309,Mod2002015Key.TC416)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251310,Mod2002015Key.TC417)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251311,Mod2002015Key.TC418)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251312,Mod2002015Key.TC420)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251313,Mod2002015Key.TC421)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251401,Mod2002015Key.TC422)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251402,Mod2002015Key.TC423)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251403,Mod2002015Key.TC424)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251404,Mod2002015Key.TC425)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251405,Mod2002015Key.TC426)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251406,Mod2002015Key.TC427)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251407,Mod2002015Key.TC428)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251408,Mod2002015Key.TC429)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251409,Mod2002015Key.TC430)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251410,Mod2002015Key.TC431)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251411,Mod2002015Key.TC432)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251412,Mod2002015Key.TC434)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251413,Mod2002015Key.TC435)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152801,Mod2002015Key.TC450)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152802,Mod2002015Key.TC451)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152803,Mod2002015Key.TC452)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152804,Mod2002015Key.TC453)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152805,Mod2002015Key.TC454)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152806,Mod2002015Key.TC455)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152807,Mod2002015Key.TC456)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152808,Mod2002015Key.TC457)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152809,Mod2002015Key.TC458)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152811,Mod2002015Key.TC461)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152812,Mod2002015Key.TC462)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152813,Mod2002015Key.TC463)

		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153001,Mod2002015Key.TC464)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153002,Mod2002015Key.TC465)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153003,Mod2002015Key.TC466)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153004,Mod2002015Key.TC467)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153005,Mod2002015Key.TC468)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153006,Mod2002015Key.TC469)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153007,Mod2002015Key.TC470)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153008,Mod2002015Key.TC471)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153009,Mod2002015Key.TC472)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153011,Mod2002015Key.TC475)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153012,Mod2002015Key.TC476)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP153013,Mod2002015Key.TC477)

		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152701,Mod2002015Key.TC478)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152702,Mod2002015Key.TC479)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152703,Mod2002015Key.TC480)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152704,Mod2002015Key.TC481)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152705,Mod2002015Key.TC482)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152706,Mod2002015Key.TC483)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152707,Mod2002015Key.TC484)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152708,Mod2002015Key.TC485)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152709,Mod2002015Key.TC486)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152711,Mod2002015Key.TC489)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152712,Mod2002015Key.TC490)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152713,Mod2002015Key.TC491)

		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152901,Mod2002015Key.TC492)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152902,Mod2002015Key.TC493)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152903,Mod2002015Key.TC494)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152904,Mod2002015Key.TC495)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152905,Mod2002015Key.TC496)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152906,Mod2002015Key.TC497)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152907,Mod2002015Key.TC498)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152908,Mod2002015Key.TC499)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152909,Mod2002015Key.TC502)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152911,Mod2002015Key.TC503)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152912,Mod2002015Key.TC504)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNP152913,Mod2002015Key.TC505)

		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251501,Mod2002015Key.TC436)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251502,Mod2002015Key.TC437)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251503,Mod2002015Key.TC438)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251504,Mod2002015Key.TC439)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251505,Mod2002015Key.TC440)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251506,Mod2002015Key.TC441)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251507,Mod2002015Key.TC442)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251508,Mod2002015Key.TC443)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251509,Mod2002015Key.TC444)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251510,Mod2002015Key.TC445)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251511,Mod2002015Key.TC446)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251512,Mod2002015Key.TC448)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251513,Mod2002015Key.TC449)
		
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251601,Mod2002015Key.TC506)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251602,Mod2002015Key.TC507)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251603,Mod2002015Key.TC508)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251604,Mod2002015Key.TC509)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251605,Mod2002015Key.TC510)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251606,Mod2002015Key.TC511)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251607,Mod2002015Key.TC512)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251608,Mod2002015Key.TC513)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251609,Mod2002015Key.TC514)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251610,Mod2002015Key.TC515)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251611,Mod2002015Key.TC516)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251612,Mod2002015Key.TC518)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251613,Mod2002015Key.TC519)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251701,Mod2002015Key.TC520)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251702,Mod2002015Key.TC521)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251703,Mod2002015Key.TC522)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251704,Mod2002015Key.TC523)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251705,Mod2002015Key.TC524)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251706,Mod2002015Key.TC525)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251707,Mod2002015Key.TC526)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251708,Mod2002015Key.TC527)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251709,Mod2002015Key.TC528)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251710,Mod2002015Key.TC529)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251711,Mod2002015Key.TC530)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251712,Mod2002015Key.TC532)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251713,Mod2002015Key.TC533)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251801,Mod2002015Key.TC534)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251802,Mod2002015Key.TC535)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251803,Mod2002015Key.TC536)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251804,Mod2002015Key.TC537)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251805,Mod2002015Key.TC538)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251806,Mod2002015Key.TC539)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251807,Mod2002015Key.TC540)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251808,Mod2002015Key.TC541)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251809,Mod2002015Key.TC542)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251810,Mod2002015Key.TC543)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251811,Mod2002015Key.TC544)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251812,Mod2002015Key.TC546)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251813,Mod2002015Key.TC547)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252601,Mod2002015Key.TC604)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252602,Mod2002015Key.TC605)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252603,Mod2002015Key.TC606)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252604,Mod2002015Key.TC607)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252605,Mod2002015Key.TC608)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252606,Mod2002015Key.TC609)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252607,Mod2002015Key.TC610)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252608,Mod2002015Key.TC611)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252609,Mod2002015Key.TC612)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252610,Mod2002015Key.TC613)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252611,Mod2002015Key.TC614)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252612,Mod2002015Key.TC616)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252613,Mod2002015Key.TC617)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252401,Mod2002015Key.TC618)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252402,Mod2002015Key.TC619)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252403,Mod2002015Key.TC620)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252404,Mod2002015Key.TC621)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252405,Mod2002015Key.TC622)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252406,Mod2002015Key.TC623)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252407,Mod2002015Key.TC624)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252408,Mod2002015Key.TC625)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252409,Mod2002015Key.TC626)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252410,Mod2002015Key.TC627)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252411,Mod2002015Key.TC628)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252412,Mod2002015Key.TC630)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252413,Mod2002015Key.TC631)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253101,Mod2002015Key.TC715)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253102,Mod2002015Key.TC716)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253103,Mod2002015Key.TC717)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253104,Mod2002015Key.TC718)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253105,Mod2002015Key.TC719)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253106,Mod2002015Key.TC720)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253107,Mod2002015Key.TC721)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253108,Mod2002015Key.TC722)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253109,Mod2002015Key.TC723)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253110,Mod2002015Key.TC724)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253111,Mod2002015Key.TC725)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253112,Mod2002015Key.TC727)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253113,Mod2002015Key.TC728)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253201,Mod2002015Key.TC729)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253202,Mod2002015Key.TC730)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253203,Mod2002015Key.TC731)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253204,Mod2002015Key.TC732)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253205,Mod2002015Key.TC733)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253206,Mod2002015Key.TC734)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253207,Mod2002015Key.TC735)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253208,Mod2002015Key.TC736)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253209,Mod2002015Key.TC737)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253210,Mod2002015Key.TC738)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253211,Mod2002015Key.TC739)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253212,Mod2002015Key.TC741)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA253213,Mod2002015Key.TC742)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252501,Mod2002015Key.TC632)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252502,Mod2002015Key.TC633)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252503,Mod2002015Key.TC634)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252504,Mod2002015Key.TC635)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252505,Mod2002015Key.TC636)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252506,Mod2002015Key.TC637)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252507,Mod2002015Key.TC638)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252508,Mod2002015Key.TC639)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252509,Mod2002015Key.TC640)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252510,Mod2002015Key.TC641)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252511,Mod2002015Key.TC642)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252512,Mod2002015Key.TC644)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252513,Mod2002015Key.TC645)
	};
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002015 mod200, D2DepositHeaderKey D2Key, Mod2002015Key mod200Key) {
		DoubleVariable2015 dv = mod200.getVariable(mod200Key);
		ctx.put(D2Key, dv==null?0.0:dv.getValue());
	} 
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002015 mod200, D2DepositHeaderKey D2Key, Mod2002015Key[] mod200Keys) {
		Double d = 0.0;
		for(Integer i = 0; i< mod200Keys.length ; i++){
			DoubleVariable2015 dv = mod200.getVariable(mod200Keys[i]);
			d = d + (dv==null? 0.0:dv.getValue());
		}
		ctx.put(D2Key, d);
	} 
	
	
	public static void fill(Map<D2DepositHeaderKey, Double> ctx, Mod2002015 mod200) {
		fillBalance(ctx, mod200);
		fillPyg(ctx, mod200);
		fillEcpn(ctx, mod200);
		fillEcpn2(ctx, mod200);
	}
	public static void fillBalance(Map<D2DepositHeaderKey, Double> ctx, Mod2002015 mod200) {
		for (IPropertyFiller filler : BALANCE_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillPyg(Map<D2DepositHeaderKey, Double> ctx, Mod2002015 mod200) {
		for (IPropertyFiller filler : PYG_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillEcpn(Map<D2DepositHeaderKey, Double> ctx, Mod2002015 mod200) {
		for (IPropertyFiller filler : ECPN_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillEcpn2(Map<D2DepositHeaderKey, Double> ctx, Mod2002015 mod200) {
		for (IPropertyFiller filler : ECPN2_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}

}
