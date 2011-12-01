package com.code.aon.file.tax.model.MOD347.check;

import java.util.ArrayList;

import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.tax.model.MOD347.data.Deponent;

public class CheckDeponent extends Check {
	private static final String ERROR_DEPONENT_1_MESSAGE = "ERROR_DEPONENT_1";
	private static final String ERROR_DEPONENT_2_MESSAGE = "ERROR_DEPONENT_2";
	private static final String ERROR_DEPONENT_3_MESSAGE = "ERROR_DEPONENT_3";
	private static final String ERROR_DEPONENT_4_MESSAGE = "ERROR_DEPONENT_4";
	private static final String ERROR_DEPONENT_5_MESSAGE = "ERROR_DEPONENT_5";
	private static final String ERROR_DEPONENT_6_MESSAGE = "ERROR_DEPONENT_6";
	private static final String ERROR_DEPONENT_7_MESSAGE = "ERROR_DEPONENT_7";
	private static final String ERROR_DEPONENT_8_MESSAGE = "ERROR_DEPONENT_8";
	
	public static boolean parse(Deponent deponent,ArrayList<Exception> exceptions){
		boolean status = true;
		if (deponent.getYear()==null){
			exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_1_MESSAGE) ,deponent.toString()) );
			status = false;
		}
		if (deponent.getCode()==null){
			exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_2_MESSAGE) ,deponent.toString()) );
			status = false;
		}
		if (deponent.getName()==null){
			exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_3_MESSAGE) ,deponent.toString()) );
			status = false;
		}
		if (deponent.getType()==null){
			exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_4_MESSAGE) ,deponent.toString()) );
			status = false;
		}else{
			if (!deponent.getType().equals("C") && !deponent.getType().equals("T")){
				exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_5_MESSAGE) ,deponent.toString()) );
				status = false;
			}			
		}
		if (deponent.getNumber() ==null){
			exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_6_MESSAGE) ,deponent.toString()) );
			status = false;
		}
		if (deponent.getReplacement()!=null){
			if (deponent.getReplacedNumber()==null){
				exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_7_MESSAGE) ,deponent.toString()) );
				status = false;
			}
		}else{
			if (deponent.getReplacedNumber()!=null){
				exceptions.add( new Fd0Exception( getMessage(ERROR_DEPONENT_8_MESSAGE) ,deponent.toString()) );
				status = false;
			}
		}
		return status;
	}

}
