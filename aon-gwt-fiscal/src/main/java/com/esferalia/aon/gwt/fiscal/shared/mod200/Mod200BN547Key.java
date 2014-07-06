package com.esferalia.aon.gwt.fiscal.shared.mod200;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;


public enum Mod200BN547Key implements Serializable, IsSerializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C0001(Mod200Key.LQ640,Mod200Key.LQ641,Mod200Key.LQ548,"Compensaci\u00F3n de base a\u00F1o 1997")
	,C0002(Mod200Key.LQ643,Mod200Key.LQ644,Mod200Key.LQ645,"Compensaci\u00F3n de base a\u00F1o 1998")
	,C0003(Mod200Key.LQ646,Mod200Key.LQ647,Mod200Key.LQ648,"Compensaci\u00F3n de base a\u00F1o 1999")
	,C0004(Mod200Key.LQ649,Mod200Key.LQ650,Mod200Key.LQ651,"Compensaci\u00F3n de base a\u00F1o 2000")
	,C0005(Mod200Key.LQ652,Mod200Key.LQ653,Mod200Key.LQ654,"Compensaci\u00F3n de base a\u00F1o 2001")
	,C0006(Mod200Key.LQ655,Mod200Key.LQ656,Mod200Key.LQ657,"Compensaci\u00F3n de base a\u00F1o 2002")
	,C0007(Mod200Key.LQ658,Mod200Key.LQ659,Mod200Key.LQ660,"Compensaci\u00F3n de base a\u00F1o 2003")
	,C0008(Mod200Key.LQ661,Mod200Key.LQ662,Mod200Key.LQ663,"Compensaci\u00F3n de base a\u00F1o 2004")
	,C0009(Mod200Key.LQ664,Mod200Key.LQ665,Mod200Key.LQ666,"Compensaci\u00F3n de base a\u00F1o 2005")
	,C0010(Mod200Key.LQ667,Mod200Key.LQ668,Mod200Key.LQ669,"Compensaci\u00F3n de base a\u00F1o 2006")
	,C0011(Mod200Key.LQ743,Mod200Key.LQ747,Mod200Key.LQ748,"Compensaci\u00F3n de base a\u00F1o 2007")
	,C0012(Mod200Key.LQ275,Mod200Key.LQ276,Mod200Key.LQ277,"Compensaci\u00F3n de base a\u00F1o 2008")
	,C0013(Mod200Key.LQ608,Mod200Key.LQ609,Mod200Key.LQ610,"Compensaci\u00F3n de base a\u00F1o 2009")
	,C0014(Mod200Key.LQ704,Mod200Key.LQ705,Mod200Key.LQ706,"Compensaci\u00F3n de base a\u00F1o 2010")
	,C0015(Mod200Key.LQ013,Mod200Key.LQ014,Mod200Key.LQ015,"Compensaci\u00F3n de base a\u00F1o 2011")
	,C0016(Mod200Key.LQ725,Mod200Key.LQ726,Mod200Key.LQ727,"Compensaci\u00F3n de base a\u00F1o 2012")
	,C0017(Mod200Key.LQ534,Mod200Key.LQ535,Mod200Key.LQ536,"Compensaci\u00F3n de base a\u00F1o 2013")
	,C0018(Mod200Key.LQ670,null           ,Mod200Key.LQ671,"Total")
	;
	 
    private String description;
    private Mod200Key previousPendind;
    private Mod200Key current;
    private Mod200Key futurePendind;
    private Mod200Key[] keys;

	private Mod200BN547Key(Mod200Key previousPendind,
			Mod200Key current, Mod200Key futurePendind, String description) {
		this.previousPendind = previousPendind;
		this.current = current;
		this.futurePendind = futurePendind;
		this.description = description;
		keys = new Mod200Key[]{previousPendind,current,futurePendind};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod200Key getPreviousPendind() {
		return previousPendind;
	}
	public Mod200Key getCurrent() {
		return current;
	}
	public Mod200Key getFuturePendind() {
		return futurePendind;
	}
	
	@Override
	public Mod200Key[] getKeys() {
		return keys; 
	}
}

