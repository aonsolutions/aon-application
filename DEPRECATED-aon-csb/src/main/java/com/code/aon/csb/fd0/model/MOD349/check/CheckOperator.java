package com.code.aon.csb.fd0.model.MOD349.check;

import java.util.ArrayList;

import com.code.aon.csb.fd0.model.Fd0Exception;
import com.code.aon.csb.fd0.model.MOD349.data.Operator;

/**
 * Checks Operator object data
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 *
 */
public class CheckOperator extends Check {

	/**
	 * Parses data
	 * 
	 * @param operator the object to parse
	 * @param exceptions errors founds
	 * @return true if no errors
	 */
	public static boolean parse(Operator operator,ArrayList<Exception> exceptions){
		boolean status = true;
		if (operator.getCountry()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_1") ,operator.toString()) );
			status = false;
		}
		if (operator.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_2") ,operator.toString()) );
			status = false;
		}
		if (!isValidComunitaryCode(operator.getCountry(), operator.getCode())){
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
			if (!operator.getKey().equals("A") &&
					!operator.getKey().equals("E") &&
					!operator.getKey().equals("T")){
				exceptions.add( new Fd0Exception( getMessage("ERROR_OPERATOR_5") ,operator.toString()) );
				status = false;
			}			
		}
		return status;
	}

	private static boolean isValidComunitaryCode(String country , String code){
		int longitud = code.length();
		try{
			if (country.equals("DE") && longitud == 9 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("AT") && longitud == 9 ){
				return true;
			}else if( country.equals("BE") && longitud == 9 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("CY") && longitud == 9 ){
				return true;
			}else if( country.equals("DK") && longitud == 8 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("SI") && longitud == 8 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("EE") && longitud == 9 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("FI") && longitud == 8 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("FR") && longitud == 11 ){
				return true;
			}else if( country.equals("EL") && longitud == 9 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("GB") && (longitud == 5 || longitud == 9 || longitud == 12) ){
				return true;
			}else if( country.equals("NL") && longitud == 12 ){
				return true;
			}else if( country.equals("HU") && longitud == 8 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("IT") && longitud == 11 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("IE") && longitud == 8 ){
				return true;
			}else if( country.equals("LV") && longitud == 11 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("LT") && (longitud == 9 || longitud == 12) ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("LU") && longitud == 8 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("MT") && longitud == 8 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("PL") && longitud == 10 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("PT") && longitud == 9 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("CZ") && (longitud == 8 || longitud == 9 || longitud == 10 )){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("SK") && (longitud == 9 || longitud == 10) ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("SE") && longitud == 12 ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("BG") && (longitud == 9 || longitud == 10) ){
				Double.parseDouble( code );
				return true;
			}else if( country.equals("RO") && longitud == 10 ){
				Double.parseDouble( code );
				return true;
			}
			return false;
		}catch (Exception nfe){
			return false;
		}
	}

}
