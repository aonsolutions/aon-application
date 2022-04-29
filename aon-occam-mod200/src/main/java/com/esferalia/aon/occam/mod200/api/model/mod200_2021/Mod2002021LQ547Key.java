package com.esferalia.aon.occam.mod200.api.model.mod200_2021;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Detalle de la compensación de bases imponibles negativas
public enum Mod2002021LQ547Key implements Serializable, IMod200KeysProvider  {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(Mod2002021Key.LQ640 ,Mod2002021Key.LQ641 ,Mod2002021Key.LQ548 ,"Compensaci\u00F3n de base a\u00F1o 1997")
	,C02(Mod2002021Key.LQ643 ,Mod2002021Key.LQ644 ,Mod2002021Key.LQ645 ,"Compensaci\u00F3n de base a\u00F1o 1998")
	,C03(Mod2002021Key.LQ646 ,Mod2002021Key.LQ647 ,Mod2002021Key.LQ648 ,"Compensaci\u00F3n de base a\u00F1o 1999")
	,C04(Mod2002021Key.LQ649 ,Mod2002021Key.LQ650 ,Mod2002021Key.LQ651 ,"Compensaci\u00F3n de base a\u00F1o 2000")
	,C05(Mod2002021Key.LQ652 ,Mod2002021Key.LQ653 ,Mod2002021Key.LQ654 ,"Compensaci\u00F3n de base a\u00F1o 2001")
	,C06(Mod2002021Key.LQ655 ,Mod2002021Key.LQ656 ,Mod2002021Key.LQ657 ,"Compensaci\u00F3n de base a\u00F1o 2002")
	,C07(Mod2002021Key.LQ658 ,Mod2002021Key.LQ659 ,Mod2002021Key.LQ660 ,"Compensaci\u00F3n de base a\u00F1o 2003")
	,C08(Mod2002021Key.LQ661 ,Mod2002021Key.LQ662 ,Mod2002021Key.LQ663 ,"Compensaci\u00F3n de base a\u00F1o 2004")
	,C09(Mod2002021Key.LQ664 ,Mod2002021Key.LQ665 ,Mod2002021Key.LQ666 ,"Compensaci\u00F3n de base a\u00F1o 2005")
	,C10(Mod2002021Key.LQ667 ,Mod2002021Key.LQ668 ,Mod2002021Key.LQ669 ,"Compensaci\u00F3n de base a\u00F1o 2006")
	,C11(Mod2002021Key.LQ743 ,Mod2002021Key.LQ747 ,Mod2002021Key.LQ748 ,"Compensaci\u00F3n de base a\u00F1o 2007")
	,C12(Mod2002021Key.LQ275 ,Mod2002021Key.LQ276 ,Mod2002021Key.LQ277 ,"Compensaci\u00F3n de base a\u00F1o 2008")
	,C13(Mod2002021Key.LQ608 ,Mod2002021Key.LQ609 ,Mod2002021Key.LQ610 ,"Compensaci\u00F3n de base a\u00F1o 2009")
	,C14(Mod2002021Key.LQ704 ,Mod2002021Key.LQ705 ,Mod2002021Key.LQ706 ,"Compensaci\u00F3n de base a\u00F1o 2010")
	,C15(Mod2002021Key.LQ013 ,Mod2002021Key.LQ014 ,Mod2002021Key.LQ015 ,"Compensaci\u00F3n de base a\u00F1o 2011")
	,C16(Mod2002021Key.LQ725 ,Mod2002021Key.LQ726 ,Mod2002021Key.LQ727 ,"Compensaci\u00F3n de base a\u00F1o 2012")
	,C17(Mod2002021Key.LQ534 ,Mod2002021Key.LQ535 ,Mod2002021Key.LQ536 ,"Compensaci\u00F3n de base a\u00F1o 2013")
	,C18(Mod2002021Key.LQ607 ,Mod2002021Key.LQ675 ,Mod2002021Key.LQ699 ,"Compensaci\u00F3n de base a\u00F1o 2014")
	,C19(Mod2002021Key.LQ1045,Mod2002021Key.LQ1046,Mod2002021Key.LQ1047,"Compensaci\u00F3n de base a\u00F1o 2015")
	,C20(Mod2002021Key.LQ1519,Mod2002021Key.LQ1520,Mod2002021Key.LQ1521,"Compensaci\u00F3n de base a\u00F1o 2016")
	,C21(Mod2002021Key.LQ1592,Mod2002021Key.LQ1593,Mod2002021Key.LQ1594,"Compensaci\u00F3n de base a\u00F1o 2017")
	,C22(Mod2002021Key.LQ1825,Mod2002021Key.LQ1826,Mod2002021Key.LQ1827,"Compensaci\u00F3n de base a\u00F1o 2018")
	,C23(Mod2002021Key.LQ2193,Mod2002021Key.LQ2194,Mod2002021Key.LQ2195,"Compensaci\u00F3n de base a\u00F1o 2019")
	,C24(Mod2002021Key.LQ194 ,Mod2002021Key.LQ195 ,Mod2002021Key.LQ196 ,"Compensaci\u00F3n de base a\u00F1o 2020")	
	,C25(Mod2002021Key.LQ2316,Mod2002021Key.LQ2317,Mod2002021Key.LQ2318,"Compensaci\u00F3n de base a\u00F1o 2021(*)")	
	,C26(Mod2002021Key.LQ670 ,Mod2002021Key.LQ547 ,Mod2002021Key.LQ671 ,"Total")
	,C27(Mod2002021Key.LQ1048,null                ,Mod2002021Key.LQ1049,"Compensaci\u00F3n de base a\u00F1o 2021")
	;
	 
    private String description;
    private Mod2002021Key previousPendind;
    private Mod2002021Key current;
    private Mod2002021Key futurePendind;
    private Mod2002021Key[] keys;

	private Mod2002021LQ547Key(Mod2002021Key previousPendind,
			Mod2002021Key current, Mod2002021Key futurePendind, String description) {
		this.previousPendind = previousPendind;
		this.current = current;
		this.futurePendind = futurePendind;
		this.description = description;
		keys = new Mod2002021Key[]{previousPendind,current,futurePendind};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002021Key getPreviousPendind() {
		return previousPendind;
	}
	public Mod2002021Key getCurrent() {
		return current;
	}
	public Mod2002021Key getFuturePendind() {
		return futurePendind;
	}
	
	@Override
	public Mod2002021Key[] getKeys() {
		return keys; 
	}

}

