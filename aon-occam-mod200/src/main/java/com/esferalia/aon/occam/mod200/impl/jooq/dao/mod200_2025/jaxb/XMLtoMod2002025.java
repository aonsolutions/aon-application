package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2025.jaxb;

import com.esferalia.aon.occam.mod200.api.model.DoubleVariableEx;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;

public class XMLtoMod2002025 {

	public static void fillMod2002025(MOD2002025 mod,Mod2002025 mod200) {
		fillBalance(mod,mod200);		
	}

	private static void fillBalance(MOD2002025 mod, Mod2002025 mod200) {
		fillPagina03(mod,mod200);
		fillPagina04(mod,mod200);
		fillPagina05(mod,mod200);
		fillPagina06(mod,mod200);
		fillPagina07(mod,mod200);
		fillPagina08(mod,mod200);
		fillPagina09(mod,mod200);
		fillPagina10(mod,mod200);
		fillPagina11(mod,mod200);
	}

	private static void fillPagina03(MOD2002025 mod, Mod2002025 mod200) {
		TipoPagina03 pag = mod.getNormal().getBalance().getPagina03();
		if (pag==null) 
			return;
		
		addVariable(mod200,Mod2002025Key.BA103,pag.getT00103());
		addVariable(mod200,Mod2002025Key.BA104,pag.getT00104());
		addVariable(mod200,Mod2002025Key.BA105,pag.getT00105());
		addVariable(mod200,Mod2002025Key.BA106,pag.getT00106());
		addVariable(mod200,Mod2002025Key.BA107,pag.getT00107());
		addVariable(mod200,Mod2002025Key.BA108,pag.getT00108());
		addVariable(mod200,Mod2002025Key.BA700,pag.getT00700());
		addVariable(mod200,Mod2002025Key.BA701,pag.getT00701());
		addVariable(mod200,Mod2002025Key.BA109,pag.getT00109());
		addVariable(mod200,Mod2002025Key.BA110,pag.getT00110());
		addVariable(mod200,Mod2002025Key.BA111,pag.getT00111());
		addVariable(mod200,Mod2002025Key.BA112,pag.getT00112());
		addVariable(mod200,Mod2002025Key.BA113,pag.getT00113());
		addVariable(mod200,Mod2002025Key.BA114,pag.getT00114());
		addVariable(mod200,Mod2002025Key.BA115,pag.getT00115());
		addVariable(mod200,Mod2002025Key.BA116,pag.getT00116());
		addVariable(mod200,Mod2002025Key.BA117,pag.getT00117());
		addVariable(mod200,Mod2002025Key.BA119,pag.getT00119());
		addVariable(mod200,Mod2002025Key.BA120,pag.getT00120());
		addVariable(mod200,Mod2002025Key.BA121,pag.getT00121());
		addVariable(mod200,Mod2002025Key.BA122,pag.getT00122());
		addVariable(mod200,Mod2002025Key.BA123,pag.getT00123());
		addVariable(mod200,Mod2002025Key.BA124,pag.getT00124());
		addVariable(mod200,Mod2002025Key.BA125,pag.getT00125());
		addVariable(mod200,Mod2002025Key.BA127,pag.getT00127());
		addVariable(mod200,Mod2002025Key.BA128,pag.getT00128());
		addVariable(mod200,Mod2002025Key.BA129,pag.getT00129());
		addVariable(mod200,Mod2002025Key.BA130,pag.getT00130());
		addVariable(mod200,Mod2002025Key.BA131,pag.getT00131());
		addVariable(mod200,Mod2002025Key.BA132,pag.getT00132());
		addVariable(mod200,Mod2002025Key.BA133,pag.getT00133());
		addVariable(mod200,Mod2002025Key.BA134,pag.getT00134());
		addVariable(mod200,Mod2002025Key.BA135,pag.getT00135());
		addVariable(mod200,Mod2002025Key.BA137,pag.getT00137());
		addVariable(mod200,Mod2002025Key.BA138,pag.getT00138());
		addVariable(mod200,Mod2002025Key.BA139,pag.getT00139());
		addVariable(mod200,Mod2002025Key.BA140,pag.getT00140());
		addVariable(mod200,Mod2002025Key.BA141,pag.getT00141());
		addVariable(mod200,Mod2002025Key.BA142,pag.getT00142());
		addVariable(mod200,Mod2002025Key.BA143,pag.getT00143());
		addVariable(mod200,Mod2002025Key.BA144,pag.getT00144());
		addVariable(mod200,Mod2002025Key.BA145,pag.getT00145());
		addVariable(mod200,Mod2002025Key.BA146,pag.getT00146());
		addVariable(mod200,Mod2002025Key.BA147,pag.getT00147());
		addVariable(mod200,Mod2002025Key.BA148,pag.getT00148());
	}

