package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022;

import java.util.Arrays;
import java.util.LinkedHashMap;

import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN082Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1039Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1040Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1041Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1280Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN1344Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN2314Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN2315Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN565_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN565_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN570Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN571Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN572Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN573Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN584Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN585Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN588Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022BN590Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022CorrectionKey;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022KeyDC;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1212Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1494Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1535Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM1579Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LM538Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1032Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1033_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ1033_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ243Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ547Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022LQ561Key;

public class Mod2002022Compute {
	
	// FALTA - REVISAR CALCULOS CON LAS NUEVAS CASILLAS Y EL DOCUMENTO PADIS
	
	// MUY IMPORTANTE - LOS CALCULOS DEBEN ESTAR ORDENADOS EN EL MAP SEGUN LOS CALCULOS DE LAS CASILLAS
	// DEL MODELO, YA QUE NO SE HACEN CALCULOS RECURSIVOS EN LAS EXPRESIONES MVEL, PUES NO SE LE PASA
	// EL MAP AL CONTEXT DE MVEL, PARA GANAR VELOCIDAD, DADO EL TAMAÑO DEL MAP Y LOS CALCULOS A REALIZAR
	// POR ESO SE USA UN LinkedHashMap, PORQUE SE NECESITA QUE PARA EL CALCULO DEL MODELO, SE RECORRA 
	// EL MAP EXACTAMENTE EN EL ORDEN EN QUE APARECE EN ESTA CLASE
	
	public static LinkedHashMap<Mod2002022Key,String> COMPUTE_EXPRESSION_MAP = new LinkedHashMap<Mod2002022Key,String>();
		
	static {
		
		// PARTICIPACIONES		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1501,"computeP1501()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1502,"computeP1502()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1503,"computeP1503()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1504,"computeP1504()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1506,"computeP1506()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1809,"computeP1809()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1810,"computeP1810()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1507,"computeP1507()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.P1508,"computeP1508()");
		
	}

	static { 
		
		// BALANCE: ACTIVO
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA102,"isBalanceNormal()?(BA103+BA104+BA105+BA106+BA107+BA108+BA700+BA109):(BA106+BA110)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA111,"isBalanceNormal()?(BA112+BA113+BA114):(BA111)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA115,"isBalanceNormal()?(BA116+BA117):(BA115)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA118,"isBalanceNormal()?(BA119+BA120+BA121+BA122+BA123+BA124):(BA119+BA125)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA126,"isBalanceNormal()?(BA127+BA128+BA129+BA130+BA131+BA132):(BA127+BA133)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA101,"(BA102+BA111+BA115+BA118+BA126+BA134+BA135)");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA141,"isBalanceNormal()?(BA142+BA143):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA144,"isBalanceNormal()?(BA145+BA146):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA138,"isBalanceNormal()?(BA139+BA140+BA141+BA144+BA147+BA148+BA701):(BA138)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA150,"(BA151+BA152)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA149,"isBalanceNormal()?(BA150+BA153+BA154+BA155+BA156+BA157+BA158):(BA150+BA158+BA159)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA160,"isBalanceNormal()?(BA161+BA162+BA163+BA164+BA165+BA166):(BA161+BA167)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA168,"isBalanceNormal()?(BA169+BA170+BA171+BA172+BA173+BA174):(BA169+BA175)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA177,"isBalanceNormal()?(BA178+BA179):(BA177)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA136,"(BA137+BA138+BA149+BA160+BA168+BA176+BA177)");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BA180,"(BA101+BA136)");
		
	}
	
