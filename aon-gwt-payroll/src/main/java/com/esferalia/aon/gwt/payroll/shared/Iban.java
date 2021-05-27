package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class Iban implements Serializable{
	
	private static final String DEFSTRS = ""
            + "AL28 AD24 AT20 AZ28 BE16 BH22 BA20 BR29 BG22 "
            + "HR21 CY28 CZ24 DK18 DO28 EE20 FO18 FI18 FR27 GE22 DE22 GI23 "
            + "GL18 GT28 HU28 IS26 IE22 IL23 IT27 KZ20 KW30 LV21 LB28 LI21 "
            + "LT20 LU20 MK19 MT31 MR27 MU30 MC27 MD24 ME22 NL18 NO15 PK24 "
            + "PS29 PL28 PT25 RO24 SM27 SA24 RS22 SK24 SI19 ES24 SE24 CH21 "
            + "TN24 TR26 AE23 GB22 VG24 GR27 CR21";
    
	private static final Map<String, Integer> DEFINITIONS = new HashMap<>();
 
    static {
        for (String definition : DEFSTRS.split(" "))
            DEFINITIONS.put(definition.substring(0, 2), Integer.parseInt(definition.substring(2)));
    }
 
    public static boolean validateIBAN(String iban) {
        iban = iban.replaceAll("\\s", "").toUpperCase();
 
        int len = iban.length();
        if (len < 4 || !iban.matches("[0-9A-Z]+") || DEFINITIONS.getOrDefault(iban.substring(0, 2), 0) != len)
            return false;
 
        iban = iban.substring(4) + iban.substring(0, 4);
 
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++)
            sb.append(Character.digit(iban.charAt(i), 36));
 
        BigInteger bigInt = new BigInteger(sb.toString());
 
        return bigInt.mod(BigInteger.valueOf(97)).intValue() == 1;
    }
    
    
    // ----------------------------------------------------------------
    //							MAIN
    // ----------------------------------------------------------------
    
//  public static void main(String[] args) {
//  String[] ibans = {
//          "GB82 WEST 1234 5698 7654 32",
//          "GB82 TEST 1234 5698 7654 32",
//          "GB81 WEST 1234 5698 7654 32",
//          "SA03 8000 0000 6080 1016 7519",
//          "CH93 0076 2011 6238 5295 7",
//          "XX00 0000",
//          "",
//          "DE",
//          "DE13 הצü_ 1234 1234 1234 12"};
//  for (String iban : ibans)
//      System.out.printf("%s is %s.%n", iban, validateIBAN(iban) ? "valid" : "not valid");
//}

}