	private static void fillPagina04(MOD2002025 mod, Mod2002025 mod200) {
		TipoPagina04 pag = mod.getNormal().getBalance().getPagina04();
		if (pag==null) 
			return;
		
		addVariable(mod200,Mod2002025Key.BA151, pag.getT00151());
		addVariable(mod200,Mod2002025Key.BA152, pag.getT00152());
		addVariable(mod200,Mod2002025Key.BA153, pag.getT00153());
		addVariable(mod200,Mod2002025Key.BA154, pag.getT00154());
		addVariable(mod200,Mod2002025Key.BA155, pag.getT00155());
		addVariable(mod200,Mod2002025Key.BA156, pag.getT00156());
		addVariable(mod200,Mod2002025Key.BA157, pag.getT00157());
		addVariable(mod200,Mod2002025Key.BA158, pag.getT00158());
		addVariable(mod200,Mod2002025Key.BA159, pag.getT00159());
		addVariable(mod200,Mod2002025Key.BA161, pag.getT00161());
		addVariable(mod200,Mod2002025Key.BA162, pag.getT00162());
		addVariable(mod200,Mod2002025Key.BA163, pag.getT00163());
		addVariable(mod200,Mod2002025Key.BA164, pag.getT00164());
		addVariable(mod200,Mod2002025Key.BA165, pag.getT00165());
		addVariable(mod200,Mod2002025Key.BA166, pag.getT00166());
		addVariable(mod200,Mod2002025Key.BA167, pag.getT00167());
		addVariable(mod200,Mod2002025Key.BA169, pag.getT00169());
		addVariable(mod200,Mod2002025Key.BA170, pag.getT00170());
		addVariable(mod200,Mod2002025Key.BA171, pag.getT00171());
		addVariable(mod200,Mod2002025Key.BA172, pag.getT00172());
		addVariable(mod200,Mod2002025Key.BA173, pag.getT00173());
		addVariable(mod200,Mod2002025Key.BA174, pag.getT00174());
		addVariable(mod200,Mod2002025Key.BA175, pag.getT00175());
		addVariable(mod200,Mod2002025Key.BA176, pag.getT00176());
		addVariable(mod200,Mod2002025Key.BA177, pag.getT00177());
		addVariable(mod200,Mod2002025Key.BA178, pag.getT00178());
		addVariable(mod200,Mod2002025Key.BA179, pag.getT00179());
	}
	
	private static void fillPagina05(MOD2002025 mod, Mod2002025 mod200) {
		TipoPagina05 pag = mod.getNormal().getBalance().getPagina05();
		if (pag==null) 
			return;
		
		addVariable(mod200,Mod2002025Key.BP188, pag.getT00188());
		addVariable(mod200,Mod2002025Key.BP189, pag.getT00189());
		addVariable(mod200,Mod2002025Key.BP764, pag.getT00764());
		addVariable(mod200,Mod2002025Key.BP765, pag.getT00765());
		addVariable(mod200,Mod2002025Key.BP190, pag.getT00190());
		addVariable(mod200,Mod2002025Key.BP1001,pag.getT01001());
		addVariable(mod200,Mod2002025Key.BP1002,pag.getT01002());
		addVariable(mod200,Mod2002025Key.BP712, pag.getT00712());
		addVariable(mod200,Mod2002025Key.BP766, pag.getT00766());
		addVariable(mod200,Mod2002025Key.BP767, pag.getT00767());
		addVariable(mod200,Mod2002025Key.BP192, pag.getT00192());
		addVariable(mod200,Mod2002025Key.BP193, pag.getT00193());
		addVariable(mod200,Mod2002025Key.BP702, pag.getT00702());
		addVariable(mod200,Mod2002025Key.BP194, pag.getT00194());
		addVariable(mod200,Mod2002025Key.BP195, pag.getT00195());
		addVariable(mod200,Mod2002025Key.BP196, pag.getT00196());
		addVariable(mod200,Mod2002025Key.BP197, pag.getT00197());
		addVariable(mod200,Mod2002025Key.BP198, pag.getT00198());
		addVariable(mod200,Mod2002025Key.BP199, pag.getT00199());
		addVariable(mod200,Mod2002025Key.BP200, pag.getT00200());
		addVariable(mod200,Mod2002025Key.BP768, pag.getT00768());
		addVariable(mod200,Mod2002025Key.BP769, pag.getT00769());
		addVariable(mod200,Mod2002025Key.BP201, pag.getT00201());
		addVariable(mod200,Mod2002025Key.BP202, pag.getT00202());
		addVariable(mod200,Mod2002025Key.BP203, pag.getT00203());
		addVariable(mod200,Mod2002025Key.BP204, pag.getT00204());
		addVariable(mod200,Mod2002025Key.BP205, pag.getT00205());
		addVariable(mod200,Mod2002025Key.BP206, pag.getT00206());
		addVariable(mod200,Mod2002025Key.BP207, pag.getT00207());
		addVariable(mod200,Mod2002025Key.BP208, pag.getT00208());
		addVariable(mod200,Mod2002025Key.BP209, pag.getT00209());
		addVariable(mod200,Mod2002025Key.BP780, pag.getT00780());
		addVariable(mod200,Mod2002025Key.BP782, pag.getT00782());
		addVariable(mod200,Mod2002025Key.BP783, pag.getT00783());
		addVariable(mod200,Mod2002025Key.BP784, pag.getT00784());
		addVariable(mod200,Mod2002025Key.BP211, pag.getT00211());
		addVariable(mod200,Mod2002025Key.BP212, pag.getT00212());
		addVariable(mod200,Mod2002025Key.BP213, pag.getT00213());
		addVariable(mod200,Mod2002025Key.BP214, pag.getT00214());
		addVariable(mod200,Mod2002025Key.BP215, pag.getT00215());
		addVariable(mod200,Mod2002025Key.BP217, pag.getT00217());
		addVariable(mod200,Mod2002025Key.BP218, pag.getT00218());
		addVariable(mod200,Mod2002025Key.BP219, pag.getT00219());
		addVariable(mod200,Mod2002025Key.BP220, pag.getT00220());
		addVariable(mod200,Mod2002025Key.BP221, pag.getT00221());
		addVariable(mod200,Mod2002025Key.BP222, pag.getT00222());
		addVariable(mod200,Mod2002025Key.BP223, pag.getT00223());
		addVariable(mod200,Mod2002025Key.BP224, pag.getT00224());
		addVariable(mod200,Mod2002025Key.BP225, pag.getT00225());
		addVariable(mod200,Mod2002025Key.BP226, pag.getT00226());
		addVariable(mod200,Mod2002025Key.BP227, pag.getT00227());
	}
	
