package com.esferalia.aon.occam.api.model.fiscal.mod200_2014;

import java.io.Serializable;


public enum Mod2002014LQ547Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(Mod2002014Key.LQ640,Mod2002014Key.LQ641,Mod2002014Key.LQ548,"Compensaci\u00F3n de base a\u00F1o 1997")
	,C0002(Mod2002014Key.LQ643,Mod2002014Key.LQ644,Mod2002014Key.LQ645,"Compensaci\u00F3n de base a\u00F1o 1998")
	,C0003(Mod2002014Key.LQ646,Mod2002014Key.LQ647,Mod2002014Key.LQ648,"Compensaci\u00F3n de base a\u00F1o 1999")
	,C0004(Mod2002014Key.LQ649,Mod2002014Key.LQ650,Mod2002014Key.LQ651,"Compensaci\u00F3n de base a\u00F1o 2000")
	,C0005(Mod2002014Key.LQ652,Mod2002014Key.LQ653,Mod2002014Key.LQ654,"Compensaci\u00F3n de base a\u00F1o 2001")
	,C0006(Mod2002014Key.LQ655,Mod2002014Key.LQ656,Mod2002014Key.LQ657,"Compensaci\u00F3n de base a\u00F1o 2002")
	,C0007(Mod2002014Key.LQ658,Mod2002014Key.LQ659,Mod2002014Key.LQ660,"Compensaci\u00F3n de base a\u00F1o 2003")
	,C0008(Mod2002014Key.LQ661,Mod2002014Key.LQ662,Mod2002014Key.LQ663,"Compensaci\u00F3n de base a\u00F1o 2004")
	,C0009(Mod2002014Key.LQ664,Mod2002014Key.LQ665,Mod2002014Key.LQ666,"Compensaci\u00F3n de base a\u00F1o 2005")
	,C0010(Mod2002014Key.LQ667,Mod2002014Key.LQ668,Mod2002014Key.LQ669,"Compensaci\u00F3n de base a\u00F1o 2006")
	,C0011(Mod2002014Key.LQ743,Mod2002014Key.LQ747,Mod2002014Key.LQ748,"Compensaci\u00F3n de base a\u00F1o 2007")
	,C0012(Mod2002014Key.LQ275,Mod2002014Key.LQ276,Mod2002014Key.LQ277,"Compensaci\u00F3n de base a\u00F1o 2008")
	,C0013(Mod2002014Key.LQ608,Mod2002014Key.LQ609,Mod2002014Key.LQ610,"Compensaci\u00F3n de base a\u00F1o 2009")
	,C0014(Mod2002014Key.LQ704,Mod2002014Key.LQ705,Mod2002014Key.LQ706,"Compensaci\u00F3n de base a\u00F1o 2010")
	,C0015(Mod2002014Key.LQ013,Mod2002014Key.LQ014,Mod2002014Key.LQ015,"Compensaci\u00F3n de base a\u00F1o 2011")
	,C0016(Mod2002014Key.LQ725,Mod2002014Key.LQ726,Mod2002014Key.LQ727,"Compensaci\u00F3n de base a\u00F1o 2012")
	,C0017(Mod2002014Key.LQ534,Mod2002014Key.LQ535,Mod2002014Key.LQ536,"Compensaci\u00F3n de base a\u00F1o 2013")
	,C0018(Mod2002014Key.LQ607,Mod2002014Key.LQ675,Mod2002014Key.LQ699,"Compensaci\u00F3n de base a\u00F1o 2014")
	,C0019(Mod2002014Key.LQ670,null           ,Mod2002014Key.LQ671,"Total")
	;
	 
    private String description;
    private Mod2002014Key previousPendind;
    private Mod2002014Key current;
    private Mod2002014Key futurePendind;
    private Mod2002014Key[] keys;

	private Mod2002014LQ547Key(Mod2002014Key previousPendind,
			Mod2002014Key current, Mod2002014Key futurePendind, String description) {
		this.previousPendind = previousPendind;
		this.current = current;
		this.futurePendind = futurePendind;
		this.description = description;
		keys = new Mod2002014Key[]{previousPendind,current,futurePendind};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002014Key getPreviousPendind() {
		return previousPendind;
	}
	public Mod2002014Key getCurrent() {
		return current;
	}
	public Mod2002014Key getFuturePendind() {
		return futurePendind;
	}
	
	@Override
	public Mod2002014Key[] getKeys() {
		return keys; 
	}
}

