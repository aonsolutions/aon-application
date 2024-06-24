package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.esferalia.aon.occam.mod200.api.model.BalanceType;
import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002023toMOD2002023 {

	public static MOD2002023 getMOD2002023(Mod2002023 mod200) {
		MOD2002023 mod = new MOD2002023();
		mod.setNormal( getNormal(mod200) );
		return mod;
	}

	private static TipoNormal getNormal(Mod2002023 mod200) {
		TipoNormal normal = new TipoNormal();
		normal.setBalance(getBalance(mod200));
		normal.setCuentaPyG(getCuentaPyG(mod200));
		normal.setCambiosPN(getCambiosPN(mod200));
		return normal;
	}

	private static TipoBalanceNormal getBalance(Mod2002023 mod200) {
		TipoBalanceNormal tipoBalanceNormal = new TipoBalanceNormal();
		tipoBalanceNormal.setPagina03(getPagina03(mod200));
		tipoBalanceNormal.setPagina04(getPagina04(mod200));
		tipoBalanceNormal.setPagina05(getPagina05(mod200));
		tipoBalanceNormal.setPagina06(getPagina06(mod200));
		return tipoBalanceNormal;
	}

	private static TipoPagina03 getPagina03(Mod2002023 mod200) {
		TipoPagina03 pagina03 = new TipoPagina03();
		pagina03.setT00103(getKey(mod200,Mod2002023Key.BA103)); 
		pagina03.setT00104(getKey(mod200,Mod2002023Key.BA104)); 
		pagina03.setT00105(getKey(mod200,Mod2002023Key.BA105)); 
		pagina03.setT00106(getKey(mod200,Mod2002023Key.BA106));
		pagina03.setT00107(getKey(mod200,Mod2002023Key.BA107)); 
		pagina03.setT00108(getKey(mod200,Mod2002023Key.BA108)); 
		pagina03.setT00700(getKey(mod200,Mod2002023Key.BA700)); 
		pagina03.setT00109(getKey(mod200,Mod2002023Key.BA109)); 
		pagina03.setT00110(getKey(mod200,Mod2002023Key.BA110));
		if (mod200.getBalanceType() == BalanceType.ABREVIADO || mod200.getBalanceType() == BalanceType.PYMES)
			pagina03.setT00111(getKey(mod200,Mod2002023Key.BA111)); // Solo A,P
		pagina03.setT00112(getKey(mod200,Mod2002023Key.BA112));
		pagina03.setT00113(getKey(mod200,Mod2002023Key.BA113));
		pagina03.setT00114(getKey(mod200,Mod2002023Key.BA114));
		if (mod200.getBalanceType() == BalanceType.ABREVIADO || mod200.getBalanceType() == BalanceType.PYMES)
			pagina03.setT00115(getKey(mod200,Mod2002023Key.BA115)); // Solo A,P 
		pagina03.setT00116(getKey(mod200,Mod2002023Key.BA116));
		pagina03.setT00117(getKey(mod200,Mod2002023Key.BA117));
		pagina03.setT00119(getKey(mod200,Mod2002023Key.BA119));
		pagina03.setT00120(getKey(mod200,Mod2002023Key.BA120));
		pagina03.setT00121(getKey(mod200,Mod2002023Key.BA121));
		pagina03.setT00122(getKey(mod200,Mod2002023Key.BA122));
		pagina03.setT00123(getKey(mod200,Mod2002023Key.BA123));
		pagina03.setT00124(getKey(mod200,Mod2002023Key.BA124));
		pagina03.setT00125(getKey(mod200,Mod2002023Key.BA125));
		pagina03.setT00127(getKey(mod200,Mod2002023Key.BA127));
		pagina03.setT00128(getKey(mod200,Mod2002023Key.BA128));
		pagina03.setT00129(getKey(mod200,Mod2002023Key.BA129));
		pagina03.setT00130(getKey(mod200,Mod2002023Key.BA130));
		pagina03.setT00131(getKey(mod200,Mod2002023Key.BA131));
		pagina03.setT00132(getKey(mod200,Mod2002023Key.BA132));
		pagina03.setT00133(getKey(mod200,Mod2002023Key.BA133));
		pagina03.setT00134(getKey(mod200,Mod2002023Key.BA134));
		pagina03.setT00135(getKey(mod200,Mod2002023Key.BA135));
		pagina03.setT00137(getKey(mod200,Mod2002023Key.BA137));
		if (mod200.getBalanceType() == BalanceType.ABREVIADO || mod200.getBalanceType() == BalanceType.PYMES)
			pagina03.setT00138(getKey(mod200,Mod2002023Key.BA138)); // Solo A,P
		pagina03.setT00139(getKey(mod200,Mod2002023Key.BA139));
		pagina03.setT00140(getKey(mod200,Mod2002023Key.BA140));
		pagina03.setT00142(getKey(mod200,Mod2002023Key.BA142));
		pagina03.setT00143(getKey(mod200,Mod2002023Key.BA143));
		pagina03.setT00145(getKey(mod200,Mod2002023Key.BA145));
		pagina03.setT00146(getKey(mod200,Mod2002023Key.BA146));
		pagina03.setT00147(getKey(mod200,Mod2002023Key.BA147));
		pagina03.setT00148(getKey(mod200,Mod2002023Key.BA148));
		pagina03.setT00701(getKey(mod200,Mod2002023Key.BA701));		
		return pagina03;
	}

	private static TipoPagina04 getPagina04(Mod2002023 mod200) {
		TipoPagina04 pagina04 = new TipoPagina04();
	    pagina04.setT00151(getKey(mod200,Mod2002023Key.BA151));
	    pagina04.setT00152(getKey(mod200,Mod2002023Key.BA152));
	    pagina04.setT00153(getKey(mod200,Mod2002023Key.BA153));
	    pagina04.setT00154(getKey(mod200,Mod2002023Key.BA154));
	    pagina04.setT00155(getKey(mod200,Mod2002023Key.BA155));
	    pagina04.setT00156(getKey(mod200,Mod2002023Key.BA156));
	    pagina04.setT00157(getKey(mod200,Mod2002023Key.BA157));
	    pagina04.setT00158(getKey(mod200,Mod2002023Key.BA158));
	    pagina04.setT00159(getKey(mod200,Mod2002023Key.BA159));
	    pagina04.setT00161(getKey(mod200,Mod2002023Key.BA161));
	    pagina04.setT00162(getKey(mod200,Mod2002023Key.BA162));
	    pagina04.setT00163(getKey(mod200,Mod2002023Key.BA163));
	    pagina04.setT00164(getKey(mod200,Mod2002023Key.BA164));
	    pagina04.setT00165(getKey(mod200,Mod2002023Key.BA165));
	    pagina04.setT00166(getKey(mod200,Mod2002023Key.BA166));
	    pagina04.setT00167(getKey(mod200,Mod2002023Key.BA167));
	    pagina04.setT00169(getKey(mod200,Mod2002023Key.BA169));
	    pagina04.setT00170(getKey(mod200,Mod2002023Key.BA170));
	    pagina04.setT00171(getKey(mod200,Mod2002023Key.BA171));
	    pagina04.setT00172(getKey(mod200,Mod2002023Key.BA172));
	    pagina04.setT00173(getKey(mod200,Mod2002023Key.BA173));
	    pagina04.setT00174(getKey(mod200,Mod2002023Key.BA174));
	    pagina04.setT00175(getKey(mod200,Mod2002023Key.BA175));
	    pagina04.setT00176(getKey(mod200,Mod2002023Key.BA176));
	    if (mod200.getBalanceType() == BalanceType.ABREVIADO || mod200.getBalanceType() == BalanceType.PYMES)
	    	pagina04.setT00177(getKey(mod200,Mod2002023Key.BA177)); // Solo A,P
	    pagina04.setT00178(getKey(mod200,Mod2002023Key.BA178));
	    pagina04.setT00179(getKey(mod200,Mod2002023Key.BA179));
		return pagina04;
	}

	private static TipoPagina05 getPagina05(Mod2002023 mod200) {
		TipoPagina05 pagina05 = new TipoPagina05();
	    pagina05.setT00188(getKey(mod200,Mod2002023Key.BP188));
	    pagina05.setT00189(getKey(mod200,Mod2002023Key.BP189));
	    pagina05.setT00190(getKey(mod200,Mod2002023Key.BP190));
    	pagina05.setT01001(getKey(mod200,Mod2002023Key.BP1001));  
    	pagina05.setT01002(getKey(mod200,Mod2002023Key.BP1002));
    	pagina05.setT00712(getKey(mod200,Mod2002023Key.BP712));
	    pagina05.setT00192(getKey(mod200,Mod2002023Key.BP192));
	    pagina05.setT00193(getKey(mod200,Mod2002023Key.BP193));
	    pagina05.setT00702(getKey(mod200,Mod2002023Key.BP702));
	    pagina05.setT00194(getKey(mod200,Mod2002023Key.BP194));
	    if (mod200.getBalanceType() == BalanceType.ABREVIADO || mod200.getBalanceType() == BalanceType.PYMES)
	    	pagina05.setT00195(getKey(mod200,Mod2002023Key.BP195)); // Solo A,P
	    pagina05.setT00196(getKey(mod200,Mod2002023Key.BP196));
	    pagina05.setT00197(getKey(mod200,Mod2002023Key.BP197));
	    pagina05.setT00198(getKey(mod200,Mod2002023Key.BP198));
	    pagina05.setT00199(getKey(mod200,Mod2002023Key.BP199));
	    pagina05.setT00200(getKey(mod200,Mod2002023Key.BP200));
	    pagina05.setT00201(getKey(mod200,Mod2002023Key.BP201));
	    if (mod200.getBalanceType() == BalanceType.ABREVIADO)
	    	pagina05.setT00202(getKey(mod200,Mod2002023Key.BP202)); // Solo A
	    pagina05.setT00203(getKey(mod200,Mod2002023Key.BP203));
	    pagina05.setT00204(getKey(mod200,Mod2002023Key.BP204));
	    pagina05.setT00205(getKey(mod200,Mod2002023Key.BP205));
	    pagina05.setT00206(getKey(mod200,Mod2002023Key.BP206));
	    pagina05.setT00207(getKey(mod200,Mod2002023Key.BP207));
	    pagina05.setT00208(getKey(mod200,Mod2002023Key.BP208));
	    pagina05.setT00209(getKey(mod200,Mod2002023Key.BP209));
	    if (mod200.getBalanceType() == BalanceType.ABREVIADO || mod200.getBalanceType() == BalanceType.PYMES)
	    	pagina05.setT00211(getKey(mod200,Mod2002023Key.BP211)); // Solo A,P
	    pagina05.setT00212(getKey(mod200,Mod2002023Key.BP212));
	    pagina05.setT00213(getKey(mod200,Mod2002023Key.BP213));
	    pagina05.setT00214(getKey(mod200,Mod2002023Key.BP214));
	    pagina05.setT00215(getKey(mod200,Mod2002023Key.BP215));
	    pagina05.setT00217(getKey(mod200,Mod2002023Key.BP217));
	    pagina05.setT00218(getKey(mod200,Mod2002023Key.BP218));
	    pagina05.setT00219(getKey(mod200,Mod2002023Key.BP219));
	    pagina05.setT00220(getKey(mod200,Mod2002023Key.BP220));
	    pagina05.setT00221(getKey(mod200,Mod2002023Key.BP221));
	    pagina05.setT00222(getKey(mod200,Mod2002023Key.BP222));
	    pagina05.setT00223(getKey(mod200,Mod2002023Key.BP223));
	    pagina05.setT00224(getKey(mod200,Mod2002023Key.BP224));
	    pagina05.setT00225(getKey(mod200,Mod2002023Key.BP225));
	    pagina05.setT00226(getKey(mod200,Mod2002023Key.BP226));
	    pagina05.setT00227(getKey(mod200,Mod2002023Key.BP227));
		return pagina05;
	}

	private static TipoPagina06 getPagina06(Mod2002023 mod200) {
		TipoPagina06 pagina06 = new TipoPagina06();
	    pagina06.setT00229(getKey(mod200,Mod2002023Key.BP229));
	    if (mod200.getBalanceType() == BalanceType.ABREVIADO || mod200.getBalanceType() == BalanceType.PYMES)
	    	pagina06.setT00230(getKey(mod200,Mod2002023Key.BP230)); // Solo A,P
	    pagina06.setT00703(getKey(mod200,Mod2002023Key.BP703));
	    pagina06.setT00704(getKey(mod200,Mod2002023Key.BP704));
	    pagina06.setT00232(getKey(mod200,Mod2002023Key.BP232));
	    pagina06.setT00233(getKey(mod200,Mod2002023Key.BP233));
	    pagina06.setT00234(getKey(mod200,Mod2002023Key.BP234));
	    pagina06.setT00235(getKey(mod200,Mod2002023Key.BP235));
	    pagina06.setT00236(getKey(mod200,Mod2002023Key.BP236));
	    pagina06.setT00237(getKey(mod200,Mod2002023Key.BP237));
	    pagina06.setT00238(getKey(mod200,Mod2002023Key.BP238));
	    pagina06.setT00241(getKey(mod200,Mod2002023Key.BP241));
	    pagina06.setT00242(getKey(mod200,Mod2002023Key.BP242));
	    pagina06.setT00243(getKey(mod200,Mod2002023Key.BP243));
	    pagina06.setT00244(getKey(mod200,Mod2002023Key.BP244));
	    pagina06.setT00245(getKey(mod200,Mod2002023Key.BP245));
	    pagina06.setT00246(getKey(mod200,Mod2002023Key.BP246));
	    pagina06.setT00247(getKey(mod200,Mod2002023Key.BP247));
	    pagina06.setT00248(getKey(mod200,Mod2002023Key.BP248));
	    pagina06.setT00249(getKey(mod200,Mod2002023Key.BP249));
	    pagina06.setT00250(getKey(mod200,Mod2002023Key.BP250));
	    pagina06.setT00251(getKey(mod200,Mod2002023Key.BP251));
		return pagina06;
	}

	private static TipoCuentaNormal getCuentaPyG(Mod2002023 mod200) {
		TipoCuentaNormal tipoCuentaNormal = new TipoCuentaNormal();
		tipoCuentaNormal.setPagina07(getPagina07(mod200));
		tipoCuentaNormal.setPagina08(getPagina08(mod200));
		return tipoCuentaNormal;
	}

	private static TipoPagina07 getPagina07(Mod2002023 mod200) {
		TipoPagina07 pagina07 = new TipoPagina07();
		if (mod200.getPygType() == BalanceType.ABREVIADO || mod200.getPygType() == BalanceType.PYMES)
			pagina07.setT00255(getKey(mod200,Mod2002023Key.PG255)); // Solo A,P
	    pagina07.setT00256(getKey(mod200,Mod2002023Key.PG256));
	    pagina07.setT00257(getKey(mod200,Mod2002023Key.PG257));
	    pagina07.setT00711(getKey(mod200,Mod2002023Key.PG711)); 
	    pagina07.setT00706(getKey(mod200,Mod2002023Key.PG706));
	    pagina07.setT00707(getKey(mod200,Mod2002023Key.PG707));
	    pagina07.setT00708(getKey(mod200,Mod2002023Key.PG708));
	    pagina07.setT00258(getKey(mod200,Mod2002023Key.PG258));
	    pagina07.setT00259(getKey(mod200,Mod2002023Key.PG259));
	    pagina07.setT00760(getKey(mod200,Mod2002023Key.PG760));
	    pagina07.setT00761(getKey(mod200,Mod2002023Key.PG761));
	    pagina07.setT00762(getKey(mod200,Mod2002023Key.PG762));
	    pagina07.setT00763(getKey(mod200,Mod2002023Key.PG763));
	    pagina07.setT00263(getKey(mod200,Mod2002023Key.PG263));
	    pagina07.setT00264(getKey(mod200,Mod2002023Key.PG264));
	    pagina07.setT00267(getKey(mod200,Mod2002023Key.PG267));
	    pagina07.setT00268(getKey(mod200,Mod2002023Key.PG268));
	    pagina07.setT00269(getKey(mod200,Mod2002023Key.PG269));
	    pagina07.setT00271(getKey(mod200,Mod2002023Key.PG271));
	    pagina07.setT00273(getKey(mod200,Mod2002023Key.PG273));
	    pagina07.setT00274(getKey(mod200,Mod2002023Key.PG274));
	    pagina07.setT00275(getKey(mod200,Mod2002023Key.PG275));
	    pagina07.setT00276(getKey(mod200,Mod2002023Key.PG276));
	    pagina07.setT00277(getKey(mod200,Mod2002023Key.PG277));
	    pagina07.setT00278(getKey(mod200,Mod2002023Key.PG278));
	    pagina07.setT00253(getKey(mod200,Mod2002023Key.PG253));
	    pagina07.setT00254(getKey(mod200,Mod2002023Key.PG254));
	    pagina07.setT00281(getKey(mod200,Mod2002023Key.PG281));
	    pagina07.setT00282(getKey(mod200,Mod2002023Key.PG282));
	    pagina07.setT00283(getKey(mod200,Mod2002023Key.PG283));
	    pagina07.setT00709(getKey(mod200,Mod2002023Key.PG709));
	    pagina07.setT00284(getKey(mod200,Mod2002023Key.PG284));
	    pagina07.setT00285(getKey(mod200,Mod2002023Key.PG285));
	    pagina07.setT00286(getKey(mod200,Mod2002023Key.PG286));
	    pagina07.setT00289(getKey(mod200,Mod2002023Key.PG289));
	    pagina07.setT00290(getKey(mod200,Mod2002023Key.PG290));
	    pagina07.setT00292(getKey(mod200,Mod2002023Key.PG292));
	    pagina07.setT00293(getKey(mod200,Mod2002023Key.PG293));
	    pagina07.setT00710(getKey(mod200,Mod2002023Key.PG710));
	    pagina07.setT00294(getKey(mod200,Mod2002023Key.PG294));
	    pagina07.setT00295(getKey(mod200,Mod2002023Key.PG295));
		return pagina07;
	}

	private static TipoPagina08 getPagina08(Mod2002023 mod200) {
		TipoPagina08 pagina08 = new TipoPagina08();
	    pagina08.setT00299(getKey(mod200,Mod2002023Key.PG299));
	    pagina08.setT00300(getKey(mod200,Mod2002023Key.PG300));
	    pagina08.setT00302(getKey(mod200,Mod2002023Key.PG302));
	    pagina08.setT00303(getKey(mod200,Mod2002023Key.PG303));
	    pagina08.setT00304(getKey(mod200,Mod2002023Key.PG304));
	    pagina08.setT00306(getKey(mod200,Mod2002023Key.PG306));
	    pagina08.setT00307(getKey(mod200,Mod2002023Key.PG307));
	    pagina08.setT00308(getKey(mod200,Mod2002023Key.PG308));
	    if (mod200.getPygType() == BalanceType.ABREVIADO || mod200.getPygType() == BalanceType.PYMES)
	    	pagina08.setT00309(getKey(mod200,Mod2002023Key.PG309)); // Solo A,P
	    pagina08.setT00310(getKey(mod200,Mod2002023Key.PG310));
	    pagina08.setT00311(getKey(mod200,Mod2002023Key.PG311));
	    pagina08.setT00312(getKey(mod200,Mod2002023Key.PG312));
	    pagina08.setT00315(getKey(mod200,Mod2002023Key.PG315));
	    pagina08.setT00316(getKey(mod200,Mod2002023Key.PG316));
	    pagina08.setT00317(getKey(mod200,Mod2002023Key.PG317));
	    pagina08.setT00318(getKey(mod200,Mod2002023Key.PG318));
	    pagina08.setT00320(getKey(mod200,Mod2002023Key.PG320));
	    pagina08.setT00321(getKey(mod200,Mod2002023Key.PG321));
	    pagina08.setT00322(getKey(mod200,Mod2002023Key.PG322));
	    pagina08.setT00323(getKey(mod200,Mod2002023Key.PG323));
	    pagina08.setT00326(getKey(mod200,Mod2002023Key.PG326));
	    pagina08.setT00328(getKey(mod200,Mod2002023Key.PG328));
	    pagina08.setT00330(getKey(mod200,Mod2002023Key.PG330));
	    pagina08.setT00331(getKey(mod200,Mod2002023Key.PG331));
	    pagina08.setT00332(getKey(mod200,Mod2002023Key.PG332));
		return pagina08;
	}

	private static TipoCambiosPN getCambiosPN(Mod2002023 mod200) {
		TipoCambiosPN tipoCambiosPN = new TipoCambiosPN();
		tipoCambiosPN.setPagina09(getPagina09(mod200));
		tipoCambiosPN.setPagina10(getPagina10(mod200));
		tipoCambiosPN.setPagina11(getPagina11(mod200));
		return tipoCambiosPN;
	}

	private static TipoPagina09 getPagina09(Mod2002023 mod200) {
		TipoPagina09 pagina09 = new TipoPagina09();
		if (mod200.getPygType() == BalanceType.ABREVIADO || mod200.getPygType() == BalanceType.PYMES)
			pagina09.setT00336(getKey(mod200,Mod2002023Key.T0336)); // Solo A,P
	    pagina09.setT00337(getKey(mod200,Mod2002023Key.T0337));
	    pagina09.setT00338(getKey(mod200,Mod2002023Key.T0338));
	    pagina09.setT00339(getKey(mod200,Mod2002023Key.T0339));
	    pagina09.setT00340(getKey(mod200,Mod2002023Key.T0340));
	    pagina09.setT00341(getKey(mod200,Mod2002023Key.T0341));
	    pagina09.setT00342(getKey(mod200,Mod2002023Key.T0342));
	    pagina09.setT00343(getKey(mod200,Mod2002023Key.T0343));
	    pagina09.setT00344(getKey(mod200,Mod2002023Key.T0344));
	    if (mod200.getPygType() == BalanceType.ABREVIADO || mod200.getPygType() == BalanceType.PYMES)
	    	pagina09.setT00346(getKey(mod200,Mod2002023Key.T0346)); // Solo A,P
	    pagina09.setT00347(getKey(mod200,Mod2002023Key.T0347));
	    pagina09.setT00348(getKey(mod200,Mod2002023Key.T0348));
	    pagina09.setT00349(getKey(mod200,Mod2002023Key.T0349));
	    pagina09.setT00350(getKey(mod200,Mod2002023Key.T0350));
	    pagina09.setT00351(getKey(mod200,Mod2002023Key.T0351));
	    pagina09.setT00352(getKey(mod200,Mod2002023Key.T0352));
	    pagina09.setT00353(getKey(mod200,Mod2002023Key.T0353));
		return pagina09;
	}

	private static TipoPagina10 getPagina10(Mod2002023 mod200) {
		TipoPagina10 pagina10 = new TipoPagina10();
	    pagina10.setT00380(getKey(mod200,Mod2002023Key.TC380));
	    pagina10.setT00381(getKey(mod200,Mod2002023Key.TC381));
	    pagina10.setT00382(getKey(mod200,Mod2002023Key.TC382));
	    pagina10.setT00383(getKey(mod200,Mod2002023Key.TC383));
	    pagina10.setT00384(getKey(mod200,Mod2002023Key.TC384));
	    pagina10.setT00385(getKey(mod200,Mod2002023Key.TC385));
	    pagina10.setT00386(getKey(mod200,Mod2002023Key.TC386));
	    pagina10.setT00394(getKey(mod200,Mod2002023Key.TC394));
	    pagina10.setT00395(getKey(mod200,Mod2002023Key.TC395));
	    pagina10.setT00396(getKey(mod200,Mod2002023Key.TC396));
	    pagina10.setT00397(getKey(mod200,Mod2002023Key.TC397));
	    pagina10.setT00398(getKey(mod200,Mod2002023Key.TC398));
	    pagina10.setT00399(getKey(mod200,Mod2002023Key.TC399));
	    pagina10.setT00400(getKey(mod200,Mod2002023Key.TC400));
	    pagina10.setT00408(getKey(mod200,Mod2002023Key.TC408));
	    pagina10.setT00409(getKey(mod200,Mod2002023Key.TC409));
	    pagina10.setT00410(getKey(mod200,Mod2002023Key.TC410));
	    pagina10.setT00411(getKey(mod200,Mod2002023Key.TC411));
	    pagina10.setT00412(getKey(mod200,Mod2002023Key.TC412));
	    pagina10.setT00413(getKey(mod200,Mod2002023Key.TC413));
	    pagina10.setT00414(getKey(mod200,Mod2002023Key.TC414));
	    pagina10.setT00436(getKey(mod200,Mod2002023Key.TC436));
	    pagina10.setT00437(getKey(mod200,Mod2002023Key.TC437));
	    pagina10.setT00438(getKey(mod200,Mod2002023Key.TC438));
	    pagina10.setT00439(getKey(mod200,Mod2002023Key.TC439));
	    pagina10.setT00440(getKey(mod200,Mod2002023Key.TC440));
	    pagina10.setT00441(getKey(mod200,Mod2002023Key.TC441));
	    pagina10.setT00442(getKey(mod200,Mod2002023Key.TC442));
	    pagina10.setT00450(getKey(mod200,Mod2002023Key.TC450));
	    pagina10.setT00451(getKey(mod200,Mod2002023Key.TC451));
	    pagina10.setT00452(getKey(mod200,Mod2002023Key.TC452));
	    pagina10.setT00453(getKey(mod200,Mod2002023Key.TC453));
	    pagina10.setT00454(getKey(mod200,Mod2002023Key.TC454));
	    pagina10.setT00455(getKey(mod200,Mod2002023Key.TC455));
	    pagina10.setT00456(getKey(mod200,Mod2002023Key.TC456));
	    pagina10.setT00478(getKey(mod200,Mod2002023Key.TC478));
	    pagina10.setT00479(getKey(mod200,Mod2002023Key.TC479));
	    pagina10.setT00480(getKey(mod200,Mod2002023Key.TC480));
	    pagina10.setT00481(getKey(mod200,Mod2002023Key.TC481));
	    pagina10.setT00482(getKey(mod200,Mod2002023Key.TC482));
	    pagina10.setT00483(getKey(mod200,Mod2002023Key.TC483));
	    pagina10.setT00484(getKey(mod200,Mod2002023Key.TC484));
	    pagina10.setT00492(getKey(mod200,Mod2002023Key.TC492));
	    pagina10.setT00493(getKey(mod200,Mod2002023Key.TC493));
	    pagina10.setT00494(getKey(mod200,Mod2002023Key.TC494));
	    pagina10.setT00495(getKey(mod200,Mod2002023Key.TC495));
	    pagina10.setT00496(getKey(mod200,Mod2002023Key.TC496));
	    pagina10.setT00497(getKey(mod200,Mod2002023Key.TC497));
	    pagina10.setT00498(getKey(mod200,Mod2002023Key.TC498));
	    pagina10.setT00520(getKey(mod200,Mod2002023Key.TC520));
	    pagina10.setT00521(getKey(mod200,Mod2002023Key.TC521));
	    pagina10.setT00522(getKey(mod200,Mod2002023Key.TC522));
	    pagina10.setT00523(getKey(mod200,Mod2002023Key.TC523));
	    pagina10.setT00524(getKey(mod200,Mod2002023Key.TC524));
	    pagina10.setT00525(getKey(mod200,Mod2002023Key.TC525));
	    pagina10.setT00526(getKey(mod200,Mod2002023Key.TC526));
	    pagina10.setT00534(getKey(mod200,Mod2002023Key.TC534));
	    pagina10.setT00535(getKey(mod200,Mod2002023Key.TC535));
	    pagina10.setT00536(getKey(mod200,Mod2002023Key.TC536));
	    pagina10.setT00537(getKey(mod200,Mod2002023Key.TC537));
	    pagina10.setT00538(getKey(mod200,Mod2002023Key.TC538));
	    pagina10.setT00539(getKey(mod200,Mod2002023Key.TC539));
	    pagina10.setT00540(getKey(mod200,Mod2002023Key.TC540));
	    pagina10.setT00548(getKey(mod200,Mod2002023Key.TC548));
	    pagina10.setT00549(getKey(mod200,Mod2002023Key.TC549));
	    pagina10.setT00550(getKey(mod200,Mod2002023Key.TC550));
	    pagina10.setT00551(getKey(mod200,Mod2002023Key.TC551));
	    pagina10.setT00552(getKey(mod200,Mod2002023Key.TC552));
	    pagina10.setT00553(getKey(mod200,Mod2002023Key.TC553));
	    pagina10.setT00554(getKey(mod200,Mod2002023Key.TC554));
	    pagina10.setT00562(getKey(mod200,Mod2002023Key.TC562));
	    pagina10.setT00563(getKey(mod200,Mod2002023Key.TC563));
	    pagina10.setT00564(getKey(mod200,Mod2002023Key.TC564));
	    pagina10.setT00565(getKey(mod200,Mod2002023Key.TC565));
	    pagina10.setT00566(getKey(mod200,Mod2002023Key.TC566));
	    pagina10.setT00567(getKey(mod200,Mod2002023Key.TC567));
	    pagina10.setT00568(getKey(mod200,Mod2002023Key.TC568));
	    pagina10.setT00576(getKey(mod200,Mod2002023Key.TC576));
	    pagina10.setT00577(getKey(mod200,Mod2002023Key.TC577));
	    pagina10.setT00578(getKey(mod200,Mod2002023Key.TC578));
	    pagina10.setT00579(getKey(mod200,Mod2002023Key.TC579));
	    pagina10.setT00580(getKey(mod200,Mod2002023Key.TC580));
	    pagina10.setT00581(getKey(mod200,Mod2002023Key.TC581));
	    pagina10.setT00582(getKey(mod200,Mod2002023Key.TC582));
	    pagina10.setT00590(getKey(mod200,Mod2002023Key.TC590));
	    pagina10.setT00591(getKey(mod200,Mod2002023Key.TC591));
	    pagina10.setT00592(getKey(mod200,Mod2002023Key.TC592));
	    pagina10.setT00593(getKey(mod200,Mod2002023Key.TC593));
	    pagina10.setT00594(getKey(mod200,Mod2002023Key.TC594));
	    pagina10.setT00595(getKey(mod200,Mod2002023Key.TC595));
	    pagina10.setT00596(getKey(mod200,Mod2002023Key.TC596));
	    pagina10.setT00604(getKey(mod200,Mod2002023Key.TC604));
	    pagina10.setT00605(getKey(mod200,Mod2002023Key.TC605));
	    pagina10.setT00606(getKey(mod200,Mod2002023Key.TC606));
	    pagina10.setT00607(getKey(mod200,Mod2002023Key.TC607));
	    pagina10.setT00608(getKey(mod200,Mod2002023Key.TC608));
	    pagina10.setT00609(getKey(mod200,Mod2002023Key.TC609));
	    pagina10.setT00610(getKey(mod200,Mod2002023Key.TC610));
	    pagina10.setT00715(getKey(mod200,Mod2002023Key.TC715));
	    pagina10.setT00716(getKey(mod200,Mod2002023Key.TC716));
	    pagina10.setT00717(getKey(mod200,Mod2002023Key.TC717));
	    pagina10.setT00718(getKey(mod200,Mod2002023Key.TC718));
	    pagina10.setT00719(getKey(mod200,Mod2002023Key.TC719));
	    pagina10.setT00720(getKey(mod200,Mod2002023Key.TC720));
	    pagina10.setT00721(getKey(mod200,Mod2002023Key.TC721));
	    pagina10.setT00729(getKey(mod200,Mod2002023Key.TC729));
	    pagina10.setT00730(getKey(mod200,Mod2002023Key.TC730));
	    pagina10.setT00731(getKey(mod200,Mod2002023Key.TC731));
	    pagina10.setT00732(getKey(mod200,Mod2002023Key.TC732));
	    pagina10.setT00733(getKey(mod200,Mod2002023Key.TC733));
	    pagina10.setT00734(getKey(mod200,Mod2002023Key.TC734));
	    pagina10.setT00735(getKey(mod200,Mod2002023Key.TC735));
		return pagina10;
	}

	private static TipoPagina11 getPagina11(Mod2002023 mod200) {
		TipoPagina11 pagina11 = new TipoPagina11();
	    pagina11.setT00387(getKey(mod200,Mod2002023Key.TC387));
	    pagina11.setT00388(getKey(mod200,Mod2002023Key.TC388));
	    pagina11.setT00389(getKey(mod200,Mod2002023Key.TC389));
	    pagina11.setT00390(getKey(mod200,Mod2002023Key.TC390));
	    pagina11.setT00391(getKey(mod200,Mod2002023Key.TC391));
	    pagina11.setT00392(getKey(mod200,Mod2002023Key.TC392));
	    pagina11.setT00401(getKey(mod200,Mod2002023Key.TC401));
	    pagina11.setT00402(getKey(mod200,Mod2002023Key.TC402));
	    pagina11.setT00403(getKey(mod200,Mod2002023Key.TC403));
	    pagina11.setT00404(getKey(mod200,Mod2002023Key.TC404));
	    pagina11.setT00405(getKey(mod200,Mod2002023Key.TC405));
	    pagina11.setT00406(getKey(mod200,Mod2002023Key.TC406));
	    pagina11.setT00415(getKey(mod200,Mod2002023Key.TC415));
	    pagina11.setT00416(getKey(mod200,Mod2002023Key.TC416));
	    pagina11.setT00417(getKey(mod200,Mod2002023Key.TC417));
	    pagina11.setT00418(getKey(mod200,Mod2002023Key.TC418));
	    pagina11.setT00419(getKey(mod200,Mod2002023Key.TC419));
	    pagina11.setT00420(getKey(mod200,Mod2002023Key.TC420));
	    pagina11.setT00443(getKey(mod200,Mod2002023Key.TC443));
	    pagina11.setT00444(getKey(mod200,Mod2002023Key.TC444));
	    pagina11.setT00445(getKey(mod200,Mod2002023Key.TC445));
	    pagina11.setT00446(getKey(mod200,Mod2002023Key.TC446));
	    pagina11.setT00448(getKey(mod200,Mod2002023Key.TC448));
	    pagina11.setT00457(getKey(mod200,Mod2002023Key.TC457));
	    pagina11.setT00458(getKey(mod200,Mod2002023Key.TC458));
	    pagina11.setT00461(getKey(mod200,Mod2002023Key.TC461));
	    pagina11.setT00462(getKey(mod200,Mod2002023Key.TC462));
	    pagina11.setT00485(getKey(mod200,Mod2002023Key.TC485));
	    pagina11.setT00486(getKey(mod200,Mod2002023Key.TC486));
	    pagina11.setT00489(getKey(mod200,Mod2002023Key.TC489));
	    pagina11.setT00490(getKey(mod200,Mod2002023Key.TC490));
	    pagina11.setT00499(getKey(mod200,Mod2002023Key.TC499));
	    pagina11.setT00502(getKey(mod200,Mod2002023Key.TC502));
	    pagina11.setT00503(getKey(mod200,Mod2002023Key.TC503));
	    pagina11.setT00504(getKey(mod200,Mod2002023Key.TC504));
	    pagina11.setT00527(getKey(mod200,Mod2002023Key.TC527));
	    pagina11.setT00528(getKey(mod200,Mod2002023Key.TC528));
	    pagina11.setT00529(getKey(mod200,Mod2002023Key.TC529));
	    pagina11.setT00530(getKey(mod200,Mod2002023Key.TC530));
	    pagina11.setT00531(getKey(mod200,Mod2002023Key.TC531));
	    pagina11.setT00532(getKey(mod200,Mod2002023Key.TC532));
	    pagina11.setT00541(getKey(mod200,Mod2002023Key.TC541));
	    pagina11.setT00542(getKey(mod200,Mod2002023Key.TC542));
	    pagina11.setT00543(getKey(mod200,Mod2002023Key.TC543));
	    pagina11.setT00544(getKey(mod200,Mod2002023Key.TC544));
	    pagina11.setT00545(getKey(mod200,Mod2002023Key.TC545));
	    pagina11.setT00546(getKey(mod200,Mod2002023Key.TC546));
	    pagina11.setT00555(getKey(mod200,Mod2002023Key.TC555));
	    pagina11.setT00556(getKey(mod200,Mod2002023Key.TC556));
	    pagina11.setT00557(getKey(mod200,Mod2002023Key.TC557));
	    pagina11.setT00558(getKey(mod200,Mod2002023Key.TC558));
	    pagina11.setT00560(getKey(mod200,Mod2002023Key.TC560));
	    pagina11.setT00569(getKey(mod200,Mod2002023Key.TC569));
	    pagina11.setT00570(getKey(mod200,Mod2002023Key.TC570));
	    pagina11.setT00571(getKey(mod200,Mod2002023Key.TC571));
	    pagina11.setT00572(getKey(mod200,Mod2002023Key.TC572));
	    pagina11.setT00574(getKey(mod200,Mod2002023Key.TC574));
	    pagina11.setT00583(getKey(mod200,Mod2002023Key.TC583));
	    pagina11.setT00584(getKey(mod200,Mod2002023Key.TC584));
	    pagina11.setT00585(getKey(mod200,Mod2002023Key.TC585));
	    pagina11.setT00586(getKey(mod200,Mod2002023Key.TC586));
	    pagina11.setT00588(getKey(mod200,Mod2002023Key.TC588));
	    pagina11.setT00597(getKey(mod200,Mod2002023Key.TC597));
	    pagina11.setT00598(getKey(mod200,Mod2002023Key.TC598));
	    pagina11.setT00599(getKey(mod200,Mod2002023Key.TC599));
	    pagina11.setT00600(getKey(mod200,Mod2002023Key.TC600));
	    pagina11.setT00602(getKey(mod200,Mod2002023Key.TC602));
	    pagina11.setT00611(getKey(mod200,Mod2002023Key.TC611));
	    pagina11.setT00612(getKey(mod200,Mod2002023Key.TC612));
	    pagina11.setT00613(getKey(mod200,Mod2002023Key.TC613));
	    pagina11.setT00614(getKey(mod200,Mod2002023Key.TC614));
	    pagina11.setT00615(getKey(mod200,Mod2002023Key.TC615));
	    pagina11.setT00616(getKey(mod200,Mod2002023Key.TC616));
	    pagina11.setT00722(getKey(mod200,Mod2002023Key.TC722));
	    pagina11.setT00723(getKey(mod200,Mod2002023Key.TC723));
	    pagina11.setT00724(getKey(mod200,Mod2002023Key.TC724));
	    pagina11.setT00725(getKey(mod200,Mod2002023Key.TC725));
	    pagina11.setT00726(getKey(mod200,Mod2002023Key.TC726));
	    pagina11.setT00727(getKey(mod200,Mod2002023Key.TC727));
	    pagina11.setT00736(getKey(mod200,Mod2002023Key.TC736));
	    pagina11.setT00737(getKey(mod200,Mod2002023Key.TC737));
	    pagina11.setT00738(getKey(mod200,Mod2002023Key.TC738));
	    pagina11.setT00739(getKey(mod200,Mod2002023Key.TC739));
	    pagina11.setT00740(getKey(mod200,Mod2002023Key.TC740));
	    pagina11.setT00741(getKey(mod200,Mod2002023Key.TC741));
		return pagina11;
	}
	private static BigDecimal getKey(Mod2002023 mod200, Mod2002023Key key) {
		return getKey(mod200, key, 2);
	}
	
	private static BigDecimal getKey(Mod2002023 mod200, Mod2002023Key key,int scale) {
		DoubleVariableEx dv = mod200.getVariable(key);
		Double value = dv==null?null:AonMathUtils.round(dv.getValue());
		return (value == null || value == 0.0)
			?null
			:new BigDecimal(Double.toString(value)).setScale(scale,RoundingMode.HALF_UP);
	}
}