	private static void fillPagina06(MOD2002025 mod, Mod2002025 mod200) {
		if (mod.getNormal().getBalance() == null) 
			return;
		
		TipoPagina06 pag = mod.getNormal().getBalance().getPagina06();
		addVariable(mod200,Mod2002025Key.BP785, pag.getT00785());
		addVariable(mod200,Mod2002025Key.BP787, pag.getT00787());
		addVariable(mod200,Mod2002025Key.BP788, pag.getT00788());
		addVariable(mod200,Mod2002025Key.BP789, pag.getT00789());
		addVariable(mod200,Mod2002025Key.BP229, pag.getT00229());
		addVariable(mod200,Mod2002025Key.BP230, pag.getT00230());
		addVariable(mod200,Mod2002025Key.BP703, pag.getT00703());
		addVariable(mod200,Mod2002025Key.BP704, pag.getT00704());
		addVariable(mod200,Mod2002025Key.BP232, pag.getT00232());
		addVariable(mod200,Mod2002025Key.BP233, pag.getT00233());
		addVariable(mod200,Mod2002025Key.BP234, pag.getT00234());
		addVariable(mod200,Mod2002025Key.BP235, pag.getT00235());
		addVariable(mod200,Mod2002025Key.BP236, pag.getT00236());
		addVariable(mod200,Mod2002025Key.BP237, pag.getT00237());
		addVariable(mod200,Mod2002025Key.BP238, pag.getT00238());
		addVariable(mod200,Mod2002025Key.BP241, pag.getT00241());
		addVariable(mod200,Mod2002025Key.BP242, pag.getT00242());
		addVariable(mod200,Mod2002025Key.BP243, pag.getT00243());
		addVariable(mod200,Mod2002025Key.BP244, pag.getT00244());
		addVariable(mod200,Mod2002025Key.BP245, pag.getT00245());
		addVariable(mod200,Mod2002025Key.BP246, pag.getT00246());
		addVariable(mod200,Mod2002025Key.BP247, pag.getT00247());
		addVariable(mod200,Mod2002025Key.BP248, pag.getT00248());
		addVariable(mod200,Mod2002025Key.BP249, pag.getT00249());
		addVariable(mod200,Mod2002025Key.BP250, pag.getT00250());
		addVariable(mod200,Mod2002025Key.BP251, pag.getT00251());
	}
	
