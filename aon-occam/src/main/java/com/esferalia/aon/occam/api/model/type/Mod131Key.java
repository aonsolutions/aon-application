package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod131Key implements IFiscalModelKey {

	 P2  		("131-P2"		, 0)
	
	,C01		("131-AC01"		, 1)
	,C02		("131-AC02"		, 2)
	
	,C03		("131-03"		, 3)
	,C04		("131-04"		, 4)
	
	,C05		("131-05"		, 5)
	,C06		("131-06"		, 6)
	
	,C07		("131-07"		, 7)
	,C08		("131-08"		, 8)
	,C09		("131-09"		, 9)
	,C091		("131-091"		, 9)
	,C10		("131-10"		,10)
	,C11		("131-11"		,11)
	,C12		("131-12"		,12)
	,C13		("131-13"		,13)
	,C14		("131-14"		,14)
	,C15		("131-15"		,15)
	,CT_TIP		("131-DT"		,0)
	;

    private String value;
    private int box;
    
    private Mod131Key(String value,int box) {
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
    @Override
	public String getBoxFormatted() {
		return " [" + getBoxAsString() +"] ";
	}
	public String getBoxAsString() {
		return AonStringUtils.leftPad(Integer.toString(getBox()), 3, '0');
	}
	public static Mod131Key getKey(String value) {
		for (Mod131Key key : Mod131Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
	
	
	public static void main(String[] args) {
		for (Mod131Key key : Mod131Key.values()) {
			System.out.println(
				"," + key.toString() 
				+ "\t(Mod131Key."+key.toString()
				+ "\t, (mod -> mod.isAEAT())"
				+ "\t, null"
				+ "\t, null"
				+ ")"
					);
		}
	}
}