	static { 
		
		// BALANCE: PATRIMONIO NETO Y PASIVO		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP187,"(BP188+BP189)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP191,"isBalanceNormal()?(BP192+BP193+BP702+BP1001+BP1002+BP712):(BP193+BP1001+BP1002+BP712)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP195,"isBalanceNormal()?(BP196+BP197):(BP195)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP201,"isBalancePymes()?(0.0):(BP201)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP186,"isBalancePymes()?(BP187+BP190+BP191+BP194+BP195+BP198+BP199+BP200):(BP187+BP190+BP191+BP194+BP195+BP198+BP199+BP200+BP201)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP202,"isBalanceNormal()?(BP203+BP204+BP205+BP206+BP207):(BP202)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP185,"isBalancePymes()?(BP186+BP209+BP208):(BP186+BP209+BP202)");
				
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP211,"isBalanceNormal()?(BP212+BP213+BP214+BP215):(BP211)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP216,"isBalanceNormal()?(BP217+BP218+BP219+BP220+BP221):(BP218+BP219+BP222)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP210,"(BP211+BP216+BP223+BP224+BP225+BP226+BP227)");
				
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP230,"isBalanceNormal()?(BP703+BP704):(BP230)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP231,"isBalanceNormal()?(BP232+BP233+BP234+BP235+BP236):(BP233+BP234+BP237)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP240,"(BP241+BP242)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP239,"isBalanceNormal()?(BP240+BP243+BP244+BP245+BP246+BP247+BP248):(BP240+BP249)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP228,"isBalanceNormal()?(BP229+BP230+BP231+BP238+BP239+BP250+BP251):(BP229+BP230+BP231+BP238+BP239+BP250+BP251)");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BP252,"(BP185+BP210+BP228)");
		
	}
	
	static { 
		
		// CUENTA DE PERDIDAS Y GANANCIAS		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG705,"isPygNormal()?(PG706+PG707+PG708):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG255,"isPygNormal()?(PG256+PG257+PG711+PG705):(PG255)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG261,"(PG760+PG761)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG262,"(PG762+PG763)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG260,"(PG261+PG262+PG263+PG264)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG266,"(PG267+PG268)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG265,"(PG266+PG269)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG270,"(PG271+PG273+PG274+PG275+PG276+PG277+PG278)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG280,"(PG253+PG254)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG279,"(PG280+PG281+PG282+PG283+PG709)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG288,"(PG289+PG290)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG291,"(PG292+PG293)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG287,"(PG288+PG291+PG710)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG296,"isPygPymes()"
			+ "?(PG255+PG258+PG259+PG260+PG265+PG270+PG279+PG284+PG285+PG286+PG287+PG295)"
			+ ":(PG255+PG258+PG259+PG260+PG265+PG270+PG279+PG284+PG285+PG286+PG287+PG294+PG295)");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG298,"(PG299+PG300)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG301,"(PG302+PG303)");		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG297,"(PG298+PG301+PG304)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG305,"(PG306+PG307+PG308)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG309,"isPygNormal()?(PG310+PG311):(PG309)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG314,"(PG315+PG316+PG317+PG318)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG319,"(PG320+PG321+PG322+PG323)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG313,"(PG314+PG319)");		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG329,"(PG330+PG331+PG332)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG324,"(PG297+PG305+PG309+PG312+PG313+PG329)");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG325,"(PG296+PG324)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG327,"(PG325+PG326)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.PG500,"isPygNormal()?(PG327+PG328):(PG327)");
		
	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO DE INGRESOS Y GASTOS RECONOCIDOS EN EL EJERCICIO
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.T0500,"PG500");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.T0336,"isEcpnNormal()?(T0337+T0338):(T0336)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.T0345,"(T0336+T0339+T0340+T0341+T0342+T0343+T0344)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.T0346,"isEcpnNormal()?(T0347+T0348):(T0346)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.T0354,"(T0346+T0349+T0350+T0351+T0352+T0353)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.T0355,"(T0500+T0345+T0354)");

	}
	
	static { 
		
		// ESTADO DE CAMBIOS EN EL PATRIMONIO NETO. ESTADO TOTAL DE CAMBIOS EN EL PATRIMONIO NETO
		
		// Fila: SALDO AJUSTADO, INICIO DEL EJERCICIO (N, A, P)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC422,"(TC380+TC394+TC408)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC423,"(TC381+TC395+TC409)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC424,"(TC382+TC396+TC410)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC425,"(TC383+TC397+TC411)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC426,"(TC384+TC398+TC412)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC428,"(TC386+TC400+TC414)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC429,"(TC387+TC401+TC415)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC427,"(TC385+TC399+TC413)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC430,"(TC388+TC402+TC416)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC431,"(TC389+TC403+TC417)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC432,"(TC390+TC404+TC418)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC433,"(TC391+TC405+TC419)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC434,"(TC392+TC406+TC420)");
		
		// Fila: Ingresos y gastos reconocidos en patrimonio neto (P) 
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC464,"isEcpnPymes()?(TC478+TC492):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC465,"isEcpnPymes()?(TC479+TC493):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC466,"isEcpnPymes()?(TC480+TC494):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC467,"isEcpnPymes()?(TC481+TC495):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC468,"isEcpnPymes()?(TC482+TC496):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC469,"isEcpnPymes()?(TC483+TC497):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC470,"isEcpnPymes()?(TC484+TC498):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC471,"isEcpnPymes()?(TC485+TC499):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC472,"isEcpnPymes()?(TC486+TC502):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC475,"isEcpnPymes()?(TC489+TC503):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC476,"isEcpnPymes()?(TC490+TC504):(0.0)");

		// Fila: Operaciones con socios o propietarios (N, A, P) 
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC506,"isEcpnNormal()?(TC520+TC534+TC548+TC562+TC576+TC590+TC604):(TC520+TC534+TC604)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC507,"isEcpnNormal()?(TC521+TC535+TC549+TC563+TC577+TC591+TC605):(TC521+TC535+TC605)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC508,"isEcpnNormal()?(TC522+TC536+TC550+TC564+TC578+TC592+TC606):(TC522+TC536+TC606)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC509,"isEcpnNormal()?(TC523+TC537+TC551+TC565+TC579+TC593+TC607):(TC523+TC537+TC607)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC510,"isEcpnNormal()?(TC524+TC538+TC552+TC566+TC580+TC594+TC608):(TC524+TC538+TC608)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC511,"isEcpnNormal()?(TC525+TC539+TC553+TC567+TC581+TC595+TC609):(TC525+TC539+TC609)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC512,"isEcpnNormal()?(TC526+TC540+TC554+TC568+TC582+TC596+TC610):(TC526+TC540+TC610)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC513,"isEcpnNormal()?(TC527+TC541+TC555+TC569+TC583+TC597+TC611):(TC527+TC541+TC611)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC514,"isEcpnNormal()?(TC528+TC542+TC556+TC570+TC584+TC598+TC612):(TC528+TC542+TC612)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC515,"isEcpnNormal()?(TC529+TC543+TC557+TC571+TC585+TC599+TC613):(TC529+TC543+TC613)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC516,"isEcpnNormal()?(TC530+TC544+TC558+TC572+TC586+TC600+TC614):(TC530+TC544+TC614)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC517,"isEcpnNormal()?(TC531+TC545+TC615):(TC531+TC545+TC615)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC518,"isEcpnNormal()?(TC532+TC546+TC560+TC574+TC588+TC602+TC616):(TC532+TC546+TC616)");
		
		// Fila: Otras variaciones del patrimonio neto (N, A, P)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC618,"(TC715+TC729)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC619,"(TC716+TC730)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC620,"(TC717+TC731)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC621,"(TC718+TC732)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC622,"(TC719+TC733)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC623,"(TC720+TC734)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC624,"(TC721+TC735)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC625,"(TC722+TC736)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC626,"(TC723+TC737)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC627,"(TC724+TC738)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC628,"(TC725+TC739)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC629,"(TC726+TC740)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC630,"(TC727+TC741)");

		// Fila: SALDO, FINAL DEL EJERCICIO (N, A, P)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC632,"isEcpnPymes()?(TC422+TC436+TC450+TC464+TC506+TC618):(TC422+TC436+TC506+TC618)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC633,"isEcpnPymes()?(TC423+TC437+TC451+TC465+TC507+TC619):(TC423+TC437+TC507+TC619)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC634,"isEcpnPymes()?(TC424+TC438+TC452+TC466+TC508+TC620):(TC424+TC438+TC508+TC620)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC635,"isEcpnPymes()?(TC425+TC439+TC453+TC467+TC509+TC621):(TC425+TC439+TC509+TC621)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC636,"isEcpnPymes()?(TC426+TC440+TC454+TC468+TC510+TC622):(TC426+TC440+TC510+TC622)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC637,"isEcpnPymes()?(TC427+TC441+TC455+TC469+TC511+TC623):(TC427+TC441+TC511+TC623)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC638,"isEcpnPymes()?(TC428+TC442+TC456+TC470+TC512+TC624):(TC428+TC442+TC512+TC624)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC639,"isEcpnPymes()?(TC429+TC443+TC457+TC471+TC513+TC625):(TC429+TC443+TC513+TC625)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC640,"isEcpnPymes()?(TC430+TC444+TC458+TC472+TC514+TC626):(TC430+TC444+TC514+TC626)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC641,"isEcpnPymes()?(TC431+TC445+TC515+TC627):(TC431+TC445+TC515+TC627)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC642,"isEcpnPymes()?(TC432+TC446+TC516+TC628):(TC432+TC446+TC516+TC628)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC643,"isEcpnPymes()?(TC433+TC461+TC475+TC517+TC629):(TC433+TC517+TC629)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC644,"isEcpnPymes()?(TC434+TC448+TC462+TC476+TC518+TC630):(TC434+TC448+TC518+TC630)");

		// Columna: Total (N,A,P)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC393,"(TC380+TC381+TC382+TC383+TC384+TC385+TC386+TC387+TC388+TC389+TC390+TC391+TC392)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC407,"(TC394+TC395+TC396+TC397+TC398+TC399+TC400+TC401+TC402+TC403+TC404+TC405+TC406)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC421,"(TC408+TC409+TC410+TC411+TC412+TC413+TC414+TC415+TC416+TC417+TC418+TC419+TC420)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC435,"(TC422+TC423+TC424+TC425+TC426+TC427+TC428+TC429+TC430+TC431+TC432+TC433+TC434)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC449,"(TC436+TC437+TC438+TC439+TC440+TC441+TC442+TC443+TC444+TC445+TC446+TC448)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC463,"(TC450+TC451+TC452+TC453+TC454+TC455+TC456+TC457+TC458+TC461+TC462)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC477,"(TC464+TC465+TC466+TC467+TC468+TC469+TC470+TC471+TC472+TC475+TC476)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC491,"(TC478+TC479+TC480+TC481+TC482+TC483+TC484+TC485+TC486+TC489+TC490)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC505,"(TC492+TC493+TC494+TC495+TC496+TC497+TC498+TC499+TC502+TC503+TC504)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC519,"(TC506+TC507+TC508+TC509+TC510+TC511+TC512+TC513+TC514+TC515+TC516+TC517+TC518)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC533,"(TC520+TC521+TC522+TC523+TC524+TC525+TC526+TC527+TC528+TC529+TC530+TC531+TC532)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC547,"(TC534+TC535+TC536+TC537+TC538+TC539+TC540+TC541+TC542+TC543+TC544+TC545+TC546)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC561,"(TC548+TC549+TC550+TC551+TC552+TC553+TC554+TC555+TC556+TC557+TC558+TC560)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC575,"(TC562+TC563+TC564+TC565+TC566+TC567+TC568+TC569+TC570+TC571+TC572+TC574)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC589,"(TC576+TC577+TC578+TC579+TC580+TC581+TC582+TC583+TC584+TC585+TC586+TC588)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC603,"(TC590+TC591+TC592+TC593+TC594+TC595+TC596+TC597+TC598+TC599+TC600+TC602)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC617,"(TC604+TC605+TC606+TC607+TC608+TC609+TC610+TC611+TC612+TC613+TC614+TC615+TC616)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC631,"(TC618+TC619+TC620+TC621+TC622+TC623+TC624+TC625+TC626+TC627+TC628+TC629+TC630)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC728,"(TC715+TC716+TC717+TC718+TC719+TC720+TC721+TC722+TC723+TC724+TC725+TC726+TC727)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC742,"(TC729+TC730+TC731+TC732+TC733+TC734+TC735+TC736+TC737+TC738+TC739+TC740+TC741)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TC645,"(TC632+TC633+TC634+TC635+TC636+TC637+TC638+TC639+TC640+TC641+TC642+TC643+TC644)");

	}
	
	// LIQUIDACION I
	
	static { 
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ500,"PG500");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ301,"(PG326<0)?(PG326*-1):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ302,"(PG326>0)?(PG326):(0.0)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ501,"(LQ500+LQ301-LQ302)");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1230,"isGroup()?LQ1230:0.0"); 
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1231,"isGroup()?LQ1231:0.0");
		
		// Detalle de las correcciones al resultado contable
		
		String dc2301 = ""; // Sumatorio elemento 1 detalleAumento 
		String dc2303 = ""; // Sumatorio elemento 2 detalleAumento  
		String dc2307 = ""; // Sumatorio elemento 3 detalleAumento
    	String dc2305 = ""; // Sumatorio elemento 4 detalleAumento
		String dc2309 = ""; // Sumatorio elemento 5 detalleAumento
		                
		String dc2302 = ""; // Sumatorio elemento 1 detalleDisminucion
		String dc2304 = ""; // Sumatorio elemento 2 detalleDisminucion
		String dc2308 = ""; // Sumatorio elemento 3 detalleDisminucion
		String dc2306 = ""; // Sumatorio elemento 4 detalleDisminucion
		String dc2310 = ""; // Sumatorio elemento 5 detalleDisminucion
		
		String i0417 = ""; // Total correcciones aumentos
		String d0418 = ""; // Total correcciones disminuciones
		
		for (Mod2002022CorrectionKey key : Mod2002022CorrectionKey.values()) {
			// Aumento
			if (key.getIncrease() != null) {
				String detailKeys = "";
				Mod2002022KeyDC[] detailIncrease = key.getDetailIncrease();
				for (int i=1; i<=3; i++) {
					Mod2002022KeyDC key2 = detailIncrease[i];
					if (key2!=null) {
						if (!detailKeys.isEmpty()) 
							detailKeys = detailKeys + "+";
						detailKeys = detailKeys + key2.toString();
					}
				}
				
				COMPUTE_EXPRESSION_MAP.put(key.getIncrease(), detailKeys);
				
				if (detailIncrease[0] != null)
					dc2305 = dc2305 + (dc2305.isEmpty()?"":"+")+detailIncrease[0].toString(); 
				if (detailIncrease[1] != null)
					dc2301 = dc2301 + (dc2301.isEmpty()?"":"+")+detailIncrease[1].toString(); 
				if (detailIncrease[2] != null)
					dc2303 = dc2303 + (dc2303.isEmpty()?"":"+")+detailIncrease[2].toString(); 
				if (detailIncrease[3] != null)
					dc2307 = dc2307 + (dc2307.isEmpty()?"":"+")+detailIncrease[3].toString(); 
				if (detailIncrease[4] != null)
					dc2309 = dc2309 + (dc2309.isEmpty()?"":"+")+detailIncrease[4].toString(); 
				
				i0417 = i0417 + (i0417.isEmpty()?"":"+")+key.getIncrease().toString(); // Total correcciones aumentos
				
			}
			
			// Disminucion
			if (key.getDecrease() != null) {
				String detailKeys = "";
				Mod2002022KeyDC[] detailDecrease = key.getDetailDecrease();
				for (int i=1; i<=3; i++) {
					Mod2002022KeyDC key2 = detailDecrease[i];
					if (key2!=null) {
						if (!detailKeys.isEmpty()) 
							detailKeys = detailKeys + "+";
						detailKeys = detailKeys + key2.toString();
					}
				}
				if (key.getDecrease() == Mod2002022Key.D1004) {
					COMPUTE_EXPRESSION_MAP.put(key.getDecrease(), "computeD1004(" + detailKeys + ")");
				} else {
					COMPUTE_EXPRESSION_MAP.put(key.getDecrease(), detailKeys);
				}
				
				if (detailDecrease[0]!=null)
					dc2306 = dc2306 + (dc2306.isEmpty()?"":"+")+detailDecrease[0].toString(); 
				if (detailDecrease[1]!=null)
					dc2302 = dc2302 + (dc2302.isEmpty()?"":"+")+detailDecrease[1].toString(); 
				if (detailDecrease[2]!=null)
					dc2304 = dc2304 + (dc2304.isEmpty()?"":"+")+detailDecrease[2].toString(); 
				if (detailDecrease[3]!=null)
					dc2308 = dc2308 + (dc2308.isEmpty()?"":"+")+detailDecrease[3].toString(); 
				if (detailDecrease[4]!=null)
					dc2310 = dc2310 + (dc2310.isEmpty()?"":"+")+detailDecrease[4].toString(); 
				
				d0418 = d0418 + (d0418.isEmpty()?"":"+")+key.getDecrease().toString(); // Total correcciones disminuciones				
				
			}
			
		}
		
		// Totales de los desgloses de las correcciones
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2301, dc2301);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2303, dc2303);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2307, dc2307);
    	COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2305, dc2305);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2309, dc2309);

		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2302, dc2302);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2304, dc2304);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2308, dc2308);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2306, dc2306);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.DC2310, dc2310);
		
		// Total correcciones al resultado de la cuenta de pérdidas y ganancias
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.I0417, i0417);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.D0418, d0418);
		
        COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.I0417B,"I0417");
        COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.D0418B,"D0418");

	}
	
	// LIQUIDACION II
	
	static {
		
		// Entidades navieras en regimen  de tributación en función del tonelaje
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ578,"C0022?(LQ501+LQ1230-LQ1231+I0417-D0418):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ579,"C0022?(LQ630+(LQ631-LQ632)):0.0");
		
	}
	
	static { 
		
		// Entidades que forman parte de grupos de consolidación fiscal
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1029,"(C0009 || C0010)?(LQ501+LQ1230-LQ1231+I0417-D0418):(0.0)");
		
		// Base imponible
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ550,"computeLQ550()");
		
		// Casilla 1032: Reserva de capitalización		
		addBreakdown(Mod2002022LQ1032Key.values(), Mod2002022Key.LQ1032);
		
		// FALTA - CONTROL CASILLA 1032
		// Si 1032 > 10% (00550 - 00415 + 00211 - 00416 + 00543); hacer 01032 = 10% (00550 - 00415 + 00211 - 00416 + 00543)
		// SI LA CASILLA 1032 LLEVA DESGLOSE Y SE DEBE RECALCULAR, HABRIA QUE RECALCULAR TAMBIEN LAS CASILLAS DEL DESGLOSE EN ORDEN INVERSO SUPONGO
		
		// FALTA - NUEVAS CASILLAS 541 Y 564 NO SE SI LLEVAN ALGUN CALCULO ESPECIAL
