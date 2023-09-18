package com.esferalia.aon.occam.mod200.api.model.mod200_2022;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Detalle de la compensación de bases imponibles negativas
public enum Mod2002022LQ547Key implements Serializable, IMod200KeysProvider  {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002022Key[]{Mod2002022Key.LQ640 ,Mod2002022Key.LQ641 ,Mod2002022Key.LQ548 },"Compensaci\u00F3n de base a\u00F1o 1997")
	,C02(new Mod2002022Key[]{Mod2002022Key.LQ643 ,Mod2002022Key.LQ644 ,Mod2002022Key.LQ645 },"Compensaci\u00F3n de base a\u00F1o 1998")
	,C03(new Mod2002022Key[]{Mod2002022Key.LQ646 ,Mod2002022Key.LQ647 ,Mod2002022Key.LQ648 },"Compensaci\u00F3n de base a\u00F1o 1999")
	,C04(new Mod2002022Key[]{Mod2002022Key.LQ649 ,Mod2002022Key.LQ650 ,Mod2002022Key.LQ651 },"Compensaci\u00F3n de base a\u00F1o 2000")
	,C05(new Mod2002022Key[]{Mod2002022Key.LQ652 ,Mod2002022Key.LQ653 ,Mod2002022Key.LQ654 },"Compensaci\u00F3n de base a\u00F1o 2001")
	,C06(new Mod2002022Key[]{Mod2002022Key.LQ655 ,Mod2002022Key.LQ656 ,Mod2002022Key.LQ657 },"Compensaci\u00F3n de base a\u00F1o 2002")
	,C07(new Mod2002022Key[]{Mod2002022Key.LQ658 ,Mod2002022Key.LQ659 ,Mod2002022Key.LQ660 },"Compensaci\u00F3n de base a\u00F1o 2003")
	,C08(new Mod2002022Key[]{Mod2002022Key.LQ661 ,Mod2002022Key.LQ662 ,Mod2002022Key.LQ663 },"Compensaci\u00F3n de base a\u00F1o 2004")
	,C09(new Mod2002022Key[]{Mod2002022Key.LQ664 ,Mod2002022Key.LQ665 ,Mod2002022Key.LQ666 },"Compensaci\u00F3n de base a\u00F1o 2005")
	,C10(new Mod2002022Key[]{Mod2002022Key.LQ667 ,Mod2002022Key.LQ668 ,Mod2002022Key.LQ669 },"Compensaci\u00F3n de base a\u00F1o 2006")
	,C11(new Mod2002022Key[]{Mod2002022Key.LQ743 ,Mod2002022Key.LQ747 ,Mod2002022Key.LQ748 },"Compensaci\u00F3n de base a\u00F1o 2007")
	,C12(new Mod2002022Key[]{Mod2002022Key.LQ275 ,Mod2002022Key.LQ276 ,Mod2002022Key.LQ277 },"Compensaci\u00F3n de base a\u00F1o 2008")
	,C13(new Mod2002022Key[]{Mod2002022Key.LQ608 ,Mod2002022Key.LQ609 ,Mod2002022Key.LQ610 },"Compensaci\u00F3n de base a\u00F1o 2009")
	,C14(new Mod2002022Key[]{Mod2002022Key.LQ704 ,Mod2002022Key.LQ705 ,Mod2002022Key.LQ706 },"Compensaci\u00F3n de base a\u00F1o 2010")
	,C15(new Mod2002022Key[]{Mod2002022Key.LQ013 ,Mod2002022Key.LQ014 ,Mod2002022Key.LQ015 },"Compensaci\u00F3n de base a\u00F1o 2011")
	,C16(new Mod2002022Key[]{Mod2002022Key.LQ725 ,Mod2002022Key.LQ726 ,Mod2002022Key.LQ727 },"Compensaci\u00F3n de base a\u00F1o 2012")
	,C17(new Mod2002022Key[]{Mod2002022Key.LQ534 ,Mod2002022Key.LQ535 ,Mod2002022Key.LQ536 },"Compensaci\u00F3n de base a\u00F1o 2013")
	,C18(new Mod2002022Key[]{Mod2002022Key.LQ607 ,Mod2002022Key.LQ675 ,Mod2002022Key.LQ699 },"Compensaci\u00F3n de base a\u00F1o 2014")
	,C19(new Mod2002022Key[]{Mod2002022Key.LQ1045,Mod2002022Key.LQ1046,Mod2002022Key.LQ1047},"Compensaci\u00F3n de base a\u00F1o 2015")
	,C20(new Mod2002022Key[]{Mod2002022Key.LQ1519,Mod2002022Key.LQ1520,Mod2002022Key.LQ1521},"Compensaci\u00F3n de base a\u00F1o 2016")
	,C21(new Mod2002022Key[]{Mod2002022Key.LQ1592,Mod2002022Key.LQ1593,Mod2002022Key.LQ1594},"Compensaci\u00F3n de base a\u00F1o 2017")
	,C22(new Mod2002022Key[]{Mod2002022Key.LQ1825,Mod2002022Key.LQ1826,Mod2002022Key.LQ1827},"Compensaci\u00F3n de base a\u00F1o 2018")
	,C23(new Mod2002022Key[]{Mod2002022Key.LQ2193,Mod2002022Key.LQ2194,Mod2002022Key.LQ2195},"Compensaci\u00F3n de base a\u00F1o 2019")
	,C24(new Mod2002022Key[]{Mod2002022Key.LQ194 ,Mod2002022Key.LQ195 ,Mod2002022Key.LQ196 },"Compensaci\u00F3n de base a\u00F1o 2020")
	,C25(new Mod2002022Key[]{Mod2002022Key.LQ151 ,Mod2002022Key.LQ152 ,Mod2002022Key.LQ164 },"Compensaci\u00F3n de base a\u00F1o 2021")	
	,C26(new Mod2002022Key[]{Mod2002022Key.LQ2316,Mod2002022Key.LQ2317,Mod2002022Key.LQ2318},"Compensaci\u00F3n de base a\u00F1o 2022(*)")	
	,C27(new Mod2002022Key[]{Mod2002022Key.LQ670 ,Mod2002022Key.LQ547 ,Mod2002022Key.LQ671 },"Total")
	,C28(new Mod2002022Key[]{Mod2002022Key.LQ1048,null                ,Mod2002022Key.LQ1049},"Compensaci\u00F3n de base a\u00F1o 2022")
	
	;
	 
    private String description;
    private Mod2002022Key[] keys;

	private Mod2002022LQ547Key(Mod2002022Key[] keys, String description) {
		this.keys = keys;
		this.description = description;	
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public Mod2002022Key[] getKeys() {
		return keys; 
	}

}

