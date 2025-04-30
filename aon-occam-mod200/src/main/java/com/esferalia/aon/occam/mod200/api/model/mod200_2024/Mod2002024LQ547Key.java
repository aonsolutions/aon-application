package com.esferalia.aon.occam.mod200.api.model.mod200_2024;

import java.io.Serializable;

import com.esferalia.aon.occam.mod200.api.model.IMod200KeysProvider;

// Detalle de la compensación de bases imponibles negativas
public enum Mod2002024LQ547Key implements Serializable, IMod200KeysProvider  {
	
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF

	 C01(new Mod2002024Key[]{Mod2002024Key.LQ640 ,Mod2002024Key.LQ641 ,Mod2002024Key.LQ548 },"Compensaci\u00F3n de base a\u00F1o 1997")
	,C02(new Mod2002024Key[]{Mod2002024Key.LQ643 ,Mod2002024Key.LQ644 ,Mod2002024Key.LQ645 },"Compensaci\u00F3n de base a\u00F1o 1998")
	,C03(new Mod2002024Key[]{Mod2002024Key.LQ646 ,Mod2002024Key.LQ647 ,Mod2002024Key.LQ648 },"Compensaci\u00F3n de base a\u00F1o 1999")
	,C04(new Mod2002024Key[]{Mod2002024Key.LQ649 ,Mod2002024Key.LQ650 ,Mod2002024Key.LQ651 },"Compensaci\u00F3n de base a\u00F1o 2000")
	,C05(new Mod2002024Key[]{Mod2002024Key.LQ652 ,Mod2002024Key.LQ653 ,Mod2002024Key.LQ654 },"Compensaci\u00F3n de base a\u00F1o 2001")
	,C06(new Mod2002024Key[]{Mod2002024Key.LQ655 ,Mod2002024Key.LQ656 ,Mod2002024Key.LQ657 },"Compensaci\u00F3n de base a\u00F1o 2002")
	,C07(new Mod2002024Key[]{Mod2002024Key.LQ658 ,Mod2002024Key.LQ659 ,Mod2002024Key.LQ660 },"Compensaci\u00F3n de base a\u00F1o 2003")
	,C08(new Mod2002024Key[]{Mod2002024Key.LQ661 ,Mod2002024Key.LQ662 ,Mod2002024Key.LQ663 },"Compensaci\u00F3n de base a\u00F1o 2004")
	,C09(new Mod2002024Key[]{Mod2002024Key.LQ664 ,Mod2002024Key.LQ665 ,Mod2002024Key.LQ666 },"Compensaci\u00F3n de base a\u00F1o 2005")
	,C10(new Mod2002024Key[]{Mod2002024Key.LQ667 ,Mod2002024Key.LQ668 ,Mod2002024Key.LQ669 },"Compensaci\u00F3n de base a\u00F1o 2006")
	,C11(new Mod2002024Key[]{Mod2002024Key.LQ743 ,Mod2002024Key.LQ747 ,Mod2002024Key.LQ748 },"Compensaci\u00F3n de base a\u00F1o 2007")
	,C12(new Mod2002024Key[]{Mod2002024Key.LQ275 ,Mod2002024Key.LQ276 ,Mod2002024Key.LQ277 },"Compensaci\u00F3n de base a\u00F1o 2008")
	,C13(new Mod2002024Key[]{Mod2002024Key.LQ608 ,Mod2002024Key.LQ609 ,Mod2002024Key.LQ610 },"Compensaci\u00F3n de base a\u00F1o 2009")
	,C14(new Mod2002024Key[]{Mod2002024Key.LQ704 ,Mod2002024Key.LQ705 ,Mod2002024Key.LQ706 },"Compensaci\u00F3n de base a\u00F1o 2010")
	,C15(new Mod2002024Key[]{Mod2002024Key.LQ013 ,Mod2002024Key.LQ014 ,Mod2002024Key.LQ015 },"Compensaci\u00F3n de base a\u00F1o 2011")
	,C16(new Mod2002024Key[]{Mod2002024Key.LQ725 ,Mod2002024Key.LQ726 ,Mod2002024Key.LQ727 },"Compensaci\u00F3n de base a\u00F1o 2012")
	,C17(new Mod2002024Key[]{Mod2002024Key.LQ534 ,Mod2002024Key.LQ535 ,Mod2002024Key.LQ536 },"Compensaci\u00F3n de base a\u00F1o 2013")
	,C18(new Mod2002024Key[]{Mod2002024Key.LQ607 ,Mod2002024Key.LQ675 ,Mod2002024Key.LQ699 },"Compensaci\u00F3n de base a\u00F1o 2014")
	,C19(new Mod2002024Key[]{Mod2002024Key.LQ1045,Mod2002024Key.LQ1046,Mod2002024Key.LQ1047},"Compensaci\u00F3n de base a\u00F1o 2015")
	,C20(new Mod2002024Key[]{Mod2002024Key.LQ1519,Mod2002024Key.LQ1520,Mod2002024Key.LQ1521},"Compensaci\u00F3n de base a\u00F1o 2016")
	,C21(new Mod2002024Key[]{Mod2002024Key.LQ1592,Mod2002024Key.LQ1593,Mod2002024Key.LQ1594},"Compensaci\u00F3n de base a\u00F1o 2017")
	,C22(new Mod2002024Key[]{Mod2002024Key.LQ1825,Mod2002024Key.LQ1826,Mod2002024Key.LQ1827},"Compensaci\u00F3n de base a\u00F1o 2018")
	,C23(new Mod2002024Key[]{Mod2002024Key.LQ2193,Mod2002024Key.LQ2194,Mod2002024Key.LQ2195},"Compensaci\u00F3n de base a\u00F1o 2019")
	,C24(new Mod2002024Key[]{Mod2002024Key.LQ194 ,Mod2002024Key.LQ195 ,Mod2002024Key.LQ196 },"Compensaci\u00F3n de base a\u00F1o 2020")
	,C25(new Mod2002024Key[]{Mod2002024Key.LQ151 ,Mod2002024Key.LQ152 ,Mod2002024Key.LQ164 },"Compensaci\u00F3n de base a\u00F1o 2021")
	,C26(new Mod2002024Key[]{Mod2002024Key.LQ896 ,Mod2002024Key.LQ897 ,Mod2002024Key.LQ898 },"Compensaci\u00F3n de base a\u00F1o 2022")
	,C27(new Mod2002024Key[]{Mod2002024Key.LQ009 ,Mod2002024Key.LQ010 ,Mod2002024Key.LQ020 },"Compensaci\u00F3n de base a\u00F1o 2023")
	,C28(new Mod2002024Key[]{Mod2002024Key.LQ2316,Mod2002024Key.LQ2317,Mod2002024Key.LQ2318},"Compensaci\u00F3n de base a\u00F1o 2024(*)")	
	,C29(new Mod2002024Key[]{Mod2002024Key.LQ670 ,Mod2002024Key.LQ547 ,Mod2002024Key.LQ671 },"Total")
	,C30(new Mod2002024Key[]{Mod2002024Key.LQ1048,null                ,Mod2002024Key.LQ1049},"Compensaci\u00F3n de base a\u00F1o 2024")
	
	;
	 
    private String description;
    private Mod2002024Key[] keys;

	private Mod2002024LQ547Key(Mod2002024Key[] keys, String description) {
		this.keys = keys;
		this.description = description;	
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public Mod2002024Key[] getKeys() {
		return keys; 
	}

}