//		Para el régimen especial de buques y empresas navieras, para el cálculo de la clave 00550 no se ha realizado aún el desglose de bases imponibles (claves 00541 y 00564), por lo que deberá tenerse en cuenta la limitación de que las bases negativas del régimen especial no pueden compersarse con las positivas del resto de actividades. Por tanto, en aquellos supuestos que 00541 tenga importe negativo y 00564 positivo, se sustituirá 00550 por 00564.
//		Si marca 00069 y 00541 con importe negativo y 00564 con importe positivo, entonces:
//		01032 <= 10% (00564 - 00415 + 00211 - 00416 + 00543);
//		Si 1032 > 10% (00564 - 00415 + 00211 - 00416 + 00543); hacer 01032 = 10% (00564 - 00415 + 00211 - 00416 + 00543)

		// Casilla 547: Compensación de bases imponibles negativas de períodos anteriores
		addBreakdown(Mod2002022LQ547Key.values(), Mod2002022Key.LQ547);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1049,"LQ1048");
		
		// Casillas 1887, 1890
		// Compensación de bases imponibles negativas períodos anteriores de la parte de base imponible régimen especial [01887]
		// Compensación de bases imponibles negativas períodos anteriores de la parte de base imponible resto de actividades [01890]		
		addBreakdown(Mod2002022LQ243Key.values(), Mod2002022Key.LQ1886, true, Mod2002022Key.LQ168 , Mod2002022Key.LQ202 );
		addBreakdown(Mod2002022LQ243Key.values(), Mod2002022Key.LQ1889, true, Mod2002022Key.LQ168 , Mod2002022Key.LQ202 );
		addBreakdown(Mod2002022LQ243Key.values(), Mod2002022Key.LQ216 , true, Mod2002022Key.LQ1886, Mod2002022Key.LQ1889);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ267,"LQ266");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ344,"LQ290-LQ2465");
		
		// Base imponible		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ552,"computeLQ552()");		
				
		// Casilla 1033: Reserva de nivelación - Reducción de la base imponible 
		// Lleva cálculos especiales en las casillas de la columna 2
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1456,"LQ1601-LQ1455");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1962,"computeLQ1033_1(LQ1961,LQ1602,LQ1456)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2239,"computeLQ1033_1(LQ2238,LQ1603,LQ1456+LQ1962)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2411,"computeLQ1033_1(LQ2410,LQ1604,LQ1456+LQ1962+LQ2239)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1730,"computeLQ1033_1(LQ1109,LQ1605,LQ1456+LQ1962+LQ2239+LQ2411)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1404,"computeLQ1033_1(LQ1406,LQ1405,LQ1456+LQ1962+LQ2239+LQ2411+LQ1730)");
		
		// Además las casillas de la última columna, no siguen la regla de la diferencia de las dos columnas anteriores
		// FALTA - IGUAL LO PUEDE CALCULAR YA ADDBREAKDOWN CON LO QUE HE PUESTO PARA LA 1039 Y 2314, EXCEPTO LAS DOS ULTIMAS FILAS 
