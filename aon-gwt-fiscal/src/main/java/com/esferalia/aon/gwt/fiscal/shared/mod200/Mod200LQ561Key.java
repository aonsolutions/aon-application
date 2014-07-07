package com.esferalia.aon.gwt.fiscal.shared.mod200;

import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key.*;
import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200LQ561Key implements Serializable, IsSerializable, IMod200KeysProvider  {

	 C0001(new Mod200Key[]{LQ673,LQ674,null },"Compensaci\u00F3n de cuota a\u00F1o 1998")
	,C0002(new Mod200Key[]{LQ676,LQ677,LQ678},"Compensaci\u00F3n de cuota a\u00F1o 1999")
	,C0003(new Mod200Key[]{LQ679,LQ680,LQ681},"Compensaci\u00F3n de cuota a\u00F1o 2000")
	,C0004(new Mod200Key[]{LQ682,LQ683,LQ684},"Compensaci\u00F3n de cuota a\u00F1o 2001")
	,C0005(new Mod200Key[]{LQ685,LQ686,LQ687},"Compensaci\u00F3n de cuota a\u00F1o 2002")
	,C0006(new Mod200Key[]{LQ688,LQ689,LQ690},"Compensaci\u00F3n de cuota a\u00F1o 2003")
	,C0007(new Mod200Key[]{LQ691,LQ692,LQ693},"Compensaci\u00F3n de cuota a\u00F1o 2004")
	,C0008(new Mod200Key[]{LQ623,LQ624,LQ672},"Compensaci\u00F3n de cuota a\u00F1o 2005") 
	,C0009(new Mod200Key[]{LQ279,LQ280,LQ281},"Compensaci\u00F3n de cuota a\u00F1o 2006")
	,C0010(new Mod200Key[]{LQ587,LQ515,LQ900},"Compensaci\u00F3n de cuota a\u00F1o 2007")
	,C0011(new Mod200Key[]{LQ059,LQ099,LQ100},"Compensaci\u00F3n de cuota a\u00F1o 2008")
	,C0012(new Mod200Key[]{LQ017,LQ018,LQ019},"Compensaci\u00F3n de cuota a\u00F1o 2009")
	,C0013(new Mod200Key[]{LQ772,LQ773,LQ777},"Compensaci\u00F3n de cuota a\u00F1o 2010") 
	,C0014(new Mod200Key[]{LQ907,LQ908,LQ909},"Compensaci\u00F3n de cuota a\u00F1o 2011") 
	,C0015(new Mod200Key[]{LQ910,LQ911,LQ912},"Compensaci\u00F3n de cuota a\u00F1o 2012")
	,C0016(new Mod200Key[]{LQ935,LQ936,LQ937},"Compensaci\u00F3n de cuota a\u00F1o 2013")
	,C0017(new Mod200Key[]{LQ694,null ,LQ695},"Total")
	;
	 
    private String description;
    private Mod200Key[] keys;

	private Mod200LQ561Key(Mod200Key[] keys, String description) {
		this.description = description;
		this.keys = keys;
	}
	
	public String getDescription() {
		return description;
	}

	@Override
	public Mod200Key[] getKeys() {
		return keys; 
	}
}