	private static void fillPagina07(MOD2002025 mod, Mod2002025 mod200) {
		TipoPagina07 pag = mod.getNormal().getCuentaPyG().getPagina07();
		if (pag==null) return;
		addVariable(mod200,Mod2002025Key.PG255, pag.getT00255());
		addVariable(mod200,Mod2002025Key.PG256, pag.getT00256());
		addVariable(mod200,Mod2002025Key.PG257, pag.getT00257());
		addVariable(mod200,Mod2002025Key.PG711, pag.getT00711());
		addVariable(mod200,Mod2002025Key.PG706, pag.getT00706());
		addVariable(mod200,Mod2002025Key.PG707, pag.getT00707());
		addVariable(mod200,Mod2002025Key.PG708, pag.getT00708());
		addVariable(mod200,Mod2002025Key.PG258, pag.getT00258());
		addVariable(mod200,Mod2002025Key.PG259, pag.getT00259());
		addVariable(mod200,Mod2002025Key.PG760, pag.getT00760());
		addVariable(mod200,Mod2002025Key.PG761, pag.getT00761());
		addVariable(mod200,Mod2002025Key.PG762, pag.getT00762());
		addVariable(mod200,Mod2002025Key.PG763, pag.getT00763());
		addVariable(mod200,Mod2002025Key.PG771, pag.getT00771());
		addVariable(mod200,Mod2002025Key.PG772, pag.getT00772());
		addVariable(mod200,Mod2002025Key.PG263, pag.getT00263());
		addVariable(mod200,Mod2002025Key.PG264, pag.getT00264());
		addVariable(mod200,Mod2002025Key.PG267, pag.getT00267());
		addVariable(mod200,Mod2002025Key.PG268, pag.getT00268());
		addVariable(mod200,Mod2002025Key.PG269, pag.getT00269());
		addVariable(mod200,Mod2002025Key.PG271, pag.getT00271());
		addVariable(mod200,Mod2002025Key.PG790, pag.getT00790());
		addVariable(mod200,Mod2002025Key.PG273, pag.getT00273());
		addVariable(mod200,Mod2002025Key.PG274, pag.getT00274());
		addVariable(mod200,Mod2002025Key.PG275, pag.getT00275());
		addVariable(mod200,Mod2002025Key.PG276, pag.getT00276());
		addVariable(mod200,Mod2002025Key.PG277, pag.getT00277());
		addVariable(mod200,Mod2002025Key.PG278, pag.getT00278());
		addVariable(mod200,Mod2002025Key.PG253, pag.getT00253());
		addVariable(mod200,Mod2002025Key.PG254, pag.getT00254());
		addVariable(mod200,Mod2002025Key.PG281, pag.getT00281());
		addVariable(mod200,Mod2002025Key.PG282, pag.getT00282());
		addVariable(mod200,Mod2002025Key.PG283, pag.getT00283());
		addVariable(mod200,Mod2002025Key.PG709, pag.getT00709());
		addVariable(mod200,Mod2002025Key.PG284, pag.getT00284());
		addVariable(mod200,Mod2002025Key.PG285, pag.getT00285());
		addVariable(mod200,Mod2002025Key.PG286, pag.getT00286());
		addVariable(mod200,Mod2002025Key.PG289, pag.getT00289());
		addVariable(mod200,Mod2002025Key.PG290, pag.getT00290());
		addVariable(mod200,Mod2002025Key.PG292, pag.getT00292());
		addVariable(mod200,Mod2002025Key.PG293, pag.getT00293());
		addVariable(mod200,Mod2002025Key.PG710, pag.getT00710());
		addVariable(mod200,Mod2002025Key.PG792, pag.getT00792());
		addVariable(mod200,Mod2002025Key.PG793, pag.getT00793());
		addVariable(mod200,Mod2002025Key.PG294, pag.getT00294());
		addVariable(mod200,Mod2002025Key.PG295, pag.getT00295());
	}
	
	private static void fillPagina08(MOD2002025 mod, Mod2002025 mod200) {
		TipoPagina08 pag = mod.getNormal().getCuentaPyG().getPagina08();
		if (pag==null) 
			return;
		
		addVariable(mod200,Mod2002025Key.PG299, pag.getT00299());
		addVariable(mod200,Mod2002025Key.PG300, pag.getT00300());
		addVariable(mod200,Mod2002025Key.PG302, pag.getT00302());
		addVariable(mod200,Mod2002025Key.PG303, pag.getT00303());
		addVariable(mod200,Mod2002025Key.PG794, pag.getT00794());
		addVariable(mod200,Mod2002025Key.PG304, pag.getT00304());
		addVariable(mod200,Mod2002025Key.PG306, pag.getT00306());
		addVariable(mod200,Mod2002025Key.PG307, pag.getT00307());
		addVariable(mod200,Mod2002025Key.PG308, pag.getT00308());
		addVariable(mod200,Mod2002025Key.PG796, pag.getT00796());
		addVariable(mod200,Mod2002025Key.PG309, pag.getT00309());
		addVariable(mod200,Mod2002025Key.PG310, pag.getT00310());
		addVariable(mod200,Mod2002025Key.PG311, pag.getT00311());
		addVariable(mod200,Mod2002025Key.PG312, pag.getT00312());
		addVariable(mod200,Mod2002025Key.PG314, pag.getT00314());
		addVariable(mod200,Mod2002025Key.PG315, pag.getT00315());
		addVariable(mod200,Mod2002025Key.PG316, pag.getT00316());
		addVariable(mod200,Mod2002025Key.PG317, pag.getT00317());
		addVariable(mod200,Mod2002025Key.PG318, pag.getT00318());
		addVariable(mod200,Mod2002025Key.PG319, pag.getT00319());
		addVariable(mod200,Mod2002025Key.PG320, pag.getT00320());
		addVariable(mod200,Mod2002025Key.PG321, pag.getT00321());
		addVariable(mod200,Mod2002025Key.PG322, pag.getT00322());
		addVariable(mod200,Mod2002025Key.PG323, pag.getT00323());
		addVariable(mod200,Mod2002025Key.PG326, pag.getT00326());
		addVariable(mod200,Mod2002025Key.PG328, pag.getT00328());
		addVariable(mod200,Mod2002025Key.PG330, pag.getT00330());
		addVariable(mod200,Mod2002025Key.PG331, pag.getT00331());
		addVariable(mod200,Mod2002025Key.PG332, pag.getT00332());
	}
	
