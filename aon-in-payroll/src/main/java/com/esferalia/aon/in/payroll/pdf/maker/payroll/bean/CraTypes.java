 package com.esferalia.aon.in.payroll.pdf.maker.payroll.bean;

import java.util.Locale;
import java.util.ResourceBundle;

public class CraTypes {	

	public static String getType(int type, Locale language) throws UnknownCraException {
		ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.maker.payroll.bundle.CraTypesBundle",language);
		
		switch (type) {
			case 0:  return words.getString("0");
			case 1:  return words.getString("1");
			case 2:  return words.getString("2");
			case 3:  return words.getString("3");
			case 4:  return words.getString("4");
			case 5:  return words.getString("5");
			case 6:  return words.getString("6");
			case 7:  return words.getString("7");
			case 8:  return words.getString("8");
			case 9:  return words.getString("9");
			case 10: return words.getString("10");
			case 11: return words.getString("11");
			case 12: return words.getString("12");
			case 13: return words.getString("13");
			case 14: return words.getString("14");
			case 15: return words.getString("15");
			case 16: return words.getString("16");
			case 17: return words.getString("17");
			case 18: return words.getString("18");
			case 19: return words.getString("19");
			case 20: return words.getString("20");
			case 21: return words.getString("21");
			case 22: return words.getString("22");
			case 23: return words.getString("23");
			case 24: return words.getString("24");
			case 25: return words.getString("25");
			case 26: return words.getString("26");
			case 27: return words.getString("27");
			case 28: return words.getString("28");
			case 29: return words.getString("29");
			case 30: return words.getString("30");
			case 31: return words.getString("31");
			case 32: return words.getString("32");
			case 33: return words.getString("33");
			case 34: return words.getString("34");
			case 35: return words.getString("35");
			case 36: return words.getString("36");
			case 37: return words.getString("37");
			case 38: return words.getString("38");
			case 39: return words.getString("39");
			case 40: return words.getString("40");
			case 41: return words.getString("41");
			case 42: return words.getString("42");
			case 43: return words.getString("43");
			case 44: return words.getString("44");
			case 45: return words.getString("45");
			case 46: return words.getString("46");
			case 47: return words.getString("47");
			case 48: return words.getString("48");
			case 49: return words.getString("49");
			case 50: return words.getString("50");
			case 51: return words.getString("51");
			case 52: return words.getString("52");
			case 53: return words.getString("53");
			case 54: return words.getString("54");
			case 55: return words.getString("55");
			case 56: return words.getString("56");
			case 57: return words.getString("57");
			case 58: return words.getString("58");
			case 59: return words.getString("59");
			case 60: return words.getString("60");
			case 61: return words.getString("61");
			default: throw new UnknownCraException(type + " is not a valid CRA type.");
		}
	}
}
