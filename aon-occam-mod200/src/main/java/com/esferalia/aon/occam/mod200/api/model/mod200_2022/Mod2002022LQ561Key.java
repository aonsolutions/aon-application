package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Régimen de cooperativas - Detalle de compensación de cuotas
public enum Mod2002022LQ561Key implements Serializable, IMod200KeysProvider  {

	 C01(new Mod2002022Key[]{Mod2002022Key.LQ673 ,Mod2002022Key.LQ674 ,Mod2002022Key.LQ1224},"Compensaci\u00F3n de cuota a\u00F1o 2000")
	,C02(new Mod2002022Key[]{Mod2002022Key.LQ676 ,Mod2002022Key.LQ677 ,Mod2002022Key.LQ678 },"Compensaci\u00F3n de cuota a\u00F1o 2001")
	,C03(new Mod2002022Key[]{Mod2002022Key.LQ679 ,Mod2002022Key.LQ680 ,Mod2002022Key.LQ681 },"Compensaci\u00F3n de cuota a\u00F1o 2002")
	,C04(new Mod2002022Key[]{Mod2002022Key.LQ682 ,Mod2002022Key.LQ683 ,Mod2002022Key.LQ684 },"Compensaci\u00F3n de cuota a\u00F1o 2003")
	,C05(new Mod2002022Key[]{Mod2002022Key.LQ685 ,Mod2002022Key.LQ686 ,Mod2002022Key.LQ687 },"Compensaci\u00F3n de cuota a\u00F1o 2004")
	,C06(new Mod2002022Key[]{Mod2002022Key.LQ688 ,Mod2002022Key.LQ689 ,Mod2002022Key.LQ690 },"Compensaci\u00F3n de cuota a\u00F1o 2005")
	,C07(new Mod2002022Key[]{Mod2002022Key.LQ691 ,Mod2002022Key.LQ692 ,Mod2002022Key.LQ693 },"Compensaci\u00F3n de cuota a\u00F1o 2006")
	,C08(new Mod2002022Key[]{Mod2002022Key.LQ623 ,Mod2002022Key.LQ624 ,Mod2002022Key.LQ672 },"Compensaci\u00F3n de cuota a\u00F1o 2007")
	,C09(new Mod2002022Key[]{Mod2002022Key.LQ279 ,Mod2002022Key.LQ280 ,Mod2002022Key.LQ281 },"Compensaci\u00F3n de cuota a\u00F1o 2008")
	,C10(new Mod2002022Key[]{Mod2002022Key.LQ587 ,Mod2002022Key.LQ515 ,Mod2002022Key.LQ900 },"Compensaci\u00F3n de cuota a\u00F1o 2009")
	,C11(new Mod2002022Key[]{Mod2002022Key.LQ059 ,Mod2002022Key.LQ099 ,Mod2002022Key.LQ100 },"Compensaci\u00F3n de cuota a\u00F1o 2010")
	,C12(new Mod2002022Key[]{Mod2002022Key.LQ017 ,Mod2002022Key.LQ018 ,Mod2002022Key.LQ019 },"Compensaci\u00F3n de cuota a\u00F1o 2011")
	,C13(new Mod2002022Key[]{Mod2002022Key.LQ772 ,Mod2002022Key.LQ773 ,Mod2002022Key.LQ777 },"Compensaci\u00F3n de cuota a\u00F1o 2012")
	,C14(new Mod2002022Key[]{Mod2002022Key.LQ907 ,Mod2002022Key.LQ908 ,Mod2002022Key.LQ909 },"Compensaci\u00F3n de cuota a\u00F1o 2013")
	,C15(new Mod2002022Key[]{Mod2002022Key.LQ910 ,Mod2002022Key.LQ911 ,Mod2002022Key.LQ912 },"Compensaci\u00F3n de cuota a\u00F1o 2014")
	,C16(new Mod2002022Key[]{Mod2002022Key.LQ935 ,Mod2002022Key.LQ936 ,Mod2002022Key.LQ937 },"Compensaci\u00F3n de cuota a\u00F1o 2015")
	,C17(new Mod2002022Key[]{Mod2002022Key.LQ1511,Mod2002022Key.LQ1512,Mod2002022Key.LQ1513},"Compensaci\u00F3n de cuota a\u00F1o 2016")
	,C18(new Mod2002022Key[]{Mod2002022Key.LQ1767,Mod2002022Key.LQ1768,Mod2002022Key.LQ1769},"Compensaci\u00F3n de cuota a\u00F1o 2017")
	,C19(new Mod2002022Key[]{Mod2002022Key.LQ2113,Mod2002022Key.LQ2114,Mod2002022Key.LQ2115},"Compensaci\u00F3n de cuota a\u00F1o 2018")
	,C20(new Mod2002022Key[]{Mod2002022Key.LQ2281,Mod2002022Key.LQ2282,Mod2002022Key.LQ2283},"Compensaci\u00F3n de cuota a\u00F1o 2019")
	,C21(new Mod2002022Key[]{Mod2002022Key.LQ2452,Mod2002022Key.LQ2453,Mod2002022Key.LQ2454},"Compensaci\u00F3n de cuota a\u00F1o 2020")
	,C22(new Mod2002022Key[]{Mod2002022Key.LQ1186,Mod2002022Key.LQ1187,Mod2002022Key.LQ1190},"Compensaci\u00F3n de cuota a\u00F1o 2021")	
	,C23(new Mod2002022Key[]{Mod2002022Key.LQ1516,Mod2002022Key.LQ1517,Mod2002022Key.LQ1518},"Compensaci\u00F3n de cuota a\u00F1o 2022(*)")	
	,C24(new Mod2002022Key[]{Mod2002022Key.LQ694 ,Mod2002022Key.LQ561 ,Mod2002022Key.LQ695 },"Total")
	,C25(new Mod2002022Key[]{Mod2002022Key.LQ1225,null                ,Mod2002022Key.LQ1226},"Compensaci\u00F3n de cuota a\u00F1o 2022")
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LQ561Key(Mod2002022Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002022Key[] getKeys() {
		return keys; 
	}

}