	private static void fillPagina09(MOD2002025 mod, Mod2002025 mod200) {
		
		// A partir del 2016, no tiene por que venir el ECPN, pues es voluntario
		if (mod.getNormal().getCambiosPN()==null) 
			return;
		
		TipoPagina09 pag = mod.getNormal().getCambiosPN().getPagina09();
		if (pag==null) 
			return;
		
		addVariable(mod200,Mod2002025Key.T0336, pag.getT00336());
		addVariable(mod200,Mod2002025Key.T0337, pag.getT00337());
		addVariable(mod200,Mod2002025Key.T0338, pag.getT00338());
		addVariable(mod200,Mod2002025Key.T0339, pag.getT00339());
		addVariable(mod200,Mod2002025Key.T0340, pag.getT00340());
		addVariable(mod200,Mod2002025Key.T0341, pag.getT00341());
		addVariable(mod200,Mod2002025Key.T0342, pag.getT00342());
		addVariable(mod200,Mod2002025Key.T0343, pag.getT00343());
		addVariable(mod200,Mod2002025Key.T0344, pag.getT00344());
		addVariable(mod200,Mod2002025Key.T0346, pag.getT00346());
		addVariable(mod200,Mod2002025Key.T0347, pag.getT00347());
		addVariable(mod200,Mod2002025Key.T0348, pag.getT00348());
		addVariable(mod200,Mod2002025Key.T0349, pag.getT00349());
		addVariable(mod200,Mod2002025Key.T0350, pag.getT00350());
		addVariable(mod200,Mod2002025Key.T0351, pag.getT00351());
		addVariable(mod200,Mod2002025Key.T0352, pag.getT00352());
		addVariable(mod200,Mod2002025Key.T0353, pag.getT00353());
	}

