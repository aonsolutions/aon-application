package com.esferalia.aon.occam.impl.jooq.dao.mod200_2013.jaxb;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.DoubleVariable2014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod2002014toMOD2002013 {

	public static MOD2002013 getMOD2002013(Mod2002014 mod200) {
		MOD2002013 mod = new MOD2002013();
		mod.setNormal( getNormal(mod200) );
		return mod;
	}

	private static TipoNormal getNormal(Mod2002014 mod200) {
		TipoNormal normal = new TipoNormal();
		normal.setBalance(getBalance(mod200));
		normal.setCuentaPyG(getCuentaPyG(mod200));
		normal.setCambiosPN(getCambiosPN(mod200));
		return normal;
	}

	private static TipoBalanceNormal getBalance(Mod2002014 mod200) {
		TipoBalanceNormal tipoBalanceNormal = new TipoBalanceNormal();
		tipoBalanceNormal.setPagina03(getPagina03(mod200));
		tipoBalanceNormal.setPagina04(getPagina04(mod200));
		tipoBalanceNormal.setPagina05(getPagina05(mod200));
		tipoBalanceNormal.setPagina06(getPagina06(mod200));
		return tipoBalanceNormal;
	}

	private static TipoPagina03 getPagina03(Mod2002014 mod200) {
		TipoPagina03 pagina03 = new TipoPagina03();
		pagina03.setT103(getKey(mod200,Mod2002014Key.BA103));
		pagina03.setT104(getKey(mod200,Mod2002014Key.BA104));
		pagina03.setT105(getKey(mod200,Mod2002014Key.BA105));
		pagina03.setT106(getKey(mod200,Mod2002014Key.BA106));
		pagina03.setT107(getKey(mod200,Mod2002014Key.BA107));
		pagina03.setT108(getKey(mod200,Mod2002014Key.BA108));
		pagina03.setT700(getKey(mod200,Mod2002014Key.BA700));
		pagina03.setT701(getKey(mod200,Mod2002014Key.BA701));
		pagina03.setT109(getKey(mod200,Mod2002014Key.BA109));
		pagina03.setT110(getKey(mod200,Mod2002014Key.BA110));
		pagina03.setT111(getKey(mod200,Mod2002014Key.BA111));
		pagina03.setT112(getKey(mod200,Mod2002014Key.BA112));
		pagina03.setT113(getKey(mod200,Mod2002014Key.BA113));
		pagina03.setT114(getKey(mod200,Mod2002014Key.BA114));
		pagina03.setT115(getKey(mod200,Mod2002014Key.BA115));
		pagina03.setT116(getKey(mod200,Mod2002014Key.BA116));
		pagina03.setT117(getKey(mod200,Mod2002014Key.BA117));
		pagina03.setT119(getKey(mod200,Mod2002014Key.BA119));
		pagina03.setT120(getKey(mod200,Mod2002014Key.BA120));
		pagina03.setT121(getKey(mod200,Mod2002014Key.BA121));
		pagina03.setT122(getKey(mod200,Mod2002014Key.BA122));
		pagina03.setT123(getKey(mod200,Mod2002014Key.BA123));
		pagina03.setT124(getKey(mod200,Mod2002014Key.BA124));
		pagina03.setT125(getKey(mod200,Mod2002014Key.BA125));
		pagina03.setT127(getKey(mod200,Mod2002014Key.BA127));
		pagina03.setT128(getKey(mod200,Mod2002014Key.BA128));
		pagina03.setT129(getKey(mod200,Mod2002014Key.BA129));
		pagina03.setT130(getKey(mod200,Mod2002014Key.BA130));
		pagina03.setT131(getKey(mod200,Mod2002014Key.BA131));
		pagina03.setT132(getKey(mod200,Mod2002014Key.BA132));
		pagina03.setT133(getKey(mod200,Mod2002014Key.BA133));
		pagina03.setT134(getKey(mod200,Mod2002014Key.BA134));
		pagina03.setT135(getKey(mod200,Mod2002014Key.BA135));
		pagina03.setT137(getKey(mod200,Mod2002014Key.BA137));
		pagina03.setT138(getKey(mod200,Mod2002014Key.BA138));
		pagina03.setT139(getKey(mod200,Mod2002014Key.BA139));
		pagina03.setT140(getKey(mod200,Mod2002014Key.BA140));
		pagina03.setT141(getKey(mod200,Mod2002014Key.BA141));
		pagina03.setT142(getKey(mod200,Mod2002014Key.BA142));
		pagina03.setT143(getKey(mod200,Mod2002014Key.BA143));
		pagina03.setT144(getKey(mod200,Mod2002014Key.BA144));
		pagina03.setT145(getKey(mod200,Mod2002014Key.BA145));
		pagina03.setT146(getKey(mod200,Mod2002014Key.BA146));
		pagina03.setT147(getKey(mod200,Mod2002014Key.BA147));
		pagina03.setT148(getKey(mod200,Mod2002014Key.BA148));
		return pagina03;
	}

	private static TipoPagina04 getPagina04(Mod2002014 mod200) {
		TipoPagina04 pagina04 = new TipoPagina04();
//	    pagina04.setT150(getKey(mod200,Mod200Key.BA150));
	    pagina04.setT151(getKey(mod200,Mod2002014Key.BA151));
	    pagina04.setT152(getKey(mod200,Mod2002014Key.BA152));
	    pagina04.setT153(getKey(mod200,Mod2002014Key.BA153));
	    pagina04.setT154(getKey(mod200,Mod2002014Key.BA154));
	    pagina04.setT155(getKey(mod200,Mod2002014Key.BA155));
	    pagina04.setT156(getKey(mod200,Mod2002014Key.BA156));
	    pagina04.setT157(getKey(mod200,Mod2002014Key.BA157));
	    pagina04.setT158(getKey(mod200,Mod2002014Key.BA158));
	    pagina04.setT159(getKey(mod200,Mod2002014Key.BA159));
	    pagina04.setT161(getKey(mod200,Mod2002014Key.BA161));
	    pagina04.setT162(getKey(mod200,Mod2002014Key.BA162));
	    pagina04.setT163(getKey(mod200,Mod2002014Key.BA163));
	    pagina04.setT164(getKey(mod200,Mod2002014Key.BA164));
	    pagina04.setT165(getKey(mod200,Mod2002014Key.BA165));
	    pagina04.setT166(getKey(mod200,Mod2002014Key.BA166));
	    pagina04.setT167(getKey(mod200,Mod2002014Key.BA167));
	    pagina04.setT169(getKey(mod200,Mod2002014Key.BA169));
	    pagina04.setT170(getKey(mod200,Mod2002014Key.BA170));
	    pagina04.setT171(getKey(mod200,Mod2002014Key.BA171));
	    pagina04.setT172(getKey(mod200,Mod2002014Key.BA172));
	    pagina04.setT173(getKey(mod200,Mod2002014Key.BA173));
	    pagina04.setT174(getKey(mod200,Mod2002014Key.BA174));
	    pagina04.setT175(getKey(mod200,Mod2002014Key.BA175));
	    pagina04.setT176(getKey(mod200,Mod2002014Key.BA176));
	    pagina04.setT177(getKey(mod200,Mod2002014Key.BA177));
	    pagina04.setT178(getKey(mod200,Mod2002014Key.BA178));
	    pagina04.setT179(getKey(mod200,Mod2002014Key.BA179));
//	    pagina04.setT180(getKey(mod200,Mod200Key.BA180));
		return pagina04;
	}

	private static TipoPagina05 getPagina05(Mod2002014 mod200) {
		TipoPagina05 pagina05 = new TipoPagina05();
	    pagina05.setT188(getKey(mod200,Mod2002014Key.BP188));
	    pagina05.setT189(getKey(mod200,Mod2002014Key.BP189));
	    pagina05.setT190(getKey(mod200,Mod2002014Key.BP190));
	    pagina05.setT191(getKey(mod200,Mod2002014Key.BP191));
	    pagina05.setT192(getKey(mod200,Mod2002014Key.BP192));
	    pagina05.setT193(getKey(mod200,Mod2002014Key.BP193));
	    pagina05.setT702(getKey(mod200,Mod2002014Key.BP702));
	    pagina05.setT194(getKey(mod200,Mod2002014Key.BP194));
	    pagina05.setT195(getKey(mod200,Mod2002014Key.BP195));
	    pagina05.setT196(getKey(mod200,Mod2002014Key.BP196));
	    pagina05.setT197(getKey(mod200,Mod2002014Key.BP197));
	    pagina05.setT198(getKey(mod200,Mod2002014Key.BP198));
	    pagina05.setT199(getKey(mod200,Mod2002014Key.BP199));
	    pagina05.setT200(getKey(mod200,Mod2002014Key.BP200));
	    pagina05.setT201(getKey(mod200,Mod2002014Key.BP201));
	    pagina05.setT202(getKey(mod200,Mod2002014Key.BP202));
	    pagina05.setT203(getKey(mod200,Mod2002014Key.BP203));
	    pagina05.setT204(getKey(mod200,Mod2002014Key.BP204));
	    pagina05.setT205(getKey(mod200,Mod2002014Key.BP205));
	    pagina05.setT206(getKey(mod200,Mod2002014Key.BP206));
	    pagina05.setT207(getKey(mod200,Mod2002014Key.BP207));
	    pagina05.setT208(getKey(mod200,Mod2002014Key.BP208));
	    pagina05.setT209(getKey(mod200,Mod2002014Key.BP209));
	    pagina05.setT211(getKey(mod200,Mod2002014Key.BP211));
	    pagina05.setT212(getKey(mod200,Mod2002014Key.BP212));
	    pagina05.setT213(getKey(mod200,Mod2002014Key.BP213));
	    pagina05.setT214(getKey(mod200,Mod2002014Key.BP214));
	    pagina05.setT215(getKey(mod200,Mod2002014Key.BP215));
	    pagina05.setT217(getKey(mod200,Mod2002014Key.BP217));
	    pagina05.setT218(getKey(mod200,Mod2002014Key.BP218));
	    pagina05.setT219(getKey(mod200,Mod2002014Key.BP219));
	    pagina05.setT220(getKey(mod200,Mod2002014Key.BP220));
	    pagina05.setT221(getKey(mod200,Mod2002014Key.BP221));
	    pagina05.setT222(getKey(mod200,Mod2002014Key.BP222));
	    pagina05.setT223(getKey(mod200,Mod2002014Key.BP223));
	    pagina05.setT224(getKey(mod200,Mod2002014Key.BP224));
	    pagina05.setT225(getKey(mod200,Mod2002014Key.BP225));
	    pagina05.setT226(getKey(mod200,Mod2002014Key.BP226));
	    pagina05.setT227(getKey(mod200,Mod2002014Key.BP227));
		return pagina05;
	}

	private static TipoPagina06 getPagina06(Mod2002014 mod200) {
		TipoPagina06 pagina06 = new TipoPagina06();
	    pagina06.setT229(getKey(mod200,Mod2002014Key.BP229));
	    pagina06.setT230(getKey(mod200,Mod2002014Key.BP230));
	    pagina06.setT703(getKey(mod200,Mod2002014Key.BP703));
	    pagina06.setT704(getKey(mod200,Mod2002014Key.BP704));
	    pagina06.setT232(getKey(mod200,Mod2002014Key.BP232));
	    pagina06.setT233(getKey(mod200,Mod2002014Key.BP233));
	    pagina06.setT234(getKey(mod200,Mod2002014Key.BP234));
	    pagina06.setT235(getKey(mod200,Mod2002014Key.BP235));
	    pagina06.setT236(getKey(mod200,Mod2002014Key.BP236));
	    pagina06.setT237(getKey(mod200,Mod2002014Key.BP237));
	    pagina06.setT238(getKey(mod200,Mod2002014Key.BP238));
	    pagina06.setT241(getKey(mod200,Mod2002014Key.BP241));
	    pagina06.setT242(getKey(mod200,Mod2002014Key.BP242));
	    pagina06.setT243(getKey(mod200,Mod2002014Key.BP243));
	    pagina06.setT244(getKey(mod200,Mod2002014Key.BP244));
	    pagina06.setT245(getKey(mod200,Mod2002014Key.BP245));
	    pagina06.setT246(getKey(mod200,Mod2002014Key.BP246));
	    pagina06.setT247(getKey(mod200,Mod2002014Key.BP247));
	    pagina06.setT248(getKey(mod200,Mod2002014Key.BP248));
	    pagina06.setT249(getKey(mod200,Mod2002014Key.BP249));
	    pagina06.setT250(getKey(mod200,Mod2002014Key.BP250));
	    pagina06.setT251(getKey(mod200,Mod2002014Key.BP251));
		return pagina06;
	}

	private static TipoCuentaNormal getCuentaPyG(Mod2002014 mod200) {
		TipoCuentaNormal tipoCuentaNormal = new TipoCuentaNormal();
		tipoCuentaNormal.setPagina07(getPagina07(mod200));
		tipoCuentaNormal.setPagina08(getPagina08(mod200));
		return tipoCuentaNormal;
	}

	private static TipoPagina07 getPagina07(Mod2002014 mod200) {
		TipoPagina07 pagina07 = new TipoPagina07();
		pagina07.setT255(getKey(mod200,Mod2002014Key.PG255));
	    pagina07.setT256(getKey(mod200,Mod2002014Key.PG256));
	    pagina07.setT257(getKey(mod200,Mod2002014Key.PG257));
	    pagina07.setT705(getKey(mod200,Mod2002014Key.PG705));
	    pagina07.setT706(getKey(mod200,Mod2002014Key.PG706));
	    pagina07.setT707(getKey(mod200,Mod2002014Key.PG707));
	    pagina07.setT708(getKey(mod200,Mod2002014Key.PG708));
	    pagina07.setT258(getKey(mod200,Mod2002014Key.PG258));
	    pagina07.setT259(getKey(mod200,Mod2002014Key.PG259));
	    pagina07.setT260(getKey(mod200,Mod2002014Key.PG260));
	    pagina07.setT261(getKey(mod200,Mod2002014Key.PG261));
	    pagina07.setT262(getKey(mod200,Mod2002014Key.PG262));
	    pagina07.setT263(getKey(mod200,Mod2002014Key.PG263));
	    pagina07.setT264(getKey(mod200,Mod2002014Key.PG264));
	    pagina07.setT266(getKey(mod200,Mod2002014Key.PG266));
	    pagina07.setT267(getKey(mod200,Mod2002014Key.PG267));
	    pagina07.setT268(getKey(mod200,Mod2002014Key.PG268));
	    pagina07.setT269(getKey(mod200,Mod2002014Key.PG269));
	    pagina07.setT271(getKey(mod200,Mod2002014Key.PG271));
//	    pagina07.setT272(getKey(mod200,Mod200Key.PG272));
	    pagina07.setT273(getKey(mod200,Mod2002014Key.PG273));
	    pagina07.setT274(getKey(mod200,Mod2002014Key.PG274));
	    pagina07.setT275(getKey(mod200,Mod2002014Key.PG275));
	    pagina07.setT276(getKey(mod200,Mod2002014Key.PG276));
	    pagina07.setT277(getKey(mod200,Mod2002014Key.PG277));
	    pagina07.setT278(getKey(mod200,Mod2002014Key.PG278));
	    pagina07.setT279(getKey(mod200,Mod2002014Key.PG279));
	    pagina07.setT280(getKey(mod200,Mod2002014Key.PG280));
	    pagina07.setT281(getKey(mod200,Mod2002014Key.PG281));
	    pagina07.setT282(getKey(mod200,Mod2002014Key.PG282));
	    pagina07.setT283(getKey(mod200,Mod2002014Key.PG283));
	    pagina07.setT709(getKey(mod200,Mod2002014Key.PG709));
	    pagina07.setT284(getKey(mod200,Mod2002014Key.PG284));
	    pagina07.setT285(getKey(mod200,Mod2002014Key.PG285));
	    pagina07.setT286(getKey(mod200,Mod2002014Key.PG286));
	    pagina07.setT288(getKey(mod200,Mod2002014Key.PG288));
	    pagina07.setT289(getKey(mod200,Mod2002014Key.PG289));
	    pagina07.setT290(getKey(mod200,Mod2002014Key.PG290));
	    pagina07.setT291(getKey(mod200,Mod2002014Key.PG291));
	    pagina07.setT292(getKey(mod200,Mod2002014Key.PG292));
	    pagina07.setT293(getKey(mod200,Mod2002014Key.PG293));
	    pagina07.setT710(getKey(mod200,Mod2002014Key.PG710));
	    pagina07.setT294(getKey(mod200,Mod2002014Key.PG294));
	    pagina07.setT295(getKey(mod200,Mod2002014Key.PG295));
	    pagina07.setT298(getKey(mod200,Mod2002014Key.PG298));
	    pagina07.setT299(getKey(mod200,Mod2002014Key.PG299));
	    pagina07.setT300(getKey(mod200,Mod2002014Key.PG300));
	    pagina07.setT301(getKey(mod200,Mod2002014Key.PG301));
	    pagina07.setT302(getKey(mod200,Mod2002014Key.PG302));
	    pagina07.setT303(getKey(mod200,Mod2002014Key.PG303));
	    pagina07.setT304(getKey(mod200,Mod2002014Key.PG304));
		return pagina07;
	}

	private static TipoPagina08 getPagina08(Mod2002014 mod200) {
		TipoPagina08 pagina08 = new TipoPagina08();
	    pagina08.setT306(getKey(mod200,Mod2002014Key.PG306));
	    pagina08.setT307(getKey(mod200,Mod2002014Key.PG307));
	    pagina08.setT308(getKey(mod200,Mod2002014Key.PG308));
	    pagina08.setT309(getKey(mod200,Mod2002014Key.PG309));
	    pagina08.setT310(getKey(mod200,Mod2002014Key.PG310));
	    pagina08.setT311(getKey(mod200,Mod2002014Key.PG311));
	    pagina08.setT312(getKey(mod200,Mod2002014Key.PG312));
	    pagina08.setT314(getKey(mod200,Mod2002014Key.PG314));
	    pagina08.setT315(getKey(mod200,Mod2002014Key.PG315));
	    pagina08.setT316(getKey(mod200,Mod2002014Key.PG316));
	    pagina08.setT317(getKey(mod200,Mod2002014Key.PG317));
	    pagina08.setT318(getKey(mod200,Mod2002014Key.PG318));
	    pagina08.setT319(getKey(mod200,Mod2002014Key.PG319));
	    pagina08.setT320(getKey(mod200,Mod2002014Key.PG320));
	    pagina08.setT321(getKey(mod200,Mod2002014Key.PG321));
	    pagina08.setT322(getKey(mod200,Mod2002014Key.PG322));
	    pagina08.setT323(getKey(mod200,Mod2002014Key.PG323));
	    pagina08.setT326(getKey(mod200,Mod2002014Key.PG326));
	    pagina08.setT328(getKey(mod200,Mod2002014Key.PG328));
	    pagina08.setT330(getKey(mod200,Mod2002014Key.PG330));
	    pagina08.setT331(getKey(mod200,Mod2002014Key.PG331));
	    pagina08.setT332(getKey(mod200,Mod2002014Key.PG332));
		return pagina08;
	}

	private static TipoCambiosPN getCambiosPN(Mod2002014 mod200) {
		TipoCambiosPN tipoCambiosPN = new TipoCambiosPN();
		tipoCambiosPN.setPagina09(getPagina09(mod200));
		tipoCambiosPN.setPagina10(getPagina10(mod200));
		tipoCambiosPN.setPagina11(getPagina11(mod200));
		return tipoCambiosPN;
	}

	private static TipoPagina09 getPagina09(Mod2002014 mod200) {
		TipoPagina09 pagina09 = new TipoPagina09();
	    pagina09.setT336(getKey(mod200,Mod2002014Key.T0336));
	    pagina09.setT337(getKey(mod200,Mod2002014Key.T0337));
	    pagina09.setT338(getKey(mod200,Mod2002014Key.T0338));
	    pagina09.setT339(getKey(mod200,Mod2002014Key.T0339));
	    pagina09.setT340(getKey(mod200,Mod2002014Key.T0340));
	    pagina09.setT341(getKey(mod200,Mod2002014Key.T0341));
	    pagina09.setT342(getKey(mod200,Mod2002014Key.T0342));
	    pagina09.setT343(getKey(mod200,Mod2002014Key.T0343));
	    pagina09.setT344(getKey(mod200,Mod2002014Key.T0344));
	    pagina09.setT346(getKey(mod200,Mod2002014Key.T0346));
	    pagina09.setT347(getKey(mod200,Mod2002014Key.T0347));
	    pagina09.setT348(getKey(mod200,Mod2002014Key.T0348));
	    pagina09.setT349(getKey(mod200,Mod2002014Key.T0349));
	    pagina09.setT350(getKey(mod200,Mod2002014Key.T0350));
	    pagina09.setT351(getKey(mod200,Mod2002014Key.T0351));
	    pagina09.setT352(getKey(mod200,Mod2002014Key.T0352));
	    pagina09.setT353(getKey(mod200,Mod2002014Key.T0353));
		return pagina09;
	}

	private static TipoPagina10 getPagina10(Mod2002014 mod200) {
		TipoPagina10 pagina10 = new TipoPagina10();
	    pagina10.setT380(getKey(mod200,Mod2002014Key.TC380));
	    pagina10.setT381(getKey(mod200,Mod2002014Key.TC381));
	    pagina10.setT382(getKey(mod200,Mod2002014Key.TC382));
	    pagina10.setT383(getKey(mod200,Mod2002014Key.TC383));
	    pagina10.setT384(getKey(mod200,Mod2002014Key.TC384));
	    pagina10.setT385(getKey(mod200,Mod2002014Key.TC385));
	    pagina10.setT386(getKey(mod200,Mod2002014Key.TC386));
	    pagina10.setT394(getKey(mod200,Mod2002014Key.TC394));
	    pagina10.setT395(getKey(mod200,Mod2002014Key.TC395));
	    pagina10.setT396(getKey(mod200,Mod2002014Key.TC396));
	    pagina10.setT397(getKey(mod200,Mod2002014Key.TC397));
	    pagina10.setT398(getKey(mod200,Mod2002014Key.TC398));
	    pagina10.setT399(getKey(mod200,Mod2002014Key.TC399));
	    pagina10.setT400(getKey(mod200,Mod2002014Key.TC400));
	    pagina10.setT408(getKey(mod200,Mod2002014Key.TC408));
	    pagina10.setT409(getKey(mod200,Mod2002014Key.TC409));
	    pagina10.setT410(getKey(mod200,Mod2002014Key.TC410));
	    pagina10.setT411(getKey(mod200,Mod2002014Key.TC411));
	    pagina10.setT412(getKey(mod200,Mod2002014Key.TC412));
	    pagina10.setT413(getKey(mod200,Mod2002014Key.TC413));
	    pagina10.setT414(getKey(mod200,Mod2002014Key.TC414));
	    pagina10.setT436(getKey(mod200,Mod2002014Key.TC436));
	    pagina10.setT437(getKey(mod200,Mod2002014Key.TC437));
	    pagina10.setT438(getKey(mod200,Mod2002014Key.TC438));
	    pagina10.setT439(getKey(mod200,Mod2002014Key.TC439));
	    pagina10.setT440(getKey(mod200,Mod2002014Key.TC440));
	    pagina10.setT441(getKey(mod200,Mod2002014Key.TC441));
	    pagina10.setT442(getKey(mod200,Mod2002014Key.TC442));
	    pagina10.setT450(getKey(mod200,Mod2002014Key.TC450));
	    pagina10.setT451(getKey(mod200,Mod2002014Key.TC451));
	    pagina10.setT452(getKey(mod200,Mod2002014Key.TC452));
	    pagina10.setT453(getKey(mod200,Mod2002014Key.TC453));
	    pagina10.setT454(getKey(mod200,Mod2002014Key.TC454));
	    pagina10.setT455(getKey(mod200,Mod2002014Key.TC455));
	    pagina10.setT456(getKey(mod200,Mod2002014Key.TC456));
	    pagina10.setT478(getKey(mod200,Mod2002014Key.TC478));
	    pagina10.setT479(getKey(mod200,Mod2002014Key.TC479));
	    pagina10.setT480(getKey(mod200,Mod2002014Key.TC480));
	    pagina10.setT481(getKey(mod200,Mod2002014Key.TC481));
	    pagina10.setT482(getKey(mod200,Mod2002014Key.TC482));
	    pagina10.setT483(getKey(mod200,Mod2002014Key.TC483));
	    pagina10.setT484(getKey(mod200,Mod2002014Key.TC484));
	    pagina10.setT492(getKey(mod200,Mod2002014Key.TC492));
	    pagina10.setT493(getKey(mod200,Mod2002014Key.TC493));
	    pagina10.setT494(getKey(mod200,Mod2002014Key.TC494));
	    pagina10.setT495(getKey(mod200,Mod2002014Key.TC495));
	    pagina10.setT496(getKey(mod200,Mod2002014Key.TC496));
	    pagina10.setT497(getKey(mod200,Mod2002014Key.TC497));
	    pagina10.setT498(getKey(mod200,Mod2002014Key.TC498));
	    pagina10.setT520(getKey(mod200,Mod2002014Key.TC520));
	    pagina10.setT521(getKey(mod200,Mod2002014Key.TC521));
	    pagina10.setT522(getKey(mod200,Mod2002014Key.TC522));
	    pagina10.setT523(getKey(mod200,Mod2002014Key.TC523));
	    pagina10.setT524(getKey(mod200,Mod2002014Key.TC524));
	    pagina10.setT525(getKey(mod200,Mod2002014Key.TC525));
	    pagina10.setT526(getKey(mod200,Mod2002014Key.TC526));
	    pagina10.setT534(getKey(mod200,Mod2002014Key.TC534));
	    pagina10.setT535(getKey(mod200,Mod2002014Key.TC535));
	    pagina10.setT536(getKey(mod200,Mod2002014Key.TC536));
	    pagina10.setT537(getKey(mod200,Mod2002014Key.TC537));
	    pagina10.setT538(getKey(mod200,Mod2002014Key.TC538));
	    pagina10.setT539(getKey(mod200,Mod2002014Key.TC539));
	    pagina10.setT540(getKey(mod200,Mod2002014Key.TC540));
	    pagina10.setT548(getKey(mod200,Mod2002014Key.TC548));
	    pagina10.setT549(getKey(mod200,Mod2002014Key.TC549));
	    pagina10.setT550(getKey(mod200,Mod2002014Key.TC550));
	    pagina10.setT551(getKey(mod200,Mod2002014Key.TC551));
	    pagina10.setT552(getKey(mod200,Mod2002014Key.TC552));
	    pagina10.setT553(getKey(mod200,Mod2002014Key.TC553));
	    pagina10.setT554(getKey(mod200,Mod2002014Key.TC554));
	    pagina10.setT562(getKey(mod200,Mod2002014Key.TC562));
	    pagina10.setT563(getKey(mod200,Mod2002014Key.TC563));
	    pagina10.setT564(getKey(mod200,Mod2002014Key.TC564));
	    pagina10.setT565(getKey(mod200,Mod2002014Key.TC565));
	    pagina10.setT566(getKey(mod200,Mod2002014Key.TC566));
	    pagina10.setT567(getKey(mod200,Mod2002014Key.TC567));
	    pagina10.setT568(getKey(mod200,Mod2002014Key.TC568));
	    pagina10.setT576(getKey(mod200,Mod2002014Key.TC576));
	    pagina10.setT577(getKey(mod200,Mod2002014Key.TC577));
	    pagina10.setT578(getKey(mod200,Mod2002014Key.TC578));
	    pagina10.setT579(getKey(mod200,Mod2002014Key.TC579));
	    pagina10.setT580(getKey(mod200,Mod2002014Key.TC580));
	    pagina10.setT581(getKey(mod200,Mod2002014Key.TC581));
	    pagina10.setT582(getKey(mod200,Mod2002014Key.TC582));
	    pagina10.setT590(getKey(mod200,Mod2002014Key.TC590));
	    pagina10.setT591(getKey(mod200,Mod2002014Key.TC591));
	    pagina10.setT592(getKey(mod200,Mod2002014Key.TC592));
	    pagina10.setT593(getKey(mod200,Mod2002014Key.TC593));
	    pagina10.setT594(getKey(mod200,Mod2002014Key.TC594));
	    pagina10.setT595(getKey(mod200,Mod2002014Key.TC595));
	    pagina10.setT596(getKey(mod200,Mod2002014Key.TC596));
	    pagina10.setT604(getKey(mod200,Mod2002014Key.TC604));
	    pagina10.setT605(getKey(mod200,Mod2002014Key.TC605));
	    pagina10.setT606(getKey(mod200,Mod2002014Key.TC606));
	    pagina10.setT607(getKey(mod200,Mod2002014Key.TC607));
	    pagina10.setT608(getKey(mod200,Mod2002014Key.TC608));
	    pagina10.setT609(getKey(mod200,Mod2002014Key.TC609));
	    pagina10.setT610(getKey(mod200,Mod2002014Key.TC610));
	    pagina10.setT618(getKey(mod200,Mod2002014Key.TC618));
	    pagina10.setT619(getKey(mod200,Mod2002014Key.TC619));
	    pagina10.setT620(getKey(mod200,Mod2002014Key.TC620));
	    pagina10.setT621(getKey(mod200,Mod2002014Key.TC621));
	    pagina10.setT622(getKey(mod200,Mod2002014Key.TC622));
	    pagina10.setT623(getKey(mod200,Mod2002014Key.TC623));
	    pagina10.setT624(getKey(mod200,Mod2002014Key.TC624));
	    pagina10.setT715(getKey(mod200,Mod2002014Key.TC715));
	    pagina10.setT716(getKey(mod200,Mod2002014Key.TC716));
	    pagina10.setT717(getKey(mod200,Mod2002014Key.TC717));
	    pagina10.setT718(getKey(mod200,Mod2002014Key.TC718));
	    pagina10.setT719(getKey(mod200,Mod2002014Key.TC719));
	    pagina10.setT720(getKey(mod200,Mod2002014Key.TC720));
	    pagina10.setT721(getKey(mod200,Mod2002014Key.TC721));
	    pagina10.setT729(getKey(mod200,Mod2002014Key.TC729));
	    pagina10.setT730(getKey(mod200,Mod2002014Key.TC730));
	    pagina10.setT731(getKey(mod200,Mod2002014Key.TC731));
	    pagina10.setT732(getKey(mod200,Mod2002014Key.TC732));
	    pagina10.setT733(getKey(mod200,Mod2002014Key.TC733));
	    pagina10.setT734(getKey(mod200,Mod2002014Key.TC734));
	    pagina10.setT735(getKey(mod200,Mod2002014Key.TC735));
		return pagina10;
	}

	private static TipoPagina11 getPagina11(Mod2002014 mod200) {
		TipoPagina11 pagina11 = new TipoPagina11();
	    pagina11.setT387(getKey(mod200,Mod2002014Key.TC387));
	    pagina11.setT388(getKey(mod200,Mod2002014Key.TC388));
	    pagina11.setT389(getKey(mod200,Mod2002014Key.TC389));
	    pagina11.setT390(getKey(mod200,Mod2002014Key.TC390));
	    pagina11.setT391(getKey(mod200,Mod2002014Key.TC391));
	    pagina11.setT392(getKey(mod200,Mod2002014Key.TC392));
	    pagina11.setT401(getKey(mod200,Mod2002014Key.TC401));
	    pagina11.setT402(getKey(mod200,Mod2002014Key.TC402));
	    pagina11.setT403(getKey(mod200,Mod2002014Key.TC403));
	    pagina11.setT404(getKey(mod200,Mod2002014Key.TC404));
	    pagina11.setT405(getKey(mod200,Mod2002014Key.TC405));
	    pagina11.setT406(getKey(mod200,Mod2002014Key.TC406));
	    pagina11.setT415(getKey(mod200,Mod2002014Key.TC415));
	    pagina11.setT416(getKey(mod200,Mod2002014Key.TC416));
	    pagina11.setT417(getKey(mod200,Mod2002014Key.TC417));
	    pagina11.setT418(getKey(mod200,Mod2002014Key.TC418));
	    pagina11.setT419(getKey(mod200,Mod2002014Key.TC419));
	    pagina11.setT420(getKey(mod200,Mod2002014Key.TC420));
	    pagina11.setT443(getKey(mod200,Mod2002014Key.TC443));
	    pagina11.setT444(getKey(mod200,Mod2002014Key.TC444));
	    pagina11.setT445(getKey(mod200,Mod2002014Key.TC445));
	    pagina11.setT446(getKey(mod200,Mod2002014Key.TC446));
	    pagina11.setT448(getKey(mod200,Mod2002014Key.TC448));
	    pagina11.setT457(getKey(mod200,Mod2002014Key.TC457));
	    pagina11.setT458(getKey(mod200,Mod2002014Key.TC458));
	    pagina11.setT461(getKey(mod200,Mod2002014Key.TC461));
	    pagina11.setT462(getKey(mod200,Mod2002014Key.TC462));
	    pagina11.setT485(getKey(mod200,Mod2002014Key.TC485));
	    pagina11.setT486(getKey(mod200,Mod2002014Key.TC486));
	    pagina11.setT489(getKey(mod200,Mod2002014Key.TC489));
	    pagina11.setT490(getKey(mod200,Mod2002014Key.TC490));
	    pagina11.setT499(getKey(mod200,Mod2002014Key.TC499));
	    pagina11.setT502(getKey(mod200,Mod2002014Key.TC502));
	    pagina11.setT503(getKey(mod200,Mod2002014Key.TC503));
	    pagina11.setT504(getKey(mod200,Mod2002014Key.TC504));
	    pagina11.setT527(getKey(mod200,Mod2002014Key.TC527));
	    pagina11.setT528(getKey(mod200,Mod2002014Key.TC528));
	    pagina11.setT529(getKey(mod200,Mod2002014Key.TC529));
	    pagina11.setT530(getKey(mod200,Mod2002014Key.TC530));
	    pagina11.setT531(getKey(mod200,Mod2002014Key.TC531));
	    pagina11.setT532(getKey(mod200,Mod2002014Key.TC532));
	    pagina11.setT541(getKey(mod200,Mod2002014Key.TC541));
	    pagina11.setT542(getKey(mod200,Mod2002014Key.TC542));
	    pagina11.setT543(getKey(mod200,Mod2002014Key.TC543));
	    pagina11.setT544(getKey(mod200,Mod2002014Key.TC544));
	    pagina11.setT545(getKey(mod200,Mod2002014Key.TC545));
	    pagina11.setT546(getKey(mod200,Mod2002014Key.TC546));
	    pagina11.setT555(getKey(mod200,Mod2002014Key.TC555));
	    pagina11.setT556(getKey(mod200,Mod2002014Key.TC556));
	    pagina11.setT557(getKey(mod200,Mod2002014Key.TC557));
	    pagina11.setT558(getKey(mod200,Mod2002014Key.TC558));
	    pagina11.setT560(getKey(mod200,Mod2002014Key.TC560));
	    pagina11.setT569(getKey(mod200,Mod2002014Key.TC569));
	    pagina11.setT570(getKey(mod200,Mod2002014Key.TC570));
	    pagina11.setT571(getKey(mod200,Mod2002014Key.TC571));
	    pagina11.setT572(getKey(mod200,Mod2002014Key.TC572));
	    pagina11.setT574(getKey(mod200,Mod2002014Key.TC574));
	    pagina11.setT583(getKey(mod200,Mod2002014Key.TC583));
	    pagina11.setT584(getKey(mod200,Mod2002014Key.TC584));
	    pagina11.setT585(getKey(mod200,Mod2002014Key.TC585));
	    pagina11.setT586(getKey(mod200,Mod2002014Key.TC586));
	    pagina11.setT588(getKey(mod200,Mod2002014Key.TC588));
	    pagina11.setT597(getKey(mod200,Mod2002014Key.TC597));
	    pagina11.setT598(getKey(mod200,Mod2002014Key.TC598));
	    pagina11.setT599(getKey(mod200,Mod2002014Key.TC599));
	    pagina11.setT600(getKey(mod200,Mod2002014Key.TC600));
	    pagina11.setT602(getKey(mod200,Mod2002014Key.TC602));
	    pagina11.setT611(getKey(mod200,Mod2002014Key.TC611));
	    pagina11.setT612(getKey(mod200,Mod2002014Key.TC612));
	    pagina11.setT613(getKey(mod200,Mod2002014Key.TC613));
	    pagina11.setT614(getKey(mod200,Mod2002014Key.TC614));
	    pagina11.setT615(getKey(mod200,Mod2002014Key.TC615));
	    pagina11.setT616(getKey(mod200,Mod2002014Key.TC616));
	    pagina11.setT625(getKey(mod200,Mod2002014Key.TC625));
	    pagina11.setT626(getKey(mod200,Mod2002014Key.TC626));
	    pagina11.setT627(getKey(mod200,Mod2002014Key.TC627));
	    pagina11.setT628(getKey(mod200,Mod2002014Key.TC628));
	    pagina11.setT629(getKey(mod200,Mod2002014Key.TC629));
	    pagina11.setT630(getKey(mod200,Mod2002014Key.TC630));
	    pagina11.setT722(getKey(mod200,Mod2002014Key.TC722));
	    pagina11.setT723(getKey(mod200,Mod2002014Key.TC723));
	    pagina11.setT724(getKey(mod200,Mod2002014Key.TC724));
	    pagina11.setT725(getKey(mod200,Mod2002014Key.TC725));
	    pagina11.setT726(getKey(mod200,Mod2002014Key.TC726));
	    pagina11.setT727(getKey(mod200,Mod2002014Key.TC727));
	    pagina11.setT736(getKey(mod200,Mod2002014Key.TC736));
	    pagina11.setT737(getKey(mod200,Mod2002014Key.TC737));
	    pagina11.setT738(getKey(mod200,Mod2002014Key.TC738));
	    pagina11.setT739(getKey(mod200,Mod2002014Key.TC739));
	    pagina11.setT740(getKey(mod200,Mod2002014Key.TC740));
	    pagina11.setT741(getKey(mod200,Mod2002014Key.TC741));
		return pagina11;
	}
	private static BigDecimal getKey(Mod2002014 mod200, Mod2002014Key key) {
		return getKey(mod200, key, 2);
	}
	
	private static BigDecimal getKey(Mod2002014 mod200, Mod2002014Key key,int scale) {
		DoubleVariable2014 dv = mod200.getVariable(key);
		Double value = dv==null?null:AonMathUtils.round(dv.getValue());
		return (value == null || value == 0.0)
			?null
			:new BigDecimal(Double.toString(value)).setScale(scale,RoundingMode.HALF_UP);
	}
}