//		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1963,"LQ1961-LQ1962-LQ1602");
//		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2240,"LQ2238-LQ2239-LQ1603");
//		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2412,"LQ2410-LQ2411-LQ1604");
//		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1111,"LQ1109-LQ1730-LQ1605");
////		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1407,"LQ1406-LQ1404-LQ1405");  
////		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1034A,"computeLQ1034A()"); // Penúltima fila (casilla 1034)
////		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1731,"LQ1034A");
//		
//		// FALTA - ESTO ES LO QUE PONE EN EL PADIS, MEZCLA LAS DOS LINEAS DEL 2022 COMPROBARLO CON LA VALIDACION
////		01731 = 01034 - 01404 - 01405
////		01407 = 01406
//		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1034A,"computeLQ1034A()"); // Penúltima fila (casilla 1034)		
//		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1731,"LQ1034A-LQ1404-LQ1405");
//		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1047,"LQ1406");
//		
//		// Fila de totales 
//		addBreakdown(Mod2002022LQ1033_1Key.values(), Mod2002022Key.LQ1033, false);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1034A,"computeLQ1034A()"); // Penúltima fila (casilla 1034)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1731,"LQ1034A-LQ1404-LQ1405");
		addBreakdown(Mod2002022LQ1033_1Key.values(), Mod2002022Key.LQ1033);
		
		// NO ESTOY NADA CONVENCIDO DE ESTO PUES SI LA BASE ES NEGATIVA LA CASILLA 1731 SE QUEDA NEGATIVA POR QUE LA 1034 ES CERO... VOLVER A RELEER DE NUEVO Y COMPROBAR CON VALIDACION
	// FALTA - ESTO ES LO QUE PONE EN EL PADIS, MEZCLA LAS DOS LINEAS DEL 2022 COMPROBARLO CON LA VALIDACION