	private static void fillPagina10(MOD2002025 mod, Mod2002025 mod200) {
		
		// A partir del 2016, no tiene por que venir el ECPN pues es voluntario
		if (mod.getNormal().getCambiosPN()==null) 
			return;
		
		TipoPagina10 pag = mod.getNormal().getCambiosPN().getPagina10();
		if (pag==null) 
			return;
		
		addVariable(mod200,Mod2002025Key.TC380, pag.getT00380());
		addVariable(mod200,Mod2002025Key.TC381, pag.getT00381());
		addVariable(mod200,Mod2002025Key.TC382, pag.getT00382());
		addVariable(mod200,Mod2002025Key.TC383, pag.getT00383());
		addVariable(mod200,Mod2002025Key.TC384, pag.getT00384());
		addVariable(mod200,Mod2002025Key.TC385, pag.getT00385());
		addVariable(mod200,Mod2002025Key.TC386, pag.getT00386());
		addVariable(mod200,Mod2002025Key.TC394, pag.getT00394());
		addVariable(mod200,Mod2002025Key.TC395, pag.getT00395());
		addVariable(mod200,Mod2002025Key.TC396, pag.getT00396());
		addVariable(mod200,Mod2002025Key.TC397, pag.getT00397());
		addVariable(mod200,Mod2002025Key.TC398, pag.getT00398());
		addVariable(mod200,Mod2002025Key.TC399, pag.getT00399());
		addVariable(mod200,Mod2002025Key.TC400, pag.getT00400());
		addVariable(mod200,Mod2002025Key.TC408, pag.getT00408());
		addVariable(mod200,Mod2002025Key.TC409, pag.getT00409());
		addVariable(mod200,Mod2002025Key.TC410, pag.getT00410());
		addVariable(mod200,Mod2002025Key.TC411, pag.getT00411());
		addVariable(mod200,Mod2002025Key.TC412, pag.getT00412());
		addVariable(mod200,Mod2002025Key.TC413, pag.getT00413());
		addVariable(mod200,Mod2002025Key.TC414, pag.getT00414());
		addVariable(mod200,Mod2002025Key.TC436, pag.getT00436());
		addVariable(mod200,Mod2002025Key.TC437, pag.getT00437());
		addVariable(mod200,Mod2002025Key.TC438, pag.getT00438());
		addVariable(mod200,Mod2002025Key.TC439, pag.getT00439());
		addVariable(mod200,Mod2002025Key.TC440, pag.getT00440());
		addVariable(mod200,Mod2002025Key.TC441, pag.getT00441());
		addVariable(mod200,Mod2002025Key.TC442, pag.getT00442());
		addVariable(mod200,Mod2002025Key.TC450, pag.getT00450());
		addVariable(mod200,Mod2002025Key.TC451, pag.getT00451());
		addVariable(mod200,Mod2002025Key.TC452, pag.getT00452());
		addVariable(mod200,Mod2002025Key.TC453, pag.getT00453());
		addVariable(mod200,Mod2002025Key.TC454, pag.getT00454());
		addVariable(mod200,Mod2002025Key.TC455, pag.getT00455());
		addVariable(mod200,Mod2002025Key.TC456, pag.getT00456());
		addVariable(mod200,Mod2002025Key.TC478, pag.getT00478());
		addVariable(mod200,Mod2002025Key.TC479, pag.getT00479());
		addVariable(mod200,Mod2002025Key.TC480, pag.getT00480());
		addVariable(mod200,Mod2002025Key.TC481, pag.getT00481());
		addVariable(mod200,Mod2002025Key.TC482, pag.getT00482());
		addVariable(mod200,Mod2002025Key.TC483, pag.getT00483());
		addVariable(mod200,Mod2002025Key.TC484, pag.getT00484());
		addVariable(mod200,Mod2002025Key.TC492, pag.getT00492());
		addVariable(mod200,Mod2002025Key.TC493, pag.getT00493());
		addVariable(mod200,Mod2002025Key.TC494, pag.getT00494());
		addVariable(mod200,Mod2002025Key.TC495, pag.getT00495());
		addVariable(mod200,Mod2002025Key.TC496, pag.getT00496());
		addVariable(mod200,Mod2002025Key.TC497, pag.getT00497());
		addVariable(mod200,Mod2002025Key.TC498, pag.getT00498());
		addVariable(mod200,Mod2002025Key.TC520, pag.getT00520());
		addVariable(mod200,Mod2002025Key.TC521, pag.getT00521());
		addVariable(mod200,Mod2002025Key.TC522, pag.getT00522());
		addVariable(mod200,Mod2002025Key.TC523, pag.getT00523());
		addVariable(mod200,Mod2002025Key.TC524, pag.getT00524());
		addVariable(mod200,Mod2002025Key.TC525, pag.getT00525());
		addVariable(mod200,Mod2002025Key.TC526, pag.getT00526());
		addVariable(mod200,Mod2002025Key.TC534, pag.getT00534());
		addVariable(mod200,Mod2002025Key.TC535, pag.getT00535());
		addVariable(mod200,Mod2002025Key.TC536, pag.getT00536());
		addVariable(mod200,Mod2002025Key.TC537, pag.getT00537());
		addVariable(mod200,Mod2002025Key.TC538, pag.getT00538());
		addVariable(mod200,Mod2002025Key.TC539, pag.getT00539());
		addVariable(mod200,Mod2002025Key.TC540, pag.getT00540());
		addVariable(mod200,Mod2002025Key.TC548, pag.getT00548());
		addVariable(mod200,Mod2002025Key.TC549, pag.getT00549());
		addVariable(mod200,Mod2002025Key.TC550, pag.getT00550());
		addVariable(mod200,Mod2002025Key.TC551, pag.getT00551());
		addVariable(mod200,Mod2002025Key.TC552, pag.getT00552());
		addVariable(mod200,Mod2002025Key.TC553, pag.getT00553());
		addVariable(mod200,Mod2002025Key.TC554, pag.getT00554());
		addVariable(mod200,Mod2002025Key.TC562, pag.getT00562());
		addVariable(mod200,Mod2002025Key.TC563, pag.getT00563());
		addVariable(mod200,Mod2002025Key.TC564, pag.getT00564());
		addVariable(mod200,Mod2002025Key.TC565, pag.getT00565());
		addVariable(mod200,Mod2002025Key.TC566, pag.getT00566());
		addVariable(mod200,Mod2002025Key.TC567, pag.getT00567());
		addVariable(mod200,Mod2002025Key.TC568, pag.getT00568());
		addVariable(mod200,Mod2002025Key.TC576, pag.getT00576());
		addVariable(mod200,Mod2002025Key.TC577, pag.getT00577());
		addVariable(mod200,Mod2002025Key.TC578, pag.getT00578());
		addVariable(mod200,Mod2002025Key.TC579, pag.getT00579());
		addVariable(mod200,Mod2002025Key.TC580, pag.getT00580());
		addVariable(mod200,Mod2002025Key.TC581, pag.getT00581());
		addVariable(mod200,Mod2002025Key.TC582, pag.getT00582());
		addVariable(mod200,Mod2002025Key.TC590, pag.getT00590());
		addVariable(mod200,Mod2002025Key.TC591, pag.getT00591());
		addVariable(mod200,Mod2002025Key.TC592, pag.getT00592());
		addVariable(mod200,Mod2002025Key.TC593, pag.getT00593());
		addVariable(mod200,Mod2002025Key.TC594, pag.getT00594());
		addVariable(mod200,Mod2002025Key.TC595, pag.getT00595());
		addVariable(mod200,Mod2002025Key.TC596, pag.getT00596());
		addVariable(mod200,Mod2002025Key.TC604, pag.getT00604());
		addVariable(mod200,Mod2002025Key.TC605, pag.getT00605());
		addVariable(mod200,Mod2002025Key.TC606, pag.getT00606());
		addVariable(mod200,Mod2002025Key.TC607, pag.getT00607());
		addVariable(mod200,Mod2002025Key.TC608, pag.getT00608());
		addVariable(mod200,Mod2002025Key.TC609, pag.getT00609());
		addVariable(mod200,Mod2002025Key.TC610, pag.getT00610());
		addVariable(mod200,Mod2002025Key.TC618, pag.getT00618());
		addVariable(mod200,Mod2002025Key.TC619, pag.getT00619());
		addVariable(mod200,Mod2002025Key.TC620, pag.getT00620());
		addVariable(mod200,Mod2002025Key.TC621, pag.getT00621());
		addVariable(mod200,Mod2002025Key.TC622, pag.getT00622());
		addVariable(mod200,Mod2002025Key.TC623, pag.getT00623());
		addVariable(mod200,Mod2002025Key.TC624, pag.getT00624());
		addVariable(mod200,Mod2002025Key.TC715, pag.getT00715());
		addVariable(mod200,Mod2002025Key.TC716, pag.getT00716());
		addVariable(mod200,Mod2002025Key.TC717, pag.getT00717());
		addVariable(mod200,Mod2002025Key.TC718, pag.getT00718());
		addVariable(mod200,Mod2002025Key.TC719, pag.getT00719());
		addVariable(mod200,Mod2002025Key.TC720, pag.getT00720());
		addVariable(mod200,Mod2002025Key.TC721, pag.getT00721());
		addVariable(mod200,Mod2002025Key.TC729, pag.getT00729());
		addVariable(mod200,Mod2002025Key.TC730, pag.getT00730());
		addVariable(mod200,Mod2002025Key.TC731, pag.getT00731());
		addVariable(mod200,Mod2002025Key.TC732, pag.getT00732());
		addVariable(mod200,Mod2002025Key.TC733, pag.getT00733());
		addVariable(mod200,Mod2002025Key.TC734, pag.getT00734());
		addVariable(mod200,Mod2002025Key.TC735, pag.getT00735());
	}

