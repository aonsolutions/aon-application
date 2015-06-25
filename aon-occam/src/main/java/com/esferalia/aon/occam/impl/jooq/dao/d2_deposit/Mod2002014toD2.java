package com.esferalia.aon.occam.impl.jooq.dao.d2_deposit;

import java.util.Map;

import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.DoubleVariable2014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;

public class Mod2002014toD2 {
	
	@FunctionalInterface
	private static interface IPropertyFiller {
		public void fill(Map<D2DepositHeaderKey,Double> map,Mod2002014 mod200);
	}
	
	private static final IPropertyFiller[] BALANCE_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111000,Mod2002014Key.BA101)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111100,Mod2002014Key.BA102)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111200,Mod2002014Key.BA111)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111300,Mod2002014Key.BA115)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111400,Mod2002014Key.BA118)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111500,Mod2002014Key.BA126)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111600,Mod2002014Key.BA134)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA111700,Mod2002014Key.BA135)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112000,Mod2002014Key.BA136)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112100,Mod2002014Key.BA137)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112200,Mod2002014Key.BA138)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112300,Mod2002014Key.BA149)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112380,Mod2002014Key.BA150)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112381,Mod2002014Key.BA151)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112382,Mod2002014Key.BA152)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112370,Mod2002014Key.BA158)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112390,Mod2002014Key.BA159)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112400,Mod2002014Key.BA160)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112500,Mod2002014Key.BA168)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112600,Mod2002014Key.BA176)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA112700,Mod2002014Key.BA177)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA110000,Mod2002014Key.BA180)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2120000,Mod2002014Key.BP185)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121000,Mod2002014Key.BP186)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121100,Mod2002014Key.BP187)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121110,Mod2002014Key.BP188)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121120,Mod2002014Key.BP189)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121200,Mod2002014Key.BP190)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121300,Mod2002014Key.BP191)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121400,Mod2002014Key.BP194)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121500,Mod2002014Key.BP195)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121600,Mod2002014Key.BP198)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121700,Mod2002014Key.BP199)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121800,Mod2002014Key.BP200)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2121900,Mod2002014Key.BP201)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2122000,Mod2002014Key.BP202)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2123000,Mod2002014Key.BP209)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131000,Mod2002014Key.BP210)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131100,Mod2002014Key.BP211)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131200,Mod2002014Key.BP216)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131220,Mod2002014Key.BP218)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131230,Mod2002014Key.BP219)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131290,Mod2002014Key.BP222)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131300,Mod2002014Key.BP223)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131400,Mod2002014Key.BP224)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131500,Mod2002014Key.BP225)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131600,Mod2002014Key.BP226)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2131700,Mod2002014Key.BP227)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232000,Mod2002014Key.BP228)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232100,Mod2002014Key.BP229)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232200,Mod2002014Key.BP230)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232300,Mod2002014Key.BP231)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232320,Mod2002014Key.BP233)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232330,Mod2002014Key.BP234)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232390,Mod2002014Key.BP237)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232400,Mod2002014Key.BP238)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232500,Mod2002014Key.BP239)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232580,Mod2002014Key.BP240)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232581,Mod2002014Key.BP241)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232582,Mod2002014Key.BP242)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232590,Mod2002014Key.BP249)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232600,Mod2002014Key.BP250)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2232700,Mod2002014Key.BP251)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.BA2230000,Mod2002014Key.BP252)
	};
		
	private static final IPropertyFiller[] PYG_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40100,Mod2002014Key.PG255)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40200,Mod2002014Key.PG258)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40300,Mod2002014Key.PG259)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40400,Mod2002014Key.PG260)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40500,Mod2002014Key.PG265)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40600,Mod2002014Key.PG270)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40700,Mod2002014Key.PG279)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40800,Mod2002014Key.PG284)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA40900,Mod2002014Key.PG285)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41000,Mod2002014Key.PG286)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41100,Mod2002014Key.PG287)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41200,Mod2002014Key.PG294)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41300,Mod2002014Key.PG295)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49100,Mod2002014Key.PG296)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41400,Mod2002014Key.PG297)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41430,Mod2002014Key.PG304)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41490,Mod2002014Key.PG329)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41500,Mod2002014Key.PG305)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41600,Mod2002014Key.PG309)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41700,Mod2002014Key.PG312)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41800,Mod2002014Key.PG313)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49200,Mod2002014Key.PG324)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49300,Mod2002014Key.PG325)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA41900,Mod2002014Key.PG326)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PA49500,Mod2002014Key.PG327)
	};
		
	private static final IPropertyFiller[] ECPN_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159100,Mod2002014Key.T0500)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150010,Mod2002014Key.T0336)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150020,Mod2002014Key.T0339)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150030,Mod2002014Key.T0340)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150040,Mod2002014Key.T0341)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150050,Mod2002014Key.T0342)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150060,Mod2002014Key.T0343)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150070,Mod2002014Key.T0344)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159200,Mod2002014Key.T0345)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150080,Mod2002014Key.T0346)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150090,Mod2002014Key.T0349)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150100,Mod2002014Key.T0350)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150110,Mod2002014Key.T0351)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150120,Mod2002014Key.T0352)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA150130,Mod2002014Key.T0353)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159300,Mod2002014Key.T0354)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA159400,Mod2002014Key.T0355)
	};
	private static final IPropertyFiller[] ECPN2_ACTIVE_KEYS = new IPropertyFiller[] {
		 (ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251101,Mod2002014Key.TC380)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251102,Mod2002014Key.TC381)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251103,Mod2002014Key.TC382)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251104,Mod2002014Key.TC383)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251105,Mod2002014Key.TC384)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251106,Mod2002014Key.TC385)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251107,Mod2002014Key.TC386)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251108,Mod2002014Key.TC387)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251109,Mod2002014Key.TC388)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251110,Mod2002014Key.TC389)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251111,Mod2002014Key.TC390)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251112,Mod2002014Key.TC392)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251113,Mod2002014Key.TC393)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251201,Mod2002014Key.TC394)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251202,Mod2002014Key.TC395)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251203,Mod2002014Key.TC396)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251204,Mod2002014Key.TC397)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251205,Mod2002014Key.TC398)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251206,Mod2002014Key.TC399)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251207,Mod2002014Key.TC400)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251208,Mod2002014Key.TC401)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251209,Mod2002014Key.TC402)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251210,Mod2002014Key.TC403)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251211,Mod2002014Key.TC404)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251212,Mod2002014Key.TC406)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251213,Mod2002014Key.TC407)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251301,Mod2002014Key.TC408)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251302,Mod2002014Key.TC409)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251303,Mod2002014Key.TC410)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251304,Mod2002014Key.TC411)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251305,Mod2002014Key.TC412)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251306,Mod2002014Key.TC413)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251307,Mod2002014Key.TC414)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251308,Mod2002014Key.TC415)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251309,Mod2002014Key.TC416)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251310,Mod2002014Key.TC417)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251311,Mod2002014Key.TC418)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251312,Mod2002014Key.TC420)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251313,Mod2002014Key.TC421)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251401,Mod2002014Key.TC422)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251402,Mod2002014Key.TC423)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251403,Mod2002014Key.TC424)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251404,Mod2002014Key.TC425)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251405,Mod2002014Key.TC426)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251406,Mod2002014Key.TC427)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251407,Mod2002014Key.TC428)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251408,Mod2002014Key.TC429)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251409,Mod2002014Key.TC430)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251410,Mod2002014Key.TC431)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251411,Mod2002014Key.TC432)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251412,Mod2002014Key.TC434)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251413,Mod2002014Key.TC435)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251501,Mod2002014Key.TC436)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251502,Mod2002014Key.TC437)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251503,Mod2002014Key.TC438)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251504,Mod2002014Key.TC439)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251505,Mod2002014Key.TC440)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251506,Mod2002014Key.TC441)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251507,Mod2002014Key.TC442)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251508,Mod2002014Key.TC443)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251509,Mod2002014Key.TC444)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251510,Mod2002014Key.TC445)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251511,Mod2002014Key.TC446)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251512,Mod2002014Key.TC448)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251513,Mod2002014Key.TC449)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251601,Mod2002014Key.TC506)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251602,Mod2002014Key.TC507)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251603,Mod2002014Key.TC508)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251604,Mod2002014Key.TC509)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251605,Mod2002014Key.TC510)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251606,Mod2002014Key.TC511)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251607,Mod2002014Key.TC512)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251608,Mod2002014Key.TC513)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251609,Mod2002014Key.TC514)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251610,Mod2002014Key.TC515)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251611,Mod2002014Key.TC516)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251612,Mod2002014Key.TC518)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251613,Mod2002014Key.TC519)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251701,Mod2002014Key.TC520)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251702,Mod2002014Key.TC521)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251703,Mod2002014Key.TC522)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251704,Mod2002014Key.TC523)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251705,Mod2002014Key.TC524)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251706,Mod2002014Key.TC525)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251707,Mod2002014Key.TC526)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251708,Mod2002014Key.TC527)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251709,Mod2002014Key.TC528)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251710,Mod2002014Key.TC529)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251711,Mod2002014Key.TC530)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251712,Mod2002014Key.TC532)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251713,Mod2002014Key.TC533)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251801,Mod2002014Key.TC534)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251802,Mod2002014Key.TC535)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251803,Mod2002014Key.TC536)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251804,Mod2002014Key.TC537)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251805,Mod2002014Key.TC538)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251806,Mod2002014Key.TC539)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251807,Mod2002014Key.TC540)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251808,Mod2002014Key.TC541)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251809,Mod2002014Key.TC542)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251810,Mod2002014Key.TC543)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251811,Mod2002014Key.TC544)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251812,Mod2002014Key.TC546)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA251813,Mod2002014Key.TC547)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252601,Mod2002014Key.TC604)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252602,Mod2002014Key.TC605)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252603,Mod2002014Key.TC606)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252604,Mod2002014Key.TC607)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252605,Mod2002014Key.TC608)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252606,Mod2002014Key.TC609)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252607,Mod2002014Key.TC610)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252608,Mod2002014Key.TC611)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252609,Mod2002014Key.TC612)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252610,Mod2002014Key.TC613)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252611,Mod2002014Key.TC614)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252612,Mod2002014Key.TC616)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252613,Mod2002014Key.TC617)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252401,Mod2002014Key.TC618)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252402,Mod2002014Key.TC619)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252403,Mod2002014Key.TC620)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252404,Mod2002014Key.TC621)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252405,Mod2002014Key.TC622)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252406,Mod2002014Key.TC623)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252407,Mod2002014Key.TC624)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252408,Mod2002014Key.TC625)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252409,Mod2002014Key.TC626)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252410,Mod2002014Key.TC627)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252411,Mod2002014Key.TC628)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252412,Mod2002014Key.TC630)
		,(ctx,mod200) ->  set(ctx,mod200,D2DepositHeaderKey.PNA252413,Mod2002014Key.TC631)
	};
	
	private static void set(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200, D2DepositHeaderKey D2Key, Mod2002014Key mod200Key) {
		DoubleVariable2014 dv = mod200.getVariable(mod200Key);
		ctx.put(D2Key, dv==null?0.0:dv.getValue());
	} 

	public static void fill(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200) {
		fillBalance(ctx, mod200);
		fillPyg(ctx, mod200);
		fillEcpn(ctx, mod200);
		fillEcpn2(ctx, mod200);
	}
	public static void fillBalance(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200) {
		for (IPropertyFiller filler : BALANCE_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillPyg(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200) {
		for (IPropertyFiller filler : PYG_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillEcpn(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200) {
		for (IPropertyFiller filler : ECPN_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}
	public static void fillEcpn2(Map<D2DepositHeaderKey, Double> ctx, Mod2002014 mod200) {
		for (IPropertyFiller filler : ECPN2_ACTIVE_KEYS ) {
			filler.fill(ctx, mod200);
		}
	}

}