//	01731 = 01034 - 01404 - 01405
//	01407 = 01406

	COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1407,"LQ1406"); 
	
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1034,"LQ1034A");
		
		// Reserva de nivelación - Dotacion de la reserva (no lleva columna de totales)
		addBreakdown(Mod2002022LQ1033_2Key.values(), Mod2002022Key.LQ1158, false);
		
		// Base imponible después de la reserva de nivelación
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1330,"C0006?LQ552+LQ1033-LQ1034:LQ552");

		// Sólo entidades cooperativas (Casillas 553 y 554)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.CP0C6,"(isCooperativa())?CP0C1-CP0C2-CP0C3-CP0C4:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.CP0E6,"(isCooperativa())?CP0E1-CP0E2-CP0E3-CP0E4+CP0E5:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.CPC12,"(isCooperativa())?CP0C6+CP0C7-CP0C8-CP0C9+CPC10+CPC11:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.CPE12,"(isCooperativa())?CP0E6+CP0E7-CP0E8-CP0E9+CPE10+CPE11:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ553,"(isCooperativa())?CPC12:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ554,"(isCooperativa())?CPE12:0.0");
		
		// Solo SOCIMIS (Casillas 520 y 521 automaticas, solo si caracter 12 o 64, si no, son manuales)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ520,"(C0012 || C0064)?LQ550TG-LQ1032-LQ547:LQ520");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ521,"(C0012 || C0064)?LQ550T0:LQ521");
		
		// Rentas que no limitan la compensación de bases imponibles y cuotas negativas
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ545,"(LQ545<0.0)?0.0:LQ545");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1509,"(LQ1509<0.0)?0.0:LQ1509");
		
		// Tipo de gravamen
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ558,"computeLQ558()");
		
		// Sólo entidades cooperativas
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ560,"computeLQ560()");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ593,"(LQ593<0.0)?0.0:LQ593");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1510,"(LQ1510<0.0)?0.0:LQ1510");		
		
		// Casilla 561: Compensación de cuotas por pérdidas de cooperativas 
		addBreakdown(Mod2002022LQ561Key.values(), Mod2002022Key.LQ561);
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1226,"LQ1225");
		
		// Cuota íntegra
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1331,"(isCooperativa() && (LQ560+LQ210-LQ480+LQ408-LQ1037-LQ561+LQ1285-LQ1286)>0)?(LQ560+LQ210-LQ480+LQ408-LQ1037-LQ561+LQ1285-LQ1286):(0.0)");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ562,"(isCooperativa())?(LQ1331):(computeLQ562())");
		
	}
	
	// LIQUIDACION III
	
	static { 
		
		// Casilla 570: DI interna de periodos anteriores aplicada en el ejercicio (art.30 RDL 4/2004)
		addPreBreakdownDoubleImposition(Mod2002022BN570Key.values(), Mod2002022Key.BN570, Mod2002022Key.BN103A);
		addBreakdown(Mod2002022BN570Key.values(), Mod2002022Key.BN570);
		
		// Casilla 1344: DI interna de periodos anteriores aplicada en el ejercicio (DT 23.1 LIS)
		addPreBreakdownDoubleImposition(Mod2002022BN1344Key.values(), Mod2002022Key.BN1344, Mod2002022Key.BN103B);
		addBreakdown(Mod2002022BN1344Key.values(), Mod2002022Key.BN1344);
		
		// Casilla 1280: DI interna generada y aplicada en el ejercicio (DT 23ª.1 LIS)
		addBreakdown(Mod2002022BN1280Key.values(), Mod2002022Key.BN1280);
		
		// Casilla 572: DI internacional de períodos anteriores aplicada en el ejercicio (RDL 4/2004)
		addPreBreakdownDoubleImposition(Mod2002022BN572Key.values(), Mod2002022Key.BN572, Mod2002022Key.BN103C);
		addBreakdown(Mod2002022BN572Key.values(), Mod2002022Key.BN572);
		
		// Casilla 571: DI internacional de períodos anteriores aplicada en el ejercicio (LIS)
		addPreBreakdownDoubleImposition(Mod2002022BN571Key.values(),Mod2002022Key.BN571, Mod2002022Key.BN103D);
		addBreakdown(Mod2002022BN571Key.values(),Mod2002022Key.BN571);

		// Casilla 573: DI internacional generada y aplicada en el ejercicio actual (arts. 31 y 32 LIS)
		addBreakdown(Mod2002022BN573Key.values(),Mod2002022Key.BN573);
		
		// Casilla 582: Cuota íntegra ajustada positiva
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN582,"(LQ562+LQ1038)-(BN567+BN568+BN563+BN566+BN576+BN569+BN570+BN1344+BN1280+BN572+BN571+BN573+BN575+BN577+BN581)");
	}
	
	// LIQUIDACION IV
	
	static { 
		
		// Casilla 585: Deducción DT 24ª.7 LIS, art. 42 RDL 4/2004
		addBreakdown(Mod2002022BN585Key.values(), Mod2002022Key.BN585);

		// Casilla 584: Deducciones DT 24ª.1 LIS 
		addBreakdown(Mod2002022BN584Key.values(), Mod2002022Key.BN584);
		
	    // Casilla 588: Deducciones para incentivar determinadas actividades (Cap. IV Tit. VI, DT 24ª.3 LIS y art. 27.3 primero Ley 49/2002)
		// Lleva 2 totales, la fila de las casillas 634, 635 y 636 y la fila de las casillas 831, 588, 832		
		addBreakdown(Mod2002022BN588Key.values(), Mod2002022Key.BN635, true, Mod2002022Key.BN1626, Mod2002022Key.BN1683); 
		addBreakdown(Mod2002022BN588Key.values(), Mod2002022Key.BN588, true, null, Mod2002022Key.BN1683);
		
		// Casilla 1039: Deducciones por producciones cinematográficas extranjeras (art. 36.2 LIS)
		addBreakdown(Mod2002022BN1039Key.values(), Mod2002022Key.BN1039);
		
		// Casilla 2314: Deducciones por producciones cinematográficas extranjeras en Canarias (art. 36.2 LIS y DA 14ª Ley 19/1994)
		addBreakdown(Mod2002022BN2314Key.values(), Mod2002022Key.BN2314);
		
		// Casilla 2315: Deducción por inversiones y gastos realizados por las autoridades portuarias (art. 38 bis LIS)
		addBreakdown(Mod2002022BN2315Key.values(), Mod2002022Key.BN2315);

		// Casilla 565: Deducción donaciones a entidades sin fines de lucro (Ley 49/2002). Tiene dos apartados con varios subtotales
		// Donaciones de carácter general 
		addBreakdown(Mod2002022BN565_1Key.values(), Mod2002022Key.BN1689, true, Mod2002022Key.BN904, Mod2002022Key.BN997);
		addBreakdown(Mod2002022BN565_1Key.values(), Mod2002022Key.BN1692, true, Mod2002022Key.BN246, Mod2002022Key.BN1326);
		addBreakdown(Mod2002022BN565_1Key.values(), Mod2002022Key.BN1695, true, Mod2002022Key.BN246, Mod2002022Key.BN1326);
		addBreakdown(Mod2002022BN565_1Key.values(), Mod2002022Key.BN1698, true, Mod2002022Key.BN1689, Mod2002022Key.BN1695);
		// Donaciones para actividades prioritarias de mecenazgo y otras con derecho a deducción incrementada
		addBreakdown(Mod2002022BN565_2Key.values(), Mod2002022Key.BN1701, true, Mod2002022Key.BN899, Mod2002022Key.BN930);
		addBreakdown(Mod2002022BN565_2Key.values(), Mod2002022Key.BN1704, true, Mod2002022Key.BN933, Mod2002022Key.BN1374);
		addBreakdown(Mod2002022BN565_2Key.values(), Mod2002022Key.BN1729, true, Mod2002022Key.BN933, Mod2002022Key.BN1374);
		addBreakdown(Mod2002022BN565_2Key.values(), Mod2002022Key.BN1079, true, Mod2002022Key.BN1701, Mod2002022Key.BN1729);
		// Total deducciones a entidades sin fines de lucro (Ley 49/2002)
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN598,"BN1698+BN1079");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN565,"BN1699+BN1080");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN895,"BN1700+BN1081");

		// Casilla 590: Deducciones Inversión Canarias con limites incrementados
		addBreakdown(Mod2002022BN590Key.values(), Mod2002022Key.BN590);
		
		// Casilla 082: Deducciones sin límite I + D + i		
		addPreBreakdownBN082();
		addBreakdown(Mod2002022BN082Key.values(),Mod2002022Key.BN082);
		
		// Casilla 1040: Deducción por reversión de medidas temporales DT 37ª.1 LIS
		addPreBreakdownTemporaryMeasures(Mod2002022BN1040Key.values(), Mod2002022Key.BN1040);
		addBreakdown(Mod2002022BN1040Key.values(), Mod2002022Key.BN1040);

		// Casilla 1041: Deducción por reversión de medidas temporales DT 37ª.2 LIS
		addPreBreakdownTemporaryMeasures(Mod2002022BN1041Key.values(), Mod2002022Key.BN1041);
		addBreakdown(Mod2002022BN1041Key.values(), Mod2002022Key.BN1041);
		
		// FALTA - Casilla 619: Cuota líquida mínima (art. 30 bis.2 LIS)  
		// PAGINAS 175-185 DEL PADIS - NUEVA CASILLA COMPROBAR COMO SE CALCULA
		
		// Casilla 592: Cuota líquida 
		// FALTA - COMPROBAR SI AFECTA LA CUOTA MINIMA POR LO QUE HE LEIDO SI DEBE REAJUSTARSE ESTA CASILLA TAMBIEN DEBERIAN REAJUSTARSE LAS POSIBLES 
		// BONIFICACIONES O DEDUCCIONES QUE SE HAYAN HECHO ES DECIR SUPONGO QUE AL FINAL SE DEBE CUMPLIR LA FORMULA DE ESTA CASILLA
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN592,"(BN582<=0.0)?(0.0):(BN582-(BN583+BN585+BN584+BN588+BN1039+BN2314+BN2315+BN565+BN590+BN399+BN082+BN1040+BN1041))");
	}
	
	// LIQUIDACION V
	
	static {
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1766, "BN1785+BN1787+BN1789+BN1791+BN1793+BN1795+BN597+BN1798");	
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1784, "BN1786+BN1788+BN1790+BN1792+BN1794+BN1796+BN1797+BN1799");
		
		// Desglose Tributación Conjunta
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR050, "C0028?(TR051+TR052+TR053+TR054+TR055+TR056):0.0");
		                                                
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR626, "(C0028 && round(TR050-TR051) != 0.0)?round(TR052*100/(TR050-TR051)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR627, "(C0028 && round(TR050-TR051) != 0.0)?round(TR053*100/(TR050-TR051)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR628, "(C0028 && round(TR050-TR051) != 0.0)?round(TR054*100/(TR050-TR051)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR629, "(C0028 && round(TR050-TR051) != 0.0)?round(TR055*100/(TR050-TR051)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR625, "(C0028 && round(TR050-TR051) != 0.0)?(100.00-(TR626+TR627+TR628+TR629)):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR420, "computeTR420()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR421, "computeTR421()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR426, "computeTR426()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR427, "computeTR427()");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR600, "C0028?(TR420+TR421+TR426+TR427):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR602, "C0028?(TR402+TR442+TR443+TR444):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR604, "C0028?(TR445+TR446+TR447+TR448):0.0");		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR606, "C0028?(TR449+TR451+TR450+TR465):0.0");		
		                                                
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR474, "C0028?(TR420-(TR402+TR445+TR449)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR475, "C0028?(TR421-(TR442+TR446+TR450)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR476, "C0028?(TR426-(TR443+TR447+TR451)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR477, "C0028?(TR427-(TR444+TR448+TR465)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR612, "C0028?(TR474+TR475+TR476+TR477):0.0");
		                                                
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR616, "C0028?(TR482+TR483+TR484+TR485 ):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR642, "C0028?(TR913+TR914+TR915+TR916 ):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR618, "C0028?(TR486+TR487+TR488+TR489 ):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1332, "C0028?(TR1334+TR1335+TR1336+TR1337):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1333, "C0028?(TR1338+TR1339+TR1340+TR1341):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1881, "C0028?(TR1877+TR1878+TR1879+TR1880):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1624, "C0028?(TR474+TR482+TR913+TR486-TR1334-TR1338-TR1877):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1625, "C0028?(TR475+TR483+TR914+TR487-TR1335-TR1339-TR1878):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1629, "C0028?(TR476+TR484+TR915+TR488-TR1336-TR1340-TR1879):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1630, "C0028?(TR477+TR485+TR916+TR489-TR1337-TR1341-TR1880):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1587, "C0028?(TR1624+TR1625+TR1629+TR1630):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1583, "C0028?(TR1607+TR1608+TR1609+TR1610):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1585, "C0028?(TR1611+TR1612+TR1613+TR1623):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR494, "C0028?(TR1624-TR1607+TR1611):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR495, "C0028?(TR1625-TR1608+TR1612):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR496, "C0028?(TR1629-TR1609+TR1613):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR497, "C0028?(TR1630-TR1610+TR1623):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR622, "C0028?(TR494+TR495+TR496+TR497):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR2480, "C0028?(TR1631+TR1632+TR1633+TR1634):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR2482, "C0028?(TR1635+TR1636+TR1637+TR1641):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR2484, "C0028?(TR1642+TR1643+TR1644+TR1645):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1646, "C0028 && C0037?(TR1624-TR1635+TR1642):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1647, "C0028 && C0037?(TR1625-TR1636+TR1643):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1648, "C0028 && C0037?(TR1629-TR1637+TR1644):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1649, "C0028 && C0037?(TR1630-TR1641+TR1645):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR2486, "C0028 && C0037?(TR1646+TR1647+TR1648+TR1649):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR2488, "C0028?(TR1650+TR1651+TR1652+TR1653):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1654, "C0028 && C0037?(TR1646-TR1650):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1655, "C0028 && C0037?(TR1647-TR1651):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1656, "C0028 && C0037?(TR1648-TR1652):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1657, "C0028 && C0037?(TR1649-TR1653):0.0");		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR3242, "C0028 && C0037?(TR1654+TR1655+TR1656+TR1657):0.0");
		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1043, "C0028?(TR1300+TR1301+TR1302+TR1303):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR1044, "C0028?(TR1305+TR1306+TR1307+TR1308):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR3245, "C0028?(TR1658+TR1659+TR1660+TR1661):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR3319, "C0028?(TR1662+TR1663+TR1664+TR1665):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR2491, "C0028?(TR1666+TR1667+TR1668+TR1669):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.TR2494, "C0028?(TR1670+TR1671+TR1672+TR1673):0.0");
		
		// Cuota del ejercicio a ingresar o a devolver 
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN599, "C0028?(TR625/100*(BN592-LQ1766-LQ1784)):(BN592-LQ1766-LQ1784)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN600, "TR600");
		
		// Cuota diferencial
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN602, "TR602");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN604, "TR604");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN606, "TR606");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN611, "BN599-(BN601+BN603+BN605)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN612, "TR612");
		
		// Resultado de la autoliquidación
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN616, "TR616");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN642, "TR642");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN618, "TR618");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1332, "TR1332");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1333, "TR1333");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1881, "TR1881");

		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1234B,"BN083+BN1332");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1042,"(C0028)?(BN1892-BN1333):(BN1892)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1893,"(C0028)?(BN1319-BN1881):(BN1319)");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1586, "BN611+BN615+BN633+BN617-BN083-BN1042-BN1893");  
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1587, "TR1587");
		
		// Liquido a ingresar o a devolver
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1583, "TR1583");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ1585, "TR1585");		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN621, "LQ1586-LQ1578+LQ1584");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN622, "TR622");
		
		// Resultado de la autoliquidación incluido el 1er fraccionamiento del art. 19.1 LIS
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2480, "C0037?TR2480:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2482, "C0037?TR2482:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2484, "C0037?TR2484:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2485, "C0037?LQ1586-LQ2481+LQ2483:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2486, "C0037?TR2486:0.0");
		
		// Líquido a ingresar incluido el 1er fraccionamiento del art. 19.1 LIS
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2488, "C0037?TR2488:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2489, "C0037?LQ2485-LQ2487:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ3242, "C0037?TR3242:0.0");
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Administración tributaria
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1043, "TR1043");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.BN1044, "TR1044");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ3245, "TR3245");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ3319, "TR3319");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2491, "TR2491");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2494, "TR2494");	
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM150, "BN1020+BN1043"); 
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM506, "BN1021+BN1044"); 
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ3243, "LQ3244+LQ3245");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ3317, "LQ3318+LQ3319");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ3320, "LQ2490+LQ2491");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LQ2492, "LQ2493+LQ2494");
		
	}
	
	static { 
		
		//  Limitación en la deducibilidad de gastos financieros.
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1240,"isLimitEnabled()?(LM1242+LM1243):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1246,"isLimitEnabled()?(LM1242+LM1245):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1248,"isLimitEnabled()?((LM1247>=LM1246)?0.0:(LM1246-LM1247)):0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1250,"isLimitEnabled()?PG296:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1251,"isLimitEnabled()?PG284:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1252,"isLimitEnabled()?PG285:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1253,"isLimitEnabled()?PG287:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1249,"isLimitEnabled()?computeLM1249():0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1260,"isLimitEnabled()?LM1243+LM1257:0.0");

		 // Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1191,"LM1188-LM1189");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1196,"LM1193-LM1194");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1201,"LM1198-LM1199");		
		addBreakdown(Mod2002022LM1212Key.values(), Mod2002022Key.LM1212, false);

		// Pendiente de adición por límite beneficio operativo no aplicado
		addBreakdown(Mod2002022LM538Key.values(), Mod2002022Key.LM538);
		 
	}
	
	static {
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Admón. tributaria (art. 130, DA 13ª Y DT 33ª LIS)
		
		// Activos por impuesto diferido (AID). DT 33ª y DA 13ª LIS
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1527,"LM1524-LM1525-LM1526");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1528,"LM1527");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1534,"LM1531+LM1532+LM1533");
		addBreakdown(Mod2002022LM1535Key.values(), Mod2002022Key.LM1535, false);
		
		// Activos por impuesto diferido (AID). Art. 130 LIS
		addBreakdown(Mod2002022LM1561Key.values(), Mod2002022Key.LM1561, false);
		
		// Conversión de activos por impuesto diferido en crédito exigible frente a la Admón. tributaria		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM393,"LM1537+LM1567");

		// Exceso cuota líquida positiva (art. 130.1 y DT 33ª.4 LIS). La columna de totales no sigue la regla estandar
		addBreakdown(Mod2002022LM1579Key.values(), Mod2002022Key.LM1579);  
		
	}
	
	static { 

		// APLICACION DE RESULTADOS
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.ID650,"LQ500>0?LQ500:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.ID653,"ID650+ID651+ID652");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.ID654,"ID1270+ID1271+ID1522");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.ID666,"ID654+ID655+ID656+ID658+ID659+ID660+ID662+ID664+ID665");
		
		// Dotaciones por deterioro de créditos u otros activos ...
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.LM1500,"LM1479-LM1480");  // Fila 2022
		addBreakdown(Mod2002022LM1494Key.values(), Mod2002022Key.LM1494, false);  // Fila de totales
		
		// Régimen especial de la reserva para inversiones en Canarias. La última columna no sigue la regla estandar y no tiene fila de totales
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.RC527 ,"RC524-RC525-RC526");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.RC925 ,"RC922-RC923-RC924");		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.RC996 ,"RC1165-RC928-RC938");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.RC1175,"RC1744-RC1168-RC1172");		
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.RC1821,"RC927-RC1820");  
				
	}

	static {
		
		// AGRUPACIONES DE INTERES ECONOMICO Y UTES
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.UT500,"(C0013 || C0014)?LQ500:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.UT552,"(C0013 || C0014)?LQ552:0.0");
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.UT1330,"computeUT1330()");

	}
	
	static {
		
		// Carácter 0027, base imponible negativa o cero
		COMPUTE_EXPRESSION_MAP.put(Mod2002022Key.C0027,"round(LQ552)>0?0.0:1.0");
	}

	// Calculos del desglose de determinadas casillas
	// Se calcula la fila de los totales (totalRowKey = cualquier casilla de la fila de los totales)
	// Se calcula la columna de los totales, se asume que es la ultima columna (si computeTotalCol es true)
	// fromKey y toKey si están cumplimentadas indican las casillas de las filas desde/hasta que deben acumularse para la fila total, se usa cuando la fila total, no son todas las casillas anteriores del desglose
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002022Key totalRowKey, boolean computeTotalCol, Mod2002022Key fromKey, Mod2002022Key toKey) {
		
		String[] totalRow = new String[keysProvider[0].getKeys().length];
		for (int i = 0; i < totalRow.length; i++) {
			totalRow[i] = "";
		}
		
		boolean validKey = false;
		if (fromKey == null)
			validKey = true;
		
		int row = 0;
		for (IMod200KeysProvider kp : keysProvider) {
			
			IMod200Key[] keys = kp.getKeys(); 
			
			if (Arrays.asList(keys).contains(totalRowKey)) {
				// Fila que lleva los totales
				for (int i = 0; i < totalRow.length; i++) {
					if (keys[i] != null) {
						// CasillaS 1032, 1887, 1890, 1039, 2314 y 2315 si están marcados los caracteres 9 o 10, son de cumplimentación directa (no llevan desglose)
						if (keys[i] == Mod2002022Key.LQ1032 || keys[i] == Mod2002022Key.LQ1887 || keys[i] == Mod2002022Key.LQ1890 ||
							keys[i] == Mod2002022Key.BN1039 || keys[i] == Mod2002022Key.BN2314 || keys[i] == Mod2002022Key.BN2315)
							COMPUTE_EXPRESSION_MAP.put((Mod2002022Key) keys[i], "(C0009 || C0010)?" + keys[i] + ":" + totalRow[i]);
						else
							COMPUTE_EXPRESSION_MAP.put((Mod2002022Key) keys[i], totalRow[i]);
					}
				}
				break;
			}
			else { 
				// Resto de filas, se acumulan para los totales				
				if (fromKey != null && Arrays.asList(keys).contains(fromKey))
					validKey = true;
				
				if (!validKey)
					continue;
				
				row++;
				boolean accumulate = true;
				
				// Desglose [565], subtotales [1692] y [1704] solo leen filas impares
				// Desglose [1887][243], subtotal [1886] solo lee filas impares
				if (totalRowKey == Mod2002022Key.BN1692 || totalRowKey == Mod2002022Key.BN1704 ||
					totalRowKey == Mod2002022Key.LQ1886) {
					if (row % 2 == 0)
						accumulate = false;					
				}
				
				// Desglose [565], subtotales [1695] y [1729] solo leen filas pares
				// Desglose [1890][243], subtotal [1889] solo lee filas impares
				if (totalRowKey == Mod2002022Key.BN1695 || totalRowKey == Mod2002022Key.BN1729 ||
					totalRowKey == Mod2002022Key.LQ1889) {
					if (row % 2 != 0)
						accumulate = false;					
				}
				
				if (accumulate) {
					// Acumular para la fila total
					for (int i = 0; i < totalRow.length; i++) {
						if (keys[i] != null)
							totalRow[i] = totalRow[i] + (totalRow[i].isEmpty() ? "" : "+") + keys[i].toString();				
					}
					
					// Desgloses estandar, donde las 3 ultimas columnas son: pendiente inicio/generado, aplicado, pendiente futuro
					// Formula para las 3 últimas columnas 
					// - La última es la diferencia entre la antepenúltima y la penúltima
					if (computeTotalCol) {
						Mod2002022Key key1 = (Mod2002022Key) keys[totalRow.length-3];
						Mod2002022Key key2 = (Mod2002022Key) keys[totalRow.length-2];
						Mod2002022Key key3 = (Mod2002022Key) keys[totalRow.length-1];
						// Desglose casilla 082: Lleva una columna más al final que es informativa, luego se cogen las 3 anteriores
						if (totalRowKey == Mod2002022Key.BN082) {
							key1 = (Mod2002022Key) keys[totalRow.length-4];
							key2 = (Mod2002022Key) keys[totalRow.length-3];
							key3 = (Mod2002022Key) keys[totalRow.length-2];
						}
						// Desgloses Casillas 1039, 2314, 1579, 1033, llevan 4 columnas y la columna total es la primera menos las dos siguientes						
						if (totalRowKey == Mod2002022Key.BN1039 || totalRowKey == Mod2002022Key.BN2314 || 
							totalRowKey == Mod2002022Key.LM1579 || totalRowKey == Mod2002022Key.LQ1033) {
							Mod2002022Key key0 = (Mod2002022Key) keys[totalRow.length-4];
							if (key3 != null && key2 != null && key1 != null && key0 != null) {
								COMPUTE_EXPRESSION_MAP.put(key3 , key0.toString() + "-" + key1.toString() + "-" + key2.toString());
							}
						}
						else 
						{							
							if (key3 != null && key2 != null && key1 != null) {
								COMPUTE_EXPRESSION_MAP.put(key3 , key1.toString() + "-" + key2.toString());
							}
						}
					}
				}
				
				if (toKey != null && Arrays.asList(keys).contains(toKey))
					validKey = false;				
			}
		}		
	}
	
	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002022Key totalRowKey, boolean computeTotalCol) {
		addBreakdown(keysProvider, totalRowKey, computeTotalCol, null, null);
	}

	private static void addBreakdown(IMod200KeysProvider[] keysProvider, Mod2002022Key totalRowKey) {
		addBreakdown(keysProvider, totalRowKey, true, null, null);	
	}
	
	// Cálculo de la columna 3 de determinados apartados de desglose de Deducciones Doble Imposición
	private static void addPreBreakdownDoubleImposition(IMod200KeysProvider[] keysProvider, Mod2002022Key totalRowKey, Mod2002022Key taxRateKey) {
		
		for (IMod200KeysProvider kp : keysProvider) {
			
			IMod200Key[] keys = kp.getKeys(); 
			
			if (Arrays.asList(keys).contains(totalRowKey)) {
				// Hemos llegado a la fila que contiene los totales
				break;
			}
			else { 
				// Resto de filas, se añade para calcular la columna 3				
				Mod2002022Key key1 = (Mod2002022Key) keys[0];  // Deduccion pendiente
				Mod2002022Key key2 = (Mod2002022Key) keys[1];  // Tipo de gravamen periodo de generación
				Mod2002022Key key3 = (Mod2002022Key) keys[2];  // 2022 deducción pendiente
				
				COMPUTE_EXPRESSION_MAP.put(key3, "("+taxRateKey.toString()+"==0||"+key2.toString()+"==0||LQ562==0)?("+key1.toString()+"):(round("+key1.toString()+"*"+taxRateKey.toString()+"/"+key2.toString()+"))");
			}
		}		
	}
	
	// Calculos de la columna 2 del desglose de la casilla [00082], excepto la fila de totales (col2 = col1 * 0.8)
	private static void addPreBreakdownBN082() {
		
		for (IMod200KeysProvider kp : Mod2002022BN082Key.values()) {
			
			IMod200Key[] keys = kp.getKeys(); 
			
			if (Arrays.asList(keys).contains(Mod2002022Key.BN082)) {
				// Fila que lleva los totales
				break;
			}
			else { 
				// Resto de filas, se calcula la columna 2
				Mod2002022Key key1 = (Mod2002022Key) keys[0];
				Mod2002022Key key2 = (Mod2002022Key) keys[1];
				if (key2 != null && key1 != null) {
					COMPUTE_EXPRESSION_MAP.put(key2, "round("+key1.toString()+"*0.8)");
				}
			}
		}		
	}
	
	// Cálculo de la columna 2 de los desgloses de Deducción por reversión de medidas temporales
	private static void addPreBreakdownTemporaryMeasures(IMod200KeysProvider[] keysProvider, Mod2002022Key totalRowKey) {
		
		boolean firstRow = true;
		for (IMod200KeysProvider kp : keysProvider) {
			
			IMod200Key[] keys = kp.getKeys(); 
			
			if (Arrays.asList(keys).contains(totalRowKey)) {				
				break; // Hemos llegado a la fila que contiene los totales
			}
			else { 
				// Resto de filas, se añade para calcular la columna 2				
				Mod2002022Key key1 = (Mod2002022Key) keys[0];  // Base de deducción
				Mod2002022Key key2 = (Mod2002022Key) keys[1];  // Importe generado/pendiente al principio del período				
				// Primera fila se multiplica por 0,02, el resto por 0,05
				if (firstRow)
					COMPUTE_EXPRESSION_MAP.put(key2, "round("+key1.toString()+"*0.02)");
				else 
					COMPUTE_EXPRESSION_MAP.put(key2, "round("+key1.toString()+"*0.05)");				
			}
			firstRow = false;
		}		
	}	
	
	public static void main(String[] args) {
				
		for (int i = 0; i < COMPUTE_EXPRESSION_MAP.size(); i++) {
			System.out.println(
					COMPUTE_EXPRESSION_MAP.keySet().toArray()[i] + " -> " +
					COMPUTE_EXPRESSION_MAP.values().toArray()[i]   );			
		}
		
    }

}