package com.esferalia.aon.occam.api.model.fiscal.mod200_2018;

import java.io.Serializable;

// Detalle de la compensación de bases imponibles negativas
public enum Mod2002018LQ547Key implements Serializable, IMod200KeysProvider  {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(Mod2002018Key.LQ640 ,Mod2002018Key.LQ641 ,Mod2002018Key.LQ548 ,"Compensaci\u00F3n de base a\u00F1o 1997")
	,C02(Mod2002018Key.LQ643 ,Mod2002018Key.LQ644 ,Mod2002018Key.LQ645 ,"Compensaci\u00F3n de base a\u00F1o 1998")
	,C03(Mod2002018Key.LQ646 ,Mod2002018Key.LQ647 ,Mod2002018Key.LQ648 ,"Compensaci\u00F3n de base a\u00F1o 1999")
	,C04(Mod2002018Key.LQ649 ,Mod2002018Key.LQ650 ,Mod2002018Key.LQ651 ,"Compensaci\u00F3n de base a\u00F1o 2000")
	,C05(Mod2002018Key.LQ652 ,Mod2002018Key.LQ653 ,Mod2002018Key.LQ654 ,"Compensaci\u00F3n de base a\u00F1o 2001")
	,C06(Mod2002018Key.LQ655 ,Mod2002018Key.LQ656 ,Mod2002018Key.LQ657 ,"Compensaci\u00F3n de base a\u00F1o 2002")
	,C07(Mod2002018Key.LQ658 ,Mod2002018Key.LQ659 ,Mod2002018Key.LQ660 ,"Compensaci\u00F3n de base a\u00F1o 2003")
	,C08(Mod2002018Key.LQ661 ,Mod2002018Key.LQ662 ,Mod2002018Key.LQ663 ,"Compensaci\u00F3n de base a\u00F1o 2004")
	,C09(Mod2002018Key.LQ664 ,Mod2002018Key.LQ665 ,Mod2002018Key.LQ666 ,"Compensaci\u00F3n de base a\u00F1o 2005")
	,C10(Mod2002018Key.LQ667 ,Mod2002018Key.LQ668 ,Mod2002018Key.LQ669 ,"Compensaci\u00F3n de base a\u00F1o 2006")
	,C11(Mod2002018Key.LQ743 ,Mod2002018Key.LQ747 ,Mod2002018Key.LQ748 ,"Compensaci\u00F3n de base a\u00F1o 2007")
	,C12(Mod2002018Key.LQ275 ,Mod2002018Key.LQ276 ,Mod2002018Key.LQ277 ,"Compensaci\u00F3n de base a\u00F1o 2008")
	,C13(Mod2002018Key.LQ608 ,Mod2002018Key.LQ609 ,Mod2002018Key.LQ610 ,"Compensaci\u00F3n de base a\u00F1o 2009")
	,C14(Mod2002018Key.LQ704 ,Mod2002018Key.LQ705 ,Mod2002018Key.LQ706 ,"Compensaci\u00F3n de base a\u00F1o 2010")
	,C15(Mod2002018Key.LQ013 ,Mod2002018Key.LQ014 ,Mod2002018Key.LQ015 ,"Compensaci\u00F3n de base a\u00F1o 2011")
	,C16(Mod2002018Key.LQ725 ,Mod2002018Key.LQ726 ,Mod2002018Key.LQ727 ,"Compensaci\u00F3n de base a\u00F1o 2012")
	,C17(Mod2002018Key.LQ534 ,Mod2002018Key.LQ535 ,Mod2002018Key.LQ536 ,"Compensaci\u00F3n de base a\u00F1o 2013")
	,C18(Mod2002018Key.LQ607 ,Mod2002018Key.LQ675 ,Mod2002018Key.LQ699 ,"Compensaci\u00F3n de base a\u00F1o 2014")
	,C19(Mod2002018Key.LQ1045,Mod2002018Key.LQ1046,Mod2002018Key.LQ1047,"Compensaci\u00F3n de base a\u00F1o 2015")
	,C20(Mod2002018Key.LQ1519,Mod2002018Key.LQ1520,Mod2002018Key.LQ1521,"Compensaci\u00F3n de base a\u00F1o 2016")
	,C21(Mod2002018Key.LQ1592,Mod2002018Key.LQ1593,Mod2002018Key.LQ1594,"Compensaci\u00F3n de base a\u00F1o 2017")
	,C22(Mod2002018Key.LQ1825,Mod2002018Key.LQ1826,Mod2002018Key.LQ1827,"Compensaci\u00F3n de base a\u00F1o 2018(*)")
	,C23(Mod2002018Key.LQ670 ,Mod2002018Key.LQ547 ,Mod2002018Key.LQ671 ,"Total")
	,C24(Mod2002018Key.LQ1048,null                ,Mod2002018Key.LQ1049,"Compensaci\u00F3n de base a\u00F1o 2018")
	;
	 
    private String description;
    private Mod2002018Key previousPendind;
    private Mod2002018Key current;
    private Mod2002018Key futurePendind;
    private Mod2002018Key[] keys;

	private Mod2002018LQ547Key(Mod2002018Key previousPendind,
			Mod2002018Key current, Mod2002018Key futurePendind, String description) {
		this.previousPendind = previousPendind;
		this.current = current;
		this.futurePendind = futurePendind;
		this.description = description;
		keys = new Mod2002018Key[]{previousPendind,current,futurePendind};
	}
	
	public String getDescription() {
		return description;
	}

	public Mod2002018Key getPreviousPendind() {
		return previousPendind;
	}
	public Mod2002018Key getCurrent() {
		return current;
	}
	public Mod2002018Key getFuturePendind() {
		return futurePendind;
	}
	
	@Override
	public Mod2002018Key[] getKeys() {
		return keys; 
	}
}

