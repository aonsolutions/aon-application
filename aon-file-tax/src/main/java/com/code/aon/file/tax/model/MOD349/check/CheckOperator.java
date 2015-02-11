package com.code.aon.file.tax.model.MOD349.check;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.Country;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD349.data.Operator;

public class CheckOperator extends Check {

	public static boolean parse(Operator operator,ArrayList<Exception> exceptions){
		boolean status = true;
		if (operator.getCountry()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_1") ,operator.toString()) );
			status = false;
		}
		if (operator.getDocument()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_2") ,operator.toString()) );
			status = false;
		}
		if (!isValidComunitaryCode(operator.getCountry(), operator.getDocument())){
			exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_6") ,operator.toString()) );
			status = false;
		}
		if (operator.getName()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_3") ,operator.toString()) );
			status = false;
		}
		if (operator.getKey()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_4") ,operator.toString()) );
			status = false;
		}else{
			if (!operator.getKey().equals("E") &&
				!operator.getKey().equals("M") &&
				!operator.getKey().equals("H") &&
				!operator.getKey().equals("A") &&
				!operator.getKey().equals("T") &&
				!operator.getKey().equals("S") &&
				!operator.getKey().equals("I")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_5") ,operator.toString()) );
				status = false;
			}			
		}
		return status;
	}

	private static boolean isValidComunitaryCode(String country , String doc){
		int len = doc.length();
		if (
				( "DE".equals(country) && StringUtils.isNumeric(doc) 		&& len==9 ) 
			||  ( "AT".equals(country) && StringUtils.isAlphanumeric(doc) 	&& len==9 ) 
			||  ( "BE".equals(country) && StringUtils.isNumeric(doc) 		&& (len==9 || len==10 )) 
			||  ( "BG".equals(country) && StringUtils.isNumeric(doc) 		&& (len==9 || len==10)) 
			||  ( "CY".equals(country) && StringUtils.isAlphanumeric(doc) 	&& len==9 ) 
			||  ( "DK".equals(country) && StringUtils.isNumeric(doc) 		&& len==8 ) 
			||  ( "SI".equals(country) && StringUtils.isNumeric(doc) 		&& len==8 ) 
			||  ( "EE".equals(country) && StringUtils.isNumeric(doc) 		&& len==9 ) 
			||  ( "FI".equals(country) && StringUtils.isNumeric(doc) 		&& len==8 ) 
			||  ( "FR".equals(country) && StringUtils.isAlphanumeric(doc) 	&& len==11 ) 
			||  ( "EL".equals(country) && StringUtils.isNumeric(doc) 		&& len==9 ) 
			||  ( "GB".equals(country) && StringUtils.isAlphanumeric(doc) 	&& (len==5 || len == 9 || len == 12) ) 
			||  ( "NL".equals(country) && StringUtils.isAlphanumeric(doc) 	&& len==12 ) 
			||  ( "HU".equals(country) && StringUtils.isNumeric(doc) 		&& len==8 ) 
			||  ( "HR".equals(country) && StringUtils.isNumeric(doc) 	 && len==11 ) 
			||  ( "IT".equals(country) && StringUtils.isNumeric(doc) 		&& len==11 ) 
			||  ( "IE".equals(country) && StringUtils.isAlphanumeric(doc) 	&& (len==8 || len==9) ) 
			||  ( "LV".equals(country) && StringUtils.isNumeric(doc) 		&& len==11 ) 
			||  ( "LT".equals(country) && StringUtils.isNumeric(doc) 		&& (len==9 || len == 12) ) 
			||  ( "LU".equals(country) && StringUtils.isNumeric(doc) 		&& len==8 ) 
			||  ( "MT".equals(country) && StringUtils.isNumeric(doc) 		&& len==8 ) 
			||  ( "PL".equals(country) && StringUtils.isNumeric(doc) 		&& len==10 ) 
			||  ( "PT".equals(country) && StringUtils.isNumeric(doc) 		&& len==9 ) 
			||  ( "CZ".equals(country) && StringUtils.isNumeric(doc) 		&& (len==8 || len == 9 || len == 10 )) 
			||  ( "SK".equals(country) && StringUtils.isNumeric(doc) 		&& (len==9 || len == 10) ) 
			||  ( "RO".equals(country) && StringUtils.isNumeric(doc) 		&& (len>=2 && len<=10)) 
			||  ( "SE".equals(country) && StringUtils.isNumeric(doc) 		&& len==12 ) ) return true;
		return false;
	}

}
