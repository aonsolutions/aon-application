package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod130Key implements IFiscalModelKey {

	 P0  	("130-P0"  , 0)
	,P1  	("130-P1"  , 0)
	,P2  	("130-P2"  , 0)
	,C01 	("130-01"  , 1)
	,C02 	("130-02"  , 2)
	,C03 	("130-03"  , 3)
	,C04 	("130-04"  , 4)
	,C05 	("130-05"  , 5)
	,C06 	("130-06"  , 6)
	,C07 	("130-07"  , 7)
	,C08 	("130-08"  , 8)
	,C09 	("130-09"  , 9)
	,C10 	("130-10"  ,10)
	,C11 	("130-11"  ,11)
	,C12 	("130-12"  ,12)
	,C13 	("130-13"  ,13)
	,C131	("130-131" ,13)
	,C14 	("130-14"  ,14)
	,C15 	("130-15"  ,15)
	,C16 	("130-16"  ,16)
	,C17 	("130-17"  ,17)
	,C18 	("130-18"  ,18)
	,C19 	("130-19"  ,19)
	,CT_TIP	("130-DT"  ,0)
	;

    private String value;
    private int box;
    
    private Mod130Key(String value,int box) {
    	this.value = value;
    	this.box = box;
    }

	@Override
	public String getValue() {
		return value;
	}
    @Override
	public int getBox() {
		return box;
	}
	public static Mod130Key getKey(String value) {
		for (Mod130Key key : Mod130Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
}