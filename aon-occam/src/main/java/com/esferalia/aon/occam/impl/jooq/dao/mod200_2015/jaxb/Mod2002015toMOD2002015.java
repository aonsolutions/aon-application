package com.esferalia.aon.occam.impl.jooq.dao.mod200_2015.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.DoubleVariable2015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002015toMOD2002015 {

	public static MOD2002015 getMOD2002015(Mod2002015 mod200) {
		MOD2002015 mod = new MOD2002015();
		mod.setNormal( getNormal(mod200) );
		return mod;
	}

	private static TipoNormal getNormal(Mod2002015 mod200) {
		TipoNormal normal = new TipoNormal();
		normal.setBalance(getBalance(mod200));
		normal.setCuentaPyG(getCuentaPyG(mod200));
		normal.setCambiosPN(getCambiosPN(mod200));
		return normal;
	}

	private static TipoBalanceNormal getBalance(Mod2002015 mod200) {
		TipoBalanceNormal tipoBalanceNormal = new TipoBalanceNormal();
		tipoBalanceNormal.setPagina03(getPagina03(mod200));
		tipoBalanceNormal.setPagina04(getPagina04(mod200));
		tipoBalanceNormal.setPagina05(getPagina05(mod200));
		tipoBalanceNormal.setPagina06(getPagina06(mod200));
		return tipoBalanceNormal;
	}

	private static TipoPagina03 getPagina03(Mod2002015 mod200) {
		TipoPagina03 pagina03 = new TipoPagina03();
		pagina03.setT00103(getKey(mod200,Mod2002015Key.BA103));
		pagina03.setT00104(getKey(mod200,Mod2002015Key.BA104));
		pagina03.setT00105(getKey(mod200,Mod2002015Key.BA105));
		pagina03.setT00106(getKey(mod200,Mod2002015Key.BA106));
		pagina03.setT00107(getKey(mod200,Mod2002015Key.BA107));
		pagina03.setT00108(getKey(mod200,Mod2002015Key.BA108));
		pagina03.setT00700(getKey(mod200,Mod2002015Key.BA700));
		pagina03.setT00701(getKey(mod200,Mod2002015Key.BA701));
		pagina03.setT00109(getKey(mod200,Mod2002015Key.BA109));
		pagina03.setT00110(getKey(mod200,Mod2002015Key.BA110));
		pagina03.setT00111(getKey(mod200,Mod2002015Key.BA111));
		pagina03.setT00112(getKey(mod200,Mod2002015Key.BA112));
		pagina03.setT00113(getKey(mod200,Mod2002015Key.BA113));
		pagina03.setT00114(getKey(mod200,Mod2002015Key.BA114));
		pagina03.setT00115(getKey(mod200,Mod2002015Key.BA115));
		pagina03.setT00116(getKey(mod200,Mod2002015Key.BA116));
		pagina03.setT00117(getKey(mod200,Mod2002015Key.BA117));
		pagina03.setT00119(getKey(mod200,Mod2002015Key.BA119));
		pagina03.setT00120(getKey(mod200,Mod2002015Key.BA120));
		pagina03.setT00121(getKey(mod200,Mod2002015Key.BA121));
		pagina03.setT00122(getKey(mod200,Mod2002015Key.BA122));
		pagina03.setT00123(getKey(mod200,Mod2002015Key.BA123));
		pagina03.setT00124(getKey(mod200,Mod2002015Key.BA124));
		pagina03.setT00125(getKey(mod200,Mod2002015Key.BA125));
		pagina03.setT00127(getKey(mod200,Mod2002015Key.BA127));
		pagina03.setT00128(getKey(mod200,Mod2002015Key.BA128));
		pagina03.setT00129(getKey(mod200,Mod2002015Key.BA129));
		pagina03.setT00130(getKey(mod200,Mod2002015Key.BA130));
		pagina03.setT00131(getKey(mod200,Mod2002015Key.BA131));
		pagina03.setT00132(getKey(mod200,Mod2002015Key.BA132));
		pagina03.setT00133(getKey(mod200,Mod2002015Key.BA133));
		pagina03.setT00134(getKey(mod200,Mod2002015Key.BA134));
		pagina03.setT00135(getKey(mod200,Mod2002015Key.BA135));
		pagina03.setT00137(getKey(mod200,Mod2002015Key.BA137));
		pagina03.setT00138(getKey(mod200,Mod2002015Key.BA138));
		pagina03.setT00139(getKey(mod200,Mod2002015Key.BA139));
		pagina03.setT00140(getKey(mod200,Mod2002015Key.BA140));
		pagina03.setT00141(getKey(mod200,Mod2002015Key.BA141));
		pagina03.setT00142(getKey(mod200,Mod2002015Key.BA142));
		pagina03.setT00143(getKey(mod200,Mod2002015Key.BA143));
		pagina03.setT00144(getKey(mod200,Mod2002015Key.BA144));
		pagina03.setT00145(getKey(mod200,Mod2002015Key.BA145));
		pagina03.setT00146(getKey(mod200,Mod2002015Key.BA146));
		pagina03.setT00147(getKey(mod200,Mod2002015Key.BA147));
		pagina03.setT00148(getKey(mod200,Mod2002015Key.BA148));
		return pagina03;
	}

	private static TipoPagina04 getPagina04(Mod2002015 mod200) {
		TipoPagina04 pagina04 = new TipoPagina04();
//	    pagina04.setT150(getKey(mod200,Mod200Key.BA150));
	    pagina04.setT00151(getKey(mod200,Mod2002015Key.BA151));
	    pagina04.setT00152(getKey(mod200,Mod2002015Key.BA152));
	    pagina04.setT00153(getKey(mod200,Mod2002015Key.BA153));
	    pagina04.setT00154(getKey(mod200,Mod2002015Key.BA154));
	    pagina04.setT00155(getKey(mod200,Mod2002015Key.BA155));
	    pagina04.setT00156(getKey(mod200,Mod2002015Key.BA156));
	    pagina04.setT00157(getKey(mod200,Mod2002015Key.BA157));
	    pagina04.setT00158(getKey(mod200,Mod2002015Key.BA158));
	    pagina04.setT00159(getKey(mod200,Mod2002015Key.BA159));
	    pagina04.setT00161(getKey(mod200,Mod2002015Key.BA161));
	    pagina04.setT00162(getKey(mod200,Mod2002015Key.BA162));
	    pagina04.setT00163(getKey(mod200,Mod2002015Key.BA163));
	    pagina04.setT00164(getKey(mod200,Mod2002015Key.BA164));
	    pagina04.setT00165(getKey(mod200,Mod2002015Key.BA165));
	    pagina04.setT00166(getKey(mod200,Mod2002015Key.BA166));
	    pagina04.setT00167(getKey(mod200,Mod2002015Key.BA167));
	    pagina04.setT00169(getKey(mod200,Mod2002015Key.BA169));
	    pagina04.setT00170(getKey(mod200,Mod2002015Key.BA170));
	    pagina04.setT00171(getKey(mod200,Mod2002015Key.BA171));
	    pagina04.setT00172(getKey(mod200,Mod2002015Key.BA172));
	    pagina04.setT00173(getKey(mod200,Mod2002015Key.BA173));
	    pagina04.setT00174(getKey(mod200,Mod2002015Key.BA174));
	    pagina04.setT00175(getKey(mod200,Mod2002015Key.BA175));
	    pagina04.setT00176(getKey(mod200,Mod2002015Key.BA176));
	    pagina04.setT00177(getKey(mod200,Mod2002015Key.BA177));
	    pagina04.setT00178(getKey(mod200,Mod2002015Key.BA178));
	    pagina04.setT00179(getKey(mod200,Mod2002015Key.BA179));
//	    pagina04.setT180(getKey(mod200,Mod200Key.BA180));
		return pagina04;
	}

	private static TipoPagina05 getPagina05(Mod2002015 mod200) {
		TipoPagina05 pagina05 = new TipoPagina05();
	    pagina05.setT00188(getKey(mod200,Mod2002015Key.BP188));
	    pagina05.setT00189(getKey(mod200,Mod2002015Key.BP189));
	    pagina05.setT00190(getKey(mod200,Mod2002015Key.BP190));
	    pagina05.setT01001(getKey(mod200,Mod2002015Key.BP1001));
	    pagina05.setT01002(getKey(mod200,Mod2002015Key.BP1002));
	    pagina05.setT00192(getKey(mod200,Mod2002015Key.BP192));
	    pagina05.setT00193(getKey(mod200,Mod2002015Key.BP193));
	    pagina05.setT00702(getKey(mod200,Mod2002015Key.BP702));
	    pagina05.setT00194(getKey(mod200,Mod2002015Key.BP194));
	    pagina05.setT00195(getKey(mod200,Mod2002015Key.BP195));
	    pagina05.setT00196(getKey(mod200,Mod2002015Key.BP196));
	    pagina05.setT00197(getKey(mod200,Mod2002015Key.BP197));
	    pagina05.setT00198(getKey(mod200,Mod2002015Key.BP198));
	    pagina05.setT00199(getKey(mod200,Mod2002015Key.BP199));
	    pagina05.setT00200(getKey(mod200,Mod2002015Key.BP200));
	    pagina05.setT00201(getKey(mod200,Mod2002015Key.BP201));
	    pagina05.setT00202(getKey(mod200,Mod2002015Key.BP202));
	    pagina05.setT00203(getKey(mod200,Mod2002015Key.BP203));
	    pagina05.setT00204(getKey(mod200,Mod2002015Key.BP204));
	    pagina05.setT00205(getKey(mod200,Mod2002015Key.BP205));
	    pagina05.setT00206(getKey(mod200,Mod2002015Key.BP206));
	    pagina05.setT00207(getKey(mod200,Mod2002015Key.BP207));
	    pagina05.setT00208(getKey(mod200,Mod2002015Key.BP208));
	    pagina05.setT00209(getKey(mod200,Mod2002015Key.BP209));
	    pagina05.setT00211(getKey(mod200,Mod2002015Key.BP211));
	    pagina05.setT00212(getKey(mod200,Mod2002015Key.BP212));
	    pagina05.setT00213(getKey(mod200,Mod2002015Key.BP213));
	    pagina05.setT00214(getKey(mod200,Mod2002015Key.BP214));
	    pagina05.setT00215(getKey(mod200,Mod2002015Key.BP215));
	    pagina05.setT00217(getKey(mod200,Mod2002015Key.BP217));
	    pagina05.setT00218(getKey(mod200,Mod2002015Key.BP218));
	    pagina05.setT00219(getKey(mod200,Mod2002015Key.BP219));
	    pagina05.setT00220(getKey(mod200,Mod2002015Key.BP220));
	    pagina05.setT00221(getKey(mod200,Mod2002015Key.BP221));
	    pagina05.setT00222(getKey(mod200,Mod2002015Key.BP222));
	    pagina05.setT00223(getKey(mod200,Mod2002015Key.BP223));
	    pagina05.setT00224(getKey(mod200,Mod2002015Key.BP224));
	    pagina05.setT00225(getKey(mod200,Mod2002015Key.BP225));
	    pagina05.setT00226(getKey(mod200,Mod2002015Key.BP226));
	    pagina05.setT00227(getKey(mod200,Mod2002015Key.BP227));
		return pagina05;
	}

	private static TipoPagina06 getPagina06(Mod2002015 mod200) {
		TipoPagina06 pagina06 = new TipoPagina06();
	    pagina06.setT00229(getKey(mod200,Mod2002015Key.BP229));
	    pagina06.setT00230(getKey(mod200,Mod2002015Key.BP230));
	    pagina06.setT00703(getKey(mod200,Mod2002015Key.BP703));
	    pagina06.setT00704(getKey(mod200,Mod2002015Key.BP704));
	    pagina06.setT00232(getKey(mod200,Mod2002015Key.BP232));
	    pagina06.setT00233(getKey(mod200,Mod2002015Key.BP233));
	    pagina06.setT00234(getKey(mod200,Mod2002015Key.BP234));
	    pagina06.setT00235(getKey(mod200,Mod2002015Key.BP235));
	    pagina06.setT00236(getKey(mod200,Mod2002015Key.BP236));
	    pagina06.setT00237(getKey(mod200,Mod2002015Key.BP237));
	    pagina06.setT00238(getKey(mod200,Mod2002015Key.BP238));
	    pagina06.setT00241(getKey(mod200,Mod2002015Key.BP241));
	    pagina06.setT00242(getKey(mod200,Mod2002015Key.BP242));
	    pagina06.setT00243(getKey(mod200,Mod2002015Key.BP243));
	    pagina06.setT00244(getKey(mod200,Mod2002015Key.BP244));
	    pagina06.setT00245(getKey(mod200,Mod2002015Key.BP245));
	    pagina06.setT00246(getKey(mod200,Mod2002015Key.BP246));
	    pagina06.setT00247(getKey(mod200,Mod2002015Key.BP247));
	    pagina06.setT00248(getKey(mod200,Mod2002015Key.BP248));
	    pagina06.setT00249(getKey(mod200,Mod2002015Key.BP249));
	    pagina06.setT00250(getKey(mod200,Mod2002015Key.BP250));
	    pagina06.setT00251(getKey(mod200,Mod2002015Key.BP251));
		return pagina06;
	}

	private static TipoCuentaNormal getCuentaPyG(Mod2002015 mod200) {
		TipoCuentaNormal tipoCuentaNormal = new TipoCuentaNormal();
		tipoCuentaNormal.setPagina07(getPagina07(mod200));
		tipoCuentaNormal.setPagina08(getPagina08(mod200));
		return tipoCuentaNormal;
	}

	private static TipoPagina07 getPagina07(Mod2002015 mod200) {
		TipoPagina07 pagina07 = new TipoPagina07();
		pagina07.setT00255(getKey(mod200,Mod2002015Key.PG255));
	    pagina07.setT00256(getKey(mod200,Mod2002015Key.PG256));
	    pagina07.setT00257(getKey(mod200,Mod2002015Key.PG257));
	    pagina07.setT00705(getKey(mod200,Mod2002015Key.PG705));
	    pagina07.setT00706(getKey(mod200,Mod2002015Key.PG706));
	    pagina07.setT00707(getKey(mod200,Mod2002015Key.PG707));
	    pagina07.setT00708(getKey(mod200,Mod2002015Key.PG708));
	    pagina07.setT00258(getKey(mod200,Mod2002015Key.PG258));
	    pagina07.setT00259(getKey(mod200,Mod2002015Key.PG259));
	    pagina07.setT00260(getKey(mod200,Mod2002015Key.PG260));
// TODO Revisar	    
	    pagina07.setT00261(getKey(mod200,Mod2002015Key.PG261));
	    pagina07.setT00760(getKey(mod200,Mod2002015Key.PG760));
	    pagina07.setT00761(getKey(mod200,Mod2002015Key.PG761));
	    pagina07.setT00262(getKey(mod200,Mod2002015Key.PG262));
	    pagina07.setT00762(getKey(mod200,Mod2002015Key.PG762));
	    pagina07.setT00763(getKey(mod200,Mod2002015Key.PG763));
 // ---------------------	    
	    pagina07.setT00263(getKey(mod200,Mod2002015Key.PG263));
	    pagina07.setT00264(getKey(mod200,Mod2002015Key.PG264));
	    pagina07.setT00266(getKey(mod200,Mod2002015Key.PG266));
	    pagina07.setT00267(getKey(mod200,Mod2002015Key.PG267));
	    pagina07.setT00268(getKey(mod200,Mod2002015Key.PG268));
	    pagina07.setT00269(getKey(mod200,Mod2002015Key.PG269));
	    pagina07.setT00271(getKey(mod200,Mod2002015Key.PG271));
	    pagina07.setT00273(getKey(mod200,Mod2002015Key.PG273));
	    pagina07.setT00274(getKey(mod200,Mod2002015Key.PG274));
	    pagina07.setT00275(getKey(mod200,Mod2002015Key.PG275));
	    pagina07.setT00276(getKey(mod200,Mod2002015Key.PG276));
	    pagina07.setT00277(getKey(mod200,Mod2002015Key.PG277));
	    pagina07.setT00278(getKey(mod200,Mod2002015Key.PG278));
	    pagina07.setT00279(getKey(mod200,Mod2002015Key.PG279));
	    pagina07.setT00280(getKey(mod200,Mod2002015Key.PG280));
	    pagina07.setT00281(getKey(mod200,Mod2002015Key.PG281));
	    pagina07.setT00282(getKey(mod200,Mod2002015Key.PG282));
	    pagina07.setT00283(getKey(mod200,Mod2002015Key.PG283));
	    pagina07.setT00709(getKey(mod200,Mod2002015Key.PG709));
	    pagina07.setT00284(getKey(mod200,Mod2002015Key.PG284));
	    pagina07.setT00285(getKey(mod200,Mod2002015Key.PG285));
	    pagina07.setT00286(getKey(mod200,Mod2002015Key.PG286));
	    pagina07.setT00288(getKey(mod200,Mod2002015Key.PG288));
	    pagina07.setT00289(getKey(mod200,Mod2002015Key.PG289));
	    pagina07.setT00290(getKey(mod200,Mod2002015Key.PG290));
	    pagina07.setT00291(getKey(mod200,Mod2002015Key.PG291));
	    pagina07.setT00292(getKey(mod200,Mod2002015Key.PG292));
	    pagina07.setT00293(getKey(mod200,Mod2002015Key.PG293));
	    pagina07.setT00710(getKey(mod200,Mod2002015Key.PG710));
	    pagina07.setT00294(getKey(mod200,Mod2002015Key.PG294));
	    pagina07.setT00295(getKey(mod200,Mod2002015Key.PG295));
	    pagina07.setT00298(getKey(mod200,Mod2002015Key.PG298));
	    pagina07.setT00299(getKey(mod200,Mod2002015Key.PG299));
	    pagina07.setT00300(getKey(mod200,Mod2002015Key.PG300));
	    pagina07.setT00301(getKey(mod200,Mod2002015Key.PG301));
	    pagina07.setT00302(getKey(mod200,Mod2002015Key.PG302));
	    pagina07.setT00303(getKey(mod200,Mod2002015Key.PG303));
	    pagina07.setT00304(getKey(mod200,Mod2002015Key.PG304));
		return pagina07;
	}

	private static TipoPagina08 getPagina08(Mod2002015 mod200) {
		TipoPagina08 pagina08 = new TipoPagina08();
	    pagina08.setT00306(getKey(mod200,Mod2002015Key.PG306));
	    pagina08.setT00307(getKey(mod200,Mod2002015Key.PG307));
	    pagina08.setT00308(getKey(mod200,Mod2002015Key.PG308));
	    pagina08.setT00309(getKey(mod200,Mod2002015Key.PG309));
	    pagina08.setT00310(getKey(mod200,Mod2002015Key.PG310));
	    pagina08.setT00311(getKey(mod200,Mod2002015Key.PG311));
	    pagina08.setT00312(getKey(mod200,Mod2002015Key.PG312));
	    pagina08.setT00314(getKey(mod200,Mod2002015Key.PG314));
	    pagina08.setT00315(getKey(mod200,Mod2002015Key.PG315));
	    pagina08.setT00316(getKey(mod200,Mod2002015Key.PG316));
	    pagina08.setT00317(getKey(mod200,Mod2002015Key.PG317));
	    pagina08.setT00318(getKey(mod200,Mod2002015Key.PG318));
	    pagina08.setT00319(getKey(mod200,Mod2002015Key.PG319));
	    pagina08.setT00320(getKey(mod200,Mod2002015Key.PG320));
	    pagina08.setT00321(getKey(mod200,Mod2002015Key.PG321));
	    pagina08.setT00322(getKey(mod200,Mod2002015Key.PG322));
	    pagina08.setT00323(getKey(mod200,Mod2002015Key.PG323));
	    pagina08.setT00326(getKey(mod200,Mod2002015Key.PG326));
	    pagina08.setT00328(getKey(mod200,Mod2002015Key.PG328));
	    pagina08.setT00330(getKey(mod200,Mod2002015Key.PG330));
	    pagina08.setT00331(getKey(mod200,Mod2002015Key.PG331));
	    pagina08.setT00332(getKey(mod200,Mod2002015Key.PG332));
		return pagina08;
	}

	private static TipoCambiosPN getCambiosPN(Mod2002015 mod200) {
		TipoCambiosPN tipoCambiosPN = new TipoCambiosPN();
		tipoCambiosPN.setPagina09(getPagina09(mod200));
		tipoCambiosPN.setPagina10(getPagina10(mod200));
		tipoCambiosPN.setPagina11(getPagina11(mod200));
		return tipoCambiosPN;
	}

	private static TipoPagina09 getPagina09(Mod2002015 mod200) {
		TipoPagina09 pagina09 = new TipoPagina09();
	    pagina09.setT00336(getKey(mod200,Mod2002015Key.T0336));
	    pagina09.setT00337(getKey(mod200,Mod2002015Key.T0337));
	    pagina09.setT00338(getKey(mod200,Mod2002015Key.T0338));
	    pagina09.setT00339(getKey(mod200,Mod2002015Key.T0339));
	    pagina09.setT00340(getKey(mod200,Mod2002015Key.T0340));
	    pagina09.setT00341(getKey(mod200,Mod2002015Key.T0341));
	    pagina09.setT00342(getKey(mod200,Mod2002015Key.T0342));
	    pagina09.setT00343(getKey(mod200,Mod2002015Key.T0343));
	    pagina09.setT00344(getKey(mod200,Mod2002015Key.T0344));
	    pagina09.setT00346(getKey(mod200,Mod2002015Key.T0346));
	    pagina09.setT00347(getKey(mod200,Mod2002015Key.T0347));
	    pagina09.setT00348(getKey(mod200,Mod2002015Key.T0348));
	    pagina09.setT00349(getKey(mod200,Mod2002015Key.T0349));
	    pagina09.setT00350(getKey(mod200,Mod2002015Key.T0350));
	    pagina09.setT00351(getKey(mod200,Mod2002015Key.T0351));
	    pagina09.setT00352(getKey(mod200,Mod2002015Key.T0352));
	    pagina09.setT00353(getKey(mod200,Mod2002015Key.T0353));
		return pagina09;
	}

	private static TipoPagina10 getPagina10(Mod2002015 mod200) {
		TipoPagina10 pagina10 = new TipoPagina10();
	    pagina10.setT00380(getKey(mod200,Mod2002015Key.TC380));
	    pagina10.setT00381(getKey(mod200,Mod2002015Key.TC381));
	    pagina10.setT00382(getKey(mod200,Mod2002015Key.TC382));
	    pagina10.setT00383(getKey(mod200,Mod2002015Key.TC383));
	    pagina10.setT00384(getKey(mod200,Mod2002015Key.TC384));
	    pagina10.setT00385(getKey(mod200,Mod2002015Key.TC385));
	    pagina10.setT00386(getKey(mod200,Mod2002015Key.TC386));
	    pagina10.setT00394(getKey(mod200,Mod2002015Key.TC394));
	    pagina10.setT00395(getKey(mod200,Mod2002015Key.TC395));
	    pagina10.setT00396(getKey(mod200,Mod2002015Key.TC396));
	    pagina10.setT00397(getKey(mod200,Mod2002015Key.TC397));
	    pagina10.setT00398(getKey(mod200,Mod2002015Key.TC398));
	    pagina10.setT00399(getKey(mod200,Mod2002015Key.TC399));
	    pagina10.setT00400(getKey(mod200,Mod2002015Key.TC400));
	    pagina10.setT00408(getKey(mod200,Mod2002015Key.TC408));
	    pagina10.setT00409(getKey(mod200,Mod2002015Key.TC409));
	    pagina10.setT00410(getKey(mod200,Mod2002015Key.TC410));
	    pagina10.setT00411(getKey(mod200,Mod2002015Key.TC411));
	    pagina10.setT00412(getKey(mod200,Mod2002015Key.TC412));
	    pagina10.setT00413(getKey(mod200,Mod2002015Key.TC413));
	    pagina10.setT00414(getKey(mod200,Mod2002015Key.TC414));
	    pagina10.setT00436(getKey(mod200,Mod2002015Key.TC436));
	    pagina10.setT00437(getKey(mod200,Mod2002015Key.TC437));
	    pagina10.setT00438(getKey(mod200,Mod2002015Key.TC438));
	    pagina10.setT00439(getKey(mod200,Mod2002015Key.TC439));
	    pagina10.setT00440(getKey(mod200,Mod2002015Key.TC440));
	    pagina10.setT00441(getKey(mod200,Mod2002015Key.TC441));
	    pagina10.setT00442(getKey(mod200,Mod2002015Key.TC442));
	    pagina10.setT00450(getKey(mod200,Mod2002015Key.TC450));
	    pagina10.setT00451(getKey(mod200,Mod2002015Key.TC451));
	    pagina10.setT00452(getKey(mod200,Mod2002015Key.TC452));
	    pagina10.setT00453(getKey(mod200,Mod2002015Key.TC453));
	    pagina10.setT00454(getKey(mod200,Mod2002015Key.TC454));
	    pagina10.setT00455(getKey(mod200,Mod2002015Key.TC455));
	    pagina10.setT00456(getKey(mod200,Mod2002015Key.TC456));
	    pagina10.setT00478(getKey(mod200,Mod2002015Key.TC478));
	    pagina10.setT00479(getKey(mod200,Mod2002015Key.TC479));
	    pagina10.setT00480(getKey(mod200,Mod2002015Key.TC480));
	    pagina10.setT00481(getKey(mod200,Mod2002015Key.TC481));
	    pagina10.setT00482(getKey(mod200,Mod2002015Key.TC482));
	    pagina10.setT00483(getKey(mod200,Mod2002015Key.TC483));
	    pagina10.setT00484(getKey(mod200,Mod2002015Key.TC484));
	    pagina10.setT00492(getKey(mod200,Mod2002015Key.TC492));
	    pagina10.setT00493(getKey(mod200,Mod2002015Key.TC493));
	    pagina10.setT00494(getKey(mod200,Mod2002015Key.TC494));
	    pagina10.setT00495(getKey(mod200,Mod2002015Key.TC495));
	    pagina10.setT00496(getKey(mod200,Mod2002015Key.TC496));
	    pagina10.setT00497(getKey(mod200,Mod2002015Key.TC497));
	    pagina10.setT00498(getKey(mod200,Mod2002015Key.TC498));
	    pagina10.setT00520(getKey(mod200,Mod2002015Key.TC520));
	    pagina10.setT00521(getKey(mod200,Mod2002015Key.TC521));
	    pagina10.setT00522(getKey(mod200,Mod2002015Key.TC522));
	    pagina10.setT00523(getKey(mod200,Mod2002015Key.TC523));
	    pagina10.setT00524(getKey(mod200,Mod2002015Key.TC524));
	    pagina10.setT00525(getKey(mod200,Mod2002015Key.TC525));
	    pagina10.setT00526(getKey(mod200,Mod2002015Key.TC526));
	    pagina10.setT00534(getKey(mod200,Mod2002015Key.TC534));
	    pagina10.setT00535(getKey(mod200,Mod2002015Key.TC535));
	    pagina10.setT00536(getKey(mod200,Mod2002015Key.TC536));
	    pagina10.setT00537(getKey(mod200,Mod2002015Key.TC537));
	    pagina10.setT00538(getKey(mod200,Mod2002015Key.TC538));
	    pagina10.setT00539(getKey(mod200,Mod2002015Key.TC539));
	    pagina10.setT00540(getKey(mod200,Mod2002015Key.TC540));
	    pagina10.setT00548(getKey(mod200,Mod2002015Key.TC548));
	    pagina10.setT00549(getKey(mod200,Mod2002015Key.TC549));
	    pagina10.setT00550(getKey(mod200,Mod2002015Key.TC550));
	    pagina10.setT00551(getKey(mod200,Mod2002015Key.TC551));
	    pagina10.setT00552(getKey(mod200,Mod2002015Key.TC552));
	    pagina10.setT00553(getKey(mod200,Mod2002015Key.TC553));
	    pagina10.setT00554(getKey(mod200,Mod2002015Key.TC554));
	    pagina10.setT00562(getKey(mod200,Mod2002015Key.TC562));
	    pagina10.setT00563(getKey(mod200,Mod2002015Key.TC563));
	    pagina10.setT00564(getKey(mod200,Mod2002015Key.TC564));
	    pagina10.setT00565(getKey(mod200,Mod2002015Key.TC565));
	    pagina10.setT00566(getKey(mod200,Mod2002015Key.TC566));
	    pagina10.setT00567(getKey(mod200,Mod2002015Key.TC567));
	    pagina10.setT00568(getKey(mod200,Mod2002015Key.TC568));
	    pagina10.setT00576(getKey(mod200,Mod2002015Key.TC576));
	    pagina10.setT00577(getKey(mod200,Mod2002015Key.TC577));
	    pagina10.setT00578(getKey(mod200,Mod2002015Key.TC578));
	    pagina10.setT00579(getKey(mod200,Mod2002015Key.TC579));
	    pagina10.setT00580(getKey(mod200,Mod2002015Key.TC580));
	    pagina10.setT00581(getKey(mod200,Mod2002015Key.TC581));
	    pagina10.setT00582(getKey(mod200,Mod2002015Key.TC582));
	    pagina10.setT00590(getKey(mod200,Mod2002015Key.TC590));
	    pagina10.setT00591(getKey(mod200,Mod2002015Key.TC591));
	    pagina10.setT00592(getKey(mod200,Mod2002015Key.TC592));
	    pagina10.setT00593(getKey(mod200,Mod2002015Key.TC593));
	    pagina10.setT00594(getKey(mod200,Mod2002015Key.TC594));
	    pagina10.setT00595(getKey(mod200,Mod2002015Key.TC595));
	    pagina10.setT00596(getKey(mod200,Mod2002015Key.TC596));
	    pagina10.setT00604(getKey(mod200,Mod2002015Key.TC604));
	    pagina10.setT00605(getKey(mod200,Mod2002015Key.TC605));
	    pagina10.setT00606(getKey(mod200,Mod2002015Key.TC606));
	    pagina10.setT00607(getKey(mod200,Mod2002015Key.TC607));
	    pagina10.setT00608(getKey(mod200,Mod2002015Key.TC608));
	    pagina10.setT00609(getKey(mod200,Mod2002015Key.TC609));
	    pagina10.setT00610(getKey(mod200,Mod2002015Key.TC610));
	    pagina10.setT00618(getKey(mod200,Mod2002015Key.TC618));
	    pagina10.setT00619(getKey(mod200,Mod2002015Key.TC619));
	    pagina10.setT00620(getKey(mod200,Mod2002015Key.TC620));
	    pagina10.setT00621(getKey(mod200,Mod2002015Key.TC621));
	    pagina10.setT00622(getKey(mod200,Mod2002015Key.TC622));
	    pagina10.setT00623(getKey(mod200,Mod2002015Key.TC623));
	    pagina10.setT00624(getKey(mod200,Mod2002015Key.TC624));
	    pagina10.setT00715(getKey(mod200,Mod2002015Key.TC715));
	    pagina10.setT00716(getKey(mod200,Mod2002015Key.TC716));
	    pagina10.setT00717(getKey(mod200,Mod2002015Key.TC717));
	    pagina10.setT00718(getKey(mod200,Mod2002015Key.TC718));
	    pagina10.setT00719(getKey(mod200,Mod2002015Key.TC719));
	    pagina10.setT00720(getKey(mod200,Mod2002015Key.TC720));
	    pagina10.setT00721(getKey(mod200,Mod2002015Key.TC721));
	    pagina10.setT00729(getKey(mod200,Mod2002015Key.TC729));
	    pagina10.setT00730(getKey(mod200,Mod2002015Key.TC730));
	    pagina10.setT00731(getKey(mod200,Mod2002015Key.TC731));
	    pagina10.setT00732(getKey(mod200,Mod2002015Key.TC732));
	    pagina10.setT00733(getKey(mod200,Mod2002015Key.TC733));
	    pagina10.setT00734(getKey(mod200,Mod2002015Key.TC734));
	    pagina10.setT00735(getKey(mod200,Mod2002015Key.TC735));
		return pagina10;
	}

	private static TipoPagina11 getPagina11(Mod2002015 mod200) {
		TipoPagina11 pagina11 = new TipoPagina11();
	    pagina11.setT00387(getKey(mod200,Mod2002015Key.TC387));
	    pagina11.setT00388(getKey(mod200,Mod2002015Key.TC388));
	    pagina11.setT00389(getKey(mod200,Mod2002015Key.TC389));
	    pagina11.setT00390(getKey(mod200,Mod2002015Key.TC390));
	    pagina11.setT00391(getKey(mod200,Mod2002015Key.TC391));
	    pagina11.setT00392(getKey(mod200,Mod2002015Key.TC392));
	    pagina11.setT00401(getKey(mod200,Mod2002015Key.TC401));
	    pagina11.setT00402(getKey(mod200,Mod2002015Key.TC402));
	    pagina11.setT00403(getKey(mod200,Mod2002015Key.TC403));
	    pagina11.setT00404(getKey(mod200,Mod2002015Key.TC404));
	    pagina11.setT00405(getKey(mod200,Mod2002015Key.TC405));
	    pagina11.setT00406(getKey(mod200,Mod2002015Key.TC406));
	    pagina11.setT00415(getKey(mod200,Mod2002015Key.TC415));
	    pagina11.setT00416(getKey(mod200,Mod2002015Key.TC416));
	    pagina11.setT00417(getKey(mod200,Mod2002015Key.TC417));
	    pagina11.setT00418(getKey(mod200,Mod2002015Key.TC418));
	    pagina11.setT00419(getKey(mod200,Mod2002015Key.TC419));
	    pagina11.setT00420(getKey(mod200,Mod2002015Key.TC420));
	    pagina11.setT00443(getKey(mod200,Mod2002015Key.TC443));
	    pagina11.setT00444(getKey(mod200,Mod2002015Key.TC444));
	    pagina11.setT00445(getKey(mod200,Mod2002015Key.TC445));
	    pagina11.setT00446(getKey(mod200,Mod2002015Key.TC446));
	    pagina11.setT00448(getKey(mod200,Mod2002015Key.TC448));
	    pagina11.setT00457(getKey(mod200,Mod2002015Key.TC457));
	    pagina11.setT00458(getKey(mod200,Mod2002015Key.TC458));
	    pagina11.setT00461(getKey(mod200,Mod2002015Key.TC461));
	    pagina11.setT00462(getKey(mod200,Mod2002015Key.TC462));
	    pagina11.setT00485(getKey(mod200,Mod2002015Key.TC485));
	    pagina11.setT00486(getKey(mod200,Mod2002015Key.TC486));
	    pagina11.setT00489(getKey(mod200,Mod2002015Key.TC489));
	    pagina11.setT00490(getKey(mod200,Mod2002015Key.TC490));
	    pagina11.setT00499(getKey(mod200,Mod2002015Key.TC499));
	    pagina11.setT00502(getKey(mod200,Mod2002015Key.TC502));
	    pagina11.setT00503(getKey(mod200,Mod2002015Key.TC503));
	    pagina11.setT00504(getKey(mod200,Mod2002015Key.TC504));
	    pagina11.setT00527(getKey(mod200,Mod2002015Key.TC527));
	    pagina11.setT00528(getKey(mod200,Mod2002015Key.TC528));
	    pagina11.setT00529(getKey(mod200,Mod2002015Key.TC529));
	    pagina11.setT00530(getKey(mod200,Mod2002015Key.TC530));
	    pagina11.setT00531(getKey(mod200,Mod2002015Key.TC531));
	    pagina11.setT00532(getKey(mod200,Mod2002015Key.TC532));
	    pagina11.setT00541(getKey(mod200,Mod2002015Key.TC541));
	    pagina11.setT00542(getKey(mod200,Mod2002015Key.TC542));
	    pagina11.setT00543(getKey(mod200,Mod2002015Key.TC543));
	    pagina11.setT00544(getKey(mod200,Mod2002015Key.TC544));
	    pagina11.setT00545(getKey(mod200,Mod2002015Key.TC545));
	    pagina11.setT00546(getKey(mod200,Mod2002015Key.TC546));
	    pagina11.setT00555(getKey(mod200,Mod2002015Key.TC555));
	    pagina11.setT00556(getKey(mod200,Mod2002015Key.TC556));
	    pagina11.setT00557(getKey(mod200,Mod2002015Key.TC557));
	    pagina11.setT00558(getKey(mod200,Mod2002015Key.TC558));
	    pagina11.setT00560(getKey(mod200,Mod2002015Key.TC560));
	    pagina11.setT00569(getKey(mod200,Mod2002015Key.TC569));
	    pagina11.setT00570(getKey(mod200,Mod2002015Key.TC570));
	    pagina11.setT00571(getKey(mod200,Mod2002015Key.TC571));
	    pagina11.setT00572(getKey(mod200,Mod2002015Key.TC572));
	    pagina11.setT00574(getKey(mod200,Mod2002015Key.TC574));
	    pagina11.setT00583(getKey(mod200,Mod2002015Key.TC583));
	    pagina11.setT00584(getKey(mod200,Mod2002015Key.TC584));
	    pagina11.setT00585(getKey(mod200,Mod2002015Key.TC585));
	    pagina11.setT00586(getKey(mod200,Mod2002015Key.TC586));
	    pagina11.setT00588(getKey(mod200,Mod2002015Key.TC588));
	    pagina11.setT00597(getKey(mod200,Mod2002015Key.TC597));
	    pagina11.setT00598(getKey(mod200,Mod2002015Key.TC598));
	    pagina11.setT00599(getKey(mod200,Mod2002015Key.TC599));
	    pagina11.setT00600(getKey(mod200,Mod2002015Key.TC600));
	    pagina11.setT00602(getKey(mod200,Mod2002015Key.TC602));
	    pagina11.setT00611(getKey(mod200,Mod2002015Key.TC611));
	    pagina11.setT00612(getKey(mod200,Mod2002015Key.TC612));
	    pagina11.setT00613(getKey(mod200,Mod2002015Key.TC613));
	    pagina11.setT00614(getKey(mod200,Mod2002015Key.TC614));
	    pagina11.setT00615(getKey(mod200,Mod2002015Key.TC615));
	    pagina11.setT00616(getKey(mod200,Mod2002015Key.TC616));
	    pagina11.setT00625(getKey(mod200,Mod2002015Key.TC625));
	    pagina11.setT00626(getKey(mod200,Mod2002015Key.TC626));
	    pagina11.setT00627(getKey(mod200,Mod2002015Key.TC627));
	    pagina11.setT00628(getKey(mod200,Mod2002015Key.TC628));
	    pagina11.setT00629(getKey(mod200,Mod2002015Key.TC629));
	    pagina11.setT00630(getKey(mod200,Mod2002015Key.TC630));
	    pagina11.setT00722(getKey(mod200,Mod2002015Key.TC722));
	    pagina11.setT00723(getKey(mod200,Mod2002015Key.TC723));
	    pagina11.setT00724(getKey(mod200,Mod2002015Key.TC724));
	    pagina11.setT00725(getKey(mod200,Mod2002015Key.TC725));
	    pagina11.setT00726(getKey(mod200,Mod2002015Key.TC726));
	    pagina11.setT00727(getKey(mod200,Mod2002015Key.TC727));
	    pagina11.setT00736(getKey(mod200,Mod2002015Key.TC736));
	    pagina11.setT00737(getKey(mod200,Mod2002015Key.TC737));
	    pagina11.setT00738(getKey(mod200,Mod2002015Key.TC738));
	    pagina11.setT00739(getKey(mod200,Mod2002015Key.TC739));
	    pagina11.setT00740(getKey(mod200,Mod2002015Key.TC740));
	    pagina11.setT00741(getKey(mod200,Mod2002015Key.TC741));
		return pagina11;
	}
	private static BigDecimal getKey(Mod2002015 mod200, Mod2002015Key key) {
		return getKey(mod200, key, 2);
	}
	
	private static BigDecimal getKey(Mod2002015 mod200, Mod2002015Key key,int scale) {
		DoubleVariable2015 dv = mod200.getVariable(key);
		Double value = dv==null?null:AonMathUtils.round(dv.getValue());
		return (value == null || value == 0.0)
			?null
			:new BigDecimal(Double.toString(value)).setScale(scale,RoundingMode.HALF_UP);
	}
}