	private static void fillPagina11(MOD2002025 mod, Mod2002025 mod200) {

		// A partir del 2016, no tiene por que venir el ECPN pues es voluntario
		if (mod.getNormal().getCambiosPN()==null) 
			return;

		TipoPagina11 pag = mod.getNormal().getCambiosPN().getPagina11();
		if (pag==null) 
			return;
		
		addVariable(mod200,Mod2002025Key.TC387, pag.getT00387());
		addVariable(mod200,Mod2002025Key.TC388, pag.getT00388());
		addVariable(mod200,Mod2002025Key.TC389, pag.getT00389());
		addVariable(mod200,Mod2002025Key.TC390, pag.getT00390());
		addVariable(mod200,Mod2002025Key.TC391, pag.getT00391());
		addVariable(mod200,Mod2002025Key.TC392, pag.getT00392());
		addVariable(mod200,Mod2002025Key.TC401, pag.getT00401());
		addVariable(mod200,Mod2002025Key.TC402, pag.getT00402());
		addVariable(mod200,Mod2002025Key.TC403, pag.getT00403());
		addVariable(mod200,Mod2002025Key.TC404, pag.getT00404());
		addVariable(mod200,Mod2002025Key.TC405, pag.getT00405());
		addVariable(mod200,Mod2002025Key.TC406, pag.getT00406());
		addVariable(mod200,Mod2002025Key.TC415, pag.getT00415());
		addVariable(mod200,Mod2002025Key.TC416, pag.getT00416());
		addVariable(mod200,Mod2002025Key.TC417, pag.getT00417());
		addVariable(mod200,Mod2002025Key.TC418, pag.getT00418());
		addVariable(mod200,Mod2002025Key.TC419, pag.getT00419());
		addVariable(mod200,Mod2002025Key.TC420, pag.getT00420());
		addVariable(mod200,Mod2002025Key.TC443, pag.getT00443());
		addVariable(mod200,Mod2002025Key.TC444, pag.getT00444());
		addVariable(mod200,Mod2002025Key.TC445, pag.getT00445());
		addVariable(mod200,Mod2002025Key.TC446, pag.getT00446());
		addVariable(mod200,Mod2002025Key.TC448, pag.getT00448());
		addVariable(mod200,Mod2002025Key.TC457, pag.getT00457());
		addVariable(mod200,Mod2002025Key.TC458, pag.getT00458());
		addVariable(mod200,Mod2002025Key.TC461, pag.getT00461());
		addVariable(mod200,Mod2002025Key.TC462, pag.getT00462());
		addVariable(mod200,Mod2002025Key.TC485, pag.getT00485());
		addVariable(mod200,Mod2002025Key.TC486, pag.getT00486());
		addVariable(mod200,Mod2002025Key.TC489, pag.getT00489());
		addVariable(mod200,Mod2002025Key.TC490, pag.getT00490());
		addVariable(mod200,Mod2002025Key.TC499, pag.getT00499());
		addVariable(mod200,Mod2002025Key.TC502, pag.getT00502());
		addVariable(mod200,Mod2002025Key.TC503, pag.getT00503());
		addVariable(mod200,Mod2002025Key.TC504, pag.getT00504());
		addVariable(mod200,Mod2002025Key.TC527, pag.getT00527());
		addVariable(mod200,Mod2002025Key.TC528, pag.getT00528());
		addVariable(mod200,Mod2002025Key.TC529, pag.getT00529());
		addVariable(mod200,Mod2002025Key.TC530, pag.getT00530());
		addVariable(mod200,Mod2002025Key.TC531, pag.getT00531());
		addVariable(mod200,Mod2002025Key.TC532, pag.getT00532());
		addVariable(mod200,Mod2002025Key.TC541, pag.getT00541());
		addVariable(mod200,Mod2002025Key.TC542, pag.getT00542());
		addVariable(mod200,Mod2002025Key.TC543, pag.getT00543());
		addVariable(mod200,Mod2002025Key.TC544, pag.getT00544());
		addVariable(mod200,Mod2002025Key.TC545, pag.getT00545());
		addVariable(mod200,Mod2002025Key.TC546, pag.getT00546());
		addVariable(mod200,Mod2002025Key.TC555, pag.getT00555());
		addVariable(mod200,Mod2002025Key.TC556, pag.getT00556());
		addVariable(mod200,Mod2002025Key.TC557, pag.getT00557());
		addVariable(mod200,Mod2002025Key.TC558, pag.getT00558());
		addVariable(mod200,Mod2002025Key.TC560, pag.getT00560());
		addVariable(mod200,Mod2002025Key.TC569, pag.getT00569());
		addVariable(mod200,Mod2002025Key.TC570, pag.getT00570());
		addVariable(mod200,Mod2002025Key.TC571, pag.getT00571());
		addVariable(mod200,Mod2002025Key.TC572, pag.getT00572());
		addVariable(mod200,Mod2002025Key.TC574, pag.getT00574());
		addVariable(mod200,Mod2002025Key.TC583, pag.getT00583());
		addVariable(mod200,Mod2002025Key.TC584, pag.getT00584());
		addVariable(mod200,Mod2002025Key.TC585, pag.getT00585());
		addVariable(mod200,Mod2002025Key.TC586, pag.getT00586());
		addVariable(mod200,Mod2002025Key.TC588, pag.getT00588());
		addVariable(mod200,Mod2002025Key.TC597, pag.getT00597());
		addVariable(mod200,Mod2002025Key.TC598, pag.getT00598());
		addVariable(mod200,Mod2002025Key.TC599, pag.getT00599());
		addVariable(mod200,Mod2002025Key.TC600, pag.getT00600());
		addVariable(mod200,Mod2002025Key.TC602, pag.getT00602());
		addVariable(mod200,Mod2002025Key.TC611, pag.getT00611());
		addVariable(mod200,Mod2002025Key.TC612, pag.getT00612());
		addVariable(mod200,Mod2002025Key.TC613, pag.getT00613());
		addVariable(mod200,Mod2002025Key.TC614, pag.getT00614());
		addVariable(mod200,Mod2002025Key.TC615, pag.getT00615());
		addVariable(mod200,Mod2002025Key.TC616, pag.getT00616());
		addVariable(mod200,Mod2002025Key.TC625, pag.getT00625());
		addVariable(mod200,Mod2002025Key.TC626, pag.getT00626());
		addVariable(mod200,Mod2002025Key.TC627, pag.getT00627());
		addVariable(mod200,Mod2002025Key.TC628, pag.getT00628());
		addVariable(mod200,Mod2002025Key.TC629, pag.getT00629());
		addVariable(mod200,Mod2002025Key.TC630, pag.getT00630());
		addVariable(mod200,Mod2002025Key.TC722, pag.getT00722());
		addVariable(mod200,Mod2002025Key.TC723, pag.getT00723());
		addVariable(mod200,Mod2002025Key.TC724, pag.getT00724());
		addVariable(mod200,Mod2002025Key.TC725, pag.getT00725());
		addVariable(mod200,Mod2002025Key.TC726, pag.getT00726());
		addVariable(mod200,Mod2002025Key.TC727, pag.getT00727());
		addVariable(mod200,Mod2002025Key.TC736, pag.getT00736());
		addVariable(mod200,Mod2002025Key.TC737, pag.getT00737());
		addVariable(mod200,Mod2002025Key.TC738, pag.getT00738());
		addVariable(mod200,Mod2002025Key.TC739, pag.getT00739());
		addVariable(mod200,Mod2002025Key.TC740, pag.getT00740());
		addVariable(mod200,Mod2002025Key.TC741, pag.getT00741());
	}
	
	private static void addVariable(Mod2002025 mod200, Mod2002025Key key, Number d) {
		DoubleVariableEx dv = new DoubleVariableEx(key);
		dv.setValue(d == null ? 0.0 : d.doubleValue());
		mod200.addVariable(dv);
		// Se añade tambien a draft, para que se repinten los datos en todas las páginas, despues de cargar el fichero
		dv = new DoubleVariableEx(key);
		dv.setValue(d == null ? 0.0 : d.doubleValue());
		mod200.addDraftVariable(dv);
	}
	
}
