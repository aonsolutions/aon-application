package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014LQ561Key implements Serializable, IMod200KeysProvider  {

	 C0001(new Mod2002014Key[]{Mod2002014Key.LQ673,Mod2002014Key.LQ674,null },"Compensaci\u00F3n de cuota a\u00F1o 1998")
	,C0002(new Mod2002014Key[]{Mod2002014Key.LQ676,Mod2002014Key.LQ677,Mod2002014Key.LQ678},"Compensaci\u00F3n de cuota a\u00F1o 1999")
	,C0003(new Mod2002014Key[]{Mod2002014Key.LQ679,Mod2002014Key.LQ680,Mod2002014Key.LQ681},"Compensaci\u00F3n de cuota a\u00F1o 2000")
	,C0004(new Mod2002014Key[]{Mod2002014Key.LQ682,Mod2002014Key.LQ683,Mod2002014Key.LQ684},"Compensaci\u00F3n de cuota a\u00F1o 2001")
	,C0005(new Mod2002014Key[]{Mod2002014Key.LQ685,Mod2002014Key.LQ686,Mod2002014Key.LQ687},"Compensaci\u00F3n de cuota a\u00F1o 2002")
	,C0006(new Mod2002014Key[]{Mod2002014Key.LQ688,Mod2002014Key.LQ689,Mod2002014Key.LQ690},"Compensaci\u00F3n de cuota a\u00F1o 2003")
	,C0007(new Mod2002014Key[]{Mod2002014Key.LQ691,Mod2002014Key.LQ692,Mod2002014Key.LQ693},"Compensaci\u00F3n de cuota a\u00F1o 2004")
	,C0008(new Mod2002014Key[]{Mod2002014Key.LQ623,Mod2002014Key.LQ624,Mod2002014Key.LQ672},"Compensaci\u00F3n de cuota a\u00F1o 2005") 
	,C0009(new Mod2002014Key[]{Mod2002014Key.LQ279,Mod2002014Key.LQ280,Mod2002014Key.LQ281},"Compensaci\u00F3n de cuota a\u00F1o 2006")
	,C0010(new Mod2002014Key[]{Mod2002014Key.LQ587,Mod2002014Key.LQ515,Mod2002014Key.LQ900},"Compensaci\u00F3n de cuota a\u00F1o 2007")
	,C0011(new Mod2002014Key[]{Mod2002014Key.LQ059,Mod2002014Key.LQ099,Mod2002014Key.LQ100},"Compensaci\u00F3n de cuota a\u00F1o 2008")
	,C0012(new Mod2002014Key[]{Mod2002014Key.LQ017,Mod2002014Key.LQ018,Mod2002014Key.LQ019},"Compensaci\u00F3n de cuota a\u00F1o 2009")
	,C0013(new Mod2002014Key[]{Mod2002014Key.LQ772,Mod2002014Key.LQ773,Mod2002014Key.LQ777},"Compensaci\u00F3n de cuota a\u00F1o 2010") 
	,C0014(new Mod2002014Key[]{Mod2002014Key.LQ907,Mod2002014Key.LQ908,Mod2002014Key.LQ909},"Compensaci\u00F3n de cuota a\u00F1o 2011") 
	,C0015(new Mod2002014Key[]{Mod2002014Key.LQ910,Mod2002014Key.LQ911,Mod2002014Key.LQ912},"Compensaci\u00F3n de cuota a\u00F1o 2012")
	,C0016(new Mod2002014Key[]{Mod2002014Key.LQ935,Mod2002014Key.LQ936,Mod2002014Key.LQ937},"Compensaci\u00F3n de cuota a\u00F1o 2013")
	,C0017(new Mod2002014Key[]{Mod2002014Key.LQ694,null 			    ,Mod2002014Key.LQ695},"Total")
	;
	 
    private String description;
    private Mod2002014Key[] keys;

	private Mod2002014LQ561Key(Mod2002014Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod2002014Key[] getKeys() {
		return keys; 
	}
}

